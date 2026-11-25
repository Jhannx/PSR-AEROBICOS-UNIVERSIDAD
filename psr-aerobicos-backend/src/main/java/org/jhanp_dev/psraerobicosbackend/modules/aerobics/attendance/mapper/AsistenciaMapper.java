package org.jhanp_dev.psraerobicosbackend.modules.aerobics.attendance.mapper;

import org.jhanp_dev.psraerobicosbackend.modules.aerobics.attendance.dto.request.AsistenciaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.attendance.dto.response.AsistenciaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.attendance.entity.Asistencia;
import org.springframework.stereotype.Component;

@Component
public class AsistenciaMapper {

    public Asistencia aEntidad(AsistenciaRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        return Asistencia.builder()
                .marcadoPor(dto.getUsuarioId())
                .build();
    }

    public Asistencia aEntidad(AsistenciaRequestDTO dto, Integer llegadaId, java.math.BigDecimal montoCobrado) {
        Asistencia asistencia = aEntidad(dto);
        if (asistencia != null) {
            asistencia.setLlegadaId(llegadaId);
            asistencia.setMontoCobrado(montoCobrado);
        }
        return asistencia;
    }

    public AsistenciaResponseDTO aResponseDTO(Asistencia entidad) {
        if (entidad == null) {
            return null;
        }
        return AsistenciaResponseDTO.builder()
                .id(entidad.getId())
                .llegadaId(entidad.getLlegadaId())
                .montoCobrado(entidad.getMontoCobrado())
                .marcadoPor(entidad.getMarcadoPor())
                .horaMarcado(entidad.getHoraMarcado())
                .build();
    }
}

