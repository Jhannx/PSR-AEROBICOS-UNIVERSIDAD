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
public class AuditoriaMetodoPagoResponseDTO {
    private MetodoPago metodoPago;
    private Long totalOperaciones;
    private BigDecimal montoTotal;
    private BigDecimal promedioPorOperacion;
}

