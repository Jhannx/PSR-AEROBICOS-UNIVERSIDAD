package org.jhanp_dev.psraerobicosbackend.modules.aerobics.attendance.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsistenciaRequestDTO {
    @NotNull(message = "El usuario que confirma la asistencia es obligatorio")
    @Positive(message = "El usuario debe ser válido")
    private Integer usuarioId;
}

