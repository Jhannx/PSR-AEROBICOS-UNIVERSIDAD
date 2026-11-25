package org.jhanp_dev.psraerobicosbackend.modules.aerobics.session.service.interfaces;

import org.jhanp_dev.psraerobicosbackend.modules.aerobics.session.dto.request.SesionRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.session.dto.response.SesionResponseDTO;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

public interface ISesionService {
    Page<SesionResponseDTO> listar(int page, int size);
    Page<SesionResponseDTO> listarPorFecha(LocalDate fecha, int page, int size);
    Page<SesionResponseDTO> listarFiltrado(LocalDate fecha, String estado, int page, int size);
    void asegurarSesionesDelDia(LocalDate fecha);
    SesionResponseDTO crear(SesionRequestDTO request);
    boolean cancelar(Integer id, Integer usuarioId);
}
