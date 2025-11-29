package com.administrador_proyectos.backend_api.modelo.repositorio;

import com.administrador_proyectos.backend_api.modelo.entidad.RolModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolRepository extends JpaRepository<RolModel, Long> {}