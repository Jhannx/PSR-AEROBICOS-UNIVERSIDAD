package org.jhanp_dev.psraerobicosbackend.modules.aerobics.schedule.service.interfaces;

import org.jhanp_dev.psraerobicosbackend.modules.aerobics.schedule.dto.request.ActualizarHorarioRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.schedule.dto.request.CrearHorarioRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.schedule.dto.response.HorarioResponseDTO;

import java.util.List;

public interface IHorarioService {
    List<HorarioResponseDTO> listar();
    HorarioResponseDTO crear(CrearHorarioRequestDTO request);
    HorarioResponseDTO actualizar(Integer id, ActualizarHorarioRequestDTO request);
    boolean eliminar(Integer id);
}
