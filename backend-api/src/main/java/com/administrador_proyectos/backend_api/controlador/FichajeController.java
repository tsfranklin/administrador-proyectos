package com.administrador_proyectos.backend_api.controlador;

import com.administrador_proyectos.backend_api.dto.FichajeResponseDTO;
import com.administrador_proyectos.backend_api.servicio.FichajeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fichaje")
@CrossOrigin(origins = "*")
public class FichajeController {

    private final FichajeService fichajeService;

    public FichajeController(FichajeService fichajeService) {
        this.fichajeService = fichajeService;
    }

    /**
     * Registrar entrada
     */
    @PostMapping("/entrada")
    public ResponseEntity<?> registrarEntrada(@RequestParam Long usuarioId) {
        try {
            FichajeResponseDTO fichaje = fichajeService.registrarEntrada(usuarioId);
            return ResponseEntity.status(HttpStatus.CREATED).body(fichaje);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Registrar salida
     */
    @PostMapping("/salida")
    public ResponseEntity<?> registrarSalida(@RequestParam Long usuarioId) {
        try {
            FichajeResponseDTO fichaje = fichajeService.registrarSalida(usuarioId);
            return ResponseEntity.ok(fichaje);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Obtener fichaje activo
     */
    @GetMapping("/activo")
    public ResponseEntity<?> getFichajeActivo(@RequestParam Long usuarioId) {
        FichajeResponseDTO fichaje = fichajeService.getFichajeActivo(usuarioId);
        if (fichaje != null) {
            return ResponseEntity.ok(fichaje);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    /**
     * Obtener historial de fichajes
     */
    @GetMapping("/historial")
    public ResponseEntity<List<FichajeResponseDTO>> getHistorial(@RequestParam Long usuarioId) {
        List<FichajeResponseDTO> fichajes = fichajeService.getHistorialFichajes(usuarioId);
        return ResponseEntity.ok(fichajes);
    }
}
