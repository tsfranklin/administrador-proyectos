package com.administrador_proyectos.backend_api.modelo.repositorio;

import com.administrador_proyectos.backend_api.modelo.entidad.TareaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TareaRepository extends JpaRepository<TareaModel, Long> {

    @Query("SELECT t FROM TareaModel t WHERE t.proyecto.id = :idProyecto")
    List<TareaModel> findByProyectoId(@Param("idProyecto") Long idProyecto);

    @Query("SELECT t FROM TareaModel t WHERE t.asignado.id = :idUsuario")
    List<TareaModel> findByAsignadoId(@Param("idUsuario") Long idUsuario);

    List<TareaModel> findByEstado(String estado);
    List<TareaModel> findByPrioridad(String prioridad);
}