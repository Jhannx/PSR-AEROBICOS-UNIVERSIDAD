# API REST de Analítica — PSR Aeróbicos

Implementación de una API REST de analítica en Python conectada directamente a la base de datos relacional `psr_aerobicos` en MySQL 8.0 mediante SQL nativo, sin uso de ORMs.

* **Repositorio en GitHub:** https://github.com/Jhannx/PSR-AEROBICOS-UNIVERSIDAD
* **Directorio del módulo:** `PSR Aerobicos - Backend/api-analitica-python/`

---

## 1. Documento de Homologación del Esquema

En mi proyecto de **gestión de sesiones, asistencias y recaudos para el gimnasio PSR Aeróbicos**, la Entidad Catálogo corresponde a `tarifa` y al clasificador `metodo_pago`, la Entidad Ítem es `sesion`, la Entidad Transaccional la conforman `movimiento_cuenta` y `asistencia` (junto con `llegada`), la Entidad Actor es `persona` y la Entidad Contexto es `horario`.

| Rol en el Modelo Analítico | Entidad / Elemento en `psr_aerobicos` | Función dentro del Negocio |
| :--- | :--- | :--- |
| **A. Entidad Catálogo / Clasificadora** | `tarifa` / `metodo_pago` (`movimiento_cuenta`) | Define el precio base vigente por clase ($5.000) y agrupa las transacciones según el medio de pago (`EFECTIVO`, `NEQUI`, `DAVIPLATA`, `TRANSFERENCIA`). |
| **B. Entidad Ítem / Recurso** | `sesion` | Representa cada clase individual de aeróbicos programada en una fecha determinada, sobre la cual se mide el aforo y el recaudo obtenido. |
| **C. Entidad Transaccional** | `movimiento_cuenta` y `asistencia` (con `llegada`) | Registran las operaciones monetarias y operativas con montos y marcas de tiempo: cobros por ingreso a clase, abonos anticipados y reembolsos. |
| **D. Entidad Actor / Sujeto** | `persona` | Almacena la información de los alumnos o participantes que asisten a las clases y efectúan los pagos en la academia. |
| **E. Entidad Contexto / Segmentación** | `horario` | Segmenta la operación del gimnasio según el día de la semana (`dia_semana`) y la franja horaria o turno en que se dictan las sesiones. |

---

## 2. Explicación de los 5 Endpoints y sus Consultas SQL

### 1. `GET /api/pagos/alto-recaudo`
* **Método DAO:** `obtener_metodos_pago_alto_recaudo()`
* **Qué responde en el negocio:** Identifica los métodos de pago cuyo promedio por abono es superior al promedio general de todos los abonos de la academia. Permite distinguir los canales por donde ingresan pagos grandes (como transferencias de paquetes anticipados de varias clases) frente a los medios utilizados para pagos individuales del día a día.
* **Cómo funciona la consulta SQL (`GROUP BY` + Subconsulta escalar en `HAVING`):**
  Agrupa los registros de `movimiento_cuenta` (filtrando `tipo_movimiento = 'ABONO'`) por `metodo_pago`, calcula el conteo de operaciones, la suma acumulada y el promedio de cada método, y en la cláusula `HAVING` compara ese promedio contra una subconsulta escalar que obtiene el promedio global de todos los abonos:
  ```sql
  SELECT 
      mc.metodo_pago,
      COUNT(mc.id) AS total_operaciones,
      SUM(mc.monto_abonado) AS total_acumulado,
      ROUND(AVG(mc.monto_abonado), 2) AS promedio_por_transaccion
  FROM movimiento_cuenta mc
  WHERE mc.tipo_movimiento = 'ABONO'
  GROUP BY mc.metodo_pago
  HAVING AVG(mc.monto_abonado) > (
      SELECT AVG(monto_abonado) 
      FROM movimiento_cuenta 
      WHERE tipo_movimiento = 'ABONO'
  )
  ORDER BY promedio_por_transaccion DESC;
  ```

### 2. `GET /api/alumnos/destacados`
* **Método DAO:** `obtener_alumnos_destacados()`
* **Qué responde en el negocio:** Muestra quiénes son los alumnos más constantes del gimnasio, devolviendo únicamente a las personas cuya cantidad histórica de clases asistidas supera el promedio de asistencias por alumno de toda la academia, junto con el dinero total que han pagado en sesiones.
* **Cómo funciona la consulta SQL (`GROUP BY` + Subconsulta con tabla derivada en `FROM`):**
  Une `persona`, `llegada` y `asistencia` agrupando por alumno para contar sus asistencias y sumar el monto cobrado. En el `HAVING`, compara el total de asistencias del alumno contra el promedio calculado sobre una tabla derivada (`FROM (...) AS sub`) que primero cuenta las asistencias individuales de cada alumno registrado:
  ```sql
  SELECT 
      p.id AS alumno_id,
      p.numero_documento,
      CONCAT(p.nombre, ' ', p.apellidos) AS nombre_completo,
      COUNT(a.id) AS total_asistencias,
      SUM(a.monto_cobrado) AS monto_total_acumulado
  FROM persona p
  INNER JOIN llegada l ON l.persona_id = p.id
  INNER JOIN asistencia a ON a.llegada_id = l.id
  GROUP BY p.id, p.numero_documento, p.nombre, p.apellidos
  HAVING COUNT(a.id) > (
      SELECT AVG(sub.conteo_asistencias)
      FROM (
          SELECT COUNT(a2.id) AS conteo_asistencias
          FROM persona p2
          INNER JOIN llegada l2 ON l2.persona_id = p2.id
          INNER JOIN asistencia a2 ON a2.llegada_id = l2.id
          GROUP BY p2.id
      ) AS sub
  )
  ORDER BY total_asistencias DESC, monto_total_acumulado DESC;
  ```

