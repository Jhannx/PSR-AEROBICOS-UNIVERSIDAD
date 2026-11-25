package org.jhanp_dev.psraerobicosbackend.modules.aerobics.history.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.history.enums.AccionAuditoria;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistorialAuditoriaRequestDTO {
    @NotBlank(message = "La tabla afectada es obligatoria")
    @Size(max = 50, message = "La tabla afectada no puede superar 50 caracteres")
    private String tablaAfectada;
    @NotNull(message = "El registro afectado es obligatorio")
    @Positive(message = "El registro afectado debe ser válido")
    private Integer registroId;
    @Size(max = 50, message = "El campo no puede superar 50 caracteres")
    private String campo;
    private String valorAnterior;
    private String valorNuevo;
    @NotNull(message = "La acción de auditoría es obligatoria")
    private AccionAuditoria accion;
    @Positive(message = "El usuario debe ser válido")
    private Integer usuarioId;
}

