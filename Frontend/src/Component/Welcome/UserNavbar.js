import axios from 'axios';
import React, { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Link, useNavigate } from 'react-router-dom';
import { LOGOUT } from '../redux/reducer/authReducer';
import { fetchRecipes, setSearchResults } from '../redux/reducer/searchData';
const UserNavbar = () => {
    const navigate = useNavigate();
    const [searchQuery, setSearchQuery] = useState('');
    const token = useSelector((state) => state.auth.token);
    const dispatch = useDispatch();
    const [message, setmessage] = useState('')

    const handleSearchChange = (e) => {
        const query = e.target.value;
        setSearchQuery(query);

        // If search query is cleared, fetch all recipes
        if (query.trim() === '') {
            dispatch(fetchRecipes(token));  // Fetch all recipes if search query is empty
        }
        // Trigger search when query length is greater than 2 characters
        else if (query.trim().length >= 2) {
            // Optionally trigger search only when query length is sufficient (e.g., >= 2)
        }
    };

    const handleSearchSubmit = (e) => {
        e.preventDefault();

        if (searchQuery.trim() === '') {
            // If the search query is empty, fetch all recipes
            dispatch(fetchRecipes(token));
            return;
        }

        // Perform search request if the query is not empty
        axios
            .get('http://localhost:8080/recipeenter/search', {
                params: { search: searchQuery },
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`,
                },
            })
            .then((response) => {
                console.log(response.data);
                dispatch(setSearchResults(response.data)); // Store search results in Redux
            })
            .catch((error) => {
                console.error("Error during search:", error.response ? error.response.data : error.message);
            });
    };
    const handleLogout = async () => {
        try {
            // Send the logout request to the backend
            const response = await axios.post(`http://localhost:8080/recipeenter/logout`, {}, {
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`, // Assuming 'token' is available in your component's state
                }
            });

            // Check if the logout was successful
            if (response.status === 200) {
                setmessage("LOGOUT IN A SECOND")
                // Clear token from localStorage
                localStorage.removeItem('token'); // replace 'token' with the key your token is stored under

                // Dispatch the action to clear token in the Redux store
                dispatch(LOGOUT());
                setTimeout(() => {
                    navigate('/')
                }, 1000)
            }
        } catch (error) {
            setmessage("LOGOUT ERROR")
            console.error('Error during logout:', error);
        }
    };

    return (
        <div>
            <nav className="navbar navbar-expand-lg navbar-light bg-light shadow-sm fixed-top">
                <div className="container-fluid px-4">
                    <Link to={'/profile'}>
                        <img
                            src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRV4AiTVnmQsYoyqk2qQUXddEUGsA_0L7tbbuxJZVdG1fT3s9iEEQdmfYYwv9x-d1HragA&usqp=CAU"
                            className="img-fluid rounded-circle"
                            alt="Navbar Logo"
                            style={{ width: '50px', height: '50px', objectFit: 'cover' }}
                        />
                    </Link>
                    <Link className="navbar-brand fw-bold text-primary" style={{ marginLeft: '20px' }} to="/">
                        Taste it Out
                    </Link>

                    <button
                        className="navbar-toggler"
                        type="button"
                        data-bs-toggle="collapse"
                        data-bs-target="#navbarSupportedContent"
                        aria-controls="navbarSupportedContent"
                        aria-expanded="false"
                        aria-label="Toggle navigation"
                    >
                        <span className="navbar-toggler-icon"></span>
                    </button>

                    <div className="collapse navbar-collapse" style={{ width: '100%' }} id="navbarSupportedContent">
                        <span className="nav-item w-25">
                            <Link className="nav-link text-decoration-none" to="/my-recipe">
                                ❤️My Recipe
                            </Link>
                        </span>

                        <form className="d-flex ms-100 my-2 my-lg-0" role="search" style={{ width: '100%' }} onSubmit={handleSearchSubmit}>
                            <input
                                className="form-control me-2 rounded-pill shadow-sm w-100 w-md-60"
                                type="search"
                                placeholder="Search"
                                aria-label="Search"
                                value={searchQuery}
                                onChange={handleSearchChange}
                            />
                            <button className="btn btn-outline-primary rounded-pill shadow-sm" type="submit">
                                Search
                            </button>
                        </form>

                        <ul className="navbar-nav ms-auto mb-2 mb-lg-0">
                            <li className="nav-item">
                                <Link className="nav-link m-3 active" to="/welcome">Home</Link>
                            </li>
                            <li className="nav-item">
                                <Link className="nav-link m-3" to="/contact">Contact</Link>
                            </li>
                            <li className="nav-item">
                                <Link className="nav-link m-3" to="/about">About</Link>
                            </li>
                            <li className="nav-item">
                                <Link className="nav-link m-3 text-danger" onClick={handleLogout}>Logout</Link>
                            </li>
                        </ul>
                    </div>
                </div>
            </nav>
            <div style={{ marginTop: '80px' }}></div>
            <div className={`${message.trim() === '' ? 'd-none' : 'alert alert-success text-center w-800'}`}>
                {message}
            </div>

        </div>
    );
};

export default UserNavbar;
