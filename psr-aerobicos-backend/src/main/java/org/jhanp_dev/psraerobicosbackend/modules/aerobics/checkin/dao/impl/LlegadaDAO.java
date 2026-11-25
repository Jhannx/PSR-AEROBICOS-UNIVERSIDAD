package org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.dao.impl;

import org.jhanp_dev.psraerobicosbackend.config.database.ConexionDB;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.dao.interfaces.ILlegadaDAO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.dto.response.LlegadaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.entity.Llegada;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.enums.MetodoCheckin;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.mapper.LlegadaMapper;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class LlegadaDAO implements ILlegadaDAO {

    private final LlegadaMapper llegadaMapper;

    public LlegadaDAO(LlegadaMapper llegadaMapper) {
        this.llegadaMapper = llegadaMapper;
    }

    private static final String BASE_SELECT = """
            SELECT id, persona_id, sesion_id, hora_llegada, metodo_checkin
            FROM llegada
            """;

    @Override
    public List<LlegadaResponseDTO> listarTodos() {
        String sql = BASE_SELECT + " ORDER BY hora_llegada DESC";
        List<LlegadaResponseDTO> llegadas = new ArrayList<>();

        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Llegada entidad = mapearFila(rs);
                llegadas.add(llegadaMapper.aResponseDTO(entidad));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al listar llegadas: " + e.getMessage(), e);
        }
        return llegadas;
    }

    @Override
    public List<LlegadaResponseDTO> listarPorSesionId(Integer sesionId) {
        String sql = BASE_SELECT + " WHERE sesion_id = ? ORDER BY hora_llegada ASC";
        List<LlegadaResponseDTO> llegadas = new ArrayList<>();

        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, sesionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Llegada entidad = mapearFila(rs);
                    llegadas.add(llegadaMapper.aResponseDTO(entidad));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al listar llegadas por sesion: " + e.getMessage(), e);
        }
        return llegadas;
    }

    @Override
    public List<LlegadaResponseDTO> listarPorPersonaId(Integer personaId) {
        String sql = BASE_SELECT + " WHERE persona_id = ? ORDER BY hora_llegada DESC";
        List<LlegadaResponseDTO> llegadas = new ArrayList<>();

        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, personaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Llegada entidad = mapearFila(rs);
                    llegadas.add(llegadaMapper.aResponseDTO(entidad));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al listar llegadas por persona: " + e.getMessage(), e);
        }
        return llegadas;
    }

    @Override
    public Optional<LlegadaResponseDTO> obtenerPorId(Integer id) {
        String sql = BASE_SELECT + " WHERE id = ?";

        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Llegada entidad = mapearFila(rs);
                    return Optional.of(llegadaMapper.aResponseDTO(entidad));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al obtener llegada por id: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public LlegadaResponseDTO registrar(Integer personaId, Integer sesionId, MetodoCheckin metodo) {
        MetodoCheckin metodoEfectivo = metodo == null ? MetodoCheckin.MANUAL : metodo;
        String llamada = "{CALL sp_registrar_llegada(?, ?, ?, ?)}";
        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             CallableStatement stmt = conexion.prepareCall(llamada)) {
            stmt.setInt(1, personaId);
            stmt.setInt(2, sesionId);
            stmt.setString(3, metodoEfectivo.name());
            stmt.registerOutParameter(4, java.sql.Types.INTEGER);
            stmt.execute();
            int id = stmt.getInt(4);
            return obtenerPorId(id).orElseThrow(() -> new IllegalStateException("No se encontró la llegada registrada"));
        } catch (SQLException e) {
            throw new IllegalStateException("Error al registrar llegada: " + e.getMessage(), e);
        }
    }

    private Llegada mapearFila(ResultSet rs) throws SQLException {
        Timestamp horaLlegada = rs.getTimestamp("hora_llegada");
        return Llegada.builder()
                .id(rs.getInt("id"))
                .personaId(rs.getInt("persona_id"))
                .sesionId(rs.getInt("sesion_id"))
                .horaLlegada(horaLlegada != null ? horaLlegada.toLocalDateTime() : null)
                .metodoCheckin(MetodoCheckin.valueOf(rs.getString("metodo_checkin")))
                .build();
    }
}
