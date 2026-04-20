package com.universidad.solicitudes.domain.repository;

import com.universidad.solicitudes.domain.model.SolicitudAcademica;
import com.universidad.solicitudes.domain.model.enums.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SolicitudAcademicaRepository extends JpaRepository<SolicitudAcademica, UUID> {

    List<SolicitudAcademica> findByEstado(EstadoSolicitud estado);

    List<SolicitudAcademica> findByNivelPrioridad(NivelPrioridad prioridad);

    List<SolicitudAcademica> findByTipoSolicitud(TipoSolicitud tipo);

    List<SolicitudAcademica> findByCategoria(CategoriaSolicitud categoria);

    @Query("SELECT s FROM SolicitudAcademica s WHERE " +
           "(:estado IS NULL OR s.estado = :estado) AND " +
           "(:prioridad IS NULL OR s.nivelPrioridad = :prioridad) AND " +
           "(:tipo IS NULL OR s.tipoSolicitud = :tipo) AND " +
           "(:categoria IS NULL OR s.categoria = :categoria)")
    List<SolicitudAcademica> findByFiltros(
            @Param("estado") EstadoSolicitud estado,
            @Param("prioridad") NivelPrioridad prioridad,
            @Param("tipo") TipoSolicitud tipo,
            @Param("categoria") CategoriaSolicitud categoria);

    @Query("SELECT s FROM SolicitudAcademica s JOIN s.asignaciones a " +
           "WHERE a.gestor.id = :gestorId AND a.activa = true")
    List<SolicitudAcademica> findByGestorActivoId(@Param("gestorId") UUID gestorId);

    List<SolicitudAcademica> findBySolicitanteId(UUID solicitanteId);
}
