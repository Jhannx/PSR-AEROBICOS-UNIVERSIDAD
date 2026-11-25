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
public class MetodoPagoAltoRecaudoResponseDTO {
    private MetodoPago metodoPago;
    private Long totalOperaciones;
    private BigDecimal totalAcumulado;
    private BigDecimal promedioPorTransaccion;
}

