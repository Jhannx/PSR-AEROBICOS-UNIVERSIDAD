package org.jhanp_dev.psraerobicosbackend.modules.aerobics.session.service.interfaces;

import org.jhanp_dev.psraerobicosbackend.modules.aerobics.session.dto.request.SesionRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.session.dto.response.SesionResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface ISesionService {
    List<SesionResponseDTO> listar();
    List<SesionResponseDTO> listarPorFecha(LocalDate fecha);
    SesionResponseDTO crear(SesionRequestDTO request);
    boolean cancelar(Integer id, Integer usuarioId);
}
