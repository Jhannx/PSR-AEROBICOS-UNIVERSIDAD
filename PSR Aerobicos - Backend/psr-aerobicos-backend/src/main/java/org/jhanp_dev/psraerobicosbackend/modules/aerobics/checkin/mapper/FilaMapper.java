package org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.mapper;

import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.dto.response.FilaEsperaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.dto.response.FilaRecepcionResponseDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@Component
public class FilaMapper {
    public FilaRecepcionResponseDTO aRecepcion(ResultSet rs) throws SQLException {
        Timestamp llegada = rs.getTimestamp("hora_llegada");
        Timestamp marcado = rs.getTimestamp("hora_marcado");
        Date fecha = rs.getDate("fecha_sesion");
        return FilaRecepcionResponseDTO.builder()
                .llegadaId(rs.getInt("llegada_id"))
                .sesionId(rs.getInt("sesion_id"))
                .personaId(rs.getInt("persona_id"))
                .alumno(rs.getString("alumno"))
                .numeroDocumento(rs.getString("numero_documento"))
                .horaLlegada(llegada == null ? null : llegada.toLocalDateTime())
                .metodoCheckin(rs.getString("metodo_checkin"))
                .posicionTurno(rs.getInt("posicion_turno"))
                .asistenciaId(nullableInt(rs, "asistencia_id"))
                .montoCobrado(rs.getBigDecimal("monto_cobrado"))
                .horaMarcado(marcado == null ? null : marcado.toLocalDateTime())
                .atendido(rs.getBoolean("atendido"))
                .saldo(rs.getBigDecimal("saldo"))
                .estadoCuenta(rs.getString("estado_cuenta"))
                .fechaSesion(fecha == null ? null : fecha.toLocalDate())
                .sesionCancelada(rs.getBoolean("sesion_cancelada"))
                .build();
    }

    public FilaEsperaResponseDTO aEspera(ResultSet rs) throws SQLException {
        Timestamp llegada = rs.getTimestamp("hora_llegada");
        Date fecha = rs.getDate("fecha_sesion");
        return FilaEsperaResponseDTO.builder()
                .llegadaId(rs.getInt("llegada_id"))
                .sesionId(rs.getInt("sesion_id"))
                .fechaSesion(fecha == null ? null : fecha.toLocalDate())
                .alumnoVisible(rs.getString("alumno_visible"))
                .horaLlegada(llegada == null ? null : llegada.toLocalDateTime())
                .turno(rs.getInt("turno"))
                .estadoTurno(rs.getString("estado_turno"))
                .build();
    }

    private Integer nullableInt(ResultSet rs, String column) throws SQLException {
        int value = rs.getInt(column);
        return rs.wasNull() ? null : value;
    }
}
