/**
 * Film Roll Cinematic Animation
 * Handles horizontal scrolling, infinite looping, and dynamic visual effects.
 */

document.addEventListener('DOMContentLoaded', () => {
    const track = document.querySelector('.film-roll-track');
    if (!track) return; // Exit if no film roll on page

    const container = document.querySelector('.film-roll-container');
    let originalItems = Array.from(track.querySelectorAll('.film-roll-item'));

    if (originalItems.length === 0) return;

    // --- Configuration ---
    const CONFIG = {
        speed: 1.0,           // Pixels per frame. Lower = slower/more cinematic.
        scaleCenter: 1.3,     // Scale of component in center
        scaleSide: 0.8,       // Scale of components at valid edges
        blurSide: 5,          // Max blur in px
        opacitySide: 0.5,     // Min opacity
        brightnessSide: 0.4,  // Min brightness
        focusZone: 0.3        // Fraction of screen width that is "near center"
    };

    let items = [];
    let trackWidth = 0;
    let scrollPos = 0;
    let isHovered = false;
    let animationId;

    // --- Initialization ---

    function init() {
        // Clone items to fill sufficient space for infinite scroll
        // We need at least enough width to cover the screen plus buffers.
        // A safe bet is 3x or 4x the screen width, or just enough sets to ensure smoothness.
        // For a true infinite loop by resetting position, we typically need 2 sets visible + 1 buffer.
        // Let's simplified approach: Just duplicate enough times to be huge, or use modulo arithmetic logic.

        // Better approach for performance: 
        // 1. Calculate total width of one set.
        // 2. Clone until total width > 2 * window.innerWidth + 1 set width.

        const itemStyle = window.getComputedStyle(originalItems[0]);
        const itemWidth = originalItems[0].offsetWidth +
            parseFloat(itemStyle.marginLeft) +
            parseFloat(itemStyle.marginRight);

        const setWidth = itemWidth * originalItems.length;
        const requiredWidth = window.innerWidth * 3;

        // Clone original items to fill required width
        const cloneCount = Math.max(2, Math.ceil(requiredWidth / setWidth));

        // Clear track and rebuild
        track.innerHTML = '';

        for (let i = 0; i < cloneCount; i++) {
            originalItems.forEach(item => {
                const clone = item.cloneNode(true);
                track.appendChild(clone);
            });
        }

        // Re-query items
        items = Array.from(track.querySelectorAll('.film-roll-item'));

        // Calculate total track width based on all items
        trackWidth = items.length * itemWidth; // Approximation

        // Precise calculation
        let totalW = 0;
        items.forEach(item => {
            const style = window.getComputedStyle(item);
            const w = item.offsetWidth + parseFloat(style.marginLeft) + parseFloat(style.marginRight);
            // Store specific width/offset on item for perf
            item.dataset.width = w;
            item.dataset.left = totalW;
            totalW += w;
        });
        trackWidth = totalW;

        // Start animation
        startAnimation();
    }

    // --- Animation Loop ---

    function startAnimation() {
        if (animationId) cancelAnimationFrame(animationId);
        animationLoop();
    }

    function animationLoop() {
        if (!isHovered) {
            scrollPos += CONFIG.speed;
        }

        // Infinite loop reset
        // If we have moved past 1/3 of the total width (assuming 3 sets), we can snap back?
        // Actually, simple Modulo logic:
        // However, we just want to reset when the FIRST SET has completely scrolled off.
        const oneSetCount = originalItems.length;
        const oneSetWidth = parseFloat(items[oneSetCount].dataset.left);

        if (scrollPos >= oneSetWidth) {
            scrollPos -= oneSetWidth;
        }

        // Apply visual state
        updateVisuals();

        animationId = requestAnimationFrame(animationLoop);
    }

    function updateVisuals() {
        // Center of the viewport
        const center = window.innerWidth / 2;

        // Move the track
        // We use translate3d for GPU acceleration
        track.style.transform = `translate3d(${-scrollPos}px, 0, 0)`;

        // Update each item's individual style based on its SCREEN position
        items.forEach((item) => {
            const itemLeft = parseFloat(item.dataset.left);
            const itemWidth = parseFloat(item.dataset.width);

            // Current position of item's center relative to viewport left
            // itemScreenX = (itemLeft - scrollPos) + itemWidth/2
            const itemCenterScreen = (itemLeft - scrollPos) + (itemWidth / 2);

            // Distance from center
            const dist = Math.abs(center - itemCenterScreen);

            // Calculate Effect Intensity (0 to 1)
            // 0 = at center, 1 = far away
            // range = window.innerWidth / 2 approx
            const maxDist = window.innerWidth / 1.5;
            const intensity = Math.min(1, dist / maxDist);

            // Effects
            const scale = CONFIG.scaleCenter - (intensity * (CONFIG.scaleCenter - CONFIG.scaleSide));
            const blur = intensity * CONFIG.blurSide;
            const brightness = 1 - (intensity * (1 - CONFIG.brightnessSide));
            const opacity = 1 - (intensity * (1 - CONFIG.opacitySide));

            // Apply styles
            // Use property specific transforms to avoid overwriting layout
            // Note: Item itself doesn't move, the track does. We just Scale it.
            item.style.transform = `scale(${scale})`;
            item.style.filter = `blur(${blur}px) brightness(${brightness})`;
            item.style.opacity = opacity;
            item.style.zIndex = Math.round((1 - intensity) * 100);

            // Active class for info visibility
            // If very close to center (e.g. within 10% of width)
            if (dist < 100) {
                if (!item.classList.contains('in-focus')) {
                    item.classList.add('in-focus');
                }
            } else {
                if (item.classList.contains('in-focus')) {
                    item.classList.remove('in-focus');
                }
            }
        });
    }

    // --- Event Listeners ---

    // Pause on hover - REMOVED as per user request
    // container.addEventListener('mouseenter', () => { isHovered = true; });
    // container.addEventListener('mouseleave', () => { isHovered = false; });

    // Resize handler (Throttle would be good, but simple init recall works for now)
    let resizeTimer;
    window.addEventListener('resize', () => {
        clearTimeout(resizeTimer);
        resizeTimer = setTimeout(init, 200);
    });

    // Handle touch scroll manually if needed, or let user drag?
    // "Continuous horizontal ... moving like a film reel" usually implies auto-move.
    // Making it draggable is nice but complex. Let's stick to simple auto-scroll + hover pause for now.

    // Run
    init();
});
