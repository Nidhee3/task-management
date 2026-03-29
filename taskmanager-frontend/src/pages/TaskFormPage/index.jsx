import React, { useState, useEffect } from "react";
import { useFormik } from "formik";
import * as Yup from "yup";
import { useNavigate, useParams } from "react-router-dom";
import Navbar from "../../components/Navbar";
import api from "../../api/axios";

const TaskFormPage = () => {
  const navigate = useNavigate();
  const { id } = useParams();
  const isEditMode = !!id;

  const isAdmin = localStorage.getItem("role") === "ADMIN";

  // For admin dropdown
  const [users, setUsers] = useState([]);

  // fetch the assined person name to show it to users in assigned to part in  the edit form
  const [assignedToName, setAssignedToName] = useState("");

  const [requestResponse, setRequestResponse] = useState({
    message: "",
    alertClass: "",
  });

  const [pageLoading, setPageLoading] = useState(isEditMode);

  // Redirect to login if not logged in
  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) navigate("/login");
  }, [navigate]);

  // Only admin fetches the full user list for the dropdown
  useEffect(() => {
    if (isAdmin) {
      api
        .get("/api/users")
        .then((response) => setUsers(response.data))
        .catch(() => setUsers([]));
    }
  }, [isAdmin]);

  // In edit mode — fetch the existing task and pre-fill the form
  useEffect(() => {
    if (isEditMode) {
      api
        .get(`/api/tasks/${id}`)
        .then((response) => {
          const task = response.data;

          // users can see who the task is currently assigned to
          setAssignedToName(task.assignedToName || "Unassigned");

          formik.setValues({
            title: task.title || "",
            description: task.description || "",
            status: task.status || "",
            priority: task.priority || "",
            assignedToId: task.assignedToId ? String(task.assignedToId) : "",
          });

          setPageLoading(false);
        })
        .catch(() => {
          setRequestResponse({
            message: "Failed to load task data.",
            alertClass: "alert alert-danger",
          });
          setPageLoading(false);
        });
    }
  }, [id, isEditMode]);

  
  // Status and Priority compulsory 
  const validationSchema = Yup.object({
    title: Yup.string().required("Title is required"),

    //users should choose, no defaults
    status: Yup.string()
      .oneOf(["TODO", "IN_PROGRESS", "DONE"], "Please select a valid status")
      .required("Status is required — please select one"),

    priority: Yup.string()
      .oneOf(["HIGH", "MEDIUM", "LOW"], "Please select a valid priority")
      .required("Priority is required — please select one"),
  });

  const onSubmit = (values) => {
    const taskData = {
      title: values.title,
      description: values.description,
      status: values.status,
      priority: values.priority,
      
      assignedToId:
        isAdmin && values.assignedToId ? Number(values.assignedToId) : null,
    };

    const request = isEditMode
      ? api.put(`/api/tasks/${id}`, taskData)
      : api.post("/api/tasks", taskData);

    request
      .then(() => {
        setRequestResponse({
          message: isEditMode
            ? "Task updated successfully!"
            : "Task created successfully!",
          alertClass: "alert alert-success",
        });
        setTimeout(() => navigate("/dashboard"), 1500);
      })
      .catch((error) => {
        const errorMsg =
          error.response?.data?.message || "Failed to save task.";
        setRequestResponse({
          message: errorMsg,
          alertClass: "alert alert-danger",
        });
      });
  };
