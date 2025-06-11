<script lang="ts">
  import { onMount } from 'svelte';
  import { goto } from '$app/navigation';
  import {
    getScraperSettings,
    updateScraperSettings,
    getScraperStats,
    getScraperStatsForBar,
    resetScraperStats,
    enableScraper,
    disableScraper,
    type ScraperSettings,
    type ScraperStats,
    getAllBars
  } from '$lib/services';
  import { currentUser } from '$lib/stores';
  import { format } from 'date-fns';
  import {formatFromNowDate} from "$lib/utils/formatting";

  // State variables
  let settings: ScraperSettings = $state(null);
  let stats: ScraperStats = $state(null);
  let isLoading = $state(true);
  let isSaving = $state(false);
  let isResetting = $state(false);
  let isTogglingState = $state(false);
  let error = $state<string | null>(null);
  let success = $state<string | null>(null);
  
  // Form data
  let formData: ScraperSettings = $state(null);

  // Bar selector state
  let selectedBar = $state('all');
  let allBars = $state<Bar[]>([]);

  // Extract available bars from stats
  const updateAvailableBars = () => {
    // TODO: When bar count is > 100
    getAllBars({ page: 0, size: 100, sort: ['name,asc'] })
      .then(response => allBars = response.content)
      .catch(err => {
        console.error('Failed to fetch bars:', err);
        return [];
      });

    fetchState()
  };

  // Fetch scraper settings and stats
  const fetchData = async () => {
    isLoading = true;
    error = null;
    
    try {
      // Fetch settings
      settings = await getScraperSettings();
      formData = { ...settings };
      
      // Fetch stats
      await fetchState()
      // updateAvailableBars();
    } catch (e) {
      console.error('Failed to fetch scraper data:', e);
      error = 'Failed to load scraper settings. Please try again later.';
    } finally {
      isLoading = false;
    }
  };

  const fetchState = async () => {
    if (selectedBar == 'all') {
      stats = await getScraperStats();
    } else {
      stats = await getScraperStatsForBar(selectedBar);
    }
  }

  // Save settings
  const saveSettings = async (event: Event) => {
    event.preventDefault();
    isSaving = true;
    error = null;
    success = null;
    
    try {
      settings = await updateScraperSettings(formData);
      success = 'Scraper settings updated successfully.';
    } catch (e) {
      console.error('Failed to update settings:', e);
      error = 'Failed to update scraper settings. Please try again later.';
    } finally {
      isSaving = false;
    }
  };

  // Reset stats
  const handleResetStats = async () => {
    if (!confirm('Are you sure you want to reset scraper statistics? This action cannot be undone.')) {
      return;
    }
    
    isResetting = true;
    error = null;
    success = null;
    
    try {
      await resetScraperStats();
      success = 'Scraper statistics reset successfully.';
      // Refresh stats

      await fetchState();
      // updateAvailableBars();
    } catch (e) {
      console.error('Failed to reset stats:', e);
      error = 'Failed to reset scraper statistics. Please try again later.';
    } finally {
      isResetting = false;
    }
  };

  // Toggle scraper state (enable/disable)
  const toggleScraperState = async () => {
    isTogglingState = true;
    error = null;
    success = null;
    
    try {
      if (settings.enabled) {
        settings = await disableScraper();
        success = 'Scraper disabled successfully.';
      } else {
        settings = await enableScraper();
        success = 'Scraper enabled successfully.';
      }
      formData = { ...settings };
    } catch (e) {
      console.error('Failed to toggle scraper state:', e);
      error = 'Failed to update scraper state. Please try again later.';
    } finally {
      isTogglingState = false;
    }
  };

  // Format date
  const formatDate = (dateString: string) => {
    if (!dateString) return 'N/A';
    return format(new Date(dateString), 'MMM d, yyyy HH:mm:ss');
  };

  // Format time (milliseconds to readable format)
  const formatTime = (ms: number) => {
    if (ms < 1000) return `${ms}ms`;
    return `${(ms / 1000).toFixed(2)}s`;
  };

  // Check if user is admin
  onMount(() => {
    // TODO: Add check for admin role
    if (!$currentUser) {
      goto('/login');
      return;
    }

    updateAvailableBars();
    fetchData();
  });
