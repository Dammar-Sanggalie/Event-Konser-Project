/**
 * Admin Helper Functions
 * Reusable functions for admin pages CRUD operations
 */

/**
 * Format currency to Indonesian Rupiah
 */
function formatCurrency(amount) {
    return new Intl.NumberFormat('id-ID', {
        style: 'currency',
        currency: 'IDR',
        minimumFractionDigits: 0
    }).format(amount || 0);
}

/**
 * Format date to Indonesian format
 */
function formatDate(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return new Intl.DateTimeFormat('id-ID', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    }).format(date);
}

/**
 * Format date without time
 */
function formatDateOnly(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return new Intl.DateTimeFormat('id-ID', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    }).format(date);
}

/**
 * Get status badge styling
 */
function getStatusBadge(status) {
    const badges = {
        'UPCOMING': 'bg-blue-100 text-blue-800',
        'ONGOING': 'bg-green-100 text-green-800',
        'COMPLETED': 'bg-gray-100 text-gray-800',
        'CANCELLED': 'bg-red-100 text-red-800'
    };
    return badges[status] || 'bg-gray-100 text-gray-800';
}

/**
 * Get payment status badge styling
 */
function getPaymentStatusBadge(status) {
    const badges = {
        'PENDING': 'bg-yellow-100 text-yellow-800',
        'SUCCESS': 'bg-green-100 text-green-800',
        'PAID': 'bg-green-100 text-green-800',
        'CANCELLED': 'bg-red-100 text-red-800',
        'EXPIRED': 'bg-gray-100 text-gray-800',
        'FAILED': 'bg-red-100 text-red-800',
        'REFUNDED': 'bg-orange-100 text-orange-800'
    };
    return badges[status] || 'bg-gray-100 text-gray-800';
}

/**
 * Show toast notification
 */
function showToast(message, type = 'success') {
    const toast = document.createElement('div');
    const bgColor = type === 'success' ? 'bg-green-500' : type === 'error' ? 'bg-red-500' : 'bg-blue-500';
    
    toast.className = `fixed top-4 right-4 px-6 py-3 ${bgColor} text-white rounded-lg shadow-lg z-50 animate-fade-in`;
    toast.textContent = message;
    document.body.appendChild(toast);
    
    setTimeout(() => {
        toast.remove();
    }, 3000);
}

/**
 * Show confirmation dialog
 */
function showConfirm(message) {
    return confirm(message);
}

/**
 * Generic API call with error handling
 */
async function apiCall(endpoint, options = {}) {
    try {
        const token = localStorage.getItem('authToken');
        const headers = {
            'Content-Type': 'application/json',
            ...options.headers
        };
        
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }
        
        const response = await fetch(`${window.API_BASE_URL}${endpoint}`, {
            ...options,
            headers
        });
        
        if (!response.ok) {
            if (response.status === 401) {
                window.location.href = '/login.html';
                return null;
            }
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        return await response.json();
    } catch (error) {
        console.error('API call error:', error);
        showToast(error.message || 'An error occurred', 'error');
        return null;
    }
}

/**
 * Create table row HTML from data object
 */
function createTableRow(data, columns, actions = []) {
    let html = '<tr class="border-b border-gray-200 hover:bg-gray-50">';
    
    columns.forEach(column => {
        let value = data[column.key];
        
        if (column.format) {
            value = column.format(value);
        }
        
        html += `<td class="px-4 py-3">${value || '-'}</td>`;
    });
    
    if (actions.length > 0) {
        html += '<td class="px-4 py-3 flex gap-2">';
        actions.forEach(action => {
            html += `<a href="${action.href(data)}" class="text-primary-600 hover:text-primary-700 font-medium text-sm">${action.label}</a>`;
        });
        html += '</td>';
    }
    
    html += '</tr>';
    return html;
}

/**
 * Populate select dropdown from API
 */
async function populateSelect(selectId, endpoint, labelKey, valueKey = 'id') {
    const select = document.getElementById(selectId);
    if (!select) return;
    
    const result = await apiCall(endpoint);
    if (!result || !result.data) return;
    
    result.data.forEach(item => {
        const option = document.createElement('option');
        option.value = item[valueKey] || item.id;
        option.textContent = item[labelKey];
        select.appendChild(option);
    });
}

/**
 * Fill form from object
 */
