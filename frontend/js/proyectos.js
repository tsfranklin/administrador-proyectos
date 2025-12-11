// Código JavaScript para la página de proyectos

document.addEventListener('DOMContentLoaded', () => {
    checkAuth();
    loadUserProfile();
    loadProyectosPage();
    setupEventListeners();
});

async function loadProyectosPage() {
    const projectsList = document.getElementById('projectsList');
    if (!projectsList) return;

    try {
        const token = localStorage.getItem('token');
        const user = JSON.parse(localStorage.getItem('user'));

        const response = await fetch(`${API_URL}/proyecto`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (response.ok) {
            let proyectos = await response.json();

            // Filtrar según el rol
            if (user.rol && user.rol.nombre === 'GESTOR_PROYECTO') {
                proyectos = proyectos.filter(p =>
                    p.responsable && p.responsable.idUsuario === user.idUsuario
                );
            } else if (user.rol && (user.rol.nombre === 'TRABAJADOR' || user.rol.nombre === 'COLABORADOR')) {
                const tareasResponse = await fetch(`${API_URL}/tarea`, {
                    headers: { 'Authorization': `Bearer ${token}` }
                });

                if (tareasResponse.ok) {
                    const todasLasTareas = await tareasResponse.json();
                    const misTareas = todasLasTareas.filter(tarea =>
                        tarea.asignado && tarea.asignado.idUsuario === user.idUsuario
                    );
                    const proyectosIds = [...new Set(misTareas.map(tarea => tarea.proyecto.idProyecto))];
                    proyectos = proyectos.filter(p => proyectosIds.includes(p.idProyecto));
                }
            }

            projectsList.innerHTML = '';

            if (proyectos.length === 0) {
                projectsList.innerHTML = '<p>No hay proyectos disponibles.</p>';
                return;
            }

            const esAdmin = user.rol && user.rol.nombre === 'ADMINISTRADOR';

            proyectos.forEach(proyecto => {
                const card = document.createElement('div');
                card.className = 'card';

                card.innerHTML = `
                    <div style="display: flex; justify-content: space-between; align-items: start; margin-bottom: 15px;">
                        <h3 style="font-size: 1.2rem; cursor: pointer;" onclick="window.location.href='kanban.html?project=${proyecto.idProyecto}'">${proyecto.nombre}</h3>
                        <div style="display: flex; gap: 8px; align-items: center;">
                            <span class="tag ${proyecto.activo ? 'tag-low' : 'tag-high'}">${proyecto.activo ? 'Activo' : 'Inactivo'}</span>
                            ${esAdmin ? `
                                <button onclick="event.stopPropagation(); showEditProjectModal(${proyecto.idProyecto})" style="border: none; background: none; color: #2196F3; cursor: pointer;" title="Editar Proyecto">
                                    <i class="fas fa-edit"></i>
                                </button>
                                <button onclick="event.stopPropagation(); deleteProject(${proyecto.idProyecto})" style="border: none; background: none; color: #ff4444; cursor: pointer;" title="Eliminar Proyecto">
                                    <i class="fas fa-trash"></i>
                                </button>
                            ` : ''}
                        </div>
                    </div>
                    <p style="color: #666; font-size: 0.9rem; margin-bottom: 15px; cursor: pointer;" onclick="window.location.href='kanban.html?project=${proyecto.idProyecto}'">${proyecto.descripcion || 'Sin descripción'}</p>
                    <div style="font-size: 0.85rem; color: #888; margin-bottom: 15px;">
                        <p><i class="far fa-calendar"></i> Inicio: ${new Date(proyecto.fechaInicio).toLocaleDateString()}</p>
                        ${proyecto.fechaFin ? `<p><i class="far fa-calendar-check"></i> Fin: ${new Date(proyecto.fechaFin).toLocaleDateString()}</p>` : ''}
                        <p><i class="far fa-user"></i> Responsable: ${proyecto.responsable.nombre}</p>
                        <p style="margin-top: 10px; font-weight: bold; color: var(--primary-color);">📋 Código: ${proyecto.codigoRegistro || 'N/A'}</p>
                    </div>
                    <button onclick="window.location.href='kanban.html?project=${proyecto.idProyecto}'" class="btn btn-secondary" style="width: 100%;">Ver Tareas</button>
                `;
                projectsList.appendChild(card);
            });
        }
    } catch (error) {
        console.error('Error:', error);
        projectsList.innerHTML = '<p>Error al cargar proyectos.</p>';
    }
}

async function deleteProject(id) {
    if (!confirm('¿Estás seguro de eliminar este proyecto? Se eliminarán todas sus tareas.')) return;

    try {
        const token = localStorage.getItem('token');
        const response = await fetch(`${API_URL}/proyecto/${id}`, {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (response.ok || response.status === 204) {
            alert('✅ Proyecto eliminado correctamente');
            loadProyectosPage();
        } else {
            alert('❌ Error al eliminar proyecto');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('❌ Error de conexión');
    }
}

async function showEditProjectModal(id) {
    try {
        const token = localStorage.getItem('token');
        const response = await fetch(`${API_URL}/proyecto/${id}`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (response.ok) {
            const proyecto = await response.json();

            document.getElementById('editProjId').value = proyecto.idProyecto;
            document.getElementById('editProjNombre').value = proyecto.nombre;
            document.getElementById('editProjDesc').value = proyecto.descripcion || '';

            if (proyecto.fechaFin) {
                const fecha = new Date(proyecto.fechaFin);
                const fechaFormateada = fecha.toISOString().slice(0, 16);
                document.getElementById('editProjFechaFin').value = fechaFormateada;
            }

            document.getElementById('editProjectModal').style.display = 'block';
        } else {
            alert('❌ Error al cargar proyecto');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('❌ Error de conexión');
    }
}

function showCreateProjectModal() {
    document.getElementById('createProjectModal').style.display = 'block';
}

function closeCreateProjectModal() {
    document.getElementById('createProjectModal').style.display = 'none';
    document.getElementById('createProjectForm').reset();
}

function closeEditProjectModal() {
    document.getElementById('editProjectModal').style.display = 'none';
    document.getElementById('editProjectForm').reset();
}

function setupEventListeners() {
    // FORMULARIO CREAR PROYECTO
    const createProjectForm = document.getElementById('createProjectForm');
    if (createProjectForm) {
        createProjectForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const nombre = document.getElementById('projNombre').value;
            const descripcion = document.getElementById('projDesc').value;
            const fechaFin = document.getElementById('projFechaFin').value;
            const user = JSON.parse(localStorage.getItem('user'));

            try {
                const token = localStorage.getItem('token');
                const response = await fetch(`${API_URL}/proyecto`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
                    },
                    body: JSON.stringify({
                        nombre,
                        descripcion,
                        fechaFin: fechaFin || null,
                        idResponsable: user.idUsuario,
                        activo: true
                    })
                });

                if (response.ok) {
                    const proyecto = await response.json();
                    alert(`✅ Proyecto creado con éxito!\n\n📋 Código del proyecto: ${proyecto.codigoRegistro}\n\nComparte este código con los gestores para que puedan registrarse.`);
                    closeCreateProjectModal();
                    loadProyectosPage();
                } else {
                    const error = await response.text();
                    alert('❌ Error al crear proyecto: ' + error);
                }
            } catch (error) {
                console.error('Error:', error);
                alert('❌ Error de conexión con el servidor');
            }
        });
    }

    // FORMULARIO EDITAR PROYECTO
    const editProjectForm = document.getElementById('editProjectForm');
    if (editProjectForm) {
        editProjectForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const id = document.getElementById('editProjId').value;
            const nombre = document.getElementById('editProjNombre').value;
            const descripcion = document.getElementById('editProjDesc').value;
            const fechaFin = document.getElementById('editProjFechaFin').value;

            try {
                const token = localStorage.getItem('token');
                const response = await fetch(`${API_URL}/proyecto/${id}`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
                    },
                    body: JSON.stringify({
                        nombre,
                        descripcion,
                        fechaFin: fechaFin || null,
                        activo: true
                    })
                });

                if (response.ok) {
                    alert('✅ Proyecto actualizado correctamente');
                    closeEditProjectModal();
                    loadProyectosPage();
                } else {
                    const error = await response.text();
                    alert('❌ Error al actualizar proyecto: ' + error);
                }
            } catch (error) {
                console.error('Error:', error);
                alert('❌ Error de conexión');
            }
        });
    }
}
