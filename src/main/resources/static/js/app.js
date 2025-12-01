document.addEventListener('DOMContentLoaded', () => {
    loadReusableComponents();
    
    // Tambahkan class 'loaded' ke body untuk transisi fade-in
    document.body.classList.add('loaded');
});

/**
 * Memuat komponen reusable (Navbar, Footer, & Admin Sidebar)
 */
async function loadReusableComponents() {
    const navbarPlaceholder = document.getElementById('navbar-placeholder');
    const footerPlaceholder = document.getElementById('footer-placeholder');
    const sidebarPlaceholder = document.getElementById('admin-sidebar-placeholder');

    try {
        // Load Navbar
        if (navbarPlaceholder) {
            const response = await fetch('/components/navbar.html');
            const html = await response.text();
            navbarPlaceholder.innerHTML = html;
            // Setelah HTML dimuat, jalankan logika untuk Navbar
            initializeNavbarLogic();
        }

        // Load Admin Sidebar (jika ada placeholder)
        if (sidebarPlaceholder) {
            const response = await fetch('/components/admin-sidebar.html');
            const html = await response.text();
            sidebarPlaceholder.innerHTML = html;
        }

        // Load Footer
        if (footerPlaceholder) {
            const response = await fetch('/components/footer.html');
            const html = await response.text();
            footerPlaceholder.innerHTML = html;
        }
    } catch (error) {
        console.error('Error loading reusable components:', error);
    }
}

/**
 * Menjalankan semua logika interaktif untuk Navbar
 */
function initializeNavbarLogic() {
    const mobileMenuButton = document.getElementById('mobile-menu-button');
    const mobileMenu = document.getElementById('mobile-menu');

    // 1. Logika Buka/Tutup Menu Mobile
    if (mobileMenuButton && mobileMenu) {
        mobileMenuButton.addEventListener('click', () => {
            mobileMenu.classList.toggle('hidden');
        });
    }

    // 2. Logika Update Status Autentikasi (Login/Logout)
    updateNavbarAuthState();
    
    // 3. Logika Menandai Link Aktif
    highlightActiveNavLink();
    
    // 4. Setup Admin Menu Dropdown
    setupAdminMenuDropdown();
}

/**
 * Memperbarui tampilan Navbar berdasarkan status login
 */
function updateNavbarAuthState() {
    const isAuthenticated = window.auth.isAuthenticated();
    const userName = window.auth.getUserName();
    
    // Target placeholder di Navbar
    const desktopActions = document.getElementById('nav-actions-desktop');
    const mobileActions = document.getElementById('nav-actions-mobile');
    const desktopLinks = document.getElementById('nav-links-desktop');
    const mobileLinks = document.getElementById('nav-links-mobile');
    
    if (!desktopActions || !mobileActions || !desktopLinks || !mobileLinks) return;

    // Kosongkan placeholder
    desktopActions.innerHTML = '';
    mobileActions.innerHTML = '';
    desktopLinks.innerHTML = '';
    mobileLinks.innerHTML = '';

    if (isAuthenticated) {
        // TAMPILAN JIKA SUDAH LOGIN
        const user = window.auth.getUser();
        const isAdmin = user && user.role === 'ADMIN';
        
        // Show/Hide Admin Menu
        const adminMenuDesktop = document.getElementById('admin-menu-desktop');
        const adminMenuMobile = document.getElementById('admin-menu-mobile');
        if (adminMenuDesktop) adminMenuDesktop.classList.toggle('hidden', !isAdmin);
        if (adminMenuMobile) adminMenuMobile.classList.toggle('hidden', !isAdmin);
        
        // Links (Desktop)
        let desktopLinksHtml = `
            <a href="/promo.html" class="nav-link" data-page="promo">Promo</a>
            <a href="/orders.html" class="nav-link" data-page="orders">My Orders</a>
            <a href="/wishlist.html" class="nav-link" data-page="wishlist">Wishlist</a>
            <a href="/notifications.html" class="nav-link" data-page="notifications"> Notifications</a>
        `;
        if (isAdmin) {
            desktopLinksHtml = `
                <a href="/admin-dashboard.html" class="nav-link" data-page="admin">Admin Dashboard</a>
                <a href="/notifications.html" class="nav-link" data-page="notifications"> Notifications</a>
            `;
        }
        desktopLinks.innerHTML = desktopLinksHtml;
        
        // Actions (Desktop)
        desktopActions.innerHTML = `
            <a href="/notifications.html" class="text-gray-600 hover:text-primary-600" title="Notifications">
                <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
                </svg>
            </a>
            <a href="/profile.html" class="flex items-center gap-2 text-sm font-medium text-gray-700 hover:text-primary-600">
                <span class="w-8 h-8 rounded-full bg-primary-100 text-primary-600 flex items-center justify-center font-semibold">
                    ${userName.charAt(0).toUpperCase()}
                </span>
                ${userName.split(' ')[0]} </a>
            <button id="logout-button-desktop" class="text-sm font-medium text-gray-600 hover:text-primary-600">Logout</button>
        `;
        
        // Links (Mobile)
        let mobileLinksHtml = `
            <a href="/profile.html" class="nav-link-mobile" data-page="profile">My Profile</a>
            <a href="/edit-profile.html" class="nav-link-mobile" data-page="edit-profile">Edit Profile</a>
            <a href="/promo.html" class="nav-link-mobile" data-page="promo">Promo</a>
            <a href="/orders.html" class="nav-link-mobile" data-page="orders">My Orders</a>
            <a href="/wishlist.html" class="nav-link-mobile" data-page="wishlist">Wishlist</a>
            <a href="/notifications.html" class="nav-link-mobile" data-page="notifications">Notifications</a>
        `;
        if (isAdmin) {
            mobileLinksHtml = `
                <a href="/admin-dashboard.html" class="nav-link-mobile" data-page="admin">Admin Dashboard</a>
                <a href="/profile.html" class="nav-link-mobile" data-page="profile">My Profile</a>
                <a href="/edit-profile.html" class="nav-link-mobile" data-page="edit-profile">Edit Profile</a>
                <a href="/notifications.html" class="nav-link-mobile" data-page="notifications">Notifications</a>
            `;
        }
        mobileLinks.innerHTML = mobileLinksHtml;
        
        // Actions (Mobile)
        mobileActions.innerHTML = `
            <button id="logout-button-mobile" class="w-full text-left px-3 py-2 rounded-md text-base font-medium text-red-600 hover:bg-red-50">
                Logout
            </button>
        `;
        
    } else {
        // TAMPILAN JIKA BELUM LOGIN
        
        // Actions (Desktop)
        desktopActions.innerHTML = `
            <a href="/login.html" class="text-sm font-medium text-gray-600 hover:text-primary-600">Login</a>
            <a href="/register.html" class="ml-4 px-4 py-2 rounded-md text-sm font-medium text-white bg-primary-500 hover:bg-primary-600">
                Sign Up
            </a>
        `;
        
        // Actions (Mobile)
        mobileActions.innerHTML = `
            <a href="/login.html" class="block w-full text-center px-4 py-2 rounded-md font-medium text-gray-700 bg-gray-100 hover:bg-gray-200">
                Login
            </a>
            <a href="/register.html" class="mt-2 block w-full text-center px-4 py-2 rounded-md font-medium text-white bg-primary-500 hover:bg-primary-600">
                Sign Up
            </a>
        `;
    }
    
    // Tambahkan event listener untuk tombol logout yang baru dibuat
    addLogoutButtonListeners();
}

