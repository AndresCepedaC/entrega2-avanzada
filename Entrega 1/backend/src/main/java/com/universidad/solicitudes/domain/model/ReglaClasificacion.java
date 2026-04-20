package com.universidad.solicitudes.domain.model;

import jakarta.persistence.*;
import java.util.UUID;

/**
 * Regla de clasificación — Motor de decisiones.
 * Permite evaluar condiciones sobre solicitudes para sugerir clasificación.
 */
@Entity
@Table(name = "reglas_clasificacion")
public class ReglaClasificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 200)
    private String nombre;

    private boolean activa;

    @Column(length = 500)
    private String condicion;

    @Column(length = 500)
    private String categoriaResultante;

    protected ReglaClasificacion() {}

    public ReglaClasificacion(String nombre, String condicion, String categoriaResultante) {
        this.nombre = nombre;
        this.condicion = condicion;
        this.categoriaResultante = categoriaResultante;
        this.activa = true;
    }

    public UUID getId() { return id; }
    public String getNombre() { return nombre; }
    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }
    public String getCondicion() { return condicion; }
    public String getCategoriaResultante() { return categoriaResultante; }
}