</script>

<div class="space-y-8">
  <!-- Page header -->
  <div class="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
    <div>
      <h1 class="text-3xl font-bold text-gray-900">Menu Scraper Settings</h1>
      <p class="text-gray-600 mt-1">Configure and monitor the menu scraper</p>
    </div>
    
    {#if settings}
      <button 
        onclick={toggleScraperState}
        disabled={isTogglingState}
        class={`px-4 py-2 ${settings.enabled ? 'bg-red-500 hover:bg-red-600' : 'bg-green-500 hover:bg-green-600'} text-white rounded-md focus:outline-none focus:ring-2 focus:ring-offset-2 ${settings.enabled ? 'focus:ring-red-500' : 'focus:ring-green-500'} disabled:opacity-50 disabled:cursor-not-allowed flex items-center`}
      >
        {#if isTogglingState}
          <svg class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          Updating...
        {:else if settings.enabled}
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M13.477 14.89A6 6 0 015.11 6.524l8.367 8.368zm1.414-1.414L6.524 5.11a6 6 0 018.367 8.367zM18 10a8 8 0 11-16 0 8 8 0 0116 0z" clip-rule="evenodd" />
          </svg>
          Disable Scraper
        {:else}
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
          </svg>
          Enable Scraper
        {/if}
      </button>
    {/if}
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
  {:else}
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
      <!-- Settings form -->
      <div class="bg-white rounded-lg shadow-sm p-6">
        <h2 class="text-xl font-semibold text-gray-900 mb-4">Scraper Configuration</h2>
        
        <form onsubmit={saveSettings} class="space-y-4">
          <div>
            <label for="checkInterval" class="block text-sm font-medium text-gray-700 mb-1">Check Interval (minutes)</label>
            <input
              type="number"
              id="checkInterval"
              bind:value={formData.checkInterval}
              min="1"
              required
              class="w-full px-4 py-2 rounded-md border border-gray-300 focus:outline-none focus:ring-2 focus:ring-amber-500 focus:border-transparent"
            />
            <p class="text-xs text-gray-500 mt-1">How often the scraper checks bar menus</p>
          </div>
          
          <div>
            <label for="maxRetries" class="block text-sm font-medium text-gray-700 mb-1">Max Retries</label>
            <input
              type="number"
              id="maxRetries"
              bind:value={formData.maxRetries}
              min="0"
              required
              class="w-full px-4 py-2 rounded-md border border-gray-300 focus:outline-none focus:ring-2 focus:ring-amber-500 focus:border-transparent"
            />
            <p class="text-xs text-gray-500 mt-1">Number of times to retry failed requests</p>
          </div>
          
          <div>
            <label for="retryDelay" class="block text-sm font-medium text-gray-700 mb-1">Retry Delay (seconds)</label>
            <input
              type="number"
              id="retryDelay"
              bind:value={formData.retryDelay}
              min="1"
              required
              class="w-full px-4 py-2 rounded-md border border-gray-300 focus:outline-none focus:ring-2 focus:ring-amber-500 focus:border-transparent"
            />
            <p class="text-xs text-gray-500 mt-1">Delay between retry attempts</p>
          </div>
          
          <div>
            <label for="timeout" class="block text-sm font-medium text-gray-700 mb-1">Request Timeout (seconds)</label>
            <input
              type="number"
              id="timeout"
              bind:value={formData.timeout}
              min="1"
              required
              class="w-full px-4 py-2 rounded-md border border-gray-300 focus:outline-none focus:ring-2 focus:ring-amber-500 focus:border-transparent"
            />
            <p class="text-xs text-gray-500 mt-1">Maximum time to wait for a response</p>
          </div>
          
          <div>
            <label for="userAgent" class="block text-sm font-medium text-gray-700 mb-1">User Agent</label>
            <input
              type="text"
              id="userAgent"
              bind:value={formData.userAgent}
              required
              class="w-full px-4 py-2 rounded-md border border-gray-300 focus:outline-none focus:ring-2 focus:ring-amber-500 focus:border-transparent"
            />
            <p class="text-xs text-gray-500 mt-1">User agent string for HTTP requests</p>
          </div>
          
          <div class="flex items-center">
            <input
              type="checkbox"
              id="notifyOnFailure"
              bind:checked={formData.notifyOnFailure}
              class="h-4 w-4 text-amber-600 focus:ring-amber-500 border-gray-300 rounded"
            />
            <label for="notifyOnFailure" class="ml-2 block text-sm text-gray-700">Notify on Failure</label>
          </div>
          
          <div class="pt-4">
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
                Save Settings
              {/if}
            </button>
          </div>
        </form>
        
        {#if settings.lastUpdated}
          <p class="text-xs text-gray-500 mt-4">Last updated: {formatDate(settings.lastUpdated)}</p>
        {/if}
      </div>
      
      <!-- Statistics -->
      {#if stats}
        <div class="bg-white rounded-lg shadow-sm p-6">
          <div class="flex justify-between items-center mb-4">
            <h2 class="text-xl font-semibold text-gray-900">Scraper Statistics</h2>

            <div class="flex items-center space-x-2">
              <label for="bar-selector" class="text-sm font-medium text-gray-700">
                Filter by bar:
              </label>
              <select
                      id="bar-selector"
                      bind:value={selectedBar}
                      class="block w-48 px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
              >
                <option value="all">All Bars</option>
                {#each allBars as bar}
                  <option value={bar.id}>{bar.name}</option>
                {/each}
              </select>
            </div>
          </div>
          
          <div class="grid grid-cols-2 gap-4">
            <div class="bg-gray-50 p-4 rounded-lg">
              <p class="text-sm text-gray-500">Total Checks</p>
              <p class="text-2xl font-semibold text-gray-900">{stats.totalChecks}</p>
            </div>
            
            <div class="bg-gray-50 p-4 rounded-lg">
              <p class="text-sm text-gray-500">Successful Checks</p>
              <p class="text-2xl font-semibold text-green-600">{stats.successfulChecks}</p>
            </div>
            
            <div class="bg-gray-50 p-4 rounded-lg">
              <p class="text-sm text-gray-500">Failed Checks</p>
              <p class="text-2xl font-semibold text-red-600">{stats.failedChecks}</p>
            </div>
            
            <div class="bg-gray-50 p-4 rounded-lg">
              <p class="text-sm text-gray-500">Changes Detected</p>
              <p class="text-2xl font-semibold text-amber-600">{stats.totalChangesDetected}</p>
            </div>
            
            <div class="bg-gray-50 p-4 rounded-lg">
              <p class="text-sm text-gray-500">Average Check Time</p>
              <p class="text-2xl font-semibold text-gray-900">{formatTime(stats.averageCheckTime)}</p>
            </div>
            
            <div class="bg-gray-50 p-4 rounded-lg">
              <p class="text-sm text-gray-500">Last Check</p>
              <p class="text-lg font-semibold text-gray-900" title="{stats.lastCheckTime ? formatDate(stats.lastCheckTime) : 'Never'}">{formatFromNowDate(stats.lastCheckTime)}</p>
            </div>
          </div>
          
          <div class="mt-4">
            <div class="w-full bg-gray-200 rounded-full h-2.5">
              <div class="bg-green-600 h-2.5 rounded-full" style={`width: ${stats.totalChecks > 0 ? (stats.successfulChecks / stats.totalChecks) * 100 : 0}%`}></div>
            </div>
            <p class="text-xs text-gray-500 mt-1">Success Rate: {stats.totalChecks > 0 ? ((stats.successfulChecks / stats.totalChecks) * 100).toFixed(1) : 0}%</p>
          </div>

          <button
                  onclick={handleResetStats}
                  disabled={isResetting}
                  class="px-3 py-1 mt-4 bg-gray-200 text-gray-700 rounded-md hover:bg-gray-300 focus:outline-none focus:ring-2 focus:ring-gray-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed text-sm flex items-center"
          >
            {#if isResetting}
              <svg class="animate-spin -ml-1 mr-1 h-3 w-3 text-gray-700" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              Resetting...
            {:else}
              Reset Stats
            {/if}
          </button>
        </div>
      {/if}
    </div>
  {/if}
</div>