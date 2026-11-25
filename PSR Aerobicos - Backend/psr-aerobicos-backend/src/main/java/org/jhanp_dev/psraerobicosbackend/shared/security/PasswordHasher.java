package org.jhanp_dev.psraerobicosbackend.shared.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class PasswordHasher {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String hash(String contraseña) {
        if (contraseña == null || contraseña.isBlank()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }
        if (contraseña.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException("La contraseña no puede superar 72 bytes en UTF-8");
        }
        return passwordEncoder.encode(contraseña);
    }

    public boolean matches(String contraseña, String hashGuardado) {
        if (contraseña == null || hashGuardado == null || hashGuardado.isBlank()) {
            return false;
        }
        return passwordEncoder.matches(contraseña, hashGuardado);
    }
}
