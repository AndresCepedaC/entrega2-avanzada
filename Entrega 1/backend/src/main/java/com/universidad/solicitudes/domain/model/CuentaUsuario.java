package com.universidad.solicitudes.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Cuenta de usuario del sistema (RF-13).
 * Cada usuario tiene un perfil institucional que determina sus permisos.
 */
@Entity
@Table(name = "cuentas_usuario")
public class CuentaUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, length = 150)
    private String nombreCompleto;

    private boolean activo;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "perfil_id", nullable = false)
    private Perfil perfil;

    protected CuentaUsuario() {
        // JPA
    }

    public CuentaUsuario(String email, String nombreCompleto, Perfil perfil) {
        this.email = email;
        this.nombreCompleto = nombreCompleto;
        this.perfil = perfil;
        this.activo = true;
        this.fechaCreacion = LocalDateTime.now();
    }

    // ─── Getters & Setters ───────────────────────────────────────

    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public Perfil getPerfil() { return perfil; }
    public void setPerfil(Perfil perfil) { this.perfil = perfil; }
}
