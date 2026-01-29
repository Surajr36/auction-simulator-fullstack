import { gql } from "@apollo/client";

/**
 * Login Mutation
 *
 * Authenticates user with username/password.
 * Returns JWT token and user details.
 */
export const LOGIN = gql`
  mutation Login($username: String!, $password: String!) {
    login(username: $username, password: $password) {
      token
      user {
        id
        username
        email
        role
        team {
          id
          name
        }
      }
    }
  }
`;

/**
 * Register Mutation
 *
 * Creates new user account.
 * Returns JWT token immediately (auto-login after registration).
 */
export const REGISTER = gql`
  mutation Register($input: RegisterInput!) {
    register(input: $input) {
      token
      user {
        id
        username
        email
        role
        team {
          id
          name
        }
      }
    }
  }
`;

/**
 * Me Query
 *
 * Fetches current authenticated user's details.
 * Used on app initialization to restore login state.
 */
export const ME = gql`
  query Me {
    me {
      id
      username
      email
      role
      team {
        id
        name
      }
    }
  }
`;
