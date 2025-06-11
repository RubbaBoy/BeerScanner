<script lang="ts">
  import {onMount} from 'svelte';
  import {goto} from '$app/navigation';
  import {currentUser} from '$lib/stores';
  import type {Beer, BeerRequest} from '$lib/types';
  import CreateBeerForm from "$lib/components/CreateBeerForm.svelte";
  import {createBeer} from "$lib/services";


  let pageSuccessMessage = $state<string | null>(null);
  let pageErrorMessage = $state<string | null>(null);

  onMount(() => {
    // TODO: Add check for admin role
    if (!$currentUser) {
      goto('/login');
    }
  });

  const handleBeerCreated = async (event: CustomEvent<BeerRequest>) => {
    const newBeer: BeerRequest = event.detail;

    await createBeer(newBeer as Beer)
      .then(beer => {
        pageSuccessMessage = `Beer "${beer.name}" was successfully created! You can add another one.`;
        pageErrorMessage = null;
      })
  };

</script>

<div class="container mx-auto px-4 py-8">
  <h1 class="text-3xl font-bold mb-8 text-gray-800">Add New Beer</h1>

  {#if pageSuccessMessage}
    <div class="p-4 mb-6 text-sm text-green-700 bg-green-100 rounded-lg" role="alert">
      {pageSuccessMessage}
    </div>
  {/if}
  {#if pageErrorMessage}
    <div class="p-4 mb-6 text-sm text-red-700 bg-red-100 rounded-lg" role="alert">
      {pageErrorMessage}
    </div>
  {/if}

  <CreateBeerForm on:beerCreated={handleBeerCreated} />
</div>
