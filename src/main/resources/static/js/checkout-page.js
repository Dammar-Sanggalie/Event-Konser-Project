/**
 * CHECKOUT PAGE - BOOKING & ORDER LOGIC
 * Menangani form validation, promo code, dan order creation
 */

let checkoutState = {
    ticketId: null,
    quantity: 1,
    ticketData: null,
    promoCode: null,
    discountAmount: 0,
    totalPrice: 0
};

document.addEventListener('DOMContentLoaded', async () => {
    await initializeCheckout();
});

/**
 * Initialize checkout page
 */
async function initializeCheckout() {
    try {
        // Get ticket ID from URL
        const params = new URLSearchParams(window.location.search);
        const ticketId = params.get('ticketId');
        const quantity = params.get('quantity') || 1;
        
        if (!ticketId) {
            showToast('No ticket selected', 'error');
            setTimeout(() => window.location.href = '/events.html', 2000);
            return;
        }
        
        checkoutState.ticketId = ticketId;
        checkoutState.quantity = parseInt(quantity);
        
        // Load ticket details
        await loadTicketDetails(ticketId);
        
        // Setup event listeners
        setupCheckoutListeners();
        
    } catch (error) {
        console.error('Error initializing checkout:', error);
        showToast('Error loading checkout', 'error');
    }
}

/**
 * Load ticket details
 */
async function loadTicketDetails(ticketId) {
    try {
        const response = await fetch(`${window.API_BASE_URL}/tickets/${ticketId}`);
        const result = await response.json();
        
        if (result.success && result.data) {
            checkoutState.ticketData = result.data;
            displayTicketDetails(result.data);
            updatePriceCalculation();
        }
    } catch (error) {
        console.error('Error loading ticket details:', error);
        showToast('Failed to load ticket details', 'error');
    }
}

/**
 * Display ticket details
 */
function displayTicketDetails(ticket) {
    document.getElementById('ticket-name').textContent = ticket.kategoriTiket || 'Ticket';
    document.getElementById('ticket-price').textContent = formatCurrency(ticket.hargaTiket);
    document.getElementById('ticket-description').textContent = ticket.deskripsiTiket || 'Event ticket';
    document.getElementById('available-stock').textContent = ticket.stok;
    document.getElementById('max-purchase').textContent = ticket.maxPembelian || 'Unlimited';
}

/**
 * Setup checkout event listeners
 */
function setupCheckoutListeners() {
    const qtyInput = document.getElementById('quantity-input');
    const qtyMinus = document.getElementById('qty-minus');
    const qtyPlus = document.getElementById('qty-plus');
    const promoInput = document.getElementById('promo-code-input');
    const promoBtn = document.getElementById('apply-promo-btn');
    const checkoutBtn = document.getElementById('checkout-button');
    const emailInput = document.getElementById('email-input');
    const phoneInput = document.getElementById('phone-input');
    
    // Quantity controls
    if (qtyMinus) qtyMinus.addEventListener('click', () => decreaseQuantity());
    if (qtyPlus) qtyPlus.addEventListener('click', () => increaseQuantity());
    if (qtyInput) {
        qtyInput.addEventListener('change', (e) => {
            updateQuantity(parseInt(e.target.value) || 1);
        });
    }
    
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
    
    // Form validation
    if (emailInput) {
        emailInput.addEventListener('change', validateEmail);
    }
    if (phoneInput) {
        phoneInput.addEventListener('change', validatePhone);
    }
}

/**
 * Increase quantity
 */
function increaseQuantity() {
    const max = checkoutState.ticketData?.maxPembelian || checkoutState.ticketData?.stok || 999;
    if (checkoutState.quantity < max) {
        checkoutState.quantity++;
        document.getElementById('quantity-input').value = checkoutState.quantity;
        updatePriceCalculation();
    }
}

/**
 * Decrease quantity
 */
function decreaseQuantity() {
    if (checkoutState.quantity > 1) {
        checkoutState.quantity--;
        document.getElementById('quantity-input').value = checkoutState.quantity;
        updatePriceCalculation();
    }
}

/**
 * Update quantity manually
 */
