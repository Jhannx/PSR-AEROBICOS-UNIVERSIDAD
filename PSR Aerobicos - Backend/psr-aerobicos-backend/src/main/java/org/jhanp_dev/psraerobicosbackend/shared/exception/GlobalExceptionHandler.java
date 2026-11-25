package org.jhanp_dev.psraerobicosbackend.shared.exception;

import org.jhanp_dev.psraerobicosbackend.shared.response.ApiResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleRequestValidation(MethodArgumentNotValidException ex) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Revisa los datos enviados.");
        return ResponseEntity.badRequest()
                .body(ApiResponseDTO.error(HttpStatus.BAD_REQUEST.value(), mensaje));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleUnreadableRequest(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest()
                .body(ApiResponseDTO.error(HttpStatus.BAD_REQUEST.value(),
                        "El cuerpo enviado tiene un formato inválido o un tipo de dato incorrecto."));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleInvalidCredentials(InvalidCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponseDTO.error(HttpStatus.UNAUTHORIZED.value(), ex.getMessage()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseDTO.error(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
    }

    @ExceptionHandler({InvalidDataException.class, IllegalArgumentException.class})
    public ResponseEntity<ApiResponseDTO<Void>> handleInvalidData(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDTO.error(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleConflict(ConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponseDTO.error(HttpStatus.CONFLICT.value(), ex.getMessage()));
    }

    @ExceptionHandler(DatabaseUnavailableException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleDatabaseUnavailable(DatabaseUnavailableException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponseDTO.error(
                        HttpStatus.SERVICE_UNAVAILABLE.value(),
                        "La base de datos no está disponible. Intenta de nuevo en unos minutos."
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleGeneral(Exception ex) {
        if (esDuplicadoUnico(ex)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponseDTO.error(HttpStatus.CONFLICT.value(),
                            "Ya existe un registro con uno de esos datos."));
        }
        if (esErrorDeConexion(ex)) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(ApiResponseDTO.error(
                            HttpStatus.SERVICE_UNAVAILABLE.value(),
                            "La base de datos no está disponible. Intenta de nuevo en unos minutos."
                    ));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Ocurrió un error interno. Intenta de nuevo más tarde."
                ));
    }

    private boolean esDuplicadoUnico(Throwable error) {
        Throwable causa = error;
        while (causa != null) {
            if (causa instanceof SQLException sqlException && sqlException.getErrorCode() == 1062) {
                return true;
            }
            causa = causa.getCause();
        }
        return false;
    }

    private boolean esErrorDeConexion(Throwable error) {
        Throwable causa = error;
        while (causa != null) {
            if (causa instanceof SQLException sqlException
                    && sqlException.getSQLState() != null
                    && sqlException.getSQLState().startsWith("08")) {
                return true;
            }
            causa = causa.getCause();
        }
        return false;
    }
}
