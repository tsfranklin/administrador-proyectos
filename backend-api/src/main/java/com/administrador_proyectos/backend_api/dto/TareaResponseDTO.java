package com.administrador_proyectos.backend_api.dto;

import java.time.LocalDateTime;

public record TareaResponseDTO(
        Long idTarea,
        String titulo,
        String descripcion,
        LocalDateTime fechaLimite,
        String prioridad,
        String estado,
        ProyectoResponseDTO proyecto,
        UsuarioResponseDTO asignado
) {
    public static TareaResponseDTO fromEntity(
            com.administrador_proyectos.backend_api.modelo.entidad.TareaModel entity) {
        return new TareaResponseDTO(
                entity.getId(),
                entity.getTitulo(),
                entity.getDescripcion(),
                entity.getFechaLimite(),
                entity.getPrioridad().name(),
                entity.getEstado().name(),
                ProyectoResponseDTO.fromEntity(entity.getProyecto()),
                entity.getAsignado() != null ?
                        UsuarioResponseDTO.fromEntity(entity.getAsignado()) : null
        );
    }
}