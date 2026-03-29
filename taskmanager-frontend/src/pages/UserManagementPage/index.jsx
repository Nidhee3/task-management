import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "../../components/Navbar";
import api from "../../api/axios";

const UserManagementPage = () => {
  const navigate = useNavigate();
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const token = localStorage.getItem("token");
    const role = localStorage.getItem("role");

    if (!token) {
      navigate("/login");
      return;
    }
    if (role !== "ADMIN") {
      navigate("/dashboard");
      return;
    }
    fetchUsers();
  }, [navigate]);

  const fetchUsers = () => {
    api
      .get("/api/users")
      .then((response) => {
        setUsers(response.data);
        setLoading(false);
      })
      .catch(() => {
        setError("Failed to load users.");
        setLoading(false);
      });
  };

  return (
    <>
      <Navbar />
      <div className="container mt-4">
        <h2 style={{ fontFamily: "'Cormorant Garamond', serif", fontSize: "2rem" }}>
          User Management
        </h2>
        <p className="text-muted" style={{ fontSize: "0.9rem" }}>
          Admin view — all registered users
        </p>

        {loading && (
          <div className="text-center py-4">
            <div className="spinner-border" style={{ color: "#2D6A6A" }}></div>
            <p>Loading users...</p>
          </div>
        )}

        {error && <div className="alert alert-danger">{error}</div>}

        {!loading && !error && users.length === 0 && (
          <div className="alert alert-info">No users found.</div>
        )}

        {!loading && !error && users.length > 0 && (
          <div className="wrapper">
            <table className="table table-hover" style={{ fontSize: "0.9rem" }}>
              <thead style={{ backgroundColor: "#2D6A6A", color: "#fff" }}>
                <tr>
                  <th>#</th>
                  <th>Name</th>
                  <th>Email</th>
                  <th>Role</th>
                  <th>Registered</th>
                </tr>
              </thead>
              <tbody>
                {users.map((user, index) => (
                  <tr key={user.id}>
                    <td>{index + 1}</td>
                    <td style={{ fontWeight: "500" }}>{user.name}</td>
                    <td>{user.email}</td>
                    <td>
                      <span
                        style={{
                          backgroundColor: user.role === "ADMIN" ? "#B5373A" : "#6c757d",
                          color: "white",
                          padding: "3px 10px",
                          borderRadius: "20px",
                          fontSize: "11px",
                          fontWeight: "700",
                          textTransform: "uppercase",
                          letterSpacing: "0.5px",
                        }}
                      >
                        {user.role}
                      </span>
                    </td>
                    <td>
                      {user.createdAt
                        ? new Date(user.createdAt).toLocaleDateString()
                        : "N/A"}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </>
  );
};

export default UserManagementPage;