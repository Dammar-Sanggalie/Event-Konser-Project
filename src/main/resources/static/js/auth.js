window.auth = {
    /**
     * Menyimpan data user ke localStorage
     * @param {object} user - Objek user dari backend
     */
    saveUser: (user) => {
        localStorage.setItem('currentUser', JSON.stringify(user));
    },

    /**
     * Mengambil data user dari localStorage
     * @returns {object|null} - Objek user atau null
     */
    getUser: () => {
        const user = localStorage.getItem('currentUser');
        return user ? JSON.parse(user) : null;
    },

    /**
     * Menghapus data user dari localStorage (logout)
     */
    removeUser: () => {
        localStorage.removeItem('currentUser');
    },

    /**
     * Cek apakah user sudah login
     * @returns {boolean}
     */
    isAuthenticated: () => {
        return localStorage.getItem('currentUser') !== null;
    },

    /**
     * Mendapatkan ID user yang sedang login
     * @returns {number|null}
     */
    getUserId: () => {
        const user = window.auth.getUser();
        return user ? user.idPengguna : null;
    },

    /**
     * Mendapatkan nama user
     * @returns {string|null}
     */
    getUserName: () => {
        const user = window.auth.getUser();
        return user ? user.nama : 'User';
    },

    /**
     * Menyimpan token JWT ke localStorage
     * @param {string} token - JWT token dari backend
     */
    setToken: (token) => {
        localStorage.setItem('authToken', token);
    },

    /**
     * Mengambil token JWT dari localStorage
     * @returns {string|null} - JWT token atau null
     */
    getToken: () => {
        return localStorage.getItem('authToken');
    },

    /**
     * Cek apakah user adalah admin
     * @returns {boolean}
     */
    isAdmin: () => {
        const user = window.auth.getUser();
        return user && user.role === 'ADMIN';
    }
};

// ==========================================
// FUNGSI HANDLE SUBMIT (FINAL FIX)
// ==========================================

/**
 * Fungsi untuk menangani submit form login
 */
async function handleLoginSubmit(event) {
    event.preventDefault(); // Mencegah reload halaman
    
    const form = event.target;
    const emailInput = form.elements.email;
    const passwordInput = form.elements.password;
    const button = document.getElementById('login-button');
    const buttonText = button.querySelector('.button-text');
    const loader = button.querySelector('.loader');
    
    // Reset error messages
    document.getElementById('email-error').classList.add('hidden');
    document.getElementById('password-error').classList.add('hidden');
    emailInput.classList.remove('border-red-500');
    passwordInput.classList.remove('border-red-500');

    // Validasi sederhana
    if (!emailInput.value) {
        document.getElementById('email-error').textContent = 'Email is required';
        document.getElementById('email-error').classList.remove('hidden');
        emailInput.classList.add('border-red-500');
        return;
    }
     if (!passwordInput.value) {
        document.getElementById('password-error').textContent = 'Password is required';
        document.getElementById('password-error').classList.remove('hidden');
        passwordInput.classList.add('border-red-500');
        return;
    }
    
    // Tampilkan loader, disable tombol
    button.disabled = true;
    buttonText.textContent = 'Logging in...';
    loader.classList.remove('hidden');

    try {
        // PERBAIKAN FINAL: Hapus '/api' di depan karena apiFetch sudah menambahkannya
        // Jadi pathnya cukup '/auth/login'
        const response = await apiFetch('/auth/login', {
            method: 'POST',
            body: JSON.stringify({
                // PERBAIKAN: Key sesuai LoginRequest.java
                emailOrUsername: emailInput.value, 
                password: passwordInput.value
            })
        });

        if (response && response.token) {
            window.auth.setToken(response.token);
            window.auth.saveUser({
                idPengguna: response.userId,
                nama: response.nama,
                email: response.email,
                role: response.role
            });
            
            showToast('Login berhasil! Mengalihkan...', 'success');
            setTimeout(() => {
                window.location.href = '/';
            }, 1500); 
        } else {
            throw new Error('Invalid login response');
        }
        
    } catch (error) {
        console.error('Login failed:', error);
        
        const errorMessage = error.message || 'Email atau Password salah';
        document.getElementById('email-error').textContent = errorMessage;
        document.getElementById('email-error').classList.remove('hidden');
        
        button.disabled = false;
        buttonText.textContent = 'Login';
        loader.classList.add('hidden');
    } 
}

