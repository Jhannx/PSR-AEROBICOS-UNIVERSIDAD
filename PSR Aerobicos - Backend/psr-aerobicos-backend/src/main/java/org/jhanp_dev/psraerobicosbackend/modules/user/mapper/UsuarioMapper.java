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
                .numeroDocumento(dto.getDocumento() != null ? dto.getDocumento() : dto.getIdentificador())
                .correo(dto.getCorreo())
                .personaId(dto.getPersonaId())
                .activo(true)
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
                .documento(entidad.getNumeroDocumento())
                .build();
    }

    public UsuarioResponseDTO aResponseDTO(Usuario entidad, String nombreCompleto, java.util.List<String> roles) {
        UsuarioResponseDTO dto = aResponseDTO(entidad);
        if (dto != null) {
            dto.setNombreCompleto(nombreCompleto);
            dto.setRoles(roles);
        }
        return dto;
    }

    public String obtenerIdentificador(org.jhanp_dev.psraerobicosbackend.modules.user.dto.request.UsuarioRequestDTO dto) {
        if (dto == null) return null;
        if (dto.getIdentificador() != null) return dto.getIdentificador();
        return dto.getDocumento();
    }
}

