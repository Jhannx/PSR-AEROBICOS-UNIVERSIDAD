# Diccionario de Datos (MySQL)

Esquema relacional para la gestión de recepción, asistencias y pagos del módulo de aeróbicos.

---

### 1. `rol`
Catálogo de roles de usuario en el sistema.

| Campo | Tipo | Nulo | Clave | Default | Descripción |
|---|---|:---:|:---:|---|---|
| `id` | `INT` | NO | **PK** | `AUTO_INCREMENT` | Identificador del rol. |
| `nombre` | `ENUM('ADMINISTRADOR', 'RECEPCIONISTA', 'PROFESOR', 'MIEMBRO')` | NO | **UK** | | Nombre del rol. |

---

### 2. `persona`
Datos personales de alumnos, instructores y personal administrativo.

| Campo | Tipo | Nulo | Clave | Default | Descripción |
|---|---|:---:|:---:|---|---|
| `id` | `INT` | NO | **PK** | `AUTO_INCREMENT` | Identificador de la persona. |
| `tipo_documento` | `ENUM('CC', 'TI', 'CE', 'PA')` | NO | | | Tipo de documento de identidad. |
| `numero_documento` | `VARCHAR(20)` | NO | **UK** | | Número de documento único. |
| `nombre` | `VARCHAR(100)` | NO | | | Nombres. |
| `apellidos` | `VARCHAR(100)` | NO | | | Apellidos. |
| `telefono` | `VARCHAR(20)` | NO | | | Teléfono de contacto. |
| `fecha_nacimiento` | `DATE` | SÍ | | `NULL` | Fecha de nacimiento. |
| `observaciones_salud` | `TEXT` | SÍ | | `NULL` | Condiciones médicas relevantes. |
| `fuente_registro` | `ENUM('ADMIN', 'AUTOREGISTRO')` | NO | | `'AUTOREGISTRO'` | Origen del registro. |
| `fecha_registro` | `DATETIME` | NO | | `CURRENT_TIMESTAMP` | Fecha y hora de creación del registro. |
| `activo` | `BOOLEAN` | NO | | `TRUE` | Estado de la persona en el sistema. |

---

### 3. `contacto_emergencia`
Contacto de auxilio médico o familiar de una persona.

| Campo | Tipo | Nulo | Clave | Default | Descripción |
|---|---|:---:|:---:|---|---|
| `id` | `INT` | NO | **PK** | `AUTO_INCREMENT` | Identificador del contacto. |
| `persona_id` | `INT` | NO | **FK, UK** | | `persona(id) ON DELETE CASCADE`. Relación 1:1. |
| `nombre` | `VARCHAR(150)` | SÍ | | `NULL` | Nombre del contacto. |
| `telefono` | `VARCHAR(20)` | SÍ | | `NULL` | Teléfono de urgencias. |
| `observaciones` | `TEXT` | SÍ | | `NULL` | Parentesco o indicaciones adicionales. |
| `actualizado_en` | `DATETIME` | NO | | `CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` | Fecha de última modificación. |

*Restricción:* `CHECK (nombre IS NOT NULL OR telefono IS NOT NULL OR observaciones IS NOT NULL)`

---

### 4. `usuario`
Cuentas de acceso al sistema vinculadas opcionalmente a una persona.

| Campo | Tipo | Nulo | Clave | Default | Descripción |
|---|---|:---:|:---:|---|---|
| `id` | `INT` | NO | **PK** | `AUTO_INCREMENT` | Identificador de usuario. |
| `numero_documento` | `VARCHAR(20)` | SÍ | **UK** | `NULL` | Documento para inicio de sesión. |
| `correo` | `VARCHAR(150)` | SÍ | **UK** | `NULL` | Correo electrónico de acceso. |
| `password_hash` | `VARCHAR(255)` | NO | | | Contraseña cifrada. |
| `persona_id` | `INT` | SÍ | **FK** | `NULL` | `persona(id) ON DELETE CASCADE`. |
| `activo` | `BOOLEAN` | NO | | `TRUE` | Estado de la cuenta. |
| `fecha_creacion` | `DATETIME` | NO | | `CURRENT_TIMESTAMP` | Fecha de creación. |

*Restricción:* `CHECK (numero_documento IS NOT NULL OR correo IS NOT NULL)`

---

### 5. `usuario_rol`
Asignación de roles a usuarios (relación muchos a muchos).

| Campo | Tipo | Nulo | Clave | Default | Descripción |
|---|---|:---:|:---:|---|---|
| `id` | `INT` | NO | **PK** | `AUTO_INCREMENT` | Identificador del registro. |
| `usuario_id` | `INT` | NO | **FK** | | `usuario(id) ON DELETE CASCADE`. |
| `rol_id` | `INT` | NO | **FK** | | `rol(id) ON DELETE CASCADE`. |
| `asignado_en` | `DATETIME` | NO | | `CURRENT_TIMESTAMP` | Fecha de asignación. |

