package org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilaRecepcionResponseDTO {
    private Integer llegadaId;
    private Integer sesionId;
    private Integer personaId;
    private String alumno;
    private String numeroDocumento;
    private LocalDateTime horaLlegada;
    private String metodoCheckin;
    private Integer posicionTurno;
    private Integer asistenciaId;
    private BigDecimal montoCobrado;
    private LocalDateTime horaMarcado;
    private Boolean atendido;
    private BigDecimal saldo;
    private String estadoCuenta;
    private LocalDate fechaSesion;
    private Boolean sesionCancelada;
}
