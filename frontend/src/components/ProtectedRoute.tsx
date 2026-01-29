import { Navigate } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";
import type { Role } from "../types/auth";

/**
 * Protected Route Component
 *
 * Purpose: Wrapper for routes that require authentication
 *
 * Features:
 * - Redirects to login if user not authenticated
 * - Optional role-based access control
 * - Shows loading state during auth initialization
 *
 * Usage:
 * <Route path="/dashboard" element={
 *   <ProtectedRoute>
 *     <Dashboard />
 *   </ProtectedRoute>
 * } />
 *
 * <Route path="/admin" element={
 *   <ProtectedRoute requiredRole="ADMIN">
 *     <AdminPanel />
 *   </ProtectedRoute>
 * } />
 */

interface ProtectedRouteProps {
  children: React.ReactNode;
  requiredRole?: Role;
}

export default function ProtectedRoute({
  children,
  requiredRole,
}: ProtectedRouteProps) {
  const { user, loading } = useAuth();

  // Show loading state during auth initialization
  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-lg">Loading...</div>
      </div>
    );
  }

  // Not authenticated → redirect to login
  if (!user) {
    return <Navigate to="/login" replace />;
  }

  // Role check (if required)
  if (requiredRole && user.role !== requiredRole) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-red-500 text-xl">
          Access Denied - Insufficient Permissions
        </div>
      </div>
    );
  }

  // Authenticated and authorized → render children
  return <>{children}</>;
}