### 3. `GET /api/sesiones/destacadas-por-dia`
* **Método DAO:** `obtener_sesiones_destacadas_por_dia()`
* **Qué responde en el negocio:** Evalúa cada clase dictada y filtra aquellas que tuvieron más alumnos que el promedio habitual de su propio día de la semana (por ejemplo, una sesión de martes que superó el promedio histórico de los martes). Permite detectar fechas y franjas con picos de asistencia respecto al comportamiento normal de ese mismo día.
* **Cómo funciona la consulta SQL (`GROUP BY` + Subconsulta correlacionada):**
  Agrupa por sesión (`sesion` y `horario`) para contar los asistentes de cada clase. En el `HAVING`, utiliza una subconsulta correlacionada (`WHERE h2.dia_semana = h.dia_semana`) que calcula el promedio de asistencias únicamente entre las sesiones que pertenecen al mismo día de la semana de la fila externa:
  ```sql
  SELECT 
      s.id AS sesion_id,
      s.fecha,
      h.dia_semana,
      CONCAT(h.hora_inicio, ' - ', h.hora_fin) AS franja_horaria,
      COUNT(a.id) AS total_asistencias_sesion,
      COALESCE(SUM(a.monto_cobrado), 0.00) AS recaudo_sesion
  FROM sesion s
  INNER JOIN horario h ON h.id = s.horario_id
  LEFT JOIN llegada l ON l.sesion_id = s.id
  LEFT JOIN asistencia a ON a.llegada_id = l.id
  GROUP BY s.id, s.fecha, h.dia_semana, h.hora_inicio, h.hora_fin
  HAVING COUNT(a.id) > (
      SELECT AVG(sub.asistencias_por_sesion)
      FROM (
          SELECT s2.id, s2.horario_id, COUNT(a2.id) AS asistencias_por_sesion
          FROM sesion s2
          INNER JOIN horario h2 ON h2.id = s2.horario_id
          LEFT JOIN llegada l2 ON l2.sesion_id = s2.id
          LEFT JOIN asistencia a2 ON a2.llegada_id = l2.id
          WHERE h2.dia_semana = h.dia_semana
          GROUP BY s2.id, s2.horario_id
      ) AS sub
  )
  ORDER BY h.dia_semana ASC, total_asistencias_sesion DESC;
  ```

### 4. `GET /api/turnos/sin-cancelaciones`
* **Método DAO:** `obtener_turnos_sin_cancelaciones()`
* **Qué responde en el negocio:** Entrega el total de sesiones efectivas, alumnos atendidos y dinero recaudado por cada turno u horario de la semana, descartando de antemano cualquier clase cancelada o sesión donde haya existido un reembolso para no distorsionar las cifras reales del gimnasio.
* **Cómo funciona la consulta SQL (`GROUP BY` + Subconsulta `NOT IN` en `WHERE`):**
  Une `horario`, `sesion`, `llegada` y `asistencia`, y antes de agrupar por turno aplica un filtro `WHERE s.id NOT IN (...)` cuya subconsulta reúne (mediante `UNION`) los IDs de sesiones con `cancelada = TRUE` y los IDs de sesiones asociadas a movimientos de tipo `'REEMBOLSO'`:
  ```sql
  SELECT 
      h.id AS horario_id,
      h.dia_semana,
      CONCAT(h.hora_inicio, ' - ', h.hora_fin) AS turno_franja,
      COUNT(DISTINCT s.id) AS total_sesiones_limpias,
      COUNT(a.id) AS total_asistencias,
      COALESCE(SUM(a.monto_cobrado), 0.00) AS recaudo_total_limpio
  FROM horario h
  INNER JOIN sesion s ON s.horario_id = h.id
  INNER JOIN llegada l ON l.sesion_id = s.id
  INNER JOIN asistencia a ON a.llegada_id = l.id
  WHERE s.id NOT IN (
      SELECT DISTINCT id FROM sesion WHERE cancelada = TRUE
      UNION
      SELECT DISTINCT l_sub.sesion_id
      FROM movimiento_cuenta mc_sub
      INNER JOIN llegada l_sub ON l_sub.id = mc_sub.llegada_id
      WHERE mc_sub.tipo_movimiento = 'REEMBOLSO'
  )
  GROUP BY h.id, h.dia_semana, h.hora_inicio, h.hora_fin
  ORDER BY h.dia_semana, h.hora_inicio;
  ```

