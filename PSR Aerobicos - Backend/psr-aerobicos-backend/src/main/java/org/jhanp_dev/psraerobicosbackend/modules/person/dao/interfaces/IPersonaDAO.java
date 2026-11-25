package org.jhanp_dev.psraerobicosbackend.modules.person.dao.interfaces;

import org.jhanp_dev.psraerobicosbackend.modules.person.dto.request.ActualizarPersonaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.person.dto.request.CrearPersonaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.person.dto.response.PersonaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.person.dto.response.SaldoPersonaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.person.enums.FuenteRegistro;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IPersonaDAO {
    List<PersonaResponseDTO> listarPaginado(String termino, Pageable pageable);
    long contar(String termino);
    Optional<PersonaResponseDTO> obtenerPorId(Integer id);
    Optional<PersonaResponseDTO> obtenerPorNumeroDocumento(String numeroDocumento);
    Optional<SaldoPersonaResponseDTO> obtenerSaldo(Integer personaId);
    PersonaResponseDTO crearConContacto(CrearPersonaRequestDTO dto, FuenteRegistro fuente, String passwordHash);
    boolean actualizar(Integer id, ActualizarPersonaRequestDTO dto);
    boolean cambiarEstado(Integer id, boolean activo);
}
