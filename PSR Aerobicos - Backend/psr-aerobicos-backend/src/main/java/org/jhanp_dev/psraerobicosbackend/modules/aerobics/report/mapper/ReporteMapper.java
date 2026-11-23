package org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.mapper;

import org.jhanp_dev.psraerobicosbackend.modules.aerobics.payment.enums.MetodoPago;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.payment.enums.TipoMovimiento;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.AlumnoDestacadoResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.AuditoriaMetodoPagoResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.EstadoCuentaParticipanteResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.MetodoPagoAltoRecaudoResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.MetodoPagoMasUtilizadoResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.PagoAnticipadoResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.RecaudoDiarioResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.SesionDestacadaPorDiaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.TurnoSinCancelacionResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.AsistenciaPorFechaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.enums.MetodoCheckin;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Time;

@Component
public class ReporteMapper {

    public AsistenciaPorFechaResponseDTO aAsistenciaPorFechaDTO(ResultSet rs) throws SQLException {
        Date fecha = rs.getDate("fecha_sesion");
        Time hora = rs.getTime("hora_clase");
        Timestamp llegada = rs.getTimestamp("hora_llegada");
        return AsistenciaPorFechaResponseDTO.builder()
                .asistenciaId(rs.getInt("asistencia_id"))
                .fechaSesion(fecha == null ? null : fecha.toLocalDate())
                .horaClase(hora == null ? null : hora.toLocalTime())
                .alumno(rs.getString("alumno"))
                .numeroDocumento(rs.getString("numero_documento"))
                .telefono(rs.getString("telefono"))
                .horaLlegada(llegada == null ? null : llegada.toLocalDateTime())
                .metodoCheckin(MetodoCheckin.valueOf(rs.getString("metodo_checkin")))
                .montoCobrado(rs.getBigDecimal("monto_cobrado"))
                .confirmadoPor(rs.getString("confirmado_por"))
                .build();
    }

    public MetodoPagoAltoRecaudoResponseDTO aMetodoPagoAltoRecaudoDTO(ResultSet rs) throws SQLException {
        return MetodoPagoAltoRecaudoResponseDTO.builder()
                .metodoPago(MetodoPago.valueOf(rs.getString("metodo_pago")))
                .totalOperaciones(rs.getLong("total_operaciones"))
                .totalAcumulado(rs.getBigDecimal("total_acumulado"))
                .promedioPorTransaccion(rs.getBigDecimal("promedio_por_transaccion"))
                .build();
    }

    public AlumnoDestacadoResponseDTO aAlumnoDestacadoDTO(ResultSet rs) throws SQLException {
        return AlumnoDestacadoResponseDTO.builder()
                .alumnoId(rs.getInt("alumno_id"))
                .numeroDocumento(rs.getString("numero_documento"))
                .nombreCompleto(rs.getString("nombre_completo"))
                .totalAsistencias(rs.getLong("total_asistencias"))
                .montoTotalAcumulado(rs.getBigDecimal("monto_total_acumulado"))
                .build();
    }

    public SesionDestacadaPorDiaResponseDTO aSesionDestacadaPorDiaDTO(ResultSet rs) throws SQLException {
        Date fecha = rs.getDate("fecha");
        return SesionDestacadaPorDiaResponseDTO.builder()
                .sesionId(rs.getInt("sesion_id"))
                .fecha(fecha != null ? fecha.toLocalDate() : null)
                .diaSemana(rs.getInt("dia_semana"))
                .franjaHoraria(rs.getString("franja_horaria"))
                .totalAsistenciasSesion(rs.getLong("total_asistencias_sesion"))
                .recaudoSesion(rs.getBigDecimal("recaudo_sesion"))
                .build();
    }

    public TurnoSinCancelacionResponseDTO aTurnoSinCancelacionDTO(ResultSet rs) throws SQLException {
        return TurnoSinCancelacionResponseDTO.builder()
                .horarioId(rs.getInt("horario_id"))
                .diaSemana(rs.getInt("dia_semana"))
                .turnoFranja(rs.getString("turno_franja"))
                .totalSesionesLimpias(rs.getLong("total_sesiones_limpias"))
                .totalAsistencias(rs.getLong("total_asistencias"))
                .recaudoTotalLimpio(rs.getBigDecimal("recaudo_total_limpio"))
                .build();
    }

    public AuditoriaMetodoPagoResponseDTO aAuditoriaMetodoPagoDTO(ResultSet rs) throws SQLException {
        return AuditoriaMetodoPagoResponseDTO.builder()
                .metodoPago(MetodoPago.valueOf(rs.getString("metodo_pago")))
                .totalOperaciones(rs.getLong("total_operaciones"))
                .montoTotal(rs.getBigDecimal("monto_total"))
                .promedioPorOperacion(rs.getBigDecimal("promedio_por_operacion"))
                .build();
    }

    public PagoAnticipadoResponseDTO aPagoAnticipadoDTO(ResultSet rs) throws SQLException {
        Timestamp fechaHora = rs.getTimestamp("fecha_hora");
        return PagoAnticipadoResponseDTO.builder()
                .movimientoId(rs.getInt("movimiento_id"))
                .numeroDocumento(rs.getString("numero_documento"))
                .alumno(rs.getString("alumno"))
                .montoAbonado(rs.getBigDecimal("monto_abonado"))
                .metodoPago(MetodoPago.valueOf(rs.getString("metodo_pago")))
                .fechaHora(fechaHora != null ? fechaHora.toLocalDateTime() : null)
                .build();
    }

    public MetodoPagoMasUtilizadoResponseDTO aMetodoPagoMasUtilizadoDTO(ResultSet rs) throws SQLException {
        return MetodoPagoMasUtilizadoResponseDTO.builder()
                .metodoPago(MetodoPago.valueOf(rs.getString("metodo_pago")))
                .totalPagos(rs.getLong("total_pagos"))
                .totalRecaudado(rs.getBigDecimal("total_recaudado"))
                .pagoMayor(rs.getBigDecimal("pago_mayor"))
                .build();
    }

    public EstadoCuentaParticipanteResponseDTO aEstadoCuentaParticipanteDTO(ResultSet rs) throws SQLException {
        Timestamp fechaHora = rs.getTimestamp("fecha_hora");
        return EstadoCuentaParticipanteResponseDTO.builder()
                .fechaHora(fechaHora != null ? fechaHora.toLocalDateTime() : null)
                .numeroDocumento(rs.getString("numero_documento"))
                .alumno(rs.getString("alumno"))
                .tipoMovimiento(TipoMovimiento.valueOf(rs.getString("tipo_movimiento")))
                .montoRecibido(rs.getBigDecimal("monto_recibido"))
                .montoAbonado(rs.getBigDecimal("monto_abonado"))
                .metodoPago(MetodoPago.valueOf(rs.getString("metodo_pago")))
                .registradoPor(rs.getString("registrado_por"))
                .build();
    }

    public RecaudoDiarioResponseDTO aRecaudoDiarioDTO(ResultSet rs) throws SQLException {
        return RecaudoDiarioResponseDTO.builder()
                .dia(rs.getString("dia"))
                .totalPagos(rs.getLong("total_pagos"))
                .recaudoDia(rs.getBigDecimal("recaudo_dia"))
                .build();
    }
}

