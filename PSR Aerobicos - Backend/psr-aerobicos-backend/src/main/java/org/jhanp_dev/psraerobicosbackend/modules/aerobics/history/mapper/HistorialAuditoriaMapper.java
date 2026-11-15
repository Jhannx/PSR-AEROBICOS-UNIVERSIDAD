package org.jhanp_dev.psraerobicosbackend.modules.aerobics.history.mapper;

import org.jhanp_dev.psraerobicosbackend.modules.aerobics.history.dto.request.HistorialAuditoriaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.history.dto.response.HistorialAuditoriaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.history.entity.HistorialAuditoria;
import org.springframework.stereotype.Component;

@Component
public class HistorialAuditoriaMapper {

    public HistorialAuditoria aEntidad(HistorialAuditoriaRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        return HistorialAuditoria.builder()
                .tablaAfectada(dto.getTablaAfectada())
                .registroId(dto.getRegistroId())
                .campo(dto.getCampo())
                .valorAnterior(dto.getValorAnterior())
                .valorNuevo(dto.getValorNuevo())
                .accion(dto.getAccion())
                .usuarioId(dto.getUsuarioId())
                .build();
    }

    public HistorialAuditoriaResponseDTO aResponseDTO(HistorialAuditoria entidad) {
        if (entidad == null) {
            return null;
        }
        return HistorialAuditoriaResponseDTO.builder()
                .id(entidad.getId())
                .tablaAfectada(entidad.getTablaAfectada())
                .registroId(entidad.getRegistroId())
                .campo(entidad.getCampo())
                .valorAnterior(entidad.getValorAnterior())
                .valorNuevo(entidad.getValorNuevo())
                .accion(entidad.getAccion())
                .usuarioId(entidad.getUsuarioId())
                .fechaHora(entidad.getFechaHora())
                .build();
    }
}

