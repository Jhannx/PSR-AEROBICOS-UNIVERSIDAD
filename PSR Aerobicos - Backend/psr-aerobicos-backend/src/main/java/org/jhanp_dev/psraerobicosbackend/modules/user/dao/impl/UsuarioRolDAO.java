package org.jhanp_dev.psraerobicosbackend.modules.user.dao.impl;

import org.jhanp_dev.psraerobicosbackend.config.database.ConexionDB;
import org.jhanp_dev.psraerobicosbackend.modules.user.dao.interfaces.IUsuarioRolDAO;
import org.jhanp_dev.psraerobicosbackend.modules.user.dto.response.UsuarioRolResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.user.entity.UsuarioRol;
import org.jhanp_dev.psraerobicosbackend.modules.user.mapper.UsuarioRolMapper;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class UsuarioRolDAO implements IUsuarioRolDAO {

    private final UsuarioRolMapper usuarioRolMapper;

    public UsuarioRolDAO(UsuarioRolMapper usuarioRolMapper) {
        this.usuarioRolMapper = usuarioRolMapper;
    }

    private static final String BASE_SELECT = """
            SELECT id, usuario_id, rol_id, asignado_en
            FROM usuario_rol
            """;

    @Override
    public List<UsuarioRolResponseDTO> listarTodos() {
        String sql = BASE_SELECT + " ORDER BY id ASC";
        List<UsuarioRolResponseDTO> lista = new ArrayList<>();

        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                UsuarioRol entidad = mapearFila(rs);
                lista.add(usuarioRolMapper.aResponseDTO(entidad));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al listar usuario_rol: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public Optional<UsuarioRolResponseDTO> obtenerPorId(Integer id) {
        String sql = BASE_SELECT + " WHERE id = ?";

        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    UsuarioRol entidad = mapearFila(rs);
                    return Optional.of(usuarioRolMapper.aResponseDTO(entidad));
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

        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    UsuarioRol entidad = mapearFila(rs);
                    lista.add(usuarioRolMapper.aResponseDTO(entidad));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al listar roles por usuario_id: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public boolean existeAsignacion(Integer usuarioId, String rolNombre) {
        String sql = "SELECT 1 FROM usuario_rol ur JOIN rol r ON r.id = ur.rol_id "
                + "WHERE ur.usuario_id = ? AND r.nombre = ? LIMIT 1";
        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            stmt.setString(2, rolNombre);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al validar la asignación del rol", e);
        }
    }

    @Override
    public void asignar(Integer usuarioId, String rolNombre) {
        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             CallableStatement stmt = conexion.prepareCall("{CALL sp_asignar_rol(?, ?)}")) {
            stmt.setInt(1, usuarioId);
            stmt.setString(2, rolNombre);
            stmt.execute();
        } catch (SQLException e) {
            throw new IllegalStateException("Error al asignar rol: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean retirar(Integer usuarioId, String rolNombre) {
        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             CallableStatement stmt = conexion.prepareCall("{CALL sp_retirar_rol(?, ?, ?)}")) {
            stmt.setInt(1, usuarioId);
            stmt.setString(2, rolNombre);
            stmt.registerOutParameter(3, Types.BOOLEAN);
            stmt.execute();
            return stmt.getBoolean(3);
        } catch (SQLException e) {
            throw new IllegalStateException("Error al retirar rol", e);
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
