package org.jhanp_dev.psraerobicosbackend.modules.aerobics.payment.dao.interfaces;

import org.jhanp_dev.psraerobicosbackend.modules.aerobics.payment.dto.request.MovimientoCuentaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.payment.dto.response.MovimientoCuentaResponseDTO;

import java.util.List;
import java.util.Optional;

public interface IMovimientoCuentaDAO {
    List<MovimientoCuentaResponseDTO> listarTodos();
    List<MovimientoCuentaResponseDTO> listarPorPersonaId(Integer personaId);
    Optional<MovimientoCuentaResponseDTO> obtenerPorId(Integer id);
    MovimientoCuentaResponseDTO guardar(MovimientoCuentaRequestDTO dto);
    boolean actualizar(Integer id, MovimientoCuentaRequestDTO dto);
    boolean eliminar(Integer id);
}
