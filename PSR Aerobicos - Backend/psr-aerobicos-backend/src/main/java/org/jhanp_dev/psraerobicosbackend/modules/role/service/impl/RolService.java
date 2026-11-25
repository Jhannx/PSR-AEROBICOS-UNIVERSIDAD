package org.jhanp_dev.psraerobicosbackend.modules.role.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jhanp_dev.psraerobicosbackend.modules.role.dao.interfaces.IRolDAO;
import org.jhanp_dev.psraerobicosbackend.modules.role.dto.response.RolResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.role.enums.RolNombre;
import org.jhanp_dev.psraerobicosbackend.modules.role.service.interfaces.IRolService;
import org.jhanp_dev.psraerobicosbackend.shared.exception.InvalidDataException;
import org.jhanp_dev.psraerobicosbackend.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RolService implements IRolService {

    private final IRolDAO rolDAO;

    @Override
    public List<RolResponseDTO> listarTodos() {
        log.debug("Consultando todos los roles del sistema");
        return rolDAO.listarTodos();
    }

    @Override
    public RolResponseDTO obtenerPorId(Integer id) {
        validarId(id);

        log.debug("Buscando rol con ID: {}", id);
        return rolDAO.obtenerPorId(id).orElseThrow(() -> {
            log.warn("Rol no encontrado con ID: {}", id);
            return new ResourceNotFoundException("Rol no encontrado con ID: " + id);
        });
    }

    private void validarId(Integer id) {
        if (id == null || id < 1) {
            throw new InvalidDataException("El ID del rol debe ser mayor que cero");
        }
    }

    @Override
    public RolResponseDTO obtenerPorNombre(RolNombre nombre) {
        if (nombre == null) {
            throw new InvalidDataException("El nombre del rol es obligatorio");
        }

        log.debug("Buscando rol por nombre: {}", nombre);
        return rolDAO.obtenerPorNombre(nombre).orElseThrow(() -> {
            log.warn("Rol no encontrado con nombre: {}", nombre);
            return new ResourceNotFoundException("Rol no encontrado con nombre: " + nombre);
        });
    }
}
