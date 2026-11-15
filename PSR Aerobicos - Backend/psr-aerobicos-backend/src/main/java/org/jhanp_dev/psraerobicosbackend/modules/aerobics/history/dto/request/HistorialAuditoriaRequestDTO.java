package org.jhanp_dev.psraerobicosbackend.modules.aerobics.history.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.history.enums.AccionAuditoria;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistorialAuditoriaRequestDTO {
    private String tablaAfectada;
    private Integer registroId;
    private String campo;
    private String valorAnterior;
    private String valorNuevo;
    private AccionAuditoria accion;
    private Integer usuarioId;
}

