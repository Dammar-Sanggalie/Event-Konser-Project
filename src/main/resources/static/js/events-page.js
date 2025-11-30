/**
 * EVENTS PAGE - SEARCH & FILTER LOGIC
 * Menangani semua fitur search, filter, dan display events
 */

let currentEvents = [];
let currentFilters = {
    categoryId: null,
    city: null,
    startDate: null,
    endDate: null,
    searchQuery: ''
};

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
            currentEvents = result.data;
            displayEvents(currentEvents);
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
        currentFilters.searchQuery = searchInput?.value || '';
        
        // Get filter values
        currentFilters.categoryId = document.getElementById('filter-category')?.value || null;
        currentFilters.city = document.getElementById('filter-city')?.value || null;
        currentFilters.startDate = document.getElementById('filter-start-date')?.value || null;
        currentFilters.endDate = document.getElementById('filter-end-date')?.value || null;
        
        let filteredEvents = [...currentEvents];
        
        // 1. Search filter
        if (currentFilters.searchQuery) {
            const query = currentFilters.searchQuery.toLowerCase();
            filteredEvents = filteredEvents.filter(event => 
                event.namaEvent?.toLowerCase().includes(query) ||
                event.deskripsi?.toLowerCase().includes(query) ||
                event.penyelenggara?.toLowerCase().includes(query)
            );
        }
        
        // 2. Category filter
        if (currentFilters.categoryId) {
            filteredEvents = filteredEvents.filter(event => 
                event.kategori?.idKategori == currentFilters.categoryId
            );
        }
        
        // 3. City filter
        if (currentFilters.city) {
            filteredEvents = filteredEvents.filter(event => 
                event.venue?.kota === currentFilters.city
            );
        }
        
        // 4. Date range filter
        if (currentFilters.startDate) {
            const startDate = new Date(currentFilters.startDate);
            filteredEvents = filteredEvents.filter(event => 
                new Date(event.tanggalMulai) >= startDate
            );
        }
        
        if (currentFilters.endDate) {
            const endDate = new Date(currentFilters.endDate);
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
    currentFilters = {
        categoryId: null,
        city: null,
        startDate: null,
        endDate: null,
        searchQuery: ''
    };
    
    // Display all events
    displayEvents(currentEvents);
}

/**
 * Apply sorting
 */
function applySorting(sortBy) {
    let sorted = [...currentEvents];
    
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
    if (currentFilters.searchQuery) {
        const query = currentFilters.searchQuery.toLowerCase();
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
        <div class="bg-white rounded-xl shadow-md overflow-hidden hover:shadow-xl transition-shadow cursor-pointer"
             onclick="window.location.href='/event-detail.html?id=${event.idEvent}'">
            
            <!-- Event Image -->
            <div class="relative h-40 bg-gradient-to-br from-primary-400 to-primary-600 overflow-hidden">
                ${event.posterUrl ? `
                    <img src="${event.posterUrl}" alt="${event.namaEvent}" class="w-full h-full object-cover">
                ` : `
                    <div class="w-full h-full flex items-center justify-center bg-primary-500">
                        <svg xmlns="http://www.w3.org/2000/svg" class="w-12 h-12 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14.828 14.828a4 4 0 01-5.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                        </svg>
                    </div>
                `}
                
                <!-- Category Badge -->
                <div class="absolute top-3 right-3 bg-accent text-white px-3 py-1 rounded-full text-xs font-semibold">
                    ${event.kategori?.namaKategori || 'Event'}
                </div>
                
                <!-- Status Badge -->
                <div class="absolute top-3 left-3 px-3 py-1 rounded-full text-xs font-semibold text-white ${
                    event.status === 'UPCOMING' ? 'bg-green-500' :
                    event.status === 'ONGOING' ? 'bg-blue-500' :
                    event.status === 'COMPLETED' ? 'bg-gray-500' :
                    'bg-red-500'
                }">
                    ${event.status}
                </div>
            </div>
            
            <!-- Event Info -->
            <div class="p-4">
                <!-- Title -->
                <h3 class="font-semibold text-gray-900 mb-2 line-clamp-2">
                    ${event.namaEvent}
                </h3>
                
                <!-- Date & Venue -->
                <div class="space-y-1 mb-3 text-sm text-gray-600">
                    <div class="flex items-start gap-2">
                        <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4 mt-0.5 flex-shrink-0 text-primary-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                        </svg>
                        <span>${formatDate(event.tanggalMulai)}</span>
                    </div>
                    <div class="flex items-start gap-2">
                        <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4 mt-0.5 flex-shrink-0 text-primary-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
                        </svg>
                        <span>${event.venue?.kota || 'TBA'}</span>
                    </div>
                </div>
                
                <!-- Organizer -->
                <p class="text-xs text-gray-500 mb-3">
                    By ${event.penyelenggara || 'Unknown'}
                </p>
                
                <!-- CTA Button -->
                <button class="w-full bg-primary-600 hover:bg-primary-700 text-white font-semibold py-2 rounded-lg transition"
                        onclick="event.stopPropagation(); window.location.href='/event-detail.html?id=${event.idEvent}'">
                    View Details
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