/**
 * Menambahkan event listener ke tombol logout
 */
function addLogoutButtonListeners() {
    const logoutDesktop = document.getElementById('logout-button-desktop');
    const logoutMobile = document.getElementById('logout-button-mobile');
    
    const handleLogout = () => {
        window.auth.removeUser();
        localStorage.removeItem('authToken');
        alert('Anda telah logout.');
        window.location.href = '/login.html'; // Arahkan ke halaman login
    };

    if (logoutDesktop) {
        logoutDesktop.addEventListener('click', handleLogout);
    }
    if (logoutMobile) {
        logoutMobile.addEventListener('click', handleLogout);
    }
}

/**
 * Memberi highlight pada link navigasi yang sedang aktif
 */
function highlightActiveNavLink() {
    const currentPage = document.body.dataset.page;
    if (!currentPage) return;
    
    const navLinks = document.querySelectorAll(`.nav-link[data-page="${currentPage}"], .nav-link-mobile[data-page="${currentPage}"]`);
    
    navLinks.forEach(link => {
        link.setAttribute('data-active', 'true');
    });
}

// (Kode yang sudah ada sebelumnya tetap di atas)

// === LOGIKA SPESIFIK HALAMAN ===

// Panggil fungsi inisialisasi berdasarkan halaman yang aktif
document.addEventListener('DOMContentLoaded', () => {
    const page = document.body.dataset.page;
    
    if (page === 'home') {
        initializeHomepage();
    } else if (page === 'events') {
        initializeEventsPage();
    }
    // Tambahkan else if untuk halaman lain nanti
});


// --- Homepage Logic ---
async function initializeHomepage() {
    console.log("Initializing Homepage...");
    loadHomepageCategories();
    loadHomepageEvents('popular', '#popular-event-grid', 8); // Tampilkan 8 event populer
    loadHomepageEvents('upcoming', '#upcoming-event-grid', 8); // Tampilkan 8 event upcoming
    
    // Search handler
    const searchInput = document.getElementById('home-search-input');
    if (searchInput) {
        searchInput.addEventListener('keypress', (e) => {
            if (e.key === 'Enter' && searchInput.value.trim()) {
                window.location.href = `/events.html?search=${encodeURIComponent(searchInput.value.trim())}`;
            }
        });
    }
}

