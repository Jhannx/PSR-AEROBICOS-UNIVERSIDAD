# Catálogo de Endpoints Esperados — PSR Aeróbicos (Contrato Exacto Frontend ↔ Backend)

**Ubicación:** `notas-pendientes/CATALOGO-ENDPOINTS-PSR.md` y `psr-aerobicos-backend/docs/CATALOGO-ENDPOINTS-PSR.md`<br>
**Base URL:** `http://localhost:8080/api`
**Envoltorio Estándar de Respuesta (`ApiResponseDTO<T>`):**
```json
{
  "codigo": 200,
  "mensaje": "OK",
  "totalRegistros": 1,
  "datos": { ... },
  "fechaHora": "2026-11-23T12:25:00"
}
```
> **Nota para Backend:** `ClienteHttp` en el frontend (`js/utils/cliente-http.js`) desempaqueta automáticamente `cuerpoJson.datos` cuando `codigo` es `200` o `201`, y lanza una excepción con `cuerpoJson.mensaje` cuando `codigo >= 400` o el estado HTTP no es 2xx.

---

## Resumen General por Módulo del Backend

| # | Módulo en `psr-aerobicos-backend` | Controlador | Prefijo Base | DTOs Involucrados |
| :--- | :--- | :--- | :--- | :--- |
| 1 | `modules/user` (Auth) | `AuthController` | `/api/auth` | `UsuarioRequestDTO`, `UsuarioResponseDTO` |
| 2 | `modules/person` | `PersonaController` | `/api/personas` | `PersonaRequestDTO`, `PersonaResponseDTO`, `ContactoEmergenciaResponseDTO` |
| 3 | `modules/user` y `modules/role` | `UsuarioController` / `RolController` | `/api/usuarios`, `/api/roles` | `UsuarioRequestDTO`, `UsuarioResponseDTO`, `UsuarioRolRequestDTO`, `RolResponseDTO` |
| 4 | `modules/aerobics/checkin` | `LlegadaController` | `/api/cola`, `/api/checkin` | `LlegadaRequestDTO`, `LlegadaResponseDTO` + DTOs de `vista_fila_hoy` y `vista_fila_espera` |
| 5 | `modules/aerobics/attendance` | `AsistenciaController` | `/api/llegadas/{llegadaId}/asistencia` | `AsistenciaRequestDTO`, `AsistenciaResponseDTO` |
| 6 | `modules/aerobics/payment` | `MovimientoCuentaController` | `/api/movimientos` | `MovimientoCuentaRequestDTO`, `MovimientoCuentaResponseDTO` |
| 7 | `modules/aerobics/pricing` | `TarifaController` | `/api/tarifa` | `TarifaRequestDTO`, `TarifaResponseDTO` |
| 8 | `modules/aerobics/schedule` | `HorarioController` | `/api/horarios` | `HorarioRequestDTO`, `HorarioResponseDTO` |
| 9 | `modules/aerobics/session` | `SesionController` | `/api/sesiones` | `SesionRequestDTO`, `SesionResponseDTO` |
| 10 | `modules/aerobics/report` | `ReporteController` *(Ya existe)* | `/api/reportes` | DTOs de `report/dto/response/*` |
| 11 | `modules/aerobics/history` | `HistorialAuditoriaController` | `/api/auditoria` | `HistorialAuditoriaResponseDTO` |

---

## 1. Autenticación (`AuthController`) — Usado por `js/pages/login.js` y `js/utils/sesion.js`

