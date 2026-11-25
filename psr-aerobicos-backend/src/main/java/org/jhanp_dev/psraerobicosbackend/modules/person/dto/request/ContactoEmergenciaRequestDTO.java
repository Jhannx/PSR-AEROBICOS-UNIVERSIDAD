package org.jhanp_dev.psraerobicosbackend.modules.person.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactoEmergenciaRequestDTO {
    @Positive(message = "La persona debe ser válida")
    private Integer personaId;
    @Size(max = 150, message = "El nombre del contacto no puede superar 150 caracteres")
    private String nombre;
    @Size(max = 20, message = "El teléfono no puede superar 20 caracteres")
    @Pattern(regexp = "[0-9]+", message = "El teléfono solo puede contener dígitos")
    private String telefono;
    private String observaciones;
}

