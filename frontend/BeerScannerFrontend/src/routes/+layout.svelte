<script lang="ts">
	import '../app.css';
	import { Header, Footer } from '$lib/components';
	import { onMount } from 'svelte';
	import { initAuthStore } from '$lib/stores';

	let { children } = $props();

	// Initialize auth store on mount
	onMount(() => {
		initAuthStore();
	});
</script>

<div class="flex flex-col min-h-screen bg-gray-50">
	<Header />

	<main class="flex-grow container mx-auto px-4 py-8">
		{@render children()}
	</main>

	<Footer />
</div>
