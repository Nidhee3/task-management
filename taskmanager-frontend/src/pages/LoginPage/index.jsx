import React, { useState } from "react";
import { Formik, Form, Field, ErrorMessage } from "formik";
import * as Yup from "yup";
import { Link, useNavigate } from "react-router-dom";
import styles from "./style.module.css";
import api from "../../api/axios";

const LoginPage = () => {
  const navigate = useNavigate();

  const [requestResponse, setRequestResponse] = useState({
    message: "",
    className: "",
  });

  const initialValues = {
    email: "",
    password: "",
  };

  const onSubmit = (values) => {
    api
      .post("/api/auth/login", values)
      .then((response) => {
        const token = response.data.accessToken;
        const role = response.data.role;

        localStorage.setItem("token", token);
        localStorage.setItem("role", role);

        setRequestResponse({
          message: "Login successful! Redirecting...",
          className: "alert alert-success",
        });

        setTimeout(() => navigate("/dashboard"), 1000);
      })
      .catch(() => {
        setRequestResponse({
          message: "Login failed. Please check your email and password.",
          className: "alert alert-danger",
        });
      });
  };

  const validateSchema = Yup.object({
    email: Yup.string()
      .email("Invalid email format")
      .required("Email is required"),
    password: Yup.string()
      .min(6, "Password must be at least 6 characters")
      .required("Password is required"),
  });

  return (
    <div className="container">
      <div className={styles.wrapper}>

        
        <div className={styles.siteName}>TaskFlow</div>
        <div className={styles.tagline}>Your task manager</div>
        <hr className={styles.divider} />

        
        {requestResponse.message && (
          <div className={requestResponse.className} role="alert">
            {requestResponse.message}
          </div>
        )}

        <h2 style={{ fontFamily: "'Cormorant Garamond', serif" }}>Login</h2>
        <hr />

        <Formik
          initialValues={initialValues}
          validationSchema={validateSchema}
          onSubmit={onSubmit}
          validateOnMount
        >
          {(formik) => (
            <Form>
              <div className="form-group mb-3">
                <label htmlFor="email">Email</label>
                <Field
                  type="email"
                  name="email"
                  id="email"
                  className={
                    formik.touched.email && formik.errors.email
                      ? "form-control is-invalid"
                      : "form-control"
                  }
                />
                <ErrorMessage name="email">
                  {(errorMessage) => (
                    <span className="text-danger">{errorMessage}</span>
                  )}
                </ErrorMessage>
              </div>

              <div className="form-group mb-3">
                <label htmlFor="password">Password</label>
                <Field
                  type="password"
                  name="password"
                  id="password"
                  className={
                    formik.touched.password && formik.errors.password
                      ? "form-control is-invalid"
                      : "form-control"
                  }
                />
                <ErrorMessage name="password">
                  {(errorMessage) => (
                    <span className="text-danger">{errorMessage}</span>
                  )}
                </ErrorMessage>
              </div>

              <input
                type="submit"
                value="Login"
                disabled={!formik.isValid}
                className="btn btn-primary w-100 mt-2"
              />
            </Form>
          )}
        </Formik>

        <br />
        <p className="text-center">
          Don't have an account? <Link to="/register">Register here</Link>
        </p>
      </div>
    </div>
  );
};

export default LoginPage;