<script lang="ts">
  import { onMount } from 'svelte';
  import { goto } from '$app/navigation';
  import { getAllBeers, searchBeers, getBeersByType, getBeersByBrewery } from '$lib/services';
  import { BeerCard, Pagination } from '$lib/components';
  import type { Beer, PageBeer } from '$lib/types';

  let beers: Beer[] = $state([]);
  let totalPages = $state(0);
  let currentPage = $state(0);
  let isLoading = $state(true);
  let error = $state<string | null>(null);
  let searchTerm = $state('');
  let debouncedSearchTerm = $state('');
  let searchTimeout: NodeJS.Timeout;
  let filterType = $state<string | null>(null);
  let filterBrewery = $state<string | null>(null);

  // Common beer types for filtering
  const beerTypes = [
    'IPA',
    'Stout',
    'Lager',
    'Pilsner',
    'Porter',
    'Ale',
    'Wheat Beer',
    'Sour',
  ];

  // Fetch beers with pagination
  const fetchBeers = async (page: number) => {
    isLoading = true;
    error = null;

    try {
      let response: PageBeer;

      if (debouncedSearchTerm) {
        response = await searchBeers(debouncedSearchTerm, { page, size: 12, sort: ['name,asc'] });
      } else if (filterType) {
        response = await getBeersByType(filterType, { page, size: 12, sort: ['name,asc'] });
      } else if (filterBrewery) {
        response = await getBeersByBrewery(filterBrewery, { page, size: 12, sort: ['name,asc'] });
      } else {
        response = await getAllBeers({ page, size: 12, sort: ['name,asc'] });
      }

      beers = response.content;
      totalPages = response.totalPages;
      currentPage = page;
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

  // Handle search input
  const handleSearchInput = (event: Event) => {
    const target = event.target as HTMLInputElement;
    searchTerm = target.value;

    // Clear filters when searching
    if (searchTerm) {
      filterType = null;
      filterBrewery = null;
    }

    // Debounce search
    clearTimeout(searchTimeout);
    searchTimeout = setTimeout(() => {
      debouncedSearchTerm = searchTerm;
      fetchBeers(0);
    }, 300);
  };

  // Handle search form submission
  const handleSearchSubmit = (event: Event) => {
    event.preventDefault();
    debouncedSearchTerm = searchTerm;
    fetchBeers(0);
  };

  // Handle type filter
  const handleTypeFilter = (type: string | null) => {
    filterType = type;
    filterBrewery = null;
    searchTerm = '';
    debouncedSearchTerm = '';
    fetchBeers(0);
  };

  // Handle brewery filter
  const handleBreweryFilter = (brewery: string | null) => {
    filterBrewery = brewery;
    filterType = null;
    searchTerm = '';
    debouncedSearchTerm = '';
    fetchBeers(0);
  };

  // Clear all filters
  const clearFilters = () => {
    filterType = null;
    filterBrewery = null;
    searchTerm = '';
    debouncedSearchTerm = '';
    fetchBeers(0);
  };

  // Navigate to track beer page
  const goToTrackBeerPage = () => {
    goto('/beers/track');
  };

  // Fetch initial data on mount
  onMount(() => {
    fetchBeers(0);
  });
</script>

<div class="container mx-auto p-4">
  <h1 class="text-3xl font-bold mb-6 text-center">Beer Catalog</h1>

  <div class="mb-6 flex flex-col sm:flex-row justify-between items-center gap-4">
    <form on:submit={handleSearchSubmit} class="flex-grow sm:max-w-md">
      <input
        type="text"
        bind:value={searchTerm}
        on:input={handleSearchInput}
        placeholder="Search beers..."
        class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-blue-500 focus:border-blue-500"
      />
    </form>
    <button
      on:click={goToTrackBeerPage}
      class="px-4 py-2 bg-green-500 text-white rounded-lg hover:bg-green-600 focus:outline-none focus:ring-2 focus:ring-green-500 focus:ring-opacity-50 w-full sm:w-auto"
    >
      Track a Beer
    </button>
  </div>

  <div class="mb-6 flex flex-wrap gap-2">
    {#each beerTypes as type}
      <button
        on:click={() => handleTypeFilter(type)}
        class:bg-blue-500={filterType === type}
        class:text-white={filterType === type}
        class:bg-gray-200={filterType !== type}
        class="px-3 py-1 rounded-full text-sm hover:bg-blue-400"
      >
        {type}
      </button>
    {/each}
    {#if filterType || filterBrewery || searchTerm}
      <button
        on:click={clearFilters}
        class="px-3 py-1 rounded-full text-sm bg-red-500 text-white hover:bg-red-600"
      >
        Clear Filters
      </button>
    {/if}
  </div>

  {#if isLoading}
    <p class="text-center text-gray-500">Loading beers...</p>
  {:else if error}
    <p class="text-center text-red-500">{error}</p>
  {:else if beers.length === 0}
    <p class="text-center text-gray-500">No beers found. Try adjusting your search or filters.</p>
  {:else}
    <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
      {#each beers as beer (beer.id)}
        <BeerCard {beer} on:filterByBrewery={(e) => handleBreweryFilter(e.detail)} />
      {/each}
    </div>
    {#if totalPages > 1}
      <Pagination {currentPage} {totalPages} onPageChange={handlePageChange} />
    {/if}
  {/if}
</div>