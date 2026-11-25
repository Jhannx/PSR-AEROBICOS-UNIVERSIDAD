# Modelo Entidad-Relación (E-R) — PSR Módulo Aeróbicos

Este documento define la estructura formal de entidades, atributos y relaciones del modelo relacional, normalizado en **Tercera Forma Normal (3FN)** e implementado en **MySQL 8.0**.

---

## 1. Entidades y Atributos

### 1. `rol`
Catálogo de roles y permisos del sistema.

| Atributo | Tipo de Dato | Clave | Nulo | Default | Descripción |
|---|---|:---:|:---:|---|---|
| `id` | `INT` | **PK** | NO | `AUTO_INCREMENT` | Identificador único del rol. |
| `nombre` | `ENUM('ADMINISTRADOR', 'RECEPCIONISTA', 'PROFESOR', 'MIEMBRO')` | **UK** | NO | | Nombre único del rol. |

---

### 2. `persona`
Registro central de individuos (alumnos, instructores y personal administrativo).

| Atributo | Tipo de Dato | Clave | Nulo | Default | Descripción |
|---|---|:---:|:---:|---|---|
| `id` | `INT` | **PK** | NO | `AUTO_INCREMENT` | Identificador único de la persona. |
| `tipo_documento` | `ENUM('CC', 'TI', 'CE', 'PA')` | | NO | | Tipo de documento de identidad. |
| `numero_documento` | `VARCHAR(20)` | **UK** | NO | | Cédula o documento de identidad único. |
| `nombre` | `VARCHAR(100)` | | NO | | Nombres de la persona. |
| `apellidos` | `VARCHAR(100)` | | NO | | Apellidos de la persona. |
| `telefono` | `VARCHAR(20)` | | NO | | Teléfono principal de contacto. |
| `fecha_nacimiento` | `DATE` | | SÍ | `NULL` | Fecha de nacimiento para cálculo de edad. |
| `observaciones_salud` | `TEXT` | | SÍ | `NULL` | Condiciones médicas o alergias. |
| `fuente_registro` | `ENUM('ADMIN', 'AUTOREGISTRO')` | | NO | `'AUTOREGISTRO'` | Origen del registro. |
| `fecha_registro` | `DATETIME` | | NO | `CURRENT_TIMESTAMP` | Fecha y hora de vinculación. |
| `activo` | `BOOLEAN` | | NO | `TRUE` | Estado de la persona en el sistema. |

---

### 3. `contacto_emergencia`
Información de auxilio médico o familiar de una persona.

| Atributo | Tipo de Dato | Clave | Nulo | Default | Descripción |
|---|---|:---:|:---:|---|---|
| `id` | `INT` | **PK** | NO | `AUTO_INCREMENT` | Identificador único del contacto. |
| `persona_id` | `INT` | **FK, UK** | NO | | Referencia única a `persona(id)`. Relación 1:1. |
| `nombre` | `VARCHAR(150)` | | SÍ | `NULL` | Nombre de la persona de contacto. |
| `telefono` | `VARCHAR(20)` | | SÍ | `NULL` | Teléfono de urgencias. |
| `observaciones` | `TEXT` | | SÍ | `NULL` | Parentesco o instrucciones médicas. |
| `actualizado_en` | `DATETIME` | | NO | `CURRENT_TIMESTAMP ON UPDATE` | Fecha de última modificación. |

*Restricción:* `CHECK (nombre IS NOT NULL OR telefono IS NOT NULL OR observaciones IS NOT NULL)`

---

### 4. `usuario`
Credenciales de autenticación para acceso al sistema.

| Atributo | Tipo de Dato | Clave | Nulo | Default | Descripción |
|---|---|:---:|:---:|---|---|
| `id` | `INT` | **PK** | NO | `AUTO_INCREMENT` | Identificador único de usuario. |
| `numero_documento` | `VARCHAR(20)` | **UK** | SÍ | `NULL` | Documento de ingreso. |
| `correo` | `VARCHAR(150)` | **UK** | SÍ | `NULL` | Correo electrónico de acceso. |
| `password_hash` | `VARCHAR(255)` | | NO | | Contraseña encriptada. |
| `persona_id` | `INT` | **FK** | SÍ | `NULL` | Referencia a `persona(id)` si aplica. |
| `activo` | `BOOLEAN` | | NO | `TRUE` | Habilitación de la cuenta. |
| `fecha_creacion` | `DATETIME` | | NO | `CURRENT_TIMESTAMP` | Fecha de registro de la cuenta. |

