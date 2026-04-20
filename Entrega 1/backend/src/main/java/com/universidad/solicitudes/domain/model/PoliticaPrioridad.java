package com.universidad.solicitudes.domain.model;

import com.universidad.solicitudes.domain.model.enums.NivelPrioridad;
import jakarta.persistence.*;
import java.util.UUID;

/**
 * Política de prioridad — Motor de decisiones (RF-03).
 * Define reglas configurables para recalcular prioridades.
 */
@Entity
@Table(name = "politicas_prioridad")
public class PoliticaPrioridad {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 200)
    private String nombre;

    private boolean activa;

    @Column(length = 500)
    private String condicion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NivelPrioridad nivelResultante;

    protected PoliticaPrioridad() {}

    public PoliticaPrioridad(String nombre, String condicion, NivelPrioridad nivelResultante) {
        this.nombre = nombre;
        this.condicion = condicion;
        this.nivelResultante = nivelResultante;
        this.activa = true;
    }

    public UUID getId() { return id; }
    public String getNombre() { return nombre; }
    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }
    public String getCondicion() { return condicion; }
    public NivelPrioridad getNivelResultante() { return nivelResultante; }
}
