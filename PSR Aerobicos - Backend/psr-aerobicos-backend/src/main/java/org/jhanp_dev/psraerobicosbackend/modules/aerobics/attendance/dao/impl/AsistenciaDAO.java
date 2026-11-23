package org.jhanp_dev.psraerobicosbackend.modules.aerobics.attendance.dao.impl;

import org.jhanp_dev.psraerobicosbackend.config.database.ConexionDB;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.attendance.dao.interfaces.IAsistenciaDAO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.attendance.dto.request.AsistenciaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.attendance.dto.response.AsistenciaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.attendance.entity.Asistencia;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.attendance.mapper.AsistenciaMapper;
import org.springframework.stereotype.Repository;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Optional;

@Repository
public class AsistenciaDAO implements IAsistenciaDAO {

    private final Connection conexion;
    private final AsistenciaMapper mapper;

    public AsistenciaDAO(AsistenciaMapper mapper) throws SQLException {
        this.conexion = ConexionDB.getConexion();
        this.mapper = mapper;
    }

    @Override
    public Optional<AsistenciaResponseDTO> obtenerPorLlegadaId(Integer llegadaId) {
        String sql = "SELECT id, llegada_id, monto_cobrado, marcado_por, hora_marcado FROM asistencia WHERE llegada_id = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, llegadaId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(mapper.aResponseDTO(mapearFila(rs)));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al consultar asistencia: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public AsistenciaResponseDTO registrar(Integer llegadaId, AsistenciaRequestDTO request) {
        String call = "{CALL sp_registrar_asistencia(?, ?, ?)}";
        try (CallableStatement stmt = conexion.prepareCall(call)) {
            stmt.setInt(1, llegadaId);
            stmt.setInt(2, request.getUsuarioId());
            stmt.registerOutParameter(3, Types.INTEGER);
            stmt.execute();
            return obtenerPorLlegadaId(llegadaId).orElseThrow(
                    () -> new IllegalStateException("El procedimiento no generó la asistencia esperada"));
        } catch (SQLException e) {
            throw new IllegalStateException("Error al registrar asistencia: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean anularPorLlegadaId(Integer llegadaId) {
        String sql = "DELETE FROM asistencia WHERE llegada_id = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, llegadaId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Error al anular asistencia: " + e.getMessage(), e);
        }
    }

    private Asistencia mapearFila(ResultSet rs) throws SQLException {
        Timestamp hora = rs.getTimestamp("hora_marcado");
        return Asistencia.builder()
                .id(rs.getInt("id"))
                .llegadaId(rs.getInt("llegada_id"))
                .montoCobrado(rs.getBigDecimal("monto_cobrado"))
                .marcadoPor(rs.getInt("marcado_por"))
                .horaMarcado(hora == null ? null : hora.toLocalDateTime())
                .build();
    }
}
