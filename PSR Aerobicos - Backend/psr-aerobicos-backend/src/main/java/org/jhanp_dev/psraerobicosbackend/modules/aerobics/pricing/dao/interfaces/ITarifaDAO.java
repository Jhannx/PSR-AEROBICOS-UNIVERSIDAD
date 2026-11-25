package org.jhanp_dev.psraerobicosbackend.modules.aerobics.pricing.dao.interfaces;

import org.jhanp_dev.psraerobicosbackend.modules.aerobics.pricing.dto.request.TarifaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.pricing.dto.response.TarifaResponseDTO;

import java.util.List;
import java.util.Optional;

public interface ITarifaDAO {
    List<TarifaResponseDTO> listarTodos();
    Optional<TarifaResponseDTO> obtenerPorId(Integer id);
    Optional<TarifaResponseDTO> obtenerTarifaActual();
    TarifaResponseDTO actualizarVigente(TarifaRequestDTO dto);
}
