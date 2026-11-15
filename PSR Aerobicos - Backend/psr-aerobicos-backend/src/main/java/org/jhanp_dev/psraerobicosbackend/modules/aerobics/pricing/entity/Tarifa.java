package org.jhanp_dev.psraerobicosbackend.modules.aerobics.pricing.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tarifa {
    private Integer id;
    private BigDecimal monto;
    private LocalDateTime actualizadoEn;
    private Integer actualizadoPor;
}

