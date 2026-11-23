package org.jhanp_dev.psraerobicosbackend.modules.aerobics.history.service.interfaces;

import org.jhanp_dev.psraerobicosbackend.modules.aerobics.history.dto.response.HistorialAuditoriaResponseDTO;

import java.util.List;

public interface IHistorialAuditoriaService {
    List<HistorialAuditoriaResponseDTO> listar(String tabla);
}
