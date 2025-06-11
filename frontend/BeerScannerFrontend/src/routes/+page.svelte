<script lang="ts">
  import { onMount } from 'svelte';
  import { getAllBars } from '$lib/services';
  import { getAllBeers } from '$lib/services';
  import { BarCard, BeerCard } from '$lib/components';
  import { handleGoogleCallback } from '$lib/services/authService';
  import type { Bar, Beer } from '$lib/types';

  let recentBars: Bar[] = $state([]);
  let popularBeers: Beer[] = $state([]);
  let isLoading = $state(true);
  let error = $state<string | null>(null);

  // Fetch data on mount
  onMount(async () => {
    // Check if there's a token in the URL (from Google OAuth)
    const urlParams = new URLSearchParams(window.location.search);
    const token = urlParams.get('token');

    if (token) {
      // Handle the token and remove it from the URL
      handleGoogleCallback(token);
      // Clean up the URL
      window.history.replaceState({}, document.title, window.location.pathname);
    }
    try {
      // Fetch recent bars
      const barsResponse = await getAllBars({ page: 0, size: 4, sort: ['updatedAt,desc'] });
      recentBars = barsResponse.content;

      // Fetch popular beers
      const beersResponse = await getAllBeers({ page: 0, size: 4, sort: ['updatedAt,desc'] });
      popularBeers = beersResponse.content;
    } catch (e) {
      console.error('Failed to fetch data:', e);
      error = 'Failed to load data. Please try again later.';
    } finally {
      isLoading = false;
    }
  });
</script>

<!-- Hero section -->
<section class="bg-amber-500 text-white rounded-lg shadow-md mb-12">
  <div class="container mx-auto px-6 py-12">
    <div class="flex flex-col md:flex-row items-center">
      <div class="md:w-1/2 mb-8 md:mb-0">
        <h1 class="text-4xl font-bold mb-4">Track Your Favorite Beers</h1>
        <p class="text-xl mb-6">
          Get notified when your favorite beers become available at local bars.
        </p>
        <div class="flex space-x-4">
          <a href="/bars" class="bg-white text-amber-600 hover:bg-gray-100 px-6 py-3 rounded-md font-semibold transition-colors">
            Browse Bars
          </a>
          <a href="/beers" class="bg-amber-600 text-white hover:bg-amber-700 px-6 py-3 rounded-md font-semibold transition-colors">
            Find Beers
          </a>
        </div>
      </div>
      <div class="md:w-1/2 flex justify-center">
        <img src="/images/beer-illustration.svg" alt="Beer illustration" class="max-w-full h-auto" onerror={(e) => e.currentTarget.style.display = 'none'} />
      </div>
    </div>
  </div>
</section>

<!-- Main content -->
<div class="grid grid-cols-1 gap-12">
  <!-- Recent updates section -->
  <section>
    <div class="flex justify-between items-center mb-6">
      <h2 class="text-2xl font-bold text-gray-900">Recently Updated Bars</h2>
      <a href="/bars" class="text-amber-600 hover:text-amber-700 font-medium">View All</a>
    </div>

    {#if isLoading}
      <div class="flex justify-center items-center h-64">
        <div class="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-amber-500"></div>
      </div>
    {:else if error}
      <div class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
        <p>{error}</p>
      </div>
    {:else if recentBars.length === 0}
      <div class="bg-gray-100 border border-gray-300 text-gray-700 px-4 py-8 rounded text-center">
        <p>No bars available yet. Check back soon!</p>
      </div>
    {:else}
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {#each recentBars as bar}
          <BarCard {bar} />
        {/each}
      </div>
    {/if}
  </section>

  <!-- Popular beers section -->
  <section>
    <div class="flex justify-between items-center mb-6">
      <h2 class="text-2xl font-bold text-gray-900">Popular Beers</h2>
      <a href="/beers" class="text-amber-600 hover:text-amber-700 font-medium">View All</a>
    </div>

    {#if isLoading}
      <div class="flex justify-center items-center h-64">
        <div class="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-amber-500"></div>
      </div>
    {:else if error}
      <div class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
        <p>{error}</p>
      </div>
    {:else if popularBeers.length === 0}
      <div class="bg-gray-100 border border-gray-300 text-gray-700 px-4 py-8 rounded text-center">
        <p>No beers available yet. Check back soon!</p>
      </div>
    {:else}
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {#each popularBeers as beer}
          <BeerCard {beer} />
        {/each}
      </div>
    {/if}
  </section>

  <!-- Features section -->
  <section class="bg-gray-100 rounded-lg p-8 mt-6">
    <h2 class="text-2xl font-bold text-gray-900 mb-6 text-center">How It Works</h2>

    <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
      <div class="bg-white p-6 rounded-lg shadow-md">
        <div class="bg-amber-100 text-amber-800 rounded-full w-12 h-12 flex items-center justify-center mb-4 mx-auto">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
        </div>
        <h3 class="text-xl font-semibold text-gray-900 mb-2 text-center">Find Your Favorites</h3>
        <p class="text-gray-600 text-center">
          Browse our database of bars and beers to find your favorites.
        </p>
      </div>

      <div class="bg-white p-6 rounded-lg shadow-md">
        <div class="bg-amber-100 text-amber-800 rounded-full w-12 h-12 flex items-center justify-center mb-4 mx-auto">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
          </svg>
        </div>
        <h3 class="text-xl font-semibold text-gray-900 mb-2 text-center">Track & Get Notified</h3>
        <p class="text-gray-600 text-center">
          Track bars and beers you're interested in and get notified when they're available.
        </p>
      </div>

      <div class="bg-white p-6 rounded-lg shadow-md">
        <div class="bg-amber-100 text-amber-800 rounded-full w-12 h-12 flex items-center justify-center mb-4 mx-auto">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 3v4M3 5h4M6 17v4m-2-2h4m5-16l2.286 6.857L21 12l-5.714 2.143L13 21l-2.286-6.857L5 12l5.714-2.143L13 3z" />
          </svg>
        </div>
        <h3 class="text-xl font-semibold text-gray-900 mb-2 text-center">Discover New Favorites</h3>
        <p class="text-gray-600 text-center">
          Discover new beers and bars based on your preferences and location.
        </p>
      </div>
    </div>
  </section>
</div>