//form for edit and create
  const formik = useFormik({
    initialValues: {
      title: "",
      description: "",
      status: "",
      priority: "",
      assignedToId: "",
    },
    onSubmit,
    validationSchema,
    validateOnMount: true,
    enableReinitialize: true,
  });

  if (pageLoading) {
    return (
      <>
        <Navbar />
        <div className="container text-center mt-5">
          <div
            className="spinner-border"
            style={{ color: "#2D6A6A" }}
          ></div>
          <p>Loading task...</p>
        </div>
      </>
    );
  }

  return (
    <>
      <Navbar />
      <div className="container">
        <div
          className="wrapper"
          style={{ maxWidth: "640px", margin: "50px auto" }}
        >
          {requestResponse.message && (
            <div className={requestResponse.alertClass}>
              {requestResponse.message}
            </div>
          )}

          <h2 style={{ fontFamily: "'Cormorant Garamond', serif" }}>
            {isEditMode ? "Edit Task" : "Create New Task"}
          </h2>
          <hr />

          <form onSubmit={formik.handleSubmit}>

            {/* Title */}
            <div className="form-group mb-3">
              <label>
                Title <span style={{ color: "#B5373A" }}>*</span>
              </label>
              <input
                type="text"
                className={
                  formik.errors.title && formik.touched.title
                    ? "form-control is-invalid"
                    : "form-control"
                }
                name="title"
                value={formik.values.title}
                onChange={formik.handleChange}
                onBlur={formik.handleBlur}
                placeholder="Enter task title"
              />
              {formik.touched.title && formik.errors.title ? (
                <span className="text-danger" style={{ fontSize: "0.85rem" }}>
                  {formik.errors.title}
                </span>
              ) : null}
            </div>

            {/* Description */}
            <div className="form-group mb-3">
              <label>Description</label>
              <textarea
                className="form-control"
                name="description"
                rows="3"
                value={formik.values.description}
                onChange={formik.handleChange}
                onBlur={formik.handleBlur}
                placeholder="Optional — describe the task"
              ></textarea>
            </div>

            {/* Status */}
            <div className="form-group mb-3">
              <label>
                Status <span style={{ color: "#B5373A" }}>*</span>
              </label>
              <select
                className={
                  formik.errors.status && formik.touched.status
                    ? "form-control is-invalid"
                    : "form-control"
                }
                name="status"
                value={formik.values.status}
                onChange={formik.handleChange}
                onBlur={formik.handleBlur}
              >
                <option value="">-- Select Status --</option>
                <option value="TODO">To Do</option>
                <option value="IN_PROGRESS">In Progress</option>
                <option value="DONE">Done</option>
              </select>
              {formik.touched.status && formik.errors.status ? (
                <span className="text-danger" style={{ fontSize: "0.85rem" }}>
                  {formik.errors.status}
                </span>
              ) : null}
            </div>

            {/* Priority */}
            <div className="form-group mb-3">
              <label>
                Priority <span style={{ color: "#B5373A" }}>*</span>
              </label>
              <select
                className={
                  formik.errors.priority && formik.touched.priority
                    ? "form-control is-invalid"
                    : "form-control"
                }
                name="priority"
                value={formik.values.priority}
                onChange={formik.handleChange}
                onBlur={formik.handleBlur}
              >
                <option value="">-- Select Priority --</option>
                <option value="HIGH">High</option>
                <option value="MEDIUM">Medium</option>
                <option value="LOW">Low</option>
              </select>
              {formik.touched.priority && formik.errors.priority ? (
                <span className="text-danger" style={{ fontSize: "0.85rem" }}>
                  {formik.errors.priority}
                </span>
              ) : null}
            </div>

            {/* Assign To */}
            <div className="form-group mb-4">
              <label>Assign To</label>

              {isAdmin ? (
                // Admin gets the full dropdown to reassign
                <>
                  <select
                    className="form-control"
                    name="assignedToId"
                    value={formik.values.assignedToId}
                    onChange={formik.handleChange}
                    onBlur={formik.handleBlur}
                  >
                    <option value="">— Select a user —</option>
                    {users.map((user) => (
                      <option key={user.id} value={user.id}>
                        {user.name} ({user.email})
                      </option>
                    ))}
                  </select>
                  <small className="text-muted">
                    As admin, you can assign this task to any user.
                  </small>
                </>
              ) : (
                // for user, its read only
                <>
                  <div
                    className="form-control"
                    style={{
                      backgroundColor: "#f8f9fa",
                      color: "#495057",
                      cursor: "default",
                    }}
                  >
                    {isEditMode
                      ? // edit mode: show the actual assigned person from the task
                        assignedToName
                      : // create mode: task will be assigned to themselves
                        "Yourself (auto-assigned)"}
                  </div>
                  <small className="text-muted">
                    {isEditMode
                      ? "Assignment is managed by admin."
                      : "Tasks you create are assigned to you. An admin can reassign if needed."}
                  </small>
                </>
              )}
            </div>

            <div className="d-flex gap-2">
              <input
                type="submit"
                value={isEditMode ? "Update Task" : "Create Task"}
                className="btn btn-primary"
                disabled={!formik.isValid}
              />
              <button
                type="button"
                className="btn btn-secondary"
                onClick={() => navigate("/dashboard")}
              >
                Cancel
              </button>
            </div>
          </form>
        </div>
      </div>
    </>
  );
};

export default TaskFormPage;