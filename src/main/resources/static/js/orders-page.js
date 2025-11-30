/**
 * ORDER HISTORY PAGE - Fetch & Display User Orders & Tickets
 * Menampilkan semua order user dengan ticket details
 */

if (typeof userOrders === 'undefined') {
    window.userOrders = [];
}

if (typeof selectedOrder === 'undefined') {
    window.selectedOrder = null;
}

document.addEventListener('DOMContentLoaded', async () => {
    await initializeOrdersPage();
});

/**
 * Initialize orders page
 */
async function initializeOrdersPage() {
    try {
        // Check if user logged in
        if (!window.auth.isAuthenticated()) {
            showToast('Please login first', 'warning');
            window.location.href = '/login.html?redirect=orders.html';
            return;
        }
        
        // Load user orders
        await loadUserOrders();
        
        // Setup listeners
        setupOrdersListeners();
        
    } catch (error) {
        console.error('Error initializing orders page:', error);
        showToast('Error loading orders', 'error');
    }
}

/**
 * Load user's orders
 */
async function loadUserOrders() {
    try {
        const user = window.auth.getUser();
        const token = window.auth.getToken();
        
        if (!user || !user.idPengguna) {
            console.error('[ERROR] User not found or invalid user object:', user);
            showToast('User not found', 'error');
            return;
        }
        
        const response = await fetch(
            `${window.API_BASE_URL}/orders/user/${user.idPengguna}`,
            {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            }
        );
        
        const result = await response.json();
        
        if (result.success && result.data) {
            window.userOrders = result.data;
            displayOrders(window.userOrders);
        } else {
            showToast(result.message || 'Failed to load orders', 'error');
        }
        
    } catch (error) {
        console.error('Error loading user orders:', error);
        showToast('Error fetching orders', 'error');
    }
}

/**
 * Display orders in card grid with images
 */
function displayOrders(orders) {
    const ordersList = document.getElementById('orders-list');
    const noOrdersMsg = document.getElementById('no-orders-message');
    
    if (!ordersList) return;
    
    if (orders.length === 0) {
        ordersList.innerHTML = '';
        if (noOrdersMsg) noOrdersMsg.classList.remove('hidden');
        return;
    }
    
    if (noOrdersMsg) noOrdersMsg.classList.add('hidden');
    
    // Build orders card grid
    ordersList.innerHTML = `
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            ${orders.map(order => {
                // Calculate payment amount
                let displayAmount = order.totalHarga;
                if (order.discountAmount && order.discountAmount > 0) {
                    displayAmount = (order.subtotal || order.totalHarga) - order.discountAmount;
                }
                
                // Get event image URL
                const eventImage = order.eventImageUrl || (order.ticket?.event?.posterUrl) || '/assets/images/placeholder-event.jpg';
                
                return `
                <div class="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition-shadow cursor-pointer"
                     onclick="viewOrderDetails(${order.idPembelian})">
                    <!-- Event Image -->
                    <div class="relative h-40 bg-gray-200 overflow-hidden">
                        <img src="${eventImage}" alt="${order.eventName}" 
                             class="w-full h-full object-cover hover:scale-105 transition-transform"
                             onerror="this.src='/assets/images/placeholder-event.jpg'">
                        <!-- Status Badge -->
                        <div class="absolute top-3 right-3">
                            <span class="px-3 py-1 rounded-full text-xs font-semibold text-white ${
                                order.status === 'PAID' ? 'bg-green-500' :
                                order.status === 'PENDING' ? 'bg-yellow-500' :
                                order.status === 'CANCELLED' ? 'bg-red-500' :
                                'bg-gray-500'
                            }">
                                ${order.status}
                            </span>
                        </div>
                    </div>
                    
                    <!-- Card Content -->
                    <div class="p-4 space-y-3">
                        <!-- Order ID and Event Name -->
                        <div>
                            <p class="text-xs text-gray-600">Order ID: <span class="font-mono font-semibold">#${order.idPembelian}</span></p>
                            <h3 class="font-semibold text-gray-900 line-clamp-2">${order.eventName || 'Event'}</h3>
                        </div>
                        
                        <!-- Details -->
                        <div class="text-sm text-gray-600 space-y-1">
                            <p>📅 ${formatDate(order.tanggalPembelian)}</p>
                            <p>🎫 ${order.ticketType || 'Regular'} • Qty: ${order.jumlah || 1}</p>
                            <p class="font-semibold text-primary-600">Total: ${formatCurrency(displayAmount)}</p>
                        </div>
                        
                        <!-- Action Button -->
                        <button class="w-full mt-3 px-3 py-2 bg-primary-600 text-white rounded-lg text-sm font-medium hover:bg-primary-700 transition"
                                onclick="event.stopPropagation(); viewOrderDetails(${order.idPembelian})">
                            View Details
                        </button>
                    </div>
                </div>
            `;
            }).join('')}
        </div>
    `;
}

