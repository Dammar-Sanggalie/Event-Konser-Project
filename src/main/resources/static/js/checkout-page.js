/**
 * CHECKOUT PAGE - BOOKING & ORDER LOGIC
 * Menangani form validation, promo code, dan order creation
 */

if (typeof checkoutState === 'undefined') {
    window.checkoutState = {
        ticketId: null,
        quantity: 1,
        ticketData: null,
        promoCode: null,
        discountAmount: 0,
        totalPrice: 0
    };
}

document.addEventListener('DOMContentLoaded', async () => {
    await initializeCheckout();
});

/**
 * Initialize checkout page
 */
async function initializeCheckout() {
    try {
        // Check if user logged in
        if (!window.auth.isAuthenticated()) {
            showToast('Please login first', 'warning');
            window.location.href = '/login.html?redirect=checkout.html';
            return;
        }
        
        // Get order data from sessionStorage
        const orderDataStr = sessionStorage.getItem('checkout_order');
        if (!orderDataStr) {
            showToast('No order data found. Please select tickets first', 'error');
            setTimeout(() => window.location.href = '/events.html', 2000);
            return;
        }
        
        const orderData = JSON.parse(orderDataStr);
        window.checkoutState.orderData = orderData;
        window.checkoutState.items = orderData.items || [];
        window.checkoutState.subtotal = orderData.subtotal || 0;
        
        // Display order summary
        displayOrderSummary(orderData);
        
        // Setup event listeners
        setupCheckoutListeners();
        
    } catch (error) {
        console.error('Error initializing checkout:', error);
        showToast('Error loading checkout', 'error');
    }
}

/**
 * Display order summary from booking
 */
function displayOrderSummary(orderData) {
    const itemsContainer = document.getElementById('order-items');
    if (!itemsContainer) return;
    
    let itemsHtml = '';
    
    (orderData.items || []).forEach(item => {
        const itemTotal = item.harga * item.jumlah;
        itemsHtml += `
            <div class="flex items-center justify-between py-3 border-b">
                <div>
                    <p class="font-medium text-gray-900">${item.jenisTiket}</p>
                    <p class="text-sm text-gray-600">${formatCurrency(item.harga)} x ${item.jumlah}</p>
                </div>
                <p class="font-semibold text-gray-900">${formatCurrency(itemTotal)}</p>
            </div>
        `;
    });
    
    itemsContainer.innerHTML = itemsHtml;
    
    const adminFee = 15000;
    const subtotal = orderData.subtotal || 0;
    const total = subtotal + adminFee;
    
    window.checkoutState.totalPrice = total;
    window.checkoutState.adminFee = adminFee;
    
    // Update sidebar totals
    updateCheckoutTotals(subtotal, adminFee, 0, total);
}

/**
 * Update checkout totals in sidebar
 */
function updateCheckoutTotals(subtotal, adminFee, discount, total) {
    const subtotalElem = document.getElementById('subtotal');
    const discountElem = document.getElementById('discount');
    const adminFeeElem = document.getElementById('admin-fee');
    const totalElem = document.getElementById('total');
    
    if (subtotalElem) subtotalElem.textContent = formatCurrency(subtotal);
    if (adminFeeElem) adminFeeElem.textContent = formatCurrency(adminFee);
    if (discountElem) discountElem.textContent = `${discount > 0 ? '-' : ''}${formatCurrency(discount)}`;
    if (totalElem) totalElem.textContent = formatCurrency(total);
}

/**
 * Setup checkout event listeners
 */
function setupCheckoutListeners() {
    const promoInput = document.getElementById('promo-code-input');
    const promoBtn = document.getElementById('apply-promo-btn');
    const checkoutBtn = document.getElementById('checkout-button');
    const emailInput = document.getElementById('email-input');
    const phoneInput = document.getElementById('phone-input');
    const backBtn = document.getElementById('back-to-booking-btn');
    
    // Promo code
    if (promoBtn) promoBtn.addEventListener('click', () => applyPromoCode());
    if (promoInput) {
        promoInput.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') applyPromoCode();
        });
    }
    
    // Checkout button
    if (checkoutBtn) {
        checkoutBtn.addEventListener('click', () => processCheckout());
    }
    
    // Back button
    if (backBtn) {
        backBtn.addEventListener('click', () => {
            sessionStorage.removeItem('checkout_order');
            window.location.href = '/booking.html';
        });
    }
    
    // Form validation
    if (emailInput) {
        emailInput.addEventListener('change', validateEmail);
    }
    if (phoneInput) {
        phoneInput.addEventListener('change', validatePhone);
    }
}