*Restricción:* `CHECK (numero_documento IS NOT NULL OR correo IS NOT NULL)`

---

### 5. `usuario_rol`
Tabla asociativa para la cardinalidad Muchos a Muchos (N:M) entre usuarios y roles.

| Atributo | Tipo de Dato | Clave | Nulo | Default | Descripción |
|---|---|:---:|:---:|---|---|
| `id` | `INT` | **PK** | NO | `AUTO_INCREMENT` | Identificador de la asignación. |
| `usuario_id` | `INT` | **FK** | NO | | Referencia a `usuario(id)`. |
| `rol_id` | `INT` | **FK** | NO | | Referencia a `rol(id)`. |
| `asignado_en` | `DATETIME` | | NO | `CURRENT_TIMESTAMP` | Fecha y hora de asignación del rol. |

*Restricción de unicidad:* `UNIQUE (usuario_id, rol_id)`

---

### 6. `horario`
Plantilla de programación recurrente semanal para las clases.

| Atributo | Tipo de Dato | Clave | Nulo | Default | Descripción |
|---|---|:---:|:---:|---|---|
| `id` | `INT` | **PK** | NO | `AUTO_INCREMENT` | Identificador único de la franja. |
| `dia_semana` | `TINYINT` | | NO | | Día de la semana (1 = Lunes, 7 = Domingo). |
| `hora_inicio` | `TIME` | | NO | | Hora de inicio programada. |
| `hora_fin` | `TIME` | | NO | | Hora de finalización programada. |
| `activo` | `BOOLEAN` | | NO | `TRUE` | Vigencia de la franja horaria. |

*Restricciones:* `CHECK (dia_semana BETWEEN 1 AND 7)` y `CHECK (hora_fin > hora_inicio)`

---

### 7. `sesion`
Clase individual ejecutada o programada en una fecha específica.

| Atributo | Tipo de Dato | Clave | Nulo | Default | Descripción |
|---|---|:---:|:---:|---|---|
| `id` | `INT` | **PK** | NO | `AUTO_INCREMENT` | Identificador de la clase. |
| `horario_id` | `INT` | **FK** | SÍ | `NULL` | Plantilla de horario base referenciada. |
| `fecha` | `DATE` | | NO | | Fecha en que se dicta la clase. |
| `hora_inicio` | `DATETIME` | | NO | | Momento real de apertura de la clase. |
| `hora_fin` | `DATETIME` | | SÍ | `NULL` | Momento de cierre de la clase. |
| `profesor_usuario_id`| `INT` | **FK** | SÍ | `NULL` | Instructor a cargo referenciando a `usuario(id)`. |
| `cancelada` | `BOOLEAN` | | NO | `FALSE` | Bandera si la clase fue suspendida. |
| `total_asistencias` | `INT` | | NO | `0` | Conteo acumulado de asistentes. |
| `total_recaudo` | `DECIMAL(10,2)` | | NO | `0.00` | Recaudo acumulado en dinero. |

*Restricción de unicidad:* `UNIQUE (horario_id, fecha)`

---

### 8. `llegada`
Registro de ingreso (check-in) físico del alumno a una sesión de clase.

| Atributo | Tipo de Dato | Clave | Nulo | Default | Descripción |
|---|---|:---:|:---:|---|---|
| `id` | `INT` | **PK** | NO | `AUTO_INCREMENT` | Identificador de la llegada. |
| `persona_id` | `INT` | **FK** | NO | | Alumno que ingresa (`persona.id`). |
| `sesion_id` | `INT` | **FK** | NO | | Clase a la que asiste (`sesion.id`). |
| `hora_llegada` | `DATETIME` | | NO | `CURRENT_TIMESTAMP` | Marca temporal exacta del ingreso. |
| `metodo_checkin` | `ENUM('QR', 'DOCUMENTO', 'TERMINAL', 'MANUAL')` | | NO | `'MANUAL'` | Mecanismo utilizado para registrar la llegada. |

