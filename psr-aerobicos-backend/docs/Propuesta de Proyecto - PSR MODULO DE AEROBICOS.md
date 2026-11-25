# Propuesta de Proyecto – Bases de datos II

Jhan Paul Corena Segura

Facultad de Ingeniería, Universidad de Córdoba

Bases de datos ll

Juan Manuel Muskus

Título del proyecto: Sistema de información operacional para el programa de Aeróbicos de una academia de danza.

Descripción del Problema: El presente proyecto se enfoca en la gestión del proceso de recepción del programa de Aeróbicos de una academia de danza.

Dentro de la academia se ofrecen diferentes programas y servicios, siendo Aeróbicos uno de los de mayor asistencia diaria. Actualmente, el ingreso de los participantes y el control de pagos se realizan de forma manual, obligando a la persona encargada de recepción a identificar a cada asistente, verificar su pago, registrar el método utilizado, recibir efectivo o confirmar transferencias, entregar cambio cuando corresponde y organizar el orden de llegada. Lo que provoca confusiones entre gente que asiste y no paga, gente que dejó sesiones pagas y es dificil de verificarlo y constantemente se pierde mucho tiempo en este proceso lo que hace las sesiones más cortas.

Es decir, tiene un impacto directo en la información histórica, pagos realizados, deudas de participantes, y de forma general en reportes confiables sobre el funcionamiento de este programa. Del mismo modo, los participantes dependen completamente del recepcionista para conocer el estado de su cuenta, verificar cuántas sesiones tienen disponibles o confirmar si se encuentran al día con sus pagos.

Esto principalmente sucede cuando varias personas llegan al mismo tiempo, estas actividades se concentran en un único responsable siendo el recepcionista o el mismo profesor que se encargará de la clase.

Ante esta necesidad, se propone desarrollar PSR – Módulo Aeróbicos, el primer módulo de una plataforma orientada a digitalizar diferentes procesos operativos de una academia de danza. Aunque la plataforma está pensada para incorporar en el futuro funciones como el préstamo de vestuarios, las clases personalizadas y otros servicios de la academia, este proyecto se enfocará únicamente en el programa de Aeróbicos, ya que es el proceso con mayor volumen de operación y donde actualmente se presentan los principales problemas de recepción, asistencia y control de pagos.

## Objetivo general

Desarrollar un sistema de información que permita administrar de forma estructurada el proceso de recepción del programa de Aeróbicos de una academia de danza, gestionando personas, horarios, sesiones, asistencia y pagos mediante una base de datos relacional y mecanismos de automatización que garanticen la integridad y consistencia de la información.

## Objetivos específicos

Diseñar un modelo de base de datos que represente las entidades y relaciones involucradas en el proceso de recepción del programa de Aeróbicos.

Implementar la estructura de la base de datos mediante SQL, definiendo tablas, relaciones, restricciones y reglas de integridad.

Gestionar la programación de horarios recurrentes, sus excepciones y las sesiones del programa.

Administrar la información de las personas participantes, usuarios y roles del sistema.

Registrar la asistencia de los participantes mediante documento de identidad o código QR, permitiendo el ingreso autónomo y reduciendo el tiempo de recepción.

Gestionar los pagos realizados en cada sesión, incluyendo métodos de pago, tarifas vigentes, deudas, sesiones pagadas por adelantado y vueltos pendientes.

Permitir que los participantes consulten su estado de cuenta y su historial de sesiones sin depender de una verificación manual.

Gestionar el contacto de emergencia de cada participante como información complementaria del registro.

Mantener un historial de cambios sobre operaciones relevantes como pagos, tarifas, asistencias y configuraciones del sistema.

Desarrollar consultas y reportes relacionados con asistencia, recaudo, frecuencia de participación y comportamiento de las sesiones.

Implementar procedimientos, funciones, triggers y transacciones que garanticen la consistencia e integridad de la información.

Desarrollar una aplicación que permita realizar operaciones de inserción, consulta, actualización y eliminación sobre las entidades principales del sistema.

## Alcance

El sistema permitirá gestionar de forma estructurada la información relacionada con la operación diaria del programa de Aeróbicos, centralizando el proceso de recepción, el registro de asistencia, el control de pagos y la consulta de información histórica desde una única aplicación conectada a una base de datos relacional.

