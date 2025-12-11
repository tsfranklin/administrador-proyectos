package com.administrador_proyectos.backend_api.servicio;

import com.administrador_proyectos.backend_api.dto.UsuarioRequestDTO;
import com.administrador_proyectos.backend_api.dto.UsuarioResponseDTO;
import com.administrador_proyectos.backend_api.modelo.entidad.ProyectoModel;
import com.administrador_proyectos.backend_api.modelo.entidad.RolModel;
import com.administrador_proyectos.backend_api.modelo.entidad.TareaModel;
import com.administrador_proyectos.backend_api.modelo.entidad.UsuarioModel;
import com.administrador_proyectos.backend_api.modelo.repositorio.ProyectoRepository;
import com.administrador_proyectos.backend_api.modelo.repositorio.RolRepository;
import com.administrador_proyectos.backend_api.modelo.repositorio.TareaRepository;
import com.administrador_proyectos.backend_api.modelo.repositorio.UsuarioRepository;
import com.administrador_proyectos.backend_api.util.ValidationUtil;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioService implements UserDetailsService {
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final ProyectoRepository proyectoRepository;
    private final TareaRepository tareaRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            ProyectoRepository proyectoRepository,
            TareaRepository tareaRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.proyectoRepository = proyectoRepository;
        this.tareaRepository = tareaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UsuarioModel usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

        return new org.springframework.security.core.userdetails.User(
                usuario.getEmail(),
                usuario.getContrasenaHash(),
                usuario.getActivo(),
                true, true, true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().getNombre())));
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

        // Validar que no se pueda crear más de un administrador
        if (rol.getIdRol() == 1) {
            long countAdmins = usuarioRepository.count();
            if (countAdmins > 0) {
                boolean adminExists = usuarioRepository.findAll().stream()
                        .anyMatch(u -> u.getRol() != null && u.getRol().getIdRol() == 1);
                if (adminExists) {
                    throw new RuntimeException(
                            "Ya existe un administrador en el sistema. Solo puede haber un administrador único.");
                }
            }
        }

        // Validar formato de email
        if (!ValidationUtil.isValidEmail(requestDTO.email())) {
            throw new RuntimeException("El formato del email no es válido");
        }

        // Validar email único
        if (usuarioRepository.findByEmail(requestDTO.email()).isPresent()) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Validar formato de teléfono
        if (!ValidationUtil.isValidPhone(requestDTO.telefono())) {
            throw new RuntimeException("El formato del teléfono no es válido");
        }

        // Validar contraseña segura
        if (!ValidationUtil.isValidPassword(requestDTO.contrasena())) {
            throw new RuntimeException(ValidationUtil.getPasswordRequirements());
        }

        // Lógica de validación de códigos según rol
        if (rol.getIdRol() == 2) {
            if (requestDTO.codigo() == null || requestDTO.codigo().isEmpty()) {
                throw new RuntimeException("El código de proyecto es obligatorio para Gestores");
            }
            proyectoRepository.findByCodigoRegistro(requestDTO.codigo())
                    .orElseThrow(() -> new RuntimeException("Código de proyecto inválido"));
        } else if (rol.getIdRol() == 3 || rol.getIdRol() == 4) {
            if (requestDTO.codigo() == null || requestDTO.codigo().isEmpty()) {
                throw new RuntimeException("El código de tarea es obligatorio para Trabajadores/Colaboradores");
            }
            TareaModel tarea = tareaRepository.findByCodigoTarea(requestDTO.codigo())
                    .orElseThrow(() -> new RuntimeException("Código de tarea inválido"));
        }

        UsuarioModel usuario = new UsuarioModel();
        usuario.setNombre(requestDTO.nombre());
        usuario.setEmail(requestDTO.email());
        usuario.setTelefono(requestDTO.telefono());
        usuario.setRol(rol);

        String hashedPassword = passwordEncoder.encode(requestDTO.contrasena());
        usuario.setContrasenaHash(hashedPassword);

        UsuarioModel savedUsuario = usuarioRepository.save(usuario);

        // Asignar proyecto al gestor si se registró con código de proyecto
        if (rol.getIdRol() == 2 && requestDTO.codigo() != null && !requestDTO.codigo().isEmpty()) {
            ProyectoModel proyecto = proyectoRepository.findByCodigoRegistro(requestDTO.codigo())
                    .orElseThrow(() -> new RuntimeException("Código de proyecto inválido"));
            proyecto.setResponsable(savedUsuario);
            proyectoRepository.save(proyecto);
        }

        // Asignar tarea al trabajador/colaborador
        if (rol.getIdRol() == 3 || rol.getIdRol() == 4) {
            TareaModel tarea = tareaRepository.findByCodigoTarea(requestDTO.codigo())
                    .orElseThrow(() -> new RuntimeException("Código de tarea inválido"));
            tarea.setAsignado(savedUsuario);
            tareaRepository.save(tarea);
        }

        return UsuarioResponseDTO.fromEntity(savedUsuario);
    }

    @Transactional
    public UsuarioResponseDTO update(Long id, UsuarioRequestDTO requestDTO) {
        UsuarioModel existingUser = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        usuarioRepository.findByEmail(requestDTO.email())
                .ifPresent(user -> {
                    if (!user.getId().equals(id)) {
                        throw new RuntimeException("El email ya está registrado");
                    }
                });

        if (requestDTO.idRol() != null &&
                !requestDTO.idRol().equals(existingUser.getRol().getIdRol())) {
            RolModel newRol = rolRepository.findById(requestDTO.idRol())
                    .orElseThrow(() -> new RuntimeException("Rol no válido: " + requestDTO.idRol()));
            existingUser.setRol(newRol);
        }

        existingUser.setNombre(requestDTO.nombre());
        existingUser.setEmail(requestDTO.email());
        existingUser.setTelefono(requestDTO.telefono());

        if (requestDTO.contrasena() != null && !requestDTO.contrasena().isEmpty()) {
            String hashedPassword = passwordEncoder.encode(requestDTO.contrasena());
            existingUser.setContrasenaHash(hashedPassword);
        }

        return UsuarioResponseDTO.fromEntity(usuarioRepository.save(existingUser));
    }

    @Transactional
    public void deleteById(Long id) {
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

        if (usuario.getActivo() == null) {
            usuario.setActivo(true);
        } else {
            usuario.setActivo(!usuario.getActivo());
        }

        return UsuarioResponseDTO.fromEntity(usuarioRepository.save(usuario));
    }
}