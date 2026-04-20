package com.universidad.solicitudes.domain.repository;

import com.universidad.solicitudes.domain.model.AsignacionGestor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AsignacionGestorRepository extends JpaRepository<AsignacionGestor, UUID> {
    List<AsignacionGestor> findBySolicitudIdOrderByFechaAsignacionDesc(UUID solicitudId);
}
