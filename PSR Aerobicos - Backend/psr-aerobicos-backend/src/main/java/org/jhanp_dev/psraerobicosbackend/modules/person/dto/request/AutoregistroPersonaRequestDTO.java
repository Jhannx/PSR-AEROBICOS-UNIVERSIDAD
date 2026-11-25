package org.jhanp_dev.psraerobicosbackend.modules.person.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jhanp_dev.psraerobicosbackend.modules.person.enums.TipoDocumento;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AutoregistroPersonaRequestDTO {
    @NotNull(message = "El tipo de documento es obligatorio")
    private TipoDocumento tipoDocumento;
    @NotBlank(message = "El número de documento es obligatorio")
    @Size(max = 20, message = "El documento no puede superar 20 caracteres")
    @Pattern(regexp = "[0-9]+", message = "El documento solo puede contener dígitos")
    private String numeroDocumento;
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String nombre;
    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 100, message = "Los apellidos no pueden superar 100 caracteres")
    private String apellidos;
    @NotBlank(message = "El teléfono es obligatorio")
    @Size(max = 20, message = "El teléfono no puede superar 20 caracteres")
    @Pattern(regexp = "[0-9]+", message = "El teléfono solo puede contener dígitos")
    private String telefono;
    @Past(message = "La fecha de nacimiento debe ser anterior a hoy")
    private LocalDate fechaNacimiento;
    private String observacionesSalud;
    private Boolean activo;
    @Email(message = "El correo no tiene un formato válido")
    @Size(max = 150, message = "El correo no puede superar 150 caracteres")
    private String correo;
    @ToString.Exclude
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(max = 72, message = "La contraseña no puede superar 72 caracteres")
    private String password;
    @Size(max = 150, message = "El contacto no puede superar 150 caracteres")
    private String contactoNombre;
    @Size(max = 20, message = "El teléfono del contacto no puede superar 20 caracteres")
    @Pattern(regexp = "[0-9]+", message = "El teléfono del contacto solo puede contener dígitos")
    private String contactoTelefono;
    private String contactoObservaciones;
}
