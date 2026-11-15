package org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.entity;

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
public class Llegada {
    private Integer id;
    private Integer personaId;
    private Integer sesionId;
    private LocalDateTime horaLlegada;
    private MetodoCheckin metodoCheckin;
}

