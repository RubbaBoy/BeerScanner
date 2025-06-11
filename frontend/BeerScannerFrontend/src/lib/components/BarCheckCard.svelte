<script lang="ts">
  import type { BarCheck } from '$lib/types';
  import { format } from 'date-fns';

  interface Props {
    check: BarCheck;
    showBar?: boolean;
    compact?: boolean;
  }

  let { check, showBar = true, compact = false }: Props = $props();

  // Status styling
  const getStatusClass = (status: string) => {
    switch (status) {
      case 'COMPLETED':
        return 'bg-green-100 text-green-800 border-green-200';
      case 'PROCESSING':
        return 'bg-blue-100 text-blue-800 border-blue-200';
      case 'FAILED':
        return 'bg-red-100 text-red-800 border-red-200';
      case 'PENDING':
        return 'bg-yellow-100 text-yellow-800 border-yellow-200';
      default:
        return 'bg-gray-100 text-gray-800 border-gray-200';
    }
  };

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'COMPLETED':
        return '✓';
      case 'PROCESSING':
        return '⟳';
      case 'FAILED':
        return '✗';
      case 'PENDING':
        return '⏳';
      default:
        return '?';
    }
  };
</script>

<div class="bg-white rounded-lg shadow-sm border border-gray-200 hover:shadow-md transition-shadow duration-200">
    <div class="p-4 {compact ? 'py-3' : ''}">
        <!-- Header -->
        <div class="flex items-start justify-between mb-3">
            <div class="flex-1">
                {#if showBar && check.bar}
                    <h3 class="font-semibold text-gray-900 text-lg mb-1">{check.bar.name}</h3>
                    {#if check.bar.location}
                        <p class="text-sm text-gray-600">{check.bar.location}</p>
                    {/if}
                {/if}
                {#if check.id}
                    <p class="text-xs text-gray-500 mt-1">Check #{check.id}</p>
                {/if}
            </div>

            <!-- Status Badge -->
            <div class="flex items-center space-x-2">
        <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium border {getStatusClass(check.processingStatus)}">
          <span class="mr-1">{getStatusIcon(check.processingStatus)}</span>
            {check.processingStatus}
        </span>
            </div>
        </div>

        <!-- Content -->
        <div class="space-y-2">
            {#if check.hasChanges !== undefined}
                <div class="flex items-center space-x-2">
                    <span class="text-sm font-medium text-gray-700">Menu Changes:</span>
                    <span class="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium {check.hasChanges ? 'bg-orange-100 text-orange-800' : 'bg-gray-100 text-gray-800'}">
            {check.hasChanges ? '📝 Changes Detected' : '📋 No Changes'}
          </span>
                </div>
            {/if}

            {#if check.menuHash}
                <div class="flex items-center space-x-2">
                    <span class="text-sm font-medium text-gray-700">Menu Hash:</span>
                    <code class="text-xs bg-gray-100 px-2 py-1 rounded font-mono">{check.menuHash.substring(0, 12)}...</code>
                </div>
            {/if}

            {#if check.errorMessage}
                <div class="mt-3 p-3 bg-red-50 border border-red-200 rounded-md">
                    <p class="text-sm text-red-800">
                        <span class="font-medium">Error:</span> {check.errorMessage}
                    </p>
                </div>
            {/if}

            {#if check.createdAt}
                <div class="flex items-center space-x-2 text-sm text-gray-500">
                    <span>Created:</span>
                    <time datetime={check.createdAt}>
                        {format(new Date(check.createdAt), 'MMM d, yyyy h:mm a')}
                    </time>
                </div>
            {/if}
        </div>

        <!-- Menu Content Preview (if available and not compact) -->
        {#if !compact && check.menuContent && check.menuContent.length > 0}
            <div class="mt-3 pt-3 border-t border-gray-200">
                <details class="group">
                    <summary class="cursor-pointer text-sm font-medium text-gray-700 hover:text-gray-900 list-none">
            <span class="inline-flex items-center">
              <span class="group-open:rotate-90 transition-transform duration-200 mr-1">▶</span>
              Menu Content Preview
            </span>
                    </summary>
                    <div class="mt-2 p-3 bg-gray-50 rounded-md max-h-32 overflow-y-auto">
                        <pre class="text-xs text-gray-700 whitespace-pre-wrap">{check.menuContent.substring(0, 500)}{check.menuContent.length > 500 ? '...' : ''}</pre>
                    </div>
                </details>
            </div>
        {/if}
    </div>
</div>
