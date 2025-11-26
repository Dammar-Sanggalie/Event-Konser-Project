/* ===== SHOPPING CART LOGIC ===== */

class ShoppingCart {
    constructor(storageKey = 'eventCart') {
        this.storageKey = storageKey;
        this.items = this.loadFromStorage();
        this.listeners = [];
    }

    /**
     * Add item to cart
     */
    addItem(item) {
        const existingItem = this.items.find(i => i.ticketId === item.ticketId);

        if (existingItem) {
            existingItem.quantity += item.quantity || 1;
        } else {
            this.items.push({
                ticketId: item.ticketId,
                eventId: item.eventId,
                eventName: item.eventName,
                ticketType: item.ticketType,
                price: item.price,
                quantity: item.quantity || 1,
                image: item.image
            });
        }

        this.saveToStorage();
        this.notifyListeners();
        return true;
    }

    /**
     * Remove item from cart
     */
    removeItem(ticketId) {
        this.items = this.items.filter(i => i.ticketId !== ticketId);
        this.saveToStorage();
        this.notifyListeners();
    }

    /**
     * Update item quantity
     */
    updateQuantity(ticketId, quantity) {
        const item = this.items.find(i => i.ticketId === ticketId);
        if (item) {
            if (quantity <= 0) {
                this.removeItem(ticketId);
            } else {
                item.quantity = quantity;
                this.saveToStorage();
                this.notifyListeners();
            }
        }
    }

    /**
     * Clear all items from cart
     */
    clear() {
        this.items = [];
        this.saveToStorage();
        this.notifyListeners();
    }

    /**
     * Get total items count
     */
    getItemCount() {
        return this.items.reduce((total, item) => total + item.quantity, 0);
    }

    /**
     * Get cart subtotal
     */
    getSubtotal() {
        return this.items.reduce((total, item) => total + (item.price * item.quantity), 0);
    }

    /**
     * Apply discount/promo code
     */
    applyPromo(promoCode) {
        // This would typically validate the promo code against the backend
        // For now, returning a mock discount
        return {
            code: promoCode,
            discountType: 'PERCENTAGE', // or 'FIXED'
            discountValue: 10,
            isValid: true
        };
    }

    /**
     * Calculate total with tax and discount
     */
    calculateTotal(taxRate = 0.1, discountAmount = 0) {
        const subtotal = this.getSubtotal();
        const tax = subtotal * taxRate;
        const total = subtotal + tax - discountAmount;

        return {
            subtotal,
            tax,
            discount: discountAmount,
            total,
            itemCount: this.getItemCount()
        };
    }

    /**
     * Get cart summary
     */
    getSummary() {
        const calculation = this.calculateTotal();
        return {
            items: this.items,
            ...calculation
        };
    }

    /**
     * Check if cart is empty
     */
    isEmpty() {
        return this.items.length === 0;
    }

    /**
     * Validate cart items
     */
    validate() {
        if (this.isEmpty()) {
            return { valid: false, message: 'Cart is empty' };
        }

        // Check if all items have valid quantities
        const invalidItem = this.items.find(i => i.quantity < 1);
        if (invalidItem) {
            return { valid: false, message: `Invalid quantity for ${invalidItem.eventName}` };
        }

        return { valid: true };
    }

    /**
     * Convert cart to order
     */
    convertToOrder(customerInfo) {
        const validation = this.validate();
        if (!validation.valid) {
            throw new Error(validation.message);
        }

        const order = {
            orderDate: new Date().toISOString(),
            customerName: customerInfo.name,
            customerEmail: customerInfo.email,
            customerPhone: customerInfo.phone,
            items: this.items,
            ...this.calculateTotal()
        };

        return order;
    }

    /**
     * Save cart to localStorage
     */
    saveToStorage() {
        try {
            localStorage.setItem(this.storageKey, JSON.stringify(this.items));
        } catch (error) {
            console.error('Error saving cart to storage:', error);
        }
    }

    /**
     * Load cart from localStorage
     */
    loadFromStorage() {
        try {
            const data = localStorage.getItem(this.storageKey);
            return data ? JSON.parse(data) : [];
        } catch (error) {
            console.error('Error loading cart from storage:', error);
            return [];
        }
    }

    /**
     * Add listener for cart changes
     */
    addListener(callback) {
        this.listeners.push(callback);
    }

    /**
     * Remove listener
     */
    removeListener(callback) {
        this.listeners = this.listeners.filter(l => l !== callback);
    }

    /**
     * Notify all listeners of changes
     */
    notifyListeners() {
        this.listeners.forEach(callback => callback(this.getSummary()));
    }

    /**
     * Get cart statistics
     */
    getStats() {
        return {
            itemCount: this.getItemCount(),
            uniqueItems: this.items.length,
            subtotal: this.getSubtotal(),
            isEmpty: this.isEmpty()
        };
    }
}

// Create global cart instance
window.cart = new ShoppingCart();

// Export for use
window.ShoppingCart = ShoppingCart;
