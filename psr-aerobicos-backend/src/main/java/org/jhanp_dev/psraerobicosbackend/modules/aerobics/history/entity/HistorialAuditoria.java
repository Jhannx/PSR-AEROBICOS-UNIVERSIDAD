package org.jhanp_dev.psraerobicosbackend.modules.aerobics.history.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.history.enums.AccionAuditoria;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistorialAuditoria {
    private Integer id;
    private String tablaAfectada;
    private Integer registroId;
    private String campo;
    private String valorAnterior;
    private String valorNuevo;
    private AccionAuditoria accion;
    private Integer usuarioId;
    private LocalDateTime fechaHora;
}

