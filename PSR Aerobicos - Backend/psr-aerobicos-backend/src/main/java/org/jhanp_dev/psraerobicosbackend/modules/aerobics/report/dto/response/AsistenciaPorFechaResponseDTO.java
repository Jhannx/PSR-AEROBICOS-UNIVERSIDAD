package org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.enums.MetodoCheckin;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsistenciaPorFechaResponseDTO {
    private Integer asistenciaId;
    private LocalDate fechaSesion;
    private LocalTime horaClase;
    private String alumno;
    private String numeroDocumento;
    private String telefono;
    private LocalDateTime horaLlegada;
    private MetodoCheckin metodoCheckin;
    private BigDecimal montoCobrado;
    private String confirmadoPor;
}
