package org.jhanp_dev.psraerobicosbackend.modules.aerobics.session.dao.interfaces;

import org.jhanp_dev.psraerobicosbackend.modules.aerobics.session.dto.request.SesionRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.session.dto.response.SesionResponseDTO;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ISesionDAO {
    List<SesionResponseDTO> listarPaginado(Pageable pageable);
    long contar();
    List<SesionResponseDTO> listarPorFechaPaginado(LocalDate fecha, Pageable pageable);
    long contarPorFecha(LocalDate fecha);
    List<SesionResponseDTO> listarFiltrado(LocalDate fecha, String estado, LocalDateTime ahora, Pageable pageable);
    long contarFiltrado(LocalDate fecha, String estado, LocalDateTime ahora);
    Set<String> listarHorariosProgramados(LocalDate desde, LocalDate hasta);
    Optional<SesionResponseDTO> obtenerPorId(Integer id);
    boolean existePorHorarioYFecha(Integer horarioId, LocalDate fecha);
    SesionResponseDTO guardar(SesionRequestDTO dto);
    boolean cancelar(Integer id, Integer usuarioId);
}
