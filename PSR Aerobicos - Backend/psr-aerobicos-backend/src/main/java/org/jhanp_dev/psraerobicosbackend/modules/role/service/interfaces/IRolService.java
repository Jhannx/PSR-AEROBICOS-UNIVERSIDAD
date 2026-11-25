package org.jhanp_dev.psraerobicosbackend.modules.role.service.interfaces;

import org.jhanp_dev.psraerobicosbackend.modules.role.dto.response.RolResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.role.enums.RolNombre;

import java.util.List;

public interface IRolService {

    List<RolResponseDTO> listarTodos();

    RolResponseDTO obtenerPorId(Integer id);

    RolResponseDTO obtenerPorNombre(RolNombre nombre);
}
