package com.universidad.solicitudes.application.dto;

import com.universidad.solicitudes.domain.model.enums.MedioIngreso;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * DTO para crear una nueva solicitud académica (RF-01).
 */
public record SolicitudCrearDTO(
        @NotBlank(message = "La descripción es obligatoria")
        @Size(max = 2000, message = "La descripción no puede exceder 2000 caracteres")
        String descripcion,

        MedioIngreso medioIngreso,

        UUID solicitanteId
) {}
