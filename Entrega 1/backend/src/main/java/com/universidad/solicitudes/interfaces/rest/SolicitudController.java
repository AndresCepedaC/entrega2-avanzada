package com.universidad.solicitudes.interfaces.rest;

import com.universidad.solicitudes.application.dto.*;
import com.universidad.solicitudes.application.service.SolicitudService;
import com.universidad.solicitudes.domain.model.enums.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controlador REST para el ciclo de vida de solicitudes académicas.
 * Implementa los endpoints del contrato OpenAPI (RF-01 a RF-08, RF-12).
 */
@RestController
@RequestMapping("/solicitudes")
@CrossOrigin(origins = "*")
public class SolicitudController {

    private final SolicitudService solicitudService;

    public SolicitudController(SolicitudService solicitudService) {
        this.solicitudService = solicitudService;
    }

    // ─── RF-01: Registro ─────────────────────────────────────────

    @PostMapping
    public ResponseEntity<SolicitudAcademicaDTO> crear(@Valid @RequestBody SolicitudCrearDTO dto) {
        SolicitudAcademicaDTO resultado = solicitudService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }

    // ─── RF-07: Consulta ─────────────────────────────────────────

    @GetMapping
    public ResponseEntity<List<SolicitudAcademicaDTO>> listar(
            @RequestParam(required = false) EstadoSolicitud estado,
            @RequestParam(required = false) NivelPrioridad prioridad,
            @RequestParam(required = false) TipoSolicitud tipo,
            @RequestParam(required = false) CategoriaSolicitud categoria) {
        List<SolicitudAcademicaDTO> solicitudes = solicitudService.listar(estado, prioridad, tipo, categoria);
        return ResponseEntity.ok(solicitudes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SolicitudAcademicaDTO> obtenerPorId(@PathVariable UUID id) {
        SolicitudAcademicaDTO solicitud = solicitudService.obtenerPorId(id);
        return ResponseEntity.ok(solicitud);
    }

    @GetMapping("/gestor/{gestorId}")
    public ResponseEntity<List<SolicitudAcademicaDTO>> listarPorGestor(@PathVariable UUID gestorId) {
        List<SolicitudAcademicaDTO> solicitudes = solicitudService.listarPorGestor(gestorId);
        return ResponseEntity.ok(solicitudes);
    }

    // ─── RF-02, RF-03: Clasificación ─────────────────────────────

    @PostMapping("/{id}/clasificar")
    public ResponseEntity<SolicitudAcademicaDTO> clasificar(
            @PathVariable UUID id,
            @Valid @RequestBody ClasificacionInputDTO dto) {
        SolicitudAcademicaDTO resultado = solicitudService.clasificar(id, dto);
        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/{id}/reclasificar")
    public ResponseEntity<SolicitudAcademicaDTO> reclasificar(
            @PathVariable UUID id,
            @Valid @RequestBody ClasificacionInputDTO dto) {
        SolicitudAcademicaDTO resultado = solicitudService.reclasificar(id, dto);
        return ResponseEntity.ok(resultado);
    }

    // ─── RF-05: Asignación ───────────────────────────────────────

    @PostMapping("/{id}/asignar-gestor")
    public ResponseEntity<SolicitudAcademicaDTO> asignarGestor(
            @PathVariable UUID id,
            @RequestBody AsignarGestorDTO dto) {
        SolicitudAcademicaDTO resultado = solicitudService.asignarGestor(id, dto);
        return ResponseEntity.ok(resultado);
    }

    // ─── Rechazar ────────────────────────────────────────────────

    @PostMapping("/{id}/rechazar")
    public ResponseEntity<SolicitudAcademicaDTO> rechazar(
            @PathVariable UUID id,
            @RequestBody ComentarioDTO dto) {
        SolicitudAcademicaDTO resultado = solicitudService.rechazar(id, dto);
        return ResponseEntity.ok(resultado);
    }

    // ─── Solución ────────────────────────────────────────────────

    @PostMapping("/{id}/registrar-solucion")
    public ResponseEntity<SolicitudAcademicaDTO> registrarSolucion(
            @PathVariable UUID id,
            @RequestBody SolucionDTO dto) {
        SolicitudAcademicaDTO resultado = solicitudService.registrarSolucion(id, dto);
        return ResponseEntity.ok(resultado);
    }

    // ─── Cancelar ────────────────────────────────────────────────

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<SolicitudAcademicaDTO> cancelar(
            @PathVariable UUID id,
            @RequestBody(required = false) ComentarioDTO dto) {
        SolicitudAcademicaDTO resultado = solicitudService.cancelar(id,
                dto != null ? dto : new ComentarioDTO(null));
        return ResponseEntity.ok(resultado);
    }

    // ─── RF-08: Cierre ───────────────────────────────────────────

    @PostMapping("/{id}/cerrar")
    public ResponseEntity<SolicitudAcademicaDTO> cerrar(
            @PathVariable UUID id,
            @Valid @RequestBody CierreDTO dto) {
        SolicitudAcademicaDTO resultado = solicitudService.cerrar(id, dto);
        return ResponseEntity.ok(resultado);
    }

    // ─── Reabrir ─────────────────────────────────────────────────

    @PostMapping("/{id}/reabrir")
    public ResponseEntity<SolicitudAcademicaDTO> reabrir(
            @PathVariable UUID id,
            @RequestBody ComentarioDTO dto) {
        SolicitudAcademicaDTO resultado = solicitudService.reabrir(id, dto);
        return ResponseEntity.ok(resultado);
    }

    // ─── RF-06: Seguimiento ──────────────────────────────────────

    @GetMapping("/{id}/seguimientos")
    public ResponseEntity<List<RegistroSeguimientoDTO>> obtenerSeguimientos(@PathVariable UUID id) {
        List<RegistroSeguimientoDTO> seguimientos = solicitudService.obtenerSeguimientos(id);
        return ResponseEntity.ok(seguimientos);
    }

    @PostMapping("/{id}/seguimientos")
    public ResponseEntity<RegistroSeguimientoDTO> agregarSeguimiento(
            @PathVariable UUID id,
            @Valid @RequestBody RegistroSeguimientoCrearDTO dto) {
        RegistroSeguimientoDTO resultado = solicitudService.agregarSeguimiento(id, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }

    // ─── Asignaciones ────────────────────────────────────────────

    @GetMapping("/{id}/asignaciones")
    public ResponseEntity<List<AsignacionGestorDTO>> obtenerAsignaciones(@PathVariable UUID id) {
        List<AsignacionGestorDTO> asignaciones = solicitudService.obtenerAsignaciones(id);
        return ResponseEntity.ok(asignaciones);
    }
}
