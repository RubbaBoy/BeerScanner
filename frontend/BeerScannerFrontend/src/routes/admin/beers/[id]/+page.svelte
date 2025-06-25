<script lang="ts">
  import { onMount } from 'svelte';
  import { page } from '$app/stores';
  import { 
    getBeerById, 
    updateBeer, 
    deleteBeer, 
    getBeerAliases, 
    addBeerAlias, 
    deleteBeerAlias 
  } from '$lib/services/beerService';
  import type { Beer, BeerExtended, BeerAlias } from '$lib/types';
  import BeerSearch from "$lib/components/BeerSearch.svelte";

  // Get the beer ID from the URL
  const beerId = parseInt($page.params.id);

  // State variables
  let beer: BeerExtended = $state(null);
  let beerAliases: BeerAlias[] = $state([]);
  let isLoading = $state(true);
  let isSaving = $state(false);
  let isDeleting = $state(false);
  let error = $state<string | null>(null);
  let success = $state<string | null>(null);
  let formData = $state<Beer | null>(null);
  let showDeleteConfirmation = $state(false);

  // New alias form data
  let newAliasName = $state('');
  let newAliasBrewery = $state('');
  let isAddingAlias = $state(false);

  // Merge modal state
  let showMergeModal = $state(false);
  let isMerging = $state(false);
  let mergeWithBeer: Beer | null = $state(null);


  // Fetch beer details
  const fetchBeer = async () => {
    isLoading = true;
    error = null;
    
    try {
      beer = await getBeerById(beerId);
      formData = { ...beer };
      
      // Fetch beer aliases
      beerAliases = await getBeerAliases(beerId);
    } catch (e) {
      console.error('Failed to fetch beer:', e);
      error = 'Failed to load beer details. Please try again later.';
    } finally {
      isLoading = false;
    }
  };

  // Save beer details
  const saveBeer = async () => {
    isSaving = true;
    error = null;
    success = null;
    
    try {
      beer = await updateBeer(beerId, formData);
      success = 'Beer details updated successfully.';
    } catch (e) {
      console.error('Failed to update beer:', e);
      error = 'Failed to update beer details. Please try again later.';
    } finally {
      isSaving = false;
    }
  };

  // Delete beer
  const confirmDeleteBeer = () => {
    showDeleteConfirmation = true;
  };

  const cancelDeleteBeer = () => {
    showDeleteConfirmation = false;
  };

  const performDeleteBeer = async () => {
    isDeleting = true;
    error = null;
    
    try {
      await deleteBeer(beerId);
      window.location.href = '/admin/beers';
    } catch (e) {
      console.error('Failed to delete beer:', e);
      error = 'Failed to delete beer. Please try again later.';
      isDeleting = false;
      showDeleteConfirmation = false;
    }
  };

  // Add beer alias
  const handleAddAlias = async () => {
    if (!newAliasName || !newAliasBrewery) {
      error = 'Both name and brewery are required for an alias.';
      return;
    }

    isAddingAlias = true;
    error = null;
    success = null;
    
    try {
      const newAlias = await addBeerAlias(beerId, newAliasName, newAliasBrewery);
      beerAliases = [...beerAliases, newAlias];
      newAliasName = '';
      newAliasBrewery = '';
      success = 'Beer alias added successfully.';
    } catch (e) {
      console.error('Failed to add beer alias:', e);
      error = 'Failed to add beer alias. Please try again later.';
    } finally {
      isAddingAlias = false;
    }
  };

  // Delete beer alias
  const handleDeleteAlias = async (aliasId: number) => {
    error = null;
    success = null;
    
    try {
      await deleteBeerAlias(aliasId);
      beerAliases = beerAliases.filter(alias => alias.id !== aliasId);
      success = 'Beer alias removed successfully.';
    } catch (e) {
      console.error('Failed to delete beer alias:', e);
      error = 'Failed to delete beer alias. Please try again later.';
    }
  };

  // Merge beer methods
  const openMergeModal = () => {
    showMergeModal = true;
    mergeWithBeer = null;
  };

  const closeMergeModal = () => {
    showMergeModal = false;
    mergeWithBeer = null;
  };

  const performMerge = async () => {
    // if (!mergeTargetId) {
    //   error = 'Please enter a target beer ID.';
    //   return;
    // }

    // const targetId = parseInt(mergeTargetId);
    // if (isNaN(targetId)) {
    //   error = 'Please enter a valid beer ID.';
    //   return;
    // }

    isMerging = true;
    error = null;

    console.log(`Merging beer ${beerId} with target beer ${mergeWithBeer.name} ${mergeWithBeer.id}...`);

    try {
      await mergeBeer(beerId, targetId);
      success = 'Beer merged successfully.';
      closeMergeModal();
      // Redirect to the target beer page
      window.location.href = `/admin/beers/${targetId}`;
    } catch (e) {
      console.error('Failed to merge beer:', e);
      error = 'Failed to merge beer. Please try again later.';
    } finally {
      isMerging = false;
    }
  };

  // Handle escape key for modal
  const handleKeydown = (event: KeyboardEvent) => {
    if (event.key === 'Escape' && showMergeModal) {
      closeMergeModal();
    }
  };

  // Handle form submission
  const handleSubmit = (event: Event) => {
    event.preventDefault();
    saveBeer();
  };

  // Format date
  const formatDate = (dateString: string) => {
    if (!dateString) return 'Never';
    const date = new Date(dateString);
    return date.toLocaleString();
  };

  // Fetch initial data on mount
  onMount(() => {
    fetchBeer();
  });
