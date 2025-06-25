<script lang="ts">
  import { searchBeers } from '$lib/services';
  import type { Beer } from '$lib/types';
  import { clickOutside } from '$lib/actions/clickOutside';

  interface Props {
    selectedBeer: Beer | null;
    onBeerSelected: (beer: Beer) => void;
    onCreateBeer?: (beerName: string) => void;
    showCreateOption?: boolean;
    placeholder?: string;
    disabled?: boolean;
  }

  let {
    selectedBeer = $bindable(),
    onBeerSelected = () => {},
    onCreateBeer = () => {},
    showCreateOption = false,
    placeholder = "Search for a beer...",
    disabled = false
  }: Props = $props();

  // State variables
  let searchTerm = $state('');
  let searchResults: Beer[] = $state([]);
  let isSearching = $state(false);
  let error = $state<string | null>(null);
  let isFocused = $state(false);

  // Display value in input
  let displayValue = $derived(
    selectedBeer ? selectedBeer.name : searchTerm
  );

  // Search for beers
  const handleSearch = async () => {
    if (disabled) return;

    // Clear selected beer when user starts typing
    if (selectedBeer && searchTerm !== selectedBeer.name) {
      selectedBeer = null;
    }

    if (!searchTerm.trim()) {
      searchResults = [];
      return;
    }

    isSearching = true;
    error = null;

    try {
      const response = await searchBeers(searchTerm, { page: 0, size: 10, sort: ['name,asc'] });
      searchResults = response.content;
    } catch (e) {
      console.error('Failed to search beers:', e);
      error = 'Failed to search beers. Please try again later.';
    } finally {
      isSearching = false;
    }
  };

  // Select a beer from search results
  const selectBeer = (beer: Beer) => {
    if (disabled) return;

    selectedBeer = beer;
    onBeerSelected(beer);
    searchTerm = beer.name;
    closeSearchResults();
  };

  // Close search results popup
  const closeSearchResults = () => {
    searchResults = [];
    error = null;
    isFocused = false;
  };

  // Handle create beer action
  const handleCreateBeer = () => {
    if (disabled || !onCreateBeer) return;

    onCreateBeer(searchTerm);
    closeSearchResults();
  };

  // Handle input focus
  const handleFocus = () => {
    if (disabled) return;

    isFocused = true;
    // If there's a selected beer, clear the search term to allow new search
    if (selectedBeer) {
      searchTerm = '';
    }
  };

  // Handle input change
  const handleInput = (e: Event) => {
    if (disabled) return;

    searchTerm = (e.target as HTMLInputElement).value;
    handleSearch();
  };

  // Show dropdown when we have results or when searching with no selected beer
  let showDropdown = $derived(
    !disabled && isFocused && (
      isSearching ||
      error ||
      (searchResults.length > 0) ||
      (searchTerm && searchResults.length === 0 && !isSearching && !error)
    )
  );

  // Dynamic input classes
  let inputClasses = $derived(
    `w-full p-3 border rounded-lg focus:outline-none ${
      disabled
        ? 'bg-gray-100 border-gray-200 cursor-not-allowed text-gray-500'
        : 'border-gray-300 focus:ring-2 focus:ring-blue-500'
    }`
  );
</script>

<div class="relative">
    <input
            type="text"
            value={displayValue}
            oninput={handleInput}
            onfocus={handleFocus}
            {placeholder}
            {disabled}
            class={inputClasses}
    />

    {#if showDropdown}
        <div
                class="absolute top-full left-0 right-0 mt-1 bg-white border border-gray-200 rounded-lg shadow-lg z-10"
                use:clickOutside={closeSearchResults}
        >
            {#if isSearching}
                <div class="p-4">
                    <p class="text-gray-500">Searching...</p>
                </div>
            {:else if error}
                <div class="p-4 bg-red-50">
                    <p class="text-red-600">{error}</p>
                </div>
            {:else if searchResults.length > 0}
                <div class="max-h-60 overflow-y-auto">
                    {#each searchResults as beer}
                        <button
                                type="button"
                                onclick={() => selectBeer(beer)}
                                class="w-full text-left p-3 hover:bg-gray-100 transition-colors duration-200 border-b border-gray-100 last:border-b-0"
                        >
                            <h4 class="font-semibold text-gray-800">{beer.name}</h4>
                            {#if beer.brewery}
                                <p class="text-sm text-gray-600">{beer.brewery}</p>
                            {/if}
                        </button>
                    {/each}
                </div>
            {:else if searchTerm}
                <div class="p-4">
                    {#if showCreateOption && onCreateBeer}
                        <p class="text-gray-600 mb-3">No beer found matching "{searchTerm}"</p>
                        <button
                                type="button"
                                onclick={handleCreateBeer}
                                class="w-full bg-blue-500 text-white py-2 px-4 rounded-lg hover:bg-blue-600 transition-colors duration-200"
                        >
                            Create new beer: "{searchTerm}"
                        </button>
                    {:else}
                        <p class="text-gray-600">No beer found</p>
                    {/if}
                </div>
            {/if}
        </div>
    {/if}
</div>
