<script lang="ts">
  import { currentUser } from '$lib/stores';
  import { unreadCount } from '$lib/stores';
  import { logout } from '$lib/services/authService';
  import { onMount } from 'svelte';
  import { initAuthStore } from '$lib/stores';
  import { initNotificationStore } from '$lib/stores';
  import { clickOutside } from '$lib/actions/clickOutside';
  import { page } from '$app/stores';
  import {isAdmin} from "$lib/types";

  let isMenuOpen = $state(false);
  let isUserMenuOpen = $state(false);
  let isAdminMenuOpen = $state(false);

  const closeAllMenus = () => {
    isMenuOpen = false;
    isUserMenuOpen = false;
    isAdminMenuOpen = false;
  };

  $effect(() => {
    if ($page.url.pathname) {
      closeAllMenus();
    }
  });

  // Toggle the mobile menu
  const toggleMenu = () => {
    isMenuOpen = !isMenuOpen;
    if (isMenuOpen) {
      isUserMenuOpen = false;
      isAdminMenuOpen = false;
    }
  };

  // Toggle the user menu
  const toggleUserMenu = () => {
    isUserMenuOpen = !isUserMenuOpen;
    if (isUserMenuOpen) {
      isMenuOpen = false;
      isAdminMenuOpen = false;
    }
  };

  // Toggle the admin menu
  const toggleAdminMenu = () => {
    isAdminMenuOpen = !isAdminMenuOpen;
    if (isAdminMenuOpen) {
      isMenuOpen = false;
      isUserMenuOpen = false;
    }
  };

  // Handle logout
  const handleLogout = () => {
    logout();
  };

  // Initialize stores on mount
  onMount(() => {
    initAuthStore();
    if ($currentUser) {
      initNotificationStore();
    }
  });
</script>

