import { createSlice } from '@reduxjs/toolkit';

// Initial state setup
const initialState = {
  token: localStorage.getItem('authToken') || null, // Load token from localStorage
};

const authSlice = createSlice({
  name: 'auth',
  initialState, // Corrected this part: use the defined initialState
  reducers: {
    // Action to store the token on login
    LOGIN(state, action) {
      state.token = action.payload; // Set token to payload
      // Save token to localStorage to persist on refresh
      localStorage.setItem('authToken', action.payload);
    },
    LOGOUT(state) {
      state.token = null;
      localStorage.removeItem('authToken');
    },
  },
});

// Export the action creators so you can dispatch them in components
export const { LOGIN, LOGOUT } = authSlice.actions;

// Export the reducer to be used in the store
export default authSlice.reducer;
