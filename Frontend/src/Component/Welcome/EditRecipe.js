import axios from 'axios';
import React, { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import { useNavigate, useParams } from 'react-router-dom';
import UserNavbar from './UserNavbar';

const EditRecipe = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const token = useSelector((state) => state.auth.token);
  const [message, setMessage] = useState('');
  const [recipe, setRecipe] = useState({
    recipeTitle: '',
    ingredients: [],
    instructions: [],
    imageUrl: '',
    cookingTime: '',
    difficultyLevel: '',
  });
  const [imageFile, setImageFile] = useState(null);

  useEffect(() => {
    const fetchRecipe = async () => {
      try {
        const response = await axios.get(`http://localhost:8080/recipeenter/recipe/${id}`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
  
        if (response.data) {
          const { ingredients, instructions, imageUrl, ...rest } = response.data;
  
          // Function to clean and parse ingredients and instructions
          const parseData = (data, delimiter = ',') => {
            return data
              ? data.split(delimiter).map((item) => item.trim())
              : [];
          };
  
          setRecipe({
            ...rest,
            ingredients: parseData(ingredients),
            instructions: parseData(instructions, '\r\n'),
            imageUrl: imageUrl || '',
          });
        }
      } catch (error) {
        console.error('Error fetching recipe:', error);
        setMessage('Failed to fetch recipe data.');
      }
    };
  
    if (id && token) fetchRecipe();
  }, [id, token]);
  

  const handleInputChange = (e) => {
    const { name, value } = e.target;

    if (name === 'ingredients') {
      setRecipe({
        ...recipe,
        ingredients: value.split('\n').map((line) => line.trim()),
      });
    } else if (name === 'instructions') {
      setRecipe({
        ...recipe,
        instructions: value.split('\n').map((line) => line.trim()),
      });
    } else {
      setRecipe({ ...recipe, [name]: value });
    }
  };

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (file && file.type.startsWith('image/')) {
      setImageFile(file);
      const reader = new FileReader();
      reader.onload = () => {
        setRecipe({ ...recipe, imageUrl: reader.result });
      };
      reader.readAsDataURL(file);
    } else {
      alert('Please upload a valid image file.');
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const updatedRecipe = {
        ...recipe,
        ingredients: JSON.stringify(recipe.ingredients),
        instructions: JSON.stringify(recipe.instructions),
      };

      const formData = new FormData();
      formData.append('title', updatedRecipe.recipeTitle);
      formData.append('ingredients', updatedRecipe.ingredients);
      formData.append('instructions', updatedRecipe.instructions);
      formData.append('cookingTime', updatedRecipe.cookingTime);
      formData.append('difficultyLevel', updatedRecipe.difficultyLevel);

      if (imageFile) {
        formData.append('image', imageFile);
      }

      const response = await axios.put(
        `http://localhost:8080/recipeenter/updaterecipe/${id}`,
        formData,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (response.status === 200) {
        setMessage('Update Successful!');
        setTimeout(() => navigate('/my-recipe'), 2000);
      }
    } catch (error) {
      console.error('Error updating recipe:', error);
      setMessage('Failed to update recipe.');
    }
  };

  return (
    <><UserNavbar/>
    <div
      style={{
        backgroundImage: 'url(https://img.freepik.com/premium-photo/wide-view-from-banner-image-vegetarian-day-food_1030895-74118.jpg)',
        backgroundSize: 'cover',
        backgroundPosition: 'center',
        minHeight: '100vh',
        padding: '20px',
      }}
    >
      <div className="container mt-5">
        <h2>Edit Recipe</h2>
        {message && <div className="alert alert-info">{message}</div>}
        <form onSubmit={handleSubmit}>
          <div className="mb-3">
            <label htmlFor="recipeTitle" className="form-label">Recipe Name</label>
            <input
              type="text"
              className="form-control"
              id="recipeTitle"
              name="recipeTitle"
              value={recipe.recipeTitle}
              onChange={handleInputChange}
              required
            />
          </div>

          <div className="mb-3">
            <label htmlFor="ingredients" className="form-label">Ingredients</label>
            <textarea
              className="form-control"
              id="ingredients"
              name="ingredients"
              value={recipe.ingredients.join('\n')}
              onChange={handleInputChange}
              placeholder="Enter ingredients, one per line (e.g., '2 cups flour')"
              required
            />
          </div>

          <div className="mb-3">
            <label htmlFor="instructions" className="form-label">Instructions</label>
            <textarea
              className="form-control"
              id="instructions"
              name="instructions"
              value={recipe.instructions.join('\n')}
              onChange={handleInputChange}
              placeholder="Enter instructions, one step per line"
              required
            />
          </div>

          <div className="mb-3">
            <label htmlFor="image" className="form-label">Image Upload</label>
            <input
              type="file"
              className="form-control"
              id="image"
              name="image"
              onChange={handleFileChange}
            />
            {recipe.imageUrl && <img src={recipe.imageUrl} alt="Recipe" className="img-thumbnail mt-3" style={{ maxWidth: '300px' }} />}
          </div>

          <div className="mb-3">
            <label htmlFor="cookingTime" className="form-label">Cooking Time</label>
            <input
              type="text"
              className="form-control"
              id="cookingTime"
              name="cookingTime"
              value={recipe.cookingTime}
              onChange={handleInputChange}
              required
            />
          </div>

          <div className="mb-3">
            <label htmlFor="difficultyLevel" className="form-label">Difficulty</label>
            <select
              className="form-select"
              id="difficultyLevel"
              name="difficultyLevel"
              value={recipe.difficultyLevel}
              onChange={handleInputChange}
              required
            >
              <option value="">Select difficulty</option>
              <option value="EASY">Easy</option>
              <option value="MEDIUM">Medium</option>
              <option value="HARD">Hard</option>
            </select>
          </div>

          <button type="submit" className="btn btn-primary">Save Changes</button>
        </form>
      </div>
    </div>
    </>
  );
};

export default EditRecipe;
