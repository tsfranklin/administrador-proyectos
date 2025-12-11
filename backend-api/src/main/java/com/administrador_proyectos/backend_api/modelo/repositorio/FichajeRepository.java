package com.administrador_proyectos.backend_api.modelo.repositorio;

import com.administrador_proyectos.backend_api.modelo.entidad.FichajeModel;
import com.administrador_proyectos.backend_api.modelo.entidad.UsuarioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FichajeRepository extends JpaRepository<FichajeModel, Long> {

    /**
     * Buscar todos los fichajes de un usuario
     */
    List<FichajeModel> findByUsuarioOrderByFechaHoraEntradaDesc(UsuarioModel usuario);

    /**
     * Buscar el fichaje activo (sin salida) de un usuario
     */
    @Query("SELECT f FROM FichajeModel f WHERE f.usuario.id = :usuarioId AND f.fechaHoraSalida IS NULL")
    Optional<FichajeModel> findFichajeActivoByUsuarioId(@Param("usuarioId") Long usuarioId);

    /**
     * Buscar fichajes de un usuario por ID
     */
    @Query("SELECT f FROM FichajeModel f WHERE f.usuario.id = :usuarioId ORDER BY f.fechaHoraEntrada DESC")
    List<FichajeModel> findByUsuarioId(@Param("usuarioId") Long usuarioId);
}
