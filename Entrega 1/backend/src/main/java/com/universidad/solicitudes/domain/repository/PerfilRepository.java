package com.universidad.solicitudes.domain.repository;

import com.universidad.solicitudes.domain.model.Perfil;
import com.universidad.solicitudes.domain.model.enums.TipoPerfil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PerfilRepository extends JpaRepository<Perfil, UUID> {
    Optional<Perfil> findByTipoPerfil(TipoPerfil tipoPerfil);
}
