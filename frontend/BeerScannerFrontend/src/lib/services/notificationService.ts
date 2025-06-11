import api from './api';
import type { Notification, PageNotification, Pageable } from '$lib/types';

// Get notifications with pagination
export const getNotifications = async (pageable: Pageable): Promise<PageNotification> => {
  const response = await api.get('/api/v1/notifications', { params: pageable });
  return response.data;
};

// Get unread notifications
export const getUnreadNotifications = async (): Promise<Notification[]> => {
  const response = await api.get('/api/v1/notifications/unread');
  return response.data;
};

// Count unread notifications
export const countUnreadNotifications = async (): Promise<Record<string, number>> => {
  const response = await api.get('/api/v1/notifications/unread/count');
  return response.data;
};

// Mark a notification as read
export const markNotificationAsRead = async (id: number): Promise<Notification> => {
  const response = await api.put(`/api/v1/notifications/${id}/read`);
  return response.data;
};

// Admin functions
// Send a system notification (admin only)
export const sendSystemNotification = async (data: Record<string, string>): Promise<Record<string, string>> => {
  const response = await api.post('/api/v1/notifications/admin/system', data);
  return response.data;
};

// Process unsent notifications (admin only)
export const processUnsentNotifications = async (): Promise<Record<string, string>> => {
  const response = await api.post('/api/v1/notifications/admin/process-unsent');
  return response.data;
};
