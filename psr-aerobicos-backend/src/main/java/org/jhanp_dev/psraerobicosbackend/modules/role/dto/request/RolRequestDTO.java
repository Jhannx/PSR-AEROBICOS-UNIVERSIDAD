package org.jhanp_dev.psraerobicosbackend.modules.role.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jhanp_dev.psraerobicosbackend.modules.role.enums.RolNombre;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RolRequestDTO {
    @NotNull(message = "El nombre del rol es obligatorio")
    private RolNombre nombre;
}

