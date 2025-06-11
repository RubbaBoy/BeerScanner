<script lang="ts">
  import {createEventDispatcher} from 'svelte';
  import {getBeerTypes, createBeer} from '$lib/services';
  import type {Beer, BeerRequest, User} from '$lib/types';
  import {clickOutside} from '$lib/actions/clickOutside';
  import {currentUser} from '$lib/stores';

  let {initialName = ''} = $props<{
    initialName?: string;
  }>();

  // Component state
  let beer: BeerRequest = $state({
    name: initialName ?? '',
    brewery: '',
    type: '',
    abv: null,
    description: '',
    requestedBy: {id: $currentUser.id} as User
  });
  let isSubmitting = $state(false);
  let error = $state<string | null>(null);
  let success = $state<string | null>(null);

  // Beer type selection state
  const allBeerTypes = getBeerTypes(); // Assuming this is a synchronous call returning a static list
  let beerTypeSearchTerm = $state('');
  let filteredBeerTypes = $state<string[]>([]);
  let showBeerTypeDropdown = $state(false);

  const dispatch = createEventDispatcher<{ beerCreated: BeerRequest }>();

  // Reactive effect to filter beer types based on search term
  $effect(() => {
    if (!showBeerTypeDropdown) {
      filteredBeerTypes = [];
      return;
    }
    if (beerTypeSearchTerm.trim() === '') {
      // Show all types or a limited number if the list is very long when search is empty
      // For now, let's show a few or all if not too many.
      // Or, you might want to keep it empty until user types.
      filteredBeerTypes = allBeerTypes.slice(0, 10); // Example: show top 10 or all
    } else {
      filteredBeerTypes = allBeerTypes.filter(type =>
        type.toLowerCase().includes(beerTypeSearchTerm.toLowerCase())
      );
    }
  });

  const handleBeerTypeInput = (event: Event) => {
    const inputElement = event.target as HTMLInputElement;
    beerTypeSearchTerm = inputElement.value;
    beer.type = ''; // Clear selected type if user is typing
    showBeerTypeDropdown = true;
  };

  const selectBeerType = (typeOption: string) => {
    beer.type = typeOption;
    beerTypeSearchTerm = typeOption; // Update search term to reflect selection
    showBeerTypeDropdown = false;
    filteredBeerTypes = [];
  };

  const resetForm = () => {
    beer = {
      name: '',
      brewery: '',
      type: '',
      abv: null,
      description: '',
      requestedBy: {id: $currentUser.id} as User
    };
    beerTypeSearchTerm = '';
    error = null;
    success = null; // Clear success message on reset for next use
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    isSubmitting = true;
    error = null;
    success = null;

    if (!beer.name || !beer.brewery || !beer.type) {
      error = 'Name, Brewery, and Type are required fields.';
      isSubmitting = false;
      return;
    }
    if (beer.abv !== null && (isNaN(beer.abv) || beer.abv < 0 || beer.abv > 100)) {
      error = 'ABV must be a number between 0 and 100.';
      isSubmitting = false;
      return;
    }


    // try {
    //   const newBeer = await createBeer(beer);
    //   success = `Beer "${newBeer.name}" created successfully!`;
    dispatch('beerCreated', beer);
    resetForm(); // Reset form fields for potential next entry
    isSubmitting = false;
    // } catch (e: any) {
    //   console.error('Failed to create beer:', e);
    //   error = e.response?.data?.message || e.message || 'Failed to create beer. Please try again.';
    // } finally {
    // }
  };

  function closeDropdown() {
    showBeerTypeDropdown = false;
  }
</script>

