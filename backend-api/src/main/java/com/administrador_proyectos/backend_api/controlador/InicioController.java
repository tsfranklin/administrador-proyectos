package com.administrador_proyectos.backend_api.controlador;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1")
public class InicioController {
    @GetMapping("/")
    public String inicio() {
        return "API Administrador Proyectos.";
    }
}
