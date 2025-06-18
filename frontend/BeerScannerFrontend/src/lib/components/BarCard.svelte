<script lang="ts">
  import type {Bar} from '$lib/types';
  import {currentUser, trackedBars} from '$lib/stores';
  import {trackBar, untrackBar} from '$lib/services/barService';
  import {formatFromNowDate} from "$lib/utils/formatting";

  let { bar, showActions = true } = $props<{
    bar: Bar;
    showActions?: boolean;
  }>();

  let isTracking = $state(false);
  let isLoading = $state(false);

  // Check if the user is tracking this bar
  $effect(() => {
    isTracking = $trackedBars.some(trackedBar => trackedBar.id === bar.id);
  });

  // Toggle tracking status
  const toggleTracking = async () => {
    if (!$currentUser) {
      window.location.href = '/login';
      return;
    }

    isLoading = true;
    try {
      if (isTracking) {
        await untrackBar(bar.id!);
      } else {
        await trackBar(bar.id!);
      }
      isTracking = !isTracking;
    } catch (error) {
      console.error('Failed to toggle tracking:', error);
    } finally {
      isLoading = false;
    }
  };
</script>

<div class="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition-shadow duration-300">
  <div class="p-6">
    <div class="flex justify-between items-start">
      <h3 class="text-xl font-bold text-gray-900 mb-2">
        <a href="/bars/{bar.id}" class="hover:text-amber-600 transition-colors">
          {bar.name}
        </a>
      </h3>

      {#if showActions && $currentUser}
        <button 
          onclick={toggleTracking}
          disabled={isLoading}
          class={`flex items-center justify-center h-8 w-8 rounded-full focus:outline-none transition-colors ${
            isTracking ? 'bg-amber-500 text-white hover:bg-amber-600' : 'bg-gray-200 text-gray-600 hover:bg-gray-300'
          }`}
          aria-label={isTracking ? 'Untrack bar' : 'Track bar'}
        >
          {#if isLoading}
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
        </button>
      {/if}
    </div>

    <p class="text-gray-600 mb-4">{bar.location}</p>

    <div class="flex justify-between text-sm text-gray-500">
      <div class="flex flex-col items-start">
        <span>Last updated:</span>
        <span>{formatFromNowDate(bar.lastCheckedAt)}</span>
      </div>
      <span>{bar.currentBeerCount || 0} beers available</span>
    </div>


    {#if bar.currentBeers && bar.currentBeers.length > 0}
      <div class="mt-4">
        <h4 class="text-sm font-semibold text-gray-700 mb-2">Featured beers:</h4>
        <div class="flex flex-wrap gap-2">
          {#each bar.currentBeers.slice(0, 3) as beer}
            <a 
              href="/beers/{beer.id}" 
              class="inline-block bg-amber-100 text-amber-800 px-2 py-1 rounded text-xs hover:bg-amber-200 transition-colors"
            >
              {beer.name}
            </a>
          {/each}
          {#if bar.currentBeers.length > 3}
            <a 
              href="/bars/{bar.id}" 
              class="inline-block bg-gray-100 text-gray-800 px-2 py-1 rounded text-xs hover:bg-gray-200 transition-colors"
            >
              +{bar.currentBeers.length - 3} more
            </a>
          {/if}
        </div>
      </div>
    {/if}
  </div>
</div>
