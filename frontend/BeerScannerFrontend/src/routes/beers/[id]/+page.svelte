<script lang="ts">
  import { onMount } from 'svelte';
  import { page } from '$app/stores';
  import { getBeerById, getBarsWithBeer } from '$lib/services';
  import { trackBeer, untrackBeer } from '$lib/services/beerService';
  import { BarCard } from '$lib/components';
  import { currentUser, trackedBeers } from '$lib/stores';
  import {type Beer, type Bar, type BeerExtended, isAdmin} from '$lib/types';
  
  const beerId = parseInt($page.params.id);
  
  let beer: BeerExtended | null = $state(null);
  let availableBars: Bar[] = $state([]);
  let isLoading = $state(true);
  let error = $state<string | null>(null);
  let isTracking = $state(false);
  let isTrackingLoading = $state(false);
  
  // Fetch beer data
  const fetchBeerData = async () => {
    isLoading = true;
    error = null;
    
    try {
      // Fetch beer details
      beer = await getBeerById(beerId);
      
      // Fetch bars where this beer is available
      availableBars = await getBarsWithBeer(beerId);
      
      // Check if user is tracking this beer
      isTracking = $trackedBeers.some(trackedBeer => trackedBeer.id === beerId);
    } catch (e) {
      console.error('Failed to fetch beer data:', e);
      error = 'Failed to load beer data. Please try again later.';
    } finally {
      isLoading = false;
    }
  };
  
  // Toggle tracking status
  const toggleTracking = async () => {
    if (!$currentUser) {
      window.location.href = '/login';
      return;
    }
    
    isTrackingLoading = true;
    try {
      if (isTracking) {
        await untrackBeer(beerId);
      } else {
        await trackBeer(beerId);
      }
      isTracking = !isTracking;
    } catch (error) {
      console.error('Failed to toggle tracking:', error);
    } finally {
      isTrackingLoading = false;
    }
  };
  
  // Fetch data on mount
  onMount(() => {
    fetchBeerData();
  });
</script>

<div class="space-y-8">
  {#if isLoading}
    <div class="flex justify-center items-center h-64">
      <div class="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-amber-500"></div>
    </div>
  {:else if error}
    <div class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
      <p>{error}</p>
    </div>
  {:else if beer}
    <!-- Beer header -->
    <div class="bg-white rounded-lg shadow-md p-6">
      <div class="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
        <div>
          <h1 class="text-3xl font-bold text-gray-900">{beer.name}</h1>
          {#if beer.brewery}
            <p class="text-gray-600 mt-1">{beer.brewery}</p>
          {/if}
        </div>

        {#if $currentUser}
          <div class="flex flex-col sm:flex-row gap-2">
            {#if beer && beer.id && isAdmin($currentUser)}
              <a
                      href={`/admin/beers/${beer.id}`}
                      class="px-4 py-2 bg-gray-100 text-gray-800 rounded-md hover:bg-gray-200 focus:outline-none focus:ring-2 focus:ring-gray-500 focus:ring-offset-2 flex items-center justify-center"
              >
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" viewBox="0 0 20 20" fill="currentColor">
                  <path d="M11 3a1 1 0 100 2h2.586l-6.293 6.293a1 1 0 101.414 1.414L15 6.414V9a1 1 0 102 0V4a1 1 0 00-1-1h-5z" />
                  <path d="M5 5a2 2 0 00-2 2v8a2 2 0 002 2h8a2 2 0 002-2v-3a1 1 0 10-2 0v3H5V7h3a1 1 0 000-2H5z" />
                </svg>
                View Admin Page
              </a>
            {/if}

            <button
              onclick={toggleTracking}
              disabled={isTrackingLoading}
              class={`flex items-center space-x-2 px-4 py-2 rounded-md transition-colors ${
                isTracking
                  ? 'bg-amber-500 text-white hover:bg-amber-600'
                  : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
              }`}
            >
              {#if isTrackingLoading}
                <span class="animate-spin">⟳</span>
              {:else}
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                  {#if isTracking}
                    <path fill-rule="evenodd" d="M3.172 5.172a4 4 0 015.656 0L10 6.343l1.172-1.171a4 4 0 115.656 5.656L10 17.657l-6.828-6.829a4 4 0 010-5.656z" clip-rule="evenodd" />
                  {:else}
                    <path fill-rule="evenodd" d="M3.172 5.172a4 4 0 015.656 0L10 6.343l1.172-1.171a4 4 0 115.656 5.656L10 17.657l-6.828-6.829a4 4 0 010-5.656z" clip-rule="evenodd" stroke-width="1" stroke="currentColor" fill="none" />
                  {/if}
                </svg>
              {/if}
              <span>{isTracking ? 'Tracking' : 'Track Beer'}</span>
            </button>
          </div>
        {/if}
      </div>
      
      <div class="mt-6">
        <div class="flex flex-wrap gap-3 mb-4">
          {#if beer.type}
            <span class="inline-block bg-green-100 text-green-800 px-3 py-1 rounded-full text-sm">
              {beer.type}
            </span>
          {/if}
          
          {#if beer.abv}
            <span class="inline-block bg-purple-100 text-purple-800 px-3 py-1 rounded-full text-sm">
              {beer.abv}% ABV
            </span>
          {/if}
        </div>
        
        {#if beer.description}
          <div class="mt-4">
            <h2 class="text-lg font-semibold text-gray-900 mb-2">Description</h2>
            <p class="text-gray-700">{beer.description}</p>
          </div>
        {/if}
      </div>
    </div>
    
    <!-- Available at section -->
    <div>
      <h2 class="text-2xl font-bold text-gray-900 mb-4">Available At</h2>
      
      {#if availableBars.length === 0}
        <div class="bg-gray-100 border border-gray-300 text-gray-700 px-4 py-8 rounded text-center">
          <p>This beer is not currently available at any tracked bars.</p>
          {#if $currentUser}
            <p class="mt-2">
              <button 
                onclick={toggleTracking}
                disabled={isTrackingLoading}
                class="text-amber-600 hover:text-amber-700 font-medium"
              >
                {isTracking ? 'Untrack this beer' : 'Track this beer'} to get notified when it becomes available.
              </button>
            </p>
          {/if}
        </div>
      {:else}
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {#each availableBars as bar}
            <BarCard {bar} />
          {/each}
        </div>
      {/if}
    </div>
    
    <!-- Previously available at section -->
    {#if beer.previouslyAvailableAt && beer.previouslyAvailableAt.length > 0}
      <div>
        <h2 class="text-2xl font-bold text-gray-900 mb-4">Previously Available At</h2>
        
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {#each beer.previouslyAvailableAt as bar}
            <BarCard {bar} />
          {/each}
        </div>
      </div>
    {/if}
  {:else}
    <div class="bg-gray-100 border border-gray-300 text-gray-700 px-4 py-8 rounded text-center">
      <p>Beer not found.</p>
    </div>
  {/if}
</div>