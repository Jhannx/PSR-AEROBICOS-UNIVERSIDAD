package org.jhanp_dev.psraerobicosbackend.shared.exception;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void devuelveErrorAmigableSiLaConexionSePierdeDuranteUnaConsulta() {
        SQLException causa = new SQLException("Detalle interno de MySQL", "08S01");

        var respuesta = handler.handleGeneral(new IllegalStateException("Error de consulta", causa));

        assertEquals(503, respuesta.getStatusCode().value());
        assertEquals(503, respuesta.getBody().getCodigo());
        assertFalse(respuesta.getBody().getMensaje().contains("MySQL"));
    }

    @Test
    void noExponeElDetalleDeUnErrorInterno() {
        var respuesta = handler.handleGeneral(new IllegalStateException("Clave y consulta internas"));

        assertEquals(500, respuesta.getStatusCode().value());
        assertFalse(respuesta.getBody().getMensaje().contains("Clave y consulta internas"));
    }
}
