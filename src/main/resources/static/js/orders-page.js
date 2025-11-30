/**
 * ORDER HISTORY PAGE - Fetch & Display User Orders & Tickets
 * Menampilkan semua order user dengan ticket details
 */

let userOrders = [];
let selectedOrder = null;

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
        
        if (!user || !user.id) {
            showToast('User not found', 'error');
            return;
        }
        
        const response = await fetch(
            `${window.API_BASE_URL}/orders/user/${user.id}`,
            {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            }
        );
        
        const result = await response.json();
        
        if (result.success && result.data) {
            userOrders = result.data;
            displayOrders(userOrders);
        } else {
            showToast(result.message || 'Failed to load orders', 'error');
        }
        
    } catch (error) {
        console.error('Error loading user orders:', error);
        showToast('Error fetching orders', 'error');
    }
}

/**
 * Display orders in table/list
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
    
    // Build orders table
    ordersList.innerHTML = `
        <div class="overflow-x-auto">
            <table class="w-full text-sm">
                <thead>
                    <tr class="border-b-2 border-gray-200">
                        <th class="text-left py-3 px-4 font-semibold text-gray-700">Order ID</th>
                        <th class="text-left py-3 px-4 font-semibold text-gray-700">Event</th>
                        <th class="text-left py-3 px-4 font-semibold text-gray-700">Date</th>
                        <th class="text-left py-3 px-4 font-semibold text-gray-700">Amount</th>
                        <th class="text-left py-3 px-4 font-semibold text-gray-700">Status</th>
                        <th class="text-left py-3 px-4 font-semibold text-gray-700">Actions</th>
                    </tr>
                </thead>
                <tbody>
                    ${orders.map(order => `
                        <tr class="border-b border-gray-100 hover:bg-gray-50">
                            <td class="py-4 px-4 font-mono text-primary-600">#${order.idPembelian}</td>
                            <td class="py-4 px-4">${order.ticket?.event?.namaEvent || 'N/A'}</td>
                            <td class="py-4 px-4">${formatDate(order.tanggalPembelian)}</td>
                            <td class="py-4 px-4 font-semibold">${formatCurrency(order.totalHarga)}</td>
                            <td class="py-4 px-4">
                                <span class="px-3 py-1 rounded-full text-xs font-semibold text-white ${
                                    order.status === 'PAID' ? 'bg-green-500' :
                                    order.status === 'PENDING' ? 'bg-yellow-500' :
                                    order.status === 'CANCELLED' ? 'bg-red-500' :
                                    'bg-gray-500'
                                }">
                                    ${order.status}
                                </span>
                            </td>
                            <td class="py-4 px-4">
                                <button class="text-primary-600 hover:text-primary-800 font-medium text-sm"
                                        onclick="viewOrderDetails(${order.idPembelian})">
                                    View
                                </button>
                            </td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
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
            selectedOrder = result.data;
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
                        <span>${formatCurrency(order.totalHarga)}</span>
                    </div>
                    ${order.diskonTotal ? `
                        <div class="flex justify-between text-green-600">
                            <span>Discount:</span>
                            <span>- ${formatCurrency(order.diskonTotal)}</span>
                        </div>
                    ` : ''}
                    <div class="flex justify-between font-bold text-lg border-t pt-2">
                        <span>Total:</span>
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
    if (!selectedOrder || !selectedOrder.qrCode) {
        showToast('QR Code not available', 'warning');
        return;
    }
    
    // Generate PDF or image download
    const link = document.createElement('a');
    link.href = `data:image/png;base64,${selectedOrder.qrCode}`;
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
