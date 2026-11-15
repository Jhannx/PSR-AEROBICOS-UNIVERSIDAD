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
public class UsuarioRol {
    private Integer id;
    private Integer usuarioId;
    private Integer rolId;
    private LocalDateTime asignadoEn;
}

