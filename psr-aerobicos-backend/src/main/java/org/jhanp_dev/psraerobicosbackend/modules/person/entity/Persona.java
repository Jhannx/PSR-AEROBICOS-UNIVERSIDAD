package org.jhanp_dev.psraerobicosbackend.modules.person.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jhanp_dev.psraerobicosbackend.modules.person.enums.FuenteRegistro;
import org.jhanp_dev.psraerobicosbackend.modules.person.enums.TipoDocumento;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Persona {
    private Integer id;
    private TipoDocumento tipoDocumento;
    private String numeroDocumento;
    private String nombre;
    private String apellidos;
    private String telefono;
    private LocalDate fechaNacimiento;
    private String observacionesSalud;
    private FuenteRegistro fuenteRegistro;
    private LocalDateTime fechaRegistro;
    private Boolean activo;
}

