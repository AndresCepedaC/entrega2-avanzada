package com.universidad.solicitudes.application.dto;

import java.util.UUID;

/**
 * DTO para asignar un gestor a una solicitud (RF-05).
 */
public record AsignarGestorDTO(
        UUID gestorId
) {}
