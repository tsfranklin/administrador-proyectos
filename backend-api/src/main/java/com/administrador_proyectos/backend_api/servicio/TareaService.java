package com.administrador_proyectos.backend_api.servicio;

import com.administrador_proyectos.backend_api.dto.TareaRequestDTO;
import com.administrador_proyectos.backend_api.dto.TareaResponseDTO;
import com.administrador_proyectos.backend_api.modelo.entidad.ProyectoModel;
import com.administrador_proyectos.backend_api.modelo.entidad.TareaModel;
import com.administrador_proyectos.backend_api.modelo.entidad.UsuarioModel;
import com.administrador_proyectos.backend_api.modelo.repositorio.ProyectoRepository;
import com.administrador_proyectos.backend_api.modelo.repositorio.TareaRepository;
import com.administrador_proyectos.backend_api.modelo.repositorio.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TareaService {

    private final TareaRepository tareaRepository;
    private final ProyectoRepository proyectoRepository;
    private final UsuarioRepository usuarioRepository;

    public TareaService(TareaRepository tareaRepository,
            ProyectoRepository proyectoRepository,
            UsuarioRepository usuarioRepository) {
        this.tareaRepository = tareaRepository;
        this.proyectoRepository = proyectoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<TareaResponseDTO> findAll() {
        return tareaRepository.findAll().stream()
                .map(TareaResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TareaResponseDTO> findByEstado(String estado) {
        return tareaRepository.findByEstado(estado).stream()
                .map(TareaResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TareaResponseDTO> findByPrioridad(String prioridad) {
        return tareaRepository.findByPrioridad(prioridad).stream()
                .map(TareaResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<TareaResponseDTO> findById(Long id) {
        return tareaRepository.findById(id)
                .map(TareaResponseDTO::fromEntity);
    }

    @Transactional
    public TareaResponseDTO save(TareaRequestDTO requestDTO) {
        // Validar proyecto existente
        ProyectoModel proyecto = proyectoRepository.findById(requestDTO.idProyecto())
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        // Validar asignado si existe
        UsuarioModel asignado = null;
        if (requestDTO.idAsignado() != null) {
            asignado = usuarioRepository.findById(requestDTO.idAsignado())
                    .orElseThrow(() -> new RuntimeException("Usuario asignado no encontrado"));
        }

        TareaModel tarea = new TareaModel();
        tarea.setTitulo(requestDTO.titulo());
        tarea.setDescripcion(requestDTO.descripcion());
        tarea.setFechaLimite(requestDTO.fechaLimite());
        tarea.setPrioridad(TareaModel.Prioridad.valueOf(requestDTO.prioridad()));
        tarea.setEstado(TareaModel.Estado.valueOf(requestDTO.estado()));
        tarea.setProyecto(proyecto);
        tarea.setAsignado(asignado);

        // Generar código único formato TASK-XXXXXX
        tarea.setCodigoTarea(generarCodigoTarea());

        return TareaResponseDTO.fromEntity(tareaRepository.save(tarea));
    }

    @Transactional
    public Optional<TareaResponseDTO> update(Long id, TareaRequestDTO requestDTO) {
        return tareaRepository.findById(id)
                .map(existingTarea -> {
                    // Actualizar proyecto si cambia
                    if (requestDTO.idProyecto() != null &&
                            !requestDTO.idProyecto().equals(existingTarea.getProyecto().getId())) {
                        ProyectoModel newProyecto = proyectoRepository.findById(requestDTO.idProyecto())
                                .orElseThrow(() -> new RuntimeException("Nuevo proyecto no encontrado"));
                        existingTarea.setProyecto(newProyecto);
                    }

                    // Actualizar asignado si cambia
                    if (requestDTO.idAsignado() != null) {
                        if (existingTarea.getAsignado() == null ||
                                !requestDTO.idAsignado().equals(existingTarea.getAsignado().getId())) {
                            UsuarioModel newAsignado = usuarioRepository.findById(requestDTO.idAsignado())
                                    .orElseThrow(() -> new RuntimeException("Nuevo asignado no encontrado"));
                            existingTarea.setAsignado(newAsignado);
                        }
                    } else {
                        existingTarea.setAsignado(null);
                    }

                    // Actualizar otros campos
                    if (requestDTO.titulo() != null)
                        existingTarea.setTitulo(requestDTO.titulo());
                    if (requestDTO.descripcion() != null)
                        existingTarea.setDescripcion(requestDTO.descripcion());
                    if (requestDTO.fechaLimite() != null)
                        existingTarea.setFechaLimite(requestDTO.fechaLimite());
                    if (requestDTO.prioridad() != null)
                        existingTarea.setPrioridad(TareaModel.Prioridad.valueOf(requestDTO.prioridad()));
                    if (requestDTO.estado() != null)
                        existingTarea.setEstado(TareaModel.Estado.valueOf(requestDTO.estado()));

                    return TareaResponseDTO.fromEntity(tareaRepository.save(existingTarea));
                });
    }

    @Transactional
    public TareaResponseDTO moverEstado(Long id, String nuevoEstado) {
        return tareaRepository.findById(id)
                .map(tarea -> {
                    try {
                        tarea.setEstado(TareaModel.Estado.valueOf(nuevoEstado.toUpperCase()));
                        return TareaResponseDTO.fromEntity(tareaRepository.save(tarea));
                    } catch (IllegalArgumentException e) {
                        throw new RuntimeException("Estado inválido: " + nuevoEstado);
                    }
                })
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));
    }

    @Transactional
    public void deleteById(Long id) {
        tareaRepository.deleteById(id);
    }

    /**
     * Genera un código único para la tarea en formato TASK-XXXXXX
     * donde X son caracteres alfanuméricos aleatorios
     */
    private String generarCodigoTarea() {
        String codigo;
        do {
            codigo = "TASK-" + generarCodigoAleatorio(6);
        } while (tareaRepository.findByCodigoTarea(codigo).isPresent());
        return codigo;
    }

    /**
     * Genera una cadena aleatoria de caracteres alfanuméricos
     */
    private String generarCodigoAleatorio(int longitud) {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder codigo = new StringBuilder();
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < longitud; i++) {
            codigo.append(caracteres.charAt(random.nextInt(caracteres.length())));
        }
        return codigo.toString();
    }
}