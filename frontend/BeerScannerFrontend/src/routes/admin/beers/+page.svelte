<script lang="ts">
  import { onMount } from 'svelte';
  import { goto } from '$app/navigation';
  import {getAllBeers, deleteBeer, getRequestedBeers, unapproveBeerRequest, approveBeerRequest} from '$lib/services';
  import { currentUser } from '$lib/stores';
  import { Pagination } from '$lib/components';
  import type { Beer, PageBeer } from '$lib/types';
  
  // State variables
  let beers: Beer[] = $state([]);
  let beerRequests: Beer[] = $state([]);
  let totalPages = $state(0);
  let currentPage = $state(0);
  let isLoading = $state(true);
  let isActionLoading = $state(false);
  let error = $state<string | null>(null);
  let searchTerm = $state('');
  let debouncedSearchTerm = $state('');
  let activeTab = $state('all'); // 'all' or 'requested'
  let searchTimeout: NodeJS.Timeout;
  
  // Check if user is admin
  onMount(() => {
    // TODO: Add check for admin role
    if (!$currentUser) {
      goto('/login');
    }
    
    fetchBeers(0);
  });
  
  // Fetch beers with pagination
  const fetchBeers = async (page: number) => {
    isLoading = true;
    error = null;
    
    try {
      if (activeTab === 'all') {
        const response = await getAllBeers({ page, size: 10, sort: ['name,asc'] });
        beers = response.content;
        totalPages = response.totalPages;
        currentPage = page;
      } else if (activeTab === 'requested') {
        const response = await getRequestedBeers({ page, size: 10, sort: ['name,asc'] });
        beerRequests = response.content;
        totalPages = response.totalPages;
        currentPage = page;
      }
    } catch (e) {
      console.error('Failed to fetch beers:', e);
      error = 'Failed to load beers. Please try again later.';
    } finally {
      isLoading = false;
    }
  };
  
  // Handle page change
  const handlePageChange = (page: number) => {
    fetchBeers(page);
  };

  // Change active tab
  const setActiveTab = (tab: string) => {
    activeTab = tab;
    fetchBeers(0);
  };

  // Approved Beers
  
  // Delete a beer
  const handleDeleteBeer = async (id: number) => {
    if (!confirm('Are you sure you want to delete this beer?')) {
      return;
    }

    isActionLoading = true;

    try {
      await deleteBeer(id);
      // Refresh the list
      fetchBeers(currentPage);
    } catch (e) {
      console.error('Failed to delete beer:', e);
      error = 'Failed to delete beer. Please try again later.';
    } finally {
      isActionLoading = false;
    }
  };

  // Requested Beers

  // Unapprove a beer request
  const handleUnapproveBeerRequest = async (id: number) => {
    if (!confirm('Are you sure you want to unapprove this beer request?')) {
      return;
    }
    
    isActionLoading = true;
    
    try {
      await unapproveBeerRequest(id);
      // Refresh the list
      fetchBeers(currentPage);
    } catch (e) {
      console.error('Failed to unapprove beer:', e);
      error = 'Failed to unapprove beer. Please try again later.';
    } finally {
      isActionLoading = false;
    }
  };

  // Delete a beer
  const handleApproveBeerRequest = async (id: number) => {
    if (!confirm('Are you sure you want to approve this beer request?')) {
      return;
    }

    isActionLoading = true;

    try {
      await approveBeerRequest(id);
      // Refresh the list
      fetchBeers(currentPage);
    } catch (e) {
      console.error('Failed to approve beer:', e);
      error = 'Failed to approve beer. Please try again later.';
    } finally {
      isActionLoading = false;
    }
  };

  
  // Handle search input
  const handleSearchInput = (event: Event) => {
    const target = event.target as HTMLInputElement;
    searchTerm = target.value;
    
    // Debounce search
    clearTimeout(searchTimeout);
    searchTimeout = setTimeout(() => {
      debouncedSearchTerm = searchTerm;
      // TODO: Implement search functionality
      fetchBeers(0);
    }, 300);
  };
</script>