function fillForm(formId, data) {
    const form = document.getElementById(formId);
    if (!form) return;
    
    Object.keys(data).forEach(key => {
        const field = form.elements[key];
        if (field) {
            if (field.type === 'checkbox') {
                field.checked = data[key];
            } else if (field.type === 'radio') {
                const radio = form.querySelector(`input[name="${key}"][value="${data[key]}"]`);
                if (radio) radio.checked = true;
            } else {
                field.value = data[key] || '';
            }
        }
    });
}

/**
 * Get form data as object
 */
function getFormData(formId) {
    const form = document.getElementById(formId);
    if (!form) return {};
    
    const formData = new FormData(form);
    const data = {};
    
    formData.forEach((value, key) => {
        if (data[key]) {
            if (!Array.isArray(data[key])) {
                data[key] = [data[key]];
            }
            data[key].push(value);
        } else {
            data[key] = value;
        }
    });
    
    return data;
}

/**
 * Validate required fields
 */
function validateRequired(formId, requiredFields) {
    const form = document.getElementById(formId);
    if (!form) return false;
    
    for (const field of requiredFields) {
        const input = form.elements[field];
        if (!input || !input.value.trim()) {
            showToast(`${field} is required`, 'error');
            return false;
        }
    }
    
    return true;
}

/**
 * Enable/disable form buttons during submission
 */
function setFormSubmitting(formId, isSubmitting) {
    const form = document.getElementById(formId);
    if (!form) return;
    
    const buttons = form.querySelectorAll('button[type="submit"]');
    buttons.forEach(btn => {
        btn.disabled = isSubmitting;
        if (isSubmitting) {
            btn.classList.add('opacity-50', 'cursor-not-allowed');
        } else {
            btn.classList.remove('opacity-50', 'cursor-not-allowed');
        }
    });
}

/**
 * Delete item with confirmation
 */
async function deleteItem(endpoint, itemName = 'Item') {
    if (!showConfirm(`Are you sure you want to delete this ${itemName}?`)) {
        return false;
    }
    
    const result = await apiCall(endpoint, { method: 'DELETE' });
    if (result && result.success) {
        showToast(`${itemName} deleted successfully`);
        return true;
    }
    return false;
}

/**
 * Format file size
 */
function formatFileSize(bytes) {
    if (bytes === 0) return '0 Bytes';
    
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    
    return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
}

/**
 * Paginate array
 */
function paginate(array, pageNumber = 1, pageSize = 10) {
    const startIndex = (pageNumber - 1) * pageSize;
    const endIndex = startIndex + pageSize;
    
    return {
        data: array.slice(startIndex, endIndex),
        totalPages: Math.ceil(array.length / pageSize),
        currentPage: pageNumber,
        total: array.length
    };
}

/**
 * Generate pagination HTML
 */
function generatePagination(totalPages, currentPage, onPageClick) {
    let html = '<div class="flex gap-2 justify-center mt-6">';
    
    for (let i = 1; i <= totalPages; i++) {
        const isActive = i === currentPage;
        const activeClass = isActive ? 'bg-primary-600 text-white' : 'bg-gray-200 text-gray-700 hover:bg-gray-300';
        html += `<button onclick="${onPageClick}(${i})" class="px-3 py-1 rounded ${activeClass}">${i}</button>`;
    }
    
    html += '</div>';
    return html;
}

/**
 * Debounce function for search
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
 * Filter array by search term
 */
function searchItems(items, searchTerm, searchFields) {
    if (!searchTerm.trim()) return items;
    
    const term = searchTerm.toLowerCase();
    return items.filter(item => {
        return searchFields.some(field => {
            const value = item[field];
            return value && value.toString().toLowerCase().includes(term);
        });
    });
}

/**
 * Check if user is admin
 */
function isAdmin() {
    const user = window.auth?.getUser?.();
    return user && user.role === 'ADMIN';
}

/**
 * Protect admin page - redirect if not admin
 */
function protectAdminPage() {
    if (!window.auth.isAuthenticated()) {
        window.location.href = '/login.html?redirect=' + encodeURIComponent(window.location.pathname);
        return;
    }
    
    if (!isAdmin()) {
        window.location.href = '/index.html';
        return;
    }
}

// Export for global use
window.admin = {
    formatCurrency,
    formatDate,
    formatDateOnly,
    getStatusBadge,
    getPaymentStatusBadge,
    showToast,
    showConfirm,
    apiCall,
    createTableRow,
    populateSelect,
    fillForm,
    getFormData,
    validateRequired,
    setFormSubmitting,
    deleteItem,
    formatFileSize,
    paginate,
    generatePagination,
    debounce,
    searchItems,
    isAdmin,
    protectAdminPage
};
