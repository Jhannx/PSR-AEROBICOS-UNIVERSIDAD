package org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.payment.enums.MetodoPago;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetodoPagoMasUtilizadoResponseDTO {
    private MetodoPago metodoPago;
    private Long totalPagos;
    private BigDecimal totalRecaudado;
    private BigDecimal pagoMayor;
}

