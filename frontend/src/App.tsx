import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider } from "./contexts/AuthContext";
import ProtectedRoute from "./components/ProtectedRoute";
import Navbar from "./components/Navbar";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import AuctionDashboard from "./components/AuctionDashboard";

/**
 * Main App Component
 *
 * Structure:
 * 1. AuthProvider - Wraps entire app (provides auth context)
 * 2. BrowserRouter - Enables routing
 * 3. Routes:
 *    - /login (public)
 *    - /register (public)
 *    - / (protected - dashboard)
 *
 * Navbar shown only on protected routes (when user authenticated).
 */

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          {/* Public Routes */}
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />

          {/* Protected Routes */}
          <Route
            path="/"
            element={
              <ProtectedRoute>
                <>
                  <Navbar />
                  <AuctionDashboard />
                </>
              </ProtectedRoute>
            }
          />

          {/* Catch-all: Redirect to home */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
