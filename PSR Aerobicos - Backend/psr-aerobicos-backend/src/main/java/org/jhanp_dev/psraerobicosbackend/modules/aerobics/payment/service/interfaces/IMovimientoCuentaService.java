package org.jhanp_dev.psraerobicosbackend.modules.aerobics.payment.service.interfaces;

import org.jhanp_dev.psraerobicosbackend.modules.aerobics.payment.dto.request.MovimientoCuentaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.payment.dto.response.MovimientoCuentaResponseDTO;

import java.util.List;

public interface IMovimientoCuentaService {
    MovimientoCuentaResponseDTO registrar(MovimientoCuentaRequestDTO request);
    List<MovimientoCuentaResponseDTO> listarPorLlegada(Integer llegadaId);
    MovimientoCuentaResponseDTO actualizar(Integer id, MovimientoCuentaRequestDTO request);
    boolean eliminar(Integer id);
}
