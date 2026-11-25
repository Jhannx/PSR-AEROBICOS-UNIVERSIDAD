package org.jhanp_dev.psraerobicosbackend.modules.person.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaldoPersonaResponseDTO {
    private Integer personaId;
    private String numeroDocumento;
    private String nombreCompleto;
    private BigDecimal totalAbonado;
    private BigDecimal totalCobrado;
    private BigDecimal saldo;
    private String estadoCuenta;
}
