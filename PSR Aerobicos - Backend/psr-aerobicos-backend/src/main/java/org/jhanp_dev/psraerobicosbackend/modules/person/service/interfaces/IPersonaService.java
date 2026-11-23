package org.jhanp_dev.psraerobicosbackend.modules.person.service.interfaces;

import org.jhanp_dev.psraerobicosbackend.modules.person.dto.request.PersonaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.person.dto.response.PersonaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.person.dto.response.SaldoPersonaResponseDTO;

import java.util.List;
import java.util.Optional;

public interface IPersonaService {
    List<PersonaResponseDTO> listar(String buscar);
    Optional<PersonaResponseDTO> obtener(Integer id);
    Optional<SaldoPersonaResponseDTO> obtenerSaldo(Integer id);
    /** Crea persona, contacto y cuenta; la clave inicial para ADMIN se deriva del documento. */
    PersonaResponseDTO crearComoAdmin(PersonaRequestDTO request);
    /** Crea persona, contacto y cuenta usando la contraseña elegida durante el autoregistro. */
    PersonaResponseDTO autoregistrar(PersonaRequestDTO request);
    PersonaResponseDTO actualizar(Integer id, PersonaRequestDTO request);
    boolean cambiarEstado(Integer id, boolean activo);
}
