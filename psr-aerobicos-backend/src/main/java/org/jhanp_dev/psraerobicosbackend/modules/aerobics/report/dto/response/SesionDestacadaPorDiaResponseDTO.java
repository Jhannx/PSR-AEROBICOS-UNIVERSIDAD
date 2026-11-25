package org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SesionDestacadaPorDiaResponseDTO {
    private Integer sesionId;
    private LocalDate fecha;
    private Integer diaSemana;
    private String franjaHoraria;
    private Long totalAsistenciasSesion;
    private BigDecimal recaudoSesion;
}

