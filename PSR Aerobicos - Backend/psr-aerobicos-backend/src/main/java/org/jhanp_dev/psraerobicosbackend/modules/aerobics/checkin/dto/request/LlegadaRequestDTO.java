package org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.enums.MetodoCheckin;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LlegadaRequestDTO {
    private Integer personaId;
    private Integer sesionId;
    private MetodoCheckin metodoCheckin;
}

