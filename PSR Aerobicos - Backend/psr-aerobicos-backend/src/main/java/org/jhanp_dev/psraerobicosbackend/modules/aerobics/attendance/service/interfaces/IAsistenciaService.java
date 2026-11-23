package org.jhanp_dev.psraerobicosbackend.modules.aerobics.attendance.service.interfaces;

import org.jhanp_dev.psraerobicosbackend.modules.aerobics.attendance.dto.request.AsistenciaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.attendance.dto.response.AsistenciaResponseDTO;

import java.util.Optional;

public interface IAsistenciaService {
    Optional<AsistenciaResponseDTO> obtenerPorLlegada(Integer llegadaId);
    AsistenciaResponseDTO confirmar(Integer llegadaId, AsistenciaRequestDTO request);
    boolean anular(Integer llegadaId);
}
