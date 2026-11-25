package org.jhanp_dev.psraerobicosbackend.modules.aerobics.attendance.dto.response;

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
public class AsistenciaResponseDTO {
    private Integer id;
    private Integer llegadaId;
    private BigDecimal montoCobrado;
    private Integer marcadoPor;
    private LocalDateTime horaMarcado;
}

