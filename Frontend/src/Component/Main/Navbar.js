import React from 'react'
import { Link } from 'react-router-dom'
const Navbar = () => {
  return (
    <div>
                  <nav className="fixed-top navbar  navbar-expand-lg navbar-light bg-light bg-opacity-75 shadow-sm">
                <div className="container-fluid px-4 p-3">
                    <Link className="navbar-brand" to="/">Taste it Out</Link>
                    <button
                        className="navbar-toggler"
                        type="button"
                        data-bs-toggle="collapse"
                        data-bs-target="#navbarNav"
                        aria-controls="navbarNav"
                        aria-expanded="false"
                        aria-label="Toggle navigation"
                    >
                        <span className="navbar-toggler-icon"></span>
                    </button>
                    <div className="collapse navbar-collapse" id="navbarNav">
                        <ul className="navbar-nav ms-auto">
                            <li className="nav-item">
                                <Link className="nav-link" to="/">Home</Link>
                            </li>
                            <li className="nav-item">
                                <Link className="nav-link" to="/welcome/about">About</Link>
                            </li>
                            <li className="nav-item">
                                <Link className="nav-link" to="/welcome/contact">Contact</Link>
                            </li>
                            <li className="nav-item">
                                <Link className="nav-link" to="/login">Login</Link>
                            </li>
                            <li className="nav-item">
                                <Link className="nav-link" to="/signup">Sign Up</Link>
                            </li>
                        </ul>
                    </div>
                </div>
            </nav>
    </div>
  )
}

export default Navbar
