import axios from 'axios';
import React, { useState } from 'react';
import { useDispatch } from 'react-redux';
import { Link, useNavigate } from 'react-router-dom';
// In store.js
import { LOGIN } from '../redux/reducer/authReducer';
import './CSS/login.css'; // import the CSS file for styling
import Navbar from './Navbar';
const Login = () => {
  const [emailpassword, setemailpassword] = useState({
    email: '',
    password: ''
  });
  const dispatch = useDispatch();
  const navigate = useNavigate(); // Hook for navigation
  const [message, setMessage] = useState('');
  const [messageType, setMessageType] = useState('');
  const handledata = (e) => {
    e.preventDefault();

    // Validate that both email and password are filled
    if (!emailpassword.email || !emailpassword.password) {
      setMessage("Please fill in all required fields");
      setMessageType('error');
      return;
    }
    UserSubmitSend();
  };

  const UserSubmitSend = async () => {
    try {
      // Send request to backend
      const response = await axios.post('http://localhost:8080/user/login', emailpassword, {
        headers: {
          "Content-Type": 'application/json'
        }
      });

      if (response.status === 200) {
        setMessage(response.data.message || "Login successful!");
        setMessageType('success');
        
        // Save the token to localStorage
        localStorage.setItem('token', response.data.token);
        
        // Dispatch login action with token
        dispatch(LOGIN(response.data.token));
        setTimeout(() => {
          navigate('/welcome'); 
          
        }, 1000); 
      } else {
        setMessage(response.data.message || "Login failed");
        setMessageType('error');
      }
    } catch (error) {
      setMessage("An error occurred. Please try again.");
      setMessageType('error');
    }
  };
  return (
    <section className="vh-100">
      <Navbar />
      <div className="container-fluid h-custom">
        <div className="row d-flex justify-content-center align-items-center h-100">
          <div className="col-md-9 col-lg-6 col-xl-5">
            <img
              src="https://media.istockphoto.com/id/1468860049/photo/fitness-woman-eating-a-healthy-poke-bowl-in-the-kitchen-at-home.jpg?s=612x612&w=0&k=20&c=XDY46BP4RgFqq27GjLYbrAhIUnz3rkKXlu0axO-N39A="
              className="img-fluid w-100 h-100 image-rounded"
              alt="Healthy poke bowl prepared in the kitchen"
            />
          </div>

          <div className="col-md-8 col-lg-6 col-xl-4 offset-xl-1">
            {/* Displaying the response message (success or error) */}
            {message && (
              <div
                className={`alert alert-${messageType === 'success' ? 'success' : 'danger'} mb-4`}
                role="alert"
                style={{
                  transition: 'all 0.5s ease',
                  marginBottom: '20px',
                }}
              >
                {message}
              </div>
            )}
            <form onSubmit={handledata}>
              <div className="d-flex flex-row align-items-center justify-content-center justify-content-lg-start">
                <p className="lead fw-normal mb-0 me-3">Sign in with</p>
                <button type="button" className="btn btn-primary btn-floating mx-1">
                  <i className="fab fa-facebook-f"></i>
                </button>
                <button type="button" className="btn btn-primary btn-floating mx-1">
                  <i className="fab fa-twitter"></i>
                </button>
                <button type="button" className="btn btn-primary btn-floating mx-1">
                  <i className="fab fa-linkedin-in"></i>
                </button>
              </div>

              <div className="divider d-flex align-items-center my-4">
                <p className="text-center fw-bold mx-3 mb-0">Or</p>
              </div>

              {/* Email input */}
              <div className="form-outline mb-4">
                <input
                  type="email"
                  id="form3Example3"
                  className="form-control form-control-lg"
                  placeholder="Enter a valid email address"
                  name="email"
                  value={emailpassword.email}
                  onChange={(e) => setemailpassword({ ...emailpassword, email: e.target.value })}
                  required
                />
                <label className="form-label" htmlFor="form3Example3">
                  Email address
                </label>
              </div>

              {/* Password input */}
              <div className="form-outline mb-3">
                <input
                  type="password"
                  id="form3Example4"
                  className="form-control form-control-lg"
                  placeholder="Enter password"
                  name="password"
                  value={emailpassword.password}
                  onChange={(e) => setemailpassword({ ...emailpassword, password: e.target.value })}
                  required
                />
                <label className="form-label" htmlFor="form3Example4">
                  Password
                </label>
              </div>

              <div className="d-flex justify-content-between align-items-center">
                {/* Checkbox */}
                <div className="form-check mb-0">
                  <input className="form-check-input me-2" type="checkbox" value="" id="form2Example3" />
                  <label className="form-check-label" htmlFor="form2Example3">
                    Remember me
                  </label>
                </div>
                <div className="text-center text-lg-start mt-4 pt-2">
                  <button
                    type="submit" // Changed to submit
                    className="btn btn-primary btn-lg"
                    style={{ paddingLeft: '2.5rem', paddingRight: '2.5rem' }}
                  >
                    Login
                  </button>
                </div>
              </div>
              <p className="small fw-bold mt-2 pt-1 mb-0">
                <h6 className="text-dark">
                  Don't have an account?
                  <Link to={'/signup'} className="link-primary m-5 text-decoration-none">
                    Sign up
                  </Link>
                </h6>
              </p>
            </form>
          </div>
        </div>
      </div>

      {/* Footer */}
      <div className="d-flex flex-column flex-md-row text-center text-md-start justify-content-between py-4 px-4 px-xl-5 bg-primary">
        <div className="text-white mb-3 mb-md-0">
          Copyright © 2020. All rights reserved.
        </div>

        <div>
          <a href="#!" className="text-white me-4">
            <i className="fab fa-facebook-f"></i>
          </a>
          <a href="#!" className="text-white me-4">
            <i className="fab fa-twitter"></i>
          </a>
          <a href="#!" className="text-white me-4">
            <i className="fab fa-google"></i>
          </a>
          <a href="#!" className="text-white">
            <i className="fab fa-linkedin-in"></i>
          </a>
        </div>
      </div>
    </section>
  );
};

export default Login;