*Restricción de unicidad:* `UNIQUE (persona_id, sesion_id)`

---

### 9. `asistencia`
Validación y cobro de ingreso generado a partir de una llegada confirmada.

| Atributo | Tipo de Dato | Clave | Nulo | Default | Descripción |
|---|---|:---:|:---:|---|---|
| `id` | `INT` | **PK** | NO | `AUTO_INCREMENT` | Identificador del cobro de asistencia. |
| `llegada_id` | `INT` | **FK, UK** | NO | | Llegada asociada (`llegada.id`). Relación 1:1. |
| `monto_cobrado` | `DECIMAL(10,2)` | | NO | | Tarifa cobrada por la clase. |
| `marcado_por` | `INT` | **FK** | NO | | Recepcionista que valida (`usuario.id`). |
| `hora_marcado` | `DATETIME` | | NO | `CURRENT_TIMESTAMP` | Momento del cobro/confirmación. |

*Restricción:* `CHECK (monto_cobrado >= 0.00)`

---

### 10. `movimiento_cuenta`
Registro contable de ingresos, abonos y devoluciones de dinero.

| Atributo | Tipo de Dato | Clave | Nulo | Default | Descripción |
|---|---|:---:|:---:|---|---|
| `id` | `INT` | **PK** | NO | `AUTO_INCREMENT` | Identificador del movimiento financiero. |
| `persona_id` | `INT` | **FK** | NO | | Titular del pago (`persona.id`). |
| `llegada_id` | `INT` | **FK** | SÍ | `NULL` | Llegada vinculada si pagó en clase. |
| `monto_recibido` | `DECIMAL(10,2)` | | NO | | Efectivo o valor transferido por el cliente. |
| `monto_abonado` | `DECIMAL(10,2)` | | NO | | Monto acreditado a la cuenta/servicio. |
| `metodo_pago` | `ENUM('EFECTIVO', 'NEQUI', 'DAVIPLATA', 'TRANSFERENCIA')` | | NO | `'EFECTIVO'` | Canal o pasarela de pago. |
| `tipo_movimiento` | `ENUM('ABONO', 'REEMBOLSO')` | | NO | `'ABONO'` | Naturaleza del movimiento. |
| `vuelto_entregado`| `BOOLEAN` | | NO | `TRUE` | Confirmación de cambio devuelto. |
| `fecha_hora` | `DATETIME` | | NO | `CURRENT_TIMESTAMP` | Fecha y hora de la transacción. |
| `usuario_id` | `INT` | **FK** | NO | | Cajero/recepcionista responsable (`usuario.id`).|
| `nota` | `TEXT` | | SÍ | `NULL` | Observación o concepto de pago. |

*Restricción:* `CHECK (monto_recibido >= 0.00 AND monto_abonado >= 0.00)`

---

### 11. `tarifa`
Parámetro singleton del precio vigente por clase individual.

| Atributo | Tipo de Dato | Clave | Nulo | Default | Descripción |
|---|---|:---:|:---:|---|---|
| `id` | `INT` | **PK** | NO | `1` | Registro único con id fijo = 1. |
| `monto` | `DECIMAL(10,2)` | | NO | | Valor en pesos colombianos de la clase. |
| `actualizado_en` | `DATETIME` | | NO | `CURRENT_TIMESTAMP ON UPDATE` | Fecha del último ajuste de precio. |
| `actualizado_por`| `INT` | **FK** | SÍ | `NULL` | Administrador que cambió el precio (`usuario.id`). |

*Restricciones:* `CHECK (id = 1)` y `CHECK (monto > 0.00)`

---

### 12. `historial_auditoria`
Bitácora de auditoría transaccional del sistema.

