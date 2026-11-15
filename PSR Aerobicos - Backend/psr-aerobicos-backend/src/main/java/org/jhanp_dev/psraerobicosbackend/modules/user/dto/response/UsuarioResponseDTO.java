package org.jhanp_dev.psraerobicosbackend.modules.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDTO {
    private Integer id;
    private String numeroDocumento;
    private String correo;
    private Integer personaId;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
}

