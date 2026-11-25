package org.jhanp_dev.psraerobicosbackend.modules.aerobics.history.service.interfaces;

import org.jhanp_dev.psraerobicosbackend.modules.aerobics.history.dto.response.HistorialAuditoriaResponseDTO;
import org.springframework.data.domain.Page;

public interface IHistorialAuditoriaService {
    Page<HistorialAuditoriaResponseDTO> listar(int page, int size);
}
