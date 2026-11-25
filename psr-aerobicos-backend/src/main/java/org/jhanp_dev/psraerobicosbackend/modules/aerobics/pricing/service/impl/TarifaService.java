package org.jhanp_dev.psraerobicosbackend.modules.aerobics.pricing.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.pricing.dao.interfaces.ITarifaDAO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.pricing.dto.request.TarifaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.pricing.dto.response.TarifaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.pricing.service.interfaces.ITarifaService;
import org.jhanp_dev.psraerobicosbackend.modules.user.dao.interfaces.IUsuarioDAO;
import org.jhanp_dev.psraerobicosbackend.modules.user.dto.response.UsuarioResponseDTO;
import org.jhanp_dev.psraerobicosbackend.shared.exception.InvalidDataException;
import org.jhanp_dev.psraerobicosbackend.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class TarifaService implements ITarifaService {

    private final ITarifaDAO tarifaDAO;
    private final IUsuarioDAO usuarioDAO;

    @Override
    public TarifaResponseDTO obtenerVigente() {
        log.debug("Consultando la tarifa vigente");
        return tarifaDAO.obtenerTarifaActual().orElseThrow(() -> {
            log.warn("No existe una tarifa vigente configurada");
            return new ResourceNotFoundException("No existe una tarifa vigente configurada");
        });
    }

    @Override
    public TarifaResponseDTO actualizar(TarifaRequestDTO request) {
        validarSolicitud(request);
        validarUsuarioActivo(request.getUsuarioId());

        if (tarifaDAO.obtenerTarifaActual().isEmpty()) {
            log.warn("No existe una tarifa vigente para actualizar");
            throw new ResourceNotFoundException("No existe una tarifa vigente para actualizar");
        }

        log.debug("Actualizando la tarifa vigente a {}", request.getMonto());
        return tarifaDAO.actualizarVigente(request);
    }

    private void validarUsuarioActivo(Integer usuarioId) {
        UsuarioResponseDTO usuario = usuarioDAO.obtenerPorId(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el usuario indicado"));
        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new InvalidDataException("Un usuario inactivo no puede actualizar la tarifa");
        }
    }

    private void validarSolicitud(TarifaRequestDTO request) {
        if (request == null) {
            throw new InvalidDataException("Los datos de la tarifa son obligatorios");
        }
        if (request.getMonto() == null || request.getMonto().signum() <= 0) {
            throw new InvalidDataException("El monto de la tarifa debe ser mayor que cero");
        }
        if (request.getUsuarioId() == null || request.getUsuarioId() < 1) {
            throw new InvalidDataException("El usuario que actualiza la tarifa es obligatorio");
        }
    }
}
