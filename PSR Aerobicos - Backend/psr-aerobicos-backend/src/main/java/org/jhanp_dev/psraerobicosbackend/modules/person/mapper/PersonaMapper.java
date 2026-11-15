package org.jhanp_dev.psraerobicosbackend.modules.person.mapper;

import org.jhanp_dev.psraerobicosbackend.modules.person.dto.request.PersonaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.person.dto.response.PersonaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.person.entity.Persona;
import org.springframework.stereotype.Component;

@Component
public class PersonaMapper {

    public Persona aEntidad(PersonaRequestDTO dto) {
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
                .fuenteRegistro(dto.getFuenteRegistro())
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
}

