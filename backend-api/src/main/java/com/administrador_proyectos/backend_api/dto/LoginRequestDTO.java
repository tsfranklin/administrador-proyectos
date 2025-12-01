package com.administrador_proyectos.backend_api.dto;

public record LoginRequestDTO(
    String email,
    String contrasena
) {}
