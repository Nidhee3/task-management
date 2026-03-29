import React, { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import Navbar from "../../components/Navbar";
import api from "../../api/axios";

const DashboardPage = () => {
  const navigate = useNavigate();
  const [tasks, setTasks] = useState([]);
  const [users, setUsers] = useState([]);
  const [filters, setFilters] = useState({
    status: "",
    assignedTo: "",
    priority: "",
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const isAdmin = localStorage.getItem("role") === "ADMIN";

  //checking login 
  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) {
      navigate("/login");
    }
  }, [navigate]);

//fetch task 
  useEffect(() => {
    fetchTasks();
  }, [filters]);

//only admin can see users list
  useEffect(() => {
    if (isAdmin) {
      fetchUsers();
    }
  }, [isAdmin]);

  const fetchTasks = () => {
    setLoading(true);
    const params = new URLSearchParams();
    if (filters.status) params.append("status", filters.status);
    if (filters.assignedTo) params.append("assignedTo", filters.assignedTo);
    if (filters.priority) params.append("priority", filters.priority);

    api
      .get(`/api/tasks?${params.toString()}`)
      .then((response) => {
        setTasks(response.data);
        setLoading(false);
      })
      .catch((error) => {
        if (error.response?.status === 401) {
          navigate("/login");
        } else {
          setError("Failed to load tasks. Please try again.");
          setLoading(false);
        }
      });
  };

  const fetchUsers = () => {
    api
      .get("/api/users")
      .then((response) => setUsers(response.data))
      .catch(() => setUsers([]));
  };

  //delete task with warning
  const handleDelete = (taskId) => {
    if (window.confirm("Are you sure you want to delete this task?")) {
      api
        .delete(`/api/tasks/${taskId}`)
        .then(() => setTasks(tasks.filter((t) => t.id !== taskId)))
        .catch(() => alert("Failed to delete task."));
    }
  };

  const getPriorityClass = (priority) => {
    if (priority === "HIGH") return "priority-high";
    if (priority === "MEDIUM") return "priority-medium";
    if (priority === "LOW") return "priority-low";
    return "";
  };

  const getStatusClass = (status) => {
    if (status === "TODO") return "status-todo";
    if (status === "IN_PROGRESS") return "status-in-progress";
    if (status === "DONE") return "status-done";
    return "";
  };

  const formatStatus = (status) => {
    if (status === "TODO") return "To Do";
    if (status === "IN_PROGRESS") return "In Progress";
    if (status === "DONE") return "Done";
    return status;
  };

  return (
    <>
      <Navbar />

      <div className="container mt-4">

        <div
          className="d-flex justify-content-between align-items-center mb-4"
          style={{ flexWrap: "wrap", gap: "12px" }}
        >
          <h2 style={{ fontFamily: "'Times New Roman', serif", fontSize: "2rem", margin: 0 }}>
            Task Dashboard
          </h2>
          <Link to="/tasks/new" className="btn btn-success">
            + Create New Task
          </Link>
        </div>

        <div
          className="wrapper mb-4 mx-auto"
          style={{ maxWidth: "860px" }}
        >
          <h5 style={{ fontFamily: "'Times New Roman', serif", marginBottom: "16px" }}>
            Filter Tasks
          </h5>

          <div className="row g-3 justify-content-center">
            <div className="col-md-4">
              <label className="form-label">Status</label>
              <select
                className="form-control"
                value={filters.status}
                onChange={(e) => setFilters({ ...filters, status: e.target.value })}
              >
                <option value="">All Statuses</option>
                <option value="TODO">To Do</option>
                <option value="IN_PROGRESS">In Progress</option>
                <option value="DONE">Done</option>
              </select>
            </div>

            <div className="col-md-4">
              <label className="form-label">Priority</label>
              <select
                className="form-control"
                value={filters.priority}
                onChange={(e) => setFilters({ ...filters, priority: e.target.value })}
              >
                <option value="">All Priorities</option>
                <option value="HIGH">High</option>
                <option value="MEDIUM">Medium</option>
                <option value="LOW">Low</option>
              </select>
            </div>

            {isAdmin && (
              <div className="col-md-4">
                <label className="form-label">Assigned To</label>
                <select
                  className="form-control"
                  value={filters.assignedTo}
                  onChange={(e) => setFilters({ ...filters, assignedTo: e.target.value })}
                >
                  <option value="">All Users</option>
                  {users.map((user) => (
                    <option key={user.id} value={user.id}>
                      {user.name}
                    </option>
                  ))}
                </select>
              </div>
            )}
          </div>

          <div className="text-center mt-3">
            <button
              className="btn btn-secondary btn-sm"
              onClick={() => setFilters({ status: "", assignedTo: "", priority: "" })}
            >
              Clear Filters
            </button>
          </div>
        </div>

        {loading && (
          <div className="text-center py-5">
            <div
              className="spinner-border"
              role="status"
              style={{ color: "#2D6A6A" }}
            ></div>
            <p className="mt-2" style={{ color: "#2D6A6A" }}>Loading tasks...</p>
          </div>
        )}

        {error && (
          <div className="alert alert-danger text-center">{error}</div>
        )}

        {!loading && !error && tasks.length === 0 && (
          <div className="text-center wrapper" style={{ maxWidth: "500px", margin: "40px auto" }}>
            <h5 style={{ fontFamily: "'Times New Roman', serif" }}>No tasks found</h5>
            <p className="text-muted">Try adjusting your filters or create a new task.</p>
            <Link to="/tasks/new" className="btn btn-primary">
              Create First Task
            </Link>
          </div>
        )}


        {!loading && !error && tasks.length > 0 && (
          <div className="row g-4">
            {tasks.map((task) => (
              <div className="col-md-4" key={task.id}>
                <div
                  className="card h-100"
                  style={{
                    borderRadius: "12px",
                    border: "1px solid #e8ddd0",
                    boxShadow: "0 2px 12px rgba(0,0,0,0.07)",
                  }}
                >
                  <div className="card-body" style={{ padding: "20px" }}>
                    <h5
                      className="card-title"
                      style={{
                        fontFamily: "'Times New Roman', serif",
                        fontSize: "1.2rem",
                        marginBottom: "10px",
                      }}
                    >
                      {task.title}
                    </h5>

                    <div className="mb-2" style={{ display: "flex", gap: "6px", flexWrap: "wrap" }}>
                      <span className={getStatusClass(task.status)}>
                        {formatStatus(task.status)}
                      </span>
                      <span className={getPriorityClass(task.priority)}>
                        {task.priority}
                      </span>
                    </div>

                    {task.description && (
                      <p
                        className="card-text"
                        style={{ fontSize: "0.875rem", color: "#666", marginBottom: "12px" }}
                      >
                        {task.description.length > 80
                          ? task.description.substring(0, 80) + "..."
                          : task.description}
                      </p>
                    )}

                    <p style={{ fontSize: "0.8rem", color: "#888", margin: "4px 0" }}>
                      Created by: {task.createdByName}
                    </p>
                    {task.assignedToName && (
                      <p style={{ fontSize: "0.8rem", color: "#888", margin: "4px 0" }}>
                        Assigned to: {task.assignedToName}
                      </p>
                    )}
                  </div>

                  <div
                    className="card-footer"
                    style={{
                      backgroundColor: "#faf7f2",
                      borderTop: "1px solid #e8ddd0",
                      padding: "12px 20px",
                      display: "flex",
                      gap: "8px",
                      borderRadius: "0 0 12px 12px",
                    }}
                  >
                    <Link
                      to={`/tasks/edit/${task.id}`}
                      className="btn btn-primary btn-sm"
                    >
                      Edit
                    </Link>
                    {isAdmin && (
                      <button
                        className="btn btn-danger btn-sm"
                        onClick={() => handleDelete(task.id)}
                      >
                        Delete
                      </button>
                    )}
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </>
  );
};

export default DashboardPage;