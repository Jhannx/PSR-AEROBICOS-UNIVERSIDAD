package org.jhanp_dev.psraerobicosbackend.modules.user.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    private Integer id;
    private String numeroDocumento;
    private String correo;
    private String passwordHash;
    private Integer personaId;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
}

