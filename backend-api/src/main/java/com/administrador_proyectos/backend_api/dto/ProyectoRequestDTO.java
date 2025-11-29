package com.administrador_proyectos.backend_api.dto;

import java.time.LocalDateTime;

public record ProyectoRequestDTO(
        String nombre,
        String descripcion,
        LocalDateTime fechaInicio,
        LocalDateTime fechaFin,
        Boolean activo,
        Long idResponsable
) {}