/**
 * Fungsi untuk menangani submit form register
 */
async function handleRegisterSubmit(event) {
    event.preventDefault();
    
    const form = event.target;
    const nameInput = form.elements.name;
    const phoneInput = form.elements.phone;
    const emailInput = form.elements.email;
    const passwordInput = form.elements.password;
    const confirmPasswordInput = form.elements.confirmPassword;
    const addressInput = form.elements.address;
    const termsCheckbox = form.elements.terms;
    
    const button = document.getElementById('register-button');
    const buttonText = button.querySelector('.button-text');
    const loader = button.querySelector('.loader');

    // Reset errors
    document.querySelectorAll('[id$="-error"]').forEach(el => el.classList.add('hidden'));
    form.querySelectorAll('input, textarea').forEach(el => el.classList.remove('border-red-500'));

    // Validasi Frontend
    let isValid = true;
    if (!nameInput.value) {
        document.getElementById('name-error').textContent = 'Name is required';
        document.getElementById('name-error').classList.remove('hidden');
        nameInput.classList.add('border-red-500');
        isValid = false;
    }
    if (!phoneInput.value) {
        document.getElementById('phone-error').textContent = 'Phone is required';
        document.getElementById('phone-error').classList.remove('hidden');
        phoneInput.classList.add('border-red-500');
        isValid = false;
    }
    if (!emailInput.value) {
        document.getElementById('email-error').textContent = 'Email is required';
        document.getElementById('email-error').classList.remove('hidden');
        emailInput.classList.add('border-red-500');
        isValid = false;
    } else if (!/^\S+@\S+\.\S+$/.test(emailInput.value)) {
        document.getElementById('email-error').textContent = 'Invalid email format';
        document.getElementById('email-error').classList.remove('hidden');
        emailInput.classList.add('border-red-500');
        isValid = false;
    }
    if (!passwordInput.value) {
        document.getElementById('password-error').textContent = 'Password is required';
        document.getElementById('password-error').classList.remove('hidden');
        passwordInput.classList.add('border-red-500');
        isValid = false;
    } else if (passwordInput.value.length < 6) {
        document.getElementById('password-error').textContent = 'Password must be at least 6 characters';
        document.getElementById('password-error').classList.remove('hidden');
        passwordInput.classList.add('border-red-500');
        isValid = false;
    }
     if (passwordInput.value !== confirmPasswordInput.value) {
        document.getElementById('confirmPassword-error').textContent = 'Passwords do not match';
        document.getElementById('confirmPassword-error').classList.remove('hidden');
        confirmPasswordInput.classList.add('border-red-500');
        isValid = false;
    }
    if (!termsCheckbox.checked) {
         document.getElementById('terms-error').textContent = 'You must agree to the terms';
        document.getElementById('terms-error').classList.remove('hidden');
        isValid = false;
    }

    if (!isValid) return;

    button.disabled = true;
    buttonText.textContent = 'Creating Account...';
    loader.classList.remove('hidden');

    try {
        const userData = {
            nama: nameInput.value,
            email: emailInput.value,
            // PERBAIKAN: Kirim username (diisi email)
            username: emailInput.value, 
            password: passwordInput.value,
            // PERBAIKAN: Kirim confirmPassword
            confirmPassword: confirmPasswordInput.value, 
            noTelepon: phoneInput.value,
            alamat: addressInput.value
        };

        // PERBAIKAN FINAL: Hapus '/api' di depan
        const response = await apiFetch('/auth/register', {
            method: 'POST',
            body: JSON.stringify(userData)
        });

        if (response && response.token) {
            window.auth.setToken(response.token);
            window.auth.saveUser({
                idPengguna: response.userId,
                nama: response.nama,
                email: response.email,
                role: response.role
            });
            
            showToast('Registrasi berhasil! Anda sudah login.', 'success');
            setTimeout(() => {
                window.location.href = '/';
            }, 1500);
        } else {
            showToast('Registrasi berhasil! Silakan login.', 'success');
            setTimeout(() => {
                window.location.href = '/login.html';
            }, 1500); 
        }

    } catch (error) {
        button.disabled = false;
        buttonText.textContent = 'Create Account';
        loader.classList.add('hidden');
        
        if (error.message && error.message.includes('Email')) {
            document.getElementById('email-error').textContent = error.message;
            document.getElementById('email-error').classList.remove('hidden');
            emailInput.classList.add('border-red-500');
        } else {
            showToast('Registrasi Gagal: ' + error.message, 'error');
        }
    }
}

