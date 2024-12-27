import axios from 'axios';
import { PlusIcon } from 'lucide-react';
import React, { useState } from 'react';
import { useSelector } from 'react-redux';
import { Link, useNavigate } from 'react-router-dom';

const AddRecipe = () => {
    const [recipeName, setRecipeName] = useState(''); // State for recipe name
    const [ingredients, setIngredients] = useState([{ amount: '', ingredient: '' }]); // Ingredients state
    const [instructions, setInstructions] = useState(['']); // Instructions state
    const [image, setImage] = useState(null); // Image state
    const [cookingTime, setCookingTime] = useState('00:00:00'); // Default to 00:00:00
    const [difficultyLevel, setDifficultyLevel] = useState(''); // Difficulty level state
    const [successMessage, setSuccessMessage] = useState(''); // Success message state
    const navigate=useNavigate();
    const token = useSelector(state => state.auth.token);

    // Handle changes for ingredients
    const handleIngredientChange = (index, field, value) => {
        const updatedIngredients = [...ingredients];
        updatedIngredients[index][field] = value;
        setIngredients(updatedIngredients);
    };

    // Add a new ingredient to the list
    const handleAddIngredient = () => {
        setIngredients([...ingredients, { amount: '', ingredient: '' }]);
    };

    // Handle changes for instructions
    const handleInstructionChange = (index, value) => {
        const updatedInstructions = [...instructions];
        updatedInstructions[index] = value;
        setInstructions(updatedInstructions);
    };

    // Add a new instruction step
    const handleAddInstruction = () => {
        setInstructions([...instructions, '']);
    };

    // Handle image change
    const handleImageChange = (e) => {
        setImage(e.target.files[0]);
    };

    // Handle form submission
    const handleSubmit = async (e) => {
        e.preventDefault();
    
        const formData = new FormData();
        formData.append('title', recipeName); 
        formData.append('ingredients', ingredients.map(i => `${i.amount} ${i.ingredient}`).join(", "));
        formData.append('instructions', instructions.join("\n"));
        formData.append('cookingTime', cookingTime);
        formData.append('difficultyLevel', difficultyLevel);
    
        if (image) {
            formData.append('image', image);
        }
    
        try {
            // Log the headers being sent
            console.log({
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'multipart/form-data',
            });
    
            const response = await axios.post('http://localhost:8080/recipeenter/addrecipe', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                    'Authorization': `Bearer ${token}`,  
                },
            });
    
            if (response.status === 200) {
                setSuccessMessage('Recipe uploaded successfully!');
                setRecipeName('');
                setIngredients([{ amount: '', ingredient: '' }]);
                setInstructions(['']);
                setCookingTime('00:00:00');
                setDifficultyLevel('');
                setImage(null);
                setTimeout(() => {
                    navigate('/welcome')
                }, 1000);
            } else {
                console.error('Failed to upload recipe, response status:', response.status);
                alert('Failed to upload recipe');
            }
        } catch (error) {
            if (error.response) {
                console.error('Response error:', error.response);
                alert(`Error: ${error.response.status} - ${error.response.data}`);
            } else if (error.request) {
                console.error('Request error:', error.request);
                alert('Network error: No response from the server');
            } else {
                console.error('Error message:', error.message);
                alert(`Error: ${error.message}`);
            }
        }
    };
    
    return (
        <div className="container mt-5">
            
            <div className="d-flex justify-content-end mb-3">
                <Link to="/welcome" className="btn btn-danger rounded-pill w-25">
                    Clear or Back
                </Link>
            </div>

            {/* Display Success Message */}
            {successMessage && (
                <div className="alert alert-success text-center">
                    {successMessage}
                </div>
            )}

            <form onSubmit={handleSubmit} className="shadow-lg p-5 rounded bg-white border border-light">
                <div className="row mb-4">
                    {/* Right Form */}
                    <div className="col-md-8">
                        <div className="mb-3">
                            <label htmlFor="recipe-name" className="form-label text-dark font-weight-bold">Recipe Name:</label>
                            <input
                                id="recipe-name"
                                type="text"
                                placeholder="Enter the Recipe name"
                                value={recipeName}
                                onChange={(e) => setRecipeName(e.target.value)}
                                required
                                className="form-control rounded-pill shadow-sm border-0"
                            />
                        </div>

                        {/* Ingredients Section */}
                        <div>
                            <label className="form-label text-dark font-weight-bold">Ingredients</label>
                            {ingredients.map((ingredient, index) => (
                                <div key={index} className="d-flex align-items-center mb-3">
                                    <input
                                        type="number"
                                        placeholder="Amount"
                                        value={ingredient.amount}
                                        onChange={(e) => handleIngredientChange(index, 'amount', e.target.value)}
                                        required
                                        className="form-control rounded-pill shadow-sm border-0 me-2"
                                    />
                                    <input
                                        type="text"
                                        placeholder="Ingredient"
                                        value={ingredient.ingredient}
                                        onChange={(e) => handleIngredientChange(index, 'ingredient', e.target.value)}
                                        required
                                        className="form-control rounded-pill shadow-sm border-0"
                                    />
                                </div>
                            ))}
                            <PlusIcon
                                onClick={handleAddIngredient}
                                className="cursor-pointer text-success mb-3"
                                style={{ width: 30, height: 30 }}
                            />
                        </div>

                        {/* Instructions Section */}
                        <div className="mt-4">
                            <label className="form-label text-dark font-weight-bold">Instructions</label>
                            {instructions.map((instruction, index) => (
                                <div key={index} className="d-flex align-items-center mb-3">
                                    <input
                                        type="text"
                                        placeholder={`Step ${index + 1}`}
                                        value={instruction}
                                        onChange={(e) => handleInstructionChange(index, e.target.value)}
                                        required
                                        className="form-control rounded-pill shadow-sm border-0"
                                    />
                                </div>
                            ))}
                            <PlusIcon
                                onClick={handleAddInstruction}
                                className="cursor-pointer text-success mb-3"
                                style={{ width: 30, height: 30 }}
                            />
                        </div>

                        {/* Image Upload Section */}
                        <div className="mt-4">
                            <label htmlFor="recipe-image" className="form-label text-dark font-weight-bold">Upload Recipe Image:</label>
                            <input
                                id="recipe-image"
                                type="file"
                                name="image"
                                accept="image/*"
                                onChange={handleImageChange}
                                className="form-control border-0"
                            />
                        </div>

                        {/* Cooking Time Section */}
                        <div className="mt-4">
                            <label htmlFor="cooking-time" className="form-label text-dark font-weight-bold">Cooking Time (hh:mm:ss):</label>
                            <input
                                id="cooking-time"
                                type="text"
                                placeholder='00:00:00'
                                value={cookingTime}
                                onChange={(e) => setCookingTime(e.target.value)}
                                required
                                className="form-control rounded-pill shadow-sm border-0"
                            />
                        </div>

                        {/* Difficulty Level Section */}
                        <div className="mt-4">
                            <label htmlFor="difficulty-level" className="form-label text-dark font-weight-bold">Difficulty Level:</label>
                            <select
                                id="difficulty-level"
                                value={difficultyLevel}
                                onChange={(e) => setDifficultyLevel(e.target.value)}
                                className="form-control rounded-pill shadow-sm border-0"
                                required
                            >
                                <option value="">Select Difficulty</option>
                                <option value="EASY">EASY</option>
                                <option value="MEDIUM">MEDIUM</option>
                                <option value="HARD">HARD</option>
                            </select>

                        </div>

                        {/* Submit Button */}
                        <div className="mt-4 mb-5">
                            <button type="submit" className="btn btn-primary rounded-pill w-100 py-3 shadow-lg border-0">
                                Submit Recipe
                            </button>
                        </div>
                    </div>

                    {/* Left Image */}
                    <div className="col-md-4">
                        <div className="text-center">
                            <img
                                src="https://st2.depositphotos.com/1005155/8947/i/450/depositphotos_89477160-stock-photo-blank-recipe-book.jpg"
                                alt="Recipe"
                                className="img-fluid rounded shadow-lg"
                                style={{ maxWidth: '100%', height: '70vh' }}
                            />
                        </div>
                    </div>
                </div>
            </form>
        </div>
    );
};

export default AddRecipe;
