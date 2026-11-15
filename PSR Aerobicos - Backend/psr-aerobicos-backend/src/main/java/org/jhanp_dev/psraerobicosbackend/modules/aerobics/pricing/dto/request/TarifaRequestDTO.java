package org.jhanp_dev.psraerobicosbackend.modules.aerobics.pricing.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TarifaRequestDTO {
    private BigDecimal monto;
    private Integer actualizadoPor;
}

