<script lang="ts">
  import type {Notification} from '$lib/types';
  import {markNotificationAsRead} from '$lib/services/notificationService';
  import {markAsRead} from '$lib/stores/notificationStore';
  import {formatFromNowDate} from "$lib/utils/formatting";
  import {BeerCard} from "$lib/components/index";

  let { notification } = $props<{
    notification: Notification;
  }>();

  let isLoading = $state(false);
  let showBeerDetails = $state(false);

  // Check if notification has beer changes
  const hasBeerChanges = notification.beersAdded?.length > 0 || notification.beersRemoved?.length > 0;

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
      notification.read = true;
    } catch (error) {
      console.error('Failed to mark notification as read:', error);
    } finally {
      isLoading = false;
    }
  };

  const toggleBeerDetails = () => {
    showBeerDetails = !showBeerDetails;
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

      {#if hasBeerChanges}
        <div class="mt-3">
          <button
                  onclick={toggleBeerDetails}
                  class="inline-flex items-center px-3 py-1.5 text-xs font-medium text-blue-600 bg-blue-50 border border-blue-200 rounded-md hover:bg-blue-100 transition-colors duration-200"
          >
            <svg class="w-4 h-4 mr-1 transition-transform duration-200 {showBeerDetails ? 'rotate-180' : ''}" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
            </svg>
            {showBeerDetails ? 'Hide' : 'Show'} Beer Changes
            {#if notification.beersAdded?.length}
              <span class="ml-1 text-green-600">(+{notification.beersAdded.length})</span>
            {/if}
            {#if notification.beersRemoved?.length}
              <span class="ml-1 text-red-600">(-{notification.beersRemoved.length})</span>
            {/if}
          </button>
        </div>
      {/if}

      {#if showBeerDetails && hasBeerChanges}
        <div class="mt-4 space-y-4">
          {#if notification.beersAdded?.length > 0}
            <div>
              <h4 class="text-sm font-medium text-green-700 mb-2 flex items-center">
                <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"></path>
                </svg>
                Added Beers ({notification.beersAdded.length})
              </h4>
              <div class="bg-green-50 border border-green-200 rounded-md p-4">
                <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
                  {#each notification.beersAdded as beer}
                    <BeerCard {beer} showActions={false} />
                  {/each}
                </div>
              </div>
            </div>
          {/if}

          {#if notification.beersRemoved?.length > 0}
            <div>
              <h4 class="text-sm font-medium text-red-700 mb-2 flex items-center">
                <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 12H4"></path>
                </svg>
                Removed Beers ({notification.beersRemoved.length})
              </h4>
              <div class="bg-red-50 border border-red-200 rounded-md p-4">
                <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
                  {#each notification.beersRemoved as beer}
                    <BeerCard {beer} showActions={false} />
                  {/each}
                </div>
              </div>
            </div>
          {/if}
        </div>
      {/if}

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