async function loadHomepageCategories() {
    const grid = document.getElementById('category-grid');
    if (!grid) return;

    try {
        const categories = await apiFetch('/categories');
        
        // Mapping ikon sederhana (bisa diperluas)
        const categoryIcons = {
            'Konser Musik': `<svg xmlns="http://www.w3.org/2000/svg" class="w-6 h-6 lg:w-8 lg:h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5"><path stroke-linecap="round" stroke-linejoin="round" d="M9 19V6l12-3v13M9 19c0 1.105-1.343 2-3 2s-3-.895-3-2 1.343-2 3-2 3 .895 3 2zm12-3c0 1.105-1.343 2-3 2s-3-.895-3-2 1.343-2 3-2 3 .895 3 2zM9 10l12-3" /></svg>`,
            'Festival': `<svg xmlns="http://www.w3.org/2000/svg" class="w-6 h-6 lg:w-8 lg:h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5"><path stroke-linecap="round" stroke-linejoin="round" d="M14.828 14.828a4 4 0 01-5.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>`,
            // Tambahkan ikon lain jika perlu
        };
        const defaultIcon = categoryIcons['Konser Musik']; // Fallback icon

        grid.innerHTML = categories.map((cat, index) => `
            <a href="/events.html?categoryId=${cat.idKategori}" 
               class="group block bg-gradient-to-br from-gray-50 to-gray-100 hover:from-primary-50 hover:to-primary-100 rounded-2xl p-6 lg:p-8 transition duration-300 ease-in-out border-2 border-transparent hover:border-primary-200 transform hover:-translate-y-1 animate-fade-in-up" 
               style="animation-delay: ${index * 100}ms;">
                <div class="w-12 h-12 lg:w-16 lg:h-16 bg-white rounded-xl flex items-center justify-center mb-4 group-hover:bg-primary-500 transition duration-300 shadow-md text-gray-600 group-hover:text-white">
                    ${categoryIcons[cat.namaKategori] || defaultIcon}
                </div>
                <h3 class="font-semibold text-gray-900 mb-1 text-sm lg:text-base line-clamp-1">${cat.namaKategori}</h3>
                <p class="text-xs lg:text-sm text-gray-600 line-clamp-2">${cat.deskripsi || 'Explore events'}</p>
            </a>
        `).join('');
    } catch (error) {
        grid.innerHTML = '<p class="text-red-500 col-span-full">Gagal memuat kategori.</p>';
    }
}

async function loadHomepageEvents(type, gridSelector, limit) {
    const grid = document.querySelector(gridSelector);
    if (!grid) return;

    try {
        let events = [];
        if (type === 'popular') {
            events = await apiFetch('/events/popular');
        } else { // upcoming
            events = await apiFetch('/events/upcoming');
        }
        
        // Batasi jumlah event dan render
        renderEventGrid(events.slice(0, limit), gridSelector);
        
    } catch (error) {
        grid.innerHTML = `<p class="text-red-500 col-span-full">Gagal memuat ${type} event.</p>`;
    }
}

// --- Events Page Logic ---
if (typeof window.allEventsData === 'undefined') {
    window.allEventsData = []; // Cache data event
}
if (typeof window.currentFilters === 'undefined') {
    window.currentFilters = {}; // State filter
}

async function initializeEventsPage() {
    console.log("Initializing Events Page...");
    await loadFilterOptions();
    
    // Baca filter awal dari URL (jika ada)
    const urlParams = new URLSearchParams(window.location.search);
    window.currentFilters = {
        search: urlParams.get('search') || '',
        categoryId: urlParams.get('categoryId') || '',
        city: urlParams.get('city') || '',
        startDate: urlParams.get('startDate') || '',
        endDate: urlParams.get('endDate') || '',
    };
    // Set nilai awal di form filter
    document.getElementById('search-input').value = window.currentFilters.search;
    document.getElementById('filter-category').value = window.currentFilters.categoryId;
    document.getElementById('filter-city').value = window.currentFilters.city;
    document.getElementById('filter-start-date').value = window.currentFilters.startDate;
    document.getElementById('filter-end-date').value = window.currentFilters.endDate;

    await fetchAndRenderEvents();

    // Tambahkan event listeners
    setupEventListenersForEventsPage();
}

async function loadFilterOptions() {
    const categorySelect = document.getElementById('filter-category');
    const citySelect = document.getElementById('filter-city');

    try {
        const [categories, cities] = await Promise.all([
            apiFetch('/categories'),
            apiFetch('/venues/cities') // Endpoint dari VenueController
        ]);
        
        categorySelect.innerHTML = '<option value="">All Categories</option>' + 
            categories.map(cat => `<option value="${cat.idKategori}">${cat.namaKategori}</option>`).join('');
            
        citySelect.innerHTML = '<option value="">All Cities</option>' + 
            cities.map(city => `<option value="${city}">${city}</option>`).join('');
            
    } catch (error) {
        console.error("Failed to load filter options", error);
    }
}

function setupEventListenersForEventsPage() {
    const searchInput = document.getElementById('search-input');
    const filterForm = document.getElementById('filter-form');
    const resetFiltersButton = document.getElementById('reset-filters-button');
    const sortSelect = document.getElementById('sort-select');

    // Debounce search input (jalankan search setelah 500ms tidak mengetik)
    let searchTimeout;
    searchInput.addEventListener('input', () => {
        clearTimeout(searchTimeout);
        searchTimeout = setTimeout(() => {
            window.currentFilters.search = searchInput.value.trim();
            updateUrlAndFetchEvents();
        }, 500);
    });

    // Filter otomatis saat diganti
    filterForm.addEventListener('change', (e) => {
        const formData = new FormData(filterForm);
        window.currentFilters.categoryId = formData.get('categoryId');
        window.currentFilters.city = formData.get('city');
        window.currentFilters.startDate = formData.get('startDate');
        window.currentFilters.endDate = formData.get('endDate');
        // Kosongkan search jika filter lain aktif
        window.currentFilters.search = '';
        searchInput.value = '';
        updateUrlAndFetchEvents();
    });
    
    // Reset filters
    resetFiltersButton.addEventListener('click', () => {
        filterForm.reset();
        searchInput.value = '';
        window.currentFilters = {};
        updateUrlAndFetchEvents();
    });
    
    // Sorting (hanya re-render data yang sudah ada)
    sortSelect.addEventListener('change', () => {
        renderEventGrid(sortEvents(window.allEventsData, sortSelect.value), '#event-grid');
    });
}