/**
 * Apply promo code
 */
async function applyPromoCode() {
    const promoInput = document.getElementById('promo-code');
    const code = promoInput.value.trim().toUpperCase();
    
    if (!code) {
        showToast('Please enter a promo code', 'warning');
        return;
    }
    
    try {
        const response = await fetch(`${window.API_BASE_URL}/promo/validate/${code}`);
        const result = await response.json();
        
        if (result.success && result.data) {
            const promo = result.data;
            window.checkoutState.promoCode = promo;
            
            // Calculate discount on subtotal
            const subtotal = window.checkoutState.subtotal;
            if (promo.jenisDiskon === 'PERCENTAGE') {
                window.checkoutState.discountAmount = (subtotal * promo.nilaiDiskon) / 100;
            } else {
                window.checkoutState.discountAmount = promo.nilaiDiskon;
            }
            
            updatePriceCalculation();
            showToast(`Promo applied! Discount: ${formatCurrency(window.checkoutState.discountAmount)}`, 'success');
            promoInput.disabled = true;
            
        } else {
            window.checkoutState.promoCode = null;
            window.checkoutState.discountAmount = 0;
            showToast(result.message || 'Invalid promo code', 'error');
            updatePriceCalculation();
        }
    } catch (error) {
        console.error('Error validating promo code:', error);
        showToast('Error validating promo code', 'error');
    }
}

/**
 * Update price calculation
 */
function updatePriceCalculation() {
    const subtotal = window.checkoutState.subtotal || 0;
    const adminFee = 15000;
    const discount = window.checkoutState.discountAmount || 0;
    const total = Math.max(0, subtotal + adminFee - discount);
    
    window.checkoutState.totalPrice = total;
    
    // Display calculations - using correct checkout.html IDs
    const subtotalElem = document.getElementById('subtotal');
    const discountElem = document.getElementById('discount');
    const adminFeeElem = document.getElementById('admin-fee');
    const totalElem = document.getElementById('total');
    
    if (subtotalElem) subtotalElem.textContent = formatCurrency(subtotal);
    if (adminFeeElem) adminFeeElem.textContent = formatCurrency(adminFee);
    if (discountElem) discountElem.textContent = `-${formatCurrency(discount)}`;
    if (totalElem) totalElem.textContent = formatCurrency(total);
}


/**
 * Process checkout - Create order
 */
async function processCheckout() {
    try {
        // Validate user logged in
        if (!window.auth.isAuthenticated()) {
            showToast('Please login first', 'warning');
            window.location.href = '/login.html?redirect=checkout.html';
            return;
        }
        
        const user = window.auth.getUser();
        const orderData = window.checkoutState.orderData;
        
        if (!orderData) {
            showToast('No order data found', 'error');
            return;
        }
        
        if (!user || !user.idPengguna) {
            showToast('User information not found', 'error');
            return;
        }
        
        // Prepare booking request from order data
        const bookingRequest = {
            idPengguna: user.idPengguna,
            totalHarga: window.checkoutState.totalPrice,
            subtotal: window.checkoutState.subtotal || 0,
            discountAmount: window.checkoutState.discountAmount || 0,
            promoCode: window.checkoutState.promoCode?.code || null,
            items: (orderData.items || []).map(item => ({
                idTiket: item.idTiket,
                jumlah: item.jumlah
            }))
        };
        
        // Create order
        const response = await fetch(`${window.API_BASE_URL}/orders/book`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${window.auth.getToken()}`
            },
            body: JSON.stringify(bookingRequest)
        });
        
        const result = await response.json();
        
        if (result.success && result.data) {
            const order = result.data;
            showToast('Order created successfully! Redirecting to payment...', 'success');
            
            // Clear sessionStorage and redirect to payment page
            sessionStorage.removeItem('checkout_order');
            setTimeout(() => {
                window.location.href = `/payment.html?orderId=${order.idPembelian}`;
            }, 1500);
        } else {
            showToast(result.message || 'Failed to create order', 'error');
        }
        
    } catch (error) {
        console.error('Error processing checkout:', error);
        showToast('Error processing checkout', 'error');
    }
}

/**
 * Utility: Format currency to IDR
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
    // You can implement a better toast UI here
}

/**
 * Alias for proceedToPayment in checkout.html
 */
function proceedToPayment() {
    processCheckout();
}

/**
 * Apply promo code from checkout.html
 */
function applyPromo() {
    applyPromoCode();
}
