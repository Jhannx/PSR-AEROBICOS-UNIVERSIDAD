package org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.mapper;

import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.dto.request.LlegadaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.dto.response.LlegadaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.entity.Llegada;
import org.springframework.stereotype.Component;

@Component
public class LlegadaMapper {

    public Llegada aEntidad(LlegadaRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        return Llegada.builder()
                .personaId(dto.getPersonaId())
                .metodoCheckin(dto.getMetodoCheckin())
                .build();
    }

    public Llegada aEntidad(LlegadaRequestDTO dto, Integer sesionId, Integer personaId) {
        Llegada llegada = aEntidad(dto);
        if (llegada != null) {
            llegada.setSesionId(sesionId);
            llegada.setPersonaId(personaId != null ? personaId : llegada.getPersonaId());
        }
        return llegada;
    }

    public String obtenerDocumento(LlegadaRequestDTO dto) {
        if (dto == null) return null;
        return dto.getDocumento() != null ? dto.getDocumento() : dto.getDocumentNumber();
    }

    public LlegadaResponseDTO aResponseDTO(Llegada entidad) {
        if (entidad == null) {
            return null;
        }
        return LlegadaResponseDTO.builder()
                .id(entidad.getId())
                .personaId(entidad.getPersonaId())
                .sesionId(entidad.getSesionId())
                .horaLlegada(entidad.getHoraLlegada())
                .metodoCheckin(entidad.getMetodoCheckin())
                .build();
    }
}

