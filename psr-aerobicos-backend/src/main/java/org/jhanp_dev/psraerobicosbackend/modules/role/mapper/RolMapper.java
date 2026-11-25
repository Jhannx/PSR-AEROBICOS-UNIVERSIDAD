package org.jhanp_dev.psraerobicosbackend.modules.role.mapper;

import org.jhanp_dev.psraerobicosbackend.modules.role.dto.request.RolRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.role.dto.response.RolResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.role.entity.Rol;
import org.springframework.stereotype.Component;

@Component
public class RolMapper {

    public Rol aEntidad(RolRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        return Rol.builder()
                .nombre(dto.getNombre())
                .build();
    }

    public RolResponseDTO aResponseDTO(Rol entidad) {
        if (entidad == null) {
            return null;
        }
        return RolResponseDTO.builder()
                .id(entidad.getId())
                .nombre(entidad.getNombre())
                .build();
    }
}

