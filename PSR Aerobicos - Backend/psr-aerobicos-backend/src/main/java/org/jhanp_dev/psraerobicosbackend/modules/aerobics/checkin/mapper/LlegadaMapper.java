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
                .sesionId(dto.getSesionId())
                .metodoCheckin(dto.getMetodoCheckin())
                .build();
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

