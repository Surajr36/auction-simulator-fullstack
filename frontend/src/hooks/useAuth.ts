import { useContext } from "react";
import { AuthContext } from "../contexts/AuthContext";

/**
 * useAuth Hook - Access auth context from any component
 *
 * Usage:
 * const { user, login, logout } = useAuth();
 *
 * Throws error if used outside AuthProvider (prevents bugs).
 */
export function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
}