| Método | Ruta Exacta | Mecanismo JDBC / SQL | Descripción |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/login` | `PreparedStatement` blindado (`?`) cruzando `usuario`, `persona`, `usuario_rol` y `rol` | Valida documento o correo y contraseña. Devuelve los datos del usuario y su lista de roles. |

* **Request Body enviado por `login.js` (`UsuarioRequestDTO`):**
  ```json
  {
    "identificador": "1102812345",
    "documento": "1102812345",
    "password": "hash_password_1"
  }
  ```
* **Response esperado por `sesion.js` (`datos` / `UsuarioResponseDTO` ampliado con `nombreCompleto` y `roles`):**
  ```json
  {
    "id": 1,
    "personaId": 1,
    "numeroDocumento": "1102812345",
    "documento": "1102812345",
    "nombreCompleto": "Elena Vargas",
    "correo": "elena.vargas@psr.com",
    "activo": true,
    "roles": ["ADMINISTRADOR", "RECEPCIONISTA"]
  }
  ```
  *(Nota: `sesion.js` lee `datosUsuario.id`, `datosUsuario.personaId`, `datosUsuario.documento || datosUsuario.numeroDocumento`, `datosUsuario.nombreCompleto || datosUsuario.nombre`, `datosUsuario.correo` y `datosUsuario.roles`).*

---

## 2. Personas y Contactos de Emergencia (`PersonaController`) — Usado por `personas.js`, `registro.js` y `miembro.js`

| Método | Ruta Exacta | Mecanismo JDBC / SQL | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/personas?buscar=...` | `PreparedStatement` con `LIKE` (`%buscar%`) sobre `nombre`, `apellidos` y `numero_documento` | Sin `buscar`, lista todas las personas. Con `buscar`, ejecuta la consulta dinámica con comodines `LIKE`. |
| `GET` | `/api/personas/{id}` | `PreparedStatement` | Obtiene una persona por ID junto con su `contacto_emergencia` (1:1). |
| `GET` | `/api/personas/{id}/saldo` | `PreparedStatement` sobre `vista_saldo_persona` | Retorna `personaId`, `totalAbonado`, `totalCobrado`, `saldo` y `estadoCuenta` (`AL_DIA`, `DEBE`, `CON_SALDO_A_FAVOR`). |
| `POST` | `/api/personas` | `CallableStatement` → `{CALL sp_crear_persona_con_contacto(...)}` | Alta de persona, contacto y cuenta desde el formulario administrativo. El backend fija `fuenteRegistro: "ADMIN"` y la clave inicial es el documento. |
| `POST` | `/api/personas/autoregistro` | `CallableStatement` → `{CALL sp_crear_persona_con_contacto(...)}` | Alta pública desde `registro.js`. El backend fija `fuenteRegistro: "AUTOREGISTRO"` y cifra la contraseña elegida antes de llamar al SP. |
| `PUT` | `/api/personas/{id}` | `PreparedStatement` / SP de actualización | Usado por `personas.js` (editar persona + contacto) y por `miembro.js` (actualizar su contacto de emergencia). |
| `PATCH` | `/api/personas/{id}/desactivar` | `PreparedStatement` (`UPDATE persona SET activo = FALSE WHERE id = ?`) | Desactivación lógica (y `/api/personas/{id}/reactivar`). |

* **Request Body enviado por `personas.js` (`POST /api/personas`, `PUT /api/personas/{id}`) y `registro.js` (`POST /api/personas/autoregistro`) — coincide con `PersonaRequestDTO`:**
  ```json
  {
    "tipoDocumento": "CC",
    "numeroDocumento": "1067890123",
    "nombre": "Camila Andrea",
    "apellidos": "Torres",
    "telefono": "3004567890",
    "fechaNacimiento": "1999-05-14",
    "correo": "camila.torres@correo.com",
    "contactoNombre": "Luis Torres",
    "contactoTelefono": "3102223344",
    "contactoObservaciones": "Padre · Sin restricciones médicas"
  }
  ```
  Para `/api/personas/autoregistro`, el body también incluye `"password": "claveElegidaPorLaPersona"`. En el alta administrativa no se envía contraseña: el service calcula el hash del número de documento. `fuenteRegistro` se determina por la ruta y no se toma del body.
  *(Cuando `miembro.js` actualiza únicamente su contacto de emergencia en `PUT /api/personas/{id}`, envía `{ "contactoNombre": "...", "contactoTelefono": "...", "contactoObservaciones": "..." }`).*
