package org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.payment.enums.MetodoPago;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagoAnticipadoResponseDTO {
    private Integer movimientoId;
    private String numeroDocumento;
    private String alumno;
    private BigDecimal montoAbonado;
    private MetodoPago metodoPago;
    private LocalDateTime fechaHora;
}

