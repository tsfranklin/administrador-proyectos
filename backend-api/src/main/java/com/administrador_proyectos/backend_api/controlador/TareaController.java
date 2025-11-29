package com.administrador_proyectos.backend_api.controlador;

import com.administrador_proyectos.backend_api.dto.TareaRequestDTO;
import com.administrador_proyectos.backend_api.dto.TareaResponseDTO;
import com.administrador_proyectos.backend_api.servicio.TareaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tarea")
@CrossOrigin(origins = "*")
public class TareaController {

    private final TareaService tareaService;

    public TareaController(TareaService tareaService) {
        this.tareaService = tareaService;
    }

    @GetMapping
    public List<TareaResponseDTO> getAllTareas(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String prioridad) {
        if (estado != null) return tareaService.findByEstado(estado);
        if (prioridad != null) return tareaService.findByPrioridad(prioridad);
        return tareaService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TareaResponseDTO> getTareaById(@PathVariable Long id) {
        return tareaService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TareaResponseDTO> createTarea(@RequestBody TareaRequestDTO requestDTO) {
        TareaResponseDTO newTarea = tareaService.save(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newTarea);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TareaResponseDTO> updateTarea(
            @PathVariable Long id,
            @RequestBody TareaRequestDTO requestDTO) {
        return tareaService.update(id, requestDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/mover-estado")
    public ResponseEntity<TareaResponseDTO> moverEstado(
            @PathVariable Long id,
            @RequestParam String nuevoEstado) {
        return ResponseEntity.ok(tareaService.moverEstado(id, nuevoEstado));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTarea(@PathVariable Long id) {
        tareaService.deleteById(id);
    }
}