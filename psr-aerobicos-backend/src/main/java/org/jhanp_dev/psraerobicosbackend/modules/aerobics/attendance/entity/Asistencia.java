package org.jhanp_dev.psraerobicosbackend.modules.aerobics.attendance.entity;

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
public class Asistencia {
    private Integer id;
    private Integer llegadaId;
    private BigDecimal montoCobrado;
    private Integer marcadoPor;
    private LocalDateTime horaMarcado;
}

