package org.jhanp_dev.psraerobicosbackend.modules.aerobics.session.mapper;

import org.jhanp_dev.psraerobicosbackend.modules.aerobics.session.dto.request.SesionRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.session.dto.response.SesionResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.session.entity.Sesion;
import org.springframework.stereotype.Component;

@Component
public class SesionMapper {

    public Sesion aEntidad(SesionRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        return Sesion.builder()
                .horarioId(dto.getHorarioId())
                .fecha(dto.getFecha())
                .horaInicio(dto.getHoraInicio())
                .horaFin(dto.getHoraFin())
                .profesorUsuarioId(dto.getProfesorUsuarioId())
                .cancelada(dto.getCancelada())
                .totalAsistencias(dto.getTotalAsistencias())
                .totalRecaudo(dto.getTotalRecaudo())
                .build();
    }

    public SesionResponseDTO aResponseDTO(Sesion entidad) {
        if (entidad == null) {
            return null;
        }
        return SesionResponseDTO.builder()
                .id(entidad.getId())
                .horarioId(entidad.getHorarioId())
                .fecha(entidad.getFecha())
                .horaInicio(entidad.getHoraInicio())
                .horaFin(entidad.getHoraFin())
                .profesorUsuarioId(entidad.getProfesorUsuarioId())
                .cancelada(entidad.getCancelada())
                .totalAsistencias(entidad.getTotalAsistencias())
                .totalRecaudo(entidad.getTotalRecaudo())
                .build();
    }
}

