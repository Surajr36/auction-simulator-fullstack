import {
  ApolloClient,
  InMemoryCache,
  HttpLink,
  from,
} from "@apollo/client/core";
import { setContext } from "@apollo/client/link/context";

/**
 * HTTP Link - GraphQL endpoint connection
 */
const httpLink = new HttpLink({
  uri: "http://localhost:8080/graphql",
});

/**
 * Auth Link - Adds JWT token to every request
 *
 * How it works:
 * 1. Runs before every GraphQL request
 * 2. Retrieves JWT from localStorage
 * 3. Adds Authorization header if token exists
 *
 * Header format: "Authorization: Bearer <jwt>"
 *
 * Why setContext?
 * - Async operation (can fetch token from async storage)
 * - Runs dynamically for each request (gets latest token)
 * - Chainable with other links
 */
const authLink = setContext((_, { headers }) => {
  // Get token from localStorage
  const token = localStorage.getItem("token");

  // Return headers with token (if exists)
  return {
    headers: {
      ...headers,
      authorization: token ? `Bearer ${token}` : "",
    },
  };
});

/**
 * Apollo Client Instance
 *
 * Link chain: authLink → httpLink
 * 1. authLink adds Authorization header
 * 2. httpLink sends request to backend
 */
export const client = new ApolloClient({
  link: from([authLink, httpLink]),
  cache: new InMemoryCache(),
});
