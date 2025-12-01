/* ===== FORM VALIDATION UTILITIES ===== */

class Validator {
    /**
     * Validate email
     */
    static isValidEmail(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }

    /**
     * Validate phone number
     */
    static isValidPhone(phone) {
        // Indonesian phone number format
        const phoneRegex = /^(?:\+62|0)[0-9]{9,12}$/;
        return phoneRegex.test(phone.replace(/\s/g, ''));
    }

    /**
     * Validate password strength
     */
    static getPasswordStrength(password) {
        let strength = 0;
        let feedback = [];

        if (password.length >= 8) {
            strength += 1;
        } else {
            feedback.push('Password should be at least 8 characters');
        }

        if (/[a-z]/.test(password)) {
            strength += 1;
        } else {
            feedback.push('Password should contain lowercase letters');
        }

        if (/[A-Z]/.test(password)) {
            strength += 1;
        } else {
            feedback.push('Password should contain uppercase letters');
        }

        if (/[0-9]/.test(password)) {
            strength += 1;
        } else {
            feedback.push('Password should contain numbers');
        }

        if (/[^a-zA-Z0-9]/.test(password)) {
            strength += 1;
        } else {
            feedback.push('Password should contain special characters');
        }

        return {
            strength,
            level: strength === 5 ? 'Strong' : strength >= 3 ? 'Medium' : 'Weak',
            feedback
        };
    }

    /**
     * Validate URL
     */
    static isValidUrl(url) {
        try {
            new URL(url);
            return true;
        } catch {
            return false;
        }
    }

    /**
     * Validate number range
     */
    static isInRange(value, min, max) {
        const num = parseFloat(value);
        return num >= min && num <= max;
    }

    /**
     * Validate date format (YYYY-MM-DD)
     */
    static isValidDate(dateString) {
        const dateRegex = /^\d{4}-\d{2}-\d{2}$/;
        if (!dateRegex.test(dateString)) return false;

        const date = new Date(dateString);
        return date instanceof Date && !isNaN(date);
    }

    /**
     * Validate required field
     */
    static isRequired(value) {
        if (typeof value === 'string') {
            return value.trim().length > 0;
        }
        return value !== null && value !== undefined;
    }

    /**
     * Validate minimum length
     */
    static minLength(value, length) {
        return value && value.toString().length >= length;
    }

    /**
     * Validate maximum length
     */
    static maxLength(value, length) {
        return value && value.toString().length <= length;
    }

    /**
     * Validate exact length
     */
    static exactLength(value, length) {
        return value && value.toString().length === length;
    }

    /**
     * Validate alphanumeric
     */
    static isAlphanumeric(value) {
        const alphanumericRegex = /^[a-zA-Z0-9]+$/;
        return alphanumericRegex.test(value);
    }

    /**
     * Validate only numbers
     */
    static isNumeric(value) {
        return !isNaN(value) && value !== '';
    }

    /**
     * Validate only letters
     */
    static isAlpha(value) {
        const alphaRegex = /^[a-zA-Z\s]+$/;
        return alphaRegex.test(value);
    }

    /**
     * Validate credit card number
     */
    static isValidCreditCard(cardNumber) {
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
     * Validate Indonesian ID number (NIK)
     */
    static isValidNIK(nik) {
        return /^\d{16}$/.test(nik);
    }

    /**
     * Validate Indonesian tax number (NPWP)
     */
    static isValidNPWP(npwp) {
        const cleanNPWP = npwp.replace(/\D/g, '');
        return /^\d{15}$/.test(cleanNPWP);
    }

    /**
     * Validate file type
     */
    static isValidFileType(file, allowedTypes = []) {
        return allowedTypes.includes(file.type);
    }

    /**
     * Validate file size
     */
    static isValidFileSize(file, maxSizeInMB = 5) {
        const maxSizeInBytes = maxSizeInMB * 1024 * 1024;
        return file.size <= maxSizeInBytes;
    }

    /**
     * Validate colors (hex format)
     */
    static isValidHexColor(color) {
        const hexRegex = /^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$/;
        return hexRegex.test(color);
    }

    /**
     * Validate username
     */
    static isValidUsername(username) {
        const usernameRegex = /^[a-zA-Z0-9_]{3,20}$/;
        return usernameRegex.test(username);
    }

    /**
     * Validate slug format
     */
    static isValidSlug(slug) {
        const slugRegex = /^[a-z0-9]+(?:-[a-z0-9]+)*$/;
        return slugRegex.test(slug);
    }
}

/**
 * Form Field Validator - for validating entire forms
 */
class FormValidator {
    constructor(formElement, rules = {}) {
        this.form = formElement;
        this.rules = rules;
        this.errors = {};
    }