| Atributo | Tipo de Dato | Clave | Nulo | Default | Descripción |
|---|---|:---:|:---:|---|---|
| `id` | `INT` | **PK** | NO | `AUTO_INCREMENT` | Identificador único del evento de auditoría. |
| `tabla_afectada` | `VARCHAR(50)` | | NO | | Nombre de la entidad modificada. |
| `registro_id` | `INT` | | NO | | Clave primaria del registro intervenido. |
| `campo` | `VARCHAR(50)` | | SÍ | `NULL` | Columna específica modificada. |
| `valor_anterior` | `TEXT` | | SÍ | `NULL` | Estado previo del dato. |
| `valor_nuevo` | `TEXT` | | SÍ | `NULL` | Nuevo estado del dato. |
| `accion` | `ENUM('INSERT', 'UPDATE', 'DELETE')` | | NO | | Operación DML ejecutada. |
| `usuario_id` | `INT` | **FK** | SÍ | `NULL` | Usuario que ejecutó la acción. |
| `fecha_hora` | `DATETIME` | | NO | `CURRENT_TIMESTAMP` | Momento exacto del cambio. |

---

## 2. Matriz de Relaciones del Modelo

| # | Entidad Origen | Cardinalidad | Entidad Destino | Clave Foránea (FK) | Regla de Integridad / Negocio |
|:---:|---|:---:|---|---|---|
| **1** | `persona` | **1 : 1** | `contacto_emergencia` | `contacto_emergencia.persona_id` | Una persona tiene máximo un contacto de auxilio médico (`ON DELETE CASCADE`). |
| **2** | `persona` | **1 : N** | `usuario` | `usuario.persona_id` | Una persona puede tener asociada una cuenta de acceso al sistema (`ON DELETE CASCADE`). |
| **3** | `usuario` | **N : M** | `rol` | `usuario_rol(usuario_id, rol_id)` | Un usuario puede tener múltiples roles mediante la tabla intermedia `usuario_rol` (`ON DELETE CASCADE`). |
| **4** | `horario` | **1 : N** | `sesion` | `sesion.horario_id` | Muchas sesiones de clase se calendarizan basadas en una franja horaria recurrente (`ON DELETE SET NULL`). |
| **5** | `usuario` | **1 : N** | `sesion` | `sesion.profesor_usuario_id` | Un profesor imparte múltiples sesiones de clase (`ON DELETE SET NULL`). |
| **6** | `persona` | **1 : N** | `llegada` | `llegada.persona_id` | Un alumno registra múltiples check-ins o llegadas a lo largo del tiempo (`ON DELETE CASCADE`). |
| **7** | `sesion` | **1 : N** | `llegada` | `llegada.sesion_id` | Una clase recibe muchos alumnos. `UNIQUE(persona_id, sesion_id)` impide ingresos duplicados por clase (`ON DELETE CASCADE`). |
| **8** | `llegada` | **1 : 1** | `asistencia` | `asistencia.llegada_id` | Cada llegada confirmada tiene un único registro de cobro y validación (`ON DELETE CASCADE`). |
| **9** | `usuario` | **1 : N** | `asistencia` | `asistencia.marcado_por` | Un recepcionista valida y marca los cobros de múltiples asistencias (`ON DELETE RESTRICT`). |
| **10**| `persona` | **1 : N** | `movimiento_cuenta` | `movimiento_cuenta.persona_id` | Una persona realiza múltiples abonos o recibe reembolsos (`ON DELETE CASCADE`). |
| **11**| `llegada` | **1 : N** | `movimiento_cuenta` | `movimiento_cuenta.llegada_id` | Un pago puede asociarse a una llegada específica o ser nulo si es abono anticipado (`ON DELETE SET NULL`). |
| **12**| `usuario` | **1 : N** | `movimiento_cuenta` | `movimiento_cuenta.usuario_id` | Un cajero/recepcionista registra múltiples movimientos financieros (`ON DELETE RESTRICT`). |
| **13**| `usuario` | **1 : N** | `tarifa` | `tarifa.actualizado_por` | Un administrador audita o actualiza el monto vigente de la clase (`ON DELETE SET NULL`). |
| **14**| `usuario` | **1 : N** | `historial_auditoria` | `historial_auditoria.usuario_id` | Un usuario queda registrado como autor de las modificaciones del sistema (`ON DELETE SET NULL`). |
