package org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.dao.interfaces;

import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.dto.request.LlegadaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.dto.response.LlegadaResponseDTO;

import java.util.List;
import java.util.Optional;

public interface ILlegadaDAO {
    List<LlegadaResponseDTO> listarTodos();
    List<LlegadaResponseDTO> listarPorSesionId(Integer sesionId);
    List<LlegadaResponseDTO> listarPorPersonaId(Integer personaId);
    Optional<LlegadaResponseDTO> obtenerPorId(Integer id);
    LlegadaResponseDTO guardar(LlegadaRequestDTO dto);
    LlegadaResponseDTO registrar(Integer personaId, Integer sesionId, LlegadaRequestDTO request);
    boolean actualizar(Integer id, LlegadaRequestDTO dto);
    boolean eliminar(Integer id);
}