<div class="space-y-8">
  <!-- Page header -->
  <div class="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
    <div>
      <h1 class="text-3xl font-bold text-gray-900">Manage Beers</h1>
      <p class="text-gray-600 mt-1">View, edit, and manage beers in the system</p>
    </div>
    
    <a 
      href="/admin/beers/add"
      class="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-amber-600 hover:bg-amber-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-amber-500"
    >
      <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" viewBox="0 0 20 20" fill="currentColor">
        <path fill-rule="evenodd" d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11-2 0v-5H4a1 1 0 110-2h5V4a1 1 0 011-1z" clip-rule="evenodd" />
      </svg>
      Add New Beer
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
          activeTab === 'requested'
            ? 'border-amber-500 text-amber-600'
            : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
        }`}
              onclick={() => setActiveTab('requested')}
      >
        Requested Beers
      </button>
    </nav>
  </div>

  <!-- Search beers -->
  {#if activeTab === 'all' && beers.length !== 0}
    <div class="bg-white rounded-lg shadow-sm p-4">
      <div class="relative">
        <input
          type="text"
          placeholder="Search beers..."
          value={searchTerm}
          oninput={handleSearchInput}
          class="w-full px-4 py-2 pr-10 rounded-md border border-gray-300 focus:outline-none focus:ring-2 focus:ring-amber-500 focus:border-transparent"
        />
        <div class="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-500">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M8 4a4 4 0 100 8 4 4 0 000-8zM2 8a6 6 0 1110.89 3.476l4.817 4.817a1 1 0 01-1.414 1.414l-4.816-4.816A6 6 0 012 8z" clip-rule="evenodd" />
          </svg>
        </div>
      </div>
    </div>
  {/if}
  
  <!-- Error message -->
  {#if error}
    <div class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
      <p>{error}</p>
    </div>
  {/if}
  
  <!-- Beers table -->
  {#if isLoading}
    <div class="flex justify-center items-center h-64">
      <div class="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-amber-500"></div>
    </div>
  {:else if activeTab === 'all' && beers.length === 0}
    <div class="bg-gray-100 border border-gray-300 text-gray-700 px-4 py-8 rounded text-center">
      <p>No beers found in the system.</p>
    </div>
  {:else if activeTab === 'requested' && beerRequests.length === 0}
    <div class="bg-gray-100 border border-gray-300 text-gray-700 px-4 py-8 rounded text-center">
      <p>No requested beers found in the system.</p>
    </div>
  {:else if activeTab === 'all'}
    <div class="bg-white shadow overflow-hidden sm:rounded-md">
      <table class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-50">
          <tr>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Name
            </th>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Brewery
            </th>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Type
            </th>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              ABV
            </th>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Actions
            </th>
          </tr>
        </thead>
        <tbody class="bg-white divide-y divide-gray-200">
          {#each beers as beer}
            <tr>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm font-medium text-gray-900">{beer.name}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-500">{beer.brewery || '-'}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-500">{beer.type || '-'}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-500">{beer.abv ? `${beer.abv}%` : '-'}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                <a href={`/beers/${beer.id}`} class="text-indigo-600 hover:text-indigo-900 mr-4">View</a>
                <a href={`/admin/beers/${beer.id}`} class="text-amber-600 hover:text-amber-700 mr-4">Edit</a>
                <button 
                  onclick={() => handleDeleteBeer(beer.id)}
                  disabled={isActionLoading}
                  class="text-red-600 hover:text-red-900"
                >
                  Delete
                </button>
              </td>
            </tr>
          {/each}
        </tbody>
      </table>
    </div>
    
    <!-- Pagination -->
    <Pagination {totalPages} {currentPage} onPageChange={handlePageChange} />
  {:else if activeTab === 'requested'}
    <div class="bg-white shadow overflow-hidden sm:rounded-md">
      <table class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-50">
        <tr>
          <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
            Name
          </th>
          <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
            Brewery
          </th>
          <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
            Type
          </th>
          <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
            ABV
          </th>
          <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
            Actions
          </th>
        </tr>
        </thead>
        <tbody class="bg-white divide-y divide-gray-200">
        {#each beerRequests as beerRequest}
          <tr>
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="text-sm font-medium text-gray-900">{beerRequest.name}</div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="text-sm text-gray-500">{beerRequest.brewery || '-'}</div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="text-sm text-gray-500">{beerRequest.type || '-'}</div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="text-sm text-gray-500">{beerRequest.abv ? `${beerRequest.abv}%` : '-'}</div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                <button
                        onclick={() => handleApproveBeerRequest(beerRequest.id)}
                        disabled={isActionLoading}
                        class="text-indigo-600 hover:text-indigo-900 mr-4"
                >Approve</button>
              <button
                      onclick={() => handleUnapproveBeerRequest(beerRequest.id)}
                      disabled={isActionLoading}
                      class="text-red-600 hover:text-red-900"
              >
                Delete
              </button>
            </td>
          </tr>
        {/each}
        </tbody>
      </table>
    </div>

    <!-- Pagination -->
    <Pagination {totalPages} {currentPage} onPageChange={handlePageChange} />
  {/if}
  
  <!-- TODO: Add more admin functionality as needed -->
</div>