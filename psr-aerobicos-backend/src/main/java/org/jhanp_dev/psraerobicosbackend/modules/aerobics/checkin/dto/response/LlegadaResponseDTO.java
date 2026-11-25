package org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.enums.MetodoCheckin;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LlegadaResponseDTO {
    private Integer id;
    private Integer personaId;
    private Integer sesionId;
    private LocalDateTime horaLlegada;
    private MetodoCheckin metodoCheckin;
}