*Restricción:* `UNIQUE (usuario_id, rol_id)`

---

### 6. `horario`
Franjas horarias programadas para las clases semanales.

| Campo | Tipo | Nulo | Clave | Default | Descripción |
|---|---|:---:|:---:|---|---|
| `id` | `INT` | NO | **PK** | `AUTO_INCREMENT` | Identificador del horario. |
| `dia_semana` | `TINYINT` | NO | | | Día (1 = Lunes a 7 = Domingo). |
| `hora_inicio` | `TIME` | NO | | | Hora de inicio de la clase. |
| `hora_fin` | `TIME` | NO | | | Hora de finalización. |
| `activo` | `BOOLEAN` | NO | | `TRUE` | Estado de la franja. |

*Restricciones:* `CHECK (dia_semana BETWEEN 1 AND 7)`, `CHECK (hora_fin > hora_inicio)`

---

### 7. `sesion`
Instancia real de una clase en una fecha determinada.

| Campo | Tipo | Nulo | Clave | Default | Descripción |
|---|---|:---:|:---:|---|---|
| `id` | `INT` | NO | **PK** | `AUTO_INCREMENT` | Identificador de la sesión. |
| `horario_id` | `INT` | SÍ | **FK** | `NULL` | `horario(id) ON DELETE SET NULL`. |
| `fecha` | `DATE` | NO | | | Fecha de la clase. |
| `hora_inicio` | `DATETIME` | NO | | | Inicio real de la sesión. |
| `hora_fin` | `DATETIME` | SÍ | | `NULL` | Cierre de la sesión (`NULL` si sigue activa). |
| `profesor_usuario_id` | `INT` | SÍ | **FK** | `NULL` | `usuario(id) ON DELETE SET NULL`. Instructor asignado. |
| `cancelada` | `BOOLEAN` | NO | | `FALSE` | Indica si la clase fue suspendida. |
| `total_asistencias` | `INT` | NO | | `0` | Cantidad de asistentes confirmados. |
| `total_recaudo` | `DECIMAL(10,2)` | NO | | `0.00` | Monto total recaudado en la sesión. |

*Restricción:* `UNIQUE (horario_id, fecha)`

---

### 8. `llegada`
Registro de llegada del participante a recepción antes de confirmar cobro.

| Campo | Tipo | Nulo | Clave | Default | Descripción |
|---|---|:---:|:---:|---|---|
| `id` | `INT` | NO | **PK** | `AUTO_INCREMENT` | Identificador del check-in. |
| `persona_id` | `INT` | NO | **FK** | | `persona(id) ON DELETE CASCADE`. Asistente. |
| `sesion_id` | `INT` | NO | **FK** | | `sesion(id) ON DELETE CASCADE`. Sesión a la que asiste. |
| `hora_llegada` | `DATETIME` | NO | | `CURRENT_TIMESTAMP` | Momento de registro en cola. |
| `metodo_checkin` | `ENUM('QR', 'DOCUMENTO', 'TERMINAL', 'MANUAL')` | NO | | `'MANUAL'` | Medio de check-in. |

*Restricción:* `UNIQUE (persona_id, sesion_id)`

---

### 9. `asistencia`
Confirmación presencial y registro del cobro por sesión.

| Campo | Tipo | Nulo | Clave | Default | Descripción |
|---|---|:---:|:---:|---|---|
| `id` | `INT` | NO | **PK** | `AUTO_INCREMENT` | Identificador de la asistencia. |
| `llegada_id` | `INT` | NO | **FK, UK** | | `llegada(id) ON DELETE CASCADE`. Relación 1:1. |
| `monto_cobrado` | `DECIMAL(10,2)` | NO | | | Valor cobrado por la clase. |
| `marcado_por` | `INT` | NO | **FK** | | `usuario(id) ON DELETE RESTRICT`. Recepcionista que confirma. |
| `hora_marcado` | `DATETIME` | NO | | `CURRENT_TIMESTAMP` | Momento de confirmación. |

*Restricción:* `CHECK (monto_cobrado >= 0.00)`

---

### 10. `movimiento_cuenta`
Registro contable de pagos, abonos y devoluciones.

