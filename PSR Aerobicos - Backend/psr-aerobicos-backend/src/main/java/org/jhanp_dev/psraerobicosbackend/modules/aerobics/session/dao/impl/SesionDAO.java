package org.jhanp_dev.psraerobicosbackend.modules.aerobics.session.dao.impl;

import org.jhanp_dev.psraerobicosbackend.config.database.ConexionDB;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.session.dao.interfaces.ISesionDAO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.session.dto.request.SesionRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.session.dto.response.SesionResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.session.entity.Sesion;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.session.mapper.SesionMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class SesionDAO implements ISesionDAO {

    private final Connection conexion;
    private final SesionMapper sesionMapper;

    public SesionDAO(SesionMapper sesionMapper) throws SQLException {
        this.conexion = ConexionDB.getConexion();
        this.sesionMapper = sesionMapper;
    }

    private static final String BASE_SELECT = """
            SELECT id, horario_id, fecha, hora_inicio, hora_fin,
                   profesor_usuario_id, cancelada, total_asistencias, total_recaudo
            FROM sesion
            """;

    @Override
    public List<SesionResponseDTO> listarTodos() {
        String sql = BASE_SELECT + " ORDER BY fecha DESC, hora_inicio DESC";
        List<SesionResponseDTO> sesiones = new ArrayList<>();

        try (PreparedStatement stmt = conexion.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Sesion entidad = mapearFila(rs);
                sesiones.add(sesionMapper.aResponseDTO(entidad));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al listar sesiones: " + e.getMessage(), e);
        }
        return sesiones;
    }

    @Override
    public List<SesionResponseDTO> listarPorFecha(LocalDate fecha) {
        String sql = BASE_SELECT + " WHERE fecha = ? ORDER BY hora_inicio ASC";
        List<SesionResponseDTO> sesiones = new ArrayList<>();

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(fecha));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Sesion entidad = mapearFila(rs);
                    sesiones.add(sesionMapper.aResponseDTO(entidad));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al listar sesiones por fecha: " + e.getMessage(), e);
        }
        return sesiones;
    }

    @Override
    public Optional<SesionResponseDTO> obtenerPorId(Integer id) {
        String sql = BASE_SELECT + " WHERE id = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Sesion entidad = mapearFila(rs);
                    return Optional.of(sesionMapper.aResponseDTO(entidad));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al obtener sesion por id: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public SesionResponseDTO guardar(SesionRequestDTO dto) {
        Sesion sesion = sesionMapper.aEntidad(dto);
        String sql = """
                INSERT INTO sesion (
                    horario_id, fecha, hora_inicio, hora_fin,
                    profesor_usuario_id, cancelada, total_asistencias, total_recaudo
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        boolean cancelada = sesion.getCancelada() != null ? sesion.getCancelada() : false;
        int totalAsistencias = sesion.getTotalAsistencias() != null ? sesion.getTotalAsistencias() : 0;
        BigDecimal totalRecaudo = sesion.getTotalRecaudo() != null ? sesion.getTotalRecaudo() : BigDecimal.ZERO;

        try (PreparedStatement stmt = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (sesion.getHorarioId() != null) {
                stmt.setInt(1, sesion.getHorarioId());
            } else {
                stmt.setNull(1, Types.INTEGER);
            }
            stmt.setDate(2, Date.valueOf(sesion.getFecha()));
            stmt.setTimestamp(3, Timestamp.valueOf(sesion.getHoraInicio()));
            if (sesion.getHoraFin() != null) {
                stmt.setTimestamp(4, Timestamp.valueOf(sesion.getHoraFin()));
            } else {
                stmt.setNull(4, Types.TIMESTAMP);
            }
            if (sesion.getProfesorUsuarioId() != null) {
                stmt.setInt(5, sesion.getProfesorUsuarioId());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            stmt.setBoolean(6, cancelada);
            stmt.setInt(7, totalAsistencias);
            stmt.setBigDecimal(8, totalRecaudo);

            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    sesion.setId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al guardar sesion: " + e.getMessage(), e);
        }
        sesion.setCancelada(cancelada);
        sesion.setTotalAsistencias(totalAsistencias);
        sesion.setTotalRecaudo(totalRecaudo);
        return sesionMapper.aResponseDTO(sesion);
    }

    @Override
    public boolean actualizar(Integer id, SesionRequestDTO dto) {
        Sesion sesion = sesionMapper.aEntidad(dto);
        sesion.setId(id);
        String sql = """
                UPDATE sesion
                SET horario_id = ?,
                    fecha = ?,
                    hora_inicio = ?,
                    hora_fin = ?,
                    profesor_usuario_id = ?,
                    cancelada = ?,
                    total_asistencias = ?,
                    total_recaudo = ?
                WHERE id = ?
                """;

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            if (sesion.getHorarioId() != null) {
                stmt.setInt(1, sesion.getHorarioId());
            } else {
                stmt.setNull(1, Types.INTEGER);
            }
            stmt.setDate(2, Date.valueOf(sesion.getFecha()));
            stmt.setTimestamp(3, Timestamp.valueOf(sesion.getHoraInicio()));
            if (sesion.getHoraFin() != null) {
                stmt.setTimestamp(4, Timestamp.valueOf(sesion.getHoraFin()));
            } else {
                stmt.setNull(4, Types.TIMESTAMP);
            }
            if (sesion.getProfesorUsuarioId() != null) {
                stmt.setInt(5, sesion.getProfesorUsuarioId());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            stmt.setBoolean(6, sesion.getCancelada());
            stmt.setInt(7, sesion.getTotalAsistencias());
            stmt.setBigDecimal(8, sesion.getTotalRecaudo());
            stmt.setInt(9, sesion.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Error al actualizar sesion: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean eliminar(Integer id) {
        String sql = "DELETE FROM sesion WHERE id = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Error al eliminar sesion: " + e.getMessage(), e);
        }
    }

    private Sesion mapearFila(ResultSet rs) throws SQLException {
        int horarioRaw = rs.getInt("horario_id");
        Integer horarioId = rs.wasNull() ? null : horarioRaw;

        int profesorRaw = rs.getInt("profesor_usuario_id");
        Integer profesorUsuarioId = rs.wasNull() ? null : profesorRaw;

        Date fecha = rs.getDate("fecha");
        Timestamp horaInicio = rs.getTimestamp("hora_inicio");
        Timestamp horaFin = rs.getTimestamp("hora_fin");

        return Sesion.builder()
                .id(rs.getInt("id"))
                .horarioId(horarioId)
                .fecha(fecha != null ? fecha.toLocalDate() : null)
                .horaInicio(horaInicio != null ? horaInicio.toLocalDateTime() : null)
                .horaFin(horaFin != null ? horaFin.toLocalDateTime() : null)
                .profesorUsuarioId(profesorUsuarioId)
                .cancelada(rs.getBoolean("cancelada"))
                .totalAsistencias(rs.getInt("total_asistencias"))
                .totalRecaudo(rs.getBigDecimal("total_recaudo"))
                .build();
    }
}
