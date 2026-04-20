package com.universidad.solicitudes.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Asignación de gestor a una solicitud (RF-05).
 * Solo una asignación puede estar activa por solicitud.
 */
@Entity
@Table(name = "asignaciones_gestor")
public class AsignacionGestor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitud_id", nullable = false)
    private SolicitudAcademica solicitud;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gestor_id", nullable = false)
    private CuentaUsuario gestor;

    @Column(nullable = false)
    private LocalDateTime fechaAsignacion;

    private boolean activa;

    protected AsignacionGestor() {
        // JPA
    }

    public AsignacionGestor(SolicitudAcademica solicitud, CuentaUsuario gestor) {
        this.solicitud = solicitud;
        this.gestor = gestor;
        this.fechaAsignacion = LocalDateTime.now();
        this.activa = true;
    }

    // ─── Getters & Setters ───────────────────────────────────────

    public UUID getId() { return id; }
    public SolicitudAcademica getSolicitud() { return solicitud; }
    public CuentaUsuario getGestor() { return gestor; }
    public LocalDateTime getFechaAsignacion() { return fechaAsignacion; }
    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }
}
