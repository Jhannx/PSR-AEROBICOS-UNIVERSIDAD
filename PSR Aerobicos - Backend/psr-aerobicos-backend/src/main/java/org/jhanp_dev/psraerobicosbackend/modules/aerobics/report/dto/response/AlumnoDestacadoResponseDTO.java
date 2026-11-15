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
public class AlumnoDestacadoResponseDTO {
    private Integer alumnoId;
    private String numeroDocumento;
    private String nombreCompleto;
    private Long totalAsistencias;
    private BigDecimal montoTotalAcumulado;
}

