package com.universidad.solicitudes.application.dto;

import com.universidad.solicitudes.domain.model.enums.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de respuesta para SolicitudAcademica.
 */
public record SolicitudAcademicaDTO(
        UUID id,
        String descripcion,
        EstadoSolicitud estado,
        TipoSolicitud tipoSolicitud,
        NivelPrioridad nivelPrioridad,
        String justificacionPrioridad,
        CategoriaSolicitud categoria,
        MedioIngreso medioIngreso,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion,
        String descripcionSolucion,
        String observacionCierre,
        UUID solicitanteId,
        String solicitanteNombre,
        UUID asignacionActivaId,
        UUID gestorActivoId,
        String gestorActivoNombre
) {}
