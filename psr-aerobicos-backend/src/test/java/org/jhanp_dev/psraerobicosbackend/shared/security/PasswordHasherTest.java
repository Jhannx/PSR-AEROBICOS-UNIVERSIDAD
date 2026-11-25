package org.jhanp_dev.psraerobicosbackend.shared.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordHasherTest {

    @Test
    void generaSalDistintaYVerificaLaContraseña() {
        PasswordHasher passwordHasher = new PasswordHasher();

        String primerHash = passwordHasher.hash("contraseñaElegida");
        String segundoHash = passwordHasher.hash("contraseñaElegida");

        assertNotEquals(primerHash, segundoHash);
        assertTrue(primerHash.length() <= 255);
        assertTrue(passwordHasher.matches("contraseñaElegida", primerHash));
        assertFalse(passwordHasher.matches("otraContraseña", primerHash));
        assertFalse(passwordHasher.matches("contraseñaElegida", "hash_invalido"));
    }
}
