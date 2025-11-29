package com.administrador_proyectos.backend_api.dto;

import com.administrador_proyectos.backend_api.modelo.entidad.UsuarioModel;
import java.time.LocalDateTime;

public record UsuarioResponseDTO(
        Long idUsuario,
        String nombre,
        String email,
        LocalDateTime fechaRegistro,
        Boolean activo,
        String telefono,
        RolResponseDTO rol
) {
    // Constructor estático para convertir la entidad a DTO (Mapper)
    public static UsuarioResponseDTO fromEntity(UsuarioModel entity) {
        RolResponseDTO rolDTO = (entity.getRol() != null) ?
                new RolResponseDTO(entity.getRol().getIdRol(), entity.getRol().getNombre()) : null;

        return new UsuarioResponseDTO(
                entity.getId(),
                entity.getNombre(),
                entity.getEmail(),
                entity.getFechaRegistro(),
                entity.getActivo(),
                entity.getTelefono(),
                entity.getRol() != null ?
                        new RolResponseDTO(entity.getRol().getIdRol(), entity.getRol().getNombre()) :
                        null
        );
    }
}
