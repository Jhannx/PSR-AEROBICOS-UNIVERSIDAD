package org.jhanp_dev.psraerobicosbackend.modules.user.dao.impl;

import lombok.RequiredArgsConstructor;
import org.jhanp_dev.psraerobicosbackend.config.database.ConexionDB;
import org.jhanp_dev.psraerobicosbackend.modules.user.dao.interfaces.IUsuarioDAO;
import org.jhanp_dev.psraerobicosbackend.modules.user.dto.request.UsuarioRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.user.dto.response.UsuarioResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.user.entity.Usuario;
import org.jhanp_dev.psraerobicosbackend.modules.user.mapper.UsuarioMapper;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UsuarioDAO implements IUsuarioDAO {

    private final UsuarioMapper usuarioMapper;

    private static final String BASE_SELECT = """
            SELECT id, numero_documento, correo, password_hash, persona_id, activo, fecha_creacion
            FROM usuario
            """;

    @Override
    public List<UsuarioResponseDTO> listarTodos() {
        String sql = BASE_SELECT + " ORDER BY id ASC";
        List<UsuarioResponseDTO> usuarios = new ArrayList<>();

        try {
            Connection conn = ConexionDB.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Usuario entidad = mapearFila(rs);
                    usuarios.add(usuarioMapper.aResponseDTO(entidad));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al listar usuarios: " + e.getMessage(), e);
        }
        return usuarios;
    }

    @Override
    public Optional<UsuarioResponseDTO> obtenerPorId(Integer id) {
        String sql = BASE_SELECT + " WHERE id = ?";

        try {
            Connection conn = ConexionDB.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Usuario entidad = mapearFila(rs);
                        return Optional.of(usuarioMapper.aResponseDTO(entidad));
                    }
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al obtener usuario por id: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<UsuarioResponseDTO> obtenerPorCorreo(String correo) {
        String sql = BASE_SELECT + " WHERE correo = ?";

        try {
            Connection conn = ConexionDB.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, correo);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Usuario entidad = mapearFila(rs);
                        return Optional.of(usuarioMapper.aResponseDTO(entidad));
                    }
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al obtener usuario por correo: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<UsuarioResponseDTO> obtenerPorNumeroDocumento(String numeroDocumento) {
        String sql = BASE_SELECT + " WHERE numero_documento = ?";

        try {
            Connection conn = ConexionDB.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, numeroDocumento);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Usuario entidad = mapearFila(rs);
                        return Optional.of(usuarioMapper.aResponseDTO(entidad));
                    }
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al obtener usuario por documento: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public UsuarioResponseDTO guardar(UsuarioRequestDTO dto) {
        Usuario usuario = usuarioMapper.aEntidad(dto);
        String sql = "INSERT INTO usuario (numero_documento, correo, password_hash, persona_id, activo) VALUES (?, ?, ?, ?, ?)";
        boolean activo = usuario.getActivo() != null ? usuario.getActivo() : true;

        try {
            Connection conn = ConexionDB.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
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
                stmt.setString(3, usuario.getPasswordHash());
                if (usuario.getPersonaId() != null) {
                    stmt.setInt(4, usuario.getPersonaId());
                } else {
                    stmt.setNull(4, Types.INTEGER);
                }
                stmt.setBoolean(5, activo);

                stmt.executeUpdate();
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        usuario.setId(keys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al guardar usuario: " + e.getMessage(), e);
        }
        usuario.setActivo(activo);
        return usuarioMapper.aResponseDTO(usuario);
    }

    @Override
    public boolean actualizar(Integer id, UsuarioRequestDTO dto) {
        Usuario usuario = usuarioMapper.aEntidad(dto);
        usuario.setId(id);
        String sql = """
                UPDATE usuario
                SET numero_documento = ?,
                    correo = ?,
                    password_hash = ?,
                    persona_id = ?,
                    activo = ?
                WHERE id = ?
                """;

        try {
            Connection conn = ConexionDB.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
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
                stmt.setString(3, usuario.getPasswordHash());
                if (usuario.getPersonaId() != null) {
                    stmt.setInt(4, usuario.getPersonaId());
                } else {
                    stmt.setNull(4, Types.INTEGER);
                }
                stmt.setBoolean(5, usuario.getActivo());
                stmt.setInt(6, usuario.getId());

                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al actualizar usuario: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean eliminar(Integer id) {
        String sql = "DELETE FROM usuario WHERE id = ?";

        try {
            Connection conn = ConexionDB.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                return stmt.executeUpdate() > 0;
            }
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
}
