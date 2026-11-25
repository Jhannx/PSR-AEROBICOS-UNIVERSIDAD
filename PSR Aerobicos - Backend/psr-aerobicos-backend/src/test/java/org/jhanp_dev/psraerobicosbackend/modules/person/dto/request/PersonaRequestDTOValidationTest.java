package org.jhanp_dev.psraerobicosbackend.modules.person.dto.request;

import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.jhanp_dev.psraerobicosbackend.modules.person.enums.TipoDocumento;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PersonaRequestDTOValidationTest {

    @Test
    void cadaOperacionAplicaSusPropiosCamposObligatorios() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            var validator = factory.getValidator();
            var alta = CrearPersonaRequestDTO.builder()
                    .tipoDocumento(TipoDocumento.CC)
                    .numeroDocumento("123456")
                    .nombre("Ana")
                    .apellidos("Pérez")
                    .telefono("3001234567")
                    .build();
            var autoregistro = AutoregistroPersonaRequestDTO.builder()
                    .tipoDocumento(TipoDocumento.CC)
                    .numeroDocumento("123456")
                    .nombre("Ana")
                    .apellidos("Pérez")
                    .telefono("3001234567")
                    .build();
            var actualizacion = ActualizarPersonaRequestDTO.builder().telefono("3001234567").build();

            assertTrue(validator.validate(alta).isEmpty());
            assertTrue(validator.validate(autoregistro).stream()
                    .anyMatch(error -> error.getPropertyPath().toString().equals("password")));
            assertTrue(validator.validate(actualizacion).isEmpty());
        }
    }
}