function updateQuantity(qty) {
    const max = checkoutState.ticketData?.maxPembelian || checkoutState.ticketData?.stok || 999;
    if (qty > 0 && qty <= max) {
        checkoutState.quantity = qty;
        updatePriceCalculation();
    } else {
        document.getElementById('quantity-input').value = checkoutState.quantity;
        showToast(`Max quantity: ${max}`, 'warning');
    }
}

/**
 * Apply promo code
 */
async function applyPromoCode() {
    const promoInput = document.getElementById('promo-code-input');
    const code = promoInput.value.trim();
    
    if (!code) {
        showToast('Please enter a promo code', 'warning');
        return;
    }
    
    try {
        const response = await fetch(`${window.API_BASE_URL}/promo-codes/validate`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ code })
        });
        
        const result = await response.json();
        
        if (result.success && result.data) {
            const promo = result.data;
            checkoutState.promoCode = promo;
            
            // Calculate discount
            const baseTotal = checkoutState.ticketData.hargaTiket * checkoutState.quantity;
            if (promo.typeDiskon === 'PERCENTAGE') {
                checkoutState.discountAmount = (baseTotal * promo.nilaiDiskon) / 100;
            } else {
                checkoutState.discountAmount = promo.nilaiDiskon;
            }
            
            updatePriceCalculation();
            showToast(`Promo applied! Discount: ${formatCurrency(checkoutState.discountAmount)}`, 'success');
            
        } else {
            checkoutState.promoCode = null;
            checkoutState.discountAmount = 0;
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
    const unitPrice = checkoutState.ticketData?.hargaTiket || 0;
    const subtotal = unitPrice * checkoutState.quantity;
    const discount = checkoutState.discountAmount;
    const total = Math.max(0, subtotal - discount);
    
    checkoutState.totalPrice = total;
    
    // Display calculations
    document.getElementById('subtotal-amount').textContent = formatCurrency(subtotal);
    document.getElementById('discount-amount').textContent = `- ${formatCurrency(discount)}`;
    document.getElementById('total-amount').textContent = formatCurrency(total);
    
    // Show/hide discount section
    const discountSection = document.getElementById('discount-section');
    if (discountSection) {
        discountSection.classList.toggle('hidden', discount === 0);
    }
}

/**
 * Validate email
 */
function validateEmail() {
    const email = document.getElementById('email-input').value;
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    const isValid = regex.test(email);
    
    const emailError = document.getElementById('email-error');
    if (emailError) {
        emailError.classList.toggle('hidden', isValid);
    }
    
    return isValid;
}

/**
 * Validate phone
 */
function validatePhone() {
    const phone = document.getElementById('phone-input').value;
    const regex = /^(\+62|0)[0-9]{9,12}$/;
    const isValid = regex.test(phone);
    
    const phoneError = document.getElementById('phone-error');
    if (phoneError) {
        phoneError.classList.toggle('hidden', isValid);
    }
    
    return isValid;
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
        
        // Validate form
        if (!validateEmail() || !validatePhone()) {
            showToast('Please fill all fields correctly', 'error');
            return;
        }
        
        const checkoutBtn = document.getElementById('checkout-button');
        checkoutBtn.disabled = true;
        checkoutBtn.textContent = 'Processing...';
        
        const user = window.auth.getUser();
        
        // Prepare booking request
        const bookingRequest = {
            idPengguna: user.id,
            totalHarga: checkoutState.totalPrice,
            items: [{
                idTiket: checkoutState.ticketId,
                jumlah: checkoutState.quantity
            }]
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
            
            // Redirect to payment page
            setTimeout(() => {
                window.location.href = `/payment.html?orderId=${order.idPembelian}`;
            }, 2000);
        } else {
            showToast(result.message || 'Failed to create order', 'error');
            checkoutBtn.disabled = false;
            checkoutBtn.textContent = 'Complete Checkout';
        }
        
    } catch (error) {
        console.error('Error processing checkout:', error);
        showToast('Error processing checkout', 'error');
        document.getElementById('checkout-button').disabled = false;
        document.getElementById('checkout-button').textContent = 'Complete Checkout';
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
