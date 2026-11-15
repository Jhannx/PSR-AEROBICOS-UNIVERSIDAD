package org.jhanp_dev.psraerobicosbackend.modules.user.dao.impl;

import lombok.RequiredArgsConstructor;
import org.jhanp_dev.psraerobicosbackend.config.database.ConexionDB;
import org.jhanp_dev.psraerobicosbackend.modules.user.dao.interfaces.IUsuarioRolDAO;
import org.jhanp_dev.psraerobicosbackend.modules.user.dto.request.UsuarioRolRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.user.dto.response.UsuarioRolResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.user.entity.UsuarioRol;
import org.jhanp_dev.psraerobicosbackend.modules.user.mapper.UsuarioRolMapper;
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
@RequiredArgsConstructor
public class UsuarioRolDAO implements IUsuarioRolDAO {

    private final UsuarioRolMapper usuarioRolMapper;

    private static final String BASE_SELECT = """
            SELECT id, usuario_id, rol_id, asignado_en
            FROM usuario_rol
            """;

    @Override
    public List<UsuarioRolResponseDTO> listarTodos() {
        String sql = BASE_SELECT + " ORDER BY id ASC";
        List<UsuarioRolResponseDTO> lista = new ArrayList<>();

        try {
            Connection conn = ConexionDB.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    UsuarioRol entidad = mapearFila(rs);
                    lista.add(usuarioRolMapper.aResponseDTO(entidad));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al listar usuario_rol: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public Optional<UsuarioRolResponseDTO> obtenerPorId(Integer id) {
        String sql = BASE_SELECT + " WHERE id = ?";

        try {
            Connection conn = ConexionDB.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        UsuarioRol entidad = mapearFila(rs);
                        return Optional.of(usuarioRolMapper.aResponseDTO(entidad));
                    }
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al obtener usuario_rol por id: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<UsuarioRolResponseDTO> listarPorUsuarioId(Integer usuarioId) {
        String sql = BASE_SELECT + " WHERE usuario_id = ? ORDER BY id ASC";
        List<UsuarioRolResponseDTO> lista = new ArrayList<>();

        try {
            Connection conn = ConexionDB.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, usuarioId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        UsuarioRol entidad = mapearFila(rs);
                        lista.add(usuarioRolMapper.aResponseDTO(entidad));
                    }
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al listar roles por usuario_id: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public UsuarioRolResponseDTO guardar(UsuarioRolRequestDTO dto) {
        UsuarioRol usuarioRol = usuarioRolMapper.aEntidad(dto);
        String sql = "INSERT INTO usuario_rol (usuario_id, rol_id) VALUES (?, ?)";

        try {
            Connection conn = ConexionDB.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, usuarioRol.getUsuarioId());
                stmt.setInt(2, usuarioRol.getRolId());
                stmt.executeUpdate();
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        usuarioRol.setId(keys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al guardar usuario_rol: " + e.getMessage(), e);
        }
        return usuarioRolMapper.aResponseDTO(usuarioRol);
    }

    @Override
    public boolean actualizar(Integer id, UsuarioRolRequestDTO dto) {
        UsuarioRol usuarioRol = usuarioRolMapper.aEntidad(dto);
        usuarioRol.setId(id);
        String sql = "UPDATE usuario_rol SET usuario_id = ?, rol_id = ? WHERE id = ?";

        try {
            Connection conn = ConexionDB.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, usuarioRol.getUsuarioId());
                stmt.setInt(2, usuarioRol.getRolId());
                stmt.setInt(3, usuarioRol.getId());
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al actualizar usuario_rol: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean eliminar(Integer id) {
        String sql = "DELETE FROM usuario_rol WHERE id = ?";

        try {
            Connection conn = ConexionDB.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al eliminar usuario_rol: " + e.getMessage(), e);
        }
    }

    private UsuarioRol mapearFila(ResultSet rs) throws SQLException {
        Timestamp asignadoEn = rs.getTimestamp("asignado_en");
        return UsuarioRol.builder()
                .id(rs.getInt("id"))
                .usuarioId(rs.getInt("usuario_id"))
                .rolId(rs.getInt("rol_id"))
                .asignadoEn(asignadoEn != null ? asignadoEn.toLocalDateTime() : null)
                .build();
    }
}
