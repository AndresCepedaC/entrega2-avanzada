package com.universidad.solicitudes.domain.model;

import com.universidad.solicitudes.domain.model.enums.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entidad raíz del dominio — Solicitud Académica.
 * Controla el ciclo de vida completo mediante su máquina de estados interna.
 * Cada transición de estado genera un RegistroSeguimiento obligatorio.
 */
@Entity
@Table(name = "solicitudes_academicas")
public class SolicitudAcademica {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 2000)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoSolicitud estado;

    @Enumerated(EnumType.STRING)
    private TipoSolicitud tipoSolicitud;

    @Enumerated(EnumType.STRING)
    private NivelPrioridad nivelPrioridad;

    @Column(length = 500)
    private String justificacionPrioridad;

    @Enumerated(EnumType.STRING)
    private CategoriaSolicitud categoria;

    @Enumerated(EnumType.STRING)
    private MedioIngreso medioIngreso;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaActualizacion;

    @Column(length = 2000)
    private String descripcionSolucion;

    @Column(length = 1000)
    private String observacionCierre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitante_id")
    private CuentaUsuario solicitante;

    @OneToMany(mappedBy = "solicitud", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("fecha DESC")
    private List<RegistroSeguimiento> seguimientos = new ArrayList<>();

    @OneToMany(mappedBy = "solicitud", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("fechaAsignacion DESC")
    private List<AsignacionGestor> asignaciones = new ArrayList<>();

    // ─── Constructores ───────────────────────────────────────────

    protected SolicitudAcademica() {
        // JPA
    }

    public SolicitudAcademica(String descripcion, MedioIngreso medioIngreso, CuentaUsuario solicitante) {
        this.descripcion = descripcion;
        this.medioIngreso = medioIngreso;
        this.solicitante = solicitante;
        this.estado = EstadoSolicitud.REGISTRADA;
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = this.fechaCreacion;
    }

    // ─── Lógica de dominio: Transiciones de estado ───────────────

    /**
     * REGISTRADA → CLASIFICADA (RF-02, RF-03)
     */
    public void clasificar(TipoSolicitud tipo, CategoriaSolicitud categoria,
                           NivelPrioridad prioridad, String justificacion) {
        validarTransicion(EstadoSolicitud.REGISTRADA, "clasificar");
        this.tipoSolicitud = tipo;
        this.categoria = categoria;
        this.nivelPrioridad = prioridad;
        this.justificacionPrioridad = justificacion;
        cambiarEstado(EstadoSolicitud.CLASIFICADA);
    }

    /**
     * CLASIFICADA → EN_ATENCION (RF-05)
     */
    public AsignacionGestor asignarGestor(CuentaUsuario gestor) {
        validarTransicion(EstadoSolicitud.CLASIFICADA, "asignar gestor");
        if (!gestor.isActivo()) {
            throw new IllegalStateException("El gestor no está activo");
        }

        // Desactivar asignaciones previas
        asignaciones.forEach(a -> a.setActiva(false));

        AsignacionGestor asignacion = new AsignacionGestor(this, gestor);
        asignaciones.add(asignacion);
        cambiarEstado(EstadoSolicitud.EN_ATENCION);
        return asignacion;
    }

    /**
     * CLASIFICADA → RECHAZADA (terminal)
     */
    public void rechazar() {
        validarTransicion(EstadoSolicitud.CLASIFICADA, "rechazar");
        cambiarEstado(EstadoSolicitud.RECHAZADA);
    }

    /**
     * EN_ATENCION → ATENDIDA (RF-04)
     */
    public void registrarSolucion(String solucion) {
        validarTransicion(EstadoSolicitud.EN_ATENCION, "registrar solución");
        this.descripcionSolucion = solucion;
        cambiarEstado(EstadoSolicitud.ATENDIDA);
    }

    /**
     * EN_ATENCION → CLASIFICADA (reclasificación)
     */
    public void reclasificar(TipoSolicitud tipo, CategoriaSolicitud categoria,
                             NivelPrioridad prioridad, String justificacion) {
        validarTransicion(EstadoSolicitud.EN_ATENCION, "reclasificar");
        // Desactivar asignación actual
        asignaciones.stream().filter(AsignacionGestor::isActiva)
                .forEach(a -> a.setActiva(false));
        this.tipoSolicitud = tipo;
        this.categoria = categoria;
        this.nivelPrioridad = prioridad;
        this.justificacionPrioridad = justificacion;
        cambiarEstado(EstadoSolicitud.CLASIFICADA);
    }

    /**
     * REGISTRADA | EN_ATENCION → CANCELADA (terminal)
     */
    public void cancelar() {
        if (estado != EstadoSolicitud.REGISTRADA && estado != EstadoSolicitud.EN_ATENCION) {
            throw new IllegalStateException(
                    "No se puede cancelar desde el estado " + estado +
                    ". Solo permitido desde REGISTRADA o EN_ATENCION");
        }
        cambiarEstado(EstadoSolicitud.CANCELADA);
    }

    /**
     * ATENDIDA → CERRADA (terminal, RF-08)
     */
    public void cerrar(String observacion) {
        validarTransicion(EstadoSolicitud.ATENDIDA, "cerrar");
        this.observacionCierre = observacion;
        cambiarEstado(EstadoSolicitud.CERRADA);
    }

    /**
     * ATENDIDA → EN_ATENCION (reapertura)
     */
    public void reabrir() {
        validarTransicion(EstadoSolicitud.ATENDIDA, "reabrir");
        this.descripcionSolucion = null;
        cambiarEstado(EstadoSolicitud.EN_ATENCION);
    }

    /**
     * Recalcular prioridad sin cambiar estado.
     */
    public void recalcularPrioridad(NivelPrioridad nuevaPrioridad, String justificacion) {
        if (estado.esTerminal()) {
            throw new IllegalStateException("No se puede recalcular prioridad en estado terminal");
        }
        this.nivelPrioridad = nuevaPrioridad;
        this.justificacionPrioridad = justificacion;
        this.fechaActualizacion = LocalDateTime.now();
    }

    // ─── Seguimiento (RF-06) ─────────────────────────────────────

    public RegistroSeguimiento agregarSeguimiento(String comentario, CuentaUsuario usuario, String accion) {
        RegistroSeguimiento registro = new RegistroSeguimiento(this, comentario, usuario, accion);
        seguimientos.add(registro);
        return registro;
    }

    // ─── Helpers internos ────────────────────────────────────────

    private void validarTransicion(EstadoSolicitud estadoRequerido, String accion) {
        if (this.estado != estadoRequerido) {
            throw new IllegalStateException(
                    "No se puede " + accion + " desde el estado " + this.estado +
                    ". Estado requerido: " + estadoRequerido);
        }
    }

    private void cambiarEstado(EstadoSolicitud nuevoEstado) {
        this.estado = nuevoEstado;
        this.fechaActualizacion = LocalDateTime.now();
    }

    // ─── Consultas ───────────────────────────────────────────────

    public AsignacionGestor getAsignacionActiva() {
        return asignaciones.stream()
                .filter(AsignacionGestor::isActiva)
                .findFirst()
                .orElse(null);
    }

    // ─── Getters & Setters ───────────────────────────────────────

    public UUID getId() { return id; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public EstadoSolicitud getEstado() { return estado; }

    public TipoSolicitud getTipoSolicitud() { return tipoSolicitud; }

    public NivelPrioridad getNivelPrioridad() { return nivelPrioridad; }

    public String getJustificacionPrioridad() { return justificacionPrioridad; }

    public CategoriaSolicitud getCategoria() { return categoria; }

    public MedioIngreso getMedioIngreso() { return medioIngreso; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }

    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }

    public String getDescripcionSolucion() { return descripcionSolucion; }

    public String getObservacionCierre() { return observacionCierre; }

    public CuentaUsuario getSolicitante() { return solicitante; }

    public List<RegistroSeguimiento> getSeguimientos() { return seguimientos; }

    public List<AsignacionGestor> getAsignaciones() { return asignaciones; }
}
