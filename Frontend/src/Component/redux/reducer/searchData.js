import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import axios from 'axios';

// Async Thunks
export const fetchRecipes = createAsyncThunk('recipes/fetchRecipes', async (token) => {
  const response = await axios.get('http://localhost:8080/recipeenter/allrecipes', {
    headers: {
      "Content-Type": "application/json",
      "Authorization": `Bearer ${token}`,
    },
  });
  return response.data;
});

export const searchRecipes = createAsyncThunk('recipes/searchRecipes', async ({ token, query }) => {
  const response = await axios.get(`http://localhost:8080/recipeenter/search?query=${query}`, {
    headers: {
      "Content-Type": "application/json",
      "Authorization": `Bearer ${token}`,
    },
  });
  return response.data;
});

// Initial State
const initialState = {
  searchResults: [],
  loading: false,
  error: null,
  message: '',
};

// Slice
const searchDataSlice = createSlice({
    name: 'searchData',
    initialState,
    reducers: {
      clearSearchResults: (state) => {
        state.searchResults = [];
        state.message = '';
      },
      setSearchResults: (state, action) => {
        state.searchResults = action.payload;  // This will update searchResults directly
      },
    },
    extraReducers: (builder) => {
      builder
        .addCase(fetchRecipes.pending, (state) => {
          state.loading = true;
          state.error = null;
          state.message = '';
        })
        .addCase(fetchRecipes.fulfilled, (state, action) => {
          state.loading = false;
          state.searchResults = action.payload;
          state.message = 'All recipes fetched successfully.';
        })
        .addCase(fetchRecipes.rejected, (state, action) => {
          state.loading = false;
          state.error = action.error.message || 'Failed to fetch recipes.';
        })
        .addCase(searchRecipes.pending, (state) => {
          state.loading = true;
          state.error = null;
          state.message = '';
        })
        .addCase(searchRecipes.fulfilled, (state, action) => {
          state.loading = false;
          state.searchResults = action.payload;
          state.message = `Showing results for the search query.`;
        })
        .addCase(searchRecipes.rejected, (state, action) => {
          state.loading = false;
          state.error = action.error.message || 'Failed to fetch search results.';
        });
    },
  });
  
  export const { clearSearchResults, setSearchResults } = searchDataSlice.actions;  // Add setSearchResults here
  export default searchDataSlice.reducer;
  
