import api from './api';
import { trackUserResource, untrackUserResource } from './serviceHelpers';
import type {
  Bar,
  PageBar,
  Beer,
  BarCheck,
  PageBarCheck,
  Pageable,
  User,
  BarAdmin,
  PageBarAdmin,
  BeerAvailability
} from '$lib/types';

// Get all bars with pagination
export const getAllBars = async (pageable: Pageable): Promise<PageBar> => {
  const response = await api.get('/api/v1/bars/public', { params: pageable });
  return response.data;
};

// Get a bar by ID
export const getBarById = async (id: number): Promise<Bar> => {
  const response = await api.get(`/api/v1/bars/public/${id}`);
  return response.data;
};

// Get current beers for a bar
export const getCurrentBeers = async (barId: number): Promise<BeerAvailability[]> => {
  const response = await api.get(`/api/v1/bars/public/${barId}/current-beers`);
  return response.data;
};

// Get past beers for a bar
export const getPastBeers = async (barId: number): Promise<BeerAvailability[]> => {
  const response = await api.get(`/api/v1/bars/public/${barId}/past-beers`);
  return response.data;
};

// Search bars
export const searchBars = async (searchTerm: string, pageable: Pageable): Promise<PageBar> => {
  const response = await api.get('/api/v1/bars/public/search', { 
    params: { 
      searchTerm,
      ...pageable
    } 
  });
  return response.data;
};

// Get user's requested bars
export const getMyRequestedBars = async (): Promise<Bar[]> => {
  const response = await api.get('/api/v1/bars/my-requests');
  return response.data;
};

// Request a new bar
export const requestBar = async (bar: Bar): Promise<Bar> => {
  const response = await api.post('/api/v1/bars', bar);
  return response.data;
};

// Request a new bar
export const createBar = async (bar: Bar): Promise<Bar> => {
  const response = await api.post('/api/v1/admin/bars', bar);
  return response.data;
};

// Track a bar (for authenticated users)
export const trackBar = async (barId: number): Promise<User> => {
  return trackUserResource('bars', barId);
};

// Untrack a bar (for authenticated users)
export const untrackBar = async (barId: number): Promise<User> => {
  return untrackUserResource('bars', barId);
};

// Admin functions

// Get all bars (with additional info Admins need) with pagination
export const getAllAdminBars = async (pageable: Pageable): Promise<PageBarAdmin> => {
  const response = await api.get('/api/v1/admin/bars', { params: pageable });
  return response.data;
};

// Get a bar by ID (with additional info Admins need)
export const getBarAdminById = async (id: number): Promise<BarAdmin> => {
  const response = await api.get(`/api/v1/admin/bars/${id}`);
  return response.data;
};

// Update a bar (admin only)
export const updateBar = async (id: number, bar: BarAdmin): Promise<BarAdmin> => {
  const response = await api.put(`/api/v1/admin/bars/${id}`, bar);
  return response.data;
};

// Delete a bar (admin only)
export const deleteBar = async (id: number): Promise<void> => {
  await api.delete(`/api/v1/admin/bars/${id}`);
};

// Approve a bar (admin only)
export const approveBar = async (id: number): Promise<BarAdmin> => {
  const response = await api.post(`/api/v1/admin/bars/${id}/approve`);
  return response.data;
};

// Check a bar (admin only)
export const checkBar = async (barId: number): Promise<BarCheck> => {
  const response = await api.post(`/api/v1/admin/bars/${barId}/check`);
  return response.data;
};

// Get checks for a bar (admin only)
export const getChecksForBar = async (barId: number, pageable: Pageable): Promise<PageBarCheck> => {
  const response = await api.get(`/api/v1/admin/bars/${barId}/checks`, { params: pageable });
  return response.data;
};

// Get latest check for a bar (admin only)
export const getLatestCheckForBar = async (barId: number): Promise<BarCheck> => {
  const response = await api.get(`/api/v1/admin/bars/${barId}/checks/latest`);
  return response.data;
};

// Get unapproved bars (admin only)
export const getUnapprovedBars = async (pageable: Pageable): Promise<PageBarAdmin> => {
  const response = await api.get('/api/v1/admin/bars/unapproved', { params: pageable });
  return response.data;
};
