<script lang="ts">
  import { onMount } from 'svelte';
  import { goto } from '$app/navigation';
  import { getLogs, getLogsByLevel, getLogsByDateRange, clearLogs, type Log, type PageLog } from '$lib/services';
  import { currentUser } from '$lib/stores';
  import { Pagination } from '$lib/components';
  import { format } from 'date-fns';

  // State variables
  let logs: Log[] = $state([]);
  let totalPages = $state(0);
  let currentPage = $state(0);
  let isLoading = $state(true);
  let isClearing = $state(false);
  let error = $state<string | null>(null);
  let success = $state<string | null>(null);
  
  // Filter variables
  let selectedLevel = $state<string | null>(null);
  let startDate = $state<string | null>(null);
  let endDate = $state<string | null>(null);

  // Log levels
  const logLevels = ['INFO', 'WARNING', 'ERROR', 'CRITICAL'];

  // Fetch logs with pagination
  const fetchLogs = async (page: number) => {
    isLoading = true;
    error = null;
    
    try {
      let response: PageLog;
      
      if (selectedLevel) {
        response = await getLogsByLevel(selectedLevel, { page, size: 20, sort: ['timestamp,desc'] });
      } else if (startDate && endDate) {
        response = await getLogsByDateRange(startDate, endDate, { page, size: 20, sort: ['timestamp,desc'] });
      } else {
        response = await getLogs({ page, size: 20, sort: ['timestamp,desc'] });
      }
      
      logs = response.content;
      totalPages = response.totalPages;
      currentPage = page;
    } catch (e) {
      console.error('Failed to fetch logs:', e);
      error = 'Failed to load logs. Please try again later.';
    } finally {
      isLoading = false;
    }
  };

  // Handle page change
  const handlePageChange = (page: number) => {
    fetchLogs(page);
  };

  // Handle level filter
  const handleLevelFilter = (level: string | null) => {
    selectedLevel = level;
    fetchLogs(0);
  };

  // Handle date range filter
  const handleDateRangeFilter = (event: Event) => {
    event.preventDefault();
    fetchLogs(0);
  };

  // Clear all filters
  const clearFilters = () => {
    selectedLevel = null;
    startDate = null;
    endDate = null;
    fetchLogs(0);
  };

  // Clear logs
  const handleClearLogs = async () => {
    if (!confirm('Are you sure you want to clear all logs? This action cannot be undone.')) {
      return;
    }
    
    isClearing = true;
    error = null;
    success = null;
    
    try {
      await clearLogs();
      success = 'Logs cleared successfully.';
      fetchLogs(0);
    } catch (e) {
      console.error('Failed to clear logs:', e);
      error = 'Failed to clear logs. Please try again later.';
    } finally {
      isClearing = false;
    }
  };

  // Format date
  const formatDate = (dateString: string) => {
    if (!dateString) return 'N/A';
    return format(new Date(dateString), 'MMM d, yyyy HH:mm:ss');
  };

  // Get level badge class
  const getLevelBadgeClass = (level: string) => {
    switch (level) {
      case 'INFO':
        return 'bg-blue-100 text-blue-800';
      case 'WARNING':
        return 'bg-yellow-100 text-yellow-800';
      case 'ERROR':
        return 'bg-red-100 text-red-800';
      case 'CRITICAL':
        return 'bg-purple-100 text-purple-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  // Check if user is admin
  onMount(() => {
    // TODO: Add check for admin role
    if (!$currentUser) {
      goto('/login');
      return;
    }

    // Initialize date filters to last 7 days
    const end = new Date();
    const start = new Date();
    start.setDate(start.getDate() - 7);
    
    endDate = end.toISOString().split('T')[0];
    startDate = start.toISOString().split('T')[0];

    fetchLogs(0);
  });
</script>

<div class="space-y-8">
  <!-- Page header -->
  <div class="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
    <div>
      <h1 class="text-3xl font-bold text-gray-900">Error Logs</h1>
      <p class="text-gray-600 mt-1">View and manage application error logs</p>
    </div>
    
    <button 
      onclick={handleClearLogs}
      disabled={isClearing}
      class="px-4 py-2 bg-red-500 text-white rounded-md hover:bg-red-600 focus:outline-none focus:ring-2 focus:ring-red-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed flex items-center"
    >
      {#if isClearing}
        <svg class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
        </svg>
        Clearing...
      {:else}
        <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" viewBox="0 0 20 20" fill="currentColor">
          <path fill-rule="evenodd" d="M9 2a1 1 0 00-.894.553L7.382 4H4a1 1 0 000 2v10a2 2 0 002 2h8a2 2 0 002-2V6a1 1 0 100-2h-3.382l-.724-1.447A1 1 0 0011 2H9zM7 8a1 1 0 012 0v6a1 1 0 11-2 0V8zm5-1a1 1 0 00-1 1v6a1 1 0 102 0V8a1 1 0 00-1-1z" clip-rule="evenodd" />
        </svg>
        Clear Logs
      {/if}
    </button>
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

  <!-- Filters -->
  <div class="bg-white rounded-lg shadow-sm p-6">
    <h2 class="text-xl font-semibold text-gray-900 mb-4">Filters</h2>
    
    <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
      <!-- Level filter -->
      <div>
        <h3 class="text-sm font-medium text-gray-700 mb-2">Filter by Level</h3>
        <div class="flex flex-wrap gap-2">
          {#each logLevels as level}
            <button 
              onclick={() => handleLevelFilter(selectedLevel === level ? null : level)}
              class={`px-3 py-1 rounded-full text-sm ${
                selectedLevel === level 
                  ? 'bg-amber-500 text-white' 
                  : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
              }`}
            >
              {level}
            </button>
          {/each}
        </div>
      </div>
      
      <!-- Date range filter -->
      <div>
        <h3 class="text-sm font-medium text-gray-700 mb-2">Filter by Date Range</h3>
        <form onsubmit={handleDateRangeFilter} class="flex flex-col sm:flex-row gap-2">
          <div class="flex-1">
            <label for="startDate" class="sr-only">Start Date</label>
            <input
              type="date"
              id="startDate"
              bind:value={startDate}
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-amber-500 focus:border-transparent"
            />
          </div>
          
          <div class="flex-1">
            <label for="endDate" class="sr-only">End Date</label>
            <input
              type="date"
              id="endDate"
              bind:value={endDate}
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-amber-500 focus:border-transparent"
            />
          </div>
          
          <button
            type="submit"
            class="px-4 py-2 bg-amber-500 text-white rounded-md hover:bg-amber-600 focus:outline-none focus:ring-2 focus:ring-amber-500 focus:ring-offset-2"
          >
            Apply
          </button>
        </form>
      </div>
    </div>
    
    <!-- Clear filters button -->
    {#if selectedLevel || startDate || endDate}
      <div class="mt-4">
        <button 
          onclick={clearFilters}
          class="text-amber-600 hover:text-amber-700 font-medium text-sm flex items-center"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clip-rule="evenodd" />
          </svg>
          Clear Filters
        </button>
      </div>
    {/if}
  </div>

  <!-- Logs table -->
  {#if isLoading}
    <div class="flex justify-center items-center h-64">
      <div class="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-amber-500"></div>
    </div>
  {:else if logs.length === 0}
    <div class="bg-gray-100 border border-gray-300 text-gray-700 px-4 py-8 rounded text-center">
      <p>No logs found matching your criteria.</p>
    </div>
  {:else}
    <div class="overflow-x-auto">
      <table class="min-w-full bg-white rounded-lg overflow-hidden shadow-sm">
        <thead class="bg-gray-100 text-gray-700">
          <tr>
            <th class="py-3 px-4 text-left">Timestamp</th>
            <th class="py-3 px-4 text-left">Level</th>
            <th class="py-3 px-4 text-left">Source</th>
            <th class="py-3 px-4 text-left">Message</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-200">
          {#each logs as log}
            <tr class="hover:bg-gray-50">
              <td class="py-3 px-4 whitespace-nowrap">{formatDate(log.timestamp)}</td>
              <td class="py-3 px-4">
                <span class={`px-2 py-1 rounded-full text-xs font-medium ${getLevelBadgeClass(log.level)}`}>
                  {log.level}
                </span>
              </td>
              <td class="py-3 px-4">{log.source}</td>
              <td class="py-3 px-4 max-w-md truncate">
                <div class="tooltip">
                  <span class="tooltiptext">{log.message}</span>
                  {log.message}
                </div>
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

<style>
  /* Tooltip styles */
  .tooltip {
    position: relative;
    display: inline-block;
    max-width: 100%;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .tooltip .tooltiptext {
    visibility: hidden;
    width: 300px;
    background-color: #333;
    color: #fff;
    text-align: left;
    border-radius: 6px;
    padding: 8px;
    position: absolute;
    z-index: 1;
    bottom: 125%;
    left: 0;
    opacity: 0;
    transition: opacity 0.3s;
    white-space: normal;
    word-wrap: break-word;
  }

  .tooltip:hover .tooltiptext {
    visibility: visible;
    opacity: 1;
  }
</style>