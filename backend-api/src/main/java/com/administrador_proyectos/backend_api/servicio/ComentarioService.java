package com.administrador_proyectos.backend_api.servicio;

import com.administrador_proyectos.backend_api.dto.ComentarioRequestDTO;
import com.administrador_proyectos.backend_api.dto.ComentarioResponseDTO;
import com.administrador_proyectos.backend_api.modelo.entidad.ComentarioModel;
import com.administrador_proyectos.backend_api.modelo.entidad.TareaModel;
import com.administrador_proyectos.backend_api.modelo.entidad.UsuarioModel;
import com.administrador_proyectos.backend_api.modelo.repositorio.ComentarioRepository;
import com.administrador_proyectos.backend_api.modelo.repositorio.TareaRepository;
import com.administrador_proyectos.backend_api.modelo.repositorio.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;
    private final TareaRepository tareaRepository;
    private final UsuarioRepository usuarioRepository;

    public ComentarioService(ComentarioRepository comentarioRepository,
                             TareaRepository tareaRepository,
                             UsuarioRepository usuarioRepository) {
        this.comentarioRepository = comentarioRepository;
        this.tareaRepository = tareaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<ComentarioResponseDTO> findByTareaId(Long idTarea) {
        return comentarioRepository.findByTareaIdOrderByFechaComentarioDesc(idTarea).stream()
                .map(ComentarioResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public ComentarioResponseDTO save(ComentarioRequestDTO requestDTO) {
        // Validar tarea existente
        TareaModel tarea = tareaRepository.findById(requestDTO.idTarea())
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

        // Validar usuario existente
        UsuarioModel usuario = usuarioRepository.findById(requestDTO.idUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        ComentarioModel comentario = new ComentarioModel();
        comentario.setTexto(requestDTO.texto());
        comentario.setTarea(tarea);
        comentario.setUsuario(usuario);

        // Procesar menciones
        if (requestDTO.idsMenciones() != null && !requestDTO.idsMenciones().isEmpty()) {
            requestDTO.idsMenciones().stream()
                    .map(id -> usuarioRepository.findById(id)
                            .orElseThrow(() -> new RuntimeException("Usuario mencionado no encontrado: " + id)))
                    .forEach(comentario.getMenciones()::add);
        }

        return ComentarioResponseDTO.fromEntity(comentarioRepository.save(comentario));
    }

    @Transactional
    public void deleteById(Long id) {
        comentarioRepository.deleteById(id);
    }
}