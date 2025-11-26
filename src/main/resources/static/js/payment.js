/* ===== PAYMENT PROCESSING LOGIC ===== */

class PaymentProcessor {
    constructor() {
        this.currentPayment = null;
        this.paymentMethods = [
            { id: 'BANK_TRANSFER', name: 'Bank Transfer', icon: 'bank' },
            { id: 'CREDIT_CARD', name: 'Credit Card', icon: 'credit-card' },
            { id: 'E_WALLET', name: 'E-Wallet', icon: 'wallet' }
        ];
    }

    /**
     * Get available payment methods
     */
    getPaymentMethods() {
        return this.paymentMethods;
    }

    /**
     * Validate payment data
     */
    validatePayment(paymentData) {
        const errors = [];

        if (!paymentData.method) {
            errors.push('Payment method is required');
        }

        if (!paymentData.amount || paymentData.amount <= 0) {
            errors.push('Payment amount must be greater than 0');
        }

        if (!paymentData.orderId) {
            errors.push('Order ID is required');
        }

        if (paymentData.method === 'CREDIT_CARD') {
            if (!paymentData.cardNumber || !this.validateCardNumber(paymentData.cardNumber)) {
                errors.push('Invalid card number');
            }
            if (!paymentData.cvv || !this.validateCVV(paymentData.cvv)) {
                errors.push('Invalid CVV');
            }
            if (!paymentData.expiryDate || !this.validateExpiryDate(paymentData.expiryDate)) {
                errors.push('Invalid expiry date');
            }
        }

        if (paymentData.method === 'E_WALLET') {
            if (!paymentData.walletPhone) {
                errors.push('Wallet phone number is required');
            }
        }

        return {
            valid: errors.length === 0,
            errors
        };
    }

    /**
     * Validate card number (Luhn algorithm)
     */
    validateCardNumber(cardNumber) {
        const digits = cardNumber.replace(/\D/g, '');
        if (digits.length < 13 || digits.length > 19) return false;

        let sum = 0;
        let isEven = false;

        for (let i = digits.length - 1; i >= 0; i--) {
            let digit = parseInt(digits[i], 10);

            if (isEven) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }

            sum += digit;
            isEven = !isEven;
        }

        return sum % 10 === 0;
    }

    /**
     * Validate CVV
     */
    validateCVV(cvv) {
        const cvvRegex = /^\d{3,4}$/;
        return cvvRegex.test(cvv.replace(/\s/g, ''));
    }

    /**
     * Validate expiry date
     */
    validateExpiryDate(expiryDate) {
        const [month, year] = expiryDate.split('/').map(x => x.trim());
        const now = new Date();

        if (!month || !year || month < 1 || month > 12) return false;

        const expiry = new Date(2000 + parseInt(year), parseInt(month), 0);
        return expiry > now;
    }

    /**
     * Process payment
     */
    async processPayment(paymentData) {
        const validation = this.validatePayment(paymentData);
        if (!validation.valid) {
            return {
                success: false,
                message: 'Validation failed',
                errors: validation.errors
            };
        }

        try {
            // Sanitize sensitive data
            const sanitizedData = {
                method: paymentData.method,
                amount: paymentData.amount,
                orderId: paymentData.orderId,
                currency: paymentData.currency || 'IDR'
            };

            if (paymentData.method === 'CREDIT_CARD') {
                sanitizedData.cardLastFour = paymentData.cardNumber.slice(-4);
            } else if (paymentData.method === 'E_WALLET') {
                sanitizedData.walletPhone = paymentData.walletPhone;
            }

            const response = await fetch(`${window.API_BASE_URL}/payment/process`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(sanitizedData)
            });

            const result = await response.json();

            if (result.success) {
                this.currentPayment = result.data;
                return {
                    success: true,
                    message: 'Payment processed successfully',
                    transactionId: result.data.idPembayaran
                };
            } else {
                return {
                    success: false,
                    message: result.message || 'Payment processing failed'
                };
            }
        } catch (error) {
            console.error('Payment error:', error);
            return {
                success: false,
                message: 'An error occurred while processing payment'
            };
        }
    }

    /**
     * Verify payment status
     */
    async verifyPayment(transactionId) {
        try {
            const response = await fetch(`${window.API_BASE_URL}/payment/${transactionId}/verify`);
            const result = await response.json();

            if (result.success) {
                return {
                    success: true,
                    status: result.data.statusPembayaran,
                    data: result.data
                };
            }

            return {
                success: false,
                message: 'Payment verification failed'
            };
        } catch (error) {
            console.error('Verification error:', error);
            return {
                success: false,
                message: 'An error occurred while verifying payment'
            };
        }
    }

    /**
     * Get payment history
     */
    async getPaymentHistory(options = {}) {
        try {
            const params = new URLSearchParams();
            if (options.limit) params.append('limit', options.limit);
            if (options.offset) params.append('offset', options.offset);
            if (options.status) params.append('status', options.status);

            const response = await fetch(`${window.API_BASE_URL}/payment/history?${params}`);
            const result = await response.json();

            if (result.success) {
                return result.data || [];
            }

            return [];
        } catch (error) {
            console.error('Error fetching payment history:', error);
            return [];
        }
    }

    /**
     * Calculate payment amount with fees
     */
    calculateAmount(baseAmount, paymentMethod) {
        let fee = 0;

        // Add method-specific fees
        if (paymentMethod === 'CREDIT_CARD') {
            fee = baseAmount * 0.025; // 2.5% fee
        } else if (paymentMethod === 'E_WALLET') {
            fee = baseAmount * 0.01; // 1% fee
        }
        // Bank transfer is free

        return {
            baseAmount,
            fee,
            total: baseAmount + fee
        };
    }

    /**
     * Format payment for display
     */
    formatPaymentDisplay(payment) {
        return {
            id: payment.idPembayaran,
            amount: `Rp ${payment.jumlahPembayaran.toLocaleString('id-ID')}`,
            method: this.getPaymentMethodName(payment.metodePembayaran),
            status: payment.statusPembayaran,
            date: new Date(payment.tanggalPembayaran).toLocaleDateString('id-ID'),
            time: new Date(payment.tanggalPembayaran).toLocaleTimeString('id-ID')
        };
    }

    /**
     * Get payment method display name
     */
    getPaymentMethodName(methodId) {
        const method = this.paymentMethods.find(m => m.id === methodId);
        return method ? method.name : 'Unknown';
    }

    /**
     * Generate payment receipt
     */
    generateReceipt(paymentData) {
        return {
            receiptNumber: `RCP-${Date.now()}`,
            paymentDate: new Date().toISOString(),
            paymentMethod: this.getPaymentMethodName(paymentData.method),
            amount: paymentData.amount,
            status: 'COMPLETED',
            orderId: paymentData.orderId
        };
    }
}

// Create global payment processor instance
window.paymentProcessor = new PaymentProcessor();

// Export for use
window.PaymentProcessor = PaymentProcessor;
