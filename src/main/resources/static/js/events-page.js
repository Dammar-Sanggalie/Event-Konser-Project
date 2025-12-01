/**
 * EVENTS PAGE - SEARCH & FILTER LOGIC
 * Menangani semua fitur search, filter, dan display events
 */

if (typeof currentEvents === 'undefined') {
    window.currentEvents = [];
}

if (typeof currentFilters === 'undefined') {
    window.currentFilters = {
        categoryId: null,
        city: null,
        startDate: null,
        endDate: null,
        searchQuery: ''
    };
}

document.addEventListener('DOMContentLoaded', async () => {
    await initializeEventsPage();
});

/**
 * Initialize events page
 */
async function initializeEventsPage() {
    try {
        // Load categories
        await loadCategories();
        
        // Load cities
        await loadCities();
        
        // Load all events
        await loadAllEvents();
        
        // Setup event listeners
        setupEventListeners();
        
    } catch (error) {
        console.error('Error initializing events page:', error);
        showToast('Error loading events', 'error');
    }
}

/**
 * Load all categories for filter dropdown
 */
async function loadCategories() {
    try {
        const response = await fetch(`${window.API_BASE_URL}/categories`);
        const result = await response.json();
        
        if (result.success && result.data) {
            const select = document.getElementById('filter-category');
            result.data.forEach(category => {
                const option = document.createElement('option');
                option.value = category.idKategori;
                option.textContent = category.namaKategori;
                select.appendChild(option);
            });
        }
    } catch (error) {
        console.error('Error loading categories:', error);
    }
}

/**
 * Load all cities from venues
 */
async function loadCities() {
    try {
        const response = await fetch(`${window.API_BASE_URL}/venues`);
        const result = await response.json();
        
        if (result.success && result.data) {
            // Extract unique cities
            const cities = [...new Set(result.data.map(v => v.kota))];
            const select = document.getElementById('filter-city');
            
            cities.forEach(city => {
                const option = document.createElement('option');
                option.value = city;
                option.textContent = city;
                select.appendChild(option);
            });
        }
    } catch (error) {
        console.error('Error loading cities:', error);
    }
}

/**
 * Load all events - main function
 */
async function loadAllEvents() {
    try {
        const response = await fetch(`${window.API_BASE_URL}/events`);
        const result = await response.json();
        
        if (result.success && result.data) {
            window.currentEvents = result.data;
            displayEvents(window.currentEvents);
        }
    } catch (error) {
        console.error('Error loading events:', error);
        showToast('Failed to load events', 'error');
    }
}

/**
 * Setup all event listeners
 */
function setupEventListeners() {
    const searchInput = document.getElementById('search-input');
    const filterForm = document.getElementById('filter-form');
    const resetButton = document.getElementById('reset-filters-button');
    const sortSelect = document.getElementById('sort-select');
    
    // Search input - with debounce
    if (searchInput) {
        searchInput.addEventListener('input', debounce(() => {
            applyFiltersAndSearch();
        }, 300));
    }
    
    // Filter form inputs
    if (filterForm) {
        filterForm.addEventListener('change', applyFiltersAndSearch);
    }
    
    // Reset filters button
    if (resetButton) {
        resetButton.addEventListener('click', resetFilters);
    }
    
    // Sort select
    if (sortSelect) {
        sortSelect.addEventListener('change', (e) => {
            applySorting(e.target.value);
        });
    }
}

/**
 * Apply filters and search
 */
