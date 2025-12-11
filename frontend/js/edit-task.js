// Funciones para editar tareas

async function showEditTaskModal(idTarea) {
    const modal = document.getElementById('editTaskModal');
    if (!modal) return;

    try {
        const token = localStorage.getItem('token');

        // Cargar datos de la tarea
        const response = await fetch(`${API_URL}/tarea/${idTarea}`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (response.ok) {
            const tarea = await response.json();

            // Cargar proyectos disponibles
            await loadProyectosParaEdicion();

            // Llenar el formulario con los datos actuales
            document.getElementById('editTaskId').value = tarea.idTarea;
            document.getElementById('editTaskTitulo').value = tarea.titulo;
            document.getElementById('editTaskDesc').value = tarea.descripcion || '';
            document.getElementById('editTaskProyectoId').value = tarea.proyecto.idProyecto;
            document.getElementById('editTaskPrioridad').value = tarea.prioridad;

            // Formatear fecha para el input datetime-local
            if (tarea.fechaLimite) {
                const fecha = new Date(tarea.fechaLimite);
                const fechaFormateada = fecha.toISOString().slice(0, 16);
                document.getElementById('editTaskFechaLimite').value = fechaFormateada;
            }

            modal.style.display = 'block';
        } else {
            alert('❌ Error al cargar la tarea');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('❌ Error de conexión');
    }
}

async function loadProyectosParaEdicion() {
    const token = localStorage.getItem('token');
    try {
        const response = await fetch(`${API_URL}/proyecto`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (response.ok) {
            const proyectos = await response.json();
            const select = document.getElementById('editTaskProyectoId');
            select.innerHTML = '<option value="">Selecciona un proyecto...</option>';

            proyectos.forEach(proyecto => {
                const option = document.createElement('option');
                option.value = proyecto.idProyecto;
                option.textContent = `${proyecto.nombre} (${proyecto.codigoRegistro})`;
                select.appendChild(option);
            });
        }
    } catch (error) {
        console.error('Error cargando proyectos:', error);
    }
}

function closeEditTaskModal() {
    document.getElementById('editTaskModal').style.display = 'none';
    document.getElementById('editTaskForm').reset();
}

// Manejar envío del formulario de edición
const editTaskForm = document.getElementById('editTaskForm');
if (editTaskForm) {
    editTaskForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const idTarea = document.getElementById('editTaskId').value;
        const titulo = document.getElementById('editTaskTitulo').value;
        const descripcion = document.getElementById('editTaskDesc').value;
        const idProyecto = document.getElementById('editTaskProyectoId').value;
        const prioridad = document.getElementById('editTaskPrioridad').value;
        const fechaLimite = document.getElementById('editTaskFechaLimite').value;

        if (!idProyecto || idProyecto === '') {
            alert('❌ Debes seleccionar un proyecto válido.');
            return;
        }

        try {
            const token = localStorage.getItem('token');
            const response = await fetch(`${API_URL}/tarea/${idTarea}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({
                    titulo,
                    descripcion,
                    idProyecto: parseInt(idProyecto),
                    prioridad,
                    estado: 'PENDIENTE', // Mantener el estado actual o se puede obtener de la tarea
                    fechaLimite: fechaLimite || null
                })
            });

            if (response.ok) {
                alert('✅ Tarea actualizada con éxito!');
                closeEditTaskModal();
                loadKanbanBoard();
            } else {
                const error = await response.text();
                alert('❌ Error al actualizar tarea: ' + error);
            }
        } catch (error) {
            console.error('Error:', error);
            alert('❌ Error de conexión con el servidor');
        }
    });
}
