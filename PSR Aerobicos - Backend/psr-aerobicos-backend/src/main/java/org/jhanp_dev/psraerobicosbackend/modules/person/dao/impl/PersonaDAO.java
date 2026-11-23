package org.jhanp_dev.psraerobicosbackend.modules.person.dao.impl;

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
import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class PersonaDAO implements IPersonaDAO {

    private final Connection conexion;
    private final PersonaMapper personaMapper;

    public PersonaDAO(PersonaMapper personaMapper) throws SQLException {
        this.conexion = ConexionDB.getConexion();
        this.personaMapper = personaMapper;
    }

    private static final String BASE_SELECT = """
            SELECT p.id, p.tipo_documento, p.numero_documento, p.nombre, p.apellidos, p.telefono,
                   p.fecha_nacimiento, p.observaciones_salud, p.fuente_registro, p.fecha_registro, p.activo,
                   ce.nombre AS contacto_nombre, ce.telefono AS contacto_telefono,
                   ce.observaciones AS contacto_observaciones, sp.saldo, sp.estado_cuenta
            FROM persona p
            LEFT JOIN contacto_emergencia ce ON ce.persona_id = p.id
            LEFT JOIN vista_saldo_persona sp ON sp.persona_id = p.id
            """;

    @Override
    public List<PersonaResponseDTO> listarTodos() {
        String sql = BASE_SELECT + " ORDER BY id ASC";
        List<PersonaResponseDTO> personas = new ArrayList<>();

        try (PreparedStatement stmt = conexion.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Persona entidad = mapearFila(rs);
                personas.add(mapearResponse(rs, entidad));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al listar personas: " + e.getMessage(), e);
        }
        return personas;
    }

    @Override
    public List<PersonaResponseDTO> buscar(String termino) {
        String sql = BASE_SELECT + " WHERE p.nombre LIKE ? OR p.apellidos LIKE ? OR p.numero_documento LIKE ? ORDER BY p.id ASC";
        String patron = "%" + termino + "%";
        List<PersonaResponseDTO> personas = new ArrayList<>();
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, patron);
            stmt.setString(2, patron);
            stmt.setString(3, patron);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) personas.add(mapearResponse(rs, mapearFila(rs)));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al buscar personas: " + e.getMessage(), e);
        }
        return personas;
    }

    @Override
    public Optional<org.jhanp_dev.psraerobicosbackend.modules.person.dto.response.SaldoPersonaResponseDTO> obtenerSaldo(Integer personaId) {
        String sql = "SELECT persona_id, total_abonado, total_cobrado, saldo, estado_cuenta FROM vista_saldo_persona WHERE persona_id = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, personaId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(org.jhanp_dev.psraerobicosbackend.modules.person.dto.response.SaldoPersonaResponseDTO.builder()
                        .personaId(rs.getInt("persona_id"))
                        .totalAbonado(rs.getBigDecimal("total_abonado"))
                        .totalCobrado(rs.getBigDecimal("total_cobrado"))
                        .saldo(rs.getBigDecimal("saldo"))
                        .estadoCuenta(rs.getString("estado_cuenta"))
                        .build());
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al consultar el saldo de persona: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<PersonaResponseDTO> obtenerPorId(Integer id) {
        String sql = BASE_SELECT + " WHERE id = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Persona entidad = mapearFila(rs);
                    return Optional.of(mapearResponse(rs, entidad));
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

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, numeroDocumento);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Persona entidad = mapearFila(rs);
                    return Optional.of(mapearResponse(rs, entidad));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al obtener persona por documento: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public PersonaResponseDTO crearConContacto(PersonaRequestDTO dto, FuenteRegistro fuente, String passwordHash) {
        Persona persona = personaMapper.aEntidad(dto);
        String sql = "{CALL sp_crear_persona_con_contacto(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        try (CallableStatement stmt = conexion.prepareCall(sql)) {
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
            stmt.setString(9, dto.getContactoNombre());
            stmt.setString(10, dto.getContactoTelefono());
            stmt.setString(11, dto.getContactoObservaciones());
            stmt.setString(12, dto.getCorreo());
            stmt.setString(13, passwordHash);
            stmt.registerOutParameter(14, Types.INTEGER);
            stmt.registerOutParameter(15, Types.INTEGER);
            stmt.execute();
            persona.setId(stmt.getInt(14));
        } catch (SQLException e) {
            throw new IllegalStateException("Error al crear persona, contacto y usuario: " + e.getMessage(), e);
        }

        return obtenerPorId(persona.getId())
                .orElseThrow(() -> new IllegalStateException("No se encontro la persona creada"));
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
                    activo = ?
                WHERE id = ?
                """;

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
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
            stmt.setBoolean(8, persona.getActivo() != null ? persona.getActivo() : true);
            stmt.setInt(9, persona.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Error al actualizar persona: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean eliminar(Integer id) {
        String sql = "DELETE FROM persona WHERE id = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
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

    private PersonaResponseDTO mapearResponse(ResultSet rs, Persona persona) throws SQLException {
        return personaMapper.aResponseDTO(persona,
                rs.getString("contacto_nombre"),
                rs.getString("contacto_telefono"),
                rs.getString("contacto_observaciones"),
                rs.getBigDecimal("saldo"),
                rs.getString("estado_cuenta"));
    }
}
