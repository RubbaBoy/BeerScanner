<script lang="ts">
  import { onMount } from 'svelte';
  import { goto } from '$app/navigation';
  import {getAllBars, getUnapprovedBars, approveBar, deleteBar, getAllAdminBars} from '$lib/services';
  import { currentUser } from '$lib/stores';
  import { Pagination } from '$lib/components';
  import type { Bar, PageBar } from '$lib/types';
  
  // State variables
  let bars: Bar[] = $state([]);
  let unapprovedBars: BarAdmin[] = $state([]);
  let totalPages = $state(0);
  let currentPage = $state(0);
  let isLoading = $state(true);
  let isActionLoading = $state(false);
  let error = $state<string | null>(null);
  let activeTab = $state('all'); // 'all' or 'unapproved'
  
  // Check if user is admin
  onMount(() => {
    // TODO: Add check for admin role
    if (!$currentUser) {
      goto('/login');
    }
    
    fetchBars(0);
  });
  
  // Fetch bars with pagination
  const fetchBars = async (page: number) => {
    isLoading = true;
    error = null;
    
    try {
      if (activeTab === 'all') {
        const response = await getAllAdminBars({ page, size: 10, sort: ['name,asc'] });
        bars = response.content;
        totalPages = response.totalPages;
        currentPage = page;
      } else if (activeTab === 'unapproved') {
        const response = await getUnapprovedBars({ page, size: 10, sort: ['name,asc'] });
        unapprovedBars = response.content;
        totalPages = response.totalPages;
        currentPage = page;
      }
    } catch (e) {
      console.error('Failed to fetch bars:', e);
      error = 'Failed to load bars. Please try again later.';
    } finally {
      isLoading = false;
    }
  };
  
  // Handle page change
  const handlePageChange = (page: number) => {
    fetchBars(page);
  };
  
  // Change active tab
  const setActiveTab = (tab: string) => {
    activeTab = tab;
    fetchBars(0);
  };
  
  // Approve a bar
  const handleApproveBar = async (id: number) => {
    isActionLoading = true;
    
    try {
      await approveBar(id);
      // Refresh the list
      fetchBars(currentPage);
    } catch (e) {
      console.error('Failed to approve bar:', e);
      error = 'Failed to approve bar. Please try again later.';
    } finally {
      isActionLoading = false;
    }
  };
  
  // Delete a bar
  const handleDeleteBar = async (id: number) => {
    if (!confirm('Are you sure you want to delete this bar?')) {
      return;
    }
    
    isActionLoading = true;
    
    try {
      await deleteBar(id);
      // Refresh the list
      fetchBars(currentPage);
    } catch (e) {
      console.error('Failed to delete bar:', e);
      error = 'Failed to delete bar. Please try again later.';
    } finally {
      isActionLoading = false;
    }
  };
</script>

<div class="space-y-8">
  <!-- Page header -->
  <div class="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
    <div>
      <h1 class="text-3xl font-bold text-gray-900">Manage Bars</h1>
      <p class="text-gray-600 mt-1">View, approve, and manage bars in the system</p>
    </div>
    
    <a 
      href="/admin/bars/add"
      class="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-amber-600 hover:bg-amber-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-amber-500"
    >
      <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" viewBox="0 0 20 20" fill="currentColor">
        <path fill-rule="evenodd" d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11-2 0v-5H4a1 1 0 110-2h5V4a1 1 0 011-1z" clip-rule="evenodd" />
      </svg>
      Add New Bar
    </a>
  </div>
  
  <!-- Tabs -->
  <div class="border-b border-gray-200">
    <nav class="-mb-px flex space-x-8">
      <button
        class={`py-4 px-1 border-b-2 font-medium text-sm ${
          activeTab === 'all'
            ? 'border-amber-500 text-amber-600'
            : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
        }`}
        onclick={() => setActiveTab('all')}
      >
        All Bars
      </button>
      <button
        class={`py-4 px-1 border-b-2 font-medium text-sm ${
          activeTab === 'unapproved'
            ? 'border-amber-500 text-amber-600'
            : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
        }`}
        onclick={() => setActiveTab('unapproved')}
      >
        Unapproved Bars
      </button>
    </nav>
  </div>
  
  <!-- Error message -->
  {#if error}
    <div class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
      <p>{error}</p>
    </div>
  {/if}
  
  <!-- Bars table -->
  {#if isLoading}
    <div class="flex justify-center items-center h-64">
      <div class="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-amber-500"></div>
    </div>
  {:else if activeTab === 'all' && bars.length === 0}
    <div class="bg-gray-100 border border-gray-300 text-gray-700 px-4 py-8 rounded text-center">
      <p>No bars found in the system.</p>
    </div>
  {:else if activeTab === 'unapproved' && unapprovedBars.length === 0}
    <div class="bg-gray-100 border border-gray-300 text-gray-700 px-4 py-8 rounded text-center">
      <p>No unapproved bars found.</p>
    </div>
  {:else}
    <div class="bg-white shadow overflow-hidden sm:rounded-md">
      <table class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-50">
          <tr>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Name
            </th>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Location
            </th>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Status
            </th>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Actions
            </th>
          </tr>
        </thead>
        <tbody class="bg-white divide-y divide-gray-200">
          {#if activeTab === 'all'}
            {#each bars as bar}
              <tr>
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="text-sm font-medium text-gray-900">{bar.name}</div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="text-sm text-gray-500">{bar.location}</div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  {#if bar.approved}
                    <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-green-100 text-green-800">
                      Approved
                    </span>
                  {:else}
                    <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-yellow-100 text-yellow-800">
                      Pending
                    </span>
                  {/if}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                  <!-- TODO: Add edit functionality -->
                  <a href={`/admin/bars/${bar.id}`} class="text-indigo-600 hover:text-indigo-900 mr-4">View</a>
                  {#if !bar.approved}
                    <button 
                      onclick={() => handleApproveBar(bar.id)}
                      disabled={isActionLoading}
                      class="text-green-600 hover:text-green-900 mr-4"
                    >
                      Approve
                    </button>
                  {/if}
                  <button
                    onclick={() => handleDeleteBar(bar.id)}
                    disabled={isActionLoading}
                    class="text-red-600 hover:text-red-900"
                  >
                    Delete
                  </button>
                </td>
              </tr>
            {/each}
          {:else if activeTab === 'unapproved'}
            {#each unapprovedBars as bar}
              <tr>
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="text-sm font-medium text-gray-900">{bar.name}</div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="text-sm text-gray-500">{bar.location}</div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-yellow-100 text-yellow-800">
                    Pending
                  </span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                  <a href={`/admin/bars/${bar.id}`} class="text-indigo-600 hover:text-indigo-900 mr-4">View</a>
                  <button 
                    onclick={() => handleApproveBar(bar.id)}
                    disabled={isActionLoading}
                    class="text-green-600 hover:text-green-900 mr-4"
                  >
                    Approve
                  </button>
                  <button 
                    onclick={() => handleDeleteBar(bar.id)}
                    disabled={isActionLoading}
                    class="text-red-600 hover:text-red-900"
                  >
                    Delete
                  </button>
                </td>
              </tr>
            {/each}
          {/if}
        </tbody>
      </table>
    </div>
    
    <!-- Pagination -->
    <Pagination {totalPages} {currentPage} onPageChange={handlePageChange} />
  {/if}
</div>