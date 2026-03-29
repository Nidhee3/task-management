import { Link } from "react-router-dom";

//any non existent page - error
const ErrorPage = () => {
  return (
    <div className="container text-center mt-5">
      <h1>404 - Page Not Found</h1>
      <p>The page you are looking for does not exist.</p>
      <Link to="/dashboard" className="btn btn-primary">
        Go to Dashboard
      </Link>
    </div>
  );
};

export default ErrorPage;