</script>


<svelte:window on:keydown={handleKeydown} />

<div class="space-y-8">
  <!-- Page header -->
  <div class="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
    <div>
      <h1 class="text-3xl font-bold text-gray-900">
        {#if isLoading}
          Loading Beer...
        {:else if beer}
          {beer.name}
        {:else}
          Beer Details
        {/if}
      </h1>
      <p class="text-gray-600 mt-1">Manage beer details and aliases</p>
    </div>
    
    <div class="flex flex-col sm:flex-row gap-2">
      {#if beer && beer.id}
        <a 
          href={`/beers/${beer.id}`}
          class="px-4 py-2 bg-gray-100 text-gray-800 rounded-md hover:bg-gray-200 focus:outline-none focus:ring-2 focus:ring-gray-500 focus:ring-offset-2 flex items-center justify-center"
          target="_blank"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" viewBox="0 0 20 20" fill="currentColor">
            <path d="M11 3a1 1 0 100 2h2.586l-6.293 6.293a1 1 0 101.414 1.414L15 6.414V9a1 1 0 102 0V4a1 1 0 00-1-1h-5z" />
            <path d="M5 5a2 2 0 00-2 2v8a2 2 0 002 2h8a2 2 0 002-2v-3a1 1 0 10-2 0v3H5V7h3a1 1 0 000-2H5z" />
          </svg>
          View Public Page
        </a>
      {/if}

        <button
                onclick={openMergeModal}
                class="px-4 py-2 bg-amber-500 text-white rounded-md hover:bg-amber-600 focus:outline-none focus:ring-2 focus:ring-amber-500"
        >
            Merge
        </button>

      
      <button 
        onclick={confirmDeleteBeer}
        class="px-4 py-2 bg-red-500 text-white rounded-md hover:bg-red-600 focus:outline-none focus:ring-2 focus:ring-red-500 focus:ring-offset-2 flex items-center justify-center"
      >
        <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" viewBox="0 0 20 20" fill="currentColor">
          <path fill-rule="evenodd" d="M9 2a1 1 0 00-.894.553L7.382 4H4a1 1 0 000 2v10a2 2 0 002 2h8a2 2 0 002-2V6a1 1 0 100-2h-3.382l-.724-1.447A1 1 0 0011 2H9zM7 8a1 1 0 012 0v6a1 1 0 11-2 0V8zm5-1a1 1 0 00-1 1v6a1 1 0 102 0V8a1 1 0 00-1-1z" clip-rule="evenodd" />
        </svg>
        Delete Beer
      </button>
    </div>
  </div>

  <!-- Success message -->
  {#if success}
    <div class="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded relative" role="alert">
      <span class="block sm:inline">{success}</span>
      <button 
        onclick={() => success = null}
        class="absolute top-0 bottom-0 right-0 px-4 py-3"
      >
        <svg class="fill-current h-6 w-6 text-green-500" role="button" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20">
          <title>Close</title>
          <path d="M14.348 14.849a1.2 1.2 0 0 1-1.697 0L10 11.819l-2.651 3.029a1.2 1.2 0 1 1-1.697-1.697l2.758-3.15-2.759-3.152a1.2 1.2 0 1 1 1.697-1.697L10 8.183l2.651-3.031a1.2 1.2 0 1 1 1.697 1.697l-2.758 3.152 2.758 3.15a1.2 1.2 0 0 1 0 1.698z"/>
        </svg>
      </button>
    </div>
  {/if}

  <!-- Error message -->
  {#if error}
    <div class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative" role="alert">
      <span class="block sm:inline">{error}</span>
      <button 
        onclick={() => error = null}
        class="absolute top-0 bottom-0 right-0 px-4 py-3"
      >
        <svg class="fill-current h-6 w-6 text-red-500" role="button" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20">
          <title>Close</title>
          <path d="M14.348 14.849a1.2 1.2 0 0 1-1.697 0L10 11.819l-2.651 3.029a1.2 1.2 0 1 1-1.697-1.697l2.758-3.15-2.759-3.152a1.2 1.2 0 1 1 1.697-1.697L10 8.183l2.651-3.031a1.2 1.2 0 1 1 1.697 1.697l-2.758 3.152 2.758 3.15a1.2 1.2 0 0 1 0 1.698z"/>
        </svg>
      </button>
    </div>
  {/if}

  <!-- Delete confirmation modal -->
  {#if showDeleteConfirmation}
    <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg p-6 max-w-md w-full">
        <h3 class="text-xl font-bold text-gray-900 mb-4">Confirm Deletion</h3>
        <p class="text-gray-700 mb-6">Are you sure you want to delete this beer? This action cannot be undone.</p>
        <div class="flex justify-end space-x-3">
          <button 
            onclick={cancelDeleteBeer}
            class="px-4 py-2 bg-gray-200 text-gray-800 rounded-md hover:bg-gray-300 focus:outline-none focus:ring-2 focus:ring-gray-500 focus:ring-offset-2"
          >
            Cancel
          </button>
          <button 
            onclick={performDeleteBeer}
            disabled={isDeleting}
            class="px-4 py-2 bg-red-500 text-white rounded-md hover:bg-red-600 focus:outline-none focus:ring-2 focus:ring-red-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed flex items-center"
          >
            {#if isDeleting}
              <svg class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              Deleting...
            {:else}
              Delete
            {/if}
          </button>
        </div>
      </div>
    </div>
  {/if}

  {#if isLoading}
    <div class="flex justify-center items-center h-64">
      <div class="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-amber-500"></div>
    </div>
  {:else if beer}
    <!-- Beer information -->
    <div class="bg-white rounded-lg shadow-sm p-6">
      <h2 class="text-xl font-semibold text-gray-900 mb-4">Beer Information</h2>
      
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div>
          <p class="text-gray-600 mb-1">ID:</p>
          <p class="font-medium">{beer.id}</p>
        </div>
        
        <div>
          <p class="text-gray-600 mb-1">Created At:</p>
          <p class="font-medium">{formatDate(beer.createdAt)}</p>
        </div>
        
        <div>
          <p class="text-gray-600 mb-1">Updated At:</p>
          <p class="font-medium">{formatDate(beer.updatedAt)}</p>
        </div>
      </div>
    </div>
    
    <!-- Beer details form -->
    <form onsubmit={handleSubmit} class="bg-white rounded-lg shadow-sm p-6">
      <h2 class="text-xl font-semibold text-gray-900 mb-4">Beer Details</h2>
      
      <div class="space-y-4">
        <div>
          <label for="name" class="block text-sm font-medium text-gray-700 mb-1">Name</label>
          <input
            type="text"
            id="name"
            bind:value={formData.name}
            required
            class="w-full px-4 py-2 rounded-md border border-gray-300 focus:outline-none focus:ring-2 focus:ring-amber-500 focus:border-transparent"
          />
        </div>
        
        <div>
          <label for="brewery" class="block text-sm font-medium text-gray-700 mb-1">Brewery</label>
          <input
            type="text"
            id="brewery"
            bind:value={formData.brewery}
            class="w-full px-4 py-2 rounded-md border border-gray-300 focus:outline-none focus:ring-2 focus:ring-amber-500 focus:border-transparent"
          />
        </div>
      </div>
      
      <div class="mt-6">
        <button
          type="submit"
          disabled={isSaving}
          class="px-4 py-2 bg-amber-500 text-white rounded-md hover:bg-amber-600 focus:outline-none focus:ring-2 focus:ring-amber-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed flex items-center"
        >
          {#if isSaving}
            <svg class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            Saving...
          {:else}
            Save Changes
          {/if}
        </button>
      </div>
    </form>

    <!-- Beer aliases section -->
    <div class="bg-white rounded-lg shadow-sm p-6">
      <h2 class="text-xl font-semibold text-gray-900 mb-4">Beer Aliases</h2>
      
      <!-- Add new alias form -->
      <div class="mb-6 p-4 bg-gray-50 rounded-md">
        <h3 class="text-lg font-medium text-gray-900 mb-3">Add New Alias</h3>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label for="aliasName" class="block text-sm font-medium text-gray-700 mb-1">Name</label>
            <input
              type="text"
              id="aliasName"
              bind:value={newAliasName}
              required
              class="w-full px-4 py-2 rounded-md border border-gray-300 focus:outline-none focus:ring-2 focus:ring-amber-500 focus:border-transparent"
            />
          </div>
          
          <div>
            <label for="aliasBrewery" class="block text-sm font-medium text-gray-700 mb-1">Brewery</label>
            <input
              type="text"
              id="aliasBrewery"
              bind:value={newAliasBrewery}
              required
              class="w-full px-4 py-2 rounded-md border border-gray-300 focus:outline-none focus:ring-2 focus:ring-amber-500 focus:border-transparent"
            />
          </div>
        </div>
        
        <div class="mt-4">
          <button
            type="button"
            onclick={handleAddAlias}
            disabled={isAddingAlias || (!newAliasName && !newAliasBrewery)}
            class="px-4 py-2 bg-amber-500 text-white rounded-md hover:bg-amber-600 focus:outline-none focus:ring-2 focus:ring-amber-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed flex items-center"
          >
            {#if isAddingAlias}
              <svg class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              Adding...
            {:else}
              Add Alias
            {/if}
          </button>
        </div>
      </div>
      
      <!-- Aliases list -->
      {#if beerAliases.length === 0}
        <div class="text-gray-500 text-center py-4">No aliases found for this beer.</div>
      {:else}
        <div class="overflow-x-auto">
          <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
              <tr>
                <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Name</th>
                <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Brewery</th>
                <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Created At</th>
                <th scope="col" class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              {#each beerAliases as alias}
                <tr>
                  <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{alias.name}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{alias.brewery}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{formatDate(alias.createdAt)}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                    <button
                      onclick={() => handleDeleteAlias(alias.id)}
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
      {/if}
    </div>
  {/if}
</div>

{#if showMergeModal}
    <div class="fixed inset-0 bg-gray-600/50 overflow-y-auto h-full w-full z-50">
        <div class="relative top-20 mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">
            <div class="mt-3 text-center">
                <h3 class="text-lg leading-6 font-medium text-gray-900">Merge Beer</h3>
                <div class="mt-2 px-7 py-3">
                    <p class="text-sm text-gray-500 mb-4">
                        Search for the beer to merge with. The selected beer will be replaced, and all aliases will be transferred.
                    </p>
                  <BeerSearch
                          bind:selectedBeer={mergeWithBeer}
                          showCreateOption={false}
                          placeholder="Search for a beer..."
                          disabled={isMerging}
                  />
                </div>
                <div class="items-center px-4 py-3">
                    <button
                            onclick={performMerge}
                            disabled={isMerging}
                            class="px-4 py-2 bg-amber-500 text-white text-base font-medium rounded-md w-24 mr-2 hover:bg-amber-600 focus:outline-none focus:ring-2 focus:ring-amber-500 disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                        {isMerging ? 'Merging...' : 'OK'}
                    </button>
                    <button
                            onclick={closeMergeModal}
                            disabled={isMerging}
                            class="px-4 py-2 bg-gray-300 text-gray-700 text-base font-medium rounded-md w-24 hover:bg-gray-400 focus:outline-none focus:ring-2 focus:ring-gray-500 disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                        Cancel
                    </button>
                </div>
            </div>
        </div>
    </div>
{/if}
