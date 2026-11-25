package org.jhanp_dev.psraerobicosbackend.modules.user.dao.impl;

import org.jhanp_dev.psraerobicosbackend.config.database.ConexionDB;
import org.jhanp_dev.psraerobicosbackend.modules.user.dao.interfaces.IUsuarioDAO;
import org.jhanp_dev.psraerobicosbackend.modules.user.dto.response.UsuarioResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.user.entity.Usuario;
import org.jhanp_dev.psraerobicosbackend.modules.user.mapper.UsuarioMapper;
import org.springframework.data.domain.Pageable;
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
public class UsuarioDAO implements IUsuarioDAO {

    private final UsuarioMapper usuarioMapper;

    public UsuarioDAO(UsuarioMapper usuarioMapper) {
        this.usuarioMapper = usuarioMapper;
    }

    private static final String BASE_SELECT = """
            SELECT u.id, u.numero_documento, u.correo, u.password_hash, u.persona_id, u.activo, u.fecha_creacion,
                   CONCAT(p.nombre, ' ', p.apellidos) AS nombre_completo,
                   (SELECT GROUP_CONCAT(r.nombre ORDER BY r.nombre SEPARATOR ',')
                    FROM usuario_rol ur JOIN rol r ON r.id = ur.rol_id WHERE ur.usuario_id = u.id) AS roles
            FROM usuario u
            LEFT JOIN persona p ON p.id = u.persona_id
            """;

    @Override
    public List<UsuarioResponseDTO> listarPaginado(String termino, Pageable pageable) {
        String filtro = normalizarTermino(termino);
        String where = filtro == null ? "" : " WHERE p.nombre LIKE ? OR p.apellidos LIKE ? OR u.numero_documento LIKE ? OR u.correo LIKE ?";
        String sql = BASE_SELECT + where + " ORDER BY u.id ASC LIMIT ? OFFSET ?";
        List<UsuarioResponseDTO> usuarios = new ArrayList<>();
        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            int indice = 1;
            if (filtro != null) {
                String patron = "%" + filtro + "%";
                for (int i = 0; i < 4; i++) stmt.setString(indice++, patron);
            }
            stmt.setInt(indice++, pageable.getPageSize());
            stmt.setLong(indice, pageable.getOffset());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) usuarios.add(mapearResponse(rs, mapearFila(rs)));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al listar usuarios: " + e.getMessage(), e);
        }
        return usuarios;
    }

    @Override
    public long contar(String termino) {
        String filtro = normalizarTermino(termino);
        String where = filtro == null ? "" : " WHERE p.nombre LIKE ? OR p.apellidos LIKE ? OR u.numero_documento LIKE ? OR u.correo LIKE ?";
        String sql = "SELECT COUNT(*) FROM usuario u LEFT JOIN persona p ON p.id = u.persona_id" + where;
        return contar(sql, filtro, 4, "usuarios");
    }

    @Override
    public List<UsuarioResponseDTO> listarProfesoresPaginado(Pageable pageable) {
        String where = " WHERE EXISTS (SELECT 1 FROM usuario_rol ur JOIN rol r ON r.id = ur.rol_id "
                + "WHERE ur.usuario_id = u.id AND r.nombre IN ('PROFESOR', 'ADMINISTRADOR'))";
        String sql = BASE_SELECT + where + " ORDER BY u.id ASC LIMIT ? OFFSET ?";
        List<UsuarioResponseDTO> profesores = new ArrayList<>();
        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, pageable.getPageSize());
            stmt.setLong(2, pageable.getOffset());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) profesores.add(mapearResponse(rs, mapearFila(rs)));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al listar profesores: " + e.getMessage(), e);
        }
        return profesores;
    }

    @Override
    public long contarProfesores() {
        String sql = "SELECT COUNT(*) FROM usuario u WHERE EXISTS ("
                + "SELECT 1 FROM usuario_rol ur JOIN rol r ON r.id = ur.rol_id "
                + "WHERE ur.usuario_id = u.id AND r.nombre IN ('PROFESOR', 'ADMINISTRADOR'))";
        return contar(sql, null, 0, "profesores");
    }

    private long contar(String sql, String filtro, int parametrosFiltro, String descripcion) {
        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            if (filtro != null) {
                String patron = "%" + filtro + "%";
                for (int i = 1; i <= parametrosFiltro; i++) stmt.setString(i, patron);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0L;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al contar " + descripcion + ": " + e.getMessage(), e);
        }
    }

    private String normalizarTermino(String termino) {
        return termino == null || termino.isBlank() ? null : termino.trim();
    }

    @Override
    public UsuarioResponseDTO crearCuenta(Integer personaId, String passwordHash, String correo) {
        String llamada = "{CALL sp_registrar_usuario_para_persona(?, ?, ?, ?)}";
        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             CallableStatement stmt = conexion.prepareCall(llamada)) {
            stmt.setInt(1, personaId);
            stmt.setString(2, passwordHash);
            stmt.setString(3, correo);
            stmt.registerOutParameter(4, Types.INTEGER);
            stmt.execute();
            int id = stmt.getInt(4);
            return obtenerPorId(id).orElseThrow(() -> new IllegalStateException("No se encontró la cuenta creada"));
        } catch (SQLException e) {
            throw new IllegalStateException("Error al crear la cuenta: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<UsuarioResponseDTO> obtenerPorId(Integer id) {
        String sql = BASE_SELECT + " WHERE u.id = ?";

        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Usuario entidad = mapearFila(rs);
                    return Optional.of(mapearResponse(rs, entidad));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al obtener usuario por id: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<UsuarioResponseDTO> obtenerPorCorreo(String correo) {
        String sql = BASE_SELECT + " WHERE u.correo = ?";

        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, correo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Usuario entidad = mapearFila(rs);
                    return Optional.of(mapearResponse(rs, entidad));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al obtener usuario por correo: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<UsuarioResponseDTO> obtenerPorNumeroDocumento(String numeroDocumento) {
        String sql = BASE_SELECT + " WHERE u.numero_documento = ?";

        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, numeroDocumento);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Usuario entidad = mapearFila(rs);
                    return Optional.of(mapearResponse(rs, entidad));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al obtener usuario por documento: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Usuario> obtenerParaAutenticacion(String identificador) {
        String sql = BASE_SELECT + " WHERE u.numero_documento = ? OR u.correo = ? LIMIT 1";
        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, identificador);
            stmt.setString(2, identificador);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearFila(rs));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al consultar las credenciales", e);
        }
        return Optional.empty();
    }

    private Usuario mapearFila(ResultSet rs) throws SQLException {
        int personaRaw = rs.getInt("persona_id");
        Integer personaId = rs.wasNull() ? null : personaRaw;
        Timestamp fechaCreacion = rs.getTimestamp("fecha_creacion");

        return Usuario.builder()
                .id(rs.getInt("id"))
                .numeroDocumento(rs.getString("numero_documento"))
                .correo(rs.getString("correo"))
                .passwordHash(rs.getString("password_hash"))
                .personaId(personaId)
                .activo(rs.getBoolean("activo"))
                .fechaCreacion(fechaCreacion != null ? fechaCreacion.toLocalDateTime() : null)
                .build();
    }

    private UsuarioResponseDTO mapearResponse(ResultSet rs, Usuario usuario) throws SQLException {
        String rolesCsv = rs.getString("roles");
        List<String> roles = rolesCsv == null || rolesCsv.isBlank()
                ? List.of()
                : java.util.Arrays.asList(rolesCsv.split(","));
        return usuarioMapper.aResponseDTO(usuario, rs.getString("nombre_completo"), roles);
    }
}
