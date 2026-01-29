import React, { createContext, useState, useEffect } from "react";
import { useMutation, useLazyQuery } from "@apollo/client/react";
import { LOGIN, REGISTER, ME } from "../graphql/auth";
import type { AuthContextType, User, Role } from "../types/auth";

// GraphQL response types
interface LoginResponse {
  login: {
    token: string;
    user: User;
  };
}

interface RegisterResponse {
  register: {
    token: string;
    user: User;
  };
}

interface MeResponse {
  me: User;
}

/**
 * Auth Context - Global authentication state management
 *
 * Responsibilities:
 * 1. Store current user and JWT token
 * 2. Provide login/register/logout methods
 * 3. Persist authentication across page refreshes
 * 4. Initialize auth state on app load
 *
 * Why Context API?
 * - Avoids prop drilling (any component can access auth state)
 * - Single source of truth for authentication
 * - Simple for this use case (Redux overkill for V1)
 *
 * localStorage Strategy:
 * - Token stored in localStorage (persists across sessions)
 * - On app load, check for token → validate with backend → restore user
 * - On logout, clear localStorage
 *
 * Security Note:
 * - localStorage is vulnerable to XSS attacks
 * - Alternative: httpOnly cookies (more secure, but complex)
 * - For learning project, localStorage is acceptable
 * - In production, consider httpOnly cookies + CSRF protection
 */

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(true);

  // GraphQL mutations/queries
  const [loginMutation] = useMutation<LoginResponse>(LOGIN);
  const [registerMutation] = useMutation<RegisterResponse>(REGISTER);
  const [meQuery] = useLazyQuery<MeResponse>(ME);

  /**
   * Initialize authentication on app load
   *
   * Flow:
   * 1. Check if token exists in localStorage
   * 2. If yes, validate with backend (call "me" query)
   * 3. If valid, restore user state
   * 4. If invalid/expired, clear localStorage
   */
  useEffect(() => {
    const initAuth = async () => {
      const storedToken = localStorage.getItem("token");

      if (storedToken) {
        setToken(storedToken);
        try {
          // Validate token with backend and get user data
          const { data } = await meQuery();
          if (data?.me) {
            setUser(data.me);
          }
        } catch (error) {
          // Token invalid/expired - clear storage
          console.error("Token validation failed:", error);
          localStorage.removeItem("token");
          setToken(null);
        }
      }

      setLoading(false);
    };

    initAuth();
  }, [meQuery]);

  /**
   * Login function
   *
   * Flow:
   * 1. Call login mutation with credentials
   * 2. Store token in localStorage and state
   * 3. Store user in state
   * 4. Apollo Client will auto-add token to future requests
   *
   * Error handling:
   * - Backend returns error → caught by caller (Login component)
   * - Display error message to user
   */
  const login = async (username: string, password: string) => {
    const { data } = await loginMutation({
      variables: { username, password },
    });

    if (!data) {
      throw new Error("Login failed - no data returned");
    }

    const { token: newToken, user: newUser } = data.login;

    // Store token
    localStorage.setItem("token", newToken);
    setToken(newToken);
    setUser(newUser);
  };

  /**
   * Register function
   *
   * Similar to login, but calls register mutation.
   * Auto-logs in user after registration (returns token immediately).
   */
  const register = async (
    username: string,
    password: string,
    email: string,
    role: Role,
    teamId?: string,
  ) => {
    const { data } = await registerMutation({
      variables: {
        input: {
          username,
          password,
          email,
          role,
          teamId: teamId ? teamId : null,
        },
      },
    });

    if (!data) {
      throw new Error("Registration failed - no data returned");
    }

    const { token: newToken, user: newUser } = data.register;

    // Store token
    localStorage.setItem("token", newToken);
    setToken(newToken);
    setUser(newUser);
  };

  /**
   * Logout function
   *
   * Flow:
   * 1. Clear token from localStorage
   * 2. Clear state (user, token)
   * 3. Redirect to login (handled by caller)
   *
   * Note: JWT tokens are stateless (can't be revoked on backend)
   * Token remains valid until expiry even after logout
   * Mitigation: Short expiration times (15-30 min)
   */
  const logout = () => {
    localStorage.removeItem("token");
    setToken(null);
    setUser(null);
  };

  const value: AuthContextType = {
    user,
    token,
    loading,
    login,
    register,
    logout,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export { AuthContext };
