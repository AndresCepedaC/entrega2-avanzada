package com.universidad.solicitudes.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record AsignacionGestorDTO(
        UUID id,
        UUID solicitudId,
        UUID gestorId,
        String gestorNombre,
        LocalDateTime fechaAsignacion,
        boolean activa
) {}