### Incluye

Registro y administración de personas participantes.

Gestión de usuarios y roles del sistema.

Programación de horarios recurrentes y registro de excepciones para fechas específicas.

Administración de sesiones del programa de Aeróbicos.

Registro de ingreso autónomo mediante documento de identidad o código QR.

Control de la cola de recepción con visualización del orden de llegada y estado de atención en tiempo real.

Registro, consulta y actualización de pagos.

Administración de métodos de pago.

Configuración de la tarifa vigente por sesión.

Control de deudas, sesiones pagadas por adelantado y estado de cuenta de cada participante.

Registro y seguimiento de vueltos pendientes hasta su entrega.

Gestión del contacto de emergencia de los participantes.

Historial de asistencia por participante y por sesión.

Historial de cambios sobre información relevante del sistema.

Consultas y reportes operativos de asistencia, recaudo y participación.

Operaciones CRUD sobre las entidades principales desde una aplicación conectada a la base de datos.

### No incluye

Los demás programas o disciplinas de la academia, solo se incluye el programa de aerobicos.

Integración con pasarelas de pago (Nequi, Wompi, Mercado Pago, etc.). los pagos únicamente serán registrados dentro del sistema como forma de control, no de procesamiento.

Facturación electrónica.

## Información disponible para administración

El sistema permitirá consultar indicadores operativos como:

Número de asistentes por sesión.

Promedio de asistencia por período.

Recaudo diario, semanal y mensual.

Estado de cuenta de los participantes.

Personas con mayor frecuencia de asistencia.

Métodos de pago más utilizados.

Historial de pagos y de asistencias.

Historial de cambios realizados sobre la información del sistema.

## Diseño del Sistema

Durante el desarrollo del proyecto se contemplará la elaboración de los siguientes componentes:

Modelo Entidad–Relación (ER): El sistema propuesto se orienta a la gestión del proceso de recepción del programa de Aeróbicos de una academia de danza, integrando el registro de personas, usuarios y roles, la programación de horarios y sesiones, el control de ingreso y asistencia, la administración de pagos y la configuración de los procesos operativos de recepción. A continuación, se presentan las entidades principales junto con sus atributos, así como las relaciones que estructuran el modelo.

### Entidades y atributos

Rol: define los roles del sistema. Sus atributos son: identificador (id) y nombre (administrador, recepcionista, miembro o profesor). (Se identificará al participante como miembro al existir como usuario en la base de datos)

Persona: representa a cada participante del programa, que acopla toda la información personal. Está compuesta por los atributos: identificador (id), tipo de documento, número de documento, nombre, apellidos, teléfono, fuente de registro ([AUTOREGISTRO, ADMIN] con objetivo de futuras analíticas), fecha de registro y estado (activo).

Contacto de Emergencia: almacena el contacto de emergencia de una persona. Sus atributos son: identificador (id), nombre, teléfono, observaciones y fecha de actualización.

Usuario: representa la cuenta de acceso al sistema de una persona. Está compuesta por: identificador (id), documento de identidad, correo electrónico, contraseña, estado (activo) y fecha de creación.

Horario: define la programación recurrente de una clase. Sus atributos son: identificador (id), día de la semana, hora de inicio, hora de finalización y estado (activo).

Horario Excepción: registra las fechas puntuales en que un horario recurrente no se dicta. Está compuesta por: identificador (id), fecha y motivo.

Sesión: corresponde a la instancia real de una clase en una fecha determinada. Sus atributos son: identificador (id), fecha, hora de inicio, hora de finalización y estado.

Llegada: registra el momento en que un participante llega a una sesión. Está compuesta por: identificador (id), hora de llegada y método de ingreso (documento, código QR o manual).

Asistencia: representa la confirmación de una llegada por parte del administrador, junto con el cobro correspondiente. Sus atributos son: identificador (id), monto cobrado y hora de confirmación.

Método de Pago: define los métodos de pago aceptados por el sistema. Está compuesta por: identificador (id), nombre y estado (activo).

Movimiento de Cuenta: registra cada pago o reembolso realizado por un participante. Sus atributos son: identificador (id), monto recibido, monto abonado, tipo de movimiento (abono o reembolso), estado del vuelto (entregado), fecha y hora, y nota.

