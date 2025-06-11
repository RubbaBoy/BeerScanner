<script lang="ts">
  import { onMount } from 'svelte';
  import { goto } from '$app/navigation';
  import { sendSystemNotification, getNotifications, processUnsentNotifications } from '$lib/services';
  import { currentUser } from '$lib/stores';
  import { Pagination } from '$lib/components';
  import type { Notification } from '$lib/types';

  // State variables
  let notifications: Notification[] = $state([]);
  let totalPages = $state(0);
  let currentPage = $state(0);
  let isLoading = $state(true);
  let isSending = $state(false);
  let isProcessing = $state(false);
  let error = $state<string | null>(null);
  let success = $state<string | null>(null);
  
  // Form data
  let title = $state('');
  let message = $state('');

  // Fetch notifications with pagination
  const fetchNotifications = async (page: number) => {
    isLoading = true;
    error = null;
    
    try {
      const response = await getNotifications({ page, size: 10, sort: ['createdAt,desc'] });
      notifications = response.content;
      totalPages = response.totalPages;
      currentPage = page;
    } catch (e) {
      console.error('Failed to fetch notifications:', e);
      error = 'Failed to load notifications. Please try again later.';
    } finally {
      isLoading = false;
    }
  };

  // Handle page change
  const handlePageChange = (page: number) => {
    fetchNotifications(page);
  };

  // Send system notification
  const handleSendNotification = async (event: Event) => {
    event.preventDefault();
    isSending = true;
    error = null;
    success = null;
    
    try {
      await sendSystemNotification({ title, message });
      success = 'System notification sent successfully.';
      title = '';
      message = '';
      // Refresh the notifications list
      fetchNotifications(0);
    } catch (e) {
      console.error('Failed to send notification:', e);
      error = 'Failed to send notification. Please try again later.';
    } finally {
      isSending = false;
    }
  };

  // Process unsent notifications
  const handleProcessUnsent = async () => {
    isProcessing = true;
    error = null;
    success = null;
    
    try {
      await processUnsentNotifications();
      success = 'Unsent notifications processed successfully.';
      // Refresh the notifications list
      fetchNotifications(0);
    } catch (e) {
      console.error('Failed to process unsent notifications:', e);
      error = 'Failed to process unsent notifications. Please try again later.';
    } finally {
      isProcessing = false;
    }
  };

  // Format date
  const formatDate = (dateString: string) => {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleString();
  };

  // Check if user is admin
  onMount(() => {
    // TODO: Add check for admin role
    if (!$currentUser) {
      goto('/login');
      return;
    }

    fetchNotifications(0);
  });
</script>

