package org.jhanp_dev.psraerobicosbackend.modules.person.dao.interfaces;

import org.jhanp_dev.psraerobicosbackend.modules.person.dto.request.PersonaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.person.dto.response.PersonaResponseDTO;

import java.util.List;
import java.util.Optional;

public interface IPersonaDAO {
    List<PersonaResponseDTO> listarTodos();
    Optional<PersonaResponseDTO> obtenerPorId(Integer id);
    Optional<PersonaResponseDTO> obtenerPorNumeroDocumento(String numeroDocumento);
    PersonaResponseDTO guardar(PersonaRequestDTO dto);
    boolean actualizar(Integer id, PersonaRequestDTO dto);
    boolean eliminar(Integer id);
}