<form onsubmit={handleSubmit} class="space-y-6 bg-white p-6 sm:p-8 rounded-lg shadow-md">
    {#if success}
        <div class="p-4 mb-4 text-sm text-green-700 bg-green-100 rounded-lg" role="alert">
            {success}
        </div>
    {/if}
    {#if error}
        <div class="p-4 mb-4 text-sm text-red-700 bg-red-100 rounded-lg" role="alert">
            {error}
        </div>
    {/if}

    <div>
        <label for="beerName" class="block text-sm font-medium text-gray-700">Name</label>

        <input
                type="text"
                id="beerName"
                bind:value={beer.name}
                required
                class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                placeholder="e.g., Glacier IPA"
        />
    </div>

    <div>
        <label for="brewery" class="block text-sm font-medium text-gray-700">Brewery</label>
        <input
                type="text"
                id="brewery"
                bind:value={beer.brewery}
                required
                class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                placeholder="e.g., Mountain Top Brewery"
        />
    </div>

    <div class="relative" use:clickOutside={closeDropdown} onfocusout={(e) => {
        // Delay closing to allow click on dropdown item
        if (!e.currentTarget.contains(e.relatedTarget)) {
            setTimeout(() => {
                if (document.activeElement && (!e.currentTarget || !e.currentTarget.contains(document.activeElement))) {
                    showBeerTypeDropdown = false;
                }
            }, 100);
        }
      }}>
        <label for="beerType" class="block text-sm font-medium text-gray-700">Type</label>
        <input
                type="text"
                id="beerType"
                bind:value={beerTypeSearchTerm}
                oninput={handleBeerTypeInput}
                onfocus={() => showBeerTypeDropdown = true}
                autocomplete="off"
                required
                class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                placeholder="e.g., IPA, Stout, Lager"
        />
        {#if showBeerTypeDropdown && (filteredBeerTypes.length > 0 || beerTypeSearchTerm.trim() !== '')}
            <ul
                    class="absolute z-10 mt-1 w-full bg-white border border-gray-300 rounded-md shadow-lg max-h-60 overflow-auto"

            >
                {#if filteredBeerTypes.length > 0}
                    {#each filteredBeerTypes as typeOption (typeOption)}
                        <li
                                class="px-3 py-2 text-sm text-gray-700 hover:bg-indigo-500 hover:text-white cursor-pointer"
                                onclick={() => selectBeerType(typeOption)}
                                onkeydown={(e) => {
                if (e.key === 'Enter' || e.key === ' ') {
                  e.preventDefault();
                  selectBeerType(typeOption);
                }
              }}
                                tabindex="0"
                                role="option"
                                aria-selected={beer.type === typeOption}
                        >
                            {typeOption}
                        </li>
                    {/each}
                {:else if beerTypeSearchTerm.trim() !== ''}
                    <li class="px-3 py-2 text-sm text-gray-500">
                        No types match "{beerTypeSearchTerm}". You can still submit this new type.
                        <button type="button"
                                class="ml-2 text-indigo-600 hover:text-indigo-800 text-xs"
                                onclick={() => {beer.type = beerTypeSearchTerm; showBeerTypeDropdown = false;}}>
                            Use "{beerTypeSearchTerm}"
                        </button>
                    </li>
                {/if}
            </ul>
        {/if}
    </div>
    {#if beer.type}
        <p class="text-xs text-gray-500 mt-1">Selected type: <span class="font-semibold">{beer.type}</span></p>
    {/if}


    <div>
        <label for="abv" class="block text-sm font-medium text-gray-700">ABV (%)</label>
        <input
                type="number"
                id="abv"
                bind:value={beer.abv}
                step="0.1"
                min="0"
                max="100"
                class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                placeholder="e.g., 5.5"
        />
    </div>

    <div>
        <label for="description" class="block text-sm font-medium text-gray-700">Description (Optional)</label>
        <textarea
                id="description"
                bind:value={beer.description}
                rows="4"
                class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                placeholder="e.g., A hoppy IPA with citrus notes."
        ></textarea>
    </div>

    <div>
        <button
                type="submit"
                disabled={isSubmitting}
                class="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50"
        >
            {isSubmitting ? 'Creating...' : 'Create Beer'}
        </button>
    </div>
</form>
