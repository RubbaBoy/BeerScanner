<script lang="ts">
  import { onMount } from 'svelte';
  import { handleGoogleCallback } from '$lib/services/authService';

  let error = $state<string | null>(null);

  onMount(() => {
    try {
      // Get the token from the URL query parameters
      const urlParams = new URLSearchParams(window.location.search);
      const token = urlParams.get('token');
      
      if (!token) {
        error = 'No authentication token received. Please try again.';
        return;
      }
      
      // Handle the callback with the token
      handleGoogleCallback(token);
    } catch (e) {
      console.error('Failed to process authentication:', e);
      error = 'Failed to process authentication. Please try again.';
    }
  });
</script>

<div class="flex justify-center items-center min-h-[calc(100vh-200px)]">
  <div class="bg-white rounded-lg shadow-md p-8 w-full max-w-md text-center">
    {#if error}
      <div class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
        <p>{error}</p>
        <a href="/login" class="inline-block mt-4 text-amber-600 hover:text-amber-700 font-medium">
          Return to Login
        </a>
      </div>
    {:else}
      <h1 class="text-2xl font-bold text-gray-900 mb-4">Logging you in...</h1>
      <div class="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-amber-500 mx-auto"></div>
    {/if}
  </div>
</div>