<script lang="ts">
  import {onMount} from 'svelte';
  import {
    getCurrentUser,
    getTrackedBars,
    getTrackedBeers,
    updateNotificationSettings,
    getMyRequestedBars,
    getMyBeerRequests
  } from '$lib/services';
  import { formatDistanceToNow } from 'date-fns';
  import {BarCard, BeerCard, Pagination} from '$lib/components';
  import {currentUser} from '$lib/stores';
  import {logout} from '$lib/services/authService';
  import type {User, Bar, Beer, BeerRequest} from '$lib/types';
  import {formatFromNowDate} from "$lib/utils/formatting";

  let user: User | null = $state(null);
  let trackedBars: Bar[] = $state([]);
  let trackedBeers: Beer[] = $state([]);
  let requestedBars: Bar[] = $state([]);
  let beerRequests: BeerRequest[] = $state([]);
  let isLoading = $state(true);
  let error = $state<string | null>(null);
  let activeTab = $state('bars');
  let notificationEnabled = $state(false);
  let isSaving = $state(false);
  let totalPages = $state(0);
  let currentPage = $state(0);

  // Fetch user data
  const fetchUserData = async () => {
    isLoading = true;
    error = null;

    try {
      // Fetch current user
      user = await getCurrentUser();
      notificationEnabled = user.notificationEnabled || false;

      const getPagedBeerRequests = async (page: number): BeerRequest[] => {
        let pagedBeerRequests = await getMyBeerRequests({currentPage: page, size: 12, sort: ['name,asc']});
        totalPages = pagedBeerRequests.totalPages;
        return pagedBeerRequests.content
      };

      [trackedBars, trackedBeers, requestedBars, beerRequests] = await Promise.all([
        getTrackedBars(),
        getTrackedBeers(),
        getMyRequestedBars(),
        getPagedBeerRequests(currentPage)
      ]);

    } catch (e) {
      console.error('Failed to fetch user data:', e);
      error = 'Failed to load user data. Please try again later.';
    } finally {
      isLoading = false;
    }
  };

  // Change active tab
  const setActiveTab = (tab: string) => {
    currentPage = 0;
    activeTab = tab;
  };

  // Toggle notification settings
  const toggleNotifications = async () => {
    if (!user) return;

    isSaving = true;
    try {
      const newSettings = {notificationEnabled: !notificationEnabled};
      await updateNotificationSettings(newSettings);
      notificationEnabled = !notificationEnabled;
    } catch (e) {
      console.error('Failed to update notification settings:', e);
      error = 'Failed to update notification settings. Please try again later.';
    } finally {
      isSaving = false;
    }
  };

  // Handle logout
  const handleLogout = () => {
    logout();
  };

  // Fetch data on mount
  onMount(() => {
    if (!$currentUser) {
      window.location.href = '/login';
      return;
    }

    fetchUserData();
  });
</script>

