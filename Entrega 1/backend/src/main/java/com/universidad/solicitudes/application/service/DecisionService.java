package com.universidad.solicitudes.application.service;

import com.universidad.solicitudes.domain.model.*;
import com.universidad.solicitudes.domain.model.enums.*;
import com.universidad.solicitudes.domain.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Motor de decisiones — Clasificación y priorización (RF-03).
 * Evalúa reglas activas y políticas para sugerir o recalcular prioridades.
 */
@Service
@Transactional(readOnly = true)
public class DecisionService {

    private final ReglaClasificacionRepository reglaRepo;
    private final PoliticaPrioritadRepository politicaRepo;
    private final SolicitudAcademicaRepository solicitudRepo;

    public DecisionService(ReglaClasificacionRepository reglaRepo,
                           PoliticaPrioritadRepository politicaRepo,
                           SolicitudAcademicaRepository solicitudRepo) {
        this.reglaRepo = reglaRepo;
        this.politicaRepo = politicaRepo;
        this.solicitudRepo = solicitudRepo;
    }

    public List<ReglaClasificacion> listarReglasActivas() {
        return reglaRepo.findByActivaTrue();
    }

    public List<PoliticaPrioridad> listarPoliticasActivas() {
        return politicaRepo.findByActivaTrue();
    }

    public List<ReglaClasificacion> listarTodasLasReglas() {
        return reglaRepo.findAll();
    }

    public List<PoliticaPrioridad> listarTodasLasPoliticas() {
        return politicaRepo.findAll();
    }

    /**
     * Recalcula la prioridad de una solicitud evaluando las políticas activas.
     * Implementa un motor de reglas simple basado en tipo de solicitud y categoría.
     */
    @Transactional
    public NivelPrioridad recalcularPrioridad(UUID solicitudId) {
        SolicitudAcademica solicitud = solicitudRepo.findById(solicitudId)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                        "Solicitud no encontrada: " + solicitudId));

        List<PoliticaPrioridad> politicas = politicaRepo.findByActivaTrue();
        NivelPrioridad prioridadCalculada = evaluarPoliticas(solicitud, politicas);

        solicitud.recalcularPrioridad(prioridadCalculada,
                "Prioridad recalculada automáticamente por el motor de decisiones");
        solicitudRepo.save(solicitud);

        return prioridadCalculada;
    }

    /**
     * Motor de reglas simple: evalúa políticas activas contra la solicitud.
     * En producción, se podría integrar un motor como Drools.
     */
    private NivelPrioridad evaluarPoliticas(SolicitudAcademica solicitud,
                                             List<PoliticaPrioridad> politicas) {
        NivelPrioridad mayorPrioridad = solicitud.getNivelPrioridad() != null
                ? solicitud.getNivelPrioridad()
                : NivelPrioridad.BAJA;

        for (PoliticaPrioridad politica : politicas) {
            if (evaluarCondicion(politica.getCondicion(), solicitud)) {
                if (compararPrioridad(politica.getNivelResultante(), mayorPrioridad) > 0) {
                    mayorPrioridad = politica.getNivelResultante();
                }
            }
        }

        return mayorPrioridad;
    }

    /**
     * Evaluador de condiciones simple basado en el contenido de la solicitud.
     * Soporta condiciones como:
     *   "TIPO=HOMOLOGACION"
     *   "CATEGORIA=ACADEMICO"
     *   "DESCRIPCION_CONTIENE=urgente"
     */
    private boolean evaluarCondicion(String condicion, SolicitudAcademica solicitud) {
        if (condicion == null || condicion.isBlank()) return true;

        String[] partes = condicion.split("=", 2);
        if (partes.length != 2) return false;

        String campo = partes[0].trim().toUpperCase();
        String valor = partes[1].trim().toUpperCase();

        return switch (campo) {
            case "TIPO" -> solicitud.getTipoSolicitud() != null &&
                    solicitud.getTipoSolicitud().name().equals(valor);
            case "CATEGORIA" -> solicitud.getCategoria() != null &&
                    solicitud.getCategoria().name().equals(valor);
            case "DESCRIPCION_CONTIENE" -> solicitud.getDescripcion() != null &&
                    solicitud.getDescripcion().toUpperCase().contains(valor);
            case "MEDIO" -> solicitud.getMedioIngreso() != null &&
                    solicitud.getMedioIngreso().name().equals(valor);
            default -> false;
        };
    }

    private int compararPrioridad(NivelPrioridad a, NivelPrioridad b) {
        return Integer.compare(a.ordinal(), b.ordinal());
    }
}
