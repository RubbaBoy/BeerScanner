<script lang="ts">
  import { onMount } from 'svelte';
  import { getNotifications } from '$lib/services';
  import { NotificationItem, Pagination } from '$lib/components';
  import { isAuthenticated } from '$lib/services/authService';
  import { goto } from '$app/navigation';
  import type { Notification } from '$lib/types';

  let notifications: Notification[] = $state([]);
  let totalPages = $state(0);
  let currentPage = $state(0);
  let isLoading = $state(true);
  let error = $state<string | null>(null);

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

  // Redirect to login if not authenticated
  onMount(() => {
    if (!isAuthenticated()) {
      goto('/login');
      return;
    }
    
    fetchNotifications(0);
  });
</script>

<div class="space-y-8">
  <!-- Page header -->
  <div>
    <h1 class="text-3xl font-bold text-gray-900">Notifications</h1>
    <p class="text-gray-600 mt-1">Stay updated on your tracked beers and bars</p>
  </div>
  
  <!-- Notifications list -->
  {#if isLoading}
    <div class="flex justify-center items-center h-64">
      <div class="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-amber-500"></div>
    </div>
  {:else if error}
    <div class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
      <p>{error}</p>
    </div>
  {:else if notifications.length === 0}
    <div class="bg-gray-100 border border-gray-300 text-gray-700 px-4 py-8 rounded text-center">
      <p>You don't have any notifications yet.</p>
      <p class="mt-2">Track beers and bars to get notified when they're updated.</p>
    </div>
  {:else}
    <div class="space-y-4">
      {#each notifications as notification}
        <NotificationItem {notification} />
      {/each}
    </div>
    
    <!-- Pagination -->
    {#if totalPages > 1}
      <Pagination {totalPages} {currentPage} onPageChange={handlePageChange} />
    {/if}
  {/if}
</div>