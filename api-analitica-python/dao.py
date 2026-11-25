from conexion import ConexionDB


class AnaliticaDAO:

    def __init__(self):
        self.db = ConexionDB.obtener_instancia()

    # Métodos de pago donde el abono promedio supera el promedio general de abonos
    # Retorna: medio de pago, cantidad de abonos, dinero total y promedio por abono.
    def obtener_metodos_pago_alto_recaudo(self) -> list:
        sql = """
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
        """
        conn = self.db.obtener_conexion()
        with conn.cursor(dictionary=True, prepared=True) as cursor:
            cursor.execute(sql)
            return cursor.fetchall()

    # Alumnos que han asistido a más clases que el promedio de la academia
    # Retorna: id del alumno, documento, nombre, total de clases y dinero pagado en clases.
    def obtener_alumnos_destacados(self) -> list:
        sql = """
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
        """
        conn = self.db.obtener_conexion()
        with conn.cursor(dictionary=True, prepared=True) as cursor:
            cursor.execute(sql)
            return cursor.fetchall()

    # Clases específicas que tuvieron más alumnos que el promedio de su mismo día (ej. un martes con más asistentes que los martes normales)
    # Retorna: id de clase, fecha, día de la semana, horario, alumnos que fueron y plata recaudada.
    def obtener_sesiones_destacadas_por_dia(self) -> list:
        sql = """
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
        """
        conn = self.db.obtener_conexion()
        with conn.cursor(dictionary=True, prepared=True) as cursor:
            cursor.execute(sql)
            return cursor.fetchall()

    # Alumnos y dinero por turno y horario, descartando clases canceladas o reembolsos para no alterar los números
    # Retorna: id de horario, día, turno, total de clases hechas, total de alumnos y dinero recaudado.
    def obtener_turnos_sin_cancelaciones(self) -> list:
        sql = """
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
        """
        conn = self.db.obtener_conexion()
        with conn.cursor(dictionary=True, prepared=True) as cursor:
            cursor.execute(sql)
            return cursor.fetchall()

    # Métodos de pago filtrados por un mínimo de operaciones (?min_operaciones=N) y promedio mayor o igual a la tarifa base
    # Retorna: medio de pago, total de operaciones, dinero acumulado y promedio por pago.
    def obtener_auditoria_metodos_pago(self, min_operaciones: int) -> list:
        sql = """
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
        """
        conn = self.db.obtener_conexion()
        with conn.cursor(dictionary=True, prepared=True) as cursor:
            cursor.execute(sql, (min_operaciones,))
            return cursor.fetchall()
