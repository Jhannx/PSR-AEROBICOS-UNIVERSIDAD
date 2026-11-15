package org.jhanp_dev.psraerobicosbackend.modules.person.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jhanp_dev.psraerobicosbackend.modules.person.enums.FuenteRegistro;
import org.jhanp_dev.psraerobicosbackend.modules.person.enums.TipoDocumento;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonaRequestDTO {
    private TipoDocumento tipoDocumento;
    private String numeroDocumento;
    private String nombre;
    private String apellidos;
    private String telefono;
    private LocalDate fechaNacimiento;
    private String observacionesSalud;
    private FuenteRegistro fuenteRegistro;
    private Boolean activo;
}

