package com.administrador_proyectos.backend_api.controlador;

import com.administrador_proyectos.backend_api.configuracion.JwtUtils;
import com.administrador_proyectos.backend_api.dto.LoginRequestDTO;
import com.administrador_proyectos.backend_api.dto.LoginResponseDTO;
import com.administrador_proyectos.backend_api.dto.UsuarioRequestDTO;
import com.administrador_proyectos.backend_api.dto.UsuarioResponseDTO;
import com.administrador_proyectos.backend_api.modelo.entidad.UsuarioModel;
import com.administrador_proyectos.backend_api.modelo.repositorio.UsuarioRepository;
import com.administrador_proyectos.backend_api.servicio.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final UsuarioService usuarioService;

    public AuthController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils,
            UsuarioService usuarioService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UsuarioRequestDTO request) {
        try {
            UsuarioResponseDTO nuevoUsuario = usuarioService.save(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        UsuarioModel usuario = usuarioRepository.findByEmail(loginRequest.email())
                .orElse(null);

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");
        }

        // Verificar contraseña
        boolean passwordMatch = passwordEncoder.matches(loginRequest.contrasena(), usuario.getContrasenaHash());

        if (!passwordMatch && loginRequest.contrasena().equals(usuario.getContrasenaHash())) {
            passwordMatch = true; // Fallback para contraseñas en texto plano
        }

        if (!passwordMatch) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
        }

        if (!usuario.getActivo()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Usuario inactivo");
        }

        String token = jwtUtils.generateToken(
                usuario.getEmail(),
                usuario.getId(),
                usuario.getRol() != null ? usuario.getRol().getNombre() : "USER");

        return ResponseEntity.ok(new LoginResponseDTO(token, UsuarioResponseDTO.fromEntity(usuario)));
    }
}
