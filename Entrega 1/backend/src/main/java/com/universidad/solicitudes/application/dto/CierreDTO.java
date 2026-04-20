package com.universidad.solicitudes.application.dto;

import jakarta.validation.constraints.NotBlank;

public record CierreDTO(
        @NotBlank(message = "La justificación de cierre es obligatoria")
        String justificacion
) {}
