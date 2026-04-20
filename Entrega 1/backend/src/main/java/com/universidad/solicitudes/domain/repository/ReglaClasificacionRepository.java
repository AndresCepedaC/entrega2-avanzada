package com.universidad.solicitudes.domain.repository;

import com.universidad.solicitudes.domain.model.ReglaClasificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReglaClasificacionRepository extends JpaRepository<ReglaClasificacion, UUID> {
    List<ReglaClasificacion> findByActivaTrue();
}
