package org.jhanp_dev.psraerobicosbackend.modules.aerobics.schedule.service.interfaces;

import org.jhanp_dev.psraerobicosbackend.modules.aerobics.schedule.dto.request.HorarioRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.schedule.dto.response.HorarioResponseDTO;

import java.util.List;

public interface IHorarioService {
    List<HorarioResponseDTO> listar();
    HorarioResponseDTO crear(HorarioRequestDTO request);
    HorarioResponseDTO actualizar(Integer id, HorarioRequestDTO request);
    boolean eliminar(Integer id);
}
