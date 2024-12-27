import axios from 'axios';
import React, { useState } from 'react';
import { useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import '../Main/CSS/profile.css'; // Assuming the CSS file is in the same folder
import UserNavbar from './UserNavbar';

const Profile = () => {
    const navigate = useNavigate();
    const [passwordReset, setPasswordReset] = useState(false);
    const [status,setstatus]=useState(0);
    const [message,setMessage]=useState('');
    const [formData, setFormData] = useState({
        phoneNumber: '',
        address: '',
        currentPassword: '',
        newPassword: '',
        confirmPassword: '',
    });

    // Handle input changes
    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData(prevData => ({
            ...prevData,
            [name]: value,
        }));
    };

    // Handle the Reset Password button click
    const togglePasswordReset = () => {
        setPasswordReset(!passwordReset);
    };

    // Handle form submission
    const handleSubmit = (e) => {
        e.preventDefault(); // Prevent default form submission behavior
        if (formData.newPassword && formData.newPassword !== formData.confirmPassword) {
            alert('Passwords do not match!');
            return;
        }
        console.log('Form Submitted:', formData);
        passdata();
    };

    const token = useSelector(state => state.auth.token);

    // Function to handle data submission to the backend
    const passdata = async () => {
        try {
            if (token) {
                const response = await axios.post('http://localhost:8080/recipeenter/details', formData, {
                    headers: {
                        "Content-Type": 'application/json',
                        'Authorization': `Bearer ${token}`
                    }
                });
                console.log(response)
                setstatus(response.status)
                if(response.status===200){
                    setMessage("Updated successfully")
                }
                else{
                    setMessage("Failed to update ")
                }
            } else {
                // Redirect to login page if there's no token
                navigate("/login");
            }
        } catch (error) {
            console.log("Error in updating", error); 
        }
    };

    return (
        <div className='container-fluid p-2'>
            <UserNavbar />
            {status===200?<div>
                <span className='alert alert-success m-auto'>{message}</span>
            </div>:<div></div>}
            <h1 className='text-center display-4 text-secondary'>Add Information</h1>
            <div className="profile-container">
                <form className="profile-form mt-5 p-4" onSubmit={handleSubmit}>
                    
                    <div className="form-group">
                        <label>Phone Number</label>
                        <input
                            type="number"
                            name="phoneNumber"
                            placeholder='Enter your phone number'
                            value={formData.phoneNumber}
                            onChange={handleInputChange}
                        />
                    </div>

                    {/* Address Input */}
                    <div className="form-group">
                        <label>Address</label>
                        <input
                            type="text"
                            name="address"
                            maxLength={250}
                            placeholder='Enter Address'
                            value={formData.address || ''}
                            onChange={handleInputChange}
                        />
                    </div>

                    {/* Reset Password Button */}
                    <button type="button" className="reset-btn" onClick={togglePasswordReset}>
                        Reset Password
                    </button><br></br>

                    {/* Password Reset Section */}
                    {passwordReset && (
                        <div className="password-reset-section">
                            <div className="form-group">
                                <label>Enter Your Current Password</label>
                                <input
                                    type="password"
                                    name="currentPassword"
                                    value={formData.currentPassword}
                                    onChange={handleInputChange}
                                />
                            </div>
                            <div className="form-group">
                                <label>Enter New Password</label>
                                <input
                                    type="password"
                                    name="newPassword"
                                    value={formData.newPassword}
                                    onChange={handleInputChange}
                                />
                            </div>
                            <div className="form-group">
                                <label>Re-Enter New Password</label>
                                <input
                                    type="password"
                                    name="confirmPassword"
                                    value={formData.confirmPassword}
                                    onChange={handleInputChange}
                                />
                            </div>
                        </div>
                    )}

                    {/* Submit Button */}
                    <button type="submit" className='btn btn-primary'>Submit</button>
                </form>
            </div>
        </div>
    );
};

export default Profile;
