document.addEventListener('DOMContentLoaded', () => {
    if (!Auth.isAuthenticated()) {
        window.location.href = 'index.html';
        return;
    }

    const user = Auth.getCurrentUser();
    document.getElementById('userName').textContent = user ? user.nombre : 'Usuario';

    document.getElementById('logoutBtn').addEventListener('click', (e) => {
        e.preventDefault();
        Auth.logout();
    });

    loadDashboardData();
});

async function loadDashboardData() {
    try {
        // Cargar proyectos
        const proyectos = await API.get('/proyecto?activo=true');
        renderProyectos(proyectos);

        // Cargar tareas (ejemplo)
        // const tareas = await API.get('/tarea');
        // updateStats(tareas);
    } catch (error) {
        console.error('Error cargando datos:', error);
    }
}

function renderProyectos(proyectos) {
    const container = document.getElementById('projectsContainer');
    if (!proyectos || proyectos.length === 0) {
        container.innerHTML = '<p>No hay proyectos activos.</p>';
        return;
    }

    container.innerHTML = proyectos.map(p => `
        <div class="project-card">
            <h3>${p.nombre}</h3>
            <p>${p.descripcion || 'Sin descripción'}</p>
            <div style="margin-top: 10px; font-size: 0.85rem; color: #666;">
                Responsable: ${p.responsable ? p.responsable.nombre : 'N/A'}
            </div>
        </div>
    `).join('');
}
