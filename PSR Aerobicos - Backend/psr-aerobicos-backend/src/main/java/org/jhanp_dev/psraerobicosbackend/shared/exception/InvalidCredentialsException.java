package org.jhanp_dev.psraerobicosbackend.shared.exception;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Documento/correo o contraseña incorrectos");
    }
}
