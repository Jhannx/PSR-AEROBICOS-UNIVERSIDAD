package org.jhanp_dev.psraerobicosbackend.modules.aerobics.session.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SesionRequestDTO {
    @Positive(message = "El horario debe ser válido")
    private Integer horarioId;
    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;
    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime horaInicio;
    @NotNull(message = "La hora de fin es obligatoria")
    private LocalTime horaFin;
    @Positive(message = "El profesor debe ser válido")
    private Integer profesorUsuarioId;
}

