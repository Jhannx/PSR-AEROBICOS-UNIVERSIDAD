package org.jhanp_dev.psraerobicosbackend.modules.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {
    @Size(max = 150, message = "El identificador no puede superar 150 caracteres")
    private String identificador;
    @Size(max = 150, message = "El documento o correo no puede superar 150 caracteres")
    private String documento;
    @ToString.Exclude
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(max = 72, message = "La contraseña no puede superar 72 caracteres")
    private String password;
}