async function applyFiltersAndSearch() {
    try {
        // Get search query
        const searchInput = document.getElementById('search-input');
        window.currentFilters.searchQuery = searchInput?.value || '';
        
        // Get filter values
        window.currentFilters.categoryId = document.getElementById('filter-category')?.value || null;
        window.currentFilters.city = document.getElementById('filter-city')?.value || null;
        window.currentFilters.startDate = document.getElementById('filter-start-date')?.value || null;
        window.currentFilters.endDate = document.getElementById('filter-end-date')?.value || null;
        
        let filteredEvents = [...window.currentEvents];
        
        // 1. Search filter
        if (window.currentFilters.searchQuery) {
            const query = window.currentFilters.searchQuery.toLowerCase();
            filteredEvents = filteredEvents.filter(event => 
                event.namaEvent?.toLowerCase().includes(query) ||
                event.deskripsi?.toLowerCase().includes(query) ||
                event.penyelenggara?.toLowerCase().includes(query)
            );
        }
        
        // 2. Category filter
        if (window.currentFilters.categoryId) {
            filteredEvents = filteredEvents.filter(event => 
                event.kategori?.idKategori == window.currentFilters.categoryId
            );
        }
        
        // 3. City filter
        if (window.currentFilters.city) {
            filteredEvents = filteredEvents.filter(event => 
                event.venue?.kota === window.currentFilters.city
            );
        }
        
        // 4. Date range filter
        if (window.currentFilters.startDate) {
            const startDate = new Date(window.currentFilters.startDate);
            filteredEvents = filteredEvents.filter(event => 
                new Date(event.tanggalMulai) >= startDate
            );
        }
        
        if (window.currentFilters.endDate) {
            const endDate = new Date(window.currentFilters.endDate);
            endDate.setHours(23, 59, 59, 999);
            filteredEvents = filteredEvents.filter(event => 
                new Date(event.tanggalMulai) <= endDate
            );
        }
        
        displayEvents(filteredEvents);
        
    } catch (error) {
        console.error('Error applying filters:', error);
        showToast('Error filtering events', 'error');
    }
}

/**
 * Reset all filters
 */
function resetFilters() {
    // Clear form inputs
    document.getElementById('filter-category').value = '';
    document.getElementById('filter-city').value = '';
    document.getElementById('filter-start-date').value = '';
    document.getElementById('filter-end-date').value = '';
    document.getElementById('search-input').value = '';
    document.getElementById('sort-select').value = 'upcoming';
    
    // Reset filter object
    window.currentFilters = {
        categoryId: null,
        city: null,
        startDate: null,
        endDate: null,
        searchQuery: ''
    };
    
    // Display all events
    displayEvents(window.currentEvents);
}

/**
 * Apply sorting
 */
function applySorting(sortBy) {
    let sorted = [...window.currentEvents];
    
    switch (sortBy) {
        case 'upcoming':
            sorted.sort((a, b) => new Date(a.tanggalMulai) - new Date(b.tanggalMulai));
            break;
        case 'latest':
            sorted.sort((a, b) => new Date(b.tanggalMulai) - new Date(a.tanggalMulai));
            break;
        case 'popular':
            // Sort by number of tickets sold (if available)
            sorted.sort((a, b) => (b.popularityScore || 0) - (a.popularityScore || 0));
            break;
    }
    
    // Reapply search/filters then display
    let filtered = sorted;
    if (window.currentFilters.searchQuery) {
        const query = window.currentFilters.searchQuery.toLowerCase();
        filtered = filtered.filter(event => 
            event.namaEvent?.toLowerCase().includes(query)
        );
    }
    
    displayEvents(filtered);
}

/**
 * Display events in grid
 */
