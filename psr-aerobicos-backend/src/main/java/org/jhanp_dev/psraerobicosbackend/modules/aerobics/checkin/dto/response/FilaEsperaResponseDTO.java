package org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilaEsperaResponseDTO {
    private Integer llegadaId;
    private Integer sesionId;
    private LocalDate fechaSesion;
    private String alumnoVisible;
    private LocalDateTime horaLlegada;
    private Integer turno;
    private String estadoTurno;
}
