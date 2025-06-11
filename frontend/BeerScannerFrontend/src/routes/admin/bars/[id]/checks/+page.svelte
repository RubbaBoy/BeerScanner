<script lang="ts">
  import { onMount } from 'svelte';
  import { page } from '$app/stores';
  import { goto } from '$app/navigation';
  import {checkBar, getBarById, getChecksForBar} from '$lib/services';
  import { currentUser } from '$lib/stores';
  import { Pagination } from '$lib/components';
  import { format, formatDistanceToNow } from 'date-fns';
  import type { Bar, BarCheck, PageBarCheck } from '$lib/types';
  import {formatFromNowDate} from "$lib/utils/formatting";

  const barId = parseInt($page.params.id);

  let bar: Bar | null = $state(null);
  let checks: BarCheck[] = $state([]);
  let totalPages = $state(0);
  let currentPage = $state(0);
  let isLoading = $state(true);
  let error = $state<string | null>(null);

  // Format date
  const formatDate = (dateString: string | undefined) => {
    if (!dateString) return 'N/A';
    return format(new Date(dateString), 'MMM d, yyyy HH:mm:ss');
  };

  // Get status badge class
  const getStatusBadgeClass = (status: string) => {
    switch (status) {
      case 'PENDING':
        return 'bg-yellow-100 text-yellow-800';
      case 'PROCESSING':
        return 'bg-blue-100 text-blue-800';
      case 'COMPLETED':
        return 'bg-green-100 text-green-800';
      case 'FAILED':
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  // Fetch bar data and checks
  const fetchData = async (page: number) => {
    isLoading = true;
    error = null;

    try {
      // Fetch bar details
      bar = await getBarById(barId);

      // Fetch checks with pagination
      const response = await getChecksForBar(barId, { page, size: 10, sort: ['createdAt,desc'] });
      checks = response.content;
      totalPages = response.totalPages;
      currentPage = page;
    } catch (e) {
      console.error('Failed to fetch data:', e);
      error = 'Failed to load data. Please try again later.';
    } finally {
      isLoading = false;
    }
  };

  // Handle page change
  const handlePageChange = (page: number) => {
    fetchData(page);
  };

  // Check if user is admin
  onMount(() => {
    // TODO: Add check for admin role
    if (!$currentUser) {
      goto('/login');
      return;
    }

    fetchData(0);
  });
</script>

<div class="space-y-8">
  <!-- Page header -->
  <div class="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
    <div>
      <h1 class="text-3xl font-bold text-gray-900">Bar Check History</h1>
      {#if bar}
        <p class="text-gray-600 mt-1">Menu check history for {bar.name}</p>
      {/if}
    </div>

    <div class="flex space-x-4">
      <a
        href={`/admin/bars/${barId}`}
        class="inline-flex items-center px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-amber-500"
      >
        <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" viewBox="0 0 20 20" fill="currentColor">
          <path fill-rule="evenodd" d="M9.707 16.707a1 1 0 01-1.414 0l-6-6a1 1 0 010-1.414l6-6a1 1 0 011.414 1.414L5.414 9H17a1 1 0 110 2H5.414l4.293 4.293a1 1 0 010 1.414z" clip-rule="evenodd" />
        </svg>
        Back to Bar
      </a>

      <a
        href={`/admin/bars/${barId}/check`}
        class="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-amber-600 hover:bg-amber-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-amber-500"
      >
        <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" viewBox="0 0 20 20" fill="currentColor">
          <path fill-rule="evenodd" d="M4 2a1 1 0 011 1v2.101a7.002 7.002 0 0111.601 2.566 1 1 0 11-1.885.666A5.002 5.002 0 005.999 7H9a1 1 0 010 2H4a1 1 0 01-1-1V3a1 1 0 011-1zm.008 9.057a1 1 0 011.276.61A5.002 5.002 0 0014.001 13H11a1 1 0 110-2h5a1 1 0 011 1v5a1 1 0 11-2 0v-2.101a7.002 7.002 0 01-11.601-2.566 1 1 0 01.61-1.276z" clip-rule="evenodd" />
        </svg>
        Run New Check
      </a>
    </div>
  </div>

  <!-- Error message -->
  {#if error}
    <div class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
      <p>{error}</p>
    </div>
  {/if}

  <!-- Checks table -->
  {#if isLoading}
    <div class="flex justify-center items-center h-64">
      <div class="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-amber-500"></div>
    </div>
  {:else if checks.length === 0}
    <div class="bg-gray-100 border border-gray-300 text-gray-700 px-4 py-8 rounded text-center">
      <p>No menu checks found for this bar.</p>
    </div>
  {:else}
    <div class="bg-white shadow overflow-hidden sm:rounded-md">
      <table class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-50">
          <tr>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Date
            </th>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Status
            </th>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Menu Hash
            </th>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Changes Detected
            </th>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Duration
            </th>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Details
            </th>
          </tr>
        </thead>
        <tbody class="bg-white divide-y divide-gray-200">
          {#each checks as check}
            <tr>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-900" title="{formatFromNowDate(check.createdAt)}">{formatDate(check.createdAt)}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span class={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${getStatusBadgeClass(check.processingStatus)}`}>
                  {check.processingStatus}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-500">{check.menuHash ? check.menuHash.substring(0, 8) + '...' : 'N/A'}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                {#if check.hasChanges === true}
                  <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-green-100 text-green-800">
                    Yes
                  </span>
                {:else if check.hasChanges === false}
                  <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-gray-100 text-gray-800">
                    No
                  </span>
                {:else}
                  <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-gray-100 text-gray-800">
                    N/A
                  </span>
                {/if}
              </td>

              <td class="px-6 py-4 whitespace-nowrap">
                <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-gray-100 text-gray-800">
                  {check.processDuration ? `${check.processDuration} ms` : 'N/A'}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                <a href={`/admin/bars/${barId}/checks/${check.id}`} class="text-indigo-600 hover:text-indigo-900">
                  View Details
                </a>
              </td>
            </tr>
          {/each}
        </tbody>
      </table>
    </div>

    <!-- Pagination -->
    {#if totalPages > 1}
      <Pagination {totalPages} {currentPage} onPageChange={handlePageChange} />
    {/if}
  {/if}
</div>
