package com.universidad.solicitudes.domain.repository;

import com.universidad.solicitudes.domain.model.CuentaUsuario;
import com.universidad.solicitudes.domain.model.enums.TipoPerfil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CuentaUsuarioRepository extends JpaRepository<CuentaUsuario, UUID> {
    Optional<CuentaUsuario> findByEmail(String email);

    @Query("SELECT u FROM CuentaUsuario u WHERE " +
           "(:activo IS NULL OR u.activo = :activo) AND " +
           "(:tipoPerfil IS NULL OR u.perfil.tipoPerfil = :tipoPerfil)")
    List<CuentaUsuario> findByFiltros(
            @Param("activo") Boolean activo,
            @Param("tipoPerfil") TipoPerfil tipoPerfil);
}
