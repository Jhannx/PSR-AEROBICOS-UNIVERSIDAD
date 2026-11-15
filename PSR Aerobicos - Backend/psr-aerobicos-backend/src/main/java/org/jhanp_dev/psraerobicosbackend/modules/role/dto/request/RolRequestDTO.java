package org.jhanp_dev.psraerobicosbackend.modules.role.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jhanp_dev.psraerobicosbackend.modules.role.enums.RolNombre;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RolRequestDTO {
    private RolNombre nombre;
}

