package com.administrador_proyectos.backend_api.dto;

import java.time.LocalDateTime;

public record TareaRequestDTO(
        String titulo,
        String descripcion,
        LocalDateTime fechaLimite,
        String prioridad, // BAJA, MEDIA, ALTA
        String estado,    // PENDIENTE, EN_PROGRESO, BLOQUEADA, COMPLETADA
        Long idProyecto,
        Long idAsignado   // Puede ser null
) {}