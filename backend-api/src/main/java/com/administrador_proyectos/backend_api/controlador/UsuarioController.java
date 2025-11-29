package com.administrador_proyectos.backend_api.controlador;

import com.administrador_proyectos.backend_api.dto.UsuarioRequestDTO;
import com.administrador_proyectos.backend_api.dto.UsuarioResponseDTO;
import com.administrador_proyectos.backend_api.servicio.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/usuario")
@CrossOrigin(origins = "*")
public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public List<UsuarioResponseDTO> getAllUsers() {
        return usuarioService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> getUserById(@PathVariable Long id) {
        return usuarioService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> createUser(@RequestBody UsuarioRequestDTO usuarioRequestDTO) {
        UsuarioResponseDTO newUsuario = usuarioService.save(usuarioRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUsuario);
    }

    // ¡CORREGIDO! Ahora maneja excepciones consistentemente
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> updateUser(
            @PathVariable Long id,
            @RequestBody UsuarioRequestDTO usuarioRequestDTO) {

        try {
            UsuarioResponseDTO updatedUsuario = usuarioService.update(id, usuarioRequestDTO);
            return ResponseEntity.ok(updatedUsuario);
        } catch (RuntimeException e) {
            // Manejo centralizado de errores (mejor que try/catch en cada método)
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        usuarioService.deleteById(id);
    }

    // Nuevo endpoint para toggle de activo
    @PatchMapping("/{id}/toggle-activo")
    public ResponseEntity<UsuarioResponseDTO> toggleActivo(@PathVariable Long id) {
        try {
            UsuarioResponseDTO updatedUsuario = usuarioService.toggleActivo(id);
            return ResponseEntity.ok(updatedUsuario);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}