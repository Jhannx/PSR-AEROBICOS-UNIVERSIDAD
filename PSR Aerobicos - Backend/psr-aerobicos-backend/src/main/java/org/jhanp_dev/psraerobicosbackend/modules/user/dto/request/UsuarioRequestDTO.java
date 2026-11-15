package org.jhanp_dev.psraerobicosbackend.modules.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRequestDTO {
    private String numeroDocumento;
    private String correo;
    private String passwordHash;
    private Integer personaId;
    private Boolean activo;
}

