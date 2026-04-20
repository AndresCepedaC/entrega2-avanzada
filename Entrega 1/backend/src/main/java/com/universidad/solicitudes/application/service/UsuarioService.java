package com.universidad.solicitudes.application.service;

import com.universidad.solicitudes.application.dto.*;
import com.universidad.solicitudes.application.mapper.DomainMapper;
import com.universidad.solicitudes.domain.model.CuentaUsuario;
import com.universidad.solicitudes.domain.model.Perfil;
import com.universidad.solicitudes.domain.model.enums.TipoPerfil;
import com.universidad.solicitudes.domain.repository.CuentaUsuarioRepository;
import com.universidad.solicitudes.domain.repository.PerfilRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Servicio de gestión de usuarios y perfiles (RF-13).
 */
@Service
@Transactional
public class UsuarioService {

    private final CuentaUsuarioRepository usuarioRepo;
    private final PerfilRepository perfilRepo;
    private final DomainMapper mapper;

    public UsuarioService(CuentaUsuarioRepository usuarioRepo,
                          PerfilRepository perfilRepo,
                          DomainMapper mapper) {
        this.usuarioRepo = usuarioRepo;
        this.perfilRepo = perfilRepo;
        this.mapper = mapper;
    }

    public CuentaUsuarioDTO crear(CuentaUsuarioCrearDTO dto) {
        Perfil perfil = perfilRepo.findById(dto.perfilId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Perfil no encontrado con ID: " + dto.perfilId()));

        if (usuarioRepo.findByEmail(dto.email()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un usuario con el email: " + dto.email());
        }

        CuentaUsuario usuario = new CuentaUsuario(dto.email(), dto.nombreCompleto(), perfil);
        usuario = usuarioRepo.save(usuario);
        return mapper.toDTO(usuario);
    }

    @Transactional(readOnly = true)
    public CuentaUsuarioDTO obtenerPorId(UUID id) {
        CuentaUsuario usuario = buscarUsuario(id);
        return mapper.toDTO(usuario);
    }

    @Transactional(readOnly = true)
    public List<CuentaUsuarioDTO> listar(Boolean activo, TipoPerfil tipoPerfil) {
        return usuarioRepo.findByFiltros(activo, tipoPerfil).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    public CuentaUsuarioDTO actualizar(UUID id, Boolean activo, UUID perfilId) {
        CuentaUsuario usuario = buscarUsuario(id);

        if (activo != null) {
            usuario.setActivo(activo);
        }

        if (perfilId != null) {
            Perfil perfil = perfilRepo.findById(perfilId)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Perfil no encontrado con ID: " + perfilId));
            usuario.setPerfil(perfil);
        }

        usuario = usuarioRepo.save(usuario);
        return mapper.toDTO(usuario);
    }

    @Transactional(readOnly = true)
    public List<Perfil> listarPerfiles() {
        return perfilRepo.findAll();
    }

    private CuentaUsuario buscarUsuario(UUID id) {
        return usuarioRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Usuario no encontrado con ID: " + id));
    }
}
