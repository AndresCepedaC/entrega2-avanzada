package com.universidad.solicitudes.domain.model;

import com.universidad.solicitudes.domain.model.enums.TipoPerfil;
import jakarta.persistence.*;
import java.util.UUID;

/**
 * Perfil institucional — Determina el rol funcional del usuario (RF-13).
 */
@Entity
@Table(name = "perfiles")
public class Perfil {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPerfil tipoPerfil;

    @Column(nullable = false, length = 100)
    private String nombre;

    protected Perfil() {
        // JPA
    }

    public Perfil(TipoPerfil tipoPerfil, String nombre) {
        this.tipoPerfil = tipoPerfil;
        this.nombre = nombre;
    }

    // ─── Getters ─────────────────────────────────────────────────

    public UUID getId() { return id; }
    public TipoPerfil getTipoPerfil() { return tipoPerfil; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
