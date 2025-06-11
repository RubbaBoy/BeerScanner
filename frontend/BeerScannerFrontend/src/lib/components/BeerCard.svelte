<script lang="ts">
  import type { Beer } from '$lib/types';
  import { currentUser, trackedBeers } from '$lib/stores';
  import { trackBeer, untrackBeer } from '$lib/services/beerService';

  let { beer, showActions = true } = $props<{
    beer: Beer;
    showActions?: boolean;
  }>();

  let isTracking = $state(false);
  let isLoading = $state(false);

  // Check if the user is tracking this beer
  $effect(() => {
    isTracking = $trackedBeers.some(trackedBeer => trackedBeer.id === beer.id);
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
        await untrackBeer(beer.id!);
      } else {
        await trackBeer(beer.id!);
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
        <a href="/beers/{beer.id}" class="hover:text-amber-600 transition-colors">
          {beer.name}
        </a>
      </h3>

      {#if showActions && $currentUser}
        <button 
          onclick={toggleTracking}
          disabled={isLoading}
          class={`flex items-center justify-center h-8 w-8 rounded-full focus:outline-none transition-colors ${
            isTracking ? 'bg-amber-500 text-white hover:bg-amber-600' : 'bg-gray-200 text-gray-600 hover:bg-gray-300'
          }`}
          aria-label={isTracking ? 'Untrack beer' : 'Track beer'}
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

    <div class="flex flex-wrap gap-2 mb-3">
      {#if beer.brewery}
        <span class="inline-block bg-blue-100 text-blue-800 px-2 py-1 rounded text-xs">
          {beer.brewery}
        </span>
      {/if}

      {#if beer.type}
        <span class="inline-block bg-green-100 text-green-800 px-2 py-1 rounded text-xs">
          {beer.type}
        </span>
      {/if}

      {#if beer.abv}
        <span class="inline-block bg-purple-100 text-purple-800 px-2 py-1 rounded text-xs">
          {beer.abv}% ABV
        </span>
      {/if}
    </div>

    {#if beer.description}
      <p class="text-gray-600 mb-4 text-sm line-clamp-2">{beer.description}</p>
    {/if}

    {#if beer.availableAt} <!-- Likely not extended, so we don't care -->
      {#if beer.availableAt.length > 0}
        <div class="mt-4">
          <h4 class="text-sm font-semibold text-gray-700 mb-2">Available at:</h4>
          <div class="flex flex-wrap gap-2">
            {#each beer.availableAt.slice(0, 3) as bar}
              <a
                href="/bars/{bar.id}"
                class="inline-block bg-amber-100 text-amber-800 px-2 py-1 rounded text-xs hover:bg-amber-200 transition-colors"
              >
                {bar.name}
              </a>
            {/each}
            {#if beer.availableAt.length > 3}
              <a
                href="/beers/{beer.id}"
                class="inline-block bg-gray-100 text-gray-800 px-2 py-1 rounded text-xs hover:bg-gray-200 transition-colors"
              >
                +{beer.availableAt.length - 3} more locations
              </a>
            {/if}
          </div>
        </div>
      {:else}
        <p class="text-sm text-gray-500 mt-4">Not currently available at any tracked bars</p>
      {/if}
    {/if}
  </div>
</div>