/**
 * View order details modal
 */
async function viewOrderDetails(orderId) {
    try {
        const token = window.auth.getToken();
        
        const response = await fetch(
            `${window.API_BASE_URL}/orders/${orderId}`,
            {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            }
        );
        
        const result = await response.json();
        
        if (result.success && result.data) {
            window.selectedOrder = result.data;
            displayOrderModal(result.data);
        } else {
            showToast('Failed to load order details', 'error');
        }
        
    } catch (error) {
        console.error('Error loading order details:', error);
        showToast('Error fetching order details', 'error');
    }
}

/**
 * Display order details modal
 */
function displayOrderModal(order) {
    const modalContent = document.getElementById('order-modal-content');
    const modal = document.getElementById('order-modal');
    
    if (!modal) return;
    
    modalContent.innerHTML = `
        <div class="space-y-6">
            <!-- Header -->
            <div class="flex items-center justify-between border-b pb-4">
                <div>
                    <h2 class="text-2xl font-bold text-gray-900">Order #${order.idPembelian}</h2>
                    <p class="text-sm text-gray-600 mt-1">${formatDate(order.tanggalPembelian)}</p>
                </div>
                <span class="px-4 py-2 rounded-full font-semibold text-white ${
                    order.status === 'PAID' ? 'bg-green-500' :
                    order.status === 'PENDING' ? 'bg-yellow-500' :
                    order.status === 'CANCELLED' ? 'bg-red-500' :
                    'bg-gray-500'
                }">
                    ${order.status}
                </span>
            </div>
            
            <!-- Event Information -->
            <div>
                <h3 class="font-semibold text-gray-900 mb-3">Event Details</h3>
                <div class="bg-gray-50 p-4 rounded-lg space-y-2">
                    <p><strong>Event:</strong> ${order.ticket?.event?.namaEvent || 'N/A'}</p>
                    <p><strong>Date:</strong> ${formatDate(order.ticket?.event?.tanggalMulai)}</p>
                    <p><strong>Venue:</strong> ${order.ticket?.event?.venue?.namaVenue || 'N/A'}</p>
                    <p><strong>City:</strong> ${order.ticket?.event?.venue?.kota || 'N/A'}</p>
                </div>
            </div>
            
            <!-- Ticket Information -->
            <div>
                <h3 class="font-semibold text-gray-900 mb-3">Ticket Details</h3>
                <div class="bg-gray-50 p-4 rounded-lg space-y-2">
                    <p><strong>Ticket Type:</strong> ${order.ticket?.kategoriTiket || 'N/A'}</p>
                    <p><strong>Quantity:</strong> ${order.jumlahTiket || 1}</p>
                    <p><strong>Unit Price:</strong> ${formatCurrency(order.ticket?.hargaTiket)}</p>
                </div>
            </div>
            
            <!-- QR Code (if available) -->
            ${order.qrCode ? `
                <div>
                    <h3 class="font-semibold text-gray-900 mb-3">Ticket QR Code</h3>
                    <div class="bg-gray-50 p-6 rounded-lg flex flex-col items-center">
                        <img src="data:image/png;base64,${order.qrCode}" alt="QR Code" class="w-40 h-40 mb-3">
                        <p class="text-xs text-gray-600 text-center">Show this code at the entrance</p>
                    </div>
                </div>
            ` : ''}
            
            <!-- Pricing Summary -->
            <div>
                <h3 class="font-semibold text-gray-900 mb-3">Pricing</h3>
                <div class="bg-gray-50 p-4 rounded-lg space-y-2">
                    <div class="flex justify-between">
                        <span>Subtotal:</span>
                        <span>${formatCurrency(order.subtotal || order.totalHarga)}</span>
                    </div>
                    ${order.discountAmount && order.discountAmount > 0 ? `
                        <div class="flex justify-between text-green-600">
                            <span>Discount${order.promoCode ? ' (' + order.promoCode + ')' : ''}:</span>
                            <span>- ${formatCurrency(order.discountAmount)}</span>
                        </div>
                    ` : ''}
                    <div class="flex justify-between font-bold text-lg border-t pt-2">
                        <span>Total Amount Paid:</span>
                        <span>${formatCurrency(order.totalHarga)}</span>
                    </div>
                </div>
            </div>
            
            <!-- Actions -->
            <div class="flex gap-3 pt-4">
                ${order.status === 'PENDING' ? `
                    <button class="flex-1 bg-primary-600 hover:bg-primary-700 text-white font-semibold py-2 rounded-lg transition"
                            onclick="proceedToPayment(${order.idPembelian})">
                        Complete Payment
                    </button>
                    <button class="flex-1 bg-red-600 hover:bg-red-700 text-white font-semibold py-2 rounded-lg transition"
                            onclick="cancelOrder(${order.idPembelian})">
                        Cancel Order
                    </button>
                ` : ''}
                <button class="flex-1 bg-gray-300 hover:bg-gray-400 text-gray-900 font-semibold py-2 rounded-lg transition"
                        onclick="downloadTicket(${order.idPembelian})">
                    Download Ticket
                </button>
            </div>
        </div>
    `;
    
    // Show modal
    modal.classList.remove('hidden');
}

