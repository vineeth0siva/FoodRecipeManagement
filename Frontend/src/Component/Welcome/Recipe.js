import axios from 'axios';
import React, { useEffect, useState } from 'react';
import { FaHeart } from 'react-icons/fa';
import { useSelector } from 'react-redux';
import { useParams } from 'react-router-dom';
import UserNavbar from './UserNavbar';

const Recipe = () => {
    const { id } = useParams();
    const [recipe, setRecipe] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [likesByMe, setLikesByMe] = useState({});
    const [l, setL] = useState(0); 
    const token = useSelector((state) => state.auth.token);

    useEffect(() => {
        const fetchRecipe = async () => {
            try {
                const response = await axios.get(`http://localhost:8080/recipeenter/recipe/${id}`, {
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`,
                    },
                });

                if (response.status === 200) {
                    const data = response.data;

                    const parsedIngredients = data.ingredients.split(',').map((item) => ({
                        ingredient: item.trim(),
                    }));
                    const parsedInstructions = data.instructions.split('\r\n').map((step) => step.trim());

                    setRecipe({
                        ...data,
                        ingredients: parsedIngredients,
                        instructions: parsedInstructions,
                    });

                    setLikesByMe((prevState) => ({
                        ...prevState,
                        [data.recipeId]: data.isLiked,
                    }));

                    // Set the 'likes' count based on the response
                    setL(data.likes);
                } else {
                    setError('Failed to fetch recipe data');
                }
            } catch (error) {
                console.error('Error fetching recipe:', error);
                if (error.response) {
                    setError(`Failed to fetch recipe: ${error.response.statusText}`);
                } else if (error.request) {
                    setError('No response from server.');
                } else {
                    setError('An error occurred.');
                }
            } finally {
                setLoading(false);
            }
        };

        fetchRecipe();
    }, [id, token]);

    const handleLikeClick = async (recipeId) => {
        if (!recipe) return;

        // Determine the current like status
        const currentLikeStatus = likesByMe[recipeId] || false; // Default to false if undefined
        const updatedLikes = recipe.likes + (currentLikeStatus ? -1 : 1); // Update likes optimistically

        // Update UI optimistically
        setLikesByMe((prevState) => ({
            ...prevState,
            [recipeId]: !currentLikeStatus,
        }));

        setRecipe((prevRecipe) => ({
            ...prevRecipe,
            likes: updatedLikes,
        }));

        try {
            const response = await axios.put(
                `http://localhost:8080/recipeenter/${recipeId}/happy`,
                {},
                {
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`,
                    },
                }
            );
            setL(response.data); // Update 'likes' count with the response
            if (response.status === 200) {
                // Update likes with the server's response
                setRecipe((prevRecipe) => ({
                    ...prevRecipe,
                    likes: response.data.likes,
                }));
            }
        } catch (error) {
            console.error('Error during like API call:', error);

            // Revert UI update if API call fails
            setLikesByMe((prevState) => ({
                ...prevState,
                [recipeId]: currentLikeStatus,
            }));

            setRecipe((prevRecipe) => ({
                ...prevRecipe,
                likes: updatedLikes - (currentLikeStatus ? -1 : 1), // Revert to original likes count
            }));
        }
    };

    if (loading) {
        return (
            <div className="d-flex justify-content-center align-items-center vh-100">
                <div className="spinner-border text-success" role="status">
                    <span className="sr-only">Loading...</span>
                </div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="text-center text-danger">
                <h2>Error fetching recipe data.</h2>
                <p>{error}</p>
            </div>
        );
    }

    if (!recipe) {
        return (
            <div className="text-center">
                <h2>Recipe not found</h2>
                <p>Please check the URL or try again later.</p>
            </div>
        );
    }

    return (
        <div>
            <UserNavbar />
            <div
                style={{
                    backgroundImage: 'url(https://img.freepik.com/free-photo/flat-lay-composition-pasta-with-copyspace_23-2148189950.jpg)',
                    backgroundSize: 'cover',
                    backgroundPosition: 'center',
                    backgroundAttachment: 'fixed',
                    minHeight: '100vh',
                    padding: '20px',
                }}
            >
                <div className="container">
                    <div className="recipe-page card shadow-lg">
                        <div className="card-body">
                            <h2 className="card-title text-center text-success mb-4">{recipe.recipeTitle}</h2>
                            <p>
                                <strong>Recipe by: </strong>{recipe.userName || 'Unknown'}
                            </p>

                            <div className="d-flex justify-content-between align-items-center">
                                <div className="d-flex align-items-center">
                                    <FaHeart
                                        onClick={() => handleLikeClick(recipe.recipeId)}
                                        className={`heart-icon ${likesByMe[recipe.recipeId] ? 'text-danger' : 'text-secondary'}`}
                                        size={32}
                                        style={{
                                            cursor: 'pointer',
                                            transition: 'transform 0.2s',
                                        }}
                                        onMouseEnter={(e) => (e.currentTarget.style.transform = 'scale(1.2)')}
                                        onMouseLeave={(e) => (e.currentTarget.style.transform = 'scale(1)')}
                                    />
                                    <span className="ms-2">{l}</span>
                                </div>
                            </div>

                            {/* Ingredients and Instructions */}
                            <div className="row mt-4">
                                <div className="col-md-6 text-center">
                                    <img
                                        src={recipe.imageUrl}
                                        alt={recipe.name}
                                        className="img-fluid"
                                        style={{ maxWidth: '100%', borderRadius: '40px', width: '400px' }}
                                    />
                                </div>

                                <div className="col-md-6">
                                    <h4 className="text-success">Ingredients</h4>
                                    <table className="table table-striped">
                                        <thead>
                                            <tr>
                                                <th>#</th>
                                                <th>Ingredient</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {recipe.ingredients.map((ingredient, index) => (
                                                <tr key={index}>
                                                    <td>{index + 1}</td>
                                                    <td>{ingredient.ingredient}</td>
                                                </tr>
                                            ))}
                                        </tbody>
                                    </table>
                                </div>
                            </div>

                            <h4 className="mt-4 text-success">Instructions</h4>
                            <table className="table table-striped">
                                <thead>
                                    <tr>
                                        <th>#</th>
                                        <th>Step</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {recipe.instructions.map((instruction, index) => (
                                        <tr key={index}>
                                            <td>{index + 1}</td>
                                            <td>{instruction}</td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>

                            {/* Recipe Details */}
                            <div className="row mt-4">
                                <div className="col-sm-6">
                                    <h5 className="text-muted text-center">LEVEL</h5>
                                    <span
                                        className={`badge ${
                                            recipe.difficultyLevel === 'EASY'
                                                ? 'bg-success'
                                                : recipe.difficultyLevel === 'MEDIUM'
                                                ? 'bg-warning'
                                                : 'bg-danger'
                                        }`}
                                    >
                                        {recipe.difficultyLevel}
                                    </span>
                                </div>
                                <div className="col-sm-6">
                                    <h5>Cooking Time:</h5>
                                    <p className="text-primary">{recipe.cookingTime}</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Recipe;
