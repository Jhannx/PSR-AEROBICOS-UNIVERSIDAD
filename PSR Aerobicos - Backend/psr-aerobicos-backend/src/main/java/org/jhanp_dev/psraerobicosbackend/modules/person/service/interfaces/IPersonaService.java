package org.jhanp_dev.psraerobicosbackend.modules.person.service.interfaces;

import org.jhanp_dev.psraerobicosbackend.modules.person.dto.request.ActualizarPersonaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.person.dto.request.AutoregistroPersonaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.person.dto.request.CrearPersonaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.person.dto.response.PersonaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.person.dto.response.SaldoPersonaResponseDTO;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface IPersonaService {
    Page<PersonaResponseDTO> listar(String buscar, int page, int size);
    Optional<PersonaResponseDTO> obtener(Integer id);
    Optional<SaldoPersonaResponseDTO> obtenerSaldo(Integer id);
    /** Crea persona, contacto y cuenta; la clave inicial para ADMIN se deriva del documento. */
    PersonaResponseDTO crearComoAdmin(CrearPersonaRequestDTO request);
    /** Crea persona, contacto y cuenta usando la contraseña elegida durante el autoregistro. */
    PersonaResponseDTO autoregistrar(AutoregistroPersonaRequestDTO request);
    PersonaResponseDTO actualizar(Integer id, ActualizarPersonaRequestDTO request);
    boolean cambiarEstado(Integer id, boolean activo);
}
