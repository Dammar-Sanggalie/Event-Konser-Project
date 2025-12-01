// Base URL backend Anda

if (typeof window.API_BASE_URL === 'undefined') {
    window.API_BASE_URL = 'http://localhost:8081/api'; // Export to window for use in other files
}

/**
 * Fungsi helper untuk melakukan fetch request.
 * Ini mirip dengan 'axios' di React, tapi versi sederhana.
 * @param {string} endpoint - Cth: /events
 * @param {object} options - Opsi standar untuk fetch (method, headers, body)
 */
async function apiFetch(endpoint, options = {}) {
    const url = `${window.API_BASE_URL}${endpoint}`;
    
    // Siapkan headers default
    const defaultHeaders = {
        'Content-Type': 'application/json',
    };
    
    // Cek jika ada token di auth.js dan tambahkan ke header
    // (Kita akan buat auth.js sebentar lagi)
    if (window.auth && auth.isAuthenticated()) {
        // Note: Backend kita tidak pakai JWT, jadi baris ini
        // sebenarnya tidak diperlukan, TAPI ini adalah
        // best practice jika nanti Anda menambahkannya.
        // defaultHeaders['Authorization'] = `Bearer ${auth.getToken()}`;
    }

    const config = {
        ...options,
        headers: {
            ...defaultHeaders,
            ...options.headers,
        },
    };

    try {
        const response = await fetch(url, config);
        
        // Jika respons bukan JSON (cth: 204 No Content)
        if (response.status === 204) {
            return null;
        }

        const data = await response.json();
        
        if (!response.ok) {
            // Jika backend mengirim pesan error
            throw new Error(data.message || 'Terjadi kesalahan pada server');
        }
        
        // Handle response format from the backend
        if (data.success) {
            // For endpoints that return the data directly in the response (like /events/upcoming)
            // we need to check if the data is in the root or in data.data
            if (Array.isArray(data) || (data.data === undefined && data.success === true)) {
                return data; // Return the entire response for arrays or responses without a data property
            } else if (data.data !== undefined) {
                return data.data; // Return the data property for object responses
            } else {
                return data; // Fallback to returning the entire response
            }
        } else {
            throw new Error(data.message || 'Respons API tidak sukses');
        }
        
    } catch (error) {
        console.error('API Fetch Error:', error);
        // Tampilkan error ke user
        alert(`Error: ${error.message}`);
        throw error;
    }
}