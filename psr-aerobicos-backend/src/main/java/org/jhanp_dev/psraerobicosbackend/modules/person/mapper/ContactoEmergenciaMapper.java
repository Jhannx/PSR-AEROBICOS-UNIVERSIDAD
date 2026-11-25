package org.jhanp_dev.psraerobicosbackend.modules.person.mapper;

import org.jhanp_dev.psraerobicosbackend.modules.person.dto.request.ContactoEmergenciaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.person.dto.response.ContactoEmergenciaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.person.entity.ContactoEmergencia;
import org.springframework.stereotype.Component;

@Component
public class ContactoEmergenciaMapper {

    public ContactoEmergencia aEntidad(ContactoEmergenciaRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        return ContactoEmergencia.builder()
                .personaId(dto.getPersonaId())
                .nombre(dto.getNombre())
                .telefono(dto.getTelefono())
                .observaciones(dto.getObservaciones())
                .build();
    }

    public ContactoEmergenciaResponseDTO aResponseDTO(ContactoEmergencia entidad) {
        if (entidad == null) {
            return null;
        }
        return ContactoEmergenciaResponseDTO.builder()
                .id(entidad.getId())
                .personaId(entidad.getPersonaId())
                .nombre(entidad.getNombre())
                .telefono(entidad.getTelefono())
                .observaciones(entidad.getObservaciones())
                .actualizadoEn(entidad.getActualizadoEn())
                .build();
    }
}

