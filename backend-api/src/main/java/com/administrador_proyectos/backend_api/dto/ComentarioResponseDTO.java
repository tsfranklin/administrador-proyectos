package com.administrador_proyectos.backend_api.dto;

import com.administrador_proyectos.backend_api.modelo.entidad.ComentarioModel;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record ComentarioResponseDTO(
        Long idComentario,
        String texto,
        LocalDateTime fechaComentario,
        TareaResponseDTO tarea,
        UsuarioResponseDTO usuario,
        List<UsuarioResponseDTO> menciones
) {
    public static ComentarioResponseDTO fromEntity(
            com.administrador_proyectos.backend_api.modelo.entidad.ComentarioModel entity) {
        return new ComentarioResponseDTO(
                entity.getIdComentario(),
                entity.getTexto(),
                entity.getFechaComentario(),
                TareaResponseDTO.fromEntity(entity.getTarea()),
                UsuarioResponseDTO.fromEntity(entity.getUsuario()),
                entity.getMenciones().stream()
                        .map(UsuarioResponseDTO::fromEntity)
                        .collect(Collectors.toList())
        );
    }
}