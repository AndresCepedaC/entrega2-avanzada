package com.universidad.solicitudes.application.dto;

import com.universidad.solicitudes.domain.model.enums.TipoPerfil;
import java.time.LocalDateTime;
import java.util.UUID;

public record CuentaUsuarioDTO(
        UUID id,
        String email,
        String nombreCompleto,
        boolean activo,
        LocalDateTime fechaCreacion,
        UUID perfilId,
        TipoPerfil tipoPerfil,
        String perfilNombre
) {}
