package org.jhanp_dev.psraerobicosbackend.modules.aerobics.attendance.dao.interfaces;

import org.jhanp_dev.psraerobicosbackend.modules.aerobics.attendance.dto.request.AsistenciaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.attendance.dto.response.AsistenciaResponseDTO;

import java.util.List;
import java.util.Optional;

public interface IAsistenciaDAO {
    List<AsistenciaResponseDTO> listarTodos();
    Optional<AsistenciaResponseDTO> obtenerPorId(Integer id);
    Optional<AsistenciaResponseDTO> obtenerPorLlegadaId(Integer llegadaId);
    AsistenciaResponseDTO guardar(AsistenciaRequestDTO dto);
    boolean actualizar(Integer id, AsistenciaRequestDTO dto);
    boolean eliminar(Integer id);
}
