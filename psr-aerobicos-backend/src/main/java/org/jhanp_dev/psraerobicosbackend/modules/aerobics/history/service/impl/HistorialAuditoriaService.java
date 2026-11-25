package org.jhanp_dev.psraerobicosbackend.modules.aerobics.history.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.history.dao.interfaces.IHistorialAuditoriaDAO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.history.dto.response.HistorialAuditoriaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.history.service.interfaces.IHistorialAuditoriaService;
import org.jhanp_dev.psraerobicosbackend.shared.exception.InvalidDataException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HistorialAuditoriaService implements IHistorialAuditoriaService {

    private final IHistorialAuditoriaDAO historialAuditoriaDAO;

    @Override
    public Page<HistorialAuditoriaResponseDTO> listar(int page, int size) {
        Pageable pageable = validarPaginacion(page, size);
        log.debug("Consultando historial de auditoría, página {} y tamaño {}", page, size);
        List<HistorialAuditoriaResponseDTO> historialCambios = historialAuditoriaDAO.listarPaginado(pageable);
        return new PageImpl<>(historialCambios, pageable, historialAuditoriaDAO.contar());
    }

    private Pageable validarPaginacion(int page, int size) {
        if (page < 0 || size < 1 || size > 100) {
            throw new InvalidDataException("La página debe ser mayor o igual a cero y el tamaño debe estar entre 1 y 100");
        }
        return PageRequest.of(page, size);
    }
}