function updateUrlAndFetchEvents() {
    // Update URL tanpa reload
    const urlParams = new URLSearchParams();
    Object.entries(window.currentFilters).forEach(([key, value]) => {
        if (value) {
            urlParams.set(key, value);
        }
    });
    const newUrl = `${window.location.pathname}?${urlParams.toString()}`;
    window.history.pushState({ path: newUrl }, '', newUrl);

    // Fetch data baru
    fetchAndRenderEvents();
}

async function fetchAndRenderEvents() {
    const eventGrid = document.getElementById('event-grid');
    const eventCount = document.getElementById('event-count');
    const noEventsMessage = document.getElementById('no-events-message');
    
    // Tampilkan skeleton loading
    eventGrid.innerHTML = Array(6).fill(`
        <div class="bg-white p-4 rounded-xl shadow-md animate-pulse">
             <div class="h-40 bg-gray-200 rounded-lg mb-4"></div>
             <div class="h-6 bg-gray-200 rounded w-3/4 mb-2"></div>
             <div class="h-4 bg-gray-200 rounded w-1/2 mb-4"></div>
             <div class="h-4 bg-gray-200 rounded w-1/3"></div>
        </div>
    `).join('');
    eventCount.textContent = 'Loading events...';
    noEventsMessage.classList.add('hidden');

    try {
        let endpoint = '/events/upcoming'; // Default
        const params = new URLSearchParams();
        
        if (window.currentFilters.search) {
             endpoint = '/events/search';
             params.set('q', window.currentFilters.search);
        } else if (window.currentFilters.categoryId || window.currentFilters.city || window.currentFilters.startDate || window.currentFilters.endDate) {
             endpoint = '/events/filter';
             if (window.currentFilters.categoryId) params.set('categoryId', window.currentFilters.categoryId);
             if (window.currentFilters.city) params.set('city', window.currentFilters.city);
             if (window.currentFilters.startDate) params.set('startDate', window.currentFilters.startDate);
             if (window.currentFilters.endDate) params.set('endDate', window.currentFilters.endDate);
        }
        
        // Ambil data event
        window.allEventsData = await apiFetch(`${endpoint}?${params.toString()}`);
        
        // Update count
        eventCount.textContent = `${window.allEventsData.length} events found`;

        // Render grid
        if (window.allEventsData.length > 0) {
            renderEventGrid(sortEvents(window.allEventsData, document.getElementById('sort-select').value), '#event-grid');
            noEventsMessage.classList.add('hidden');
        } else {
            eventGrid.innerHTML = ''; // Kosongkan grid
            noEventsMessage.classList.remove('hidden');
        }

    } catch (error) {
        eventGrid.innerHTML = '<p class="text-red-500 col-span-full">Gagal memuat event.</p>';
        eventCount.textContent = 'Error loading events';
        noEventsMessage.classList.add('hidden');
    }
}

/**
 * Merender event cards ke dalam grid
 * @param {Array} events - Array of event objects
 * @param {string} gridSelector - CSS selector for the grid container
 */
function renderEventGrid(events, gridSelector) {
    const grid = document.querySelector(gridSelector);
    if (!grid) return;

    grid.innerHTML = events.map((event, index) => {
        // Format tanggal (contoh sederhana)
        const eventDate = event.tanggalMulai 
            ? new Date(event.tanggalMulai).toLocaleDateString('id-ID', { day: 'numeric', month: 'short', year: 'numeric' })
            : 'TBA';
            
        // Harga terendah
        const lowestPrice = event.tickets?.length > 0 
            ? Math.min(...event.tickets.map(t => t.harga)) 
            : 0;
        const formattedPrice = lowestPrice > 0 
            ? new Intl.NumberFormat('id-ID', { style: 'currency', currency: 'IDR', minimumFractionDigits: 0 }).format(lowestPrice)
            : 'Free';

        return `
            <a href="/event-detail.html?id=${event.idEvent}" 
               class="group block bg-white rounded-2xl overflow-hidden shadow-md hover:shadow-xl transition duration-300 transform hover:-translate-y-1 animate-fade-in-up" 
               style="animation-delay: ${index * 50}ms;">
                <div class="relative h-48 overflow-hidden">
                    <img src="${event.bannerUrl || event.posterUrl || '/assets/images/placeholder-event.jpg'}" 
                         alt="${event.namaEvent}" 
                         class="w-full h-full object-cover transition duration-500 group-hover:scale-110"
                         onerror="this.onerror=null;this.src='/assets/images/placeholder-event.jpg';">
                    </div>
                <div class="p-4">
                    <p class="text-xs text-primary-600 font-semibold mb-1">${event.kategori?.namaKategori || 'Event'}</p>
                    <h3 class="font-semibold text-lg text-gray-900 mb-2 line-clamp-2" title="${event.namaEvent}">
                        ${event.namaEvent}
                    </h3>
                    <div class="text-sm text-gray-600 mb-3 space-y-1">
                        <p class="flex items-center gap-1.5">
                            <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4 flex-shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5"><path stroke-linecap="round" stroke-linejoin="round" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" /></svg>
                            ${eventDate}
                        </p>
                        ${event.venue ? `
                        <p class="flex items-center gap-1.5 line-clamp-1">
                            <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4 flex-shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5"><path stroke-linecap="round" stroke-linejoin="round" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" /><path stroke-linecap="round" stroke-linejoin="round" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" /></svg>
                            ${event.venue.namaVenue}, ${event.venue.kota}
                        </p>` : ''}
                    </div>
                    <div class="flex items-center justify-between">
                        <p class="text-xs text-gray-500">Starting from</p>
                        <p class="text-lg font-bold text-primary-600">${formattedPrice}</p>
                    </div>
                </div>
            </a>
        `;
    }).join('');
}


