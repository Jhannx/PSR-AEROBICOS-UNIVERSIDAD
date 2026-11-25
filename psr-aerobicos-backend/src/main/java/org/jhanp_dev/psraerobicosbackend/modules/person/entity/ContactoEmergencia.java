package org.jhanp_dev.psraerobicosbackend.modules.person.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactoEmergencia {
    private Integer id;
    private Integer personaId;
    private String nombre;
    private String telefono;
    private String observaciones;
    private LocalDateTime actualizadoEn;
}

