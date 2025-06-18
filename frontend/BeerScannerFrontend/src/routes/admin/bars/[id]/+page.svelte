<script lang="ts">
  import { onMount } from 'svelte';
  import { page } from '$app/stores';
  import {getBarById, updateBar, checkBar, getBarAdminById} from '$lib/services';
  import type {Bar, BarAdmin, BarCheck} from '$lib/types';

  // Get the bar ID from the URL
  const barId = parseInt($page.params.id);

  // State variables
  let bar: BarAdmin = $state(null);
  let isLoading = $state(true);
  let isChecking = $state(false);
  let isSaving = $state(false);
  let error = $state<string | null>(null);
  let success = $state<string | null>(null);
  let formData = $state<BarAdmin | null>(null);

  // Web scraping toggle and related state variables
  let webScrapingEnabled = $state(false);

  // Fetch bar details
  const fetchBar = async () => {
    isLoading = true;
    error = null;
    
    try {
      bar = await getBarAdminById(barId);
      formData = { ...bar };

      // If any are set, it's in scraping mode
      webScrapingEnabled = !!formData.menuComponentXPath
    } catch (e) {
      console.error('Failed to fetch bar:', e);
      error = 'Failed to load bar details. Please try again later.';
    } finally {
      isLoading = false;
    }
  };

  // Save bar details
  const saveBar = async () => {
    isSaving = true;
    error = null;
    success = null;

    console.log('Cleanupo:');
    console.log(formData.cleanupScript);

    if (webScrapingEnabled) {
      console.log('Web scraping enabled, using component XPath');
      formData.menuXPath = undefined;
    } else {
      console.log('Web scraping disabled, using menu XPath');
      formData.menuComponentXPath = undefined;
      formData.ageVerificationXPath = undefined;
      formData.cleanupScript = undefined;
      formData.processAsText = undefined;
    }
    
    try {
      bar = await updateBar(barId, formData);
      success = 'Bar details updated successfully.';
    } catch (e) {
      console.error('Failed to update bar:', e);
      error = 'Failed to update bar details. Please try again later.';
    } finally {
      isSaving = false;
    }
  };

  // Trigger a menu check
  const triggerCheck = async () => {
    isChecking = true;
    error = null;
    success = null;
    
    try {
      await checkBar(barId);
      success = 'Menu check triggered successfully.';
    } catch (e) {
      console.error('Failed to trigger check:', e);
      error = 'Failed to trigger menu check. Please try again later.';
    } finally {
      isChecking = false;
    }
  };

  // Handle form submission
  const handleSubmit = (event: Event) => {
    event.preventDefault();
    saveBar();
  };

  // Format date
  const formatDate = (dateString: string) => {
    if (!dateString) return 'Never';
    const date = new Date(dateString);
    return date.toLocaleString();
  };

  // Fetch initial data on mount
  onMount(() => {
    fetchBar();
  });
</script>

