package org.jhanp_dev.psraerobicosbackend.modules.user.mapper;

import org.jhanp_dev.psraerobicosbackend.modules.user.dto.request.UsuarioRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.user.dto.response.UsuarioResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.user.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public Usuario aEntidad(UsuarioRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        return Usuario.builder()
                .numeroDocumento(dto.getNumeroDocumento())
                .correo(dto.getCorreo())
                .passwordHash(dto.getPasswordHash())
                .personaId(dto.getPersonaId())
                .activo(dto.getActivo())
                .build();
    }

    public UsuarioResponseDTO aResponseDTO(Usuario entidad) {
        if (entidad == null) {
            return null;
        }
        return UsuarioResponseDTO.builder()
                .id(entidad.getId())
                .numeroDocumento(entidad.getNumeroDocumento())
                .correo(entidad.getCorreo())
                .personaId(entidad.getPersonaId())
                .activo(entidad.getActivo())
                .fechaCreacion(entidad.getFechaCreacion())
                .build();
    }
}

