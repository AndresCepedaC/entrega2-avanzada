# Diagrama de Clases UML - Modelo de Dominio

Este diagrama visualiza la estructura principal de la arquitectura de dominio del **Sistema Integral de Gestión de Solicitudes Académicas**. Refleja el lenguaje ubicuo correcto (evitando el término obsoleto "Caso") y centraliza la máquina de estados en el aggregate root `SolicitudAcademica`.

```mermaid
classDiagram

    %% ----------------------
    %% ENUMERACIONES
    %% ----------------------
    class EstadoSolicitud {
        <<enumeration>>
        REGISTRADA
        CLASIFICADA
        EN_ATENCION
        ATENDIDA
        CERRADA
        RECHAZADA
        CANCELADA
    }

    class NivelPrioridad {
        <<enumeration>>
        BAJA
        MEDIA
        ALTA
        URGENTE
    }

    class CategoriaSolicitud {
        <<enumeration>>
        ACADEMICO
        ADMINISTRATIVO
        FINANCIERO
        OTRO
    }

    class MedioIngreso {
        <<enumeration>>
        WEB
        PRESENCIAL
        CORREO
        TELEFONO
    }

    class TipoPerfil {
        <<enumeration>>
        SOLICITANTE
        GESTOR
        ADMINISTRADOR
        SUPERVISOR
    }

    %% ----------------------
    %% ENTIDADES DEL DOMINIO
    %% ----------------------
    class SolicitudAcademica {
        <<Aggregate Root>>
        - UUID id
        - String descripcion
        - EstadoSolicitud estado
        - NivelPrioridad nivelPrioridad
        - String justificacionPrioridad
        - CategoriaSolicitud categoria
        - MedioIngreso medioIngreso
        - LocalDateTime fechaCreacion
        - LocalDateTime fechaActualizacion
        - String descripcionSolucion
        - String observacionCierre
        + clasificar(tipo, categoria, prioridad, justificacion)
        + asignarGestor(CuentaUsuario gestor) AsignacionGestor
        + rechazar()
        + registrarSolucion(String solucion)
        + reclasificar(tipo, categoria, prioridad, justificacion)
        + cancelar()
        + cerrar(String observacion)
        + reabrir()
        + recalcularPrioridad(NivelPrioridad nuevaPrioridad)
        + agregarSeguimiento(...) RegistroSeguimiento
    }

    class RegistroSeguimiento {
        <<Entity>>
        - UUID id
        - LocalDateTime fecha
        - String comentario
        - String accion
    }

    class AsignacionGestor {
        <<Entity>>
        - UUID id
        - LocalDateTime fechaAsignacion
        - boolean activa
    }

    class CuentaUsuario {
        <<Entity>>
        - UUID id
        - String email
        - String nombreCompleto
        - boolean activo
        - LocalDateTime fechaCreacion
    }

    class Perfil {
        <<Entity>>
        - UUID id
        - TipoPerfil tipoPerfil
        - String nombre
    }

    class ReglaClasificacion {
        <<Entity>>
        - UUID id
        - String nombre
        - boolean activa
        - String condicion
        - String categoriaResultante
    }

    class PoliticaPrioridad {
        <<Entity>>
        - UUID id
        - String nombre
        - boolean activa
        - String condicion
        - NivelPrioridad nivelResultante
    }

    %% ----------------------
    %% RELACIONES
    %% ----------------------

    SolicitudAcademica *-- "1..*" RegistroSeguimiento : genera trazabilidad
    SolicitudAcademica "1" *-- "0..*" AsignacionGestor : asignada mediante
    AsignacionGestor "0..*" --> "1" CuentaUsuario : gestionada por
    RegistroSeguimiento "0..*" --> "1" CuentaUsuario : autor (auditoría JWT)
    SolicitudAcademica "0..*" --> "1" CuentaUsuario : solicitante
    
    CuentaUsuario "1" --> "1" Perfil : posee un

    %% Dependencias conceptuales y enumeraciones
    SolicitudAcademica ..> EstadoSolicitud 
    SolicitudAcademica ..> NivelPrioridad 
    SolicitudAcademica ..> CategoriaSolicitud 
    SolicitudAcademica ..> MedioIngreso 
    Perfil ..> TipoPerfil

    ReglaClasificacion ..> SolicitudAcademica : evalúa (Motor de Reglas)
    PoliticaPrioridad ..> SolicitudAcademica : prioriza
```

## Beneficios de este Diseño para el Hito 2
1. **Lógica de Estado Centralizada:** Evita la propagación de validaciones. `SolicitudAcademica` defiende su regla de vida (Solo se puede cerrar si está en `ATENDIDA`).
2. **Alta Cohesión, Bajo Acoplamiento:** Dependencia con el módulo de toma de decisiones externalizado para soportar políticas editadas e independencia de IA.
3. **Auditoría Inquebrantable:** El agregado fuerza a que *cada* transición (sea clasificar, atender o cerrar) retorne/genere obligatoriamente un `RegistroSeguimiento` con la fecha en milisegundos, blindando la trazabilidad exigida por un Sistema de Gestión Académico.
