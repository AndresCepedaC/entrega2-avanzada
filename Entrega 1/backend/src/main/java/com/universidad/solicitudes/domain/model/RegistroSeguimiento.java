package com.universidad.solicitudes.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Registro de seguimiento — Trazabilidad obligatoria (RF-06).
 * Cada transición de estado y acción relevante genera un registro.
 */
@Entity
@Table(name = "registros_seguimiento")
public class RegistroSeguimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitud_id", nullable = false)
    private SolicitudAcademica solicitud;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false, length = 2000)
    private String comentario;

    @Column(nullable = false, length = 100)
    private String accion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private CuentaUsuario usuario;

    protected RegistroSeguimiento() {
        // JPA
    }

    public RegistroSeguimiento(SolicitudAcademica solicitud, String comentario,
                                CuentaUsuario usuario, String accion) {
        this.solicitud = solicitud;
        this.comentario = comentario;
        this.usuario = usuario;
        this.accion = accion;
        this.fecha = LocalDateTime.now();
    }

    // ─── Getters ─────────────────────────────────────────────────

    public UUID getId() { return id; }
    public SolicitudAcademica getSolicitud() { return solicitud; }
    public LocalDateTime getFecha() { return fecha; }
    public String getComentario() { return comentario; }
    public String getAccion() { return accion; }
    public CuentaUsuario getUsuario() { return usuario; }
}
