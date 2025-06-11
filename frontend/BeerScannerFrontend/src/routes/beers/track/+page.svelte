<script lang="ts">
  import { onMount } from 'svelte';
  import { goto } from '$app/navigation';
  import {searchBeers, trackBeer, requestBeer, getAllBars} from '$lib/services';
  import {currentUser, refreshTrackedBars} from '$lib/stores';
  import type {Beer, Bar, BeerRequest} from '$lib/types';
  import { clickOutside } from '$lib/actions/clickOutside';
  import CreateBeerForm from "$lib/components/CreateBeerForm.svelte";


  // State variables
  let searchTerm = $state('');
  let searchResults: Beer[] = $state([]);
  let allBars: Bar[] = $state([]);
  let isSearching = $state(false);
  let selectedBeer: Beer | null = $state(null);
  let selectedBar: Bar | null = $state(null);
  let isLoading = $state(true);
  let isSubmitting = $state(false);
  let error = $state<string | null>(null);
  let success = $state<string | null>(null);
  let showNewBeerForm = $state(false);
  let newBeerInitialName = $state('');

  // Check if user is logged in
  onMount(async () => {
    if (!$currentUser) {
      goto('/login');
      return;
    }

    try {
      getAllBars().then(bars => {
        console.log('Fetched bars:', bars);
        return allBars = bars.content;
      })
    } catch (e) {
      console.error('Failed to fetch tracked bars:', e);
      error = 'Failed to load your tracked bars. Please try again later.';
    } finally {
      isLoading = false;
    }
  });

  // Search for beers
  const handleSearch = async () => {
    if (!searchTerm.trim()) {
      searchResults = []; // Clear results if search term is empty
      return;
    }

    isSearching = true;
    error = null;

    try {
      const response = await searchBeers(searchTerm, { page: 0, size: 10, sort: ['name,asc'] });
      searchResults = response.content;

      // We don't set an error message for no results anymore
      // since we're showing a "Request New Beer" button instead
    } catch (e) {
      console.error('Failed to search beers:', e);
      error = 'Failed to search beers. Please try again later.';
    } finally {
      isSearching = false;
    }
  };

  // Select a beer from search results
  const selectBeer = (beer: Beer) => {
    selectedBeer = beer;
    searchResults = [];
    searchTerm = '';
  };

  const closeSearchResultsPopup = () => {
    console.log('Closing search results popup');
    searchResults = [];
    searchTerm = '';
  };


  // Select a bar
  const selectBar = (bar: Bar | null) => {
    selectedBar = bar;
  };

  // Track the selected beer
  const handleTrackBeer = async () => {
    if (!selectedBeer) {
      error = 'Please select a beer to track.';
      return;
    }

    isSubmitting = true;
    error = null;
    success = null;

    try {
      // If selectedBar is null, track at any bar
      // If selectedBar is set, track at that specific bar
      await trackBeer(selectedBeer.id, selectedBar?.id);

      success = selectedBar
        ? `You are now tracking "${selectedBeer.name}" at ${selectedBar.name}.`
        : `You are now tracking "${selectedBeer.name}" at any bar.`;

      // Reset form
      selectedBeer = null;
      selectedBar = null;
    } catch (e) {
      console.error('Failed to track beer:', e);
      error = 'Failed to track beer. Please try again later.';
    } finally {
      isSubmitting = false;
    }
  };

  // Show the new beer form
  const showRequestNewBeerForm = () => {
    showNewBeerForm = true;

    // Pre-fill the name with the search term
    newBeerInitialName = searchTerm;
    error = null;
    closeSearchResultsPopup();
  };

  // Request and track a new beer
  const handleRequestNewBeer = async (event: CustomEvent<BeerRequest>) => {
    let newBeer: BeerRequest = event.detail;

    if (!newBeer.name.trim()) {
      error = 'Please enter a beer name.';
      return;
    }

    isSubmitting = true;
    error = null;
    success = null;

    try {
      // Request and track the new beer
      await requestBeer(newBeer, selectedBar?.id);

      // TODO: Actually track it

      success = selectedBar
        ? `You have requested and are now tracking "${newBeer.name}" at ${selectedBar.name}.`
        : `You have requested and are now tracking "${newBeer.name}" at any bar.`;

      // Reset form
      showNewBeerForm = false;
      selectedBar = null;
    } catch (e) {
      console.error('Failed to request new beer:', e);
      error = 'Failed to request new beer. Please try again later.';
    } finally {
      isSubmitting = false;
    }
  };
</script>

