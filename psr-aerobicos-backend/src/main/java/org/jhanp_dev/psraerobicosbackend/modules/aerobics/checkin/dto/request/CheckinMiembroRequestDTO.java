package org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.enums.MetodoCheckin;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckinMiembroRequestDTO {
    @NotNull(message = "La persona del check-in es obligatoria")
    @Positive(message = "La persona debe ser válida")
    private Integer personaId;
    @Size(max = 20, message = "El documento no puede superar 20 caracteres")
    @Pattern(regexp = "[0-9]+", message = "El documento solo puede contener dígitos")
    private String documento;
    @Size(max = 20, message = "El documento no puede superar 20 caracteres")
    @Pattern(regexp = "[0-9]+", message = "El documento solo puede contener dígitos")
    private String documentNumber;
    private MetodoCheckin metodoCheckin;
}
