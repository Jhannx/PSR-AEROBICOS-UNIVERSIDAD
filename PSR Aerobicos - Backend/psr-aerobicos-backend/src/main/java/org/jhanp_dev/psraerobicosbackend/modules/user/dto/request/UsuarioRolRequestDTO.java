package org.jhanp_dev.psraerobicosbackend.modules.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRolRequestDTO {
    @Size(max = 32, message = "El nombre del rol no puede superar 32 caracteres")
    @Pattern(regexp = "(?i)(ADMINISTRADOR|RECEPCIONISTA|PROFESOR|MIEMBRO)", message = "El rol indicado no es válido")
    private String rolNombre;
    @Size(max = 32, message = "El nombre del rol no puede superar 32 caracteres")
    @Pattern(regexp = "(?i)(ADMINISTRADOR|RECEPCIONISTA|PROFESOR|MIEMBRO)", message = "El rol indicado no es válido")
    private String roleName;
}

