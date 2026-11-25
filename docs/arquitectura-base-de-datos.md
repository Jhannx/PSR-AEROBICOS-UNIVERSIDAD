# Estructura de la base de datos

## 1. Presentación

La base de datos `psr_aerobicos` soporta la operación del módulo de Aeróbicos mediante un modelo relacional que integra participantes, usuarios, roles, horarios, sesiones, asistencias, pagos y auditoría.

El diseño aplica principios de integridad, consistencia y trazabilidad a través de claves primarias, claves foráneas, restricciones, vistas, procedimientos almacenados, triggers y transacciones.

## 2. Organización de los scripts

| Archivo | Propósito |
| --- | --- |
| `01_ddl_estructura_psr_aerobicos.sql` | Creación de tablas, claves, relaciones y restricciones. |
| `02_dml_15_registros.sql` | Carga de datos iniciales para pruebas y demostración. |
| `03_consultas-especificas.sql` | Consultas operativas y de apoyo a la gestión. |
| `04_triggers.sql` | Automatización de auditoría y actualización de totales. |
| `05_procedimientos.sql` | Operaciones transaccionales y reglas de negocio. |
| `06_vistas.sql` | Consultas derivadas para saldos, filas, reportes e historial. |
| `07_actualizar_hashes_usuarios_demo.sql` | Configuración de credenciales de demostración. |

## 3. Entidades principales

| Entidad | Responsabilidad |
| --- | --- |
| `persona` | Información personal y estado de los participantes. |
| `contacto_emergencia` | Datos del contacto de emergencia asociado a cada participante. |
| `usuario` | Credenciales y estado de las cuentas de acceso. |
| `rol` | Catálogo de perfiles de acceso del sistema. |
| `usuario_rol` | Relación entre usuarios y roles. |
| `horario` | Programación recurrente de las clases. |
| `horario_excepcion` | Fechas en las que un horario presenta una modificación. |
| `sesion` | Clase programada para una fecha y franja horaria. |
| `llegada` | Registro de ingreso de un participante a una sesión. |
| `asistencia` | Confirmación de asistencia y valor cobrado. |
| `metodo_pago` | Catálogo de medios de pago aceptados. |
| `movimiento_cuenta` | Abonos, reembolsos y movimientos económicos. |
| `tarifa` | Valor vigente de la sesión. |
| `token_recuperacion` | Gestión de recuperación de acceso. |
| `historial_auditoria` | Registro de cambios relevantes del sistema. |

## 4. Relaciones del modelo

- Una persona puede tener un contacto de emergencia y una cuenta de usuario.
- Un usuario puede tener uno o varios roles mediante `usuario_rol`.
- Un horario puede originar múltiples sesiones y excepciones.
- Una sesión puede recibir múltiples llegadas.
- Una llegada puede convertirse en una asistencia confirmada.
- Una persona puede registrar múltiples movimientos de cuenta.
- Cada movimiento se relaciona con un método de pago y puede asociarse a una llegada.
- Los usuarios registran operaciones y cambios auditables.

## 5. Integridad y automatización

La estructura utiliza claves primarias para identificar los registros, claves foráneas para mantener las relaciones y restricciones `UNIQUE`, `NOT NULL` y `CHECK` para controlar la calidad de los datos.

Los procedimientos almacenados centralizan operaciones como el registro de personas, usuarios y roles; la gestión de horarios y sesiones; el registro de llegadas y asistencias; la gestión de pagos y tarifas; la anulación de asistencias; la cancelación de sesiones y la generación de reportes.

Los triggers automatizan la actualización de acumulados de las sesiones y el registro de modificaciones relevantes en la auditoría.

Las operaciones que involucran varias entidades se ejecutan mediante transacciones, garantizando que los cambios se confirmen de forma completa o se reviertan cuando sea necesario.

## 6. Vistas y consultas derivadas

- `vista_saldo_persona`: estado de cuenta y saldo de cada participante.
- `vista_fila_hoy`: información operativa de llegadas y asistencias del día.
- `vista_fila_espera`: datos resumidos para la fila de recepción.
- `vista_recaudo_diario`: resumen de sesiones, asistencias y recaudo.
- `vista_historial_cambios`: auditoría asociada al usuario responsable.
- `vista_resumen_sesiones`: resumen de sesiones, profesores y totales.

## 7. Acceso desde la aplicación

El backend utiliza JDBC puro para comunicarse con MySQL. Las consultas se ejecutan mediante `PreparedStatement` y las operaciones almacenadas mediante `CallableStatement`, con lectura de resultados usando `ResultSet`.

No se utiliza ningún ORM. La aplicación interactúa directamente con las tablas, vistas y procedimientos definidos en este modelo relacional.
