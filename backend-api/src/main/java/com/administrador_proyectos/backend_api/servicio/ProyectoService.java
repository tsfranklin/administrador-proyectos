package com.administrador_proyectos.backend_api.servicio;

import com.administrador_proyectos.backend_api.dto.ProyectoRequestDTO;
import com.administrador_proyectos.backend_api.dto.ProyectoResponseDTO;
import com.administrador_proyectos.backend_api.modelo.entidad.ProyectoModel;
import com.administrador_proyectos.backend_api.modelo.entidad.UsuarioModel;
import com.administrador_proyectos.backend_api.modelo.repositorio.ProyectoRepository;
import com.administrador_proyectos.backend_api.modelo.repositorio.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProyectoService {

    private final ProyectoRepository proyectoRepository;
    private final UsuarioRepository usuarioRepository;

    public ProyectoService(ProyectoRepository proyectoRepository, UsuarioRepository usuarioRepository) {
        this.proyectoRepository = proyectoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<ProyectoResponseDTO> findAll() {
        return proyectoRepository.findAll().stream()
                .map(ProyectoResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProyectoResponseDTO> findByActivo(boolean activo) {
        return proyectoRepository.findByActivo(activo).stream()
                .map(ProyectoResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ProyectoResponseDTO> findById(Long id) {
        return proyectoRepository.findById(id)
                .map(ProyectoResponseDTO::fromEntity);
    }

    @Transactional
    public ProyectoResponseDTO save(ProyectoRequestDTO requestDTO) {
        // Validar responsable existente
        UsuarioModel responsable = usuarioRepository.findById(requestDTO.idResponsable())
                .orElseThrow(() -> new RuntimeException("Responsable no encontrado"));

        ProyectoModel proyecto = new ProyectoModel();
        proyecto.setNombre(requestDTO.nombre());
        proyecto.setDescripcion(requestDTO.descripcion());
        proyecto.setFechaInicio(
                requestDTO.fechaInicio() != null ? requestDTO.fechaInicio() : java.time.LocalDateTime.now());
        proyecto.setFechaFin(requestDTO.fechaFin());
        proyecto.setActivo(requestDTO.activo() != null ? requestDTO.activo() : true);
        proyecto.setResponsable(responsable);

        // Generar código único formato PROJ-XXXXXX
        proyecto.setCodigoRegistro(generarCodigoProyecto());

        return ProyectoResponseDTO.fromEntity(proyectoRepository.save(proyecto));
    }

    @Transactional
    public Optional<ProyectoResponseDTO> update(Long id, ProyectoRequestDTO requestDTO) {
        return proyectoRepository.findById(id)
                .map(existingProyecto -> {
                    // Actualizar responsable si cambia
                    if (requestDTO.idResponsable() != null &&
                            !requestDTO.idResponsable().equals(existingProyecto.getResponsable().getId())) {
                        UsuarioModel newResponsable = usuarioRepository.findById(requestDTO.idResponsable())
                                .orElseThrow(() -> new RuntimeException("Nuevo responsable no encontrado"));
                        existingProyecto.setResponsable(newResponsable);
                    }

                    // Actualizar otros campos
                    if (requestDTO.nombre() != null)
                        existingProyecto.setNombre(requestDTO.nombre());
                    if (requestDTO.descripcion() != null)
                        existingProyecto.setDescripcion(requestDTO.descripcion());
                    if (requestDTO.fechaInicio() != null)
                        existingProyecto.setFechaInicio(requestDTO.fechaInicio());
                    if (requestDTO.fechaFin() != null)
                        existingProyecto.setFechaFin(requestDTO.fechaFin());
                    if (requestDTO.activo() != null)
                        existingProyecto.setActivo(requestDTO.activo());

                    return ProyectoResponseDTO.fromEntity(proyectoRepository.save(existingProyecto));
                });
    }

    @Transactional
    public void deleteById(Long id) {
        proyectoRepository.deleteById(id);
    }

    @Transactional
    public ProyectoResponseDTO toggleActivo(Long id) {
        return proyectoRepository.findById(id)
                .map(proyecto -> {
                    proyecto.setActivo(!proyecto.getActivo());
                    return ProyectoResponseDTO.fromEntity(proyectoRepository.save(proyecto));
                })
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));
    }

    /**
     * Genera un código único para el proyecto en formato PROJ-XXXXXX
     * donde X son caracteres alfanuméricos aleatorios
     */
    private String generarCodigoProyecto() {
        String codigo;
        do {
            codigo = "PROJ-" + generarCodigoAleatorio(6);
        } while (proyectoRepository.findByCodigoRegistro(codigo).isPresent());
        return codigo;
    }

    /**
     * Genera una cadena aleatoria de caracteres alfanuméricos
     */
    private String generarCodigoAleatorio(int longitud) {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder codigo = new StringBuilder();
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < longitud; i++) {
            codigo.append(caracteres.charAt(random.nextInt(caracteres.length())));
        }
        return codigo.toString();
    }
}