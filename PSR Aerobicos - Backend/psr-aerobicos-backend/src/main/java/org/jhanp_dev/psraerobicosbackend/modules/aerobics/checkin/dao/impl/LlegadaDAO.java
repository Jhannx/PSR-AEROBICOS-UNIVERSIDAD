package org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.dao.impl;

import org.jhanp_dev.psraerobicosbackend.config.database.ConexionDB;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.dao.interfaces.ILlegadaDAO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.dto.request.LlegadaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.dto.response.LlegadaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.entity.Llegada;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.enums.MetodoCheckin;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.mapper.LlegadaMapper;
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
public class LlegadaDAO implements ILlegadaDAO {

    private final Connection conexion;
    private final LlegadaMapper llegadaMapper;

    public LlegadaDAO(LlegadaMapper llegadaMapper) throws SQLException {
        this.conexion = ConexionDB.getConexion();
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

        try (PreparedStatement stmt = conexion.prepareStatement(sql);
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

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
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

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
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

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
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
    public LlegadaResponseDTO guardar(LlegadaRequestDTO dto) {
        Llegada llegada = llegadaMapper.aEntidad(dto);
        String sql = "INSERT INTO llegada (persona_id, sesion_id, metodo_checkin) VALUES (?, ?, ?)";
        MetodoCheckin metodo = llegada.getMetodoCheckin() != null ? llegada.getMetodoCheckin() : MetodoCheckin.MANUAL;

        try (PreparedStatement stmt = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, llegada.getPersonaId());
            stmt.setInt(2, llegada.getSesionId());
            stmt.setString(3, metodo.name());
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    llegada.setId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al guardar llegada: " + e.getMessage(), e);
        }
        llegada.setMetodoCheckin(metodo);
        return llegadaMapper.aResponseDTO(llegada);
    }

    @Override
    public boolean actualizar(Integer id, LlegadaRequestDTO dto) {
        Llegada llegada = llegadaMapper.aEntidad(dto);
        llegada.setId(id);
        String sql = "UPDATE llegada SET persona_id = ?, sesion_id = ?, metodo_checkin = ? WHERE id = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, llegada.getPersonaId());
            stmt.setInt(2, llegada.getSesionId());
            stmt.setString(3, llegada.getMetodoCheckin().name());
            stmt.setInt(4, llegada.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Error al actualizar llegada: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean eliminar(Integer id) {
        String sql = "DELETE FROM llegada WHERE id = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Error al eliminar llegada: " + e.getMessage(), e);
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
