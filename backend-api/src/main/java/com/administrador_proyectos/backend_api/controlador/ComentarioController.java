package com.administrador_proyectos.backend_api.controlador;

import com.administrador_proyectos.backend_api.dto.ComentarioRequestDTO;
import com.administrador_proyectos.backend_api.dto.ComentarioResponseDTO;
import com.administrador_proyectos.backend_api.servicio.ComentarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/comentario")
@CrossOrigin(origins = "*")
public class ComentarioController {

    private final ComentarioService comentarioService;

    public ComentarioController(ComentarioService comentarioService) {
        this.comentarioService = comentarioService;
    }

    @GetMapping("/tarea/{idTarea}")
    public List<ComentarioResponseDTO> getComentariosByTarea(@PathVariable Long idTarea) {
        return comentarioService.findByTareaId(idTarea);
    }

    @PostMapping
    public ResponseEntity<ComentarioResponseDTO> createComentario(@RequestBody ComentarioRequestDTO requestDTO) {
        ComentarioResponseDTO newComentario = comentarioService.save(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newComentario);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComentario(@PathVariable Long id) {
        comentarioService.deleteById(id);
    }
}