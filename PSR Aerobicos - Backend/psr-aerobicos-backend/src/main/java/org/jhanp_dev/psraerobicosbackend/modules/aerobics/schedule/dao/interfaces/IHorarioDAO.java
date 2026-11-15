package org.jhanp_dev.psraerobicosbackend.modules.aerobics.schedule.dao.interfaces;

import org.jhanp_dev.psraerobicosbackend.modules.aerobics.schedule.dto.request.HorarioRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.schedule.dto.response.HorarioResponseDTO;

import java.util.List;
import java.util.Optional;

public interface IHorarioDAO {
    List<HorarioResponseDTO> listarTodos();
    List<HorarioResponseDTO> listarActivos();
    Optional<HorarioResponseDTO> obtenerPorId(Integer id);
    HorarioResponseDTO guardar(HorarioRequestDTO dto);
    boolean actualizar(Integer id, HorarioRequestDTO dto);
    boolean eliminar(Integer id);
}
