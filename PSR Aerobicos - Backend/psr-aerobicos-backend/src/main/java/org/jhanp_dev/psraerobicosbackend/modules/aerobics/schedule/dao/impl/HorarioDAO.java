package org.jhanp_dev.psraerobicosbackend.modules.aerobics.schedule.dao.impl;

import org.jhanp_dev.psraerobicosbackend.config.database.ConexionDB;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.schedule.dao.interfaces.IHorarioDAO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.schedule.dto.request.HorarioRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.schedule.dto.response.HorarioResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.schedule.entity.Horario;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.schedule.mapper.HorarioMapper;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class HorarioDAO implements IHorarioDAO {

    private final Connection conexion;
    private final HorarioMapper horarioMapper;

    public HorarioDAO(HorarioMapper horarioMapper) throws SQLException {
        this.conexion = ConexionDB.getConexion();
        this.horarioMapper = horarioMapper;
    }

    private static final String BASE_SELECT = """
            SELECT id, dia_semana, hora_inicio, hora_fin, activo
            FROM horario
            """;

    @Override
    public List<HorarioResponseDTO> listarTodos() {
        String sql = BASE_SELECT + " ORDER BY dia_semana ASC, hora_inicio ASC";
        List<HorarioResponseDTO> horarios = new ArrayList<>();

        try (PreparedStatement stmt = conexion.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Horario entidad = mapearFila(rs);
                horarios.add(horarioMapper.aResponseDTO(entidad));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al listar horarios: " + e.getMessage(), e);
        }
        return horarios;
    }

    @Override
    public List<HorarioResponseDTO> listarActivos() {
        String sql = BASE_SELECT + " WHERE activo = TRUE ORDER BY dia_semana ASC, hora_inicio ASC";
        List<HorarioResponseDTO> horarios = new ArrayList<>();

        try (PreparedStatement stmt = conexion.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Horario entidad = mapearFila(rs);
                horarios.add(horarioMapper.aResponseDTO(entidad));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al listar horarios activos: " + e.getMessage(), e);
        }
        return horarios;
    }

    @Override
    public Optional<HorarioResponseDTO> obtenerPorId(Integer id) {
        String sql = BASE_SELECT + " WHERE id = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Horario entidad = mapearFila(rs);
                    return Optional.of(horarioMapper.aResponseDTO(entidad));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al obtener horario por id: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public HorarioResponseDTO guardar(HorarioRequestDTO dto) {
        Horario horario = horarioMapper.aEntidad(dto);
        String sql = "INSERT INTO horario (dia_semana, hora_inicio, hora_fin, activo) VALUES (?, ?, ?, ?)";
        boolean activo = horario.getActivo() != null ? horario.getActivo() : true;

        try (PreparedStatement stmt = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, horario.getDiaSemana());
            stmt.setTime(2, Time.valueOf(horario.getHoraInicio()));
            stmt.setTime(3, Time.valueOf(horario.getHoraFin()));
            stmt.setBoolean(4, activo);
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    horario.setId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al guardar horario: " + e.getMessage(), e);
        }
        horario.setActivo(activo);
        return horarioMapper.aResponseDTO(horario);
    }

    @Override
    public boolean actualizar(Integer id, HorarioRequestDTO dto) {
        Horario horario = horarioMapper.aEntidad(dto);
        horario.setId(id);
        String sql = "UPDATE horario SET dia_semana = ?, hora_inicio = ?, hora_fin = ?, activo = ? WHERE id = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, horario.getDiaSemana());
            stmt.setTime(2, Time.valueOf(horario.getHoraInicio()));
            stmt.setTime(3, Time.valueOf(horario.getHoraFin()));
            stmt.setBoolean(4, horario.getActivo());
            stmt.setInt(5, horario.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Error al actualizar horario: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean eliminar(Integer id) {
        String sql = "UPDATE horario SET activo = FALSE WHERE id = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Error al eliminar horario: " + e.getMessage(), e);
        }
    }

    private Horario mapearFila(ResultSet rs) throws SQLException {
        Time horaInicio = rs.getTime("hora_inicio");
        Time horaFin = rs.getTime("hora_fin");
        return Horario.builder()
                .id(rs.getInt("id"))
                .diaSemana(rs.getInt("dia_semana"))
                .horaInicio(horaInicio != null ? horaInicio.toLocalTime() : null)
                .horaFin(horaFin != null ? horaFin.toLocalTime() : null)
                .activo(rs.getBoolean("activo"))
                .build();
    }
}
