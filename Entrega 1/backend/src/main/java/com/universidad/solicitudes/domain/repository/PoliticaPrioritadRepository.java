package com.universidad.solicitudes.domain.repository;

import com.universidad.solicitudes.domain.model.PoliticaPrioridad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PoliticaPrioritadRepository extends JpaRepository<PoliticaPrioridad, UUID> {
    List<PoliticaPrioridad> findByActivaTrue();
}
