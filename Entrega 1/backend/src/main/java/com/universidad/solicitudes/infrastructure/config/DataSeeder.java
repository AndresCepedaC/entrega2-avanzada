package com.universidad.solicitudes.infrastructure.config;

import com.universidad.solicitudes.domain.model.*;
import com.universidad.solicitudes.domain.model.enums.*;
import com.universidad.solicitudes.domain.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Seeder de datos iniciales para desarrollo y pruebas.
 * Carga Perfiles, Usuarios base y Reglas del motor de decisiones.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final PerfilRepository perfilRepo;
    private final CuentaUsuarioRepository usuarioRepo;
    private final ReglaClasificacionRepository reglaRepo;
    private final PoliticaPrioritadRepository politicaRepo;

    public DataSeeder(PerfilRepository perfilRepo, CuentaUsuarioRepository usuarioRepo,
                      ReglaClasificacionRepository reglaRepo, PoliticaPrioritadRepository politicaRepo) {
        this.perfilRepo = perfilRepo;
        this.usuarioRepo = usuarioRepo;
        this.reglaRepo = reglaRepo;
        this.politicaRepo = politicaRepo;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Ejecutar solo si la DB está vacía
        if (perfilRepo.count() > 0) return;

        // 1. Perfiles
        Perfil pSolicitante = perfilRepo.save(new Perfil(TipoPerfil.SOLICITANTE, "Estudiante Registrado"));
        Perfil pGestor = perfilRepo.save(new Perfil(TipoPerfil.GESTOR, "Coordinación Académica"));
        Perfil pAdmin = perfilRepo.save(new Perfil(TipoPerfil.ADMINISTRADOR, "Admin Sistema"));

        // 2. Usuarios Base
        CuentaUsuario estudiante = new CuentaUsuario("maria.perez@universidad.edu.co", "Maria Perez", pSolicitante);
        usuarioRepo.save(estudiante);

        CuentaUsuario gestor = new CuentaUsuario("carlos.gestor@universidad.edu.co", "Carlos Ruiz", pGestor);
        usuarioRepo.save(gestor);

        CuentaUsuario admin = new CuentaUsuario("admin@universidad.edu.co", "Administrador Root", pAdmin);
        usuarioRepo.save(admin);

        // 3. Reglas de Clasificación (RF-02)
        reglaRepo.save(new ReglaClasificacion(
                "Inscripción automáticas",
                "DESCRIPCION_CONTIENE=inscribir",
                "ACADEMICO"
        ));
        reglaRepo.save(new ReglaClasificacion(
                "Homologación externa",
                "TIPO=HOMOLOGACION",
                "ACADEMICO"
        ));

        // 4. Políticas de Prioridad (RF-03)
        politicaRepo.save(new PoliticaPrioridad(
                "Urgencia por cupo",
                "DESCRIPCION_CONTIENE=urgente",
                NivelPrioridad.URGENTE
        ));
        politicaRepo.save(new PoliticaPrioridad(
                "Priorización de Homologaciones",
                "TIPO=HOMOLOGACION",
                NivelPrioridad.ALTA
        ));

        System.out.println("✅ Data Seeder ejecutado con éxito.");
    }
}
