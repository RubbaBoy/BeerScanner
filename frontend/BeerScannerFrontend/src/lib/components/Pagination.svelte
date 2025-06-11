<script lang="ts">
  let { totalPages, currentPage, onPageChange, showPageNumbers = true } = $props<{
    totalPages: number;
    currentPage: number;
    onPageChange: (page: number) => void;
    showPageNumbers?: boolean;
  }>();

  // Calculate the range of page numbers to display
  const getPageRange = () => {
    const range: number[] = [];
    const maxPagesToShow = 5;

    if (totalPages <= maxPagesToShow) {
      // If we have fewer pages than the max, show all pages
      for (let i = 0; i < totalPages; i++) {
        range.push(i);
      }
    } else {
      // Always include the first page
      range.push(0);

      // Calculate the middle range
      let startPage = Math.max(1, currentPage - 1);
      let endPage = Math.min(totalPages - 2, currentPage + 1);

      // Adjust if we're near the beginning
      if (currentPage < 2) {
        endPage = 3;
      }

      // Adjust if we're near the end
      if (currentPage > totalPages - 3) {
        startPage = totalPages - 4;
      }

      // Add ellipsis after first page if needed
      if (startPage > 1) {
        range.push(-1); // -1 represents ellipsis
      }

      // Add the middle range
      for (let i = startPage; i <= endPage; i++) {
        range.push(i);
      }

      // Add ellipsis before last page if needed
      if (endPage < totalPages - 2) {
        range.push(-1); // -1 represents ellipsis
      }

      // Always include the last page
      range.push(totalPages - 1);
    }

    return range;
  };

  // Handle page change
  const handlePageChange = (page: number) => {
    if (page < 0 || page >= totalPages || page === currentPage) return;
    onPageChange(page);
  };
</script>

{#if totalPages > 1}
  <div class="flex justify-center items-center space-x-2 my-6">
    <!-- Previous button -->
    <button 
      onclick={() => handlePageChange(currentPage - 1)}
      disabled={currentPage === 0}
      class={`px-3 py-1 rounded-md focus:outline-none ${
        currentPage === 0 
          ? 'bg-gray-100 text-gray-400 cursor-not-allowed' 
          : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
      }`}
      aria-label="Previous page"
    >
      <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
        <path fill-rule="evenodd" d="M12.707 5.293a1 1 0 010 1.414L9.414 10l3.293 3.293a1 1 0 01-1.414 1.414l-4-4a1 1 0 010-1.414l4-4a1 1 0 011.414 0z" clip-rule="evenodd" />
      </svg>
    </button>

    <!-- Page numbers -->
    {#if showPageNumbers}
      {#each getPageRange() as page}
        {#if page === -1}
          <span class="px-3 py-1">...</span>
        {:else}
          <button 
            onclick={() => handlePageChange(page)}
            class={`px-3 py-1 rounded-md focus:outline-none ${
              page === currentPage 
                ? 'bg-amber-500 text-white' 
                : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
            }`}
          >
            {page + 1}
          </button>
        {/if}
      {/each}
    {:else}
      <span class="px-3 py-1">Page {currentPage + 1} of {totalPages}</span>
    {/if}

    <!-- Next button -->
    <button 
      onclick={() => handlePageChange(currentPage + 1)}
      disabled={currentPage === totalPages - 1}
      class={`px-3 py-1 rounded-md focus:outline-none ${
        currentPage === totalPages - 1 
          ? 'bg-gray-100 text-gray-400 cursor-not-allowed' 
          : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
      }`}
      aria-label="Next page"
    >
      <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
        <path fill-rule="evenodd" d="M7.293 14.707a1 1 0 010-1.414L10.586 10 7.293 6.707a1 1 0 011.414-1.414l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0z" clip-rule="evenodd" />
      </svg>
    </button>
  </div>
{/if}