/**
 * Mengurutkan array event
 * @param {Array} events - Array of event objects
 * @param {string} sortBy - Kriteria sort ('upcoming', 'latest', 'popular')
 */
function sortEvents(events, sortBy) {
    const sortedEvents = [...events]; // Buat salinan
    switch (sortBy) {
        case 'latest':
            sortedEvents.sort((a, b) => new Date(b.tanggalMulai) - new Date(a.tanggalMulai));
            break;
        case 'popular':
            // TODO: Backend belum punya data popularitas, sort by date saja dulu
            sortedEvents.sort((a, b) => new Date(a.tanggalMulai) - new Date(b.tanggalMulai));
            break;
        case 'upcoming': // Default
        default:
             sortedEvents.sort((a, b) => new Date(a.tanggalMulai) - new Date(b.tanggalMulai));
             break;
    }
    return sortedEvents;
}


// Placeholder image jika gambar gagal dimuat (tambahkan ke CSS)
// img[onerror] { /* style jika perlu */ }

// Buat folder assets/images dan taruh placeholder
// Contoh path: /assets/images/placeholder-event.jpg


// (Kode yang sudah ada sebelumnya tetap di atas)

// === LOGIKA SPESIFIK HALAMAN (Lanjutan) ===
document.addEventListener('DOMContentLoaded', () => {
    const page = document.body.dataset.page;
    
    if (page === 'home') {
        initializeHomepage();
    } else if (page === 'events') {
        initializeEventsPage();
    } else if (page === 'event-detail') {
        initializeEventDetailPage(); // Tambahkan ini
    }
    // ... (Tambahkan else if untuk halaman lain nanti) ...
});


