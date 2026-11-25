package org.jhanp_dev.psraerobicosbackend.shared.exception;

public class DatabaseUnavailableException extends RuntimeException {

    public DatabaseUnavailableException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
