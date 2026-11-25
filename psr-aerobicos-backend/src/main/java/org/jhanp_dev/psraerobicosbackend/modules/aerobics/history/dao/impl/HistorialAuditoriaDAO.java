package org.jhanp_dev.psraerobicosbackend.modules.aerobics.history.dao.impl;

import org.jhanp_dev.psraerobicosbackend.config.database.ConexionDB;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.history.dao.interfaces.IHistorialAuditoriaDAO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.history.dto.response.HistorialAuditoriaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.history.entity.HistorialAuditoria;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.history.enums.AccionAuditoria;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.history.mapper.HistorialAuditoriaMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Repository
public class HistorialAuditoriaDAO implements IHistorialAuditoriaDAO {

    private final HistorialAuditoriaMapper historialAuditoriaMapper;

    public HistorialAuditoriaDAO(HistorialAuditoriaMapper historialAuditoriaMapper) {
        this.historialAuditoriaMapper = historialAuditoriaMapper;
    }

    private static final String BASE_SELECT = """
            SELECT v.auditoria_id AS id, v.tabla_afectada, v.registro_id, v.campo,
                   v.valor_anterior, v.valor_nuevo, v.accion, h.usuario_id,
                   v.usuario_responsable, v.fecha_hora
            FROM vista_historial_cambios v
            JOIN historial_auditoria h ON h.id = v.auditoria_id
            """;

    @Override
    public List<HistorialAuditoriaResponseDTO> listarPaginado(Pageable pageable) {
        String sql = BASE_SELECT + " ORDER BY v.fecha_hora DESC, v.auditoria_id DESC LIMIT ? OFFSET ?";
        List<HistorialAuditoriaResponseDTO> historialCambios = new ArrayList<>();

        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, pageable.getPageSize());
            stmt.setLong(2, pageable.getOffset());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    HistorialAuditoria entidad = mapearFila(rs);
                    historialCambios.add(historialAuditoriaMapper.aResponseDTO(entidad, rs.getString("usuario_responsable")));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al listar historial de auditoría: " + e.getMessage(), e);
        }
        return historialCambios;
    }

    @Override
    public long contar() {
        String sql = "SELECT COUNT(*) FROM vista_historial_cambios";
        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0L;
        } catch (SQLException e) {
            throw new IllegalStateException("Error al contar historial de auditoría: " + e.getMessage(), e);
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
