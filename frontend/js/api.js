const API = {
    async request(endpoint, method = 'GET', body = null) {
        const headers = {
            'Content-Type': 'application/json'
        };

        const token = localStorage.getItem('token');
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        const config = {
            method,
            headers
        };

        if (body) {
            config.body = JSON.stringify(body);
        }

        try {
            const response = await fetch(`${CONFIG.API_URL}${endpoint}`, config);

            if (response.status === 401) {
                // Token expirado o inválido
                localStorage.removeItem('token');
                localStorage.removeItem('user');
                if (!window.location.pathname.endsWith('index.html')) {
                    window.location.href = 'index.html';
                }
                throw new Error('Sesión expirada');
            }

            if (!response.ok) {
                const errorData = await response.text();
                throw new Error(errorData || 'Error en la petición');
            }

            // Si la respuesta no tiene contenido (ej. 204 No Content), retornar null
            if (response.status === 204) return null;

            // Intentar parsear JSON, si falla devolver texto
            const text = await response.text();
            try {
                return JSON.parse(text);
            } catch {
                return text;
            }
        } catch (error) {
            console.error('API Error:', error);
            throw error;
        }
    },

    get(endpoint) {
        return this.request(endpoint, 'GET');
    },

    post(endpoint, body) {
        return this.request(endpoint, 'POST', body);
    },

    put(endpoint, body) {
        return this.request(endpoint, 'PUT', body);
    },

    delete(endpoint) {
        return this.request(endpoint, 'DELETE');
    },

    patch(endpoint, body) {
        return this.request(endpoint, 'PATCH', body);
    }
};