// --- Event Detail Page Logic ---
async function initializeEventDetailPage() {
    console.log("Initializing Event Detail Page...");
    const eventDetailContainer = document.getElementById('event-detail-container');
    const template = document.getElementById('event-detail-template');
    
    // Check if template exists (old implementation), if not, skip this initialization
    // because event-detail.html now has its own implementation
    if (!template) {
        console.log("Event detail template not found - using inline implementation");
        return;
    }
    
    // 1. Ambil ID event dari URL parameter (?id=...)
    const urlParams = new URLSearchParams(window.location.search);
    const eventId = urlParams.get('id');

    if (!eventId) {
        eventDetailContainer.innerHTML = '<p class="text-red-500 text-center py-16">Event ID not found in URL.</p>';
        return;
    }

    try {
        // 2. Fetch data event dan tiket secara paralel
        const [eventData, ticketsData] = await Promise.all([
            apiFetch(`/events/${eventId}`), // Endpoint detail event (termasuk category, venue, artists, schedules, sponsors)
            apiFetch(`/tickets/event/${eventId}/available`) // Endpoint tiket yang available
        ]);

        // 3. Clone template dan isi datanya
        const clone = template.content.cloneNode(true);
        
        // --- Isi Data Event Utama ---
        clone.getElementById('event-banner').src = eventData.bannerUrl || eventData.posterUrl || '/assets/images/placeholder-event.jpg';
        clone.getElementById('event-banner').alt = eventData.namaEvent;
        clone.getElementById('event-category').textContent = eventData.kategori?.namaKategori || 'Event';
        clone.getElementById('event-name').textContent = eventData.namaEvent;
        clone.getElementById('event-description').textContent = eventData.deskripsi || 'No description available.';
        
        // --- Status Badge ---
        const statusSpan = clone.getElementById('event-status');
        statusSpan.textContent = formatEventStatus(eventData.status); // Fungsi helper (akan kita buat)
        statusSpan.className = getStatusBadgeClass(eventData.status); // Fungsi helper (akan kita buat)

        // --- Info Grid (Date, Venue, Organizer) ---
        const infoGrid = clone.getElementById('event-info-grid');
        infoGrid.innerHTML = `
            ${renderInfoItem('Calendar', 'Date', formatDate(eventData.tanggalMulai))} 
            ${eventData.venue ? renderInfoItem('MapPin', 'Venue', `${eventData.venue.namaVenue}, ${eventData.venue.kota}`) : ''}
            ${eventData.penyelenggara ? renderInfoItem('Users', 'Organizer', eventData.penyelenggara) : ''}
             ${eventData.venue ? renderInfoItem('Users', 'Capacity', `${eventData.venue.kapasitas.toLocaleString()} people`) : ''}
        `; // Fungsi helper (akan kita buat)

        // --- Artists ---
        if (eventData.artists && eventData.artists.length > 0) {
            clone.getElementById('artists-section').classList.remove('hidden');
            const artistsGrid = clone.getElementById('artists-grid');
            artistsGrid.innerHTML = eventData.artists.map(artist => `
                <div class="text-center p-4 border border-gray-200 rounded-lg hover:border-primary-300 hover:bg-primary-50 transition">
                    <div class="w-16 h-16 mx-auto mb-3 bg-gradient-to-br from-primary-500 to-accent/80 rounded-full flex items-center justify-center">
                        <span class="text-2xl font-bold text-white">${artist.namaArtis.charAt(0).toUpperCase()}</span>
                    </div>
                    <p class="font-medium text-gray-900 mb-1 text-sm line-clamp-1">${artist.namaArtis}</p>
                    ${artist.genre ? `<p class="text-xs text-gray-500">${artist.genre}</p>` : ''}
                </div>
            `).join('');
        }

        // --- Schedules ---
        if (eventData.schedules && eventData.schedules.length > 0) {
            clone.getElementById('schedules-section').classList.remove('hidden');
            const schedulesList = clone.getElementById('schedules-list');
            schedulesList.innerHTML = eventData.schedules.map(schedule => `
                <div class="flex items-start space-x-4 p-4 border border-gray-200 rounded-lg">
                    <div class="flex-shrink-0 text-center">
                        <div class="w-12 h-12 bg-primary-100 rounded-lg flex flex-col items-center justify-center">
                            <span class="text-xs text-primary-600 font-medium">${new Date(schedule.tanggal).toLocaleDateString('id-ID', { month: 'short' })}</span>
                            <span class="text-lg font-bold text-primary-600">${new Date(schedule.tanggal).toLocaleDateString('id-ID', { day: '2-digit' })}</span>
                        </div>
                    </div>
                    <div class="flex-1">
                        <p class="font-medium text-gray-900 mb-1">${schedule.jamMulai} - ${schedule.jamSelesai}</p>
                        ${schedule.keterangan ? `<p class="text-sm text-gray-600">${schedule.keterangan}</p>` : ''}
                    </div>
                </div>
            `).join('');
        }

        // --- Sponsors ---
         if (eventData.sponsors && eventData.sponsors.length > 0) {
            clone.getElementById('sponsors-section').classList.remove('hidden');
            const sponsorsGrid = clone.getElementById('sponsors-grid');
            sponsorsGrid.innerHTML = eventData.sponsors.map(sponsor => `
                <div class="text-center p-4 border border-gray-200 rounded-lg hover:border-primary-300 transition">
                    ${sponsor.logoUrl ? `
                        <img src="${sponsor.logoUrl}" alt="${sponsor.namaSponsor}" class="w-full h-16 object-contain mb-2" onerror="this.style.display='none'; this.nextSibling.style.display='flex';">
                        <div class="w-full h-16 bg-gray-100 rounded-lg items-center justify-center mb-2 hidden text-gray-400">Icon</div>
                    ` : `
                        <div class="w-full h-16 bg-gray-100 rounded-lg flex items-center justify-center mb-2 text-gray-400">
                           <svg xmlns="http://www.w3.org/2000/svg" class="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5"><path stroke-linecap="round" stroke-linejoin="round" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" /></svg>
                        </div>
                    `}
                    <p class="text-xs font-medium text-gray-700 line-clamp-1">${sponsor.namaSponsor}</p>
                    ${sponsor.jenisSponsor ? `<p class="text-xs text-gray-500">${sponsor.jenisSponsor}</p>` : ''}
                </div>
            `).join('');
        }

        // --- Sidebar (Harga & Tiket Preview) ---
        const lowestPrice = ticketsData.length > 0 ? Math.min(...ticketsData.map(t => t.harga)) : 0;
        clone.getElementById('event-price').textContent = formatCurrency(lowestPrice); // Fungsi helper (akan kita buat)

        const ticketsPreview = clone.getElementById('tickets-preview');
        const bookButton = clone.getElementById('book-button');

        if (ticketsData.length > 0 && eventData.status === 'UPCOMING') {
             ticketsPreview.innerHTML = `
                 <h3 class="text-sm font-semibold text-gray-900 flex items-center space-x-2">
                     <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M15 5v2m0 4v2m0 4v2M5 5a2 2 0 00-2 2v3a2 2 0 110 4v3a2 2 0 002 2h14a2 2 0 002-2v-3a2 2 0 110-4V7a2 2 0 00-2-2H5z" /></svg>
                     <span>Available Tickets</span>
                 </h3>
                 ${ticketsData.slice(0, 3).map(ticket => `
                     <div class="flex items-center justify-between p-3 border border-gray-200 rounded-lg">
                         <div>
                             <p class="text-sm font-medium text-gray-900">${ticket.jenisTiket}</p>
                             <p class="text-xs text-gray-500">${ticket.stok} available</p>
                         </div>
                         <p class="text-sm font-semibold text-primary-600">${formatCurrency(ticket.harga)}</p>
                     </div>
                 `).join('')}
                 ${ticketsData.length > 3 ? `<p class="text-xs text-gray-500 text-center">+${ticketsData.length - 3} more ticket types</p>` : ''}
             `;
             bookButton.disabled = false;
             bookButton.textContent = 'Book Tickets';
             // Tambahkan event listener ke tombol Book
             bookButton.addEventListener('click', () => {
                 if (window.auth.isAuthenticated()) {
                     window.location.href = `/booking.html?eventId=${eventId}`; // Arahkan ke halaman booking
                 } else {
                     alert('Please login to book tickets.');
                     window.location.href = '/login.html'; // Arahkan ke login
                 }
             });
        } else {
             // Handle jika tiket habis atau event tidak upcoming
             let message = 'No tickets available';
             if (eventData.status === 'CANCELLED') message = 'Event Cancelled';
             else if (eventData.status === 'COMPLETED') message = 'Event Completed';
             else if (eventData.status === 'ONGOING') message = 'Event Ongoing';
             
             ticketsPreview.innerHTML = `
                 <div class="p-4 bg-red-50 border border-red-200 rounded-lg">
                     <p class="text-sm text-red-900 text-center font-medium">${message}</p>
                 </div>`;
             bookButton.disabled = true;
             bookButton.textContent = message;
        }

        // --- Wishlist & Share Button Logic ---
        setupWishlistButton(clone.getElementById('wishlist-button'), eventId);
        setupShareButton(clone.getElementById('share-button'), eventData);

        // 4. Masukkan konten yang sudah diisi ke halaman
        eventDetailContainer.innerHTML = ''; // Hapus loader
        eventDetailContainer.appendChild(clone);

    } catch (error) {
        eventDetailContainer.innerHTML = `<p class="text-red-500 text-center py-16">Failed to load event details. Error: ${error.message}</p>`;
        console.error("Failed to load event detail:", error);
    }
}

