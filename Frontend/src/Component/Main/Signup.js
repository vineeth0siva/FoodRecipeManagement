import axios from 'axios';
import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import Navbar from './Navbar';

const Signup = () => {
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    subscribeNewsletter: false,
  });
const navigators=useNavigate();
  const [responseMessage, setResponseMessage] = useState(''); // State for response message
  const [messageType, setMessageType] = useState(''); // To handle success or error message type

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    if (type === 'checkbox') {
      setFormData((prevState) => ({ ...prevState, [name]: checked }));
    } else {
      setFormData((prevState) => ({ ...prevState, [name]: value }));
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    console.log('Form submitted with:', formData);
    submitUserData();
  };

  const submitUserData = async () => {
    if (formData.email && formData.password && formData.firstName) {
      try {
        const response = await axios.post('http://localhost:8080/user/register', formData, {
          headers: {
            'Content-Type': 'application/json',
          },
        });

        console.log('Response:', response.data);

        if (response.data) {
          setResponseMessage(response.data); 
          setMessageType('success'); 
          setTimeout(()=>{
            navigators('/login')
          },1000);
        } 
        else {
          // Fallback in case there's no message field
          setResponseMessage("Registration failed."+response.data);
          setMessageType('error');
        }

      } catch (error) {
        console.error('Error:', error);
        setResponseMessage("Registration failed, please try again."); // Set error message
        setMessageType('error'); // For error, use 'error' type
      }
    } else {
      alert('Please fill in all required fields');
    }
  };

  return (
    <div>
      <Navbar />
      <section
        className="signup-section"
        style={{
          backgroundImage:
            'url(https://img.freepik.com/free-photo/high-angle-view-bruschetta-with-farfalle-raw-pasta-garlic-clove-tomato-oil-basil-leaf-against-isolated-white-background_23-2148195082.jpg?semt=ais_hybrid)',
          backgroundSize: 'cover',
          backgroundPosition: 'center',
          backgroundRepeat: 'no-repeat',
          minHeight: '100vh',
          paddingTop: '50px',
        }}
      >
        <div className="container mt-5">
          <div className="row gx-lg-5 align-items-center">
            <div className="col-lg-6 mb-5 mb-lg-0">
              {/* Optional left-side content */}
            </div>

            <div className="mt-5 col-lg-6 mb-5 mb-lg-0">
              <div className="card signup-card">
                <div className="card-body">
                  {/* Displaying the response message (success or error) */}
                  {responseMessage && (
                    <div
                      className={`alert alert-${messageType === 'success' ? 'success' : 'danger'} mb-4`}
                      role="alert"
                      style={{
                        transition: 'all 0.5s ease',
                        marginBottom: '20px',
                      }}
                    >
                      {responseMessage}
                    </div>
                  )}
                  <form onSubmit={handleSubmit}>
                    {/* 2 column grid layout with text inputs for first and last names */}
                    <div className="row">
                      <div className="col-md-6 mb-4">
                        <div className="form-outline">
                          <input
                            type="text"
                            id="form3Example1"
                            className="form-control"
                            name="firstName"
                            value={formData.firstName}
                            onChange={handleChange}
                            required
                          />
                          <label className="form-label" htmlFor="form3Example1">
                            First name
                          </label>
                        </div>
                      </div>
                      <div className="col-md-6 mb-4">
                        <div className="form-outline">
                          <input
                            type="text"
                            id="form3Example2"
                            className="form-control"
                            name="lastName"
                            value={formData.lastName}
                            onChange={handleChange}
                            required
                          />
                          <label className="form-label" htmlFor="form3Example2">
                            Last name
                          </label>
                        </div>
                      </div>
                    </div>

                    <div className="form-outline mb-4">
                      <input
                        type="email"
                        id="form3Example3"
                        className="form-control"
                        name="email"
                        value={formData.email}
                        onChange={handleChange}
                        required
                      />
                      <label className="form-label" htmlFor="form3Example3">
                        Email address
                      </label>
                    </div>

                    {/* Password input */}
                    <div className="form-outline mb-4">
                      <input
                        type="password"
                        id="form3Example4"
                        className="form-control"
                        name="password"
                        value={formData.password}
                        onChange={handleChange}
                        required
                      />
                      <label className="form-label" htmlFor="form3Example4">
                        Password
                      </label>
                    </div>
                    <div className="form-outline mb-4">
                      <input
                        type="password"
                        id="form3Example4"
                        className="form-control"
                        value={formData.password}
                        onChange={handleChange}
                        required
                      />
                      <label className="form-label" htmlFor="form3Example4">
                       Re-enter Password
                      </label>
                    </div>

                    {/* Checkbox for newsletter subscription */}
                    <div className="form-check mb-4">
                      <input
                        className="form-check-input"
                        type="checkbox"
                        value=""
                        id="form2Example33"
                        name="subscribeNewsletter"
                        checked={formData.subscribeNewsletter}
                        onChange={handleChange}
                      />
                      <label className="form-check-label" htmlFor="form2Example33">
                        Remember Me..
                      </label>
                    </div>

                    {/* Submit button */}
                    <button type="submit" className="btn btn-primary btn-block mb-4 w-100">
                      Sign up
                    </button>
                  </form>
                  <Link to="/login" className="text-decoration-none text-white btn btn-primary btn-block mb-4 w-100">
                    Login
                  </Link>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
};

export default Signup;
