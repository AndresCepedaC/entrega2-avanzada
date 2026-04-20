package com.universidad.solicitudes.application.dto;

import com.universidad.solicitudes.domain.model.enums.*;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para clasificar/reclasificar una solicitud (RF-02, RF-03).
 */
public record ClasificacionInputDTO(
        @NotNull(message = "El tipo de solicitud es obligatorio")
        TipoSolicitud tipoSolicitud,

        @NotNull(message = "La categoría es obligatoria")
        CategoriaSolicitud categoria,

        @NotNull(message = "El nivel de prioridad es obligatorio")
        NivelPrioridad nivelPrioridad,

        String justificacionPrioridad
) {}
