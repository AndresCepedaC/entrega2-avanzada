package com.universidad.solicitudes.interfaces.rest;

import com.universidad.solicitudes.application.service.DecisionService;
import com.universidad.solicitudes.domain.model.PoliticaPrioridad;
import com.universidad.solicitudes.domain.model.ReglaClasificacion;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador para las Decisiones y Reglas (RF-03).
 */
@RestController
@RequestMapping
@CrossOrigin(origins = "*")
public class DecisionController {

    private final DecisionService decisionService;

    public DecisionController(DecisionService decisionService) {
        this.decisionService = decisionService;
    }

    @GetMapping("/reglas-clasificacion")
    public ResponseEntity<List<ReglaClasificacion>> listarReglasClasificacion() {
        return ResponseEntity.ok(decisionService.listarTodasLasReglas());
    }

    @GetMapping("/politicas-prioridad")
    public ResponseEntity<List<PoliticaPrioridad>> listarPoliticasPrioridad() {
        return ResponseEntity.ok(decisionService.listarTodasLasPoliticas());
    }
}