function displayEvents(events) {
    const eventGrid = document.getElementById('event-grid');
    const noEventsMsg = document.getElementById('no-events-message');
    const eventCount = document.getElementById('event-count');
    
    if (!eventGrid) return;
    
    // Update count
    if (eventCount) {
        eventCount.textContent = `${events.length} event${events.length !== 1 ? 's' : ''} found`;
    }
    
    // Show/hide no events message
    if (events.length === 0) {
        eventGrid.innerHTML = '';
        if (noEventsMsg) noEventsMsg.classList.remove('hidden');
        return;
    }
    
    if (noEventsMsg) noEventsMsg.classList.add('hidden');
    
    // Build event cards
    eventGrid.innerHTML = events.map(event => `
        <div class="group bg-white rounded-2xl shadow-md hover:shadow-2xl overflow-hidden transition-all duration-300 transform hover:-translate-y-2 cursor-pointer border border-gray-100 hover:border-primary-200"
             onclick="window.location.href='/event-detail.html?id=${event.idEvent}'">
            
            <!-- Event Image Container -->
            <div class="relative h-48 bg-gradient-to-br from-primary-400 to-primary-600 overflow-hidden">
                ${event.posterUrl ? `
                    <img src="${event.posterUrl}" alt="${event.namaEvent}" class="w-full h-full object-cover group-hover:scale-110 transition-transform duration-300">
                ` : `
                    <div class="w-full h-full flex items-center justify-center bg-gradient-to-br from-primary-500 to-primary-700">
                        <svg xmlns="http://www.w3.org/2000/svg" class="w-16 h-16 text-white opacity-70" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M14.828 14.828a4 4 0 01-5.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                        </svg>
                    </div>
                `}
                
                <!-- Dark overlay on hover -->
                <div class="absolute inset-0 bg-black opacity-0 group-hover:opacity-10 transition-opacity duration-300"></div>
                
                <!-- Category Badge -->
                <div class="absolute top-4 right-4 bg-white bg-opacity-90 text-gray-700 px-4 py-1.5 rounded-full text-xs font-semibold border border-gray-200">
                    ${event.kategori?.namaKategori || 'Event'}
                </div>
                
                <!-- Status Badge -->
                <div class="absolute top-4 left-4 px-4 py-1.5 rounded-full text-xs font-semibold text-gray-700 bg-white bg-opacity-90 border border-gray-200">
                    ${event.status}
                </div>
            </div>
            
            <!-- Event Info -->
            <div class="p-5">
                <!-- Title -->
                <h3 class="font-bold text-lg text-gray-900 mb-3 line-clamp-2 group-hover:text-primary-600 transition">
                    ${event.namaEvent}
                </h3>
                
                <!-- Date & Venue Info -->
                <div class="space-y-2.5 mb-4 pb-4 border-b border-gray-100">
                    <!-- Date -->
                    <div class="flex items-center gap-3">
                        <div class="flex-shrink-0 w-5 h-5 flex items-center justify-center rounded-full bg-primary-100">
                            <svg xmlns="http://www.w3.org/2000/svg" class="w-3 h-3 text-primary-600" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
                                <path stroke-linecap="round" stroke-linejoin="round" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                            </svg>
                        </div>
                        <span class="text-sm text-gray-700 font-medium">${formatDate(event.tanggalMulai)}</span>
                    </div>
                    
                    <!-- Venue -->
                    <div class="flex items-center gap-3">
                        <div class="flex-shrink-0 w-5 h-5 flex items-center justify-center rounded-full bg-primary-100">
                            <svg xmlns="http://www.w3.org/2000/svg" class="w-3 h-3 text-primary-600" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
                                <path stroke-linecap="round" stroke-linejoin="round" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
                            </svg>
                        </div>
                        <span class="text-sm text-gray-700 font-medium">${event.venue?.kota || 'TBA'}</span>
                    </div>
                </div>
                
                <!-- Organizer -->
                <p class="text-xs text-gray-500 mb-4 font-medium">
                    By <span class="text-primary-600 font-semibold">${event.penyelenggara || 'Unknown Organizer'}</span>
                </p>
                
                <!-- CTA Button -->
                <button class="w-full bg-gradient-to-r from-primary-600 to-primary-700 hover:from-primary-700 hover:to-primary-800 text-white font-bold py-2.5 rounded-lg transition-all duration-300 transform active:scale-95 flex items-center justify-center gap-2"
                        onclick="event.stopPropagation(); window.location.href='/event-detail.html?id=${event.idEvent}'">
                    <span>Explore Event</span>
                    <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                        <path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7" />
                    </svg>
                </button>
            </div>
        </div>
    `).join('');
}

/**
 * Utility: Format date to Indonesian
 */
function formatDate(dateString) {
    const options = { 
        weekday: 'short', 
        year: 'numeric', 
        month: 'short', 
        day: 'numeric',
        locale: 'id-ID'
    };
    return new Date(dateString).toLocaleDateString('id-ID', options);
}

/**
 * Utility: Debounce function
 */
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

/**
 * Utility: Show toast
 */
function showToast(message, type = 'info') {
    console.log(`[${type.toUpperCase()}] ${message}`);
}
