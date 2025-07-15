<script lang="ts">
  import { onMount } from 'svelte';
  import { page } from '$app/stores';
  import { getBarById, getCurrentBeers, getPastBeers } from '$lib/services';
  import { trackBar, untrackBar } from '$lib/services/barService';
  import { BeerCard } from '$lib/components';
  import { currentUser, trackedBars } from '$lib/stores';
  import { format } from 'date-fns';
  import {type Bar, type Beer, type BeerAvailability, isAdmin} from '$lib/types';

  const barId = parseInt($page.params.id);

  let bar: Bar | null = $state(null);
  let currentBeers: BeerAvailability[] = $state([]);
  let pastBeers: BeerAvailability[] = $state([]);
  let isLoading = $state(true);
  let error = $state<string | null>(null);
  let activeTab = $state('current');
  let isTracking = $state(false);
  let isTrackingLoading = $state(false);

  // Format the last checked date
  const formatDate = (dateString: string | undefined) => {
    if (!dateString) return 'Never';
    return format(new Date(dateString), 'MMM d, yyyy');
  };

  // Fetch bar data
  const fetchBarData = async () => {
    isLoading = true;
    error = null;

    try {
      // Fetch bar details
      bar = await getBarById(barId);

      // Fetch current beers
      currentBeers = await getCurrentBeers(barId);

      // Fetch past beers
      pastBeers = await getPastBeers(barId);

      // Check if user is tracking this bar
      isTracking = $trackedBars.some(trackedBar => trackedBar.id === barId);
    } catch (e) {
      console.error('Failed to fetch bar data:', e);
      error = 'Failed to load bar data. Please try again later.';
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
        await untrackBar(barId);
      } else {
        await trackBar(barId);
      }
      isTracking = !isTracking;
    } catch (error) {
      console.error('Failed to toggle tracking:', error);
    } finally {
      isTrackingLoading = false;
    }
  };

  // Change active tab
  const setActiveTab = (tab: string) => {
    activeTab = tab;
  };

  // Fetch data on mount
  onMount(() => {
    fetchBarData();
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
  {:else if bar}
    <!-- Bar header -->
    <div class="bg-white rounded-lg shadow-md p-6">
      <div class="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
        <div>
          <h1 class="text-3xl font-bold text-gray-900">{bar.name}</h1>
          <p class="text-gray-600 mt-1">{bar.location}</p>
        </div>

        {#if $currentUser}
          <div class="flex flex-col sm:flex-row gap-2">
              {#if bar && bar.id && isAdmin($currentUser)}
                <a
                        href={`/admin/bars/${bar.id}`}
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
                <span>{isTracking ? 'Tracking' : 'Track Bar'}</span>
              </button>
          </div>
        {/if}
      </div>

      <div class="mt-6 grid grid-cols-1 md:grid-cols-3 gap-4 text-sm">
        <div class="bg-gray-50 p-3 rounded">
          <span class="text-gray-500">Last Updated:</span>
          <span class="font-medium ml-2">{formatDate(bar.lastCheckedAt)}</span>
        </div>
        <div class="bg-gray-50 p-3 rounded">
          <span class="text-gray-500">Current Beers:</span>
          <span class="font-medium ml-2">{currentBeers.length}</span>
        </div>
        <div class="bg-gray-50 p-3 rounded">
          <span class="text-gray-500">Past Beers:</span>
          <span class="font-medium ml-2">{pastBeers.length}</span>
        </div>
      </div>
    </div>

    <!-- Tabs -->
    <div class="border-b border-gray-200">
      <nav class="-mb-px flex space-x-8">
        <button
          class={`py-4 px-1 border-b-2 font-medium text-sm ${
            activeTab === 'current'
              ? 'border-amber-500 text-amber-600'
              : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
          }`}
          onclick={() => setActiveTab('current')}
        >
          Current Beers
        </button>
        <button
          class={`py-4 px-1 border-b-2 font-medium text-sm ${
            activeTab === 'past'
              ? 'border-amber-500 text-amber-600'
              : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
          }`}
          onclick={() => setActiveTab('past')}
        >
          Past Beers
        </button>
      </nav>
    </div>

    <!-- Beer lists -->
    {#if activeTab === 'current'}
      {#if currentBeers.length === 0}
        <div class="bg-gray-100 border border-gray-300 text-gray-700 px-4 py-8 rounded text-center">
          <p>No beers currently available at this bar.</p>
        </div>
      {:else}
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
          {#each currentBeers as beerAvailability}
            <BeerCard beer={beerAvailability.beer} date={beerAvailability.availableAt} dateCurrent={true} />
          {/each}
        </div>
      {/if}
    {:else if activeTab === 'past'}
      {#if pastBeers.length === 0}
        <div class="bg-gray-100 border border-gray-300 text-gray-700 px-4 py-8 rounded text-center">
          <p>No past beers available for this bar.</p>
        </div>
      {:else}
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
          {#each pastBeers as beerAvailability}
            <BeerCard beer={beerAvailability.beer} date={beerAvailability.availableAt} dateCurrent={false} />
          {/each}
        </div>
      {/if}
    {/if}
  {:else}
    <div class="bg-gray-100 border border-gray-300 text-gray-700 px-4 py-8 rounded text-center">
      <p>Bar not found.</p>
    </div>
  {/if}
</div>