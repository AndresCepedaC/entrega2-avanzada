package com.universidad.solicitudes.interfaces.rest;

import com.universidad.solicitudes.application.dto.*;
import com.universidad.solicitudes.application.service.UsuarioService;
import com.universidad.solicitudes.domain.model.Perfil;
import com.universidad.solicitudes.domain.model.enums.TipoPerfil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controlador REST para gestión de usuarios y perfiles (RF-13).
 */
@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<CuentaUsuarioDTO>> listar(
            @RequestParam(required = false) Boolean activo,
            @RequestParam(required = false) TipoPerfil tipoPerfil) {
        List<CuentaUsuarioDTO> usuarios = usuarioService.listar(activo, tipoPerfil);
        return ResponseEntity.ok(usuarios);
    }

    @PostMapping
    public ResponseEntity<CuentaUsuarioDTO> crear(@Valid @RequestBody CuentaUsuarioCrearDTO dto) {
        CuentaUsuarioDTO resultado = usuarioService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CuentaUsuarioDTO> obtenerPorId(@PathVariable UUID id) {
        CuentaUsuarioDTO usuario = usuarioService.obtenerPorId(id);
        return ResponseEntity.ok(usuario);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CuentaUsuarioDTO> actualizar(
            @PathVariable UUID id,
            @RequestBody java.util.Map<String, Object> campos) {
        Boolean activo = campos.containsKey("activo") ? (Boolean) campos.get("activo") : null;
        UUID perfilId = campos.containsKey("perfilId")
                ? UUID.fromString((String) campos.get("perfilId")) : null;
        CuentaUsuarioDTO resultado = usuarioService.actualizar(id, activo, perfilId);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/perfiles")
    public ResponseEntity<List<Perfil>> listarPerfiles() {
        List<Perfil> perfiles = usuarioService.listarPerfiles();
        return ResponseEntity.ok(perfiles);
    }
}
