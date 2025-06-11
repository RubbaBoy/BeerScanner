<script lang="ts">
  import { format, formatDistanceToNow } from 'date-fns';
  import type { Notification } from '$lib/types';
  import { markNotificationAsRead } from '$lib/services/notificationService';
  import { markAsRead } from '$lib/stores/notificationStore';
  import {formatFromNowDate} from "$lib/utils/formatting";

  let { notification } = $props<{
    notification: Notification;
  }>();

  let isLoading = $state(false);

  // Get icon based on notification type
  const getIcon = (type: string) => {
    switch (type) {
      case 'BEER_AVAILABLE':
        return `
          <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
          </svg>
        `;
      case 'MENU_CHANGED':
        return `
          <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
          </svg>
        `;
      case 'SYSTEM':
        return `
          <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
        `;
      default:
        return `
          <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
          </svg>
        `;
    }
  };

  // Get background color based on notification type
  const getBackgroundColor = (type: string) => {
    switch (type) {
      case 'BEER_AVAILABLE':
        return 'bg-green-50 border-green-200';
      case 'MENU_CHANGED':
        return 'bg-blue-50 border-blue-200';
      case 'SYSTEM':
        return 'bg-purple-50 border-purple-200';
      default:
        return 'bg-gray-50 border-gray-200';
    }
  };

  // Get icon color based on notification type
  const getIconColor = (type: string) => {
    switch (type) {
      case 'BEER_AVAILABLE':
        return 'text-green-500 bg-green-100';
      case 'MENU_CHANGED':
        return 'text-blue-500 bg-blue-100';
      case 'SYSTEM':
        return 'text-purple-500 bg-purple-100';
      default:
        return 'text-gray-500 bg-gray-100';
    }
  };

  // Mark notification as read
  const handleMarkAsRead = async () => {
    if (notification.read) return;

    isLoading = true;
    try {
      await markNotificationAsRead(notification.id!);
      markAsRead(notification.id!);
    } catch (error) {
      console.error('Failed to mark notification as read:', error);
    } finally {
      isLoading = false;
    }
  };
</script>

<div 
  class={`border rounded-lg p-4 mb-4 ${getBackgroundColor(notification.type)} ${notification.read ? 'opacity-70' : ''}`}
>
  <div class="flex items-start">
    <div class={`rounded-full p-2 mr-4 ${getIconColor(notification.type)}`}>
      {@html getIcon(notification.type)}
    </div>

    <div class="flex-1">
      <div class="flex justify-between items-start">
        <h3 class="font-semibold text-gray-900">{notification.title}</h3>
        <span class="text-xs text-gray-500">{formatFromNowDate(notification.createdAt, '')}</span>
      </div>

      <p class="text-gray-700 mt-1">{notification.message}</p>

      <div class="mt-3 flex justify-between items-center">
        <div class="flex space-x-2">
          {#if notification.bar}
            <a 
              href={`/bars/${notification.bar.id}`} 
              class="text-xs bg-amber-100 text-amber-800 px-2 py-1 rounded hover:bg-amber-200 transition-colors"
            >
              View Bar
            </a>
          {/if}

          {#if notification.beer}
            <a 
              href={`/beers/${notification.beer.id}`} 
              class="text-xs bg-amber-100 text-amber-800 px-2 py-1 rounded hover:bg-amber-200 transition-colors"
            >
              View Beer
            </a>
          {/if}
        </div>

        {#if !notification.read}
          <button 
            onclick={handleMarkAsRead}
            disabled={isLoading}
            class="text-xs text-gray-500 hover:text-gray-700 transition-colors"
          >
            {#if isLoading}
              <span class="animate-spin">⟳</span>
            {:else}
              Mark as read
            {/if}
          </button>
        {/if}
      </div>
    </div>
  </div>
</div>
