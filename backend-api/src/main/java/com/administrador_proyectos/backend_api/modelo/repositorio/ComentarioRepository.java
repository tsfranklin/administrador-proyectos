package com.administrador_proyectos.backend_api.modelo.repositorio;

import com.administrador_proyectos.backend_api.modelo.entidad.ComentarioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ComentarioRepository extends JpaRepository<ComentarioModel, Long> {

    // ¡CORREGIDO! Usar el nombre de la propiedad Java (id), no el nombre de columna
    @Query("SELECT c FROM ComentarioModel c WHERE c.tarea.id = :idTarea ORDER BY c.fechaComentario DESC")
    List<ComentarioModel> findByTareaIdOrderByFechaComentarioDesc(@Param("idTarea") Long idTarea);
}