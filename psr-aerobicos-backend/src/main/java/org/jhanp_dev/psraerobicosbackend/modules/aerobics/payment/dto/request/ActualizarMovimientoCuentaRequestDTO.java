package org.jhanp_dev.psraerobicosbackend.modules.aerobics.payment.dto.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.payment.enums.MetodoPago;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.payment.enums.TipoMovimiento;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarMovimientoCuentaRequestDTO {
    @NotNull @Positive private Integer personaId;
    @Positive private Integer llegadaId;
    @NotNull @PositiveOrZero @Digits(integer = 8, fraction = 2) private BigDecimal montoRecibido;
    @NotNull @PositiveOrZero @Digits(integer = 8, fraction = 2) private BigDecimal montoAbonado;
    @NotNull(message = "El método de pago es obligatorio al actualizar un movimiento")
    private MetodoPago metodoPago;
    @NotNull private TipoMovimiento tipoMovimiento;
    @NotNull private Boolean vueltoEntregado;
    @NotNull @Positive private Integer usuarioId;
    private String nota;
}
