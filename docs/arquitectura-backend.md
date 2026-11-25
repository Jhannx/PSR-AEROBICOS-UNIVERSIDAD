# Estructura del backend

## 1. Presentación

El backend del sistema PSR – Módulo de Aeróbicos está desarrollado con Java y Spring Boot. Su función es exponer los servicios de la aplicación, aplicar las reglas de negocio y gestionar la comunicación con la base de datos MySQL.

La solución se organiza por módulos funcionales y utiliza una arquitectura por capas para separar responsabilidades.

## 2. Arquitectura por capas

```text
Solicitud HTTP
      │
      ▼
Controller  →  Service  →  DAO  →  MySQL
      │           │         │
      ▼           ▼         ▼
 Response     Reglas     JDBC puro
 DTO          de negocio
```

### Controller

Recibe las solicitudes HTTP, valida los datos de entrada, invoca el servicio correspondiente y construye la respuesta de la API.

### Service

Concentra las reglas de negocio, coordina las operaciones y prepara la información que se enviará a la base de datos.

### DAO

Ejecuta las consultas SQL y los procedimientos almacenados mediante JDBC puro. Esta capa administra conexiones, parámetros, resultados y mapeo de registros.

### DTO y Mapper

Los DTO definen los contratos de entrada y salida de la API. Los mappers convierten los resultados de la base de datos en objetos de respuesta para el frontend.

### Manejo de respuestas y errores

La API utiliza una estructura de respuesta uniforme y un componente centralizado para procesar validaciones, recursos no encontrados, conflictos y errores de conexión.

## 3. Organización de módulos

| Módulo | Funcionalidad |
| --- | --- |
| `person` | Registro y administración de participantes. |
| `user` | Usuarios, credenciales y roles. |
| `aerobics.schedule` | Horarios y sesiones. |
| `aerobics.attendance` | Llegadas y asistencias. |
| `aerobics.payment` | Movimientos de cuenta y métodos de pago. |
| `aerobics.pricing` | Administración de tarifas. |
| `aerobics.queue` | Gestión de la fila de recepción. |
| `aerobics.audit` | Historial de cambios. |
| `shared` | Configuración, seguridad, respuestas y excepciones compartidas. |

Cada módulo puede contener sus propios paquetes de `controller`, `service`, `dao`, `dto`, `mapper` y `enums`.

## 4. Persistencia con JDBC puro

El proyecto cumple el requisito de implementar el acceso a datos sin ORM:

- Se utilizan conexiones JDBC para acceder a MySQL.
- Las consultas parametrizadas se ejecutan mediante `PreparedStatement`.
- Los procedimientos almacenados se invocan mediante `CallableStatement`.
- Los resultados se recorren mediante `ResultSet`.
- Las entidades de la base de datos se mapean manualmente a objetos y DTO.
- No se utiliza Hibernate, JPA, Spring Data JPA ni ningún otro ORM.

Esta decisión permite evidenciar directamente el uso de SQL, procedimientos, vistas, triggers y transacciones solicitados en la asignatura.

## 5. Servicios principales

El backend expone operaciones para administrar personas, usuarios, roles, horarios, sesiones, llegadas, asistencias, pagos, tarifas, filas de recepción, reportes e historial de auditoría.

## 6. Flujo de una solicitud

1. El frontend envía una solicitud HTTP con los datos de la operación.
2. El controller recibe y valida el request DTO.
3. El service aplica las reglas de negocio.
4. El DAO prepara una consulta SQL o invoca un procedimiento almacenado mediante JDBC.
5. MySQL ejecuta la operación sobre el modelo relacional.
6. El DAO transforma el resultado en una entidad o DTO.
7. El controller responde con el resultado y el código HTTP correspondiente.

## 7. Seguridad y consistencia

Las contraseñas se almacenan mediante hash BCrypt. Las consultas se parametrizan para proteger los datos enviados por el usuario. La consistencia se refuerza mediante validaciones en Java, restricciones del modelo relacional, procedimientos almacenados y transacciones.
