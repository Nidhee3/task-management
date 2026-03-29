import React, { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";

//checks if logged in or not and their role
const Navbar = () => {
  const navigate = useNavigate();
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [isAdmin, setIsAdmin] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem("token");
    const role = localStorage.getItem("role");
    if (token) {
      setIsLoggedIn(true);
      setIsAdmin(role === "ADMIN");
    } else {
      setIsLoggedIn(false);
      setIsAdmin(false);
    }
  }, [isLoggedIn]);

//clears all when user logs out
  const onLogoutHandler = () => {
    localStorage.clear();
    navigate("/login");
  };
  return (
    //navbar
    <nav
      className="navbar navbar-expand-lg navbar-dark"
      style={{ backgroundColor: "#0d4d4d", padding: "0.6rem 1.5rem" }}
    >
      <Link
        className="navbar-brand"
        to="/"
        style={{
          fontFamily: "Times New Roman",
          fontSize: "1.6rem",
          fontWeight: "700",
          color: "#e4c893",
          letterSpacing: "1px",
        }}
      >
        TeamFlow
      </Link>

      
      <button
        className="navbar-toggler"
        type="button"
        data-bs-toggle="collapse"
        data-bs-target="#navbarContent"
        aria-controls="navbarContent"
        aria-expanded="false"
        aria-label="Toggle navigation"
      >
        <span className="navbar-toggler-icon"></span>
      </button>

      <div className="collapse navbar-collapse" id="navbarContent">
        <ul className="navbar-nav me-auto mb-2 mb-lg-0">
          {isLoggedIn && (
            <>
              <li className="nav-item">
                <Link className="nav-link" to="/dashboard">
                  Dashboard
                </Link>
              </li>
              <li className="nav-item">
                <Link className="nav-link" to="/tasks/new">
                  + New Task
                </Link>
              </li>
              {isAdmin && (
                <li className="nav-item">
                  <Link className="nav-link" to="/users">
                    User Management
                  </Link>
                </li>
              )}
            </>
          )}
        </ul>

        
        <div className="ms-auto">
          {!isLoggedIn ? (
            <Link
              to="/login"
              className="btn"
              style={{
                backgroundColor: "#C8A96E",
                borderColor: "#C8A96E",
                color: "#fff",
                fontWeight: "600",
              }}
            >
              Login
            </Link>
          ) : (
            <button
              className="btn"
              onClick={onLogoutHandler}
              style={{
                backgroundColor: "transparent",
                borderColor: "#C8A96E",
                color: "#C8A96E",
                fontWeight: "600",
              }}
            >
              Logout
            </button>
          )}
        </div>
      </div>
    </nav>
  );
};

export default Navbar;