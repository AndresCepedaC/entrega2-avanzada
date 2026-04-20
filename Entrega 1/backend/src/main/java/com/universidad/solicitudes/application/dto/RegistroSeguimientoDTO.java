package com.universidad.solicitudes.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record RegistroSeguimientoDTO(
        UUID id,
        UUID solicitudId,
        LocalDateTime fecha,
        String comentario,
        String accion,
        UUID usuarioId,
        String usuarioNombre
) {}
