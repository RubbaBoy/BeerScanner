<script lang="ts">
  import { onMount } from 'svelte';
  import { goto } from '$app/navigation';
  import { createBar } from '$lib/services';
  import { currentUser } from '$lib/stores';
  import type { Bar } from '$lib/types';
  
  // State variables
  let bar: Bar = $state({
    name: '',
    location: '',
    menuUrl: '',
    menuXPath: ''
  });
  let isSubmitting = $state(false);
  let error = $state<string | null>(null);
  let success = $state<string | null>(null);
  
  // Check if user is admin
  onMount(() => {
    // TODO: Add check for admin role
    if (!$currentUser) {
      goto('/login');
    }
  });
  
  // Handle form submission
  const handleSubmit = async (event: Event) => {
    event.preventDefault();
    isSubmitting = true;
    error = null;
    success = null;
    
    try {
      // TODO: Validate form fields
      
      // Submit the bar request
      const result = await createBar(bar);
      
      // Show success message
      success = `Bar "${result.name}" has been submitted for approval.`;
      
      // Reset form
      bar = {
        name: '',
        location: '',
        menuUrl: '',
        menuXPath: ''
      };
    } catch (e) {
      console.error('Failed to submit bar:', e);
      error = 'Failed to submit bar. Please try again later.';
    } finally {
      isSubmitting = false;
    }
  };
</script>

<div class="space-y-8">
  <!-- Page header -->
  <div>
    <h1 class="text-3xl font-bold text-gray-900">Add New Bar</h1>
    <p class="text-gray-600 mt-1">Create a new bar in the system</p>
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
    
    <form onsubmit={handleSubmit} class="space-y-6">
      <!-- Bar name -->
      <div>
        <label for="name" class="block text-sm font-medium text-gray-700">Bar Name</label>
        <input
          type="text"
          id="name"
          bind:value={bar.name}
          required
          class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-amber-500 focus:border-amber-500"
        />
      </div>
      
      <!-- Location -->
      <div>
        <label for="location" class="block text-sm font-medium text-gray-700">Location</label>
        <input
          type="text"
          id="location"
          bind:value={bar.location}
          required
          class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-amber-500 focus:border-amber-500"
        />
      </div>
      
      <!-- Menu URL -->
      <div>
        <label for="menuUrl" class="block text-sm font-medium text-gray-700">Menu URL</label>
        <input
          type="url"
          id="menuUrl"
          bind:value={bar.menuUrl}
          class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-amber-500 focus:border-amber-500"
        />
        <p class="mt-1 text-sm text-gray-500">URL to the bar's menu page</p>
      </div>
      
      <!-- Menu XPath -->
      <div>
        <label for="menuXPath" class="block text-sm font-medium text-gray-700">Menu XPath</label>
        <input
          type="text"
          id="menuXPath"
          bind:value={bar.menuXPath}
          class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-amber-500 focus:border-amber-500"
        />
        <p class="mt-1 text-sm text-gray-500">XPath to extract beer list from the menu page (optional)</p>
      </div>
      
      <!-- Submit button -->
      <div>
        <button
          type="submit"
          disabled={isSubmitting}
          class="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-amber-600 hover:bg-amber-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-amber-500"
        >
          {#if isSubmitting}
            <span class="animate-spin mr-2">⟳</span> Submitting...
          {:else}
            Add Bar
          {/if}
        </button>
      </div>
    </form>
  </div>
  
  <!-- TODO: Add section for bar approval workflow -->
  <!-- TODO: Add section for managing existing bars -->
</div>