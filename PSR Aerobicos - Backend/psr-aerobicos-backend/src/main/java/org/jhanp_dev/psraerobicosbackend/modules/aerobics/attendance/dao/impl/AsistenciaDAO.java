package org.jhanp_dev.psraerobicosbackend.modules.aerobics.attendance.dao.impl;

import lombok.RequiredArgsConstructor;
import org.jhanp_dev.psraerobicosbackend.config.database.ConexionDB;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.attendance.dao.interfaces.IAsistenciaDAO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.attendance.dto.request.AsistenciaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.attendance.dto.response.AsistenciaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.attendance.entity.Asistencia;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.attendance.mapper.AsistenciaMapper;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AsistenciaDAO implements IAsistenciaDAO {

    private final AsistenciaMapper asistenciaMapper;

    private static final String BASE_SELECT = """
            SELECT id, llegada_id, monto_cobrado, marcado_por, hora_marcado
            FROM asistencia
            """;

    @Override
    public List<AsistenciaResponseDTO> listarTodos() {
        String sql = BASE_SELECT + " ORDER BY hora_marcado DESC";
        List<AsistenciaResponseDTO> asistencias = new ArrayList<>();

        try {
            Connection conn = ConexionDB.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Asistencia entidad = mapearFila(rs);
                    asistencias.add(asistenciaMapper.aResponseDTO(entidad));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al listar asistencias: " + e.getMessage(), e);
        }
        return asistencias;
    }

    @Override
    public Optional<AsistenciaResponseDTO> obtenerPorId(Integer id) {
        String sql = BASE_SELECT + " WHERE id = ?";

        try {
            Connection conn = ConexionDB.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Asistencia entidad = mapearFila(rs);
                        return Optional.of(asistenciaMapper.aResponseDTO(entidad));
                    }
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al obtener asistencia por id: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<AsistenciaResponseDTO> obtenerPorLlegadaId(Integer llegadaId) {
        String sql = BASE_SELECT + " WHERE llegada_id = ?";

        try {
            Connection conn = ConexionDB.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, llegadaId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Asistencia entidad = mapearFila(rs);
                        return Optional.of(asistenciaMapper.aResponseDTO(entidad));
                    }
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al obtener asistencia por llegada_id: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public AsistenciaResponseDTO guardar(AsistenciaRequestDTO dto) {
        Asistencia asistencia = asistenciaMapper.aEntidad(dto);
        String sql = "INSERT INTO asistencia (llegada_id, monto_cobrado, marcado_por) VALUES (?, ?, ?)";

        try {
            Connection conn = ConexionDB.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, asistencia.getLlegadaId());
                stmt.setBigDecimal(2, asistencia.getMontoCobrado());
                stmt.setInt(3, asistencia.getMarcadoPor());
                stmt.executeUpdate();
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        asistencia.setId(keys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al guardar asistencia: " + e.getMessage(), e);
        }
        return asistenciaMapper.aResponseDTO(asistencia);
    }

    @Override
    public boolean actualizar(Integer id, AsistenciaRequestDTO dto) {
        Asistencia asistencia = asistenciaMapper.aEntidad(dto);
        asistencia.setId(id);
        String sql = "UPDATE asistencia SET llegada_id = ?, monto_cobrado = ?, marcado_por = ? WHERE id = ?";

        try {
            Connection conn = ConexionDB.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, asistencia.getLlegadaId());
                stmt.setBigDecimal(2, asistencia.getMontoCobrado());
                stmt.setInt(3, asistencia.getMarcadoPor());
                stmt.setInt(4, asistencia.getId());
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al actualizar asistencia: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean eliminar(Integer id) {
        String sql = "DELETE FROM asistencia WHERE id = ?";

        try {
            Connection conn = ConexionDB.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al eliminar asistencia: " + e.getMessage(), e);
        }
    }

    private Asistencia mapearFila(ResultSet rs) throws SQLException {
        Timestamp horaMarcado = rs.getTimestamp("hora_marcado");
        return Asistencia.builder()
                .id(rs.getInt("id"))
                .llegadaId(rs.getInt("llegada_id"))
                .montoCobrado(rs.getBigDecimal("monto_cobrado"))
                .marcadoPor(rs.getInt("marcado_por"))
                .horaMarcado(horaMarcado != null ? horaMarcado.toLocalDateTime() : null)
                .build();
    }
}