### 5. `GET /api/pagos/auditoria-metodos?min_operaciones=N`
* **Método DAO:** `obtener_auditoria_metodos_pago(min_operaciones)`
* **Qué responde en el negocio:** Audita los métodos de pago exigiendo un volumen mínimo de transacciones enviado como parámetro por el usuario en la URL (`min_operaciones`, por defecto `2`) y verificando que su promedio de pago sea mayor o igual al valor mínimo establecido en la tabla `tarifa` ($5.000).
* **Cómo funciona la consulta SQL (`GROUP BY` + Parámetro seguro `?` + Subconsulta `MIN`):**
  Agrupa los abonos por `metodo_pago` y en el `HAVING` evalúa dos condiciones: que `COUNT(mc.id) >= ?` (inyectado de forma segura mediante tupla preparada) y que `AVG(mc.monto_abonado) >= (SELECT MIN(monto) FROM tarifa)`:
  ```sql
  SELECT 
      mc.metodo_pago,
      COUNT(mc.id) AS total_operaciones,
      SUM(mc.monto_abonado) AS monto_total,
      ROUND(AVG(mc.monto_abonado), 2) AS promedio_por_operacion
  FROM movimiento_cuenta mc
  WHERE mc.tipo_movimiento = 'ABONO'
  GROUP BY mc.metodo_pago
  HAVING COUNT(mc.id) >= ?
     AND AVG(mc.monto_abonado) >= (SELECT MIN(monto) FROM tarifa)
  ORDER BY total_operaciones DESC;
  ```

---

## 3. Estructura del Código Fuente

La aplicación está organizada en cuatro módulos dentro de `PSR Aerobicos - Backend/api-analitica-python/`:

```text
api-analitica-python/
├── conexion.py        # Conexión única a MySQL bajo patrón Singleton
├── dao.py             # Consultas SQL nativas mediante cursor preparado
├── controller.py      # Rutas GET, códigos HTTP (200, 404, 500) y manejo de errores
├── app.py             # Inicialización de FastAPI y ejecución con Uvicorn
└── requirements.txt   # Dependencias del proyecto
```

* **`conexion.py`:** Implementa `ConexionDB` con el patrón Singleton (`obtener_instancia()`) usando `mysql-connector-python` para mantener y reutilizar una única conexión hacia `psr_aerobicos`.
* **`dao.py`:** Implementa `AnaliticaDAO` ejecutando las 5 consultas SQL puras con `conn.cursor(dictionary=True, prepared=True)`.
* **`controller.py`:** Expone los 5 endpoints `GET` bajo el prefijo `/api`, capturando excepciones con `try-except` y asignando explícitamente los códigos HTTP `200`, `404` y `500`.
* **`app.py`:** Instancia `FastAPI`, registra el `router` y levanta el servidor Uvicorn en el puerto `8000`.

---

## 4. Cumplimiento de Restricciones Técnicas

1. **Prohibición de ORM:** No se utiliza SQLAlchemy ni Django ORM. Todas las consultas se ejecutan en SQL nativo mediante `cursor.execute(...)`.
2. **Base de Datos Propia:** Conectado directamente al esquema relacional `psr_aerobicos` en MySQL 8.0 (`persona`, `sesion`, `horario`, `llegada`, `asistencia`, `movimiento_cuenta`, `tarifa`).
3. **Parametrización Segura:** En `/api/pagos/auditoria-metodos`, el parámetro `min_operaciones` se pasa mediante marcador de posición `?` y tupla `(min_operaciones,)` en `cursor.execute(sql, (min_operaciones,))`, evitando inyección SQL.
4. **Respuestas Homogéneas:** Todos los endpoints responden a peticiones `GET` con formato JSON uniforme (`codigo`, `mensaje`, `total_registros`, `datos`) y códigos de estado HTTP `200 OK`, `404 Not Found` y `500 Internal Server Error`.

---

## 5. Ejecución Local

1. Instalar dependencias desde `PSR Aerobicos - Backend/api-analitica-python`:
   ```bash
   pip install -r requirements.txt
   ```
2. Iniciar el servidor:
   ```bash
   python app.py
   ```
3. Documentación interactiva y pruebas:
   * **Swagger UI:** `http://localhost:8000/docs`
   * **OpenAPI JSON:** `http://localhost:8000/openapi.json`
   * `GET http://localhost:8000/api/pagos/alto-recaudo`
   * `GET http://localhost:8000/api/alumnos/destacados`
   * `GET http://localhost:8000/api/sesiones/destacadas-por-dia`
   * `GET http://localhost:8000/api/turnos/sin-cancelaciones`
   * `GET http://localhost:8000/api/pagos/auditoria-metodos?min_operaciones=2`