/**
 * Proceed to payment
 */
function proceedToPayment(orderId) {
    window.location.href = `/payment.html?orderId=${orderId}`;
}

/**
 * Cancel order
 */
async function cancelOrder(orderId) {
    if (!confirm('Are you sure you want to cancel this order?')) return;
    
    try {
        const token = window.auth.getToken();
        
        const response = await fetch(
            `${window.API_BASE_URL}/orders/${orderId}`,
            {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            }
        );
        
        const result = await response.json();
        
        if (result.success) {
            showToast('Order cancelled successfully', 'success');
            closeOrderModal();
            await loadUserOrders();
        } else {
            showToast(result.message || 'Failed to cancel order', 'error');
        }
        
    } catch (error) {
        console.error('Error cancelling order:', error);
        showToast('Error cancelling order', 'error');
    }
}

/**
 * Download ticket
 */
function downloadTicket(orderId) {
    if (!window.selectedOrder || !window.selectedOrder.qrCode) {
        showToast('QR Code not available', 'warning');
        return;
    }
    
    // Generate PDF or image download
    const link = document.createElement('a');
    link.href = `data:image/png;base64,${window.selectedOrder.qrCode}`;
    link.download = `ticket-${orderId}.png`;
    link.click();
    
    showToast('Ticket downloaded', 'success');
}

/**
 * Close order modal
 */
function closeOrderModal() {
    const modal = document.getElementById('order-modal');
    if (modal) {
        modal.classList.add('hidden');
    }
}

/**
 * Setup orders page listeners
 */
function setupOrdersListeners() {
    const closeBtn = document.getElementById('close-order-modal');
    const modal = document.getElementById('order-modal');
    
    if (closeBtn) {
        closeBtn.addEventListener('click', closeOrderModal);
    }
    
    // Close modal on outside click
    if (modal) {
        modal.addEventListener('click', (e) => {
            if (e.target === modal) {
                closeOrderModal();
            }
        });
    }
}

/**
 * Utility: Format date
 */
function formatDate(dateString) {
    if (!dateString) return 'N/A';
    const options = { 
        year: 'numeric', 
        month: 'short', 
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    };
    return new Date(dateString).toLocaleDateString('id-ID', options);
}

/**
 * Utility: Format currency
 */
function formatCurrency(amount) {
    return new Intl.NumberFormat('id-ID', {
        style: 'currency',
        currency: 'IDR',
        minimumFractionDigits: 0
    }).format(amount);
}

/**
 * Utility: Show toast
 */
function showToast(message, type = 'info') {
    console.log(`[${type.toUpperCase()}] ${message}`);
}