| Campo | Tipo | Nulo | Clave | Default | Descripción |
|---|---|:---:|:---:|---|---|
| `id` | `INT` | NO | **PK** | `AUTO_INCREMENT` | Identificador del movimiento. |
| `persona_id` | `INT` | NO | **FK** | | `persona(id) ON DELETE CASCADE`. |
| `llegada_id` | `INT` | SÍ | **FK** | `NULL` | `llegada(id) ON DELETE SET NULL`. |
| `monto_recibido` | `DECIMAL(10,2)` | NO | | | Monto entregado por el cliente. |
| `monto_abonado` | `DECIMAL(10,2)` | NO | | | Monto efectivamente abonado. |
| `metodo_pago` | `ENUM('EFECTIVO', 'NEQUI', 'DAVIPLATA', 'TRANSFERENCIA')` | NO | | `'EFECTIVO'` | Medio de pago utilizado. |
| `tipo_movimiento` | `ENUM('ABONO', 'REEMBOLSO')` | NO | | `'ABONO'` | Tipo de operación contable. |
| `vuelto_entregado` | `BOOLEAN` | NO | | `TRUE` | Indica si se entregó el cambio. |
| `fecha_hora` | `DATETIME` | NO | | `CURRENT_TIMESTAMP` | Fecha y hora del movimiento. |
| `usuario_id` | `INT` | NO | **FK** | | `usuario(id) ON DELETE RESTRICT`. Usuario que procesó el pago. |
| `nota` | `TEXT` | SÍ | | `NULL` | Observaciones adicionales. |

*Restricción:* `CHECK (monto_recibido >= 0.00 AND monto_abonado >= 0.00)`

---

### 11. `tarifa`
Configuración del valor de la sesión individual.

| Campo | Tipo | Nulo | Clave | Default | Descripción |
|---|---|:---:|:---:|---|---|
| `id` | `INT` | NO | **PK** | `1` | Fila única fija (`CHECK id = 1`). |
| `monto` | `DECIMAL(10,2)` | NO | | | Precio vigente por clase. |
| `actualizado_en` | `DATETIME` | NO | | `CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` | Fecha de cambio. |
| `actualizado_por` | `INT` | SÍ | **FK** | `NULL` | `usuario(id) ON DELETE SET NULL`. |

*Restricción:* `CHECK (id = 1)`, `CHECK (monto > 0.00)`

---

### 12. `historial_auditoria`
Registro de modificaciones y eliminaciones de registros en el sistema.

| Campo | Tipo | Nulo | Clave | Default | Descripción |
|---|---|:---:|:---:|---|---|
| `id` | `INT` | NO | **PK** | `AUTO_INCREMENT` | Identificador del evento. |
| `tabla_afectada` | `VARCHAR(50)` | NO | | | Nombre de la tabla modificada. |
| `registro_id` | `INT` | NO | | | ID del registro afectado. |
| `campo` | `VARCHAR(50)` | SÍ | | `NULL` | Columna modificada. |
| `valor_anterior` | `TEXT` | SÍ | | `NULL` | Valor previo al cambio. |
| `valor_nuevo` | `TEXT` | SÍ | | `NULL` | Valor nuevo (`NULL` si fue eliminado). |
| `accion` | `ENUM('INSERT', 'UPDATE', 'DELETE')` | NO | | | Operación realizada. |
| `usuario_id` | `INT` | SÍ | **FK** | `NULL` | `usuario(id) ON DELETE SET NULL`. |
| `fecha_hora` | `DATETIME` | NO | | `CURRENT_TIMESTAMP` | Momento de la operación. |

---

## 3. Relaciones y Claves Foráneas

| Tabla Origen | Columna FK | Tabla Destino | ON DELETE |
|---|---|---|:---:|
| `usuario` | `persona_id` | `persona(id)` | **CASCADE** |
| `contacto_emergencia` | `persona_id` | `persona(id)` | **CASCADE** |
| `usuario_rol` | `usuario_id` | `usuario(id)` | **CASCADE** |
| `usuario_rol` | `rol_id` | `rol(id)` | **CASCADE** |
| `sesion` | `horario_id` | `horario(id)` | **SET NULL** |
| `sesion` | `profesor_usuario_id` | `usuario(id)` | **SET NULL** |
| `llegada` | `persona_id` | `persona(id)` | **CASCADE** |
| `llegada` | `sesion_id` | `sesion(id)` | **CASCADE** |
| `asistencia` | `llegada_id` | `llegada(id)` | **CASCADE** |
| `asistencia` | `marcado_por` | `usuario(id)` | **RESTRICT** |
| `movimiento_cuenta` | `persona_id` | `persona(id)` | **CASCADE** |
| `movimiento_cuenta` | `llegada_id` | `llegada(id)` | **SET NULL** |
| `movimiento_cuenta` | `usuario_id` | `usuario(id)` | **RESTRICT** |
| `tarifa` | `actualizado_por` | `usuario(id)` | **SET NULL** |
| `historial_auditoria` | `usuario_id` | `usuario(id)` | **SET NULL** |
