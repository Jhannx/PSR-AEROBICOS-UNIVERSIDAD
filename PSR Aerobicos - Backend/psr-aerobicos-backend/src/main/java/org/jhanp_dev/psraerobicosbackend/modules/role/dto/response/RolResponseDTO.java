package org.jhanp_dev.psraerobicosbackend.modules.role.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jhanp_dev.psraerobicosbackend.modules.role.enums.RolNombre;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RolResponseDTO {
    private Integer id;
    private RolNombre nombre;
}