Token de Recuperación: gestiona la recuperación de contraseña de un usuario. Está compuesta por: identificador (id), token, fecha de creación, fecha de expiración y estado (usado).

Tarifa: almacena el valor vigente de la sesión. Sus atributos son: monto y fecha de la última actualización.

Configuración de Cola: almacena los parámetros del proceso de recepción. Está compuesta por: personas atendidas por turno, minutos de margen para el ingreso, minutos de visibilidad de un pago reciente en la cola y fecha de la última actualización.

Historial de Cambio: registra el historial de modificaciones realizadas sobre información relevante del sistema. Sus atributos son: identificador (id), tabla afectada, campo modificado, valor anterior, valor nuevo y fecha y hora del cambio.

### Relaciones y cardinalidades

Persona — Contacto de Emergencia (1:1): una persona puede tener un único contacto de emergencia asociado y este pertenece exclusivamente a esa persona. La relación es opcional.

Persona — Usuario (1:1): una persona puede tener una única cuenta de acceso al sistema.

Usuario — Rol (N:M): un usuario puede tener múltiples roles y un rol puede estar asignado a múltiples usuarios. La relación se implementa mediante la entidad intermedia RolUsuario.

Horario — Horario Excepción (1:N): un horario recurrente puede tener múltiples excepciones, mientras que cada excepción pertenece a un único horario.

Horario — Sesión (1:N): un horario puede originar múltiples sesiones a lo largo del tiempo. Una sesión puede estar asociada a un horario o ser creada manualmente.

Usuario — Sesión (Profesor asignado) (1:N): un profesor puede estar asignado a múltiples sesiones y cada sesión tiene, opcionalmente, un único profesor asignado.

Persona — Llegada (1:N): una persona puede registrar múltiples llegadas, una por cada sesión a la que asiste.

Sesión — Llegada (1:N): una sesión puede recibir múltiples registros de llegada y cada llegada pertenece a una única sesión.

Llegada — Asistencia (1:1): una llegada puede tener, como máximo, una confirmación de asistencia asociada. La relación es opcional mientras el participante permanece en la cola de recepción.

Usuario — Asistencia (1:N): un usuario puede confirmar múltiples asistencias y cada asistencia es confirmada por un único usuario.

Persona — Movimiento de Cuenta (1:N): una persona puede tener múltiples movimientos económicos y cada movimiento pertenece exclusivamente a esa persona.

Llegada — Movimiento de Cuenta (1:N): un movimiento de cuenta puede estar asociado, de forma opcional, a una llegada específica.

Método de Pago — Movimiento de Cuenta (1:N): un método de pago puede utilizarse en múltiples movimientos y cada movimiento utiliza un único método de pago.

Usuario — Movimiento de Cuenta (1:N): un usuario puede registrar múltiples movimientos de cuenta dentro del sistema.

Movimiento de Cuenta — Movimiento de Cuenta (1:N): un movimiento de tipo abono puede tener asociados múltiples movimientos de tipo reembolso, formando una relación autorreferencial y opcional.

Usuario — Token de Recuperación (1:N): un usuario puede generar múltiples solicitudes de recuperación de contraseña a lo largo del tiempo.

Usuario — Historial de Cambio (1:N): un usuario puede generar múltiples registros en el historial de cambios mediante las operaciones que realiza sobre la información del sistema.

Usuario — Tarifa / Configuración de Cola (1:N): tanto Tarifa como Configuración de Cola registran el usuario que realizó su última actualización, siendo una relación opcional en ambos casos.

### Diagrama ER – Núcleo de Operación

Nota: El diagrama representa una vista simplificada de las principales entidades y relaciones que intervienen en la operación del programa de Aeróbicos.

## Mockups del Sistema

Mockups simples que representen las pantallas principales.

## Plan de Trabajo

1. Análisis y consolidación de requisitos: Revisión, validación y consolidación de los requerimientos del sistema, actores, reglas de negocio y flujos funcionales identificados durante la etapa de propuesta.
 1.1 Consolidación de requerimientos: Consolidación de los requerimientos funcionales 
 1.2 Identificación de actores: Identificación definitiva de actores y funcionalidades según cada rol.
 1.3 Validación de reglas: Validación de reglas de negocio y restricciones.
 1.4 Aprobación de mockups: Consolidación y aprobación de los mockups que serán utilizados como referencia para el desarrollo.
 1.5 Definición de flujos: Definición de los flujos funcionales a partir de los requerimientos y mockups aprobados.

