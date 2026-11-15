package org.jhanp_dev.psraerobicosbackend.modules.person.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactoEmergenciaRequestDTO {
    private Integer personaId;
    private String nombre;
    private String telefono;
    private String observaciones;
}