* **Response esperado para cada elemento de `GET /api/personas` (`PersonaResponseDTO`):**
  ```json
  {
    "id": 1,
    "tipoDocumento": "CC",
    "numeroDocumento": "1067890123",
    "nombre": "Camila Andrea",
    "apellidos": "Torres",
    "telefono": "3004567890",
    "fechaNacimiento": "1999-05-14",
    "observacionesSalud": null,
    "fuenteRegistro": "AUTOREGISTRO",
    "fechaRegistro": "2026-10-01T08:00:00",
    "activo": true,
    "contactoNombre": "Luis Torres",
    "contactoTelefono": "3102223344",
    "contactoObservaciones": "Padre",
    "saldo": 0.00,
    "estadoCuenta": "AL_DIA"
  }
  ```

---

## 3. Usuarios y Roles (`UsuarioController` y `RolController`) — Usado por `roles.js` y `sesiones.js`

| Método | Ruta Exacta | Mecanismo JDBC / SQL | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/usuarios?buscar=...` | `PreparedStatement` con `JOIN` a `persona`, `usuario_rol` y `rol` | Lista todas las cuentas de usuario incluyendo su lista de `roles`. |
| `GET` | `/api/usuarios/profesores` | `PreparedStatement` filtrando rol `PROFESOR` o `ADMINISTRADOR` | Devuelve los profesores asignables a sesiones. |
| `POST` | `/api/usuarios` | `CallableStatement` → `{CALL sp_registrar_usuario_para_persona(?, ?, ?, ?)}` | Crea cuenta para una persona existente y le asigna el rol inicial `MIEMBRO`. La clave inicial se deriva del número de documento; no se recibe una contraseña del administrador. |
| `POST` | `/api/usuarios/{id}/roles` | `CallableStatement` → `{CALL sp_asignar_rol(?, ?)}` | Asigna un rol a un usuario (dispara `trg_validar_persona_requerida`). |
| `DELETE` | `/api/usuarios/{id}/roles/{rolNombre}` | `PreparedStatement` (`DELETE FROM usuario_rol ...`) | Retira el rol `{rolNombre}` (`ADMINISTRADOR`, `RECEPCIONISTA`, `PROFESOR`, `MIEMBRO`) del usuario `{id}`. |
| `GET` | `/api/roles` | `PreparedStatement` (`SELECT * FROM rol`) | Lista el catálogo de roles (`RolResponseDTO`). |

* **Request Body enviado por `roles.js` en `POST /api/usuarios` (`UsuarioRequestDTO`):**
  ```json
  {
    "personaId": 3,
    "correo": "valentina.mendoza@correo.com"
  }
  ```
* **Request Body enviado por `roles.js` en `POST /api/usuarios/{id}/roles` (`UsuarioRolRequestDTO`):**
  ```json
  {
    "rolNombre": "RECEPCIONISTA",
    "roleName": "RECEPCIONISTA"
  }
  ```

---

## 4. Cola de Recepción y Check-in (`LlegadaController`) — Usado por `recepcion.js` y `miembro.js`

| Método | Ruta Exacta | Mecanismo JDBC / SQL | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/cola/admin` | `PreparedStatement` → `SELECT * FROM vista_fila_hoy` | Devuelve la cola operativa del día para `recepcion.js`. |
| `GET` | `/api/cola/espera` | `PreparedStatement` → `SELECT * FROM vista_fila_espera` | Devuelve la fila pública de turnos sin saldos. |
| `POST` | `/api/checkin/manual/documento` | `CallableStatement` → `{CALL sp_registrar_llegada(?, ?, ?, ?)}` | Usado por el modal de check-in manual en `recepcion.js`. |
| `POST` | `/api/checkin/documento` | `CallableStatement` → `{CALL sp_registrar_llegada(?, ?, ?, ?)}` | Usado por el botón "Marcar mi llegada" en `miembro.js`. |

