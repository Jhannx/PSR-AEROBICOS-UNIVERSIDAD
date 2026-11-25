package org.jhanp_dev.psraerobicosbackend.modules.aerobics.payment.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.payment.dao.interfaces.IMovimientoCuentaDAO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.payment.dto.request.ActualizarMovimientoCuentaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.payment.dto.request.RegistrarMovimientoCuentaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.payment.dto.response.MovimientoCuentaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.payment.enums.MetodoPago;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.payment.enums.TipoMovimiento;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.payment.service.interfaces.IMovimientoCuentaService;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.dao.interfaces.ILlegadaDAO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.dto.response.LlegadaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.person.dao.interfaces.IPersonaDAO;
import org.jhanp_dev.psraerobicosbackend.modules.user.dao.interfaces.IUsuarioDAO;
import org.jhanp_dev.psraerobicosbackend.modules.user.dto.response.UsuarioResponseDTO;
import org.jhanp_dev.psraerobicosbackend.shared.exception.InvalidDataException;
import org.jhanp_dev.psraerobicosbackend.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovimientoCuentaService implements IMovimientoCuentaService {

    private static final int MAX_DIGITOS_ENTEROS = 8;
    private static final int MAX_DECIMALES = 2;

    private final IMovimientoCuentaDAO movimientoCuentaDAO;
    private final IPersonaDAO personaDAO;
    private final ILlegadaDAO llegadaDAO;
    private final IUsuarioDAO usuarioDAO;

    @Override
    public MovimientoCuentaResponseDTO registrar(RegistrarMovimientoCuentaRequestDTO request) {
        validarSolicitud(request);
        validarReferencias(request.getPersonaId(), request.getUsuarioId(), request.getLlegadaId());
        if (request.getMetodoPago() == null) {
            request.setMetodoPago(MetodoPago.EFECTIVO);
        }
        log.debug("Registrando movimiento de cuenta para la persona {}", request.getPersonaId());
        return movimientoCuentaDAO.guardar(request);
    }

    @Override
    public List<MovimientoCuentaResponseDTO> listarPorLlegada(Integer llegadaId) {
        validarId(llegadaId, "llegada");
        return movimientoCuentaDAO.listarPorLlegadaId(llegadaId);
    }

    @Override
    public MovimientoCuentaResponseDTO actualizar(Integer id, ActualizarMovimientoCuentaRequestDTO request) {
        validarId(id, "movimiento");
        validarSolicitud(request);
        validarReferencias(request.getPersonaId(), request.getUsuarioId(), request.getLlegadaId());
        if (request.getMetodoPago() == null) {
            throw new InvalidDataException("El método de pago es obligatorio al actualizar un movimiento");
        }
        if (!movimientoCuentaDAO.actualizar(id, request)) {
            throw new ResourceNotFoundException("No se encontró el movimiento con ID: " + id);
        }
        return movimientoCuentaDAO.obtenerPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el movimiento con ID: " + id));
    }

    @Override
    public boolean eliminar(Integer id) {
        validarId(id, "movimiento");
        log.debug("Eliminando movimiento de cuenta {}", id);
        return movimientoCuentaDAO.eliminar(id);
    }

    private void validarSolicitud(RegistrarMovimientoCuentaRequestDTO request) {
        if (request == null) {
            throw new InvalidDataException("Los datos del movimiento son obligatorios");
        }
        validarMontos(request.getMontoRecibido(), request.getMontoAbonado());
        validarTipoMovimiento(request.getTipoMovimiento(), request.getVueltoEntregado());
        validarIdentificadores(request.getPersonaId(), request.getUsuarioId(), request.getLlegadaId());
    }

    private void validarSolicitud(ActualizarMovimientoCuentaRequestDTO request) {
        if (request == null) {
            throw new InvalidDataException("Los datos del movimiento son obligatorios");
        }
        validarMontos(request.getMontoRecibido(), request.getMontoAbonado());
        validarTipoMovimiento(request.getTipoMovimiento(), request.getVueltoEntregado());
        validarIdentificadores(request.getPersonaId(), request.getUsuarioId(), request.getLlegadaId());
    }

    private void validarMontos(BigDecimal montoRecibido, BigDecimal montoAbonado) {
        validarMonto(montoRecibido, "El monto recibido");
        validarMonto(montoAbonado, "El monto abonado");
        if (montoRecibido.signum() == 0 && montoAbonado.signum() == 0) {
            throw new InvalidDataException("Al menos uno de los montos debe ser mayor que cero");
        }
    }

    private void validarTipoMovimiento(TipoMovimiento tipoMovimiento, Boolean vueltoEntregado) {
        if (tipoMovimiento == null) {
            throw new InvalidDataException("El tipo de movimiento es obligatorio");
        }
        if (vueltoEntregado == null) {
            throw new InvalidDataException("Debe indicar si el vuelto fue entregado");
        }
    }

    private void validarIdentificadores(Integer personaId, Integer usuarioId, Integer llegadaId) {
        validarId(personaId, "persona");
        validarId(usuarioId, "usuario");
        if (llegadaId != null) {
            validarId(llegadaId, "llegada");
        }
    }

    private void validarReferencias(Integer personaId, Integer usuarioId, Integer llegadaId) {
        if (personaDAO.obtenerPorId(personaId).isEmpty()) {
            throw new ResourceNotFoundException("No se encontró la persona indicada");
        }
        UsuarioResponseDTO usuario = usuarioDAO.obtenerPorId(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el usuario indicado"));
        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new InvalidDataException("Un usuario inactivo no puede registrar movimientos de cuenta");
        }
        if (llegadaId != null) {
            LlegadaResponseDTO llegada = llegadaDAO.obtenerPorId(llegadaId)
                    .orElseThrow(() -> new ResourceNotFoundException("No se encontró la llegada indicada"));
            if (!personaId.equals(llegada.getPersonaId())) {
                throw new InvalidDataException("La llegada indicada no pertenece a la persona seleccionada");
            }
        }
    }

    private void validarMonto(BigDecimal monto, String campo) {
        if (monto == null || monto.signum() < 0) {
            throw new InvalidDataException(campo + " debe ser mayor o igual a cero");
        }
        int digitosEnteros = Math.max(0, monto.precision() - monto.scale());
        if (digitosEnteros > MAX_DIGITOS_ENTEROS || monto.scale() > MAX_DECIMALES) {
            throw new InvalidDataException(campo + " debe tener hasta 8 dígitos enteros y 2 decimales");
        }
    }

    private void validarId(Integer id, String campo) {
        if (id == null || id < 1) {
            throw new InvalidDataException("El ID de " + campo + " debe ser mayor que cero");
        }
    }
}
