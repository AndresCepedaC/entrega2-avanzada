package com.universidad.solicitudes.application.mapper;

import com.universidad.solicitudes.application.dto.*;
import com.universidad.solicitudes.domain.model.*;
import org.springframework.stereotype.Component;

/**
 * Mapper centralizado entre entidades de dominio y DTOs de aplicación.
 * Evita exponer entidades JPA directamente en la capa REST.
 */
@Component
public class DomainMapper {

    // ─── SolicitudAcademica ──────────────────────────────────────

    public SolicitudAcademicaDTO toDTO(SolicitudAcademica s) {
        AsignacionGestor asignacionActiva = s.getAsignacionActiva();
        return new SolicitudAcademicaDTO(
                s.getId(),
                s.getDescripcion(),
                s.getEstado(),
                s.getTipoSolicitud(),
                s.getNivelPrioridad(),
                s.getJustificacionPrioridad(),
                s.getCategoria(),
                s.getMedioIngreso(),
                s.getFechaCreacion(),
                s.getFechaActualizacion(),
                s.getDescripcionSolucion(),
                s.getObservacionCierre(),
                s.getSolicitante() != null ? s.getSolicitante().getId() : null,
                s.getSolicitante() != null ? s.getSolicitante().getNombreCompleto() : null,
                asignacionActiva != null ? asignacionActiva.getId() : null,
                asignacionActiva != null ? asignacionActiva.getGestor().getId() : null,
                asignacionActiva != null ? asignacionActiva.getGestor().getNombreCompleto() : null
        );
    }

    // ─── RegistroSeguimiento ─────────────────────────────────────

    public RegistroSeguimientoDTO toDTO(RegistroSeguimiento r) {
        return new RegistroSeguimientoDTO(
                r.getId(),
                r.getSolicitud().getId(),
                r.getFecha(),
                r.getComentario(),
                r.getAccion(),
                r.getUsuario() != null ? r.getUsuario().getId() : null,
                r.getUsuario() != null ? r.getUsuario().getNombreCompleto() : null
        );
    }

    // ─── AsignacionGestor ────────────────────────────────────────

    public AsignacionGestorDTO toDTO(AsignacionGestor a) {
        return new AsignacionGestorDTO(
                a.getId(),
                a.getSolicitud().getId(),
                a.getGestor().getId(),
                a.getGestor().getNombreCompleto(),
                a.getFechaAsignacion(),
                a.isActiva()
        );
    }

    // ─── CuentaUsuario ───────────────────────────────────────────

    public CuentaUsuarioDTO toDTO(CuentaUsuario u) {
        return new CuentaUsuarioDTO(
                u.getId(),
                u.getEmail(),
                u.getNombreCompleto(),
                u.isActivo(),
                u.getFechaCreacion(),
                u.getPerfil().getId(),
                u.getPerfil().getTipoPerfil(),
                u.getPerfil().getNombre()
        );
    }
}
