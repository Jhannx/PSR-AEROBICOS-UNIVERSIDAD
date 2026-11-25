package org.jhanp_dev.psraerobicosbackend.modules.aerobics.history.dao.interfaces;

import org.jhanp_dev.psraerobicosbackend.modules.aerobics.history.dto.response.HistorialAuditoriaResponseDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IHistorialAuditoriaDAO {
    List<HistorialAuditoriaResponseDTO> listarPaginado(Pageable pageable);
    long contar();
}
