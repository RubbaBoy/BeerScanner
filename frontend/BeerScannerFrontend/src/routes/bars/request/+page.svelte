<script lang="ts">
  import { onMount } from 'svelte';
  import { goto } from '$app/navigation';
  import { requestBar } from '$lib/services';
  import { currentUser } from '$lib/stores';
  import type { Bar } from '$lib/types';

  // State variables
  let isSubmitting = $state(false);
  let error = $state<string | null>(null);
  let success = $state<string | null>(null);
  
  // Form data
  let formData = $state<Bar>({
    name: '',
    location: '',
    menuUrl: ''
  });

  // Handle form submission
  const handleSubmit = async (event: Event) => {
    event.preventDefault();
    isSubmitting = true;
    error = null;
    success = null;
    
    try {
      await requestBar(formData);
      success = 'Bar request submitted successfully. It will be reviewed by an administrator.';
      formData = {
        name: '',
        location: '',
        menuUrl: ''
      };
    } catch (e) {
      console.error('Failed to submit bar request:', e);
      error = 'Failed to submit bar request. Please try again later.';
    } finally {
      isSubmitting = false;
    }
  };

  // Check if user is logged in
  onMount(() => {
    if (!$currentUser) {
      goto('/login');
    }
  });
</script>

<div class="space-y-8">
  <!-- Page header -->
  <div>
    <h1 class="text-3xl font-bold text-gray-900">Request New Bar</h1>
    <p class="text-gray-600 mt-1">Submit a new bar to be added to BeerScanner</p>
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

  <!-- Request form -->
  <div class="bg-white rounded-lg shadow-sm p-6">
    <form onsubmit={handleSubmit} class="space-y-4">
      <div>
        <label for="name" class="block text-sm font-medium text-gray-700 mb-1">Bar Name</label>
        <input
          type="text"
          id="name"
          bind:value={formData.name}
          required
          class="w-full px-4 py-2 rounded-md border border-gray-300 focus:outline-none focus:ring-2 focus:ring-amber-500 focus:border-transparent"
          placeholder="Enter the bar name"
        />
      </div>
      
      <div>
        <label for="location" class="block text-sm font-medium text-gray-700 mb-1">Location</label>
        <input
          type="text"
          id="location"
          bind:value={formData.location}
          required
          class="w-full px-4 py-2 rounded-md border border-gray-300 focus:outline-none focus:ring-2 focus:ring-amber-500 focus:border-transparent"
          placeholder="Enter the bar location"
        />
      </div>
      
      <div>
        <label for="menuUrl" class="block text-sm font-medium text-gray-700 mb-1">Menu URL (optional)</label>
        <input
          type="url"
          id="menuUrl"
          bind:value={formData.menuUrl}
          class="w-full px-4 py-2 rounded-md border border-gray-300 focus:outline-none focus:ring-2 focus:ring-amber-500 focus:border-transparent"
          placeholder="https://example.com/menu"
        />
        <p class="text-xs text-gray-500 mt-1">If the bar has an online menu, provide the URL here</p>
      </div>
      
      <div class="pt-4">
        <button
          type="submit"
          disabled={isSubmitting}
          class="px-4 py-2 bg-amber-500 text-white rounded-md hover:bg-amber-600 focus:outline-none focus:ring-2 focus:ring-amber-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed flex items-center"
        >
          {#if isSubmitting}
            <svg class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            Submitting...
          {:else}
            Submit Request
          {/if}
        </button>
      </div>
    </form>
  </div>
  
  <div class="bg-gray-100 rounded-lg p-6">
    <h2 class="text-lg font-semibold text-gray-900 mb-2">What happens next?</h2>
    <p class="text-gray-700">
      After you submit a request, our administrators will review it. If approved, the bar will be added to BeerScanner and you'll be able to track beers at this location.
    </p>
    <p class="text-gray-700 mt-2">
      You can view the status of your bar requests in your <a href="/profile#bar-requests" class="text-amber-600 hover:text-amber-700">profile</a>.
    </p>
  </div>
</div>