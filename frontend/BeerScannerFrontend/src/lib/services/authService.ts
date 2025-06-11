import { jwtDecode } from 'jwt-decode';
import { browser } from '$app/environment';
import type { User } from '$lib/types';

// Interface for the decoded JWT token
interface DecodedToken {
  sub: string;
  email: string;
  name: string;
  picture?: string;
  exp: number;
  iat: number;
}

// Check if the user is authenticated
export const isAuthenticated = (): boolean => {
  if (!browser) return false;

  const token = localStorage.getItem('token');
  if (!token) return false;

  try {
    const decoded = jwtDecode<DecodedToken>(token);
    const currentTime = Date.now() / 1000;

    // Check if token is expired
    if (decoded.exp < currentTime) {
      localStorage.removeItem('token');
      return false;
    }

    return true;
  } catch (error) {
    localStorage.removeItem('token');
    return false;
  }
};

// Get the current user from the token
export const getUserFromToken = (): Partial<User> | null => {
  if (!browser) return null;

  const token = localStorage.getItem('token');
  if (!token) return null;

  try {
    const decoded = jwtDecode<DecodedToken>(token);
    return {
      email: decoded.email,
      name: decoded.name,
      googleId: decoded.sub,
      profilePicture: decoded.picture
    };
  } catch (error) {
    return null;
  }
};

// Set the authentication token and update the currentUser store
export const setToken = (token: string): void => {
  if (browser) {
    localStorage.setItem('token', token);

    // Also update the currentUser store by re-initializing it
    const event = new Event('storage');
    window.dispatchEvent(event);
  }
};

// Remove the authentication token (logout)
export const logout = (): void => {
  if (browser) {
    localStorage.removeItem('token');
    window.location.href = '/';
  }
};

// Handle Google OAuth callback
export const handleGoogleCallback = (token: string): void => {
  setToken(token);
};
