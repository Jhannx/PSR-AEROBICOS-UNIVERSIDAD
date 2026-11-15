package org.jhanp_dev.psraerobicosbackend.modules.aerobics.attendance.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsistenciaRequestDTO {
    private Integer llegadaId;
    private BigDecimal montoCobrado;
    private Integer marcadoPor;
}

