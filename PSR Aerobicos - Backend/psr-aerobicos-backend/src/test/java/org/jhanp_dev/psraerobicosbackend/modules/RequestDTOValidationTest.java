package org.jhanp_dev.psraerobicosbackend.modules;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.dto.request.CheckinManualRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.dto.request.CheckinMiembroRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.payment.dto.request.ActualizarMovimientoCuentaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.payment.dto.request.RegistrarMovimientoCuentaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.payment.enums.TipoMovimiento;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.schedule.dto.request.ActualizarHorarioRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.schedule.dto.request.CrearHorarioRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.user.dto.request.CrearUsuarioRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.user.dto.request.LoginRequestDTO;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RequestDTOValidationTest {

    @Test
    void usuarioDistingueCredencialesYCreacion() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            assertTrue(validator.validate(LoginRequestDTO.builder()
                    .identificador("ana@example.com").password("claveSegura").build()).isEmpty());
            assertFalse(validator.validate(LoginRequestDTO.builder()
                    .identificador("ana@example.com").build()).isEmpty());
            assertTrue(validator.validate(CrearUsuarioRequestDTO.builder()
                    .personaId(1).correo("ana@example.com").build()).isEmpty());
            assertFalse(validator.validate(CrearUsuarioRequestDTO.builder()
                    .correo("ana@example.com").build()).isEmpty());
        }
    }

    @Test
    void horarioRequiereEstadoSoloAlActualizar() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            assertTrue(validator.validate(CrearHorarioRequestDTO.builder()
                    .diaSemana(1).horaInicio(LocalTime.of(8, 0))
                    .horaFin(LocalTime.of(9, 0)).build()).isEmpty());
            assertFalse(validator.validate(ActualizarHorarioRequestDTO.builder()
                    .diaSemana(1).horaInicio(LocalTime.of(8, 0))
                    .horaFin(LocalTime.of(9, 0)).build()).isEmpty());
        }
    }

    @Test
    void movimientoRequiereMetodoDePagoSoloAlActualizar() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            assertTrue(validator.validate(RegistrarMovimientoCuentaRequestDTO.builder()
                    .personaId(1).usuarioId(2).montoRecibido(new BigDecimal("100.00"))
                    .montoAbonado(BigDecimal.ZERO).tipoMovimiento(TipoMovimiento.ABONO)
                    .vueltoEntregado(false).build()).isEmpty());
            assertFalse(validator.validate(ActualizarMovimientoCuentaRequestDTO.builder()
                    .personaId(1).usuarioId(2).montoRecibido(new BigDecimal("100.00"))
                    .montoAbonado(BigDecimal.ZERO).tipoMovimiento(TipoMovimiento.ABONO)
                    .vueltoEntregado(false).build()).isEmpty());
        }
    }

    @Test
    void checkinDeMiembroRequierePersonaYManualAceptaDocumento() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            assertTrue(validator.validate(CheckinManualRequestDTO.builder()
                    .documento("123456").build()).isEmpty());
            assertFalse(validator.validate(CheckinMiembroRequestDTO.builder()
                    .documento("123456").build()).isEmpty());
        }
    }
}
