const API_URL = 'http://localhost:8080/api/v1';

// Manejo de Login
const loginForm = document.getElementById('loginForm');
if (loginForm) {
    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;

        try {
            const response = await fetch(`${API_URL}/auth/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ email, contrasena: password })
            });

            if (response.ok) {
                const data = await response.json();
                localStorage.setItem('token', data.token);
                localStorage.setItem('user', JSON.stringify(data.usuario));
                window.location.href = 'dashboard.html';
            } else {
                const error = await response.text();
                alert('Error al iniciar sesión: ' + error);
            }
        } catch (error) {
            console.error('Error:', error);
            alert('Error de conexión con el servidor');
        }
    });
}

// Manejo de Registro
const registerForm = document.getElementById('registerForm');
if (registerForm) {
    registerForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const nombre = document.getElementById('nombre').value;
        const email = document.getElementById('email').value;
        const telefono = document.getElementById('telefono').value;
        const password = document.getElementById('password').value;
        const idRol = document.getElementById('rol').value;
        const codigo = document.getElementById('codigo').value;

        const payload = {
            nombre,
            email,
            telefono,
            contrasena: password,
            idRol: parseInt(idRol),
            codigo: codigo || null
        };

        try {
            const response = await fetch(`${API_URL}/auth/register`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(payload)
            });

            if (response.ok) {
                alert('Registro exitoso. Ahora puedes iniciar sesión.');
                window.location.href = 'index.html';
            } else {
                const error = await response.text();
                alert('Error en el registro: ' + error);
            }
        } catch (error) {
            console.error('Error:', error);
            alert('Error de conexión con el servidor');
        }
    });
}

// Verificar autenticación en páginas protegidas
function checkAuth() {
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = 'index.html';
    }
    return token;
}

// Cerrar sesión
function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    window.location.href = 'index.html';
}

// Cargar usuario en el header
function loadUserProfile() {
    const userStr = localStorage.getItem('user');
    if (userStr) {
        const user = JSON.parse(userStr);
        const userNameElement = document.getElementById('userName');
        const userRoleElement = document.getElementById('userRole');
        const headerUserName = document.getElementById('headerUserName');

        if (userNameElement) userNameElement.innerText = user.nombre;
        if (userRoleElement) userRoleElement.innerText = user.rol ? user.rol.nombre : '';
        if (headerUserName) headerUserName.innerText = user.nombre;

        // Mostrar botones según rol
        const btnCrearProyecto = document.getElementById('btnCrearProyecto');
        if (btnCrearProyecto && user.rol && user.rol.nombre === 'ADMINISTRADOR') {
            btnCrearProyecto.style.display = 'block';
        }

        const btnCrearTarea = document.getElementById('btnCrearTarea');
        if (btnCrearTarea && user.rol && (user.rol.nombre === 'ADMINISTRADOR' || user.rol.nombre === 'GESTOR_PROYECTO' || user.rol.nombre === 'TRABAJADOR')) {
            btnCrearTarea.style.display = 'block';
        }
    }
}


// Cargar Tablero Kanban
async function loadKanbanBoard() {
    const columns = {
        'PENDIENTE': document.getElementById('col-PENDIENTE'),
        'EN_PROGRESO': document.getElementById('col-EN_PROGRESO'),
        'BLOQUEADA': document.getElementById('col-BLOQUEADA'),
        'COMPLETADA': document.getElementById('col-COMPLETADA')
    };

    if (!columns['PENDIENTE']) return;

    try {
        const token = localStorage.getItem('token');
        const user = JSON.parse(localStorage.getItem('user'));

        // Verificar si hay un proyecto específico en la URL
        const urlParams = new URLSearchParams(window.location.search);
        const projectIdFromUrl = urlParams.get('project');

        // Obtener todas las tareas
        const response = await fetch(`${API_URL}/tarea`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (response.ok) {
            let tareas = await response.json();

            // FILTRO POR PROYECTO ESPECÍFICO (tiene prioridad sobre filtros de rol)
            if (projectIdFromUrl) {
                const projectId = parseInt(projectIdFromUrl);
                tareas = tareas.filter(t =>
                    t.proyecto && t.proyecto.idProyecto === projectId
                );

                // Actualizar título con el nombre del proyecto
                const projectTitle = document.getElementById('projectTitle');
                if (projectTitle && tareas.length > 0) {
                    projectTitle.innerText = `Tablero de Tareas - ${tareas[0].proyecto.nombre}`;
                } else if (projectTitle) {
                    // Si no hay tareas, obtener el nombre del proyecto del backend
                    try {
                        const proyResponse = await fetch(`${API_URL}/proyecto/${projectId}`, {
                            headers: { 'Authorization': `Bearer ${token}` }
                        });
                        if (proyResponse.ok) {
                            const proyecto = await proyResponse.json();
                            projectTitle.innerText = `Tablero de Tareas - ${proyecto.nombre}`;
                        }
                    } catch (e) {
                        projectTitle.innerText = `Tablero de Tareas - Proyecto ${projectId}`;
                    }
                }
            } else {
                // CONTROL DE ACCESO POR ROL (solo si no hay proyecto en URL)
                if (user.rol && user.rol.nombre === 'GESTOR_PROYECTO') {
                    // Los gestores solo ven tareas de SU proyecto
                    const proyectosResponse = await fetch(`${API_URL}/proyecto`, {
                        headers: { 'Authorization': `Bearer ${token}` }
                    });

                    if (proyectosResponse.ok) {
                        const proyectos = await proyectosResponse.json();
                        const misProyectos = proyectos.filter(p =>
                            p.responsable && p.responsable.idUsuario === user.idUsuario
                        );
                        const proyectosIds = misProyectos.map(p => p.idProyecto);

                        tareas = tareas.filter(t =>
                            t.proyecto && proyectosIds.includes(t.proyecto.idProyecto)
                        );

                        const projectTitle = document.getElementById('projectTitle');
                        if (projectTitle && misProyectos.length === 1) {
                            projectTitle.innerText = `Tablero de Tareas - ${misProyectos[0].nombre}`;
                        } else if (projectTitle) {
                            projectTitle.innerText = `Tablero de Tareas - Mis Proyectos`;
                        }
                    }
                } else if (user.rol && (user.rol.nombre === 'TRABAJADOR' || user.rol.nombre === 'COLABORADOR')) {
                    // Los trabajadores/colaboradores solo ven SUS tareas asignadas
                    tareas = tareas.filter(t =>
                        t.asignado && t.asignado.idUsuario === user.idUsuario
                    );

                    const projectTitle = document.getElementById('projectTitle');
                    if (projectTitle) {
                        projectTitle.innerText = `Mis Tareas Asignadas`;
                    }
                }
                // El ADMINISTRADOR ve todas las tareas (no se filtra)
            }

            // Renderizar tareas
            Object.values(columns).forEach(col => col.innerHTML = '');
            const counts = { 'PENDIENTE': 0, 'EN_PROGRESO': 0, 'BLOQUEADA': 0, 'COMPLETADA': 0 };

            tareas.forEach(tarea => {
                const col = columns[tarea.estado];
                if (col) {
                    counts[tarea.estado]++;
                    const card = document.createElement('div');
                    card.className = 'task-card';
                    card.draggable = true;
                    card.ondragstart = (e) => drag(e, tarea.idTarea);

                    const prioridadClass = tarea.prioridad === 'ALTA' ? 'tag-high' : (tarea.prioridad === 'MEDIA' ? 'tag-medium' : 'tag-low');

                    card.onclick = (e) => {
                        if (e.target.tagName !== 'BUTTON' && e.target.tagName !== 'I') {
                            openTaskDetails(tarea.idTarea);
                        }
                    };

                    card.innerHTML = `
                        <div class="task-tags">
                            <span class="tag ${prioridadClass}">${tarea.prioridad}</span>
                            <span class="tag" style="background: #eee;">${tarea.codigoTarea || 'N/A'}</span>
                            <button onclick="event.stopPropagation(); showEditTaskModal(${tarea.idTarea})" style="border: none; background: none; color: #2196F3; cursor: pointer; margin-left: auto;" title="Editar Tarea">
                                <i class="fas fa-edit"></i>
                            </button>
                            <button onclick="event.stopPropagation(); deleteTask(${tarea.idTarea})" style="border: none; background: none; color: #ff4444; cursor: pointer;" title="Eliminar Tarea">
                                <i class="fas fa-trash"></i>
                            </button>
                        </div>
                        <h4 style="margin-bottom: 8px;">${tarea.titulo}</h4>
                        <p style="font-size: 0.85rem; color: #666; margin-bottom: 10px;">${tarea.descripcion || ''}</p>
                        <div style="display: flex; justify-content: space-between; align-items: center; font-size: 0.8rem; color: #888;">
                            <span><i class="far fa-clock"></i> ${new Date(tarea.fechaLimite).toLocaleDateString()}</span>
                            ${tarea.asignado ? `<span title="${tarea.asignado.nombre}" style="background: #333; color: white; width: 24px; height: 24px; border-radius: 50%; display: flex; align-items: center; justify-content: center;">${tarea.asignado.nombre.charAt(0)}</span>` : ''}
                        </div>
                        <div style="margin-top: 10px; display: flex; gap: 5px;">
                            ${tarea.estado !== 'PENDIENTE' ? `<button onclick="moverTarea(${tarea.idTarea}, 'PENDIENTE')" style="font-size: 0.7rem; z-index: 2;">⏮</button>` : ''}
                            ${tarea.estado !== 'EN_PROGRESO' ? `<button onclick="moverTarea(${tarea.idTarea}, 'EN_PROGRESO')" style="font-size: 0.7rem; z-index: 2;">▶</button>` : ''}
                            ${tarea.estado !== 'COMPLETADA' ? `<button onclick="moverTarea(${tarea.idTarea}, 'COMPLETADA')" style="font-size: 0.7rem; z-index: 2;">✅</button>` : ''}
                        </div>
                    `;
                    col.appendChild(card);
                }
            });

            Object.keys(counts).forEach(key => {
                const badge = document.getElementById(`count-${key}`);
                if (badge) badge.innerText = counts[key];
            });
        }
    } catch (error) {
        console.error('Error cargando tareas:', error);
    }
}

// Eliminar Tarea
async function deleteTask(id) {
    if (!confirm('¿Estás seguro de eliminar esta tarea?')) return;

    try {
        const token = localStorage.getItem('token');
        const response = await fetch(`${API_URL}/tarea/${id}`, {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (response.ok || response.status === 204) {
            loadKanbanBoard();
        } else {
            alert('Error al eliminar tarea');
        }
    } catch (error) {
        console.error('Error:', error);
    }
}

// Eliminar Proyecto
async function deleteProject(id) {
    if (!confirm('¿Estás seguro de eliminar este proyecto? Se eliminarán todas sus tareas.')) return;

    try {
        const token = localStorage.getItem('token');
        const response = await fetch(`${API_URL}/proyecto/${id}`, {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (response.ok || response.status === 204) {
            loadDashboardData();
        } else {
            alert('Error al eliminar proyecto');
        }
    } catch (error) {
        console.error('Error:', error);
    }
}

// Abrir detalles de tarea
async function openTaskDetails(id) {
    const modal = document.getElementById('taskDetailsModal');
    if (!modal) return;

    try {
        const token = localStorage.getItem('token');
        const response = await fetch(`${API_URL}/tarea/${id}`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (response.ok) {
            const tarea = await response.json();
            document.getElementById('detailTitulo').innerText = tarea.titulo;
            document.getElementById('detailDescripcion').innerText = tarea.descripcion;
            document.getElementById('detailEstado').innerText = tarea.estado;
            document.getElementById('detailPrioridad').innerText = tarea.prioridad;
            modal.dataset.taskId = id;

            const commentsList = document.getElementById('commentsList');
            commentsList.innerHTML = '<p style="color: #999; font-style: italic;">No hay comentarios aún.</p>';
            modal.style.display = 'flex';
        }
    } catch (error) {
        console.error('Error cargando detalles:', error);
    }
}

// Mover tarea
async function moverTarea(id, nuevoEstado) {
    try {
        const token = localStorage.getItem('token');
        const response = await fetch(`${API_URL}/tarea/${id}/mover-estado?nuevoEstado=${nuevoEstado}`, {
            method: 'PATCH',
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (response.ok) {
            loadKanbanBoard();
        } else {
            alert('Error al mover la tarea');
        }
    } catch (error) {
        console.error('Error:', error);
    }
}

// Drag & Drop
function drag(ev, id) {
    ev.dataTransfer.setData("text", id);
}

// Sistema de Fichaje
let cronometroInterval;
let fichajeActivo = null;

async function toggleFichaje() {
    const btn = document.getElementById('btnFichar');
    const cronometroDisplay = document.getElementById('cronometro');
    const user = JSON.parse(localStorage.getItem('user'));
    const token = localStorage.getItem('token');

    if (!fichajeActivo) {
        try {
            const response = await fetch(`${API_URL}/fichaje/entrada?usuarioId=${user.idUsuario}`, {
                method: 'POST',
                headers: { 'Authorization': `Bearer ${token}` }
            });

            if (response.ok) {
                fichajeActivo = await response.json();
                btn.innerHTML = '<i class="fas fa-stop"></i> Salida';
                btn.style.backgroundColor = 'var(--danger-color)';

                const entrada = new Date(fichajeActivo.fechaHoraEntrada);
                cronometroInterval = setInterval(() => {
                    const ahora = new Date();
                    const diff = Math.floor((ahora - entrada) / 1000);
                    const hrs = Math.floor(diff / 3600).toString().padStart(2, '0');
                    const mins = Math.floor((diff % 3600) / 60).toString().padStart(2, '0');
                    const secs = (diff % 60).toString().padStart(2, '0');
                    cronometroDisplay.innerText = `${hrs}:${mins}:${secs}`;
                }, 1000);
            } else {
                const error = await response.text();
                alert('❌ Error al registrar entrada: ' + error);
            }
        } catch (error) {
            console.error('Error:', error);
            alert('❌ Error de conexión con el servidor');
        }
    } else {
        try {
            const response = await fetch(`${API_URL}/fichaje/salida?usuarioId=${user.idUsuario}`, {
                method: 'POST',
                headers: { 'Authorization': `Bearer ${token}` }
            });

            if (response.ok) {
                const fichajeFinalizado = await response.json();
                btn.innerHTML = '<i class="fas fa-play"></i> Entrada';
                btn.style.backgroundColor = 'var(--success-color)';
                clearInterval(cronometroInterval);
                cronometroDisplay.innerText = '00:00:00';
                fichajeActivo = null;

                const horas = Math.floor(fichajeFinalizado.duracionSegundos / 3600);
                const minutos = Math.floor((fichajeFinalizado.duracionSegundos % 3600) / 60);
                alert(`✅ Salida registrada\n\n⏱️ Tiempo trabajado: ${horas}h ${minutos}m`);
            } else {
                const error = await response.text();
                alert('❌ Error al registrar salida: ' + error);
            }
        } catch (error) {
            console.error('Error:', error);
            alert('❌ Error de conexión con el servidor');
        }
    }
}

async function verificarFichajeActivo() {
    const user = JSON.parse(localStorage.getItem('user'));
    const token = localStorage.getItem('token');
    if (!user || !token) return;

    try {
        const response = await fetch(`${API_URL}/fichaje/activo?usuarioId=${user.idUsuario}`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (response.ok && response.status !== 204) {
            fichajeActivo = await response.json();
            const btn = document.getElementById('btnFichar');
            const cronometroDisplay = document.getElementById('cronometro');

            if (btn && fichajeActivo) {
                btn.innerHTML = '<i class="fas fa-stop"></i> Salida';
                btn.style.backgroundColor = 'var(--danger-color)';

                const entrada = new Date(fichajeActivo.fechaHoraEntrada);
                cronometroInterval = setInterval(() => {
                    const ahora = new Date();
                    const diff = Math.floor((ahora - entrada) / 1000);
                    const hrs = Math.floor(diff / 3600).toString().padStart(2, '0');
                    const mins = Math.floor((diff % 3600) / 60).toString().padStart(2, '0');
                    const secs = (diff % 60).toString().padStart(2, '0');
                    if (cronometroDisplay) cronometroDisplay.innerText = `${hrs}:${mins}:${secs}`;
                }, 1000);
            }
        }
    } catch (error) {
        console.error('Error verificando fichaje activo:', error);
    }
}


// Creación de tareas
const createTaskForm = document.getElementById('createTaskForm');
if (createTaskForm) {
    createTaskForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const titulo = document.getElementById('taskTitulo').value;
        const descripcion = document.getElementById('taskDesc').value;
        const idProyecto = document.getElementById('taskProyectoId').value;
        const prioridad = document.getElementById('taskPrioridad').value;
        const fechaLimite = document.getElementById('taskFechaLimite').value;

        // Si estamos en una vista de proyecto específico, usar ese ID
        const urlParams = new URLSearchParams(window.location.search);
        const projectId = urlParams.get('project');

        // Prioridad: 1. ID de URL, 2. ID del input
        let finalProjectId = projectId ? parseInt(projectId) : parseInt(idProyecto);

        if (!idProyecto || idProyecto === '' || !finalProjectId || isNaN(finalProjectId) || finalProjectId <= 0) {
            alert('❌ Debes especificar un ID de proyecto válido.');
            return;
        }

        try {
            const token = localStorage.getItem('token');
            const response = await fetch(`${API_URL}/tarea`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({
                    titulo,
                    descripcion,
                    idProyecto: finalProjectId,
                    prioridad,
                    estado: 'PENDIENTE',
                    fechaLimite: fechaLimite || null
                })
            });

            if (response.ok) {
                const tarea = await response.json();
                alert(`✅ Tarea creada con éxito!\n\n📋 Código de la tarea: ${tarea.codigoTarea}\n\nComparte este código con los trabajadores/colaboradores para que puedan registrarse.`);
                closeCreateTaskModal();
                loadKanbanBoard();
            } else {
                const error = await response.text();
                alert('❌ Error al crear tarea: ' + error);
            }
        } catch (error) {
            console.error('Error:', error);
            alert('❌ Error de conexión con el servidor');
        }
    });
}

// Cargar datos del Dashboard
async function loadDashboardData() {
    try {
        const token = localStorage.getItem('token');
        const user = JSON.parse(localStorage.getItem('user'));

        // Obtener proyectos
        const proyectosResponse = await fetch(`${API_URL}/proyecto`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        // Obtener tareas
        const tareasResponse = await fetch(`${API_URL}/tarea`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (proyectosResponse.ok && tareasResponse.ok) {
            let proyectos = await proyectosResponse.json();
            let tareas = await tareasResponse.json();

            // Filtrar según rol
            if (user.rol && user.rol.nombre === 'GESTOR_PROYECTO') {
                proyectos = proyectos.filter(p =>
                    p.responsable && p.responsable.idUsuario === user.idUsuario
                );
                const proyectosIds = proyectos.map(p => p.idProyecto);
                tareas = tareas.filter(t =>
                    t.proyecto && proyectosIds.includes(t.proyecto.idProyecto)
                );
            } else if (user.rol && (user.rol.nombre === 'TRABAJADOR' || user.rol.nombre === 'COLABORADOR')) {
                tareas = tareas.filter(t =>
                    t.asignado && t.asignado.idUsuario === user.idUsuario
                );
                const proyectosIds = [...new Set(tareas.map(t => t.proyecto.idProyecto))];
                proyectos = proyectos.filter(p => proyectosIds.includes(p.idProyecto));
            }

            // Calcular estadísticas
            const proyectosActivos = proyectos.filter(p => p.activo).length;
            const tareasPendientes = tareas.filter(t => t.estado === 'PENDIENTE' || t.estado === 'EN_PROGRESO').length;
            const tareasCompletadas = tareas.filter(t => t.estado === 'COMPLETADA').length;

            // Actualizar contadores
            const proyectosActivosEl = document.getElementById('proyectosActivos');
            const tareasPendientesEl = document.getElementById('tareasPendientes');
            const tareasCompletadasEl = document.getElementById('tareasCompletadas');

            if (proyectosActivosEl) proyectosActivosEl.innerText = proyectosActivos;
            if (tareasPendientesEl) tareasPendientesEl.innerText = tareasPendientes;
            if (tareasCompletadasEl) tareasCompletadasEl.innerText = tareasCompletadas;

            // Mostrar proyectos recientes
            loadRecentProjects(proyectos);
        }
    } catch (error) {
        console.error('Error cargando dashboard:', error);
    }
}

// Cargar proyectos recientes
function loadRecentProjects(proyectos) {
    const container = document.getElementById('projectsList');
    if (!container) return;

    if (proyectos.length === 0) {
        container.innerHTML = '<p>No hay proyectos disponibles.</p>';
        return;
    }

    container.innerHTML = '';
    const recientes = proyectos.slice(0, 5);

    recientes.forEach(proyecto => {
        const card = document.createElement('div');
        card.className = 'card';
        card.style.cursor = 'pointer';
        card.onclick = () => window.location.href = `kanban.html?project=${proyecto.idProyecto}`;

        card.innerHTML = `
            <div style="display: flex; justify-content: space-between; align-items: start; margin-bottom: 10px;">
                <h3 style="font-size: 1.1rem;">${proyecto.nombre}</h3>
                <span class="tag ${proyecto.activo ? 'tag-low' : 'tag-high'}">${proyecto.activo ? 'Activo' : 'Inactivo'}</span>
            </div>
            <p style="color: #666; font-size: 0.85rem; margin-bottom: 10px;">${proyecto.descripcion || 'Sin descripción'}</p>
            <div style="font-size: 0.8rem; color: #888;">
                <p><i class="far fa-user"></i> ${proyecto.responsable.nombre}</p>
                <p style="margin-top: 5px;"><strong>Código:</strong> ${proyecto.codigoRegistro || 'N/A'}</p>
            </div>
        `;
        container.appendChild(card);
    });
}
