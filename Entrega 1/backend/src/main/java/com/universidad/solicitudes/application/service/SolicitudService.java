package com.universidad.solicitudes.application.service;

import com.universidad.solicitudes.application.dto.*;
import com.universidad.solicitudes.application.mapper.DomainMapper;
import com.universidad.solicitudes.domain.model.*;
import com.universidad.solicitudes.domain.model.enums.*;
import com.universidad.solicitudes.domain.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Servicio de aplicación para el ciclo de vida de solicitudes académicas.
 * Orquesta las operaciones de dominio y genera los registros de seguimiento.
 */
@Service
@Transactional
public class SolicitudService {

    private final SolicitudAcademicaRepository solicitudRepo;
    private final CuentaUsuarioRepository usuarioRepo;
    private final RegistroSeguimientoRepository seguimientoRepo;
    private final AsignacionGestorRepository asignacionRepo;
    private final DomainMapper mapper;

    public SolicitudService(SolicitudAcademicaRepository solicitudRepo,
                            CuentaUsuarioRepository usuarioRepo,
                            RegistroSeguimientoRepository seguimientoRepo,
                            AsignacionGestorRepository asignacionRepo,
                            DomainMapper mapper) {
        this.solicitudRepo = solicitudRepo;
        this.usuarioRepo = usuarioRepo;
        this.seguimientoRepo = seguimientoRepo;
        this.asignacionRepo = asignacionRepo;
        this.mapper = mapper;
    }

    // ─── RF-01: Registro de solicitudes ──────────────────────────

    public SolicitudAcademicaDTO crear(SolicitudCrearDTO dto) {
        CuentaUsuario solicitante = null;
        if (dto.solicitanteId() != null) {
            solicitante = buscarUsuario(dto.solicitanteId());
        }

        SolicitudAcademica solicitud = new SolicitudAcademica(
                dto.descripcion(),
                dto.medioIngreso(),
                solicitante
        );

        // RF-06: Registro automático de seguimiento
        solicitud.agregarSeguimiento(
                "Solicitud registrada: " + dto.descripcion(),
                solicitante,
                "CREAR"
        );

        solicitud = solicitudRepo.save(solicitud);
        return mapper.toDTO(solicitud);
    }

    // ─── Consultas (RF-07) ───────────────────────────────────────

    @Transactional(readOnly = true)
    public SolicitudAcademicaDTO obtenerPorId(UUID id) {
        SolicitudAcademica solicitud = buscarSolicitud(id);
        return mapper.toDTO(solicitud);
    }

