package org.jhanp_dev.psraerobicosbackend.modules.aerobics.pricing.dao.impl;

import org.jhanp_dev.psraerobicosbackend.config.database.ConexionDB;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.pricing.dao.interfaces.ITarifaDAO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.pricing.dto.request.TarifaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.pricing.dto.response.TarifaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.pricing.entity.Tarifa;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.pricing.mapper.TarifaMapper;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class TarifaDAO implements ITarifaDAO {

    private final TarifaMapper tarifaMapper;

    public TarifaDAO(TarifaMapper tarifaMapper) {
        this.tarifaMapper = tarifaMapper;
    }

    private static final int TARIFA_SINGLETON_ID = 1;
    private static final String BASE_SELECT = """
            SELECT id, monto, actualizado_en, actualizado_por
            FROM tarifa
            """;

    @Override
    public List<TarifaResponseDTO> listarTodos() {
        String sql = BASE_SELECT + " ORDER BY id ASC";
        List<TarifaResponseDTO> tarifas = new ArrayList<>();

        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Tarifa entidad = mapearFila(rs);
                tarifas.add(tarifaMapper.aResponseDTO(entidad));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al listar tarifas: " + e.getMessage(), e);
        }
        return tarifas;
    }

    @Override
    public Optional<TarifaResponseDTO> obtenerPorId(Integer id) {
        String sql = BASE_SELECT + " WHERE id = ?";

        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Tarifa entidad = mapearFila(rs);
                    return Optional.of(tarifaMapper.aResponseDTO(entidad));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al obtener tarifa por id: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<TarifaResponseDTO> obtenerTarifaActual() {
        return obtenerPorId(TARIFA_SINGLETON_ID);
    }

    @Override
    public TarifaResponseDTO actualizarVigente(TarifaRequestDTO dto) {
        Tarifa tarifa = tarifaMapper.aEntidad(dto);
        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             CallableStatement stmt = conexion.prepareCall("{CALL sp_actualizar_tarifa(?, ?, ?)}")) {
            stmt.setBigDecimal(1, tarifa.getMonto());
            if (tarifa.getActualizadoPor() != null) {
                stmt.setInt(2, tarifa.getActualizadoPor());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            stmt.registerOutParameter(3, Types.BOOLEAN);
            stmt.execute();
            if (!stmt.getBoolean(3)) {
                throw new IllegalStateException("No existe la tarifa vigente para actualizar");
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al actualizar tarifa: " + e.getMessage(), e);
        }
        return obtenerTarifaActual().orElseThrow(() -> new IllegalStateException("No se encontró la tarifa vigente"));
    }

    private Tarifa mapearFila(ResultSet rs) throws SQLException {
        int actualizadoPorRaw = rs.getInt("actualizado_por");
        Integer actualizadoPor = rs.wasNull() ? null : actualizadoPorRaw;
        Timestamp actualizadoEn = rs.getTimestamp("actualizado_en");

        return Tarifa.builder()
                .id(rs.getInt("id"))
                .monto(rs.getBigDecimal("monto"))
                .actualizadoEn(actualizadoEn != null ? actualizadoEn.toLocalDateTime() : null)
                .actualizadoPor(actualizadoPor)
                .build();
    }
}