<div class="space-y-8">
    <!-- Page header -->
    <div>
        <h1 class="text-3xl font-bold text-gray-900">Track a Beer</h1>
        <p class="text-gray-600 mt-1">Get notified when your favorite beers become available</p>
    </div>

    <!-- Form -->
    <div class="bg-white rounded-lg shadow-md p-6">
        {#if error}
            <div class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
                <p>{error}</p>
            </div>
        {/if}

        {#if success}
            <div class="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded mb-4">
                <p>{success}</p>
            </div>
        {/if}

        <div class="space-y-6">
            <!-- Beer selection -->
            <div>
                <label for="beer" class="block text-sm font-medium text-gray-700 mb-1">Select a Beer</label>

                {#if selectedBeer}
                    <div class="flex items-center justify-between p-3 border rounded-md bg-gray-50">
                        <div>
                            <span class="font-medium">{selectedBeer.name}</span>
                            {#if selectedBeer.brewery}
                                <span class="text-gray-500 ml-2">by {selectedBeer.brewery}</span>
                            {/if}
                        </div>
                        <button
                                type="button"
                                onclick={() => selectedBeer = null}
                                class="text-gray-500 hover:text-gray-700"
                        >
                            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                                <path fill-rule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clip-rule="evenodd" />
                            </svg>
                        </button>
                    </div>
                {:else}
                    <div class="relative">
                        <input
                                type="text"
                                id="beer"
                                placeholder="Search for a beer..."
                                bind:value={searchTerm}
                                oninput={handleSearch}
                                class="block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-amber-500 focus:border-amber-500"
                        />

                        {#if isSearching}
                            <div class="absolute right-3 top-1/2 transform -translate-y-1/2">
                                <div class="animate-spin rounded-full h-4 w-4 border-t-2 border-b-2 border-amber-500"></div>
                            </div>
                        {/if}

                        {#if searchResults.length > 0}
                            <div class="absolute z-10 mt-1 w-full bg-white shadow-lg rounded-md border border-gray-300 max-h-60 overflow-auto" use:clickOutside={closeSearchResultsPopup}>
                                <ul class="py-1">
                                    {#each searchResults as beer}
                                        <li>
                                            <button
                                                    type="button"
                                                    onclick={() => selectBeer(beer)}
                                                    class="w-full text-left px-4 py-2 hover:bg-gray-100"
                                            >
                                                <div class="font-medium">{beer.name}</div>
                                                {#if beer.brewery}
                                                    <div class="text-sm text-gray-500">{beer.brewery}</div>
                                                {/if}
                                            </button>
                                        </li>
                                    {/each}
                                </ul>
                            </div>
                        {:else if searchTerm.trim() && !isSearching && searchResults.length === 0}
                            <div class="absolute z-10 mt-1 w-full bg-white shadow-lg rounded-md border border-gray-300" use:clickOutside={closeSearchResultsPopup}>
                                <div class="p-4 text-center">
                                    <p class="text-gray-600 mb-3">No beers found matching "{searchTerm}".</p>
                                    <button
                                            type="button"
                                            onclick={showRequestNewBeerForm}
                                            class="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-amber-600 hover:bg-amber-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-amber-500"
                                    >
                                        <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" viewBox="0 0 20 20" fill="currentColor">
                                            <path fill-rule="evenodd" d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11-2 0v-5H4a1 1 0 110-2h5V4a1 1 0 011-1z" clip-rule="evenodd" />
                                        </svg>
                                        Request New Beer
                                    </button>
                                </div>
                            </div>
                        {/if}
                    </div>
                {/if}
            </div>

            <!-- Bar selection -->
            <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Where to Track</label>

                <div class="space-y-2">
                    <div class="flex items-center">
                        <input
                                type="radio"
                                id="anyBar"
                                name="trackingType"
                                checked={selectedBar === null}
                                onchange={() => selectBar(null)}
                                class="h-4 w-4 text-amber-600 focus:ring-amber-500 border-gray-300"
                        />
                        <label for="anyBar" class="ml-2 block text-sm text-gray-700">
                            Track at any bar (get notified when this beer appears anywhere)
                        </label>
                    </div>

                    <div class="flex items-center">
                        <input
                                type="radio"
                                id="specificBar"
                                name="trackingType"
                                checked={selectedBar !== null}
                                disabled={allBars.length === 0}
                                class="h-4 w-4 text-amber-600 focus:ring-amber-500 border-gray-300"
                        />
                        <label for="specificBar" class="ml-2 block text-sm text-gray-700">
                            Track at a specific bar
                        </label>
                    </div>

                    {#if allBars.length > 0}
                        <div class="ml-6 mt-2">
                            <select
                                    bind:value={selectedBar}
                                    class="block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-amber-500 focus:border-amber-500"
                            >
                                <option value={null}>Select a bar...</option>
                                {#each allBars as bar}
                                    <option value={bar}>{bar.name}</option>
                                {/each}
                            </select>
                        </div>
                    {/if}
                </div>
            </div>

            <!-- Submit button -->
            <div>
                <button
                        type="button"
                        onclick={handleTrackBeer}
                        disabled={!selectedBeer || isSubmitting}
                        class="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-amber-600 hover:bg-amber-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-amber-500 disabled:opacity-50 disabled:cursor-not-allowed"
                >
                    {#if isSubmitting}
                        <span class="animate-spin mr-2">⟳</span> Tracking...
                    {:else}
                        Track Beer
                    {/if}
                </button>
            </div>
        </div>
    </div>

    <!-- New Beer Form -->
    {#if showNewBeerForm}
        <CreateBeerForm initialName={newBeerInitialName} on:beerCreated={handleRequestNewBeer} />
    {/if}
</div>