* **Request Body enviado por `recepcion.js` y `miembro.js` (`LlegadaRequestDTO`):**
  ```json
  {
    "personaId": 1,
    "documento": "1067890123",
    "documentNumber": "1067890123",
    "metodoCheckin": "MANUAL"
  }
  ```
  *(Valores válidos del enum `MetodoCheckin`: `"QR"`, `"DOCUMENTO"`, `"TERMINAL"`, `"MANUAL"`).*
* **Response esperado en `POST /api/checkin/*` y en cada fila de `GET /api/cola/admin` (`vista_fila_hoy`):**
  ```json
  {
    "llegadaId": 1,
    "sesionId": 14,
    "personaId": 1,
    "alumno": "Camila Andrea Torres",
    "numeroDocumento": "1067890123",
    "horaLlegada": "2026-11-22T17:52:00",
    "metodoCheckin": "TERMINAL",
    "posicionTurno": 1,
    "asistenciaId": null,
    "montoCobrado": null,
    "horaMarcado": null,
    "atendido": false,
    "saldo": 0.00,
    "estadoCuenta": "AL_DIA",
    "fechaSesion": "2026-11-22",
    "sesionCancelada": false
  }
  ```

---

## 5. Confirmación y Anulación de Asistencias (`AsistenciaController`) — Usado por `recepcion.js`

| Método | Ruta Exacta | Mecanismo JDBC / SQL | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/llegadas/{llegadaId}/asistencia` | `PreparedStatement` | Consulta la asistencia asociada a una llegada (`AsistenciaResponseDTO`). |
| `PUT` | `/api/llegadas/{llegadaId}/asistencia` | `CallableStatement` → `{CALL sp_registrar_asistencia(?, ?, ?)}` | Confirma asistencia con la tarifa vigente y dispara `trg_calcular_totales_sesion_insert`. |
| `DELETE` | `/api/llegadas/{llegadaId}/asistencia` | `PreparedStatement` (`DELETE FROM asistencia WHERE llegada_id = ?`) | Anula la confirmación de asistencia cuando se pulsa "Anular" en `recepcion.js`. |

* **Request Body enviado por `recepcion.js` en `PUT /api/llegadas/{llegadaId}/asistencia` (`AsistenciaRequestDTO`):**
  ```json
  {
    "usuarioId": 1
  }
  ```
* **Response (`AsistenciaResponseDTO`):**
  ```json
  {
    "id": 10,
    "llegadaId": 1,
    "montoCobrado": 5000.00,
    "marcadoPor": 1,
    "horaMarcado": "2026-11-22T17:55:00"
  }
  ```

---

## 6. Movimientos de Cuenta / Pagos (`MovimientoCuentaController`) — Usado por `recepcion.js`

| Método | Ruta Exacta | Mecanismo JDBC / SQL | Descripción |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/movimientos` | `CallableStatement` → `{CALL sp_registrar_movimiento(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}` | Registra un cobro en la fila o un abono por adelantado en el modal de `recepcion.js`; el backend determina y envía el tipo de movimiento y si se entregó vuelto. |
| `GET` | `/api/movimientos/checkin/{llegadaId}` | `PreparedStatement` | Lista los movimientos asociados a una llegada. |
| `PUT` | `/api/movimientos/{id}` | `PreparedStatement` | Actualiza un movimiento (dispara `trg_auditoria_movimiento_update`). |
| `DELETE` | `/api/movimientos/{id}` | `PreparedStatement` | Elimina un movimiento (dispara `trg_auditoria_movimiento_delete`). |

