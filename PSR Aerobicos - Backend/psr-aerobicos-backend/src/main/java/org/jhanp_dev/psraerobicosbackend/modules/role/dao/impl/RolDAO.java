package org.jhanp_dev.psraerobicosbackend.modules.role.dao.impl;

import org.jhanp_dev.psraerobicosbackend.config.database.ConexionDB;
import org.jhanp_dev.psraerobicosbackend.modules.role.dao.interfaces.IRolDAO;
import org.jhanp_dev.psraerobicosbackend.modules.role.dto.response.RolResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.role.entity.Rol;
import org.jhanp_dev.psraerobicosbackend.modules.role.enums.RolNombre;
import org.jhanp_dev.psraerobicosbackend.modules.role.mapper.RolMapper;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class RolDAO implements IRolDAO {

    private final RolMapper rolMapper;

    public RolDAO(RolMapper rolMapper) {
        this.rolMapper = rolMapper;
    }

    @Override
    public List<RolResponseDTO> listarTodos() {
        String sql = "SELECT id, nombre FROM rol ORDER BY id ASC";
        List<RolResponseDTO> roles = new ArrayList<>();

        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Rol entidad = mapearFila(rs);
                roles.add(rolMapper.aResponseDTO(entidad));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al listar roles: " + e.getMessage(), e);
        }
        return roles;
    }

    @Override
    public Optional<RolResponseDTO> obtenerPorId(Integer id) {
        String sql = "SELECT id, nombre FROM rol WHERE id = ?";

        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Rol entidad = mapearFila(rs);
                    return Optional.of(rolMapper.aResponseDTO(entidad));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al obtener rol por id: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<RolResponseDTO> obtenerPorNombre(RolNombre nombre) {
        String sql = "SELECT id, nombre FROM rol WHERE nombre = ?";

        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, nombre.name());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Rol entidad = mapearFila(rs);
                    return Optional.of(rolMapper.aResponseDTO(entidad));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al obtener rol por nombre: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    private Rol mapearFila(ResultSet rs) throws SQLException {
        return Rol.builder()
                .id(rs.getInt("id"))
                .nombre(RolNombre.valueOf(rs.getString("nombre")))
                .build();
    }
}
