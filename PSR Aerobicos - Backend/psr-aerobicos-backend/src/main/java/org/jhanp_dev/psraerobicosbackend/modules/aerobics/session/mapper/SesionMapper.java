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
                .horaInicio(dto.getFecha() != null && dto.getHoraInicio() != null ? java.time.LocalDateTime.of(dto.getFecha(), dto.getHoraInicio()) : null)
                .horaFin(dto.getFecha() != null && dto.getHoraFin() != null ? java.time.LocalDateTime.of(dto.getFecha(), dto.getHoraFin()) : null)
                .profesorUsuarioId(dto.getProfesorUsuarioId())
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

    public SesionResponseDTO aResponseDTO(Sesion entidad, String instructor) {
        SesionResponseDTO dto = aResponseDTO(entidad);
        if (dto != null) dto.setInstructor(instructor);
        return dto;
    }
}

