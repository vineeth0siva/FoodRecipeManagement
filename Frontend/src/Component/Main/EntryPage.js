import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import Navbar from './Navbar';

const EntryPage = () => {
    const [data, setData] = useState([]); // Store fetched recipes
    const [loading, setLoading] = useState(true); // Loading state
    const [currentPage, setCurrentPage] = useState(1); // Current page state
    const [itemsPerPage] = useState(6); // Number of items per page
    const [totalPages, setTotalPages] = useState(1); // Total pages for pagination

    // Fetching the data from the API
    useEffect(() => {
        fetch('https://dummyjson.com/recipes')
            .then(response => response.json())
            .then(data => {
                setData(data.recipes); // Set recipes
                setLoading(false); // Stop loading
                setTotalPages(Math.ceil(data.recipes.length / itemsPerPage)); // Calculate total pages
            })
            .catch(error => {
                console.log('Error fetching data:', error);
                setLoading(false); // Stop loading in case of error
            });
    }, []);

    // Handle pagination (slice the data based on current page)
    const indexOfLastRecipe = currentPage * itemsPerPage;
    const indexOfFirstRecipe = indexOfLastRecipe - itemsPerPage;
    const currentRecipes = data.slice(indexOfFirstRecipe, indexOfLastRecipe);

    // Handle page change
    const handlePageChange = (page) => {
        if (page >= 1 && page <= totalPages) {
            setCurrentPage(page);
        }
    };

    // Loading message while data is being fetched
    if (loading) {
        return <div>Loading...</div>;
    }

    // If no recipes are available
    if (!data || data.length === 0) {
        return <div>No recipes found</div>;
    }

    return (
        <div>
            <Navbar />
            <div id="recipeCarousel" className="carousel slide" data-bs-ride="carousel ">
                <div className="container row mt-5">
                    {/* First column with background or content */}
                    <div className="col-12 col-md-6 bg-light">
                        <p className="display-6 text-muted mb-4">Welcome to our recipe collection!</p>
                        <h1 className="display-4 text-primary fw-bold mb-4">Hi, welcome!</h1>
                        <p className="lead text-dark mb-4">
                            Introduce the recipe: Start with a short introduction to the recipe, noting its origin and anything interesting about the ingredients, as well as any notes about hard-to-find ingredients or special equipment required. Home cooks and cocktail aficionados appreciate prepping and cooking times as well.
                        </p>
                        <p className="text-secondary">
                            Explore our diverse range of recipes, handpicked for you to enjoy. Whether you're an experienced chef or a beginner, these recipes will inspire your next cooking adventure. We provide detailed instructions and tips to make cooking easier and more fun!
                        </p>
                    </div>

                    {/* Carousel Column */}
                    <div className="col-6">
                        <div className="carousel-inner">
                            {currentRecipes.slice(0, 3).map((recipe, index) => (
                                <div className={`carousel-item ${index === 0 ? 'active' : ''}`} key={index}>
                                    <img
                                        src={recipe.image}
                                        className="d-block w-100 h-100" // Ensure the image takes full width of the column
                                        alt={recipe.name}
                                        style={{
                                            maxWidth: '600px',
                                            height: '400px', // Fixed height for carousel image
                                            objectFit: 'cover' // Ensures images cover the area without distortion
                                        }}
                                    />
                                    <div className="carousel-caption d-none d-md-block">
                                        <h5>{recipe.name}</h5>
                                        <p>{recipe.description}</p>
                                    </div>
                                </div>
                            ))}
                        </div>

                        {/* Carousel controls */}
                        <button
                            className="carousel-control-prev"
                            type="button"
                            data-bs-target="#recipeCarousel"
                            data-bs-slide="prev"
                        >
                            <span className="carousel-control-prev-icon" aria-hidden="true"></span>
                            <span className="visually-hidden">Previous</span>
                        </button>
                        <button
                            className="carousel-control-next"
                            type="button"
                            data-bs-target="#recipeCarousel"
                            data-bs-slide="next"
                        >
                            <span className="carousel-control-next-icon" aria-hidden="true"></span>
                            <span className="visually-hidden">Next</span>
                        </button>
                    </div>
                </div>


                <button className="carousel-control-prev" type="button" data-bs-target="#recipeCarousel" data-bs-slide="prev">
                    <span className="carousel-control-prev-icon" aria-hidden="true"></span>
                    <span className="visually-hidden">Previous</span>
                </button>
                <button className="carousel-control-next" type="button" data-bs-target="#recipeCarousel" data-bs-slide="next">
                    <span className="carousel-control-next-icon" aria-hidden="true"></span>
                    <span className="visually-hidden">Next</span>
                </button>
            </div>

            {/* Recipe Cards */}
            <div className="container my-4">
                <div className="row">
                    {/* Recipe cards with pagination */}
                    {currentRecipes.map((recipe, index) => (
                        <div key={index} className="col-md-4 mb-4">
                            <div className="card w-100">
                                <img src={recipe.image} className="card-img-top" alt={recipe.name} />
                                <div className="card-body">
                                    <h5 className="card-title">{recipe.name}</h5>
                                    <p className="card-text">{recipe.description}</p>
                                    <Link to="/signup" className="btn btn-primary">
                                        {recipe.name}
                                    </Link>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>

                {/* Pagination Controls */}
                <div className="d-flex justify-content-center my-4">
                    <button onClick={() => handlePageChange(currentPage - 1)} disabled={currentPage === 1} className="btn btn-outline-secondary me-3">
                        Previous
                    </button>
                    <span>Page {currentPage} of {totalPages}</span>
                    <button onClick={() => handlePageChange(currentPage + 1)} disabled={currentPage === totalPages} className="btn btn-outline-secondary ms-3">
                        Next
                    </button>
                </div>
            </div>
        </div >
    );
};

export default EntryPage;