// --- Helper Functions for Event Detail ---

/** Format tanggal (dd MMMM yyyy) */
function formatDate(dateString) {
    if (!dateString) return 'Date TBA';
    try {
        return new Date(dateString).toLocaleDateString('id-ID', { day: 'numeric', month: 'long', year: 'numeric' });
    } catch (e) { return dateString; }
}

/** Format mata uang (Rp X.XXX) */
function formatCurrency(amount) {
     if (amount === null || amount === undefined) return 'Price TBA';
     try {
        return new Intl.NumberFormat('id-ID', { style: 'currency', currency: 'IDR', minimumFractionDigits: 0 }).format(amount);
     } catch(e) { return amount; }
}

/** Format status event */
function formatEventStatus(status) {
    const map = { UPCOMING: 'Upcoming', ONGOING: 'Ongoing', COMPLETED: 'Completed', CANCELLED: 'Cancelled' };
    return map[status] || status;
}

/** Dapatkan class CSS untuk badge status */
function getStatusBadgeClass(status) {
    let baseClass = 'inline-block px-3 py-1 rounded-full text-xs font-medium';
    const map = {
        UPCOMING: 'bg-blue-100 text-blue-800',
        ONGOING: 'bg-green-100 text-green-800',
        COMPLETED: 'bg-gray-100 text-gray-800',
        CANCELLED: 'bg-red-100 text-red-800',
    };
    return `${baseClass} ${map[status] || map.COMPLETED}`;
}

/** Render item info (ikon + label + value) */
function renderInfoItem(iconName, label, value) {
    const icons = { // SVG sederhana, bisa diganti dengan ikon yang lebih baik jika perlu
        Calendar: `<svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5 text-primary-600 flex-shrink-0 mt-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" /></svg>`,
        MapPin: `<svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5 text-primary-600 flex-shrink-0 mt-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" /><path stroke-linecap="round" stroke-linejoin="round" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" /></svg>`,
        Users: `<svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5 text-primary-600 flex-shrink-0 mt-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.653-.08-1.282-.23-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.653.08-1.282.23-1.857m10.519-2.87A7.5 7.5 0 0012 11c-2.02 0-3.83.74-5.23 1.94" /></svg>`,
    };
    return `
        <div class="flex items-start space-x-3">
            ${icons[iconName] || ''}
            <div>
                <p class="text-sm text-gray-500 mb-1">${label}</p>
                <p class="font-medium text-gray-900">${value}</p>
            </div>
        </div>`;
}

// --- Wishlist & Share Logic ---
async function setupWishlistButton(button, eventId) {
    if (!button) return;
    
    const heartIconSVG = `<svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" /></svg>`;
    let isWishlisted = false; // Status awal
    const userId = window.auth.getUserId();

    // Cek status awal jika user login
    if (userId) {
        try {
            // Call API to check wishlist status
            const response = await fetch(`${window.API_BASE_URL}/wishlist/check?userId=${userId}&eventId=${eventId}`);
            if (response.ok) {
                const result = await response.json();
                console.log('Wishlist check response:', result);
                // apiFetch already extracts data.data, so result.success tells if it worked
                if (result.success) {
                    isWishlisted = result.data;
                }
            }
        } catch (e) { 
            console.error("Failed to check wishlist status", e); 
        }
    }
    
    // Update tampilan tombol awal
    updateWishlistButtonUI(button, isWishlisted, heartIconSVG);

    // Tambah event listener
    button.addEventListener('click', async () => {
        if (!window.auth.isAuthenticated()) {
            alert('Please login to add to wishlist.');
            window.location.href = '/login.html';
            return;
        }
        
        button.disabled = true; // Cegah double click
        
        try {
            if (isWishlisted) {
                // Hapus dari wishlist
                const response = await fetch(`${window.API_BASE_URL}/wishlist?userId=${userId}&eventId=${eventId}`, { method: 'DELETE' });
                const result = await response.json();
                if (!result.success) {
                    throw new Error(result.message || 'Failed to remove from wishlist');
                }
                showToast('Removed from wishlist', 'success');
                isWishlisted = false;
            } else {
                // Tambah ke wishlist
                const response = await fetch(`${window.API_BASE_URL}/wishlist?userId=${userId}&eventId=${eventId}`, { method: 'POST' });
                const result = await response.json();
                if (!result.success) {
                    throw new Error(result.message || 'Failed to add to wishlist');
                }
                showToast('Added to wishlist', 'success');
                isWishlisted = true;
            }
            // Update UI tombol
            updateWishlistButtonUI(button, isWishlisted, heartIconSVG);
            
        } catch (error) {
            console.error('Failed to update wishlist', error);
            showToast('Failed to update wishlist: ' + error.message, 'error');
        } finally {
            button.disabled = false;
        }
    });
}

