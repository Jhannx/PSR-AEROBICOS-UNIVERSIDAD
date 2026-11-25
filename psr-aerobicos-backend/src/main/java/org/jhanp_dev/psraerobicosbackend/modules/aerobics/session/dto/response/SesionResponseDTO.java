package org.jhanp_dev.psraerobicosbackend.modules.aerobics.session.dto.response;

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
public class SesionResponseDTO {
    private Integer id;
    private Integer horarioId;
    private LocalDate fecha;
    private LocalDateTime horaInicio;
    private LocalDateTime horaFin;
    private Integer profesorUsuarioId;
    private Boolean cancelada;
    private Integer totalAsistencias;
    private BigDecimal totalRecaudo;
    private String instructor;
}

