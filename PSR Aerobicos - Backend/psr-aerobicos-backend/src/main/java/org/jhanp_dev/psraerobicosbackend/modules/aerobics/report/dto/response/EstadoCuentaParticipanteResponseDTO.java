package org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response;

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
public class EstadoCuentaParticipanteResponseDTO {
    private LocalDateTime fechaHora;
    private String numeroDocumento;
    private String alumno;
    private TipoMovimiento tipoMovimiento;
    private BigDecimal montoRecibido;
    private BigDecimal montoAbonado;
    private MetodoPago metodoPago;
    private String registradoPor;
}

