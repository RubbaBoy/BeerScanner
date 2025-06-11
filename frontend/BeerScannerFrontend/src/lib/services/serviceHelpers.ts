/**
 * Common service helpers and utility functions
 * This file centralizes common functionality used across service files
 * to prevent duplication and ensure consistency.
 */

import api from './api';
import type { User } from '$lib/types';

/**
 * Track user resource helper function
 * Used by both beer and bar tracking to ensure consistent implementation
 */
export const trackUserResource = async (
  resourceType: 'bars' | 'beers',
  resourceId: number,
  options?: { [key: string]: any }
): Promise<User> => {
  const response = await api.post(
    `/api/v1/users/me/tracked-${resourceType}/${resourceId}`,
    null,
    options ? { params: options } : undefined
  );
  return response.data;
};

/**
 * Untrack user resource helper function
 * Used by both beer and bar untracking to ensure consistent implementation
 */
export const untrackUserResource = async (
  resourceType: 'bars' | 'beers',
  resourceId: number
): Promise<User> => {
  const response = await api.delete(`/api/v1/users/me/tracked-${resourceType}/${resourceId}`);
  return response.data;
};
