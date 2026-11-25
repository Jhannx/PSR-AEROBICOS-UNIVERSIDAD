package org.jhanp_dev.psraerobicosbackend.modules.aerobics.attendance.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.attendance.dao.interfaces.IAsistenciaDAO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.attendance.dto.request.AsistenciaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.attendance.dto.response.AsistenciaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.attendance.service.interfaces.IAsistenciaService;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.dao.interfaces.ILlegadaDAO;
import org.jhanp_dev.psraerobicosbackend.modules.user.dao.interfaces.IUsuarioDAO;
import org.jhanp_dev.psraerobicosbackend.modules.user.dto.response.UsuarioResponseDTO;
import org.jhanp_dev.psraerobicosbackend.shared.exception.InvalidDataException;
import org.jhanp_dev.psraerobicosbackend.shared.exception.ConflictException;
import org.jhanp_dev.psraerobicosbackend.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsistenciaService implements IAsistenciaService {

    private final IAsistenciaDAO asistenciaDAO;
    private final ILlegadaDAO llegadaDAO;
    private final IUsuarioDAO usuarioDAO;

    @Override
    public Optional<AsistenciaResponseDTO> obtenerPorLlegada(Integer llegadaId) {
        validarLlegadaId(llegadaId);
        validarLlegadaExiste(llegadaId);
        log.debug("Consultando asistencia para la llegada {}", llegadaId);
        return asistenciaDAO.obtenerPorLlegadaId(llegadaId);
    }

    @Override
    public AsistenciaResponseDTO confirmar(Integer llegadaId, AsistenciaRequestDTO request) {
        validarLlegadaId(llegadaId);
        validarSolicitudConfirmacion(request);
        validarLlegadaExiste(llegadaId);
        validarUsuarioActivo(request.getUsuarioId());
        if (asistenciaDAO.obtenerPorLlegadaId(llegadaId).isPresent()) {
            throw new ConflictException("La llegada ya fue confirmada previamente");
        }

        log.debug("Confirmando asistencia para la llegada {} por el usuario {}", llegadaId, request.getUsuarioId());
        return asistenciaDAO.registrar(llegadaId, request);
    }

    @Override
    public boolean anular(Integer llegadaId) {
        validarLlegadaId(llegadaId);
        validarLlegadaExiste(llegadaId);
        log.debug("Anulando asistencia de la llegada {}", llegadaId);
        return asistenciaDAO.anularPorLlegadaId(llegadaId);
    }

    private void validarLlegadaExiste(Integer llegadaId) {
        if (llegadaDAO.obtenerPorId(llegadaId).isEmpty()) {
            throw new ResourceNotFoundException("No se encontró la llegada indicada");
        }
    }

    private void validarSolicitudConfirmacion(AsistenciaRequestDTO request) {
        if (request == null || request.getUsuarioId() == null || request.getUsuarioId() < 1) {
            throw new InvalidDataException("El usuario que confirma la asistencia es obligatorio");
        }
    }

    private void validarUsuarioActivo(Integer usuarioId) {
        UsuarioResponseDTO usuario = usuarioDAO.obtenerPorId(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el usuario indicado"));
        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new InvalidDataException("Un usuario inactivo no puede confirmar asistencias");
        }
    }

    private void validarLlegadaId(Integer llegadaId) {
        if (llegadaId == null || llegadaId < 1) {
            throw new InvalidDataException("El ID de llegada debe ser mayor que cero");
        }
    }
}
