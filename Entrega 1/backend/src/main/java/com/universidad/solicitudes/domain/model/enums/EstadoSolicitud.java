package com.universidad.solicitudes.domain.model.enums;

/**
 * Ciclo de vida de la solicitud académica.
 * Terminales: CERRADA, RECHAZADA, CANCELADA.
 * Happy path: REGISTRADA → CLASIFICADA → EN_ATENCION → ATENDIDA → CERRADA.
 */
public enum EstadoSolicitud {
    REGISTRADA,
    CLASIFICADA,
    EN_ATENCION,
    ATENDIDA,
    CERRADA,
    RECHAZADA,
    CANCELADA;

    public boolean esTerminal() {
        return this == CERRADA || this == RECHAZADA || this == CANCELADA;
    }
}
