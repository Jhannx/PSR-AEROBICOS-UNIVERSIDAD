package org.jhanp_dev.psraerobicosbackend.modules.aerobics.payment.mapper;

import org.jhanp_dev.psraerobicosbackend.modules.aerobics.payment.dto.request.ActualizarMovimientoCuentaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.payment.dto.request.RegistrarMovimientoCuentaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.payment.dto.response.MovimientoCuentaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.payment.entity.MovimientoCuenta;
import org.springframework.stereotype.Component;

@Component
public class MovimientoCuentaMapper {

    public MovimientoCuenta aEntidad(RegistrarMovimientoCuentaRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        return MovimientoCuenta.builder()
                .personaId(dto.getPersonaId())
                .llegadaId(dto.getLlegadaId())
                .montoRecibido(dto.getMontoRecibido())
                .montoAbonado(dto.getMontoAbonado())
                .metodoPago(dto.getMetodoPago())
                .tipoMovimiento(dto.getTipoMovimiento())
                .vueltoEntregado(dto.getVueltoEntregado())
                .usuarioId(dto.getUsuarioId())
                .nota(dto.getNota())
                .build();
    }

    public MovimientoCuenta aEntidad(ActualizarMovimientoCuentaRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        return MovimientoCuenta.builder()
                .personaId(dto.getPersonaId())
                .llegadaId(dto.getLlegadaId())
                .montoRecibido(dto.getMontoRecibido())
                .montoAbonado(dto.getMontoAbonado())
                .metodoPago(dto.getMetodoPago())
                .tipoMovimiento(dto.getTipoMovimiento())
                .vueltoEntregado(dto.getVueltoEntregado())
                .usuarioId(dto.getUsuarioId())
                .nota(dto.getNota())
                .build();
    }

    public MovimientoCuentaResponseDTO aResponseDTO(MovimientoCuenta entidad) {
        if (entidad == null) {
            return null;
        }
        return MovimientoCuentaResponseDTO.builder()
                .id(entidad.getId())
                .personaId(entidad.getPersonaId())
                .llegadaId(entidad.getLlegadaId())
                .montoRecibido(entidad.getMontoRecibido())
                .montoAbonado(entidad.getMontoAbonado())
                .metodoPago(entidad.getMetodoPago())
                .tipoMovimiento(entidad.getTipoMovimiento())
                .vueltoEntregado(entidad.getVueltoEntregado())
                .fechaHora(entidad.getFechaHora())
                .usuarioId(entidad.getUsuarioId())
                .nota(entidad.getNota())
                .build();
    }
}

