package org.jhanp_dev.psraerobicosbackend.modules.aerobics.schedule.dao.impl;

import org.jhanp_dev.psraerobicosbackend.config.database.ConexionDB;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.schedule.dao.interfaces.IHorarioDAO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.schedule.dto.request.ActualizarHorarioRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.schedule.dto.request.CrearHorarioRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.schedule.dto.response.HorarioResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.schedule.entity.Horario;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.schedule.mapper.HorarioMapper;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class HorarioDAO implements IHorarioDAO {

    private final HorarioMapper horarioMapper;

    public HorarioDAO(HorarioMapper horarioMapper) {
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

        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql);
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

        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql);
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

        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
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
    public HorarioResponseDTO guardar(CrearHorarioRequestDTO dto) {
        Horario horario = horarioMapper.aEntidad(dto);
        boolean activo = horario.getActivo() != null ? horario.getActivo() : true;

        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             CallableStatement stmt = conexion.prepareCall("{CALL sp_crear_horario(?, ?, ?, ?, ?)}")) {
            stmt.setInt(1, horario.getDiaSemana());
            stmt.setTime(2, Time.valueOf(horario.getHoraInicio()));
            stmt.setTime(3, Time.valueOf(horario.getHoraFin()));
            stmt.setBoolean(4, activo);
            stmt.registerOutParameter(5, Types.INTEGER);
            stmt.execute();
            horario.setId(stmt.getInt(5));
        } catch (SQLException e) {
            throw new IllegalStateException("Error al guardar horario: " + e.getMessage(), e);
        }
        horario.setActivo(activo);
        return horarioMapper.aResponseDTO(horario);
    }

    @Override
    public boolean actualizar(Integer id, ActualizarHorarioRequestDTO dto) {
        Horario horario = horarioMapper.aEntidad(dto);

        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             CallableStatement stmt = conexion.prepareCall("{CALL sp_actualizar_horario(?, ?, ?, ?, ?, ?)}")) {
            stmt.setInt(1, id);
            stmt.setInt(2, horario.getDiaSemana());
            stmt.setTime(3, Time.valueOf(horario.getHoraInicio()));
            stmt.setTime(4, Time.valueOf(horario.getHoraFin()));
            stmt.setBoolean(5, horario.getActivo());
            stmt.registerOutParameter(6, Types.BOOLEAN);
            stmt.execute();
            return stmt.getBoolean(6);
        } catch (SQLException e) {
            throw new IllegalStateException("Error al actualizar horario: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean eliminar(Integer id) {
        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             CallableStatement stmt = conexion.prepareCall("{CALL sp_desactivar_horario(?, ?)}")) {
            stmt.setInt(1, id);
            stmt.registerOutParameter(2, Types.BOOLEAN);
            stmt.execute();
            return stmt.getBoolean(2);
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
