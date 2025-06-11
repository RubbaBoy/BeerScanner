import { writable } from 'svelte/store';
import type { User } from '$lib/types';
import { browser } from '$app/environment';
import { getUserFromToken, isAuthenticated } from '$lib/services/authService';
import { getCurrentUser } from '$lib/services/userService';
import {refreshTrackedStore} from "$lib/stores/trackedStore";

// Create a writable store for the current user
export const currentUser = writable<Partial<User> | null>(null);

const setupUser = () => {
  if (isAuthenticated()) {
    // First set the partial user from token for immediate UI response
    const partialUser = getUserFromToken();
    if (partialUser) {
      currentUser.set(partialUser);

      // Then fetch the full user data from the API
      getCurrentUser()
          .then(fullUser => {
            currentUser.set(fullUser);
          })
          .catch(error => {
            console.error('Failed to fetch full user data:', error);
          });

      // Forget result, just run async
      refreshTrackedStore()
    }
  } else {
    currentUser.set(null);
  }
}

// Initialize the store with the user from the token if available
export const initAuthStore = () => {
  // Initial check
  if (browser) {
    setupUser()

    // Listen for storage events (including our custom one)
    window.addEventListener('storage', () => {
      setupUser()
    });
  }
};


// Clear the store when the user logs out
export const clearCurrentUser = () => {
  currentUser.set(null);
};
