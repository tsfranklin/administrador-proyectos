package com.administrador_proyectos.backend_api.dto;

import java.util.List;

public record ComentarioRequestDTO(
        String texto,
        Long idTarea,
        Long idUsuario,
        List<Long> idsMenciones // IDs de usuarios mencionados
) {}