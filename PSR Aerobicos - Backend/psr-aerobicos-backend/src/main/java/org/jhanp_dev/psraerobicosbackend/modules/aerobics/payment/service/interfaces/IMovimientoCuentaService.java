package org.jhanp_dev.psraerobicosbackend.modules.aerobics.payment.service.interfaces;

import org.jhanp_dev.psraerobicosbackend.modules.aerobics.payment.dto.request.ActualizarMovimientoCuentaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.payment.dto.request.RegistrarMovimientoCuentaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.payment.dto.response.MovimientoCuentaResponseDTO;

import java.util.List;

public interface IMovimientoCuentaService {
    MovimientoCuentaResponseDTO registrar(RegistrarMovimientoCuentaRequestDTO request);
    List<MovimientoCuentaResponseDTO> listarPorLlegada(Integer llegadaId);
    MovimientoCuentaResponseDTO actualizar(Integer id, ActualizarMovimientoCuentaRequestDTO request);
    boolean eliminar(Integer id);
}
