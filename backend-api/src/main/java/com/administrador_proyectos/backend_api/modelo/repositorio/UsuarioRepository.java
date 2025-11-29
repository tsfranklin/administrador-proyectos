package com.administrador_proyectos.backend_api.modelo.repositorio;

import com.administrador_proyectos.backend_api.modelo.entidad.UsuarioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioModel, Long> {

    Optional<UsuarioModel> findByEmail(String email);

    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END " +
            "FROM TareaModel t WHERE t.asignado.id = :usuarioId")
    boolean existsByAssignedTasks(@Param("usuarioId") Long usuarioId);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
            "FROM ProyectoModel p WHERE p.responsable.id = :usuarioId")
    boolean existsByManagedProjects(@Param("usuarioId") Long usuarioId);
}