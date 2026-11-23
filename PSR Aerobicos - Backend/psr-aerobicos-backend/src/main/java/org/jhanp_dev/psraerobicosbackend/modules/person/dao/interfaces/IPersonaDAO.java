package org.jhanp_dev.psraerobicosbackend.modules.person.dao.interfaces;

import org.jhanp_dev.psraerobicosbackend.modules.person.dto.request.PersonaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.person.dto.response.PersonaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.person.dto.response.SaldoPersonaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.person.enums.FuenteRegistro;

import java.util.List;
import java.util.Optional;

public interface IPersonaDAO {
    List<PersonaResponseDTO> listarTodos();
    List<PersonaResponseDTO> buscar(String termino);
    Optional<PersonaResponseDTO> obtenerPorId(Integer id);
    Optional<PersonaResponseDTO> obtenerPorNumeroDocumento(String numeroDocumento);
    Optional<SaldoPersonaResponseDTO> obtenerSaldo(Integer personaId);
    PersonaResponseDTO crearConContacto(PersonaRequestDTO dto, FuenteRegistro fuente, String passwordHash);
    boolean actualizar(Integer id, PersonaRequestDTO dto);
    boolean eliminar(Integer id);
}
