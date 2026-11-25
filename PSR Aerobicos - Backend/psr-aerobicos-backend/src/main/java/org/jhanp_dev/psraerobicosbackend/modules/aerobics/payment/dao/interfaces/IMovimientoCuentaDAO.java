package org.jhanp_dev.psraerobicosbackend.modules.aerobics.payment.dao.interfaces;

import org.jhanp_dev.psraerobicosbackend.modules.aerobics.payment.dto.request.ActualizarMovimientoCuentaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.payment.dto.request.RegistrarMovimientoCuentaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.payment.dto.response.MovimientoCuentaResponseDTO;

import java.util.List;
import java.util.Optional;

public interface IMovimientoCuentaDAO {
    List<MovimientoCuentaResponseDTO> listarTodos();
    List<MovimientoCuentaResponseDTO> listarPorPersonaId(Integer personaId);
    List<MovimientoCuentaResponseDTO> listarPorLlegadaId(Integer llegadaId);
    Optional<MovimientoCuentaResponseDTO> obtenerPorId(Integer id);
    MovimientoCuentaResponseDTO guardar(RegistrarMovimientoCuentaRequestDTO dto);
    boolean actualizar(Integer id, ActualizarMovimientoCuentaRequestDTO dto);
    boolean eliminar(Integer id);
}
