import { motion } from 'framer-motion';
import React, { useState } from 'react';
import { useSelector } from 'react-redux';
import UserNavbar from '../Welcome/UserNavbar';
import './CSS/contact.css';
import Navbar from './Navbar';

const ContactPage = () => {
    const [formData, setFormData] = useState({
        name: '',
        email: '',
        message: ''
    });
    const token = useSelector(state => state.auth.token)
    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prevState => ({
            ...prevState,
            [name]: value
        }));
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        // Add form submission logic here
        console.log('Form submitted:', formData);
        // Reset form after submission
        setFormData({
            name: '',
            email: '',
            message: ''
        });
    };

    return (
        <div className="contact-page vh-100 d-flex align-items-center justify-content-center">
            {token ? <UserNavbar /> : <Navbar />}
            <motion.div
                className="container"
                initial={{ opacity: 0, scale: 0.9 }}
                animate={{ opacity: 1, scale: 1 }}
                transition={{ duration: 0.5 }}
            >
                <div className="row justify-content-center contact-page">
                    {/* Contact Form Section */}
                    <div className='col-12 col-md-6'>
                        <motion.div
                            className="card shadow-lg border-0"
                            whileHover={{ scale: 1.02 }}
                            transition={{ type: "spring", stiffness: 300 }}
                        >
                            <div className="card-body p-5">
                                <h2 className="text-center mb-4">Contact Us</h2>
                                <form onSubmit={handleSubmit}>
                                    <div className="mb-3">
                                        <label htmlFor="name" className="form-label">Name</label>
                                        <motion.input
                                            type="text"
                                            className="form-control"
                                            id="name"
                                            name="name"
                                            value={formData.name}
                                            onChange={handleChange}
                                            required
                                            whileFocus={{ scale: 1.02 }}
                                        />
                                    </div>
                                    <div className="mb-3">
                                        <label htmlFor="email" className="form-label">Email</label>
                                        <motion.input
                                            type="email"
                                            className="form-control"
                                            id="email"
                                            name="email"
                                            value={formData.email}
                                            onChange={handleChange}
                                            required
                                            whileFocus={{ scale: 1.02 }}
                                        />
                                    </div>
                                    <div className="mb-3">
                                        <label htmlFor="message" className="form-label">Message</label>
                                        <motion.textarea
                                            className="form-control"
                                            id="message"
                                            name="message"
                                            rows="4"
                                            value={formData.message}
                                            onChange={handleChange}
                                            required
                                            whileFocus={{ scale: 1.02 }}
                                        ></motion.textarea>
                                    </div>
                                    <motion.button
                                        type="submit"
                                        className="btn btn-primary w-100"
                                        whileHover={{ scale: 1.05 }}
                                        whileTap={{ scale: 0.95 }}
                                    >
                                        Send Message
                                    </motion.button>
                                </form>
                            </div>
                        </motion.div>
                    </div>
                    <div className="col-12 col-md-6 d-flex justify-content-center align-items-center">
                        <motion.div
                            className="text-center w-100" 
                            initial={{ opacity: 0 }}
                            animate={{ opacity: 1 }}
                            transition={{ delay: 0.5 }}
                        >
                            <h3 className="text-white mb-3">Our Contact Information</h3> 
                            <div className="contact-info text-dark">
                                <p>📍 Taste it Out😋, Trivandrum Kerala</p>
                                <p>📞 (+91) 7306515836</p>
                                <p className='text-primary'>✉️ RecipeTasteitOutboys@gmail.com</p>
                            </div>
                        </motion.div>
                    </div>

                </div>
            </motion.div>
        </div>
    );
};

export default ContactPage;
