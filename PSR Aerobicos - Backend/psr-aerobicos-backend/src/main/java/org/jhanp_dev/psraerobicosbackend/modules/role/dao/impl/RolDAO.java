package org.jhanp_dev.psraerobicosbackend.modules.role.dao.impl;

import org.jhanp_dev.psraerobicosbackend.config.database.ConexionDB;
import org.jhanp_dev.psraerobicosbackend.modules.role.dao.interfaces.IRolDAO;
import org.jhanp_dev.psraerobicosbackend.modules.role.dto.request.RolRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.role.dto.response.RolResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.role.entity.Rol;
import org.jhanp_dev.psraerobicosbackend.modules.role.enums.RolNombre;
import org.jhanp_dev.psraerobicosbackend.modules.role.mapper.RolMapper;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class RolDAO implements IRolDAO {

    private final Connection conexion;
    private final RolMapper rolMapper;

    public RolDAO(RolMapper rolMapper) throws SQLException {
        this.conexion = ConexionDB.getConexion();
        this.rolMapper = rolMapper;
    }

    @Override
    public List<RolResponseDTO> listarTodos() {
        String sql = "SELECT id, nombre FROM rol ORDER BY id ASC";
        List<RolResponseDTO> roles = new ArrayList<>();

        try (PreparedStatement stmt = conexion.prepareStatement(sql);
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

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
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

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
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

    @Override
    public RolResponseDTO guardar(RolRequestDTO dto) {
        Rol rol = rolMapper.aEntidad(dto);
        String sql = "INSERT INTO rol (nombre) VALUES (?)";

        try (PreparedStatement stmt = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, rol.getNombre().name());
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    rol.setId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al guardar rol: " + e.getMessage(), e);
        }
        return rolMapper.aResponseDTO(rol);
    }

    @Override
    public boolean actualizar(Integer id, RolRequestDTO dto) {
        Rol rol = rolMapper.aEntidad(dto);
        rol.setId(id);
        String sql = "UPDATE rol SET nombre = ? WHERE id = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, rol.getNombre().name());
            stmt.setInt(2, rol.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Error al actualizar rol: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean eliminar(Integer id) {
        String sql = "DELETE FROM rol WHERE id = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Error al eliminar rol: " + e.getMessage(), e);
        }
    }

    private Rol mapearFila(ResultSet rs) throws SQLException {
        return Rol.builder()
                .id(rs.getInt("id"))
                .nombre(RolNombre.valueOf(rs.getString("nombre")))
                .build();
    }
}
