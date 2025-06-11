import { writable } from 'svelte/store';
import type { Notification } from '$lib/types';
import { getUnreadNotifications, countUnreadNotifications } from '$lib/services/notificationService';

// Create a writable store for unread notifications
export const unreadNotifications = writable<Notification[]>([]);

// Create a writable store for the unread notification count
export const unreadCount = writable<number>(0);

// Initialize the notification store
export const initNotificationStore = async () => {
  try {
    // Get unread notifications
    const notifications = await getUnreadNotifications();
    unreadNotifications.set(notifications);
    
    // Get unread notification count
    const countResponse = await countUnreadNotifications();
    const count = countResponse.count || 0;
    unreadCount.set(count);
  } catch (error) {
    console.error('Failed to initialize notification store:', error);
  }
};

// Add a notification to the store
export const addNotification = (notification: Notification) => {
  unreadNotifications.update(notifications => [notification, ...notifications]);
  unreadCount.update(count => count + 1);
};

// Mark a notification as read
export const markAsRead = (id: number) => {
  unreadNotifications.update(notifications => 
    notifications.filter(notification => notification.id !== id)
  );
  unreadCount.update(count => Math.max(0, count - 1));
};

// Clear all notifications
export const clearNotifications = () => {
  unreadNotifications.set([]);
  unreadCount.set(0);
};