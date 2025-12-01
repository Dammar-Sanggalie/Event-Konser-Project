/* ===== EVENT-SPECIFIC LOGIC ===== */

class EventManager {
    constructor() {
        this.events = [];
        this.selectedEvent = null;
        this.filters = {
            category: [],
            priceRange: [],
            date: [],
            rating: []
        };
    }

    /**
     * Fetch all events from API
     */
    async fetchEvents(options = {}) {
        try {
            const params = new URLSearchParams();
            if (options.category) params.append('category', options.category);
            if (options.search) params.append('search', options.search);
            if (options.page) params.append('page', options.page);
            if (options.limit) params.append('limit', options.limit);

            const url = `${window.API_BASE_URL}/event${params.toString() ? '?' + params : ''}`;
            const response = await fetch(url);
            const result = await response.json();

            if (result.success) {
                this.events = result.data || [];
                return this.events;
            }
            throw new Error(result.message || 'Failed to fetch events');
        } catch (error) {
            console.error('Error fetching events:', error);
            throw error;
        }
    }

    /**
     * Fetch single event by ID
     */
    async fetchEventById(eventId) {
        try {
            const response = await fetch(`${window.API_BASE_URL}/event/${eventId}`);
            const result = await response.json();

            if (result.success) {
                this.selectedEvent = result.data;
                return this.selectedEvent;
            }
            throw new Error(result.message || 'Failed to fetch event');
        } catch (error) {
            console.error('Error fetching event:', error);
            throw error;
        }
    }

    /**
     * Search events
     */
    async searchEvents(query, options = {}) {
        try {
            const params = new URLSearchParams({
                search: query,
                ...options
            });

            const response = await fetch(`${window.API_BASE_URL}/event?${params}`);
            const result = await response.json();

            if (result.success) {
                return result.data || [];
            }
            throw new Error(result.message || 'Search failed');
        } catch (error) {
            console.error('Error searching events:', error);
            return [];
        }
    }

    /**
     * Filter events
     */
    filterEvents(criteria) {
        return this.events.filter(event => {
            // Category filter
            if (criteria.category?.length && !criteria.category.includes(event.kategori?.namaKategori)) {
                return false;
            }

            // Price range filter
            if (criteria.priceRange?.length) {
                const price = event.hargaTiket || 0;
                const matchesPrice = criteria.priceRange.some(range => {
                    if (range === '0-100000') return price <= 100000;
                    if (range === '100000-500000') return price >= 100000 && price <= 500000;
                    if (range === '500000+') return price >= 500000;
                    return false;
                });
                if (!matchesPrice) return false;
            }

            // Date filter
            if (criteria.date?.length) {
                const eventDate = new Date(event.tanggalEvent);
                const today = new Date();
                const matchesDate = criteria.date.some(range => {
                    if (range === 'today') return eventDate.toDateString() === today.toDateString();
                    if (range === 'this-week') {
                        const weekFrom = new Date(today);
                        weekFrom.setDate(weekFrom.getDate() + 7);
                        return eventDate >= today && eventDate <= weekFrom;
                    }
                    if (range === 'this-month') {
                        return eventDate.getMonth() === today.getMonth() && eventDate.getFullYear() === today.getFullYear();
                    }
                    return false;
                });
                if (!matchesDate) return false;
            }

            // Rating filter
            if (criteria.rating?.length) {
                const rating = event.rating || 0;
                const matchesRating = criteria.rating.some(r => {
                    if (r === '4+') return rating >= 4;
                    if (r === '3+') return rating >= 3;
                    if (r === '2+') return rating >= 2;
                    return false;
                });
                if (!matchesRating) return false;
            }

            return true;
        });
    }

    /**
     * Sort events
     */
    sortEvents(sortBy = 'date') {
        const events = [...this.events];

        switch (sortBy) {
            case 'date':
                return events.sort((a, b) => new Date(a.tanggalEvent) - new Date(b.tanggalEvent));
            case 'price-low':
                return events.sort((a, b) => (a.hargaTiket || 0) - (b.hargaTiket || 0));
            case 'price-high':
                return events.sort((a, b) => (b.hargaTiket || 0) - (a.hargaTiket || 0));
            case 'rating':
                return events.sort((a, b) => (b.rating || 0) - (a.rating || 0));
            case 'popularity':
                return events.sort((a, b) => (b.views || 0) - (a.views || 0));
            default:
                return events;
        }
    }

    /**
     * Get featured events
     */
    getFeaturedEvents(limit = 5) {
        return this.events
            .filter(e => e.featured === true)
            .slice(0, limit);
    }

    /**
     * Get upcoming events
     */
    getUpcomingEvents(limit = 10) {
        const today = new Date();
        return this.events
            .filter(e => new Date(e.tanggalEvent) >= today)
            .sort((a, b) => new Date(a.tanggalEvent) - new Date(b.tanggalEvent))
            .slice(0, limit);
    }

    /**
     * Get popular events
     */
    getPopularEvents(limit = 5) {
        return this.events
            .sort((a, b) => (b.views || 0) - (a.views || 0))
            .slice(0, limit);
    }

    /**
     * Check if event is available
     */
    isEventAvailable(eventId) {
        const event = this.events.find(e => e.idEvent === eventId);
        if (!event) return false;

        const now = new Date();
        const eventDate = new Date(event.tanggalEvent);
        return eventDate > now && event.status === 'ACTIVE';
    }

    /**
     * Get event statistics
     */
    getEventStats() {
        const total = this.events.length;
        const active = this.events.filter(e => e.status === 'ACTIVE').length;
        const featured = this.events.filter(e => e.featured === true).length;
        const avgRating = (this.events.reduce((sum, e) => sum + (e.rating || 0), 0) / total) || 0;

        return {
            total,
            active,
            featured,
            avgRating: avgRating.toFixed(1)
        };
    }

    /**
     * Add view to event
     */
    async addEventView(eventId) {
        try {
            const response = await fetch(`${window.API_BASE_URL}/event/${eventId}/view`, {
                method: 'POST'
            });
            const result = await response.json();
            return result.success;
        } catch (error) {
            console.error('Error adding event view:', error);
            return false;
        }
    }
}

// Export for use
window.EventManager = EventManager;
