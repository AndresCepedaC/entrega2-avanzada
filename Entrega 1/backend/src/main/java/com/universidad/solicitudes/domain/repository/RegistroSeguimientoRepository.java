package com.universidad.solicitudes.domain.repository;

import com.universidad.solicitudes.domain.model.RegistroSeguimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RegistroSeguimientoRepository extends JpaRepository<RegistroSeguimiento, UUID> {
    List<RegistroSeguimiento> findBySolicitudIdOrderByFechaDesc(UUID solicitudId);
}