<div class="space-y-8">
  <!-- Page header -->
  <div class="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
    <div>
      <h1 class="text-3xl font-bold text-gray-900">
        {#if isLoading}
          Loading Bar...
        {:else if bar}
          {bar.name}
        {:else}
          Bar Details
        {/if}
      </h1>
      <p class="text-gray-600 mt-1">Manage bar details and menu scraping</p>
    </div>
    
    <div class="flex flex-col sm:flex-row gap-2">
      <a 
        href={`/admin/bars/${barId}/checks`}
        class="px-4 py-2 bg-gray-100 text-gray-800 rounded-md hover:bg-gray-200 focus:outline-none focus:ring-2 focus:ring-gray-500 focus:ring-offset-2 flex items-center justify-center"
      >
        <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" viewBox="0 0 20 20" fill="currentColor">
          <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm1-12a1 1 0 10-2 0v4a1 1 0 00.293.707l2.828 2.829a1 1 0 101.415-1.415L11 9.586V6z" clip-rule="evenodd" />
        </svg>
        View Check History
      </a>
      
      <button 
        onclick={triggerCheck}
        disabled={isChecking}
        class="px-4 py-2 bg-amber-500 text-white rounded-md hover:bg-amber-600 focus:outline-none focus:ring-2 focus:ring-amber-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center"
      >
        {#if isChecking}
          <svg class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          Checking...
        {:else}
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M4 2a1 1 0 011 1v2.101a7.002 7.002 0 0111.601 2.566 1 1 0 11-1.885.666A5.002 5.002 0 005.999 7H9a1 1 0 010 2H4a1 1 0 01-1-1V3a1 1 0 011-1zm.008 9.057a1 1 0 011.276.61A5.002 5.002 0 0014.001 13H11a1 1 0 110-2h5a1 1 0 011 1v5a1 1 0 11-2 0v-2.101a7.002 7.002 0 01-11.601-2.566 1 1 0 01.61-1.276z" clip-rule="evenodd" />
          </svg>
          Check Menu Now
        {/if}
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

  {#if isLoading}
    <div class="flex justify-center items-center h-64">
      <div class="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-amber-500"></div>
    </div>
  {:else if bar}
    <!-- Menu scraping info -->
    <div class="bg-white rounded-lg shadow-sm p-6">
      <h2 class="text-xl font-semibold text-gray-900 mb-4">Menu Scraping Information</h2>
      
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div>
          <p class="text-gray-600 mb-1">Last checked:</p>
          <p class="font-medium">{formatDate(bar.lastCheckedAt)}</p>
        </div>
        
        <div>
          <p class="text-gray-600 mb-1">Menu URL:</p>
          <p class="font-medium break-all">
            {#if bar.menuUrl}
              <a href="{bar.menuUrl}" target="_blank" class="text-amber-600 hover:text-amber-700">
                {bar.menuUrl}
              </a>
            {:else}
              <span class="text-gray-400">Not set</span>
            {/if}
          </p>
        </div>
        
        <div>
          <p class="text-gray-600 mb-1">Menu XPath:</p>
          <p class="font-medium font-mono text-sm break-all">
            {#if bar.menuXPath}
              {bar.menuXPath}
            {:else}
              <span class="text-gray-400">Not set</span>
            {/if}
          </p>
        </div>
        
        <div>
          <p class="text-gray-600 mb-1">Last menu hash:</p>
          <p class="font-medium font-mono text-sm break-all">
            {#if bar.lastMenuHash}
              {bar.lastMenuHash}
            {:else}
              <span class="text-gray-400">Not available</span>
            {/if}
          </p>
        </div>
      </div>
    </div>
    
    <!-- Bar details form -->
    <form onsubmit={handleSubmit} class="bg-white rounded-lg shadow-sm p-6">
      <h2 class="text-xl font-semibold text-gray-900 mb-4">Bar Details</h2>
      
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
          <label for="location" class="block text-sm font-medium text-gray-700 mb-1">Location</label>
          <input
            type="text"
            id="location"
            bind:value={formData.location}
            required
            class="w-full px-4 py-2 rounded-md border border-gray-300 focus:outline-none focus:ring-2 focus:ring-amber-500 focus:border-transparent"
          />
        </div>
        
        <div>
          <label for="menuUrl" class="block text-sm font-medium text-gray-700 mb-1">Menu URL</label>
          <input
            type="url"
            id="menuUrl"
            bind:value={formData.menuUrl}
            class="w-full px-4 py-2 rounded-md border border-gray-300 focus:outline-none focus:ring-2 focus:ring-amber-500 focus:border-transparent"
          />
        </div>
        
        <!-- Web Scraping Toggle -->
        <div class="flex items-center">
          <input
            type="checkbox"
            id="webScraping"
            bind:checked={webScrapingEnabled}
            class="h-4 w-4 text-amber-600 focus:ring-amber-500 border-gray-300 rounded"
          />
          <label for="webScraping" class="ml-2 block text-sm text-gray-700">Web Scraping</label>
        </div>

        <!-- Conditional Menu XPath inputs -->
        {#if !webScrapingEnabled}
        <div>
          <label for="menuXPath" class="block text-sm font-medium text-gray-700 mb-1">Menu XPath</label>
          <input
            type="text"
            id="menuXPath"
            placeholder="XPath to the menu button"
            bind:value={formData.menuXPath}
            class="w-full px-4 py-2 rounded-md border border-gray-300 focus:outline-none focus:ring-2 focus:ring-amber-500 focus:border-transparent"
          />
        </div>
        {:else}
          <div>
            <label for="menuXPathAdvanced" class="block text-sm font-medium text-gray-700 mb-1">Menu XPath <span class="text-red-500">*</span></label>
            <input
              type="text"
              id="menuComponentXPath"
              placeholder="XPath to the menu element, if the URL is not a direct link"
              bind:value={formData.menuComponentXPath}
              required
              class="w-full px-4 py-2 rounded-md border border-gray-300 focus:outline-none focus:ring-2 focus:ring-amber-500 focus:border-transparent"
            />
          </div>

          <div>
            <label for="ageVerificationXPath" class="block text-sm font-medium text-gray-700 mb-1">Age Verification XPath</label>
            <input
              type="text"
              id="ageVerificationXPath"
              bind:value={formData.ageVerificationXPath}
              class="w-full px-4 py-2 rounded-md border border-gray-300 focus:outline-none focus:ring-2 focus:ring-amber-500 focus:border-transparent"
            />
          </div>

          <div>
            <label for="cleanupScript" class="block text-sm font-medium text-gray-700 mb-1">Cleanup Script</label>
            <textarea
              id="cleanupScript"
              bind:value={formData.cleanupScript}
              rows="6"
              class="w-full px-4 py-2 rounded-md border border-gray-300 focus:outline-none focus:ring-2 focus:ring-amber-500 focus:border-transparent font-mono text-sm"
              placeholder="// JavaScript code for cleanup..."
            ></textarea>
          </div>

          <div class="flex items-center">
            <input
              type="checkbox"
              id="approved"
              bind:checked={formData.processAsText}
              class="h-4 w-4 text-amber-600 focus:ring-amber-500 border-gray-300 rounded"
            />
            <label for="approved" class="ml-2 block text-sm text-gray-700">Process as text <small>If false, an image will be processed of the Menu XPath</small></label>
          </div>
        {/if}

        <div class="flex items-center">
          <input
            type="checkbox"
            id="approved"
            bind:checked={formData.approved}
            class="h-4 w-4 text-amber-600 focus:ring-amber-500 border-gray-300 rounded"
          />
          <label for="approved" class="ml-2 block text-sm text-gray-700">Approved</label>
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
  {/if}
</div>