* **Request Body enviado por `recepcion.js` en `POST /api/movimientos` (`MovimientoCuentaRequestDTO`):**
  ```json
  {
    "personaId": 1,
    "llegadaId": 1,
    "montoRecibido": 5000,
    "montoAbonado": 5000,
    "metodoPago": "EFECTIVO",
    "tipoMovimiento": "ABONO",
    "vueltoEntregado": true,
    "usuarioId": 1,
    "nota": "Pago de sesión en recepción"
  }
  ```
  *(Valores del enum `MetodoPago`: `"EFECTIVO"`, `"TRANSFERENCIA"`, `"NEQUI"`, `"DAVIPLATA"`).*

---

## 7. Tarifa Vigente (`TarifaController`) — Usado por `configuracion.js`, `recepcion.js` y `miembro.js`

| Método | Ruta Exacta | Mecanismo JDBC / SQL | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/tarifa` | `PreparedStatement` (`SELECT * FROM tarifa WHERE id = 1`) | Devuelve `TarifaResponseDTO`. |
| `PUT` | `/api/tarifa` | `PreparedStatement` (`UPDATE tarifa SET monto = ?, actualizado_por = ? WHERE id = 1`) | Actualiza el precio vigente y dispara `trg_tarifa_actualizar_y_auditar`. |

* **Request Body enviado por `configuracion.js` en `PUT /api/tarifa` (`TarifaRequestDTO`):**
  ```json
  {
    "monto": 6000,
    "usuarioId": 1
  }
  ```
* **Response leído por `configuracion.js` (`TarifaResponseDTO`):**
  ```json
  {
    "id": 1,
    "monto": 5000.00,
    "actualizadoEn": "2026-11-22T16:10:00",
    "actualizadoPor": 1
  }
  ```

---

## 8. Horarios Recurrentes (`HorarioController`) — Usado por `sesiones.js`

| Método | Ruta Exacta | Mecanismo JDBC / SQL | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/horarios` | `PreparedStatement` | Lista los horarios semanales (`HorarioResponseDTO`). |
| `POST` | `/api/horarios` | `PreparedStatement` | Registra un nuevo horario recurrente desde el modal de `sesiones.js`. |
| `PUT` | `/api/horarios/{id}` | `PreparedStatement` | Edita un horario recurrente. |
| `DELETE` | `/api/horarios/{id}` | `PreparedStatement` | Desactiva o elimina un horario recurrente. |

* **Request Body enviado por `sesiones.js` en `POST /api/horarios` (`HorarioRequestDTO`):**
  ```json
  {
    "diaSemana": 1,
    "horaInicio": "18:00",
    "horaFin": "19:00",
    "activo": true
  }
  ```

---

## 9. Sesiones de Clase (`SesionController`) — Usado por `sesiones.js`

| Método | Ruta Exacta | Mecanismo JDBC / SQL | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/sesiones` | `PreparedStatement` → `SELECT * FROM vista_resumen_sesiones` | Lista las sesiones con profesor, asistencias y recaudo. |
| `GET` | `/api/sesiones/fecha/{fecha}` | `PreparedStatement` → `vista_resumen_sesiones WHERE fecha = ?` | Filtra las sesiones de una fecha (`YYYY-MM-DD`). |
| `POST` | `/api/sesiones` | `CallableStatement` → `{CALL sp_crear_sesion_manual(?, ?, ?, ?, ?, ?)}` | Crea una sesión manual desde `#modalCrearSesion` en `sesiones.js`. |
| `DELETE` | `/api/sesiones/{id}?usuarioId=1` | `CallableStatement` → `{CALL sp_cancelar_sesion(?, ?)}` | Cancela la sesión `{id}` y audita el cambio con `usuarioId`. |