<div class="space-y-8">
    {#if isLoading}
        <div class="flex justify-center items-center h-64">
            <div class="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-amber-500"></div>
        </div>
    {:else if error}
        <div class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
            <p>{error}</p>
        </div>
    {:else if user}
        <!-- User profile header -->
        <div class="bg-white rounded-lg shadow-md p-6">
            <div class="flex flex-col md:flex-row items-start md:items-center gap-6">
                <div class="flex-shrink-0">
                    {#if user.profilePicture}
                        <img
                                src={user.profilePicture}
                                alt={user.name}
                                class="h-24 w-24 rounded-full"
                        />
                    {:else}
                        <div class="h-24 w-24 rounded-full bg-amber-500 flex items-center justify-center">
                            <span class="text-2xl font-bold text-white">{user.name?.charAt(0)}</span>
                        </div>
                    {/if}
                </div>

                <div class="flex-grow">
                    <h1 class="text-3xl font-bold text-gray-900">{user.name}</h1>
                    <p class="text-gray-600">{user.email}</p>

                    <div class="mt-4 flex flex-wrap gap-4">
                        <button
                                onclick={toggleNotifications}
                                disabled={isSaving}
                                class="flex items-center space-x-2 px-4 py-2 rounded-md bg-gray-200 text-gray-700 hover:bg-gray-300 transition-colors"
                        >
                            {#if isSaving}
                                <span class="animate-spin">⟳</span>
                            {:else}
                                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                                    <path d="M10 2a6 6 0 00-6 6v3.586l-.707.707A1 1 0 004 14h12a1 1 0 00.707-1.707L16 11.586V8a6 6 0 00-6-6zM10 18a3 3 0 01-3-3h6a3 3 0 01-3 3z"/>
                                </svg>
                            {/if}
                            <span>Notifications: {notificationEnabled ? 'On' : 'Off'}</span>
                        </button>

                        <button
                                onclick={handleLogout}
                                class="flex items-center space-x-2 px-4 py-2 rounded-md bg-red-100 text-red-700 hover:bg-red-200 transition-colors"
                        >
                            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                                <path fill-rule="evenodd" d="M3 3a1 1 0 00-1 1v12a1 1 0 001 1h12a1 1 0 001-1V7.414l-5-5H3zm7 5a1 1 0 10-2 0v4.586l-1.293-1.293a1 1 0 10-1.414 1.414l3 3a1 1 0 001.414 0l3-3a1 1 0 00-1.414-1.414L12 12.586V8z" clip-rule="evenodd"/>
                            </svg>
                            <span>Logout</span>
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Tabs -->
        <div class="border-b border-gray-200">
            <nav class="-mb-px flex space-x-8">
                <button
                        class={`py-4 px-1 border-b-2 font-medium text-sm ${
            activeTab === 'bars'
              ? 'border-amber-500 text-amber-600'
              : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
          }`}
                        onclick={() => setActiveTab('bars')}
                >
                    Tracked Bars ({trackedBars.length})
                </button>
                <button
                        class={`py-4 px-1 border-b-2 font-medium text-sm ${
            activeTab === 'beers'
              ? 'border-amber-500 text-amber-600'
              : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
          }`}
                        onclick={() => setActiveTab('beers')}
                >
                    Tracked Beers ({trackedBeers.length})
                </button>
                <button
                        class={`py-4 px-1 border-b-2 font-medium text-sm ${
            activeTab === 'bar_requests'
              ? 'border-amber-500 text-amber-600'
              : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
          }`}
                        onclick={() => setActiveTab('bar_requests')}
                        id="bar-requests"
                >
                    Bar Requests ({requestedBars.length})
                </button>
                <button
                        class={`py-4 px-1 border-b-2 font-medium text-sm ${
            activeTab === 'beer_requests'
              ? 'border-amber-500 text-amber-600'
              : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
          }`}
                        onclick={() => setActiveTab('beer_requests')}
                        id="beer-requests"
                >
                    Beer Requests ({beerRequests.length})
                </button>
            </nav>
        </div>

        <!-- Content based on active tab -->
        {#if activeTab === 'bars'}
            {#if trackedBars.length === 0}
                <div class="bg-gray-100 border border-gray-300 text-gray-700 px-4 py-8 rounded text-center">
                    <p>You are not tracking any bars yet.</p>
                    <a href="/bars" class="inline-block mt-4 text-amber-600 hover:text-amber-700 font-medium">
                        Browse bars to start tracking
                    </a>
                </div>
            {:else}
                <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    {#each trackedBars as bar}
                        <BarCard {bar}/>
                    {/each}
                </div>
            {/if}
        {:else if activeTab === 'beers'}
            {#if trackedBeers.length === 0}
                <div class="bg-gray-100 border border-gray-300 text-gray-700 px-4 py-8 rounded text-center">
                    <p>You are not tracking any beers yet.</p>
                    <a href="/beers" class="inline-block mt-4 text-amber-600 hover:text-amber-700 font-medium">
                        Browse beers to start tracking
                    </a>
                </div>
            {:else}
                <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    {#each trackedBeers as beer}
                        <BeerCard {beer}/>
                    {/each}
                </div>
            {/if}
        {:else if activeTab === 'bar_requests'}
            {#if requestedBars.length === 0}
                <div class="bg-gray-100 border border-gray-300 text-gray-700 px-4 py-8 rounded text-center">
                    <p>You haven't requested any bars yet.</p>
                    <a href="/bars/request" class="inline-block mt-4 text-amber-600 hover:text-amber-700 font-medium">
                        Request a new bar
                    </a>
                </div>
            {:else}
                <div class="bg-white rounded-lg shadow-sm overflow-hidden">
                    <table class="min-w-full divide-y divide-gray-200">
                        <thead class="bg-gray-50">
                        <tr>
                            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Bar Name
                            </th>
                            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Location
                            </th>
                            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Status
                            </th>
                            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Requested On
                            </th>
                        </tr>
                        </thead>
                        <tbody class="bg-white divide-y divide-gray-200">
                        {#each requestedBars as bar}
                            <tr>
                                <td class="px-6 py-4 whitespace-nowrap">
                                    <div class="text-sm font-medium text-gray-900">{bar.name}</div>
                                </td>
                                <td class="px-6 py-4 whitespace-nowrap">
                                    <div class="text-sm text-gray-500">{bar.location}</div>
                                </td>
                                <td class="px-6 py-4 whitespace-nowrap">
                                    {#if bar.approved}
                      <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-green-100 text-green-800">
                        Approved
                      </span>
                                    {:else}
                      <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-yellow-100 text-yellow-800">
                        Pending
                      </span>
                                    {/if}
                                </td>
                                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                    {formatFromNowDate(bar.requestedAt)}
                                </td>
                            </tr>
                        {/each}
                        </tbody>
                    </table>
                </div>
            {/if}
        {:else if activeTab === 'beer_requests'}
            {#if beerRequests.length === 0}
                <div class="bg-gray-100 border border-gray-300 text-gray-700 px-4 py-8 rounded text-center">
                    <p>You haven't requested any beers yet.</p>
                    <a href="/beers/request" class="inline-block mt-4 text-amber-600 hover:text-amber-700 font-medium">
                        Request a new beer
                    </a>
                </div>
            {:else}
                <div class="bg-white rounded-lg shadow-sm overflow-hidden">
                    <table class="min-w-full divide-y divide-gray-200">
                        <thead class="bg-gray-50">
                        <tr>
                            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Name
                            </th>
                            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Brewery
                            </th>
                            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Type
                            </th>
                            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                ABV
                            </th>
                            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Requested On
                            </th>
                        </tr>
                        </thead>
                        <tbody class="bg-white divide-y divide-gray-200">
                        {#each beerRequests as beerRequest}
                            <tr>
                                <td class="px-6 py-4 whitespace-nowrap">
                                    <div class="text-sm font-medium text-gray-900">{beerRequest.name}</div>
                                </td>
                                <td class="px-6 py-4 whitespace-nowrap">
                                    <div class="text-sm text-gray-500">{beerRequest.brewery || '-'}</div>
                                </td>
                                <td class="px-6 py-4 whitespace-nowrap">
                                    <div class="text-sm text-gray-500">{beerRequest.type || '-'}</div>
                                </td>
                                <td class="px-6 py-4 whitespace-nowrap">
                                    <div class="text-sm text-gray-500">{beerRequest.abv ? `${beerRequest.abv}%` : '-'}</div>
                                </td>
                                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                    {formatFromNowDate(beerRequest.requestedAt)}
                                </td>
                            </tr>
                        {/each}
                        </tbody>
                    </table>
                </div>

                {#if totalPages > 1}
                    <Pagination {currentPage} {totalPages} onPageChange={(e) => currentPage = e}/>
                {/if}
            {/if}
        {/if}
    {:else}
        <div class="bg-gray-100 border border-gray-300 text-gray-700 px-4 py-8 rounded text-center">
            <p>Please log in to view your profile.</p>
            <a href="/login" class="inline-block mt-4 bg-amber-500 hover:bg-amber-600 text-white px-4 py-2 rounded-md transition-colors">
                Login
            </a>
        </div>
    {/if}
</div>
