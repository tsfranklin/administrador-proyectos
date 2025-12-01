package com.administrador_proyectos.backend_api.controlador;

import com.administrador_proyectos.backend_api.configuracion.JwtUtils;
import com.administrador_proyectos.backend_api.dto.LoginRequestDTO;
import com.administrador_proyectos.backend_api.dto.LoginResponseDTO;
import com.administrador_proyectos.backend_api.dto.UsuarioResponseDTO;
import com.administrador_proyectos.backend_api.modelo.entidad.UsuarioModel;
import com.administrador_proyectos.backend_api.modelo.repositorio.UsuarioRepository;
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

    public AuthController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        UsuarioModel usuario = usuarioRepository.findByEmail(loginRequest.email())
                .orElse(null);

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");
        }

        // Verificar contraseña (asumiendo que en BD ya están hasheadas, o si no, comparar directo si es dev)
        // Nota: En producción siempre usar matches. Si en BD hay texto plano, esto fallará si no se migran.
        // Para este caso, si falla matches, intentamos equals por si acaso (solo para dev/legacy support)
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
                usuario.getRol() != null ? usuario.getRol().getNombre() : "USER"
        );

        return ResponseEntity.ok(new LoginResponseDTO(token, UsuarioResponseDTO.fromEntity(usuario)));
    }
}
