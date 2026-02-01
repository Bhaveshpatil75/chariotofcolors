/**
 * Film Roll Cinematic Animation
 * Handles horizontal scrolling, infinite looping, and dynamic visual effects.
 * Now with Touch & Mouse Drag support (Strict Scroll, No Momentum).
 */

document.addEventListener('DOMContentLoaded', () => {
    const track = document.querySelector('.film-roll-track');
    const container = document.querySelector('.film-roll-container');

    if (!track || !container) return;

    let originalItems = Array.from(track.querySelectorAll('.film-roll-item'));
    if (originalItems.length === 0) return;

    // --- Configuration ---
    const CONFIG = {
        baseSpeed: 1.0,       // Normal auto-scroll speed
        scaleCenter: 1.3,
        scaleSide: 0.8,
        blurSide: 5,
        opacitySide: 0.5,
        brightnessSide: 0.4,
        dragMultiplier: 1.5   // Sensitivity of drag
    };

    let items = [];
    let scrollPos = 0;
    let animationId;

    // Interaction State
    let isDragging = false;
    let lastX = 0;
    let isInteracting = false;
    let resumeAutoScrollTimer;

    // --- Initialization ---

    function init() {
        // Calculate initial total width
        let initialTotalWidth = 0;
        originalItems.forEach(item => {
            const style = window.getComputedStyle(item);
            initialTotalWidth += item.offsetWidth + parseFloat(style.marginLeft) + parseFloat(style.marginRight);
        });

        // If width is 0 (images not loaded), retry shortly or wait for load event interactions
        // We'll proceed with best guess, but observing is key.
        const requiredWidth = window.innerWidth * 4;
        const setWidth = initialTotalWidth || window.innerWidth; // Fallback
        const cloneCount = Math.max(2, Math.ceil(requiredWidth / setWidth));

        track.innerHTML = '';

        for (let i = 0; i < cloneCount; i++) {
            originalItems.forEach(item => {
                const clone = item.cloneNode(true);
                const img = clone.querySelector('img');
                if (img) {
                    img.draggable = false;
                    // Force a re-measure on load if stuck
                    img.onload = measureAndReflow;
                }
                track.appendChild(clone);
            });
        }

        items = Array.from(track.querySelectorAll('.film-roll-item'));
        measureAndReflow();

        // Start animation loop if not running
        if (!animationId) startAnimation();
    }

    function measureAndReflow() {
        let totalW = 0;
        items.forEach(item => {
            const style = window.getComputedStyle(item);
            const w = item.offsetWidth + parseFloat(style.marginLeft) + parseFloat(style.marginRight);
            item.dataset.width = w;
            item.dataset.left = totalW;
            totalW += w;
        });
    }

    // --- Interaction Handlers ---

    function handleStart(x) {
        isDragging = true;
        isInteracting = true;
        lastX = x;
        container.style.cursor = 'grabbing';

        // Stop any pending resume
        clearTimeout(resumeAutoScrollTimer);
    }

    function handleMove(x) {
        if (!isDragging) return;

        const delta = (x - lastX) * CONFIG.dragMultiplier;
        scrollPos -= delta;
        lastX = x;

        // Force immediate visual update for valid feel
        updateVisuals();
    }

    function handleEnd() {
        if (!isDragging) return;
        isDragging = false;
        container.style.cursor = 'grab';

        // STRICT SCROLL: No momentum. Stop instantly.
        // Resume auto-scroll after a short pause.
        resumeAutoScroll();
    }

    function handleWheel(e) {
        // Standard vertical scroll (deltaY) should SCROLL THE PAGE.
        // Horizontal scroll (deltaX) or Shift+Wheel should SCROLL THE FILM ROLL.

        const isHorizontal = Math.abs(e.deltaX) > Math.abs(e.deltaY);
        const isShiftKey = e.shiftKey;

        if (isHorizontal || isShiftKey) {
            e.preventDefault();

            isInteracting = true;
            clearTimeout(resumeAutoScrollTimer);

            let delta = e.deltaX;
            if (delta === 0 && isShiftKey) delta = e.deltaY;

            scrollPos += delta * 1.5;
            updateVisuals();
            resumeAutoScroll();
        }
    }

    function resumeAutoScroll() {
        clearTimeout(resumeAutoScrollTimer);
        resumeAutoScrollTimer = setTimeout(() => {
            isInteracting = false;
        }, 1000); // 1s pause before resuming auto-scroll
    }

    // Mouse Events
    container.addEventListener('mousedown', (e) => handleStart(e.pageX));
    window.addEventListener('mousemove', (e) => handleMove(e.pageX));
    window.addEventListener('mouseup', handleEnd);
    container.addEventListener('wheel', handleWheel, { passive: false });

    // Touch Events
    container.addEventListener('touchstart', (e) => handleStart(e.touches[0].pageX), { passive: true });
    window.addEventListener('touchmove', (e) => handleMove(e.touches[0].pageX), { passive: false });
    window.addEventListener('touchend', handleEnd);

    // --- Animation Loop ---

    function startAnimation() {
        if (animationId) cancelAnimationFrame(animationId);
        animationLoop();
    }

    function animationLoop() {
        // Only move if not interacting
        if (!isInteracting) {
            scrollPos += CONFIG.baseSpeed;
        }

        // Infinite Loop Logic
        const oneSetCount = originalItems.length;
        // Use the position of the item *after* the first set
        // Safe check if items exist
        if (items.length > oneSetCount) {
            const firstSetEnd = parseFloat(items[oneSetCount].dataset.left);
            if (firstSetEnd > 0) {
                if (scrollPos >= firstSetEnd) {
                    scrollPos -= firstSetEnd;
                } else if (scrollPos < 0) {
                    scrollPos += firstSetEnd;
                }
            }
        }

        updateVisuals();
        animationId = requestAnimationFrame(animationLoop);
    }

    function updateVisuals() {
        const center = window.innerWidth / 2;
        track.style.transform = `translate3d(${-scrollPos}px, 0, 0)`;

        items.forEach((item) => {
            const itemLeft = parseFloat(item.dataset.left) || 0;
            const itemWidth = parseFloat(item.dataset.width) || 300; // fallback

            const itemCenterScreen = (itemLeft - scrollPos) + (itemWidth / 2);
            const dist = Math.abs(center - itemCenterScreen);

            const maxDist = window.innerWidth / 1.5;
            const intensity = Math.min(1, dist / maxDist);

            const scale = CONFIG.scaleCenter - (intensity * (CONFIG.scaleCenter - CONFIG.scaleSide));
            const blur = intensity * CONFIG.blurSide;
            const brightness = 1 - (intensity * (1 - CONFIG.brightnessSide));
            const opacity = 1 - (intensity * (1 - CONFIG.opacitySide));

            item.style.transform = `scale(${scale})`;
            item.style.filter = `blur(${blur}px) brightness(${brightness})`;
            item.style.opacity = opacity;
            item.style.zIndex = Math.round((1 - intensity) * 100);

            if (dist < 100) {
                if (!item.classList.contains('in-focus')) item.classList.add('in-focus');
            } else {
                if (item.classList.contains('in-focus')) item.classList.remove('in-focus');
            }
        });
    }

    // Observers & Listeners
    let resizeTimer;
    window.addEventListener('resize', () => {
        clearTimeout(resizeTimer);
        resizeTimer = setTimeout(init, 200);
    });

    // Ensure layout is correct after all resources load
    window.addEventListener('load', () => {
        // Re-measure everything to be sure
        measureAndReflow();
    });

    // Start
    init();
});
