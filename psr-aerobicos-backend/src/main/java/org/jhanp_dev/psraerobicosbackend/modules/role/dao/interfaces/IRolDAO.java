package org.jhanp_dev.psraerobicosbackend.modules.role.dao.interfaces;

import org.jhanp_dev.psraerobicosbackend.modules.role.dto.response.RolResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.role.enums.RolNombre;

import java.util.List;
import java.util.Optional;

public interface IRolDAO {
    List<RolResponseDTO> listarTodos();
    Optional<RolResponseDTO> obtenerPorId(Integer id);
    Optional<RolResponseDTO> obtenerPorNombre(RolNombre nombre);
}
