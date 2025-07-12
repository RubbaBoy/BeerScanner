<script lang="ts">
  import { onMount } from 'svelte';
  import { page } from '$app/stores';
  import { goto } from '$app/navigation';
  import { getBarById, getCheckForBar } from '$lib/services';
  import { isAuthenticated } from '$lib/services/authService';
  import { format } from 'date-fns';
  import { formatFromNowDate } from '$lib/utils/formatting';
  import { BeerCard } from '$lib/components';
  import type { Bar, BarCheck } from '$lib/types';

  const barId = parseInt($page.params.id);
  const checkId = parseInt($page.params.checkId);

  let bar: Bar | null = $state(null);
  let barCheck: BarCheck | null = $state(null);
  let isLoading = $state(true);
  let error = $state<string | null>(null);

  // Format date
  const formatDate = (dateString: string | undefined) => {
    if (!dateString) return 'N/A';
    return format(new Date(dateString), 'MMM d, yyyy HH:mm:ss');
  };

  // Get status badge class
  const getStatusBadgeClass = (status: string) => {
    switch (status) {
      case 'PENDING':
        return 'bg-yellow-100 text-yellow-800';
      case 'PROCESSING':
        return 'bg-blue-100 text-blue-800';
      case 'COMPLETED':
        return 'bg-green-100 text-green-800';
      case 'FAILED':
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  // Fetch bar check data
  const fetchData = async () => {
    isLoading = true;
    error = null;

    try {
      // Fetch bar details and check details
      const [barResponse, checkResponse] = await Promise.all([
        getBarById(barId),
        getCheckForBar(barId, checkId)
      ]);

      bar = barResponse;
      barCheck = checkResponse;
    } catch (e) {
      console.error('Failed to fetch data:', e);
      error = 'Failed to load bar check details. Please try again later.';
    } finally {
      isLoading = false;
    }
  };

  // Navigate back to checks list
  const goBack = () => {
    goto(`/admin/bars/${barId}/checks`);
  };

  // Redirect to login if not authenticated
  onMount(() => {
    if (!isAuthenticated()) {
      goto('/login');
      return;
    }

    fetchData();
  });
</script>

<div class="space-y-8">
    <!-- Navigation breadcrumb -->
    <nav class="flex items-center space-x-2 text-sm text-gray-600">
        <button onclick={goBack} class="hover:text-gray-900 transition-colors">
            ← Back to Checks
        </button>
    </nav>

    {#if isLoading}
        <div class="flex justify-center items-center h-64">
            <div class="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-amber-500"></div>
        </div>
    {:else if error}
        <div class="bg-red-50 border border-red-200 rounded-lg p-4">
            <p class="text-red-700">{error}</p>
        </div>
    {:else if barCheck && bar}
        <!-- Check Details Header -->
        <div class="bg-white rounded-lg shadow p-6">
            <div class="flex justify-between items-start mb-4">
                <div>
                    <h1 class="text-2xl font-bold text-gray-900">Check #{barCheck.id}</h1>
                    <p class="text-gray-600">{bar.name}</p>
                </div>
                <span class="px-3 py-1 rounded-full text-sm font-medium {getStatusBadgeClass(barCheck.processingStatus)}">
                    {barCheck.processingStatus}
                </span>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
                <div>
                    <p class="text-sm text-gray-500">Created At</p>
                    <p class="font-medium">{formatDate(barCheck.createdAt)}</p>
                </div>
                <div>
                    <p class="text-sm text-gray-500">Process Duration</p>
                    <p class="font-medium">{barCheck.processDuration ? `${barCheck.processDuration}ms` : 'N/A'}</p>
                </div>
                <div>
                    <p class="text-sm text-gray-500">Has Changes</p>
                    <p class="font-medium">{barCheck.hasChanges ? 'Yes' : 'No'}</p>
                </div>
                <div>
                    <p class="text-sm text-gray-500">Menu Hash</p>
                    <p class="font-medium text-xs font-mono">{barCheck.menuHash || 'N/A'}</p>
                </div>
            </div>

            {#if barCheck.errorMessage}
                <div class="mt-4 bg-red-50 border border-red-200 rounded-lg p-4">
                    <h3 class="text-sm font-medium text-red-800 mb-2">Error Message</h3>
                    <p class="text-red-700">{barCheck.errorMessage}</p>
                </div>
            {/if}
        </div>

        <!-- Beer Changes -->
        {#if barCheck.hasChanges}
            <div class="space-y-6 lg:space-y-0 lg:grid lg:grid-cols-2 lg:gap-6">
                <!-- Beers Added -->
                {#if barCheck.beersAdded && barCheck.beersAdded.length > 0}
                    <div class="bg-white rounded-lg shadow">
                        <div class="px-6 py-4 border-b border-gray-200">
                            <h2 class="text-lg font-semibold text-gray-900 flex items-center">
                                <span class="bg-green-100 text-green-800 px-2 py-1 rounded-full text-xs font-medium mr-3">
                                    ADDED
                                </span>
                                Beers Added ({barCheck.beersAdded.length})
                            </h2>
                        </div>
                        <div class="p-4">
                            <div class="bg-green-50 border border-green-200 rounded-md p-4">
                                <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                                    {#each barCheck.beersAdded as beer}
                                        <BeerCard {beer} showActions={false} />
                                    {/each}
                                </div>
                            </div>
                        </div>
                    </div>
                {/if}

                <!-- Beers Removed -->
                {#if barCheck.beersRemoved && barCheck.beersRemoved.length > 0}
                    <div class="bg-white rounded-lg shadow">
                        <div class="px-6 py-4 border-b border-gray-200">
                            <h2 class="text-lg font-semibold text-gray-900 flex items-center">
                                <span class="bg-red-100 text-red-800 px-2 py-1 rounded-full text-xs font-medium mr-3">
                                    REMOVED
                                </span>
                                Beers Removed ({barCheck.beersRemoved.length})
                            </h2>
                        </div>
                        <div class="p-4">
                            <div class="bg-red-50 border border-red-200 rounded-md p-4">
                                <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                                    {#each barCheck.beersRemoved as beer}
                                        <BeerCard {beer} showActions={false} />
                                    {/each}
                                </div>
                            </div>
                        </div>
                    </div>
                {/if}
            </div>
        {:else}
            <div class="bg-gray-50 rounded-lg p-8 text-center col-span-2">
                <p class="text-gray-600">No changes detected in this check.</p>
            </div>
        {/if}

        <!-- Menu Content (if available) -->
        {#if barCheck.menuContent}
            <div class="bg-white rounded-lg shadow">
                <div class="px-6 py-4 border-b border-gray-200">
                    <h2 class="text-lg font-semibold text-gray-900">Menu Content</h2>
                </div>
                <div class="px-6 py-4">
                    <pre class="text-xs text-gray-700 whitespace-pre-wrap font-mono bg-gray-50 p-4 rounded overflow-x-auto">{barCheck.menuContent}</pre>
                </div>
            </div>
        {/if}
    {/if}
</div>