import axios from 'axios';
import { PlusCircle, XCircle } from 'lucide-react';
import React, { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Link } from 'react-router-dom';
import { clearSearchResults, fetchRecipes, searchRecipes } from '../redux/reducer/searchData';
import UserNavbar from './UserNavbar';

const MainPage = () => {
  const dispatch = useDispatch();
  const token = useSelector((state) => state.auth.token);
  const searchResults = useSelector((state) => state.searchData.searchResults || []);
  const message = useSelector((state) => state.searchData.message);
  const loading = useSelector((state) => state.searchData.loading);
  const error = useSelector((state) => state.searchData.error);
  const [searchQuery, setSearchQuery] = useState('');
  const [likedRecipes, setLikedRecipes] = useState({});

  // Pagination State
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 6;
  const [totalPages, setTotalPages] = useState(1);

  const clearSearch = () => {
    setSearchQuery('');
    dispatch(clearSearchResults());
  };

  const fetchAllRecipes = () => {
    setSearchQuery('');
    dispatch(fetchRecipes(token));
  };

  const handleLikeClick = async (recipeId) => {
    try {
      const currentLikeStatus = likedRecipes[recipeId];
      
      // Send the like/unlike request to the server
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

      // Toggle the like status and update the like count from the server response
      setLikedRecipes((prevState) => ({
        ...prevState,
        [recipeId]: !currentLikeStatus, // Toggle like state
      }));

    } catch (error) {
      console.log('Error during like API call:', error);
    }
  };

  useEffect(() => {
    // Fetch recipes with pagination
    fetchPaginatedRecipes(currentPage);
  }, [dispatch, token, searchQuery, currentPage]);

  // Fetch recipes with pagination
  const fetchPaginatedRecipes = (page) => {
    if (!token) return;
    const params = {
      page: page,
      size: itemsPerPage,
    };

    if (searchQuery.trim()) {
      dispatch(searchRecipes({ token, query: searchQuery, ...params }));
    } else {
      dispatch(fetchRecipes(token, params)); // Pass pagination params if no search query
    }
  };

  // Calculate total pages based on searchResults length
  useEffect(() => {
    if (searchResults.length > 0) {
      const total = Math.ceil(searchResults.length / itemsPerPage);
      setTotalPages(total);
    }
  }, [searchResults]);

  if (loading) {
    return (
      <div className="d-flex justify-content-center align-items-center vh-100">
        <div className="spinner-border text-success" role="status">
          <span className="visually-hidden">Loading...</span>
        </div>
        <h2 className="ms-3 text-success">Loading Delicious Recipes...</h2>
      </div>
    );
  }

  if (error) {
    return (
      <div className="container vh-100 d-flex justify-content-center align-items-center">
        <div className="text-center">
          <h2 className="display-4 text-danger mb-4">Oops! Something went wrong</h2>
          <p className="lead">{error}</p>
        </div>
      </div>
    );
  }

  if (searchResults.length === 0) {
    return (
      <div className="container vh-100 d-flex justify-content-center align-items-center">
        <div className="text-center">
          <h2 className="display-6 mb-4">No Recipes Found</h2>
          <p className="lead">{message || "Check back later for delicious recipes!"}</p>
        </div>
      </div>
    );
  }

  // Pagination Controls
  const handlePageChange = (newPage) => {
    if (newPage < 1 || newPage > totalPages) return; // Prevent out of bounds
    setCurrentPage(newPage);
  };

  // Slice search results for the current page
  const currentPageResults = searchResults.slice((currentPage - 1) * itemsPerPage, currentPage * itemsPerPage);

  return (
    <div>
      <UserNavbar setSearchQuery={setSearchQuery} />
      <div className="container-fluid bg-light min-vh-100">
        <div className="container">
          <div className="row mb-4 align-items-center justify-content-center">
            <div className="col-auto text-center">
              <h1 className="display-4 text-success mb-0 d-inline-block me-4">Delicious Recipes Await</h1>
              <div className="d-inline-flex align-items-center">
                <Link
                  to='/add-recipe'
                  className="btn btn-outline-primary rounded-circle p-0 d-flex justify-content-center align-items-center"
                  style={{ width: '50px', height: '50px', marginLeft: '0.5rem' }}
                >
                  <PlusCircle size={40} />
                </Link>

                {/* Only show the red button if searching happens (searchQuery has value) */}
                {searchQuery && (
                  <button 
                    onClick={fetchAllRecipes} 
                    className="btn btn-danger rounded-circle p-2 ms-3">
                    <XCircle size={20} />
                  </button>
                )}
              </div>
            </div>
          </div>

          <div className="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
            {currentPageResults.map((recipe) => (
              <div key={recipe.recipeId} className="col">
                <div className="card h-100 shadow-sm position-relative">
                  <div className="position-relative overflow-hidden">
                    <img
                      src={recipe.imageUrl}
                      alt={recipe.recipeTitle}
                      className="card-img-top recipe-image transition"
                      style={{
                        height: '250px',
                        objectFit: 'cover',
                        transition: 'transform 0.3s ease-in-out'
                      }}
                    />
                  </div>
                  <div className="card-body">
                    <div className="d-flex justify-content-between align-items-center mb-2">
                      <Link to={`/recipe/${recipe.recipeId}`} className="text-decoration-none">
                        <h5 className="card-title text-success">{recipe.recipeTitle}</h5>
                      </Link>
                    </div>
                    <div className="d-flex justify-content-between align-items-center mb-2">
                      <small className="text-muted">
                        Created by: <span className="fw-bold">{recipe.userName}</span>
                      </small>
                    </div>
                    <div className="d-flex justify-content-between align-items-center">
                      <div className="d-flex align-items-center">
                        <small className="text-muted me-2">Cooking Time:</small>
                        <small className="fw-bold">{recipe.cookingTime}</small>
                      </div>
                      <span
                        className={`badge ${recipe.difficultyLevel === 'EASY' ? 'bg-success' :
                          recipe.difficultyLevel === 'MEDIUM' ? 'bg-warning' :
                          recipe.difficultyLevel === 'HARD' ? 'bg-danger' : 'bg-secondary'
                        }`}
                      >
                        {recipe.difficultyLevel}
                      </span>
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>

          {/* Pagination Controls */}
          <div className="d-flex justify-content-center my-4">
            <button 
              onClick={() => handlePageChange(currentPage - 1)} 
              className="btn btn-outline-secondary me-3"
              disabled={currentPage === 1}
            >
              Previous
            </button>
            <span className="align-self-center">
              Page {currentPage} of {totalPages}
            </span>
            <button 
              onClick={() => handlePageChange(currentPage + 1)} 
              className="btn btn-outline-secondary ms-3"
              disabled={currentPage === totalPages}
            >
              Next
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default MainPage;
