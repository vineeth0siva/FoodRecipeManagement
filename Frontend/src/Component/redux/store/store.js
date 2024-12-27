import { configureStore } from '@reduxjs/toolkit';
// In store.js
import authReducer from '../reducer/authReducer'; // Correct path to auth.js
import searchDataReducer from '../reducer/searchData'; // Correct path to searchData.js


export const store = configureStore({
  reducer: {
    searchData: searchDataReducer,
    auth: authReducer,
  },
});
