package com.administrador_proyectos.backend_api.servicio;

import com.administrador_proyectos.backend_api.dto.FichajeResponseDTO;
import com.administrador_proyectos.backend_api.modelo.entidad.FichajeModel;
import com.administrador_proyectos.backend_api.modelo.entidad.UsuarioModel;
import com.administrador_proyectos.backend_api.modelo.repositorio.FichajeRepository;
import com.administrador_proyectos.backend_api.modelo.repositorio.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FichajeService {

    private final FichajeRepository fichajeRepository;
    private final UsuarioRepository usuarioRepository;

    public FichajeService(FichajeRepository fichajeRepository, UsuarioRepository usuarioRepository) {
        this.fichajeRepository = fichajeRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Registrar entrada de un usuario
     */
    @Transactional
    public FichajeResponseDTO registrarEntrada(Long usuarioId) {
        // Verificar que el usuario existe
        UsuarioModel usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar que no tenga un fichaje activo
        fichajeRepository.findFichajeActivoByUsuarioId(usuarioId)
                .ifPresent(f -> {
                    throw new RuntimeException("Ya existe un fichaje activo. Debe registrar la salida primero.");
                });

        // Crear nuevo fichaje
        FichajeModel fichaje = new FichajeModel();
        fichaje.setUsuario(usuario);
        fichaje.setFechaHoraEntrada(LocalDateTime.now());

        FichajeModel savedFichaje = fichajeRepository.save(fichaje);
        return FichajeResponseDTO.fromEntity(savedFichaje);
    }

    /**
     * Registrar salida de un usuario
     */
    @Transactional
    public FichajeResponseDTO registrarSalida(Long usuarioId) {
        // Buscar fichaje activo
        FichajeModel fichaje = fichajeRepository.findFichajeActivoByUsuarioId(usuarioId)
                .orElseThrow(
                        () -> new RuntimeException("No existe un fichaje activo. Debe registrar la entrada primero."));

        // Registrar salida
        LocalDateTime salida = LocalDateTime.now();
        fichaje.setFechaHoraSalida(salida);

        // Calcular duración en segundos
        Duration duration = Duration.between(fichaje.getFechaHoraEntrada(), salida);
        fichaje.setDuracionSegundos(duration.getSeconds());

        FichajeModel savedFichaje = fichajeRepository.save(fichaje);
        return FichajeResponseDTO.fromEntity(savedFichaje);
    }

    /**
     * Obtener fichaje activo de un usuario
     */
    @Transactional(readOnly = true)
    public FichajeResponseDTO getFichajeActivo(Long usuarioId) {
        return fichajeRepository.findFichajeActivoByUsuarioId(usuarioId)
                .map(FichajeResponseDTO::fromEntity)
                .orElse(null);
    }

    /**
     * Obtener historial de fichajes de un usuario
     */
    @Transactional(readOnly = true)
    public List<FichajeResponseDTO> getHistorialFichajes(Long usuarioId) {
        return fichajeRepository.findByUsuarioId(usuarioId).stream()
                .map(FichajeResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
