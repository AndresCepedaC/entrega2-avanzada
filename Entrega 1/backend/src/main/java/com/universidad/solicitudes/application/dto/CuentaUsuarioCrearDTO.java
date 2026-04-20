package com.universidad.solicitudes.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CuentaUsuarioCrearDTO(
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email debe ser válido")
        String email,

        @NotBlank(message = "El nombre completo es obligatorio")
        String nombreCompleto,

        @NotNull(message = "El perfil es obligatorio")
        UUID perfilId
) {}
