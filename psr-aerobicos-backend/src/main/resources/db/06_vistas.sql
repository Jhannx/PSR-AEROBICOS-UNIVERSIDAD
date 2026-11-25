USE psr_aerobicos;

/*
1. vista_saldo_persona
Calcula el total abonado, total cobrado, saldo neto y estado de cuenta de cada persona activa.
*/
CREATE OR REPLACE VIEW vista_saldo_persona AS
SELECT
    p.id AS persona_id,
    p.tipo_documento,
    p.numero_documento,
    CONCAT(p.nombre, ' ', p.apellidos) AS nombre_completo,
    p.telefono,
    COALESCE(m.total_abonado, 0.00) AS total_abonado,
    COALESCE(a.total_cobrado, 0.00) AS total_cobrado,
    (COALESCE(m.total_abonado, 0.00) - COALESCE(a.total_cobrado, 0.00)) AS saldo,
    CASE 
        WHEN (COALESCE(m.total_abonado, 0.00) - COALESCE(a.total_cobrado, 0.00)) > 0 THEN 'CON_SALDO_A_FAVOR'
        WHEN (COALESCE(m.total_abonado, 0.00) - COALESCE(a.total_cobrado, 0.00)) < 0 THEN 'DEBE'
        ELSE 'AL_DIA'
    END AS estado_cuenta
FROM persona p
LEFT JOIN (
    SELECT persona_id, SUM(monto_abonado) AS total_abonado
    FROM movimiento_cuenta
    GROUP BY persona_id
) m ON m.persona_id = p.id
LEFT JOIN (
    SELECT l.persona_id, SUM(a.monto_cobrado) AS total_cobrado
    FROM asistencia a
    JOIN llegada l ON l.id = a.llegada_id
    GROUP BY l.persona_id
) a ON a.persona_id = p.id
WHERE p.activo = TRUE;

/*
2. vista_fila_hoy
Muestra el orden de llegada, cobro de asistencia y estado de cuenta para recepcion.
*/
CREATE OR REPLACE VIEW vista_fila_hoy AS
SELECT
    l.id AS llegada_id,
    l.sesion_id,
    l.persona_id,
    CONCAT(p.nombre, ' ', p.apellidos) AS alumno,
    p.numero_documento,
    l.hora_llegada,
    l.metodo_checkin,
    ROW_NUMBER() OVER (PARTITION BY l.sesion_id ORDER BY l.hora_llegada) AS posicion_turno,
    a.id AS asistencia_id,
    a.monto_cobrado,
    a.hora_marcado,
    CASE WHEN a.id IS NOT NULL THEN TRUE ELSE FALSE END AS atendido,
    sp.saldo,
    sp.estado_cuenta,
    s.fecha AS fecha_sesion,
    s.cancelada AS sesion_cancelada
FROM llegada l
JOIN sesion s ON s.id = l.sesion_id
JOIN persona p ON p.id = l.persona_id
LEFT JOIN asistencia a ON a.llegada_id = l.id
LEFT JOIN vista_saldo_persona sp ON sp.persona_id = l.persona_id;

/*
3. vista_fila_espera
Muestra la fila de turnos para la pantalla de espera sin incluir montos ni saldos.
*/
CREATE OR REPLACE VIEW vista_fila_espera AS
SELECT
    l.id AS llegada_id,
    l.sesion_id,
    s.fecha AS fecha_sesion,
    CONCAT(p.nombre, ' ', LEFT(p.apellidos, 1), '.') AS alumno_visible,
    l.hora_llegada,
    ROW_NUMBER() OVER (PARTITION BY l.sesion_id ORDER BY l.hora_llegada) AS turno,
    CASE WHEN a.id IS NOT NULL THEN 'ATENDIDO' ELSE 'EN_ESPERA' END AS estado_turno
FROM llegada l
JOIN sesion s ON s.id = l.sesion_id
JOIN persona p ON p.id = l.persona_id
LEFT JOIN asistencia a ON a.llegada_id = l.id;

/*
4. vista_recaudo_diario
Agrupa la cantidad de sesiones, asistencias y recaudo total por fecha.
*/
CREATE OR REPLACE VIEW vista_recaudo_diario AS
SELECT
    s.fecha,
    COUNT(DISTINCT s.id) AS total_sesiones_programadas,
    COUNT(a.id) AS total_asistencias_efectivas,
    COALESCE(SUM(a.monto_cobrado), 0.00) AS total_recaudo_asistencias
FROM sesion s
LEFT JOIN llegada l ON l.sesion_id = s.id
LEFT JOIN asistencia a ON a.llegada_id = l.id
WHERE s.cancelada = FALSE
GROUP BY s.fecha;

/*
5. vista_historial_cambios
Consulta los eventos registrados en historial_auditoria junto con el correo del usuario responsable.
*/
CREATE OR REPLACE VIEW vista_historial_cambios AS
SELECT
    h.id AS auditoria_id,
    h.tabla_afectada,
    h.registro_id,
    h.campo,
    h.valor_anterior,
    h.valor_nuevo,
    h.accion,
    u.correo AS usuario_responsable,
    h.fecha_hora
FROM historial_auditoria h
LEFT JOIN usuario u ON u.id = h.usuario_id;

/*
6. vista_resumen_sesiones
Muestra las sesiones con los datos del profesor asignado, total de asistencias y recaudo.
*/
CREATE OR REPLACE VIEW vista_resumen_sesiones AS
SELECT
    s.id AS sesion_id,
    s.fecha,
    TIME(s.hora_inicio) AS hora_inicio,
    TIME(s.hora_fin) AS hora_fin,
    s.cancelada,
    CONCAT(prof_p.nombre, ' ', prof_p.apellidos) AS instructor,
    s.total_asistencias,
    s.total_recaudo
FROM sesion s
LEFT JOIN usuario prof_u ON prof_u.id = s.profesor_usuario_id
LEFT JOIN persona prof_p ON prof_p.id = prof_u.persona_id;

