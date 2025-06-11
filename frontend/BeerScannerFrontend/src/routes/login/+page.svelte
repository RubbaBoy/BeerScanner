<script lang="ts">
  import { onMount } from 'svelte';
  import { isAuthenticated } from '$lib/services/authService';
  import { goto } from '$app/navigation';

  let isLoading = $state(false);
  let error = $state<string | null>(null);

  // Redirect to home if already authenticated
  onMount(() => {
    if (isAuthenticated()) {
      goto('/');
    }
  });

  // Handle Google login
  const handleGoogleLogin = () => {
    isLoading = true;
    error = null;
    
    try {
      // Redirect to Google OAuth endpoint
      window.location.href = 'http://localhost:8080/api/v1/auth/google';
    } catch (e) {
      console.error('Failed to initiate login:', e);
      error = 'Failed to initiate login. Please try again later.';
      isLoading = false;
    }
  };
</script>

<div class="flex justify-center items-center min-h-[calc(100vh-200px)]">
  <div class="bg-white rounded-lg shadow-md p-8 w-full max-w-md">
    <h1 class="text-3xl font-bold text-gray-900 mb-6 text-center">Login to BeerScanner</h1>
    
    {#if error}
      <div class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
        <p>{error}</p>
      </div>
    {/if}
    
    <div class="space-y-4">
      <button 
        onclick={handleGoogleLogin}
        disabled={isLoading}
        class="w-full flex items-center justify-center space-x-2 bg-white border border-gray-300 rounded-md py-3 px-4 text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-amber-500 transition-colors"
      >
        {#if isLoading}
          <span class="animate-spin">⟳</span>
        {:else}
          <svg class="h-5 w-5" viewBox="0 0 24 24">
            <path d="M12.545,10.239v3.821h5.445c-0.712,2.315-2.647,3.972-5.445,3.972c-3.332,0-6.033-2.701-6.033-6.032s2.701-6.032,6.033-6.032c1.498,0,2.866,0.549,3.921,1.453l2.814-2.814C17.503,2.988,15.139,2,12.545,2C7.021,2,2.543,6.477,2.543,12s4.478,10,10.002,10c8.396,0,10.249-7.85,9.426-11.748L12.545,10.239z" fill="#4285F4"/>
            <path d="M7.545,10.239v3.821h5.445c-0.712,2.315-2.647,3.972-5.445,3.972c-3.332,0-6.033-2.701-6.033-6.032s2.701-6.032,6.033-6.032c1.498,0,2.866,0.549,3.921,1.453l2.814-2.814C12.503,2.988,10.139,2,7.545,2C2.021,2,-2.457,6.477,-2.457,12s4.478,10,10.002,10c8.396,0,10.249-7.85,9.426-11.748L7.545,10.239z" fill="#34A853"/>
            <path d="M2.545,7.239v3.821h5.445c-0.712,2.315-2.647,3.972-5.445,3.972c-3.332,0-6.033-2.701-6.033-6.032s2.701-6.032,6.033-6.032c1.498,0,2.866,0.549,3.921,1.453l2.814-2.814C7.503,2.988,5.139,2,2.545,2C-2.979,2,-7.457,6.477,-7.457,12s4.478,10,10.002,10c8.396,0,10.249-7.85,9.426-11.748L2.545,7.239z" fill="#FBBC05"/>
            <path d="M2.545,16.761v-3.821h5.445c-0.712-2.315-2.647-3.972-5.445-3.972c-3.332,0-6.033,2.701-6.033,6.032s2.701,6.032,6.033,6.032c1.498,0,2.866-0.549,3.921-1.453l2.814,2.814C7.503,21.012,5.139,22,2.545,22C-2.979,22,-7.457,17.523,-7.457,12s4.478-10,10.002-10c8.396,0,10.249,7.85,9.426,11.748L2.545,16.761z" fill="#EA4335"/>
          </svg>
        {/if}
        <span>{isLoading ? 'Logging in...' : 'Continue with Google'}</span>
      </button>
      
      <p class="text-center text-sm text-gray-600 mt-4">
        By logging in, you agree to our 
        <a href="#" class="text-amber-600 hover:text-amber-500">Terms of Service</a> and 
        <a href="#" class="text-amber-600 hover:text-amber-500">Privacy Policy</a>.
      </p>
    </div>
  </div>
</div>