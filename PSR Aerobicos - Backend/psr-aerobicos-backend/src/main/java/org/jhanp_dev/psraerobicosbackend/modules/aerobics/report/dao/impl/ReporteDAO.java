package org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dao.impl;

import org.jhanp_dev.psraerobicosbackend.config.database.ConexionDB;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dao.interfaces.IReporteDAO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.AlumnoDestacadoResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.AuditoriaMetodoPagoResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.EstadoCuentaParticipanteResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.MetodoPagoAltoRecaudoResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.MetodoPagoMasUtilizadoResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.PagoAnticipadoResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.RecaudoDiarioResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.SesionDestacadaPorDiaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.TurnoSinCancelacionResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.mapper.ReporteMapper;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ReporteDAO implements IReporteDAO {

    private final Connection conexion;
    private final ReporteMapper reporteMapper;

    public ReporteDAO(ReporteMapper reporteMapper) throws SQLException {
        this.conexion = ConexionDB.getConexion();
        this.reporteMapper = reporteMapper;
    }

    @Override
    public List<MetodoPagoAltoRecaudoResponseDTO> obtenerMetodosPagoAltoRecaudo() {
        String sql = """
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
                ORDER BY promedio_por_transaccion DESC
                """;
        List<MetodoPagoAltoRecaudoResponseDTO> lista = new ArrayList<>();

        try (PreparedStatement stmt = conexion.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(reporteMapper.aMetodoPagoAltoRecaudoDTO(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al consultar metodos de pago de alto recaudo: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public List<AlumnoDestacadoResponseDTO> obtenerAlumnosDestacados() {
        String sql = """
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
                ORDER BY total_asistencias DESC, monto_total_acumulado DESC
                """;
        List<AlumnoDestacadoResponseDTO> lista = new ArrayList<>();

        try (PreparedStatement stmt = conexion.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(reporteMapper.aAlumnoDestacadoDTO(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al consultar alumnos destacados: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public List<SesionDestacadaPorDiaResponseDTO> obtenerSesionesDestacadasPorDia() {
        String sql = """
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
                ORDER BY h.dia_semana ASC, total_asistencias_sesion DESC
                """;
        List<SesionDestacadaPorDiaResponseDTO> lista = new ArrayList<>();

        try (PreparedStatement stmt = conexion.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(reporteMapper.aSesionDestacadaPorDiaDTO(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al consultar sesiones destacadas por dia: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public List<TurnoSinCancelacionResponseDTO> obtenerTurnosSinCancelaciones() {
        String sql = """
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
                ORDER BY h.dia_semana, h.hora_inicio
                """;
        List<TurnoSinCancelacionResponseDTO> lista = new ArrayList<>();

        try (PreparedStatement stmt = conexion.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(reporteMapper.aTurnoSinCancelacionDTO(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al consultar turnos sin cancelaciones: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public List<AuditoriaMetodoPagoResponseDTO> obtenerAuditoriaMetodosPago(int minOperaciones) {
        String sql = """
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
                ORDER BY total_operaciones DESC
                """;
        List<AuditoriaMetodoPagoResponseDTO> lista = new ArrayList<>();

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, minOperaciones);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(reporteMapper.aAuditoriaMetodoPagoDTO(rs));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al consultar auditoria de metodos de pago: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public List<PagoAnticipadoResponseDTO> obtenerPagosAnticipados() {
        String sql = """
                SELECT 
                    mc.id AS movimiento_id,
                    p.numero_documento,
                    CONCAT(p.nombre, ' ', p.apellidos) AS alumno,
                    mc.monto_abonado,
                    mc.metodo_pago,
                    mc.fecha_hora
                FROM movimiento_cuenta mc
                INNER JOIN persona p ON p.id = mc.persona_id
                WHERE mc.tipo_movimiento = 'ABONO'
                  AND mc.monto_abonado > (
                      SELECT monto 
                      FROM tarifa 
                      WHERE id = 1
                  )
                """;
        List<PagoAnticipadoResponseDTO> lista = new ArrayList<>();

        try (PreparedStatement stmt = conexion.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(reporteMapper.aPagoAnticipadoDTO(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al consultar pagos anticipados: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public List<MetodoPagoMasUtilizadoResponseDTO> obtenerMetodosPagoMasUtilizados() {
        String sql = """
                SELECT 
                    mc.metodo_pago,
                    COUNT(mc.id) AS total_pagos,
                    SUM(mc.monto_abonado) AS total_recaudado,
                    MAX(mc.monto_abonado) AS pago_mayor
                FROM movimiento_cuenta mc
                WHERE mc.tipo_movimiento = 'ABONO'
                GROUP BY mc.metodo_pago
                HAVING COUNT(mc.id) >= 2
                ORDER BY total_recaudado DESC
                """;
        List<MetodoPagoMasUtilizadoResponseDTO> lista = new ArrayList<>();

        try (PreparedStatement stmt = conexion.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(reporteMapper.aMetodoPagoMasUtilizadoDTO(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al consultar metodos de pago mas utilizados: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public List<EstadoCuentaParticipanteResponseDTO> obtenerEstadoCuentaPorDocumento(String numeroDocumento) {
        String sql = """
                SELECT 
                    mc.fecha_hora,
                    p.numero_documento,
                    CONCAT(p.nombre, ' ', p.apellidos) AS alumno,
                    mc.tipo_movimiento,
                    mc.monto_recibido,
                    mc.monto_abonado,
                    mc.metodo_pago,
                    u.correo AS registrado_por
                FROM movimiento_cuenta mc
                INNER JOIN persona p ON p.id = mc.persona_id
                INNER JOIN usuario u ON u.id = mc.usuario_id
                WHERE p.numero_documento = ?
                ORDER BY mc.fecha_hora DESC
                """;
        List<EstadoCuentaParticipanteResponseDTO> lista = new ArrayList<>();

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, numeroDocumento);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(reporteMapper.aEstadoCuentaParticipanteDTO(rs));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al consultar estado de cuenta por documento: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public List<RecaudoDiarioResponseDTO> obtenerRecaudoDiario() {
        String sql = """
                SELECT 
                    DATE_FORMAT(mc.fecha_hora, '%d/%m/%Y') AS dia,
                    COUNT(mc.id) AS total_pagos,
                    SUM(mc.monto_abonado) AS recaudo_dia
                FROM movimiento_cuenta mc
                WHERE mc.tipo_movimiento = 'ABONO'
                GROUP BY dia
                ORDER BY MIN(mc.fecha_hora) DESC
                """;
        List<RecaudoDiarioResponseDTO> lista = new ArrayList<>();

        try (PreparedStatement stmt = conexion.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(reporteMapper.aRecaudoDiarioDTO(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al consultar recaudo diario: " + e.getMessage(), e);
        }
        return lista;
    }
}
