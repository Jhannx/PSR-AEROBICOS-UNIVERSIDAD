# PSR – Módulo de Aeróbicos

Sistema de información para apoyar la gestión operativa del programa de Aeróbicos de una academia de danza. El proyecto centraliza el registro de participantes, horarios, sesiones, asistencias y pagos en una base de datos relacional.

## Información académica

- **Estudiante:** Jhan Corena Segura
- **Asignatura:** Bases de Datos 2
- **Universidad:** Universidad de Córdoba
- **Periodo:** 2026 - 2
- **Sede de desarrollo:** Sahagún

## Descripción

PSR – Módulo de Aeróbicos permite digitalizar el proceso de recepción y control de las clases de aeróbicos. La solución facilita el registro de personas, la programación de horarios, el control de ingreso, la confirmación de asistencias y la administración de pagos.

También permite consultar el estado de cuenta de los participantes, revisar el historial de operaciones y generar información útil sobre asistencia, recaudo y participación.

## Flujo principal

1. Se registra la información del participante y su contacto de emergencia.
2. El sistema crea su cuenta de acceso y asigna el rol correspondiente.
3. Se configuran los horarios recurrentes y las sesiones del programa.
4. El participante registra su llegada mediante documento, código QR o apoyo del personal de recepción.
5. La asistencia se confirma y se registra el cobro de la sesión.
6. El sistema actualiza el estado de cuenta y conserva el historial de pagos y asistencias.
7. Los usuarios autorizados consultan reportes de operación, recaudo y participación.

## Funcionalidades principales

- Administración de personas, usuarios y roles.
- Gestión de horarios, excepciones y sesiones.
- Registro y control de llegadas y asistencias.
- Administración de métodos de pago y tarifas.
- Control de abonos, deudas, sesiones pagadas por adelantado y vueltos pendientes.
- Consulta del estado de cuenta e historial de cada participante.
- Gestión de contactos de emergencia.
- Auditoría de cambios relevantes.
- Consultas y reportes de asistencia, recaudo y frecuencia de participación.

## Arquitectura del proyecto

El proyecto está organizado en módulos que trabajan sobre la misma base de datos relacional:

```text
PSR AEROBICOS/
├── psr-aerobicos-backend/   # API principal y lógica de negocio
├── psr-aerobicos-frontend/  # Interfaz web del sistema
├── api-analitica-python/    # API de consultas analíticas
└── docs/                    # Propuesta, modelos y documentación técnica
```

### Tecnologías

- **Base de datos:** MySQL 8.0
- **Backend:** Java, Spring Boot y Maven
- **Frontend:** HTML, CSS, JavaScript y Bootstrap
- **API de analítica:** Python, FastAPI y consultas SQL nativas
- **Documentación de servicios:** OpenAPI / Swagger

## Cumplimiento de requisitos de Bases de Datos 2

Aunque el backend fue desarrollado con **Spring Boot**, el proyecto no utiliza ningún ORM. El acceso a la base de datos se implementó exclusivamente mediante **JDBC puro**, usando conexiones, `PreparedStatement`, `CallableStatement` y `ResultSet`.

No se utilizó Hibernate, JPA, Spring Data JPA ni ningún otro ORM. Para cubrir los requisitos de la entrega se implementaron directamente sobre MySQL las tablas, relaciones, restricciones de integridad, consultas, vistas, procedimientos almacenados, funciones, triggers y transacciones requeridas por el sistema.

## Modelo de datos

El modelo relacional contempla, entre otras, las entidades `persona`, `usuario`, `rol`, `horario`, `sesion`, `llegada`, `asistencia`, `movimiento_cuenta`, `tarifa` y `contacto_emergencia`.

La base de datos utiliza relaciones, restricciones de integridad, consultas, vistas, procedimientos almacenados, funciones, triggers y transacciones para mantener la consistencia de la información.

## Ejecución local

### Backend

El backend es una aplicación **Spring Boot** administrada con Maven. Antes de ejecutarlo se requiere tener instalado Java, una instancia de MySQL 8.0 activa y configuradas las credenciales de conexión en `psr-aerobicos-backend/src/main/resources/application.properties`.

Desde la carpeta `psr-aerobicos-backend`, ejecutar:

```bash
./mvnw spring-boot:run
```

En Windows:

```powershell
./mvnw.cmd spring-boot:run
```

El comando compila el proyecto, inicia Spring Boot y levanta la API principal. También es posible ejecutarlo desde el IDE iniciando la clase `PsrAerobicosBackendApplication`.

La API principal queda disponible en `http://localhost:8080`.

### Frontend

Abrir `psr-aerobicos-frontend/index.html` en un navegador o servir la carpeta con un servidor web local. El frontend se comunica con la API principal mediante solicitudes HTTP.

### API de analítica

Desde la carpeta `api-analitica-python`:

```bash
pip install -r requirements.txt
python app.py
```

La documentación interactiva queda disponible en `http://localhost:8000/docs`.

## Documentación

La carpeta `docs` contiene la propuesta del proyecto, el modelo relacional y la documentación formal de entrega:

- [Arquitectura de la base de datos](docs/arquitectura-base-de-datos.md)
- [Arquitectura del backend](docs/arquitectura-backend.md)
- [Flujo de datos del registro de una persona](docs/flujo-de-datos-registro-persona.md)

## Alcance

Este proyecto se enfoca en la operación del programa de Aeróbicos: recepción, sesiones, asistencias, pagos, consultas y reportes. Su diseño permite ampliar la plataforma posteriormente a otros servicios de la academia.