2. Diseño y modelado del sistema: Definición de la arquitectura y de los modelos necesarios para la implementación del sistema.
 2.1 Arquitectura: Definición de la arquitectura cliente-servidor.
 2.2 Modelo conceptual: Elaboración del modelo conceptual (ER).
 2.3 Modelo relacional: Elaboración del modelo relacional.
 2.4 Diccionario de datos: Elaboración del diccionario de datos. 
 2.5 Diseño de consultas: Diseño de consultas y vistas requeridas por el sistema.
 2.6 Diseño de automatización: Diseño de procedimientos, funciones, triggers y                                transacciones según las reglas de negocio. 
 2.7 Modelo físico: Elaboración del modelo físico de la base de datos.
 2.8 Contrato de API: Definición del contrato de API: endpoints, métodos, requests y responses.

3. Implementación de la base de datos: Construcción de la base de datos relacional y de los mecanismos definidos durante la etapa de diseño.
 3.1 Estructura de base de datos: Creación de tablas, relaciones, claves y restricciones.
 3.2 Consultas y vistas: Implementación de consultas y vistas.
 3.3 Procedimientos y triggers: Implementación de procedimientos, funciones, triggers y transacciones.

4. Desarrollo de la aplicación – Backend: Construcción de la aplicación servidor de acuerdo con los requerimientos, el modelo de datos y el contrato de API.
 4.1 Configuración y estructura del proyecto: Configuración inicial del proyecto, organización de paquetes y módulos, definición de variables y configuraciones principales.
 4.2 Persistencia: Implementación de entidades, repositorios y mapeos necesarios para interactuar con la base de datos.
 4.3 Lógica de negocio: Implementación de servicios, DTOs, mappers y operaciones correspondientes a los requerimientos funcionales.
 4.4 Controladores: Implementación de controladores y endpoints definidos en el contrato de API.
 4.5 Validaciones y manejo de errores: Implementación de validaciones, reglas de negocio y manejo de excepciones.
 4.6 Pruebas del Backend: Verificación de los endpoints y operaciones del Backend mediante herramientas de prueba como Postman.

5. Desarrollo de la aplicación – Frontend: Construcción de la interfaz web a partir de los mockups y requerimientos funcionales consolidados.
 5.1 Configuración y estructura del proyecto: Configuración inicial del proyecto, organización de carpetas, definición de variables principales y configuración de los recursos necesarios para el desarrollo.
 5.2 Construcción de la estructura visual: Implementación de las páginas, navegación y estructura visual general definida en los mockups.
 5.3 Implementación de componentes e interacción: Implementación de formularios, botones, tablas, tarjetas, modales y demás componentes definidos en los mockups.
 5.4 Implementación de navegación y flujos: Implementación de la navegación y los flujos definidos para las diferentes funcionalidades, utilizando datos de prueba cuando sea necesario para validar las interfaces antes de su integración con el Backend.

6. Integración Frontend – Backend: Integración de las interfaces desarrolladas con los servicios proporcionados por el Backend.
 6.1 Configuración de comunicación: Configuración e implementación de la comunicación entre Frontend y Backend.
 6.2 Integración de endpoints: Integración de los endpoints con las funcionalidades correspondientes.
 6.3 Manejo de estados: Manejo de estados de carga, éxito y error provenientes de las solicitudes.
 6.4 Actualización de interfaces: Actualización de las interfaces a partir de las respuestas obtenidas.
 6.5 Verificación de flujos: Verificación del funcionamiento de los flujos integrados.

7. Pruebas: Verificación del funcionamiento del sistema y de sus diferentes componentes.
 7.1 Pruebas funcionales: Validación de las funcionalidades principales de acuerdo con los requerimientos definidos.
 7.2 Pruebas de base de datos: Verificación de consultas, vistas, integridad de datos, procedimientos, funciones, triggers y transacciones.
 7.3 Pruebas de integración: Verificación del funcionamiento conjunto entre Frontend, Backend y base de datos.
 7.4 Corrección de errores: Identificación, corrección y validación de los errores encontrados durante las pruebas.
