package org.jhanp_dev.psraerobicosbackend.modules.person.dao.impl;

import lombok.RequiredArgsConstructor;
import org.jhanp_dev.psraerobicosbackend.config.database.ConexionDB;
import org.jhanp_dev.psraerobicosbackend.modules.person.dao.interfaces.IPersonaDAO;
import org.jhanp_dev.psraerobicosbackend.modules.person.dto.request.PersonaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.person.dto.response.PersonaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.person.entity.Persona;
import org.jhanp_dev.psraerobicosbackend.modules.person.enums.FuenteRegistro;
import org.jhanp_dev.psraerobicosbackend.modules.person.enums.TipoDocumento;
import org.jhanp_dev.psraerobicosbackend.modules.person.mapper.PersonaMapper;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.Date;
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
public class PersonaDAO implements IPersonaDAO {

    private final PersonaMapper personaMapper;

    private static final String BASE_SELECT = """
            SELECT id, tipo_documento, numero_documento, nombre, apellidos, telefono,
                   fecha_nacimiento, observaciones_salud, fuente_registro, fecha_registro, activo
            FROM persona
            """;

    @Override
    public List<PersonaResponseDTO> listarTodos() {
        String sql = BASE_SELECT + " ORDER BY id ASC";
        List<PersonaResponseDTO> personas = new ArrayList<>();

        try {
            Connection conn = ConexionDB.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Persona entidad = mapearFila(rs);
                    personas.add(personaMapper.aResponseDTO(entidad));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al listar personas: " + e.getMessage(), e);
        }
        return personas;
    }

    @Override
    public Optional<PersonaResponseDTO> obtenerPorId(Integer id) {
        String sql = BASE_SELECT + " WHERE id = ?";

        try {
            Connection conn = ConexionDB.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Persona entidad = mapearFila(rs);
                        return Optional.of(personaMapper.aResponseDTO(entidad));
                    }
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al obtener persona por id: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<PersonaResponseDTO> obtenerPorNumeroDocumento(String numeroDocumento) {
        String sql = BASE_SELECT + " WHERE numero_documento = ?";

        try {
            Connection conn = ConexionDB.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, numeroDocumento);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Persona entidad = mapearFila(rs);
                        return Optional.of(personaMapper.aResponseDTO(entidad));
                    }
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al obtener persona por documento: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public PersonaResponseDTO guardar(PersonaRequestDTO dto) {
        Persona persona = personaMapper.aEntidad(dto);
        String sql = """
                INSERT INTO persona (
                    tipo_documento, numero_documento, nombre, apellidos, telefono,
                    fecha_nacimiento, observaciones_salud, fuente_registro, activo
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        FuenteRegistro fuente = persona.getFuenteRegistro() != null
                ? persona.getFuenteRegistro()
                : FuenteRegistro.AUTOREGISTRO;
        boolean activo = persona.getActivo() != null ? persona.getActivo() : true;

        try {
            Connection conn = ConexionDB.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, persona.getTipoDocumento().name());
                stmt.setString(2, persona.getNumeroDocumento());
                stmt.setString(3, persona.getNombre());
                stmt.setString(4, persona.getApellidos());
                stmt.setString(5, persona.getTelefono());
                if (persona.getFechaNacimiento() != null) {
                    stmt.setDate(6, Date.valueOf(persona.getFechaNacimiento()));
                } else {
                    stmt.setNull(6, Types.DATE);
                }
                if (persona.getObservacionesSalud() != null) {
                    stmt.setString(7, persona.getObservacionesSalud());
                } else {
                    stmt.setNull(7, Types.VARCHAR);
                }
                stmt.setString(8, fuente.name());
                stmt.setBoolean(9, activo);

                stmt.executeUpdate();
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        persona.setId(keys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al guardar persona: " + e.getMessage(), e);
        }
        persona.setFuenteRegistro(fuente);
        persona.setActivo(activo);
        return personaMapper.aResponseDTO(persona);
    }

    @Override
    public boolean actualizar(Integer id, PersonaRequestDTO dto) {
        Persona persona = personaMapper.aEntidad(dto);
        persona.setId(id);
        String sql = """
                UPDATE persona
                SET tipo_documento = ?,
                    numero_documento = ?,
                    nombre = ?,
                    apellidos = ?,
                    telefono = ?,
                    fecha_nacimiento = ?,
                    observaciones_salud = ?,
                    fuente_registro = ?,
                    activo = ?
                WHERE id = ?
                """;

        try {
            Connection conn = ConexionDB.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, persona.getTipoDocumento().name());
                stmt.setString(2, persona.getNumeroDocumento());
                stmt.setString(3, persona.getNombre());
                stmt.setString(4, persona.getApellidos());
                stmt.setString(5, persona.getTelefono());
                if (persona.getFechaNacimiento() != null) {
                    stmt.setDate(6, Date.valueOf(persona.getFechaNacimiento()));
                } else {
                    stmt.setNull(6, Types.DATE);
                }
                if (persona.getObservacionesSalud() != null) {
                    stmt.setString(7, persona.getObservacionesSalud());
                } else {
                    stmt.setNull(7, Types.VARCHAR);
                }
                stmt.setString(8, persona.getFuenteRegistro().name());
                stmt.setBoolean(9, persona.getActivo());
                stmt.setInt(10, persona.getId());

                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al actualizar persona: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean eliminar(Integer id) {
        String sql = "DELETE FROM persona WHERE id = ?";

        try {
            Connection conn = ConexionDB.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al eliminar persona: " + e.getMessage(), e);
        }
    }

    private Persona mapearFila(ResultSet rs) throws SQLException {
        Date fechaNac = rs.getDate("fecha_nacimiento");
        Timestamp fechaReg = rs.getTimestamp("fecha_registro");

        return Persona.builder()
                .id(rs.getInt("id"))
                .tipoDocumento(TipoDocumento.valueOf(rs.getString("tipo_documento")))
                .numeroDocumento(rs.getString("numero_documento"))
                .nombre(rs.getString("nombre"))
                .apellidos(rs.getString("apellidos"))
                .telefono(rs.getString("telefono"))
                .fechaNacimiento(fechaNac != null ? fechaNac.toLocalDate() : null)
                .observacionesSalud(rs.getString("observaciones_salud"))
                .fuenteRegistro(FuenteRegistro.valueOf(rs.getString("fuente_registro")))
                .fechaRegistro(fechaReg != null ? fechaReg.toLocalDateTime() : null)
                .activo(rs.getBoolean("activo"))
                .build();
    }
}
