// Initialize AOS
document.addEventListener('DOMContentLoaded', () => {
    // Check if AOS is loaded
    if (typeof AOS !== 'undefined') {
        AOS.init({
            duration: 800,
            easing: 'ease-out-cubic',
            once: false, // Animation key: repeat animations on scroll
            mirror: true, // Animation key: animate out while scrolling past
            offset: 120,
            delay: 0,
        });
    } else {
        // Fallback: If AOS fails to load, reveal all hidden elements
        console.warn('AOS library not loaded. Revealing content.');
        const hiddenElements = document.querySelectorAll('[data-aos]');
        hiddenElements.forEach(el => {
            el.removeAttribute('data-aos');
            el.style.opacity = '1';
            el.style.transform = 'none';
        });
    }

    // GSAP Hero Animations
    if (typeof gsap !== 'undefined') {
        // Ensure initial state is set immediately to avoid flash or conflict
        // We set opacity to 0 initially so GSAP can fade it in
        gsap.set([".hero h1", ".hero .quote", ".hero p:not(.quote)", ".hero .btn"], { autoAlpha: 0 });

        const tl = gsap.timeline({ defaults: { ease: "power3.out" } });

        tl.fromTo(".hero h1",
            { y: 50, autoAlpha: 0 },
            { y: 0, autoAlpha: 1, duration: 1.2, delay: 0.2 }
        )
            .fromTo(".hero .quote",
                { y: 30, autoAlpha: 0, scale: 0.95 },
                { y: 0, autoAlpha: 1, scale: 1, duration: 1, color: "#ffffff" }, // Ensure full opacity and white color
                "-=0.8"
            )
            .fromTo(".hero p:not(.quote)",
                { y: 30, autoAlpha: 0 },
                { y: 0, autoAlpha: 1, duration: 1 },
                "-=0.6"
            )
            .fromTo(".hero .btn",
                { scale: 0.8, autoAlpha: 0 },
                { scale: 1, autoAlpha: 1, duration: 0.8, ease: "back.out(1.7)" },
                "-=0.6"
            );

        // Parallax-like floating for logo and icon
        gsap.to([".logo-text-img", ".nav-logo-img"], {
            y: 8,
            duration: 2,
            yoyo: true,
            repeat: -1,
            ease: "sine.inOut"
        });
    }

    // Scroll Progress Bar
    window.addEventListener('scroll', () => {
        const scrollTop = document.documentElement.scrollTop || document.body.scrollTop;
        const scrollHeight = document.documentElement.scrollHeight - document.documentElement.clientHeight;
        const scrolled = (scrollTop / scrollHeight) * 100;
        const progressBar = document.getElementById('scrollProgress');
        if (progressBar) {
            progressBar.style.width = scrolled + "%";
        }
    });
});
