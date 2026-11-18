package org.jhanp_dev.psraerobicosbackend.modules.aerobics.history.dao.impl;

import org.jhanp_dev.psraerobicosbackend.config.database.ConexionDB;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.history.dao.interfaces.IHistorialAuditoriaDAO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.history.dto.request.HistorialAuditoriaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.history.dto.response.HistorialAuditoriaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.history.entity.HistorialAuditoria;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.history.enums.AccionAuditoria;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.history.mapper.HistorialAuditoriaMapper;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class HistorialAuditoriaDAO implements IHistorialAuditoriaDAO {

    private final Connection conexion;
    private final HistorialAuditoriaMapper historialAuditoriaMapper;

    public HistorialAuditoriaDAO(HistorialAuditoriaMapper historialAuditoriaMapper) throws SQLException {
        this.conexion = ConexionDB.getConexion();
        this.historialAuditoriaMapper = historialAuditoriaMapper;
    }

    private static final String BASE_SELECT = """
            SELECT id, tabla_afectada, registro_id, campo, valor_anterior,
                   valor_nuevo, accion, usuario_id, fecha_hora
            FROM historial_auditoria
            """;

    @Override
    public List<HistorialAuditoriaResponseDTO> listarTodos() {
        String sql = BASE_SELECT + " ORDER BY fecha_hora DESC";
        List<HistorialAuditoriaResponseDTO> registros = new ArrayList<>();

        try (PreparedStatement stmt = conexion.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                HistorialAuditoria entidad = mapearFila(rs);
                registros.add(historialAuditoriaMapper.aResponseDTO(entidad));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al listar historial de auditoria: " + e.getMessage(), e);
        }
        return registros;
    }

    @Override
    public List<HistorialAuditoriaResponseDTO> listarPorTablaAfectada(String tablaAfectada) {
        String sql = BASE_SELECT + " WHERE tabla_afectada = ? ORDER BY fecha_hora DESC";
        List<HistorialAuditoriaResponseDTO> registros = new ArrayList<>();

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, tablaAfectada);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    HistorialAuditoria entidad = mapearFila(rs);
                    registros.add(historialAuditoriaMapper.aResponseDTO(entidad));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al listar historial de auditoria por tabla: " + e.getMessage(), e);
        }
        return registros;
    }

    @Override
    public Optional<HistorialAuditoriaResponseDTO> obtenerPorId(Integer id) {
        String sql = BASE_SELECT + " WHERE id = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    HistorialAuditoria entidad = mapearFila(rs);
                    return Optional.of(historialAuditoriaMapper.aResponseDTO(entidad));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al obtener historial de auditoria por id: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public HistorialAuditoriaResponseDTO guardar(HistorialAuditoriaRequestDTO dto) {
        HistorialAuditoria registro = historialAuditoriaMapper.aEntidad(dto);
        String sql = """
                INSERT INTO historial_auditoria (
                    tabla_afectada, registro_id, campo, valor_anterior,
                    valor_nuevo, accion, usuario_id
                ) VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement stmt = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, registro.getTablaAfectada());
            stmt.setInt(2, registro.getRegistroId());
            if (registro.getCampo() != null) {
                stmt.setString(3, registro.getCampo());
            } else {
                stmt.setNull(3, Types.VARCHAR);
            }
            if (registro.getValorAnterior() != null) {
                stmt.setString(4, registro.getValorAnterior());
            } else {
                stmt.setNull(4, Types.VARCHAR);
            }
            if (registro.getValorNuevo() != null) {
                stmt.setString(5, registro.getValorNuevo());
            } else {
                stmt.setNull(5, Types.VARCHAR);
            }
            stmt.setString(6, registro.getAccion().name());
            if (registro.getUsuarioId() != null) {
                stmt.setInt(7, registro.getUsuarioId());
            } else {
                stmt.setNull(7, Types.INTEGER);
            }

            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    registro.setId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al guardar historial de auditoria: " + e.getMessage(), e);
        }
        return historialAuditoriaMapper.aResponseDTO(registro);
    }

    @Override
    public boolean actualizar(Integer id, HistorialAuditoriaRequestDTO dto) {
        HistorialAuditoria registro = historialAuditoriaMapper.aEntidad(dto);
        registro.setId(id);
        String sql = """
                UPDATE historial_auditoria
                SET tabla_afectada = ?,
                    registro_id = ?,
                    campo = ?,
                    valor_anterior = ?,
                    valor_nuevo = ?,
                    accion = ?,
                    usuario_id = ?
                WHERE id = ?
                """;

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, registro.getTablaAfectada());
            stmt.setInt(2, registro.getRegistroId());
            if (registro.getCampo() != null) {
                stmt.setString(3, registro.getCampo());
            } else {
                stmt.setNull(3, Types.VARCHAR);
            }
            if (registro.getValorAnterior() != null) {
                stmt.setString(4, registro.getValorAnterior());
            } else {
                stmt.setNull(4, Types.VARCHAR);
            }
            if (registro.getValorNuevo() != null) {
                stmt.setString(5, registro.getValorNuevo());
            } else {
                stmt.setNull(5, Types.VARCHAR);
            }
            stmt.setString(6, registro.getAccion().name());
            if (registro.getUsuarioId() != null) {
                stmt.setInt(7, registro.getUsuarioId());
            } else {
                stmt.setNull(7, Types.INTEGER);
            }
            stmt.setInt(8, registro.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Error al actualizar historial de auditoria: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean eliminar(Integer id) {
        String sql = "DELETE FROM historial_auditoria WHERE id = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Error al eliminar historial de auditoria: " + e.getMessage(), e);
        }
    }

    private HistorialAuditoria mapearFila(ResultSet rs) throws SQLException {
        int usuarioRaw = rs.getInt("usuario_id");
        Integer usuarioId = rs.wasNull() ? null : usuarioRaw;
        Timestamp fechaHora = rs.getTimestamp("fecha_hora");

        return HistorialAuditoria.builder()
                .id(rs.getInt("id"))
                .tablaAfectada(rs.getString("tabla_afectada"))
                .registroId(rs.getInt("registro_id"))
                .campo(rs.getString("campo"))
                .valorAnterior(rs.getString("valor_anterior"))
                .valorNuevo(rs.getString("valor_nuevo"))
                .accion(AccionAuditoria.valueOf(rs.getString("accion")))
                .usuarioId(usuarioId)
                .fechaHora(fechaHora != null ? fechaHora.toLocalDateTime() : null)
                .build();
    }
}