* **Request Body enviado por `sesiones.js` en `POST /api/sesiones` (`SesionRequestDTO`):**
  ```json
  {
    "fecha": "2026-11-22",
    "horaInicio": "18:00",
    "horaFin": "19:00",
    "profesorUsuarioId": 3,
    "horarioId": 1
  }
  ```
  *(Nota para el Service/DAO de `Sesion`: en `SesionRequestDTO` los campos `horaInicio` y `horaFin` llegan como `LocalTime` `"18:00"`, y antes de llamar a `sp_crear_sesion_manual` se combinan con `fecha` mediante `LocalDateTime.of(dto.getFecha(), dto.getHoraInicio())`).*

---

## 10. Reportes (`ReporteController`) — Usado por `reportes.js`

Los 9 endpoints ya existentes en [`ReporteController.java`](file:///c:/Users/jhanp/Desktop/PSR%20AEROBICOS%20-%20UNIVERSIDAD/PSR%20Aerobicos%20-%20Backend/psr-aerobicos-backend/src/main/java/org/jhanp_dev/psraerobicosbackend/modules/aerobics/report/controller/ReporteController.java) más 2 consultas adicionales:

| Método | Ruta Exacta | Estado | Mecanismo JDBC / SQL |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/reportes/recaudo-diario` | **Ya existe** | `PreparedStatement` (`RecaudoDiarioResponseDTO`) |
| `GET` | `/api/reportes/pagos/mas-utilizados` | **Ya existe** | `PreparedStatement` (`MetodoPagoMasUtilizadoResponseDTO`) |
| `GET` | `/api/reportes/alumnos/destacados` | **Ya existe** | `PreparedStatement` (`AlumnoDestacadoResponseDTO`) |
| `GET` | `/api/reportes/sesiones/destacadas-por-dia` | **Ya existe** | `PreparedStatement` (`SesionDestacadaPorDiaResponseDTO`) |
| `GET` | `/api/reportes/pagos/alto-recaudo` | **Ya existe** | `PreparedStatement` (`MetodoPagoAltoRecaudoResponseDTO`) |
| `GET` | `/api/reportes/turnos/sin-cancelaciones` | **Ya existe** | `PreparedStatement` (`TurnoSinCancelacionResponseDTO`) |
| `GET` | `/api/reportes/pagos/auditoria-metodos` | **Ya existe** | `PreparedStatement` (`AuditoriaMetodoPagoResponseDTO`) |
| `GET` | `/api/reportes/pagos/anticipados` | **Ya existe** | `PreparedStatement` (`PagoAnticipadoResponseDTO`) |
| `GET` | `/api/reportes/estado-cuenta/{numeroDocumento}` | **Ya existe** | `PreparedStatement` (`EstadoCuentaParticipanteResponseDTO`) |
| `GET` | `/api/reportes/saldos-personas` | **Por añadir** | `PreparedStatement` → `SELECT * FROM vista_saldo_persona` |
| `GET` | `/api/reportes/asistencias-fecha?fecha=YYYY-MM-DD` | **Por añadir** | `CallableStatement` → `{CALL sp_reporte_asistencias_por_fecha(?)}` |

---

## 11. Auditoría de Actividad (`HistorialAuditoriaController`) — Usado por `auditoria.js`

| Método | Ruta Exacta | Mecanismo JDBC / SQL | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/auditoria/historial-cambios` | `PreparedStatement` → `SELECT * FROM vista_historial_cambios ORDER BY fecha_hora DESC` | Devuelve la bitácora generada por triggers para `auditoria.js`. |

* **Response leído por `auditoria.js` (`HistorialAuditoriaResponseDTO` / `vista_historial_cambios`):**
  ```json
  [
    {
      "id": 1,
      "tablaAfectada": "movimiento_cuenta",
      "registroId": 12,
      "campo": "monto_abonado",
      "valorAnterior": "5000.00",
      "valorNuevo": "10000.00",
      "accion": "UPDATE",
      "usuarioId": 1,
      "usuarioResponsable": "elena.vargas@psr.com",
      "fechaHora": "2026-11-22T17:45:00"
    }
  ]
  ```
