package org.jhanp_dev.psraerobicosbackend.modules.aerobics.pricing.service.interfaces;

import org.jhanp_dev.psraerobicosbackend.modules.aerobics.pricing.dto.request.TarifaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.pricing.dto.response.TarifaResponseDTO;

public interface ITarifaService {
    TarifaResponseDTO obtenerVigente();
    TarifaResponseDTO actualizar(TarifaRequestDTO request);
}
