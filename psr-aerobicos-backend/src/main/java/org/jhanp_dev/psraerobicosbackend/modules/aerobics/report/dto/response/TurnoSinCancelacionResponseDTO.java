package org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TurnoSinCancelacionResponseDTO {
    private Integer horarioId;
    private Integer diaSemana;
    private String turnoFranja;
    private Long totalSesionesLimpias;
    private Long totalAsistencias;
    private BigDecimal recaudoTotalLimpio;
}

