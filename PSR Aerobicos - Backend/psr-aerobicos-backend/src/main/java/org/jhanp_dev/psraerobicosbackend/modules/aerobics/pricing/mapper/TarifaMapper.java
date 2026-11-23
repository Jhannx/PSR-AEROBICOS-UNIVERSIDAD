package org.jhanp_dev.psraerobicosbackend.modules.aerobics.pricing.mapper;

import org.jhanp_dev.psraerobicosbackend.modules.aerobics.pricing.dto.request.TarifaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.pricing.dto.response.TarifaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.pricing.entity.Tarifa;
import org.springframework.stereotype.Component;

@Component
public class TarifaMapper {

    public Tarifa aEntidad(TarifaRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        return Tarifa.builder()
                .monto(dto.getMonto())
                .actualizadoPor(dto.getUsuarioId())
                .build();
    }

    public TarifaResponseDTO aResponseDTO(Tarifa entidad) {
        if (entidad == null) {
            return null;
        }
        return TarifaResponseDTO.builder()
                .id(entidad.getId())
                .monto(entidad.getMonto())
                .actualizadoEn(entidad.getActualizadoEn())
                .actualizadoPor(entidad.getActualizadoPor())
                .build();
    }
}

