package org.jhanp_dev.psraerobicosbackend.modules.aerobics.pricing.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TarifaRequestDTO {
    @NotNull(message = "El monto de la tarifa es obligatorio")
    @Positive(message = "El monto de la tarifa debe ser mayor que cero")
    @Digits(integer = 8, fraction = 2, message = "El monto admite hasta 8 enteros y 2 decimales")
    private BigDecimal monto;
    @NotNull(message = "El usuario que actualiza la tarifa es obligatorio")
    @Positive(message = "El usuario debe ser válido")
    private Integer usuarioId;
}

