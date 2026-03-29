import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import DashboardPage from "./pages/DashboardPage";
import TaskFormPage from "./pages/TaskFormPage";
import UserManagementPage from "./pages/UserManagementPage";
import ErrorPage from "./pages/ErrorPage";

//all pages routes
function App() {
  return (
    <Router>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/" element={<DashboardPage />} />
        <Route path="/dashboard" element={<DashboardPage />} />
        <Route path="/tasks/new" element={<TaskFormPage />} />
        <Route path="/tasks/edit/:id" element={<TaskFormPage />} />
        <Route path="/users" element={<UserManagementPage />} />
        <Route path="*" element={<ErrorPage />} />
      </Routes>
    </Router>
  );
}

export default App;