package org.jhanp_dev.psraerobicosbackend.modules.person.dao.impl;

import org.jhanp_dev.psraerobicosbackend.config.database.ConexionDB;
import org.jhanp_dev.psraerobicosbackend.modules.person.dao.interfaces.IContactoEmergenciaDAO;
import org.jhanp_dev.psraerobicosbackend.modules.person.dto.response.ContactoEmergenciaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.person.entity.ContactoEmergencia;
import org.jhanp_dev.psraerobicosbackend.modules.person.mapper.ContactoEmergenciaMapper;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ContactoEmergenciaDAO implements IContactoEmergenciaDAO {

    private final ContactoEmergenciaMapper contactoEmergenciaMapper;

    public ContactoEmergenciaDAO(ContactoEmergenciaMapper contactoEmergenciaMapper) {
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

        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql);
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

        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
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

        try (Connection conexion = ConexionDB.getInstancia().getConexion();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
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
