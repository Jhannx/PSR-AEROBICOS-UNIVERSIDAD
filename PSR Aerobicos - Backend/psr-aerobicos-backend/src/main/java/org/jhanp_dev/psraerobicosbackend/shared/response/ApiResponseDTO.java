package org.jhanp_dev.psraerobicosbackend.shared.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseDTO<T> {
    private int codigo;
    private String mensaje;
    private Integer totalRegistros;
    private T datos;
    private LocalDateTime fechaHora;

    public static <T> ApiResponseDTO<T> ok(T datos) {
        return ApiResponseDTO.<T>builder()
                .codigo(200)
                .mensaje("OK")
                .datos(datos)
                .fechaHora(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponseDTO<List<T>> okLista(List<T> datos) {
        return ApiResponseDTO.<List<T>>builder()
                .codigo(200)
                .mensaje("OK")
                .totalRegistros(datos != null ? datos.size() : 0)
                .datos(datos)
                .fechaHora(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponseDTO<T> creado(T datos) {
        return ApiResponseDTO.<T>builder()
                .codigo(201)
                .mensaje("Recurso creado exitosamente")
                .datos(datos)
                .fechaHora(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponseDTO<T> error(int codigo, String mensaje) {
        return ApiResponseDTO.<T>builder()
                .codigo(codigo)
                .mensaje(mensaje)
                .fechaHora(LocalDateTime.now())
                .build();
    }
}

