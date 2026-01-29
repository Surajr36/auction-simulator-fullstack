// Authentication-related TypeScript types

/**
 * User role constants (using const object instead of enum for erasableSyntaxOnly)
 */
export const Role = {
  ADMIN: "ADMIN",
  TEAM_USER: "TEAM_USER",
} as const;

export type Role = (typeof Role)[keyof typeof Role];

export interface User {
  id: string;
  username: string;
  email: string;
  role: Role;
  team?: {
    id: string;
    name: string;
  };
}

export interface AuthPayload {
  token: string;
  user: User;
}

export interface AuthContextType {
  user: User | null;
  token: string | null;
  loading: boolean;
  login: (username: string, password: string) => Promise<void>;
  register: (
    username: string,
    password: string,
    email: string,
    role: Role,
    teamId?: string,
  ) => Promise<void>;
  logout: () => void;
}
