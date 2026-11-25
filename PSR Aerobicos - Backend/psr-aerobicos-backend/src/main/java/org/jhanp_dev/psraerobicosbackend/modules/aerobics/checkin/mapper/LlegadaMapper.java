package org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.mapper;

import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.dto.response.LlegadaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.entity.Llegada;
import org.springframework.stereotype.Component;

@Component
public class LlegadaMapper {

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

