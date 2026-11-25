package org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dao.impl;

import org.jhanp_dev.psraerobicosbackend.config.database.ConexionDB;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dao.interfaces.IReporteDAO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.AlumnoDestacadoResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.AsistenciaPorFechaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.AuditoriaMetodoPagoResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.EstadoCuentaParticipanteResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.MetodoPagoAltoRecaudoResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.MetodoPagoMasUtilizadoResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.PagoAnticipadoResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.RecaudoDiarioResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.SesionDestacadaPorDiaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.TurnoSinCancelacionResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.mapper.ReporteMapper;
import org.jhanp_dev.psraerobicosbackend.modules.person.dto.response.SaldoPersonaResponseDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ReporteDAO implements IReporteDAO {

    private final ReporteMapper reporteMapper;

    public ReporteDAO(ReporteMapper reporteMapper) {
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
        List<MetodoPagoAltoRecaudoResponseDTO> metodosPagoAltoRecaudo = new ArrayList<>();

        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                metodosPagoAltoRecaudo.add(reporteMapper.aMetodoPagoAltoRecaudoDTO(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al consultar métodos de pago de alto recaudo: " + e.getMessage(), e);
        }
        return metodosPagoAltoRecaudo;
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
        List<AlumnoDestacadoResponseDTO> alumnosDestacados = new ArrayList<>();

        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                alumnosDestacados.add(reporteMapper.aAlumnoDestacadoDTO(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al consultar alumnos destacados: " + e.getMessage(), e);
        }
        return alumnosDestacados;
    }

    @Override
    public List<SesionDestacadaPorDiaResponseDTO> obtenerSesionesDestacadasPorDia(Pageable pageable) {
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
                ORDER BY h.dia_semana ASC, total_asistencias_sesion DESC, s.id DESC
                LIMIT ? OFFSET ?
                """;
        List<SesionDestacadaPorDiaResponseDTO> sesionesDestacadas = new ArrayList<>();

        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, pageable.getPageSize());
            stmt.setLong(2, pageable.getOffset());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    sesionesDestacadas.add(reporteMapper.aSesionDestacadaPorDiaDTO(rs));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al consultar sesiones destacadas por día: " + e.getMessage(), e);
        }
        return sesionesDestacadas;
    }

    @Override
    public long contarSesionesDestacadasPorDia() {
        String sql = """
                SELECT s.id
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
                """;
        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             PreparedStatement stmt = conexion.prepareStatement("SELECT COUNT(*) FROM (" + sql + ") AS sesiones_destacadas");
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0L;
        } catch (SQLException e) {
            throw new IllegalStateException("Error al contar sesiones destacadas por día: " + e.getMessage(), e);
        }
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
        List<TurnoSinCancelacionResponseDTO> turnosSinCancelaciones = new ArrayList<>();

        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                turnosSinCancelaciones.add(reporteMapper.aTurnoSinCancelacionDTO(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al consultar turnos sin cancelaciones: " + e.getMessage(), e);
        }
        return turnosSinCancelaciones;
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
        List<AuditoriaMetodoPagoResponseDTO> auditoriaMetodosPago = new ArrayList<>();

        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, minOperaciones);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    auditoriaMetodosPago.add(reporteMapper.aAuditoriaMetodoPagoDTO(rs));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al consultar auditoría de métodos de pago: " + e.getMessage(), e);
        }
        return auditoriaMetodosPago;
    }

    @Override
    public List<PagoAnticipadoResponseDTO> obtenerPagosAnticipados(Pageable pageable) {
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
                  AND mc.monto_abonado > (SELECT monto FROM tarifa WHERE id = 1)
                ORDER BY mc.fecha_hora DESC, mc.id DESC
                LIMIT ? OFFSET ?
                """;
        List<PagoAnticipadoResponseDTO> pagosAnticipados = new ArrayList<>();

        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, pageable.getPageSize());
            stmt.setLong(2, pageable.getOffset());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pagosAnticipados.add(reporteMapper.aPagoAnticipadoDTO(rs));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al consultar pagos anticipados: " + e.getMessage(), e);
        }
        return pagosAnticipados;
    }

    @Override
    public long contarPagosAnticipados() {
        String sql = """
                SELECT COUNT(*)
                FROM movimiento_cuenta mc
                WHERE mc.tipo_movimiento = 'ABONO'
                  AND mc.monto_abonado > (SELECT monto FROM tarifa WHERE id = 1)
                """;
        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0L;
        } catch (SQLException e) {
            throw new IllegalStateException("Error al contar pagos anticipados: " + e.getMessage(), e);
        }
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
        List<MetodoPagoMasUtilizadoResponseDTO> metodosPagoMasUtilizados = new ArrayList<>();

        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                metodosPagoMasUtilizados.add(reporteMapper.aMetodoPagoMasUtilizadoDTO(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al consultar métodos de pago más utilizados: " + e.getMessage(), e);
        }
        return metodosPagoMasUtilizados;
    }

    @Override
    public List<EstadoCuentaParticipanteResponseDTO> obtenerEstadoCuentaPorDocumento(String numeroDocumento, Pageable pageable) {
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
                ORDER BY mc.fecha_hora DESC, mc.id DESC
                LIMIT ? OFFSET ?
                """;
        List<EstadoCuentaParticipanteResponseDTO> movimientosCuenta = new ArrayList<>();

        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, numeroDocumento);
            stmt.setInt(2, pageable.getPageSize());
            stmt.setLong(3, pageable.getOffset());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    movimientosCuenta.add(reporteMapper.aEstadoCuentaParticipanteDTO(rs));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al consultar estado de cuenta por documento: " + e.getMessage(), e);
        }
        return movimientosCuenta;
    }

    @Override
    public long contarEstadoCuentaPorDocumento(String numeroDocumento) {
        String sql = """
                SELECT COUNT(*)
                FROM movimiento_cuenta mc
                INNER JOIN persona p ON p.id = mc.persona_id
                INNER JOIN usuario u ON u.id = mc.usuario_id
                WHERE p.numero_documento = ?
                """;
        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, numeroDocumento);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0L;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al contar movimientos del estado de cuenta: " + e.getMessage(), e);
        }
    }

    @Override
    public List<RecaudoDiarioResponseDTO> obtenerRecaudoDiario(Pageable pageable) {
        String sql = """
                SELECT 
                    DATE_FORMAT(DATE(mc.fecha_hora), '%d/%m/%Y') AS dia,
                    COUNT(mc.id) AS total_pagos,
                    SUM(mc.monto_abonado) AS recaudo_dia
                FROM movimiento_cuenta mc
                WHERE mc.tipo_movimiento = 'ABONO'
                GROUP BY DATE_FORMAT(DATE(mc.fecha_hora), '%d/%m/%Y')
                ORDER BY MAX(mc.fecha_hora) DESC
                LIMIT ? OFFSET ?
                """;
        List<RecaudoDiarioResponseDTO> recaudoDiario = new ArrayList<>();

        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, pageable.getPageSize());
            stmt.setLong(2, pageable.getOffset());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    recaudoDiario.add(reporteMapper.aRecaudoDiarioDTO(rs));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al consultar recaudo diario: " + e.getMessage(), e);
        }
        return recaudoDiario;
    }

    @Override
    public long contarRecaudoDiario() {
        String sql = "SELECT COUNT(DISTINCT DATE(fecha_hora)) FROM movimiento_cuenta WHERE tipo_movimiento = 'ABONO'";
        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0L;
        } catch (SQLException e) {
            throw new IllegalStateException("Error al contar días de recaudo: " + e.getMessage(), e);
        }
    }

    @Override
    public List<SaldoPersonaResponseDTO> obtenerSaldosPersonas(Pageable pageable) {
        String sql = "SELECT persona_id, numero_documento, nombre_completo, total_abonado, total_cobrado, saldo, estado_cuenta FROM vista_saldo_persona ORDER BY nombre_completo, persona_id LIMIT ? OFFSET ?";
        List<SaldoPersonaResponseDTO> saldosPersonas = new ArrayList<>();
        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, pageable.getPageSize());
            stmt.setLong(2, pageable.getOffset());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    saldosPersonas.add(SaldoPersonaResponseDTO.builder()
                            .personaId(rs.getInt("persona_id"))
                            .numeroDocumento(rs.getString("numero_documento"))
                            .nombreCompleto(rs.getString("nombre_completo"))
                            .totalAbonado(rs.getBigDecimal("total_abonado"))
                            .totalCobrado(rs.getBigDecimal("total_cobrado"))
                            .saldo(rs.getBigDecimal("saldo"))
                            .estadoCuenta(rs.getString("estado_cuenta"))
                            .build());
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al consultar saldos de personas: " + e.getMessage(), e);
        }
        return saldosPersonas;
    }

    @Override
    public long contarSaldosPersonas() {
        String sql = "SELECT COUNT(*) FROM vista_saldo_persona";
        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0L;
        } catch (SQLException e) {
            throw new IllegalStateException("Error al contar saldos de personas: " + e.getMessage(), e);
        }
    }

    @Override
    public List<AsistenciaPorFechaResponseDTO> obtenerAsistenciasPorFecha(LocalDate fecha) {
        List<AsistenciaPorFechaResponseDTO> asistenciasPorFecha = new ArrayList<>();
        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             CallableStatement stmt = conexion.prepareCall("{CALL sp_reporte_asistencias_por_fecha(?)}")) {
            stmt.setDate(1, Date.valueOf(fecha));
            boolean tieneResultados = stmt.execute();
            while (tieneResultados) {
                try (ResultSet rs = stmt.getResultSet()) {
                    while (rs.next()) asistenciasPorFecha.add(reporteMapper.aAsistenciaPorFechaDTO(rs));
                }
                tieneResultados = stmt.getMoreResults();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al consultar asistencias por fecha: " + e.getMessage(), e);
        }
        return asistenciasPorFecha;
    }
}
