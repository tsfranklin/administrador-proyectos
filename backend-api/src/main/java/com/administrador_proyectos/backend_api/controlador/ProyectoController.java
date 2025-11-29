package com.administrador_proyectos.backend_api.controlador;

import com.administrador_proyectos.backend_api.dto.ProyectoRequestDTO;
import com.administrador_proyectos.backend_api.dto.ProyectoResponseDTO;
import com.administrador_proyectos.backend_api.servicio.ProyectoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/proyecto")
@CrossOrigin(origins = "*")
public class ProyectoController {

    private final ProyectoService proyectoService;

    public ProyectoController(ProyectoService proyectoService) {
        this.proyectoService = proyectoService;
    }

    @GetMapping
    public List<ProyectoResponseDTO> getAllProyectos(
            @RequestParam(required = false) Boolean activo) {
        return activo != null ?
                proyectoService.findByActivo(activo) :
                proyectoService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProyectoResponseDTO> getProyectoById(@PathVariable Long id) {
        return proyectoService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProyectoResponseDTO> createProyecto(@RequestBody ProyectoRequestDTO requestDTO) {
        ProyectoResponseDTO newProyecto = proyectoService.save(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newProyecto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProyectoResponseDTO> updateProyecto(
            @PathVariable Long id,
            @RequestBody ProyectoRequestDTO requestDTO) {
        return proyectoService.update(id, requestDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProyecto(@PathVariable Long id) {
        proyectoService.deleteById(id);
    }

    @PatchMapping("/{id}/toggle-activo")
    public ResponseEntity<ProyectoResponseDTO> toggleActivo(@PathVariable Long id) {
        return ResponseEntity.ok(proyectoService.toggleActivo(id));
    }
}