<header class="bg-gray-900 text-white shadow-lg">
  <div class="container mx-auto px-4 py-3">
    <div class="flex justify-between items-center">
      <!-- Logo and site name -->
      <div class="flex items-center space-x-2">
        <a href="/" class="flex items-center space-x-2">
          <span class="text-2xl font-bold">BeerScanner</span>
        </a>
      </div>

      <!-- Desktop navigation -->
      <nav class="hidden md:flex space-x-6">
        <a href="/" class="hover:text-amber-400 transition-colors">Home</a>
        <a href="/bars" class="hover:text-amber-400 transition-colors">Bars</a>
        <a href="/beers" class="hover:text-amber-400 transition-colors">Beers</a>
        {#if $currentUser}
          <a href="/profile" class="hover:text-amber-400 transition-colors">Profile</a>
          <a href="/notifications" class="hover:text-amber-400 transition-colors relative">
            Notifications
            {#if $unreadCount > 0}
              <span class="absolute -top-2 -right-2 bg-red-500 text-white text-xs rounded-full h-5 w-5 flex items-center justify-center">
                {$unreadCount}
              </span>
            {/if}
          </a>

          {#if isAdmin($currentUser)}
            <!-- Admin dropdown -->
            <div class="relative">
              <button 
                class="flex items-center space-x-1 hover:text-amber-400 transition-colors focus:outline-none" 
                onclick={toggleAdminMenu}
              >
                <span>Admin</span>
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                  <path fill-rule="evenodd" d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" clip-rule="evenodd" />
                </svg>
              </button>

              {#if isAdminMenuOpen}
                <div class="absolute left-0 mt-2 w-48 bg-white rounded-md shadow-lg py-1 z-10" use:clickOutside={() => isAdminMenuOpen = false}>
                  <a href="/admin/bars" class="block px-4 py-2 text-gray-800 hover:bg-gray-100">Manage Bars</a>
                  <a href="/admin/beers" class="block px-4 py-2 text-gray-800 hover:bg-gray-100">Manage Beers</a>
                  <a href="/admin/notifications" class="block px-4 py-2 text-gray-800 hover:bg-gray-100">System Notifications</a>
                  <a href="/admin/logs" class="block px-4 py-2 text-gray-800 hover:bg-gray-100">Error Logs</a>
                  <a href="/admin/scraper" class="block px-4 py-2 text-gray-800 hover:bg-gray-100">Scraper Settings</a>
                </div>
              {/if}
            </div>
          {/if}
        {/if}
      </nav>

      <!-- User menu or login button -->
      <div class="hidden md:flex items-center space-x-4">
        {#if $currentUser}
          <div class="relative">
            <button 
              class="flex items-center space-x-2 focus:outline-none" 
              onclick={toggleUserMenu}
            >
              {#if $currentUser.profilePicture}
                <img 
                  src={$currentUser.profilePicture} 
                  alt={$currentUser.name} 
                  class="h-8 w-8 rounded-full"
                />
              {:else}
                <div class="h-8 w-8 rounded-full bg-amber-500 flex items-center justify-center">
                  <span class="text-sm font-bold">{$currentUser.name?.charAt(0)}</span>
                </div>
              {/if}
              <span>{$currentUser.name}</span>
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd" d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" clip-rule="evenodd" />
              </svg>
            </button>

            {#if isUserMenuOpen}
              <div class="absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg py-1 z-10" use:clickOutside={() => isUserMenuOpen = false}>
                {#if isAdmin($currentUser)}
                  <div class="px-4 py-2 text-gray-800 font-semibold">Admin</div>
                {/if}
                <a href="/profile" class="block px-4 py-2 text-gray-800 hover:bg-gray-100">Profile</a>
                <a href="/notifications" class="block px-4 py-2 text-gray-800 hover:bg-gray-100 relative">
                  Notifications
                  {#if $unreadCount > 0}
                    <span class="absolute top-2 right-4 bg-red-500 text-white text-xs rounded-full h-5 w-5 flex items-center justify-center">
                      {$unreadCount}
                    </span>
                  {/if}
                </a>
                <div class="border-t border-gray-100 my-1"></div>
                <a href="/profile#tracked-beers" class="block px-4 py-2 text-gray-800 hover:bg-gray-100">
                  My Tracked Beers
                </a>
                <a href="/profile#tracked-bars" class="block px-4 py-2 text-gray-800 hover:bg-gray-100">
                  My Tracked Bars
                </a>
                <a href="/bars/request" class="block px-4 py-2 text-gray-800 hover:bg-gray-100">
                  Request New Bar
                </a>
                <div class="border-t border-gray-100 my-1"></div>
                <button 
                  class="block w-full text-left px-4 py-2 text-gray-800 hover:bg-gray-100" 
                  onclick={handleLogout}
                >
                  Logout
                </button>
              </div>
            {/if}
          </div>
        {:else}
          <a href="/login" class="bg-amber-500 hover:bg-amber-600 text-white px-4 py-2 rounded-md transition-colors">
            Login
          </a>
        {/if}
      </div>

      <!-- Mobile menu button -->
      <div class="md:hidden flex items-center space-x-4">
        {#if $currentUser}
          <a href="/notifications" class="relative">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
            </svg>
            {#if $unreadCount > 0}
              <span class="absolute -top-2 -right-2 bg-red-500 text-white text-xs rounded-full h-5 w-5 flex items-center justify-center">
                {$unreadCount}
              </span>
            {/if}
          </a>
        {/if}

        <button onclick={toggleMenu} class="focus:outline-none">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
          </svg>
        </button>
      </div>
    </div>

    <!-- Mobile menu -->
    {#if isMenuOpen}
      <div class="md:hidden mt-4 pb-4">
        <nav class="flex flex-col space-y-2">
          <a href="/" class="hover:text-amber-400 transition-colors py-2">Home</a>
          <a href="/bars" class="hover:text-amber-400 transition-colors py-2">Bars</a>
          <a href="/beers" class="hover:text-amber-400 transition-colors py-2">Beers</a>

          {#if $currentUser}
            <a href="/profile" class="hover:text-amber-400 transition-colors py-2">Profile</a>
            <a href="/notifications" class="hover:text-amber-400 transition-colors py-2 relative">
              Notifications
              {#if $unreadCount > 0}
                <span class="ml-2 bg-red-500 text-white text-xs rounded-full h-5 w-5 inline-flex items-center justify-center">
                  {$unreadCount}
                </span>
              {/if}
            </a>

            <div class="border-t border-gray-700 my-2"></div>

            <a href="/profile#tracked-beers" class="hover:text-amber-400 transition-colors py-2">
              My Tracked Beers
            </a>
            <a href="/profile#tracked-bars" class="hover:text-amber-400 transition-colors py-2">
              My Tracked Bars
            </a>
            <a href="/bars/request" class="hover:text-amber-400 transition-colors py-2">
              Request New Bar
            </a>

            {#if isAdmin($currentUser)}
              <div class="border-t border-gray-700 my-2"></div>
              <div class="text-sm text-gray-400 py-1">Admin</div>

              <a href="/admin/bars" class="hover:text-amber-400 transition-colors py-2 pl-2">
                Manage Bars
              </a>
              <a href="/admin/beers" class="hover:text-amber-400 transition-colors py-2 pl-2">
                Manage Beers
              </a>
              <a href="/admin/notifications" class="hover:text-amber-400 transition-colors py-2 pl-2">
                System Notifications
              </a>
              <a href="/admin/logs" class="hover:text-amber-400 transition-colors py-2 pl-2">
                Error Logs
              </a>
              <a href="/admin/scraper" class="hover:text-amber-400 transition-colors py-2 pl-2">
                Scraper Settings
              </a>
            {/if}

            <div class="border-t border-gray-700 my-2"></div>

            <button 
              class="text-left hover:text-amber-400 transition-colors py-2" 
              onclick={handleLogout}
            >
              Logout
            </button>
          {:else}
            <a href="/login" class="hover:text-amber-400 transition-colors py-2">Login</a>
          {/if}
        </nav>
      </div>
    {/if}
  </div>
</header>
