package org.jhanp_dev.psraerobicosbackend.modules.role.service.interfaces;

import org.jhanp_dev.psraerobicosbackend.modules.role.dto.response.RolResponseDTO;

import java.util.List;

public interface IRolService {
    List<RolResponseDTO> listar();
}
