package org.jhanp_dev.psraerobicosbackend.modules.user.mapper;

import org.jhanp_dev.psraerobicosbackend.modules.user.dto.request.UsuarioRolRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.user.dto.response.UsuarioRolResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.user.entity.UsuarioRol;
import org.springframework.stereotype.Component;

@Component
public class UsuarioRolMapper {

    public UsuarioRol aEntidad(UsuarioRolRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        return UsuarioRol.builder()
                .usuarioId(dto.getUsuarioId())
                .rolId(dto.getRolId())
                .build();
    }

    public UsuarioRolResponseDTO aResponseDTO(UsuarioRol entidad) {
        if (entidad == null) {
            return null;
        }
        return UsuarioRolResponseDTO.builder()
                .id(entidad.getId())
                .usuarioId(entidad.getUsuarioId())
                .rolId(entidad.getRolId())
                .asignadoEn(entidad.getAsignadoEn())
                .build();
    }
}