<div class="space-y-8">
  <!-- Page header -->
  <div>
    <h1 class="text-3xl font-bold text-gray-900">System Notifications</h1>
    <p class="text-gray-600 mt-1">Send system-wide notifications and view notification history</p>
  </div>

  <!-- Success message -->
  {#if success}
    <div class="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded relative" role="alert">
      <span class="block sm:inline">{success}</span>
      <button 
        onclick={() => success = null}
        class="absolute top-0 bottom-0 right-0 px-4 py-3"
      >
        <svg class="fill-current h-6 w-6 text-green-500" role="button" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20">
          <title>Close</title>
          <path d="M14.348 14.849a1.2 1.2 0 0 1-1.697 0L10 11.819l-2.651 3.029a1.2 1.2 0 1 1-1.697-1.697l2.758-3.15-2.759-3.152a1.2 1.2 0 1 1 1.697-1.697L10 8.183l2.651-3.031a1.2 1.2 0 1 1 1.697 1.697l-2.758 3.152 2.758 3.15a1.2 1.2 0 0 1 0 1.698z"/>
        </svg>
      </button>
    </div>
  {/if}

  <!-- Error message -->
  {#if error}
    <div class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative" role="alert">
      <span class="block sm:inline">{error}</span>
      <button 
        onclick={() => error = null}
        class="absolute top-0 bottom-0 right-0 px-4 py-3"
      >
        <svg class="fill-current h-6 w-6 text-red-500" role="button" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20">
          <title>Close</title>
          <path d="M14.348 14.849a1.2 1.2 0 0 1-1.697 0L10 11.819l-2.651 3.029a1.2 1.2 0 1 1-1.697-1.697l2.758-3.15-2.759-3.152a1.2 1.2 0 1 1 1.697-1.697L10 8.183l2.651-3.031a1.2 1.2 0 1 1 1.697 1.697l-2.758 3.152 2.758 3.15a1.2 1.2 0 0 1 0 1.698z"/>
        </svg>
      </button>
    </div>
  {/if}

  <!-- Send notification form -->
  <div class="bg-white rounded-lg shadow-sm p-6">
    <h2 class="text-xl font-semibold text-gray-900 mb-4">Send System Notification</h2>
    
    <form onsubmit={handleSendNotification} class="space-y-4">
      <div>
        <label for="title" class="block text-sm font-medium text-gray-700 mb-1">Title</label>
        <input
          type="text"
          id="title"
          bind:value={title}
          required
          class="w-full px-4 py-2 rounded-md border border-gray-300 focus:outline-none focus:ring-2 focus:ring-amber-500 focus:border-transparent"
          placeholder="Notification title"
        />
      </div>
      
      <div>
        <label for="message" class="block text-sm font-medium text-gray-700 mb-1">Message</label>
        <textarea
          id="message"
          bind:value={message}
          required
          rows="4"
          class="w-full px-4 py-2 rounded-md border border-gray-300 focus:outline-none focus:ring-2 focus:ring-amber-500 focus:border-transparent"
          placeholder="Notification message"
        ></textarea>
      </div>
      
      <div class="flex justify-end">
        <button
          type="submit"
          disabled={isSending}
          class="px-4 py-2 bg-amber-500 text-white rounded-md hover:bg-amber-600 focus:outline-none focus:ring-2 focus:ring-amber-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed flex items-center"
        >
          {#if isSending}
            <svg class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            Sending...
          {:else}
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" viewBox="0 0 20 20" fill="currentColor">
              <path d="M10.894 2.553a1 1 0 00-1.788 0l-7 14a1 1 0 001.169 1.409l5-1.429A1 1 0 009 15.571V11a1 1 0 112 0v4.571a1 1 0 00.725.962l5 1.428a1 1 0 001.17-1.408l-7-14z" />
            </svg>
            Send Notification
          {/if}
        </button>
      </div>
    </form>
  </div>

  <!-- Process unsent notifications -->
  <div class="bg-white rounded-lg shadow-sm p-6">
    <div class="flex justify-between items-center">
      <h2 class="text-xl font-semibold text-gray-900">Process Unsent Notifications</h2>
      
      <button
        onclick={handleProcessUnsent}
        disabled={isProcessing}
        class="px-4 py-2 bg-blue-500 text-white rounded-md hover:bg-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed flex items-center"
      >
        {#if isProcessing}
          <svg class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          Processing...
        {:else}
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M4 2a1 1 0 011 1v2.101a7.002 7.002 0 0111.601 2.566 1 1 0 11-1.885.666A5.002 5.002 0 005.999 7H9a1 1 0 010 2H4a1 1 0 01-1-1V3a1 1 0 011-1zm.008 9.057a1 1 0 011.276.61A5.002 5.002 0 0014.001 13H11a1 1 0 110-2h5a1 1 0 011 1v5a1 1 0 11-2 0v-2.101a7.002 7.002 0 01-11.601-2.566 1 1 0 01.61-1.276z" clip-rule="evenodd" />
          </svg>
          Process Unsent
        {/if}
      </button>
    </div>
    <p class="text-gray-600 mt-2">Manually process notifications that haven't been sent yet.</p>
  </div>

  <!-- Notification history -->
  <div class="bg-white rounded-lg shadow-sm p-6">
    <h2 class="text-xl font-semibold text-gray-900 mb-4">Notification History</h2>
    
    {#if isLoading}
      <div class="flex justify-center items-center h-64">
        <div class="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-amber-500"></div>
      </div>
    {:else if notifications.length === 0}
      <div class="bg-gray-100 border border-gray-300 text-gray-700 px-4 py-8 rounded text-center">
        <p>No notifications found.</p>
      </div>
    {:else}
      <div class="overflow-x-auto">
        <table class="min-w-full bg-white rounded-lg overflow-hidden">
          <thead class="bg-gray-100 text-gray-700">
            <tr>
              <th class="py-3 px-4 text-left">Title</th>
              <th class="py-3 px-4 text-left">Type</th>
              <th class="py-3 px-4 text-left">Created</th>
              <th class="py-3 px-4 text-left">Sent</th>
              <th class="py-3 px-4 text-left">Status</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-200">
            {#each notifications as notification}
              <tr class="hover:bg-gray-50">
                <td class="py-3 px-4 font-medium">{notification.title}</td>
                <td class="py-3 px-4">{notification.type}</td>
                <td class="py-3 px-4">{formatDate(notification.createdAt)}</td>
                <td class="py-3 px-4">{notification.sentAt ? formatDate(notification.sentAt) : 'Not sent'}</td>
                <td class="py-3 px-4">
                  {#if notification.sent}
                    <span class="px-2 py-1 bg-green-100 text-green-800 rounded-full text-xs font-medium">Sent</span>
                  {:else}
                    <span class="px-2 py-1 bg-yellow-100 text-yellow-800 rounded-full text-xs font-medium">Pending</span>
                  {/if}
                </td>
              </tr>
            {/each}
          </tbody>
        </table>
      </div>
      
      <!-- Pagination -->
      {#if totalPages > 1}
        <div class="mt-4">
          <Pagination {totalPages} {currentPage} onPageChange={handlePageChange} />
        </div>
      {/if}
    {/if}
  </div>
</div>