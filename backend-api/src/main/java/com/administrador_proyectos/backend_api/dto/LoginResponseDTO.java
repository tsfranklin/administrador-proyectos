package com.administrador_proyectos.backend_api.dto;

public record LoginResponseDTO(
    String token,
    UsuarioResponseDTO usuario
) {}
