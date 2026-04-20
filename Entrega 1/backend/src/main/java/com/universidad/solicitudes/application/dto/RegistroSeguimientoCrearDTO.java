package com.universidad.solicitudes.application.dto;

import jakarta.validation.constraints.NotBlank;

public record RegistroSeguimientoCrearDTO(
        @NotBlank(message = "El comentario es obligatorio")
        String comentario
) {}
