package org.jhanp_dev.psraerobicosbackend.modules.user.dao.impl;

import org.jhanp_dev.psraerobicosbackend.config.database.ConexionDB;
import org.jhanp_dev.psraerobicosbackend.modules.user.dao.interfaces.IUsuarioDAO;
import org.jhanp_dev.psraerobicosbackend.modules.user.dto.request.UsuarioRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.user.dto.response.UsuarioResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.user.entity.Usuario;
import org.jhanp_dev.psraerobicosbackend.modules.user.mapper.UsuarioMapper;
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

    private final Connection conexion;
    private final UsuarioMapper usuarioMapper;

    public UsuarioDAO(UsuarioMapper usuarioMapper) throws SQLException {
        this.conexion = ConexionDB.getConexion();
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
    public List<UsuarioResponseDTO> listarTodos() {
        String sql = BASE_SELECT + " ORDER BY u.id ASC";
        List<UsuarioResponseDTO> usuarios = new ArrayList<>();

        try (PreparedStatement stmt = conexion.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Usuario entidad = mapearFila(rs);
                usuarios.add(mapearResponse(rs, entidad));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al listar usuarios: " + e.getMessage(), e);
        }
        return usuarios;
    }

    @Override
    public List<UsuarioResponseDTO> buscar(String termino) {
        String sql = BASE_SELECT + " WHERE p.nombre LIKE ? OR p.apellidos LIKE ? OR u.numero_documento LIKE ? OR u.correo LIKE ? ORDER BY u.id";
        String patron = "%" + termino + "%";
        List<UsuarioResponseDTO> lista = new ArrayList<>();
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            for (int i = 1; i <= 4; i++) stmt.setString(i, patron);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapearResponse(rs, mapearFila(rs)));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al buscar usuarios: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public UsuarioResponseDTO crearCuenta(Integer personaId, String passwordHash, String correo) {
        String llamada = "{CALL sp_registrar_usuario_para_persona(?, ?, ?, ?)}";
        try (CallableStatement stmt = conexion.prepareCall(llamada)) {
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

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
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

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
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

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
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
    public boolean actualizar(Integer id, UsuarioRequestDTO dto) {
        Usuario usuario = usuarioMapper.aEntidad(dto);
        usuario.setId(id);
        String sql = """
                UPDATE usuario
                SET numero_documento = ?,
                    correo = ?,
                    persona_id = ?,
                    activo = ?
                WHERE id = ?
                """;

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            if (usuario.getNumeroDocumento() != null) {
                stmt.setString(1, usuario.getNumeroDocumento());
            } else {
                stmt.setNull(1, Types.VARCHAR);
            }
            if (usuario.getCorreo() != null) {
                stmt.setString(2, usuario.getCorreo());
            } else {
                stmt.setNull(2, Types.VARCHAR);
            }
            if (usuario.getPersonaId() != null) {
                stmt.setInt(3, usuario.getPersonaId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            stmt.setBoolean(4, usuario.getActivo() != null ? usuario.getActivo() : true);
            stmt.setInt(5, usuario.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Error al actualizar usuario: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean eliminar(Integer id) {
        String sql = "DELETE FROM usuario WHERE id = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Error al eliminar usuario: " + e.getMessage(), e);
        }
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
