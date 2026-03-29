import React, { useState } from "react";
import { useFormik } from "formik";
import * as Yup from "yup";
import { Link, useNavigate } from "react-router-dom";
import styles from "./style.module.css";
import api from "../../api/axios";

const RegisterPage = () => {
  const navigate = useNavigate();

  const [requestResponse, setRequestResponse] = useState({
    message: "",
    alertClass: "",
  });

  const initialValues = {
    name: "",
    email: "",
    password: "",
  };

  const validationSchema = Yup.object({
    name: Yup.string().required("Name is required"),
    email: Yup.string()
      .email("Invalid email address")
      .required("Email is required"),
    password: Yup.string()
      .min(6, "Password must be at least 6 characters")
      .required("Password is required"),
  });

  const onSubmit = (values) => {
    api
      .post("/api/auth/register", values)
      .then(() => {
        setRequestResponse({
          message: "Registration successful! Redirecting to login...",
          alertClass: "alert alert-success",
        });
        setTimeout(() => navigate("/login"), 1500);
      })
      .catch((error) => {
        const errorMsg =
          error.response?.data?.message || "Registration failed. Please try again.";
        setRequestResponse({
          message: errorMsg,
          alertClass: "alert alert-danger",
        });
      });
  };

  const formik = useFormik({
    initialValues,
    onSubmit,
    validationSchema,
    validateOnMount: true,
  });

  return (
    <div className="container">
      <div className={styles.wrapper}>

        
        <div className={styles.siteName}>TaskFlow</div>
        <div className={styles.tagline}>Your task manager</div>
        <hr className={styles.divider} />

        {requestResponse.message && (
          <div className={requestResponse.alertClass}>
            {requestResponse.message}
          </div>
        )}

        <h2 style={{ fontFamily: "'Cormorant Garamond', serif" }}>Register</h2>
        <hr />

        <form onSubmit={formik.handleSubmit}>
          <div className="form-group mb-3">
            <label>Full Name</label>
            <input
              type="text"
              className={
                formik.errors.name && formik.touched.name
                  ? "form-control is-invalid"
                  : "form-control"
              }
              name="name"
              value={formik.values.name}
              onChange={formik.handleChange}
              onBlur={formik.handleBlur}
            />
            {formik.touched.name && formik.errors.name ? (
              <span className="text-danger">{formik.errors.name}</span>
            ) : null}
          </div>

          <div className="form-group mb-3">
            <label>Email</label>
            <input
              type="email"
              className={
                formik.errors.email && formik.touched.email
                  ? "form-control is-invalid"
                  : "form-control"
              }
              name="email"
              value={formik.values.email}
              onChange={formik.handleChange}
              onBlur={formik.handleBlur}
            />
            {formik.touched.email && formik.errors.email ? (
              <span className="text-danger">{formik.errors.email}</span>
            ) : null}
          </div>

          <div className="form-group mb-3">
            <label>Password</label>
            <input
              type="password"
              className={
                formik.errors.password && formik.touched.password
                  ? "form-control is-invalid"
                  : "form-control"
              }
              name="password"
              value={formik.values.password}
              onChange={formik.handleChange}
              onBlur={formik.handleBlur}
            />
            {formik.touched.password && formik.errors.password ? (
              <span className="text-danger">{formik.errors.password}</span>
            ) : null}
          </div>

          <input
            type="submit"
            value="Register"
            className="btn btn-primary w-100 mt-2"
            disabled={!formik.isValid}
          />
        </form>

        <br />
        <p className="text-center">
          Already have an account? <Link to="/login">Login here</Link>
        </p>
      </div>
    </div>
  );
};

export default RegisterPage;