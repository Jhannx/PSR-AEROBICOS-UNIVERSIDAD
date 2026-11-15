package org.jhanp_dev.psraerobicosbackend.modules.aerobics.session.dao.interfaces;

import org.jhanp_dev.psraerobicosbackend.modules.aerobics.session.dto.request.SesionRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.session.dto.response.SesionResponseDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ISesionDAO {
    List<SesionResponseDTO> listarTodos();
    List<SesionResponseDTO> listarPorFecha(LocalDate fecha);
    Optional<SesionResponseDTO> obtenerPorId(Integer id);
    SesionResponseDTO guardar(SesionRequestDTO dto);
    boolean actualizar(Integer id, SesionRequestDTO dto);
    boolean eliminar(Integer id);
}
