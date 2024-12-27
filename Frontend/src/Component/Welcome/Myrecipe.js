import axios from 'axios';
import React, { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import UserNavbar from './UserNavbar';

const Myrecipe = () => {
  const navigate = useNavigate();
  const [data, setData] = useState([]);
  const [error, setError] = useState(null);
  const [message, setMessage] = useState('');
  const token = useSelector((state) => state.auth.token);

  useEffect(() => {
    const fetchData = async () => {
      try {
        if (token) {
          const response = await axios.get('http://localhost:8080/recipeenter/myrecipe', {
            headers: {
              'Content-Type': 'application/json',
              Authorization: `Bearer ${token}`,
            },
          });

          if (Array.isArray(response.data)) {
            setData(response.data);
          } else {
            setData([]);
            setError('Received data is not in the expected format.');
          }
        }
      } catch (err) {
        if (err.response) {
          setError(`Error: ${err.response.status} - ${err.response.data || err.response.statusText}`);
        } else if (err.request) {
          setError('Network error: No response from server.');
        } else {
          setError(`Error in code: ${err.message}`);
        }
      }
    };

    if (token) {
      fetchData();
    }
  }, [token]);

  const handleView = (id) => {
    navigate(`/recipe/${id}`);
  };

  const handleEdit = (id) => {
    navigate(`/edit-recipe/${id}`);
  };

  const handleDelete = async (id) => {
    try {
      const response = await axios.delete(`http://localhost:8080/recipeenter/deleterecipe/${id}`, {
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
      });

      if (response.status === 200 || response.status === 204) {
        setMessage('DELETED SUCCESSFULLY');
      } else {
        setMessage('ERROR IN DELETING');
      }
    } catch (err) {
      if (err.response) {
        setMessage(`Error: ${err.response.status} - ${err.response.data || err.response.statusText}`);
      } else if (err.request) {
        setMessage('Network error: No response from the server.');
      } else {
        setMessage(`Error in code: ${err.message}`);
      }
    }
  };

  const handleRated = (id) => {
    navigate(`/diagram/${id}`);
  };

  return (
    <div
      style={{
        backgroundImage:
          'url(https://img.freepik.com/premium-photo/wide-view-from-banner-image-vegetarian-day-food_1030895-74118.jpg)',
        backgroundSize: 'cover',
        backgroundPosition: 'center',
        backgroundAttachment: 'fixed',
        minHeight: '100vh',
        padding: '20px',
      }}
    >
      <UserNavbar />
      <h1 className="text-center display-4 text-primary mb-4">My Recipe</h1>

      {message && (
        <div className={`alert ${message === 'DELETED SUCCESSFULLY' ? 'alert-success' : 'alert-danger'} text-center`}>
          {message}
        </div>
      )}

      <div className="container-fluid">
        {error && <p className="text-danger text-center">{error}</p>}

        {data.length > 0 ? (
          <div className="row g-4">
            {data.map((recipe, index) => (
              <div key={index} className="col-12 col-sm-6 col-md-4 col-lg-3">
                <div className="card shadow-lg rounded h-100">
                  <img
                    src={recipe.imageUrl}
                    alt={recipe.recipeTitle}
                    className="card-img-top img-fluid"
                    style={{
                      height: '200px',
                      objectFit: 'cover',
                    }}
                  />
                  <div className="card-body d-flex flex-column">
                    <h5 className="card-title text-primary">{recipe.title || 'Recipe Name'}</h5>
                    <div className="d-flex align-items-center mb-2">
                      <small className="text-muted me-2">Cooking Time:</small>
                      <small className="fw-bold">{recipe.cookingTime}</small>
                    </div>
                    <span
                      className={`badge ${
                        recipe.difficultyLevel === 'EASY'
                          ? 'bg-success'
                          : recipe.difficultyLevel === 'MEDIUM'
                          ? 'bg-warning'
                          : recipe.difficultyLevel === 'HARD'
                          ? 'bg-danger'
                          : 'bg-secondary'
                      }`}
                      style={{ fontSize: '14px' }}
                    >
                      {recipe.difficultyLevel}
                    </span>
                    <div className="d-flex flex-wrap justify-content-between mt-3 gap-2">
                      <button
                        onClick={() => handleView(recipe.id)}
                        className="btn btn-outline-success rounded-pill flex-grow-1"
                      >
                        View
                      </button>
                      <button
                        onClick={() => handleEdit(recipe.id)}
                        className="btn btn-outline-warning rounded-pill flex-grow-1"
                      >
                        Edit
                      </button>
                      <button
                        onClick={() => handleRated(recipe.id)}
                        className="btn btn-outline-info rounded-pill flex-grow-1"
                      >
                        Rated
                      </button>
                      <button
                        onClick={() => handleDelete(recipe.id)}
                        className="btn btn-outline-danger rounded-pill flex-grow-1"
                      >
                        Delete
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <p className="text-center">No recipes found.</p>
        )}
      </div>
    </div>
  );
};

export default Myrecipe;