function updateWishlistButtonUI(button, isWishlisted, svgIcon) {
     button.innerHTML = svgIcon; // Set ikon
     const icon = button.querySelector('svg');
     if (isWishlisted) {
         icon.classList.add('fill-red-500', 'text-red-500');
         icon.classList.remove('text-gray-900');
     } else {
         icon.classList.remove('fill-red-500', 'text-red-500');
         icon.classList.add('text-gray-900');
     }
}

function setupShareButton(button, eventData) {
     if (!button) return;
     
     // Ganti ikon share yang lebih baik
     button.innerHTML = `<svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5 text-gray-900" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M8.684 13.342C8.886 13.545 9 13.848 9 14.152v1.748c0 .26.105.512.293.7l.16.16c.39.39 1.024.39 1.414 0l1.96-1.96c.39-.39.39-1.024 0-1.414l-1.96-1.96a.996.996 0 00-1.414 0l-.16.16a.996.996 0 00-.293.7v1.748c0 .304-.114.607-.316.809l-4.243 4.243a1 1 0 01-1.414-1.414l4.243-4.243zM15.316 10.658c-.202-.202-.316-.505-.316-.809V8.1c0-.26-.105-.512-.293-.7l-.16-.16c-.39-.39-1.024-.39-1.414 0l-1.96 1.96c-.39.39-.39 1.024 0 1.414l1.96 1.96c.39.39 1.024.39 1.414 0l.16-.16c.188-.188.293-.44.293-.7V10.9c0-.304.114-.607.316-.809l4.243-4.243a1 1 0 011.414 1.414l-4.243 4.243z" /></svg>`;
     
     button.addEventListener('click', async () => {
         const shareData = {
             title: eventData.namaEvent,
             text: `Check out this event: ${eventData.namaEvent} on ${formatDate(eventData.tanggalMulai)}!`,
             url: window.location.href
         };
         try {
             if (navigator.share) {
                 await navigator.share(shareData);
                 console.log('Event shared successfully');
             } else {
                 // Fallback for browsers that don't support Web Share API
                 await navigator.clipboard.writeText(shareData.url);
                 alert('Link copied to clipboard!');
             }
         } catch (err) {
             console.error('Share failed:', err);
             // alert('Could not share the event.'); // Opsional
         }
     });
}

// Toast notification for wishlist
function showToast(message, type = 'info') {
    // Create or get toast container
    let toastContainer = document.getElementById('app-toast-container');
    if (!toastContainer) {
        toastContainer = document.createElement('div');
        toastContainer.id = 'app-toast-container';
        toastContainer.style.cssText = 'position: fixed; bottom: 20px; right: 20px; z-index: 9999;';
        document.body.appendChild(toastContainer);
    }
    
    // Create toast element
    const toast = document.createElement('div');
    const bgColor = type === 'success' ? 'bg-green-500' : type === 'error' ? 'bg-red-500' : 'bg-blue-500';
    toast.className = `${bgColor} text-white px-6 py-3 rounded-lg shadow-lg mb-3 animate-slide-in-right`;
    toast.textContent = message;
    
    toastContainer.appendChild(toast);
    
    // Auto remove after 3 seconds
    setTimeout(() => {
        toast.remove();
    }, 3000);
}

/**
 * Setup Admin Menu Dropdown interactions
 */
function setupAdminMenuDropdown() {
    // Desktop Admin Dropdown
    const adminMenuTrigger = document.getElementById('admin-menu-trigger');
    const adminDropdownMenu = document.getElementById('admin-dropdown-menu');
    
    if (adminMenuTrigger && adminDropdownMenu) {
        adminMenuTrigger.addEventListener('click', (e) => {
            e.stopPropagation();
            adminDropdownMenu.classList.toggle('hidden');
        });
        
        // Close dropdown when clicking outside
        document.addEventListener('click', () => {
            adminDropdownMenu.classList.add('hidden');
        });
        
        // Prevent closing when clicking inside dropdown
        adminDropdownMenu.addEventListener('click', (e) => {
            e.stopPropagation();
        });
    }
    
    // Mobile Admin Dropdown
    const adminMenuTriggerMobile = document.getElementById('admin-menu-trigger-mobile');
    const adminSubmenuMobile = document.getElementById('admin-submenu-mobile');
    const adminMenuArrowMobile = document.getElementById('admin-menu-arrow-mobile');
    
    if (adminMenuTriggerMobile && adminSubmenuMobile) {
        adminMenuTriggerMobile.addEventListener('click', (e) => {
            e.stopPropagation();
            adminSubmenuMobile.classList.toggle('hidden');
            if (adminMenuArrowMobile) {
                adminMenuArrowMobile.style.transform = adminSubmenuMobile.classList.contains('hidden') ? 'rotate(0deg)' : 'rotate(180deg)';
            }
        });
    }
}