package org.jhanp_dev.psraerobicosbackend.modules.aerobics.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.payment.enums.MetodoPago;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.payment.enums.TipoMovimiento;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoCuentaResponseDTO {
    private Integer id;
    private Integer personaId;
    private Integer llegadaId;
    private BigDecimal montoRecibido;
    private BigDecimal montoAbonado;
    private MetodoPago metodoPago;
    private TipoMovimiento tipoMovimiento;
    private Boolean vueltoEntregado;
    private LocalDateTime fechaHora;
    private Integer usuarioId;
    private String nota;
}

