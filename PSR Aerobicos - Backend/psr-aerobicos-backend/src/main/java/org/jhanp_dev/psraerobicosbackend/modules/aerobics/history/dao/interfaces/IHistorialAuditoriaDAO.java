package org.jhanp_dev.psraerobicosbackend.modules.aerobics.history.dao.interfaces;

import org.jhanp_dev.psraerobicosbackend.modules.aerobics.history.dto.request.HistorialAuditoriaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.history.dto.response.HistorialAuditoriaResponseDTO;

import java.util.List;
import java.util.Optional;

public interface IHistorialAuditoriaDAO {
    List<HistorialAuditoriaResponseDTO> listarTodos();
    List<HistorialAuditoriaResponseDTO> listarPorTablaAfectada(String tablaAfectada);
    Optional<HistorialAuditoriaResponseDTO> obtenerPorId(Integer id);
    HistorialAuditoriaResponseDTO guardar(HistorialAuditoriaRequestDTO dto);
    boolean actualizar(Integer id, HistorialAuditoriaRequestDTO dto);
    boolean eliminar(Integer id);
}
