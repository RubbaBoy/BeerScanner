/**
 * Calls a callback function when a click event occurs outside the node.
 * @param {HTMLElement} node - The element to monitor for outside clicks.
 * @param {() => void} callback - The function to call when an outside click is detected.
 */
export function clickOutside(node: HTMLElement, callback: () => void) {
    const handleClick = (event: MouseEvent) => {
        if (node && !node.contains(event.target as Node) && !event.defaultPrevented) {
            callback();
        }
    };

    // Add event listener in the capture phase to catch clicks early
    document.addEventListener('click', handleClick, true);

    return {
        destroy() {
            // Clean up the event listener when the component is unmounted
            document.removeEventListener('click', handleClick, true);
        }
    };
}
