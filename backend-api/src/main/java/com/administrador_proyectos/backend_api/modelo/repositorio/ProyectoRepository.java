package com.administrador_proyectos.backend_api.modelo.repositorio;

import com.administrador_proyectos.backend_api.modelo.entidad.ProyectoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProyectoRepository extends JpaRepository<ProyectoModel, Long> {

    List<ProyectoModel> findByActivo(boolean activo);

    @Query("SELECT p FROM ProyectoModel p WHERE p.responsable.id = :responsableId")
    List<ProyectoModel> findByResponsableId(@Param("responsableId") Long responsableId);
}