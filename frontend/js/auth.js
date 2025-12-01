const Auth = {
    async login(email, password) {
        try {
            const response = await API.post('/auth/login', {
                email: email,
                contrasena: password
            });

            if (response && response.token) {
                localStorage.setItem('token', response.token);
                localStorage.setItem('user', JSON.stringify(response.usuario));
                return true;
            }
            return false;
        } catch (error) {
            alert('Error al iniciar sesión: ' + error.message);
            return false;
        }
    },

    logout() {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        window.location.href = 'index.html';
    },

    getCurrentUser() {
        const userStr = localStorage.getItem('user');
        return userStr ? JSON.parse(userStr) : null;
    },

    isAuthenticated() {
        return !!localStorage.getItem('token');
    }
};

// Manejo del formulario de login
document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;
            const btn = loginForm.querySelector('button');

            const originalText = btn.innerText;
            btn.innerText = 'Ingresando...';
            btn.disabled = true;

            const success = await Auth.login(email, password);

            if (success) {
                window.location.href = 'dashboard.html';
            } else {
                btn.innerText = originalText;
                btn.disabled = false;
            }
        });
    }
});
