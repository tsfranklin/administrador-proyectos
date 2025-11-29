package com.administrador_proyectos.backend_api.servicio;

import com.administrador_proyectos.backend_api.dto.UsuarioRequestDTO;
import com.administrador_proyectos.backend_api.dto.UsuarioResponseDTO;
import com.administrador_proyectos.backend_api.modelo.entidad.UsuarioModel;
import com.administrador_proyectos.backend_api.modelo.entidad.RolModel;
import com.administrador_proyectos.backend_api.modelo.repositorio.UsuarioRepository;
import com.administrador_proyectos.backend_api.modelo.repositorio.RolRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          RolRepository rolRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> findAll() {
        return usuarioRepository.findAll().stream()
                .map(UsuarioResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<UsuarioResponseDTO> findById(Long id) {
        return usuarioRepository.findById(id)
                .map(UsuarioResponseDTO::fromEntity);
    }

    @Transactional
    public UsuarioResponseDTO save(UsuarioRequestDTO requestDTO) {
        // Validar rol existente
        RolModel rol = rolRepository.findById(requestDTO.idRol())
                .orElseThrow(() -> new RuntimeException("Rol no válido: " + requestDTO.idRol()));

        // Validar email único
        if (usuarioRepository.findByEmail(requestDTO.email()).isPresent()) {
            throw new RuntimeException("El email ya está registrado");
        }

        UsuarioModel usuario = new UsuarioModel();
        usuario.setNombre(requestDTO.nombre());
        usuario.setEmail(requestDTO.email());
        usuario.setTelefono(requestDTO.telefono());
        usuario.setRol(rol);

        // Encriptar contraseña
        // !!! CORRECCIÓN CLAVE: Usar requestDTO.contrasena() para coincidir con tu DTO.
        String hashedPassword = passwordEncoder.encode(requestDTO.contrasena());
        usuario.setContrasenaHash(hashedPassword);

        UsuarioModel savedUsuario = usuarioRepository.save(usuario);
        return UsuarioResponseDTO.fromEntity(savedUsuario);
    }

    // ¡CORREGIDO! Ahora devuelve UsuarioResponseDTO directo (sin Optional)
    @Transactional
    public UsuarioResponseDTO update(Long id, UsuarioRequestDTO requestDTO) {
        // Buscar usuario existente o lanzar excepción
        UsuarioModel existingUser = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        // Validar email único (excluyendo el actual)
        usuarioRepository.findByEmail(requestDTO.email())
                .ifPresent(user -> {
                    if (!user.getId().equals(id)) {
                        throw new RuntimeException("El email ya está registrado");
                    }
                });

        // Actualizar rol si cambia
        if (requestDTO.idRol() != null &&
                !requestDTO.idRol().equals(existingUser.getRol().getIdRol())) {
            RolModel newRol = rolRepository.findById(requestDTO.idRol())
                    .orElseThrow(() -> new RuntimeException("Rol no válido: " + requestDTO.idRol()));
            existingUser.setRol(newRol);
        }

        // Actualizar otros campos
        existingUser.setNombre(requestDTO.nombre());
        existingUser.setEmail(requestDTO.email());
        existingUser.setTelefono(requestDTO.telefono());

        // Actualizar contraseña solo si se proporciona una nueva
        // !!! CORRECCIÓN CLAVE: Usar requestDTO.contrasena() para coincidir con tu DTO.
        if (requestDTO.contrasena() != null && !requestDTO.contrasena().isEmpty()) {
            String hashedPassword = passwordEncoder.encode(requestDTO.contrasena());
            existingUser.setContrasenaHash(hashedPassword);
        }

        return UsuarioResponseDTO.fromEntity(usuarioRepository.save(existingUser));
    }

    @Transactional
    public void deleteById(Long id) {
        // Validar que el usuario no esté asignado a tareas/proyectos
        if (usuarioRepository.existsByAssignedTasks(id) ||
                usuarioRepository.existsByManagedProjects(id)) {
            throw new RuntimeException("No se puede eliminar el usuario porque tiene tareas o proyectos asignados");
        }

        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }

        usuarioRepository.deleteById(id);
    }

    @Transactional
    public UsuarioResponseDTO toggleActivo(Long id) {
        UsuarioModel usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        // Esto requiere que UsuarioModel tenga un campo 'activo' y sus getters/setters
        if (usuario.getActivo() == null) {
             usuario.setActivo(true);
        } else {
             usuario.setActivo(!usuario.getActivo());
        }
        
        return UsuarioResponseDTO.fromEntity(usuarioRepository.save(usuario));
    }
}