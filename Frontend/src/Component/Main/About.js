import { motion } from 'framer-motion';
import React from 'react';
import { useSelector } from 'react-redux';
import anirudh from '../image/buddy1.jpeg';
import gowtham from '../image/buddy2.jpeg';
import vs from '../image/myanimation.png';
import UserNavbar from '../Welcome/UserNavbar';
import './CSS/Main.css';
import Navbar from './Navbar';
const AboutPage = () => {
    const teamMembers = [
        {
            name: 'V S',
            role: 'Leader',
            bio: 'Builder of this Prject with support of my team',
            image: `${vs}`,
        },
        {
            name: 'Anirudh',
            role: 'Data Collection',
            bio: 'Getting all data related to recipe',
            image:`${anirudh}`
        },
        {
            name: 'Gowtham',
            role: 'information provider',
            bio: 'collecting all information related to user and recipe and pass into this project',
            image: `${gowtham}`
        }
    ];
    const token=useSelector(state=>state.auth.token)

    return (
        <div className="about-page">
            {token?<UserNavbar/>: <Navbar />}
            <motion.div 
                className="container py-5"
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                transition={{ duration: 0.5 }}
            >
                {/* Hero Section */}
                <motion.div 
                    className="row align-items-center mb-5"
                    initial={{ x: -100, opacity: 0 }}
                    animate={{ x: 0, opacity: 1 }}
                    transition={{ duration: 0.7 }}
                >
                    <div className="col-lg-6 text-white">
                        <h1 className="display-4 mb-4 text-light">About Food Haven</h1>
                        <p className="lead">
                            We're more than just a recipe website. We're a community of food lovers 
                            passionate about exploring, creating, and sharing delicious culinary experiences.
                        </p>
                        <p>
                            Founded in 2020, Food Haven has been on a mission to bring joy to kitchens 
                            around the world by providing inspiring, easy-to-follow recipes for every skill level.
                        </p>
                    </div>
                    <div className="col-lg-6">
                        <motion.img 
                            src="https://images.unsplash.com/photo-1519915028121-7d3463d20b13"
                            alt="Food Haven Kitchen"
                            className="img-fluid rounded-lg shadow-lg"
                            whileHover={{ scale: 1.05 }}
                            transition={{ type: "spring", stiffness: 300 }}
                        />
                    </div>
                </motion.div>
                <motion.div 
                    className="row my-5 bg-light p-4 rounded"
                    initial={{ x: 100, opacity: 0 }}
                    animate={{ x: 0, opacity: 1 }}
                    transition={{ duration: 0.7, delay: 0.3 }}
                >
                    <div className="col-12 text-center">
                        <h2 className="mb-4 ">Our Mission</h2>
                        <p className="lead">
                            To inspire creativity in the kitchen, promote healthy eating, 
                            and connect food lovers from all walks of life.
                        </p>
                    </div>
                </motion.div>

                {/* Team Section */}
                <div className="row">
                    <div className="col-12 text-center mb-5">
                        <h2 className='text-light'>Meet Our Team</h2>
                    </div>
                    {teamMembers.map((member, index) => (
                        <motion.div 
                            key={member.name} 
                            className="col-md-4 mb-4"
                            initial={{ opacity: 0, scale: 0.9 }}
                            animate={{ opacity: 1, scale: 1 }}
                            transition={{ 
                                duration: 0.5, 
                                delay: index * 0.2 
                            }}
                        >
                            <div className="card h-100 shadow-sm">
                                <div className="card-body text-center">
                                    <img 
                                        src={member.image} 
                                        alt={member.name} 
                                        className="rounded-circle mb-3"
                                        style={{ width: '150px', height: '150px', objectFit: 'cover' }}
                                    />
                                    <h5 className="card-title">{member.name}</h5>
                                    <p className="card-text text-muted">{member.role}</p>
                                    <p className="card-text">{member.bio}</p>
                                </div>
                            </div>
                        </motion.div>
                    ))}
                </div>
            </motion.div>
        </div>
    );
};

export default AboutPage;