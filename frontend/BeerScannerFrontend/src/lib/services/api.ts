import axios from 'axios';
import { browser } from '$app/environment';

// Create an axios instance with default config
const api = axios.create({
  baseURL: 'http://localhost:8080',
  headers: {
    'Content-Type': 'application/json',
  },
  paramsSerializer: {
    serialize: (params) => {
      // Custom serializer to handle arrays without square brackets
      return Object.entries(params)
        .map(([key, value]) => {
          if (Array.isArray(value)) {
            // Convert array to comma-separated string for sort parameter
            return `${key}=${encodeURIComponent(value.join(','))}`;
          }
          return `${key}=${encodeURIComponent(String(value))}`;
        })
        .join('&');
    }
  }
});

// Add a request interceptor to add the auth token to requests
api.interceptors.request.use(
  (config) => {
    if (browser) {
      const token = localStorage.getItem('token');
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Add a response interceptor to handle errors
api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    // Handle 401 Unauthorized errors (token expired or invalid)
    if (error.response && error.response.status === 401) {
      if (browser) {
        localStorage.removeItem('token');
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);

export default api;