    @Transactional(readOnly = true)
    public List<SolicitudAcademicaDTO> listar(EstadoSolicitud estado, NivelPrioridad prioridad,
                                               TipoSolicitud tipo, CategoriaSolicitud categoria) {
        List<SolicitudAcademica> solicitudes = solicitudRepo.findByFiltros(
                estado, prioridad, tipo, categoria);
        return solicitudes.stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SolicitudAcademicaDTO> listarPorGestor(UUID gestorId) {
        return solicitudRepo.findByGestorActivoId(gestorId).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    // ─── RF-02, RF-03: Clasificación ─────────────────────────────

    public SolicitudAcademicaDTO clasificar(UUID id, ClasificacionInputDTO dto) {
        SolicitudAcademica solicitud = buscarSolicitud(id);
        solicitud.clasificar(
                dto.tipoSolicitud(),
                dto.categoria(),
                dto.nivelPrioridad(),
                dto.justificacionPrioridad()
        );

        solicitud.agregarSeguimiento(
                "Clasificada como " + dto.tipoSolicitud() + " / " + dto.categoria() +
                " con prioridad " + dto.nivelPrioridad() +
                (dto.justificacionPrioridad() != null ? ". Justificación: " + dto.justificacionPrioridad() : ""),
                null,
                "CLASIFICAR"
        );

        solicitud = solicitudRepo.save(solicitud);
        return mapper.toDTO(solicitud);
    }

    // ─── Reclasificación ─────────────────────────────────────────

    public SolicitudAcademicaDTO reclasificar(UUID id, ClasificacionInputDTO dto) {
        SolicitudAcademica solicitud = buscarSolicitud(id);
        solicitud.reclasificar(
                dto.tipoSolicitud(),
                dto.categoria(),
                dto.nivelPrioridad(),
                dto.justificacionPrioridad()
        );

        solicitud.agregarSeguimiento(
                "Reclasificada a " + dto.tipoSolicitud() + " / " + dto.categoria() +
                " con prioridad " + dto.nivelPrioridad(),
                null,
                "RECLASIFICAR"
        );

        solicitud = solicitudRepo.save(solicitud);
        return mapper.toDTO(solicitud);
    }

    // ─── RF-05: Asignación de responsables ───────────────────────

    public SolicitudAcademicaDTO asignarGestor(UUID solicitudId, AsignarGestorDTO dto) {
        SolicitudAcademica solicitud = buscarSolicitud(solicitudId);
        CuentaUsuario gestor = buscarUsuario(dto.gestorId());

        AsignacionGestor asignacion = solicitud.asignarGestor(gestor);

        solicitud.agregarSeguimiento(
                "Gestor asignado: " + gestor.getNombreCompleto() + " (" + gestor.getEmail() + ")",
                gestor,
                "ASIGNAR_GESTOR"
        );

        solicitud = solicitudRepo.save(solicitud);
        return mapper.toDTO(solicitud);
    }

    // ─── Rechazar ────────────────────────────────────────────────

    public SolicitudAcademicaDTO rechazar(UUID id, ComentarioDTO dto) {
        SolicitudAcademica solicitud = buscarSolicitud(id);
        solicitud.rechazar();

        solicitud.agregarSeguimiento(
                "Solicitud rechazada: " + (dto.comentario() != null ? dto.comentario() : "Sin comentario"),
                null,
                "RECHAZAR"
        );

        solicitud = solicitudRepo.save(solicitud);
        return mapper.toDTO(solicitud);
    }

    // ─── Registrar solución ──────────────────────────────────────

    public SolicitudAcademicaDTO registrarSolucion(UUID id, SolucionDTO dto) {
        SolicitudAcademica solicitud = buscarSolicitud(id);
        solicitud.registrarSolucion(dto.descripcionSolucion());

        solicitud.agregarSeguimiento(
                "Solución registrada: " + (dto.comentario() != null ? dto.comentario() : dto.descripcionSolucion()),
                null,
                "REGISTRAR_SOLUCION"
        );

        solicitud = solicitudRepo.save(solicitud);
        return mapper.toDTO(solicitud);
    }

    // ─── Cancelar ────────────────────────────────────────────────

    public SolicitudAcademicaDTO cancelar(UUID id, ComentarioDTO dto) {
        SolicitudAcademica solicitud = buscarSolicitud(id);
        solicitud.cancelar();

        solicitud.agregarSeguimiento(
                "Solicitud cancelada: " + (dto != null && dto.comentario() != null ? dto.comentario() : "Sin comentario"),
                null,
                "CANCELAR"
        );

        solicitud = solicitudRepo.save(solicitud);
        return mapper.toDTO(solicitud);
    }

    // ─── RF-08: Cierre de solicitudes ────────────────────────────

    public SolicitudAcademicaDTO cerrar(UUID id, CierreDTO dto) {
        SolicitudAcademica solicitud = buscarSolicitud(id);
        solicitud.cerrar(dto.justificacion());

        solicitud.agregarSeguimiento(
                "Solicitud cerrada. Justificación: " + dto.justificacion(),
                null,
                "CERRAR"
        );

        solicitud = solicitudRepo.save(solicitud);
        return mapper.toDTO(solicitud);
    }

    // ─── Reabrir ─────────────────────────────────────────────────

    public SolicitudAcademicaDTO reabrir(UUID id, ComentarioDTO dto) {
        SolicitudAcademica solicitud = buscarSolicitud(id);
        solicitud.reabrir();

        solicitud.agregarSeguimiento(
                "Solicitud reabierta: " + (dto.comentario() != null ? dto.comentario() : "Sin comentario"),
                null,
                "REABRIR"
        );

        solicitud = solicitudRepo.save(solicitud);
        return mapper.toDTO(solicitud);
    }

    // ─── Recalcular prioridad ────────────────────────────────────

    public SolicitudAcademicaDTO recalcularPrioridad(UUID id, NivelPrioridad nuevaPrioridad,
                                                      String justificacion) {
        SolicitudAcademica solicitud = buscarSolicitud(id);
        solicitud.recalcularPrioridad(nuevaPrioridad, justificacion);

        solicitud.agregarSeguimiento(
                "Prioridad recalculada a " + nuevaPrioridad + ". " + justificacion,
                null,
                "RECALCULAR_PRIORIDAD"
        );

        solicitud = solicitudRepo.save(solicitud);
        return mapper.toDTO(solicitud);
    }

    // ─── Seguimientos (RF-06) ────────────────────────────────────

    @Transactional(readOnly = true)
    public List<RegistroSeguimientoDTO> obtenerSeguimientos(UUID solicitudId) {
        // Validar que la solicitud existe
        buscarSolicitud(solicitudId);
        return seguimientoRepo.findBySolicitudIdOrderByFechaDesc(solicitudId).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    public RegistroSeguimientoDTO agregarSeguimiento(UUID solicitudId, RegistroSeguimientoCrearDTO dto) {
        SolicitudAcademica solicitud = buscarSolicitud(solicitudId);

        RegistroSeguimiento registro = solicitud.agregarSeguimiento(
                dto.comentario(),
                null, // En producción: el usuario se extraería del JWT
                "COMENTARIO_MANUAL"
        );

        solicitudRepo.save(solicitud);
        return mapper.toDTO(registro);
    }

    // ─── Asignaciones ────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<AsignacionGestorDTO> obtenerAsignaciones(UUID solicitudId) {
        buscarSolicitud(solicitudId);
        return asignacionRepo.findBySolicitudIdOrderByFechaAsignacionDesc(solicitudId).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    // ─── Helpers ─────────────────────────────────────────────────

    private SolicitudAcademica buscarSolicitud(UUID id) {
        return solicitudRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Solicitud no encontrada con ID: " + id));
    }

    private CuentaUsuario buscarUsuario(UUID id) {
        return usuarioRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Usuario no encontrado con ID: " + id));
    }
}
