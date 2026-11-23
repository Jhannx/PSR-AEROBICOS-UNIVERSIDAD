package org.jhanp_dev.psraerobicosbackend.modules.aerobics.attendance.dao.interfaces;

import org.jhanp_dev.psraerobicosbackend.modules.aerobics.attendance.dto.request.AsistenciaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.attendance.dto.response.AsistenciaResponseDTO;

import java.util.Optional;

public interface IAsistenciaDAO {
    Optional<AsistenciaResponseDTO> obtenerPorLlegadaId(Integer llegadaId);
    AsistenciaResponseDTO registrar(Integer llegadaId, AsistenciaRequestDTO request);
    boolean anularPorLlegadaId(Integer llegadaId);
}
