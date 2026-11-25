package org.jhanp_dev.psraerobicosbackend.modules.aerobics.schedule.mapper;

import org.jhanp_dev.psraerobicosbackend.modules.aerobics.schedule.dto.request.ActualizarHorarioRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.schedule.dto.request.CrearHorarioRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.schedule.dto.response.HorarioResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.schedule.entity.Horario;
import org.springframework.stereotype.Component;

@Component
public class HorarioMapper {

    public Horario aEntidad(CrearHorarioRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        return Horario.builder()
                .diaSemana(dto.getDiaSemana())
                .horaInicio(dto.getHoraInicio())
                .horaFin(dto.getHoraFin())
                .activo(dto.getActivo())
                .build();
    }

    public Horario aEntidad(ActualizarHorarioRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        return Horario.builder()
                .diaSemana(dto.getDiaSemana())
                .horaInicio(dto.getHoraInicio())
                .horaFin(dto.getHoraFin())
                .activo(dto.getActivo())
                .build();
    }

    public HorarioResponseDTO aResponseDTO(Horario entidad) {
        if (entidad == null) {
            return null;
        }
        return HorarioResponseDTO.builder()
                .id(entidad.getId())
                .diaSemana(entidad.getDiaSemana())
                .horaInicio(entidad.getHoraInicio())
                .horaFin(entidad.getHoraFin())
                .activo(entidad.getActivo())
                .build();
    }
}

