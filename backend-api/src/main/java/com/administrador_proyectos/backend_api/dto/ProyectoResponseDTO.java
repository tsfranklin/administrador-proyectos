package com.administrador_proyectos.backend_api.dto;

import java.time.LocalDateTime;

public record ProyectoResponseDTO(
        Long idProyecto,
        String nombre,
        String descripcion,
        LocalDateTime fechaInicio,
        LocalDateTime fechaFin,
        Boolean activo,
        UsuarioResponseDTO responsable
) {
    public static ProyectoResponseDTO fromEntity(
            com.administrador_proyectos.backend_api.modelo.entidad.ProyectoModel entity) {
        return new ProyectoResponseDTO(
                entity.getId(),
                entity.getNombre(),
                entity.getDescripcion(),
                entity.getFechaInicio(),
                entity.getFechaFin(),
                entity.getActivo(),
                UsuarioResponseDTO.fromEntity(entity.getResponsable())
        );
    }
}