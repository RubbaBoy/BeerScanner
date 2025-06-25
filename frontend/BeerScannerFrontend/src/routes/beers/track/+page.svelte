<script lang="ts">
  import { onMount } from 'svelte';
  import { goto } from '$app/navigation';
  import {searchBeers, trackBeer, requestBeer, getAllBars} from '$lib/services';
  import {currentUser, refreshTrackedBars} from '$lib/stores';
  import type {Beer, Bar, BeerRequest} from '$lib/types';
  import { clickOutside } from '$lib/actions/clickOutside';
  import CreateBeerForm from "$lib/components/CreateBeerForm.svelte";
  import BeerSearch from "$lib/components/BeerSearch.svelte";


  // State variables
  let allBars: Bar[] = $state([]);
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

  // Handle create beer request
  const handleCreateBeer = (beerName: string) => {
    newBeerInitialName = beerName;
    showNewBeerForm = true;
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

                <BeerSearch
                        bind:selectedBeer
                        onCreateBeer={handleCreateBeer}
                        showCreateOption={true}
                        placeholder="Search for a beer..."
                />

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