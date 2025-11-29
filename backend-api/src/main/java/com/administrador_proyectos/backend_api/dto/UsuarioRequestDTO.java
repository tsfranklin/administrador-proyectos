package com.administrador_proyectos.backend_api.dto;

public record UsuarioRequestDTO(
        String nombre,
        String email,
        String contrasena, // Se recibe la contraseña plana para hashear
        String telefono,
        Long idRol
) {
    // Método para convertir el DTO a la entidad (Mapper)
    public com.administrador_proyectos.backend_api.modelo.entidad.UsuarioModel toEntity() {
        com.administrador_proyectos.backend_api.modelo.entidad.UsuarioModel entity =
                new com.administrador_proyectos.backend_api.modelo.entidad.UsuarioModel();
        entity.setNombre(this.nombre);
        entity.setEmail(this.email);
        // Deduda tecnica: Aquí se debería aplicar un algoritmo de hashing (Posiblemente Bcrypt)
        entity.setContrasenaHash(this.contrasena); // Asignación temporal
        entity.setTelefono(this.telefono);
        com.administrador_proyectos.backend_api.modelo.entidad.RolModel rol =
                new com.administrador_proyectos.backend_api.modelo.entidad.RolModel();
        rol.setIdRol(this.idRol);
        entity.setRol(rol);
        return entity;
    }
}
