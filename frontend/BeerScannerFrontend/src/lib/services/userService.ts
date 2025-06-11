import api from './api';
import type { User, Bar, Beer } from '$lib/types';

// Get the current user
export const getCurrentUser = async (): Promise<User> => {
  const response = await api.get('/api/v1/users/me');
  return response.data;
};

// Get tracked bars for the current user
export const getTrackedBars = async (): Promise<Bar[]> => {
  const response = await api.get('/api/v1/users/me/tracked-bars');
  return response.data;
};

// Get tracked beers for the current user
export const getTrackedBeers = async (): Promise<Beer[]> => {
  const response = await api.get('/api/v1/users/me/tracked-beers');
  return response.data;
};

// Update notification settings
export const updateNotificationSettings = async (settings: Record<string, boolean>): Promise<User> => {
  const response = await api.put('/api/v1/users/me/notification-settings', settings);
  return response.data;
};
