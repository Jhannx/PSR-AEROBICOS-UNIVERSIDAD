package org.jhanp_dev.psraerobicosbackend.modules.aerobics.schedule.dao.interfaces;

import org.jhanp_dev.psraerobicosbackend.modules.aerobics.schedule.dto.request.ActualizarHorarioRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.schedule.dto.request.CrearHorarioRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.schedule.dto.response.HorarioResponseDTO;

import java.util.List;
import java.util.Optional;

public interface IHorarioDAO {
    List<HorarioResponseDTO> listarTodos();
    List<HorarioResponseDTO> listarActivos();
    Optional<HorarioResponseDTO> obtenerPorId(Integer id);
    HorarioResponseDTO guardar(CrearHorarioRequestDTO dto);
    boolean actualizar(Integer id, ActualizarHorarioRequestDTO dto);
    boolean eliminar(Integer id);
}