/**
 * Show toast notification
 */
function showToast(message, type = 'info') {
    let toastContainer = document.getElementById('auth-toast-container');
    if (!toastContainer) {
        toastContainer = document.createElement('div');
        toastContainer.id = 'auth-toast-container';
        toastContainer.style.cssText = 'position: fixed; top: 20px; right: 20px; z-index: 9999; max-width: 400px;';
        document.body.appendChild(toastContainer);
    }
    
    const toast = document.createElement('div');
    let borderColor = '#3b82f6';
    let icon = 'ℹ';
    
    if (type === 'success') {
        icon = '✓';
        borderColor = '#10b981';
    } else if (type === 'error') {
        icon = '✕';
        borderColor = '#ef4444';
    } else if (type === 'warning') {
        icon = '⚠';
        borderColor = '#f59e0b';
    }
    
    toast.style.cssText = `
        background: white;
        padding: 16px;
        border-radius: 8px;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
        margin-bottom: 10px;
        display: flex;
        align-items: center;
        gap: 12px;
        animation: slideInRight 0.3s ease-out;
        min-width: 300px;
        border-left: 4px solid ${borderColor};
    `;
    
    toast.innerHTML = `
        <div style="font-size: 18px; font-weight: bold; color: ${borderColor};">${icon}</div>
        <div style="flex: 1;">
            <p style="margin: 0; color: #111; font-size: 14px; font-weight: 500;">${message}</p>
        </div>
    `;
    
    toastContainer.appendChild(toast);
    
    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transition = 'opacity 0.3s ease-out';
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

/**
 * Fungsi untuk toggle show/hide password
 */
function setupPasswordToggle(inputId, buttonId, eyeIconId, eyeOffIconId) {
    const passwordInput = document.getElementById(inputId);
    const toggleButton = document.getElementById(buttonId);
    const eyeIcon = document.getElementById(eyeIconId);
    const eyeOffIcon = document.getElementById(eyeOffIconId);

    if (passwordInput && toggleButton && eyeIcon && eyeOffIcon) {
        toggleButton.addEventListener('click', () => {
            const isPassword = passwordInput.type === 'password';
            passwordInput.type = isPassword ? 'text' : 'password';
            eyeIcon.classList.toggle('hidden', isPassword);
            eyeOffIcon.classList.toggle('hidden', !isPassword);
        });
    }
}


// Event Listener
if (typeof document !== 'undefined') {
    const loginFormElement = document.getElementById('login-form');
    if (loginFormElement) {
        loginFormElement.addEventListener('submit', handleLoginSubmit);
        setupPasswordToggle('password', 'toggle-password', 'eye-icon', 'eye-off-icon');
    }

    const registerFormElement = document.getElementById('register-form');
    if (registerFormElement) {
        registerFormElement.addEventListener('submit', handleRegisterSubmit);
        setupPasswordToggle('password', 'toggle-password', 'eye-icon-pw', 'eye-off-icon-pw');
        setupPasswordToggle('confirmPassword', 'toggle-confirm-password', 'eye-icon-cpw', 'eye-off-icon-cpw');
    }
}