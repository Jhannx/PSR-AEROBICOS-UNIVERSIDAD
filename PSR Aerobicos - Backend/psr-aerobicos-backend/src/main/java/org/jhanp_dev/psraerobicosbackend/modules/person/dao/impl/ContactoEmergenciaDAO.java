package org.jhanp_dev.psraerobicosbackend.modules.person.dao.impl;

import org.jhanp_dev.psraerobicosbackend.config.database.ConexionDB;
import org.jhanp_dev.psraerobicosbackend.modules.person.dao.interfaces.IContactoEmergenciaDAO;
import org.jhanp_dev.psraerobicosbackend.modules.person.dto.request.ContactoEmergenciaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.person.dto.response.ContactoEmergenciaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.person.entity.ContactoEmergencia;
import org.jhanp_dev.psraerobicosbackend.modules.person.mapper.ContactoEmergenciaMapper;
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
public class ContactoEmergenciaDAO implements IContactoEmergenciaDAO {

    private final Connection conexion;
    private final ContactoEmergenciaMapper contactoEmergenciaMapper;

    public ContactoEmergenciaDAO(ContactoEmergenciaMapper contactoEmergenciaMapper) throws SQLException {
        this.conexion = ConexionDB.getConexion();
        this.contactoEmergenciaMapper = contactoEmergenciaMapper;
    }

    private static final String BASE_SELECT = """
            SELECT id, persona_id, nombre, telefono, observaciones, actualizado_en
            FROM contacto_emergencia
            """;

    @Override
    public List<ContactoEmergenciaResponseDTO> listarTodos() {
        String sql = BASE_SELECT + " ORDER BY id ASC";
        List<ContactoEmergenciaResponseDTO> contactos = new ArrayList<>();

        try (PreparedStatement stmt = conexion.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                ContactoEmergencia entidad = mapearFila(rs);
                contactos.add(contactoEmergenciaMapper.aResponseDTO(entidad));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al listar contactos de emergencia: " + e.getMessage(), e);
        }
        return contactos;
    }

    @Override
    public Optional<ContactoEmergenciaResponseDTO> obtenerPorId(Integer id) {
        String sql = BASE_SELECT + " WHERE id = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ContactoEmergencia entidad = mapearFila(rs);
                    return Optional.of(contactoEmergenciaMapper.aResponseDTO(entidad));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al obtener contacto de emergencia por id: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<ContactoEmergenciaResponseDTO> obtenerPorPersonaId(Integer personaId) {
        String sql = BASE_SELECT + " WHERE persona_id = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, personaId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ContactoEmergencia entidad = mapearFila(rs);
                    return Optional.of(contactoEmergenciaMapper.aResponseDTO(entidad));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al obtener contacto de emergencia por persona_id: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public ContactoEmergenciaResponseDTO guardar(ContactoEmergenciaRequestDTO dto) {
        ContactoEmergencia contacto = contactoEmergenciaMapper.aEntidad(dto);
        String sql = "INSERT INTO contacto_emergencia (persona_id, nombre, telefono, observaciones) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, contacto.getPersonaId());
            if (contacto.getNombre() != null) {
                stmt.setString(2, contacto.getNombre());
            } else {
                stmt.setNull(2, Types.VARCHAR);
            }
            if (contacto.getTelefono() != null) {
                stmt.setString(3, contacto.getTelefono());
            } else {
                stmt.setNull(3, Types.VARCHAR);
            }
            if (contacto.getObservaciones() != null) {
                stmt.setString(4, contacto.getObservaciones());
            } else {
                stmt.setNull(4, Types.VARCHAR);
            }

            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    contacto.setId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al guardar contacto de emergencia: " + e.getMessage(), e);
        }
        return contactoEmergenciaMapper.aResponseDTO(contacto);
    }

    @Override
    public boolean actualizar(Integer id, ContactoEmergenciaRequestDTO dto) {
        ContactoEmergencia contacto = contactoEmergenciaMapper.aEntidad(dto);
        contacto.setId(id);
        String sql = "UPDATE contacto_emergencia SET nombre = ?, telefono = ?, observaciones = ? WHERE id = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            if (contacto.getNombre() != null) {
                stmt.setString(1, contacto.getNombre());
            } else {
                stmt.setNull(1, Types.VARCHAR);
            }
            if (contacto.getTelefono() != null) {
                stmt.setString(2, contacto.getTelefono());
            } else {
                stmt.setNull(2, Types.VARCHAR);
            }
            if (contacto.getObservaciones() != null) {
                stmt.setString(3, contacto.getObservaciones());
            } else {
                stmt.setNull(3, Types.VARCHAR);
            }
            stmt.setInt(4, contacto.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Error al actualizar contacto de emergencia: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean eliminar(Integer id) {
        String sql = "DELETE FROM contacto_emergencia WHERE id = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Error al eliminar contacto de emergencia: " + e.getMessage(), e);
        }
    }

    private ContactoEmergencia mapearFila(ResultSet rs) throws SQLException {
        Timestamp actualizado = rs.getTimestamp("actualizado_en");
        return ContactoEmergencia.builder()
                .id(rs.getInt("id"))
                .personaId(rs.getInt("persona_id"))
                .nombre(rs.getString("nombre"))
                .telefono(rs.getString("telefono"))
                .observaciones(rs.getString("observaciones"))
                .actualizadoEn(actualizado != null ? actualizado.toLocalDateTime() : null)
                .build();
    }
}
