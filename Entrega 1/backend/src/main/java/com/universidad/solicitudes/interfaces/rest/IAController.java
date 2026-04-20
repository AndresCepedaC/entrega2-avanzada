package com.universidad.solicitudes.interfaces.rest;

import com.universidad.solicitudes.infrastructure.ai.ResumenIAService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/solicitudes/{id}/ia")
@CrossOrigin(origins = "*")
public class IAController {

    private final ResumenIAService resumenIAService;

    public IAController(ResumenIAService resumenIAService) {
        this.resumenIAService = resumenIAService;
    }

    @GetMapping("/resumen")
    public ResponseEntity<Map<String, String>> generarResumen(@PathVariable UUID id) {
        String resumen = resumenIAService.generarResumenHistorial(id);
        return ResponseEntity.ok(Collections.singletonMap("resumen", resumen));
    }
}