    /**
     * Validate entire form
     */
    validate() {
        this.errors = {};
        const formData = new FormData(this.form);

        for (const [fieldName, rule] of Object.entries(this.rules)) {
            const fieldValue = formData.get(fieldName);
            const fieldErrors = this.validateField(fieldName, fieldValue, rule);

            if (fieldErrors.length > 0) {
                this.errors[fieldName] = fieldErrors;
            }
        }

        return Object.keys(this.errors).length === 0;
    }

    /**
     * Validate single field
     */
    validateField(fieldName, value, rules) {
        const errors = [];

        for (const [ruleName, ruleValue] of Object.entries(rules)) {
            if (!this.applyRule(ruleName, value, ruleValue)) {
                errors.push(this.getRuleMessage(ruleName, fieldName, ruleValue));
            }
        }

        return errors;
    }

    /**
     * Apply validation rule
     */
    applyRule(ruleName, value, ruleValue) {
        switch (ruleName) {
            case 'required':
                return ruleValue ? Validator.isRequired(value) : true;
            case 'email':
                return !value || Validator.isValidEmail(value);
            case 'phone':
                return !value || Validator.isValidPhone(value);
            case 'minLength':
                return !value || Validator.minLength(value, ruleValue);
            case 'maxLength':
                return !value || Validator.maxLength(value, ruleValue);
            case 'pattern':
                return !value || new RegExp(ruleValue).test(value);
            case 'match':
                const matchField = this.form.elements[ruleValue];
                return value === matchField.value;
            default:
                return true;
        }
    }

    /**
     * Get error message for rule
     */
    getRuleMessage(ruleName, fieldName, ruleValue) {
        const messages = {
            required: `${fieldName} is required`,
            email: `${fieldName} must be a valid email`,
            phone: `${fieldName} must be a valid phone number`,
            minLength: `${fieldName} must be at least ${ruleValue} characters`,
            maxLength: `${fieldName} must not exceed ${ruleValue} characters`,
            pattern: `${fieldName} format is invalid`,
            match: `${fieldName} does not match`
        };

        return messages[ruleName] || `${fieldName} is invalid`;
    }

    /**
     * Get all errors
     */
    getErrors() {
        return this.errors;
    }

    /**
     * Get errors for specific field
     */
    getFieldErrors(fieldName) {
        return this.errors[fieldName] || [];
    }

    /**
     * Check if field has errors
     */
    hasFieldError(fieldName) {
        return this.errors[fieldName] && this.errors[fieldName].length > 0;
    }

    /**
     * Display errors on form
     */
    displayErrors() {
        // Remove previous error messages
        this.form.querySelectorAll('.form-error').forEach(el => el.remove());

        for (const [fieldName, fieldErrors] of Object.entries(this.errors)) {
            const field = this.form.elements[fieldName];
            if (field) {
                field.classList.add('error');

                const errorDiv = document.createElement('div');
                errorDiv.className = 'form-error';
                errorDiv.textContent = fieldErrors[0];

                field.parentNode.appendChild(errorDiv);
            }
        }
    }

    /**
     * Clear errors from form
     */
    clearErrors() {
        this.form.querySelectorAll('.form-error').forEach(el => el.remove());
        this.form.querySelectorAll('.error').forEach(el => el.classList.remove('error'));
        this.errors = {};
    }
}

// Export for use
window.Validator = Validator;
window.FormValidator = FormValidator;
