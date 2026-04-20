# API — Sistema Integral de Gestión de Solicitudes Académicas

Documentación de la API REST para el registro, clasificación, priorización y seguimiento de solicitudes administrativas y académicas. La especificación formal está en **OpenAPI 3.0** en el archivo `openapi.yaml`.

---

## Especificación OpenAPI

| Campo        | Valor |
|-------------|--------|
| **Archivo** | `openapi.yaml` |
| **Versión OpenAPI** | 3.0.3 |
| **Versión API** | 1.0.0 |
| **Base URL** | `/api/v1` |
| **Formato** | JSON |

Para visualizar o probar la API puedes usar [Swagger UI](https://swagger.io/tools/swagger-ui/) o [Redoc](https://redocly.com/redoc/) cargando el archivo `openapi.yaml`.

---

## Grupos de recursos (tags)

| Tag | Descripción |
|-----|-------------|
| **Solicitudes** | Ciclo de vida de la solicitud: estados y transiciones |
| **Seguimiento** | Registro de eventos y trazabilidad (RegistroSeguimiento) |
| **Asignaciones** | Asignación de gestores a solicitudes |
| **Identidad** | Cuentas de usuario y perfiles |
| **Decisiones** | Reglas de clasificación y políticas de prioridad |

---

## Resumen de endpoints

### Solicitudes (entidad principal)

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/solicitudes` | Listar solicitudes (filtros: `estado`, `prioridad`) |
| `POST` | `/solicitudes` | **crearSolicitud** — Crear solicitud (estado inicial **REGISTRADA**) |
| `GET` | `/solicitudes/{id}` | Obtener solicitud por ID |
| `POST` | `/solicitudes/{id}/clasificar` | **clasificar** — REGISTRADA → CLASIFICADA |
| `POST` | `/solicitudes/{id}/cancelar` | **cancelar** — REGISTRADA o EN_ATENCION → CANCELADA (terminal) |
| `POST` | `/solicitudes/{id}/asignar-gestor` | **asignarGestor** — CLASIFICADA → EN_ATENCION |
| `POST` | `/solicitudes/{id}/rechazar` | **rechazar** — CLASIFICADA → RECHAZADA (terminal) |
| `POST` | `/solicitudes/{id}/registrar-solucion` | **registrarSolucion** — EN_ATENCION → ATENDIDA |
| `POST` | `/solicitudes/{id}/reclasificar` | **reclasificar** — EN_ATENCION → CLASIFICADA |
| `POST` | `/solicitudes/{id}/cerrar` | **cerrarSolicitud** — ATENDIDA → CERRADA (terminal) |
| `POST` | `/solicitudes/{id}/reabrir` | **reabrir** — ATENDIDA → EN_ATENCION |
| `POST` | `/solicitudes/{id}/recalcular-prioridad` | Recalcular prioridad según políticas activas |

### Seguimiento

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/solicitudes/{id}/seguimientos` | Historial de seguimiento de la solicitud (1:N) |
| `POST` | `/solicitudes/{id}/seguimientos` | Añadir registro de seguimiento (seguro, vía JWT) |

### Asignaciones

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/solicitudes/{id}/asignaciones` | Historial de asignaciones (1:N, una activa por solicitud) |

### Identidad (usuarios y perfiles)

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/usuarios` | Listar cuentas (filtros: `activo`, `tipoPerfil`) |
| `POST` | `/usuarios` | Crear cuenta de usuario |
| `GET` | `/usuarios/{id}` | Obtener usuario por ID |
| `PATCH` | `/usuarios/{id}` | Activar/desactivar cuenta o actualizar perfil |

### Motor de decisiones

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/reglas-clasificacion` | Listar reglas de clasificación activas |
| `GET` | `/politicas-prioridad` | Listar políticas de prioridad |

---

## Diagrama de estados de la solicitud

El objeto **SolicitudAcademica** sigue un ciclo de vida definido. Cada transición debe generar un **RegistroSeguimiento**.

### Estados

| Estado | Descripción | ¿Terminal? |
|--------|-------------|------------|
| **REGISTRADA** | Solicitud creada, información básica registrada, sin clasificar | No |
| **CLASIFICADA** | Categoría y prioridad definidas, listo para asignación | No |
| **EN_ATENCION** | Gestor asignado, en análisis o ejecución | No |
| **ATENDIDA** | Solución registrada, pendiente de validación/cierre | No |
| **CERRADA** | Solicitud cerrada exitosamente | Sí (inmutable) |
| **RECHAZADA** | Solicitud inválida o fuera de alcance | Sí (inmutable) |
| **CANCELADA** | Cancelada por el solicitante | Sí (inmutable) |

### Transiciones permitidas

| Estado origen | Acción (endpoint) | Estado destino |
|---------------|-------------------|----------------|
| [*] | `POST /solicitudes` (crearSolicitud) | REGISTRADA |
| REGISTRADA | `POST .../clasificar` | CLASIFICADA |
| REGISTRADA | `POST .../cancelar` | CANCELADA |
| CLASIFICADA | `POST .../asignar-gestor` | EN_ATENCION |
| CLASIFICADA | `POST .../rechazar` | RECHAZADA |
| EN_ATENCION | `POST .../registrar-solucion` | ATENDIDA |
| EN_ATENCION | `POST .../reclasificar` | CLASIFICADA |
| EN_ATENCION | `POST .../cancelar` | CANCELADA |
| ATENDIDA | `POST .../cerrar` | CERRADA |
| ATENDIDA | `POST .../reabrir` | EN_ATENCION |

### Flujo principal (happy path)

```
REGISTRADA → CLASIFICADA → EN_ATENCION → ATENDIDA → CERRADA
```

---

## Enumeraciones

- **EstadoSolicitud**: `REGISTRADA`, `CLASIFICADA`, `EN_ATENCION`, `ATENDIDA`, `CERRADA`, `RECHAZADA`, `CANCELADA`
- **NivelPrioridad**: `BAJA`, `MEDIA`, `ALTA`, `URGENTE`
- **CategoriaSolicitud**: `ACADEMICO`, `ADMINISTRATIVO`, `FINANCIERO`, `OTRO`
- **MedioIngreso**: `WEB`, `PRESENCIAL`, `CORREO`, `TELEFONO`
- **TipoPerfil**: `SOLICITANTE`, `GESTOR`, `ADMINISTRADOR`, `SUPERVISOR`

---

## Modelo de datos principales (esquemas)

| Esquema | Uso |
|---------|-----|
| **SolicitudAcademica** | Entidad principal: id, descripcion, estado, nivelPrioridad, categoria, medioIngreso, fechas, solicitanteId, asignacionActivaId |
| **SolicitudAcademicaCrear** | Creación: descripcion (requerido), medioIngreso, solicitanteId |
| **ClasificacionInput** | clasificar / reclasificar: categoria, nivelPrioridad (requeridos) |
| **RegistroSeguimiento** | Trazabilidad: id, solicitudId, fecha, comentario, usuarioId |
| **AsignacionGestor** | Asignación: id, solicitudId, gestorId, fechaAsignacion, activa |
| **CuentaUsuario** | Identidad: id, email, activo, fechaCreacion, perfil |
| **Perfil** | id, tipoPerfil, nombre |
| **ReglaClasificacion** | Motor decisiones: id, nombre, activa, condicion |
| **PoliticaPrioridad** | Motor decisiones: id, nombre, activa, nivelResultante |

Los identificadores (`id`, `solicitudId`, `gestorId`, etc.) son **UUID**. Las fechas se envían en formato **date-time** (ISO 8601). El contenido de las peticiones y respuestas es **application/json**.

---

## Reglas de consistencia (API)

- Una solicitud no puede finalizar sin haber sido atendida antes.
- Solo solicitudes en estado **CLASIFICADA** pueden recibir asignación de gestor.
- Solo solicitudes en **EN_ATENCION** pueden registrar solución.
- Los estados terminales (**CERRADA**, **RECHAZADA**, **CANCELADA**) no permiten nuevas transiciones.
- Solo puede haber **una asignación activa** por solicitud.
- Solo usuarios **activos** pueden ser asignados como gestores y ejecutar acciones sobre solicitudes.

---

## Uso de la especificación

1. **Documentación**: Abre `openapi.yaml` en Swagger Editor o en cualquier cliente que soporte OpenAPI 3.
2. **Generación de código**: Usa herramientas como OpenAPI Generator o el plugin de Spring Boot para generar clientes o servidores a partir de `openapi.yaml`.
3. **Pruebas**: Importa `openapi.yaml` en Postman o Insomnia para generar una colección de peticiones.

Si necesitas más detalle de un endpoint concreto (parámetros, cuerpo de la petición o códigos de respuesta), consulta directamente el archivo **`openapi.yaml`** en esta carpeta.
