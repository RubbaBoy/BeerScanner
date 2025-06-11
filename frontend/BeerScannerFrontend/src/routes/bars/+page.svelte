<script lang="ts">
  import { onMount } from 'svelte';
  import { getAllBars, searchBars } from '$lib/services';
  import { BarCard, Pagination } from '$lib/components';
  import type { Bar, PageBar } from '$lib/types';
  
  let bars: Bar[] = $state([]);
  let totalPages = $state(0);
  let currentPage = $state(0);
  let isLoading = $state(true);
  let error = $state<string | null>(null);
  let searchTerm = $state('');
  let debouncedSearchTerm = $state('');
  let searchTimeout: NodeJS.Timeout;
  
  // Fetch bars with pagination
  const fetchBars = async (page: number, search: string = '') => {
    isLoading = true;
    error = null;
    
    try {
      let response: PageBar;
      
      if (search) {
        response = await searchBars(search, { page, size: 12, sort: ['name,asc'] });
      } else {
        response = await getAllBars({ page, size: 12, sort: ['name,asc'] });
      }
      
      bars = response.content;
      totalPages = response.totalPages;
      currentPage = page;
    } catch (e) {
      console.error('Failed to fetch bars:', e);
      error = 'Failed to load bars. Please try again later.';
    } finally {
      isLoading = false;
    }
  };
  
  // Handle page change
  const handlePageChange = (page: number) => {
    fetchBars(page, debouncedSearchTerm);
  };
  
  // Handle search input
  const handleSearchInput = (event: Event) => {
    const target = event.target as HTMLInputElement;
    searchTerm = target.value;
    
    // Debounce search
    clearTimeout(searchTimeout);
    searchTimeout = setTimeout(() => {
      debouncedSearchTerm = searchTerm;
      fetchBars(0, searchTerm);
    }, 300);
  };
  
  // Handle search form submission
  const handleSearchSubmit = (event: Event) => {
    event.preventDefault();
    debouncedSearchTerm = searchTerm;
    fetchBars(0, searchTerm);
  };
  
  // Fetch initial data on mount
  onMount(() => {
    fetchBars(0);
  });
</script>

<div class="space-y-8">
  <!-- Page header -->
  <div class="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
    <div>
      <h1 class="text-3xl font-bold text-gray-900">Bars</h1>
      <p class="text-gray-600 mt-1">Browse all bars and track your favorites</p>
    </div>
    
    <!-- Search form -->
    <form onsubmit={handleSearchSubmit} class="w-full md:w-auto">
      <div class="relative">
        <input
          type="text"
          placeholder="Search bars..."
          value={searchTerm}
          oninput={handleSearchInput}
          class="w-full md:w-64 px-4 py-2 pr-10 rounded-md border border-gray-300 focus:outline-none focus:ring-2 focus:ring-amber-500 focus:border-transparent"
        />
        <button 
          type="submit"
          class="absolute right-2 top-1/2 transform -translate-y-1/2 text-gray-500 hover:text-gray-700"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M8 4a4 4 0 100 8 4 4 0 000-8zM2 8a6 6 0 1110.89 3.476l4.817 4.817a1 1 0 01-1.414 1.414l-4.816-4.816A6 6 0 012 8z" clip-rule="evenodd" />
          </svg>
        </button>
      </div>
    </form>
  </div>
  
  <!-- Bars grid -->
  {#if isLoading}
    <div class="flex justify-center items-center h-64">
      <div class="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-amber-500"></div>
    </div>
  {:else if error}
    <div class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
      <p>{error}</p>
    </div>
  {:else if bars.length === 0}
    <div class="bg-gray-100 border border-gray-300 text-gray-700 px-4 py-8 rounded text-center">
      {#if debouncedSearchTerm}
        <p>No bars found matching "{debouncedSearchTerm}". Try a different search term.</p>
      {:else}
        <p>No bars available yet. Check back soon!</p>
      {/if}
    </div>
  {:else}
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
      {#each bars as bar}
        <BarCard {bar} />
      {/each}
    </div>
    
    <!-- Pagination -->
    <Pagination {totalPages} {currentPage} onPageChange={handlePageChange} />
  {/if}
</div>