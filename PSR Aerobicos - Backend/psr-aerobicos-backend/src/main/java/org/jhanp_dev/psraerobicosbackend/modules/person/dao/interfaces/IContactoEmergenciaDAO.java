package org.jhanp_dev.psraerobicosbackend.modules.person.dao.interfaces;

import org.jhanp_dev.psraerobicosbackend.modules.person.dto.response.ContactoEmergenciaResponseDTO;

import java.util.List;
import java.util.Optional;

public interface IContactoEmergenciaDAO {
    List<ContactoEmergenciaResponseDTO> listarTodos();
    Optional<ContactoEmergenciaResponseDTO> obtenerPorId(Integer id);
    Optional<ContactoEmergenciaResponseDTO> obtenerPorPersonaId(Integer personaId);
}
