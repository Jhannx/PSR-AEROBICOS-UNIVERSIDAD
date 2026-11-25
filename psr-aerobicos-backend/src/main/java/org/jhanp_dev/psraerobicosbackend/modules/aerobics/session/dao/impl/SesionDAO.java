package org.jhanp_dev.psraerobicosbackend.modules.aerobics.session.dao.impl;

import org.jhanp_dev.psraerobicosbackend.config.database.ConexionDB;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.session.dao.interfaces.ISesionDAO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.session.dto.request.SesionRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.session.dto.response.SesionResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.session.entity.Sesion;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.session.mapper.SesionMapper;
import org.jhanp_dev.psraerobicosbackend.shared.exception.ConflictException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class SesionDAO implements ISesionDAO {

    private final SesionMapper sesionMapper;

    public SesionDAO(SesionMapper sesionMapper) {
        this.sesionMapper = sesionMapper;
    }

    private static final String BASE_SELECT = """
            SELECT s.id, s.horario_id, s.fecha, s.hora_inicio, s.hora_fin,
                   s.profesor_usuario_id, s.cancelada, s.total_asistencias, s.total_recaudo,
                   CONCAT(p.nombre, ' ', p.apellidos) AS instructor
            FROM sesion s
            LEFT JOIN usuario u ON u.id = s.profesor_usuario_id
            LEFT JOIN persona p ON p.id = u.persona_id
            """;

    private String condicionFiltro(LocalDate fecha, String estado) {
        StringBuilder condicion = new StringBuilder(" WHERE 1=1");
        if (fecha != null) condicion.append(" AND s.fecha = ?");
        if (estado != null) {
            switch (estado) {
                case "PROGRAMADA" -> condicion.append(" AND s.cancelada = FALSE AND s.hora_inicio > ?");
                case "EN_CURSO" -> condicion.append(" AND s.cancelada = FALSE AND s.hora_inicio <= ? AND s.hora_fin >= ?");
                case "FINALIZADA" -> condicion.append(" AND s.cancelada = FALSE AND s.hora_fin < ?");
                case "CANCELADA" -> condicion.append(" AND s.cancelada = TRUE");
                default -> throw new IllegalArgumentException("Estado de sesión inválido");
            }
        }
        return condicion.toString();
    }

    private int asignarFiltro(PreparedStatement stmt, LocalDate fecha, String estado, LocalDateTime ahora) throws SQLException {
        int indice = 1;
        if (fecha != null) stmt.setDate(indice++, Date.valueOf(fecha));
        if ("PROGRAMADA".equals(estado) || "FINALIZADA".equals(estado)) {
            stmt.setTimestamp(indice++, Timestamp.valueOf(ahora));
        } else if ("EN_CURSO".equals(estado)) {
            stmt.setTimestamp(indice++, Timestamp.valueOf(ahora));
            stmt.setTimestamp(indice++, Timestamp.valueOf(ahora));
        }
        return indice;
    }

    @Override
    public List<SesionResponseDTO> listarFiltrado(LocalDate fecha, String estado, LocalDateTime ahora, Pageable pageable) {
        String orden = "PROGRAMADA".equals(estado)
                ? " ORDER BY s.hora_inicio ASC, s.id ASC"
                : " ORDER BY s.fecha DESC, s.hora_inicio DESC, s.id DESC";
        String sql = BASE_SELECT + condicionFiltro(fecha, estado) + orden + " LIMIT ? OFFSET ?";
        List<SesionResponseDTO> sesiones = new ArrayList<>();
        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            int indice = asignarFiltro(stmt, fecha, estado, ahora);
            stmt.setInt(indice++, pageable.getPageSize());
            stmt.setLong(indice, pageable.getOffset());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) sesiones.add(sesionMapper.aResponseDTO(mapearFila(rs), rs.getString("instructor")));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al consultar sesiones filtradas", e);
        }
        return sesiones;
    }

    @Override
    public long contarFiltrado(LocalDate fecha, String estado, LocalDateTime ahora) {
        String sql = "SELECT COUNT(*) FROM sesion s" + condicionFiltro(fecha, estado);
        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            asignarFiltro(stmt, fecha, estado, ahora);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0L;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al contar sesiones filtradas", e);
        }
    }

    @Override
    public Set<String> listarHorariosProgramados(LocalDate desde, LocalDate hasta) {
        String sql = "SELECT horario_id, fecha FROM sesion WHERE horario_id IS NOT NULL AND fecha BETWEEN ? AND ?";
        Set<String> existentes = new HashSet<>();
        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(desde));
            stmt.setDate(2, Date.valueOf(hasta));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) existentes.add(rs.getInt("horario_id") + ":" + rs.getDate("fecha").toLocalDate());
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al consultar la programación recurrente", e);
        }
        return existentes;
    }

    @Override
    public List<SesionResponseDTO> listarPaginado(Pageable pageable) {
        String sql = BASE_SELECT + " ORDER BY s.fecha DESC, s.hora_inicio DESC, s.id DESC LIMIT ? OFFSET ?";
        List<SesionResponseDTO> sesiones = new ArrayList<>();

        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, pageable.getPageSize());
            stmt.setLong(2, pageable.getOffset());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Sesion entidad = mapearFila(rs);
                    sesiones.add(sesionMapper.aResponseDTO(entidad, rs.getString("instructor")));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al listar sesiones: " + e.getMessage(), e);
        }
        return sesiones;
    }

    @Override
    public long contar() {
        String sql = "SELECT COUNT(*) FROM sesion";
        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0L;
        } catch (SQLException e) {
            throw new IllegalStateException("Error al contar sesiones: " + e.getMessage(), e);
        }
    }

    @Override
    public List<SesionResponseDTO> listarPorFechaPaginado(LocalDate fecha, Pageable pageable) {
        String sql = BASE_SELECT + " WHERE s.fecha = ? ORDER BY s.hora_inicio ASC, s.id ASC LIMIT ? OFFSET ?";
        List<SesionResponseDTO> sesiones = new ArrayList<>();

        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(fecha));
            stmt.setInt(2, pageable.getPageSize());
            stmt.setLong(3, pageable.getOffset());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Sesion entidad = mapearFila(rs);
                    sesiones.add(sesionMapper.aResponseDTO(entidad, rs.getString("instructor")));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al listar sesiones por fecha: " + e.getMessage(), e);
        }
        return sesiones;
    }

    @Override
    public long contarPorFecha(LocalDate fecha) {
        String sql = "SELECT COUNT(*) FROM sesion WHERE fecha = ?";
        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(fecha));
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0L;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al contar sesiones por fecha: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<SesionResponseDTO> obtenerPorId(Integer id) {
        String sql = BASE_SELECT + " WHERE s.id = ?";

        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Sesion entidad = mapearFila(rs);
                    return Optional.of(sesionMapper.aResponseDTO(entidad, rs.getString("instructor")));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al obtener sesion por id: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public boolean existePorHorarioYFecha(Integer horarioId, LocalDate fecha) {
        String sql = "SELECT 1 FROM sesion WHERE horario_id = ? AND fecha = ? LIMIT 1";
        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, horarioId);
            stmt.setDate(2, Date.valueOf(fecha));
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al validar la disponibilidad de la sesión", e);
        }
    }

    @Override
    public SesionResponseDTO guardar(SesionRequestDTO dto) {
        Sesion sesion = sesionMapper.aEntidad(dto);
        String sql = "{CALL sp_crear_sesion_manual(?, ?, ?, ?, ?, ?)}";
        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             CallableStatement stmt = conexion.prepareCall(sql)) {
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
            stmt.registerOutParameter(6, Types.INTEGER);
            stmt.execute();
            sesion.setId(stmt.getInt(6));
        } catch (SQLException e) {
            if ("45000".equals(e.getSQLState()) && e.getMessage() != null
                    && e.getMessage().toLowerCase(java.util.Locale.ROOT).contains("ya existe una sesion")) {
                throw new ConflictException("Ya existe una sesión para ese horario y fecha");
            }
            throw new IllegalStateException("Error al guardar sesion: " + e.getMessage(), e);
        }
        return obtenerPorId(sesion.getId()).orElseThrow(() -> new IllegalStateException("No se encontró la sesión creada"));
    }

    @Override
    public boolean cancelar(Integer id, Integer usuarioId) {
        String sql = "{CALL sp_cancelar_sesion(?, ?)}";
        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             CallableStatement stmt = conexion.prepareCall(sql)) {
            stmt.setInt(1, id);
            stmt.setInt(2, usuarioId);
            stmt.execute();
            return true;
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
