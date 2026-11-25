package org.jhanp_dev.psraerobicosbackend.modules.person.mapper;

import org.jhanp_dev.psraerobicosbackend.modules.person.dto.request.CrearPersonaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.person.dto.response.PersonaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.person.dto.response.ContactoEmergenciaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.person.entity.Persona;
import org.springframework.stereotype.Component;

@Component
public class PersonaMapper {

    public Persona aEntidad(CrearPersonaRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        return Persona.builder()
                .tipoDocumento(dto.getTipoDocumento())
                .numeroDocumento(dto.getNumeroDocumento())
                .nombre(dto.getNombre())
                .apellidos(dto.getApellidos())
                .telefono(dto.getTelefono())
                .fechaNacimiento(dto.getFechaNacimiento())
                .observacionesSalud(dto.getObservacionesSalud())
                .activo(dto.getActivo())
                .build();
    }

    public PersonaResponseDTO aResponseDTO(Persona entidad) {
        if (entidad == null) {
            return null;
        }
        return PersonaResponseDTO.builder()
                .id(entidad.getId())
                .tipoDocumento(entidad.getTipoDocumento())
                .numeroDocumento(entidad.getNumeroDocumento())
                .nombre(entidad.getNombre())
                .apellidos(entidad.getApellidos())
                .telefono(entidad.getTelefono())
                .fechaNacimiento(entidad.getFechaNacimiento())
                .observacionesSalud(entidad.getObservacionesSalud())
                .fuenteRegistro(entidad.getFuenteRegistro())
                .fechaRegistro(entidad.getFechaRegistro())
                .activo(entidad.getActivo())
                .build();
    }

    public PersonaResponseDTO aResponseDTO(Persona persona, ContactoEmergenciaResponseDTO contacto,
                                            java.math.BigDecimal saldo, String estadoCuenta) {
        PersonaResponseDTO dto = aResponseDTO(persona);
        if (dto == null) return null;
        if (contacto != null) {
            dto.setContactoNombre(contacto.getNombre());
            dto.setContactoTelefono(contacto.getTelefono());
            dto.setContactoObservaciones(contacto.getObservaciones());
        }
        dto.setSaldo(saldo);
        dto.setEstadoCuenta(estadoCuenta);
        return dto;
    }

    public PersonaResponseDTO aResponseDTO(Persona persona, String contactoNombre, String contactoTelefono,
                                            String contactoObservaciones, java.math.BigDecimal saldo,
                                            String estadoCuenta) {
        PersonaResponseDTO dto = aResponseDTO(persona);
        if (dto != null) {
            dto.setContactoNombre(contactoNombre);
            dto.setContactoTelefono(contactoTelefono);
            dto.setContactoObservaciones(contactoObservaciones);
            dto.setSaldo(saldo);
            dto.setEstadoCuenta(estadoCuenta);
        }
        return dto;
    }
}

