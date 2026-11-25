package org.jhanp_dev.psraerobicosbackend.modules.user.service.impl;

import org.jhanp_dev.psraerobicosbackend.modules.person.dao.interfaces.IPersonaDAO;
import org.jhanp_dev.psraerobicosbackend.modules.person.dto.response.PersonaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.user.dao.interfaces.IUsuarioDAO;
import org.jhanp_dev.psraerobicosbackend.modules.user.dao.interfaces.IUsuarioRolDAO;
import org.jhanp_dev.psraerobicosbackend.modules.user.dto.request.CrearUsuarioRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.user.dto.request.LoginRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.user.dto.response.UsuarioResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.user.entity.Usuario;
import org.jhanp_dev.psraerobicosbackend.shared.exception.InvalidCredentialsException;
import org.jhanp_dev.psraerobicosbackend.shared.security.PasswordHasher;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UsuarioServiceTest {

    @Test
    void autenticaConBcryptSinExponerElHash() {
        IUsuarioDAO usuarioDAO = mock(IUsuarioDAO.class);
        IUsuarioRolDAO usuarioRolDAO = mock(IUsuarioRolDAO.class);
        IPersonaDAO personaDAO = mock(IPersonaDAO.class);
        PasswordHasher passwordHasher = new PasswordHasher();
        UsuarioService servicio = new UsuarioService(usuarioDAO, usuarioRolDAO, personaDAO, passwordHasher);
        Usuario usuario = Usuario.builder().id(5).personaId(3).activo(true)
                .passwordHash(passwordHasher.hash("claveElegida")).build();
        UsuarioResponseDTO respuesta = UsuarioResponseDTO.builder().id(5)
                .roles(List.of("MIEMBRO")).build();
        when(usuarioDAO.obtenerParaAutenticacion("123456"))
                .thenReturn(Optional.of(usuario));
        when(personaDAO.obtenerPorId(3))
                .thenReturn(Optional.of(PersonaResponseDTO.builder().id(3).activo(true).build()));
        when(usuarioDAO.obtenerPorId(5)).thenReturn(Optional.of(respuesta));

        LoginRequestDTO solicitud = LoginRequestDTO.builder()
                .identificador("123456").password("claveElegida").build();
        assertEquals(respuesta, servicio.autenticar(solicitud));

        solicitud.setPassword("claveIncorrecta");
        assertThrows(InvalidCredentialsException.class, () -> servicio.autenticar(solicitud));
    }

    @Test
    void creaCuentaAdministrativaConDocumentoComoClave() {
        IUsuarioDAO usuarioDAO = mock(IUsuarioDAO.class);
        IUsuarioRolDAO usuarioRolDAO = mock(IUsuarioRolDAO.class);
        IPersonaDAO personaDAO = mock(IPersonaDAO.class);
        PasswordHasher passwordHasher = new PasswordHasher();
        UsuarioService servicio = new UsuarioService(usuarioDAO, usuarioRolDAO, personaDAO, passwordHasher);
        when(personaDAO.obtenerPorId(3)).thenReturn(Optional.of(
                PersonaResponseDTO.builder().id(3).numeroDocumento("123456").activo(true).build()));
        when(usuarioDAO.obtenerPorNumeroDocumento("123456")).thenReturn(Optional.empty());
        when(usuarioDAO.obtenerPorCorreo("ana@correo.com")).thenReturn(Optional.empty());
        when(usuarioDAO.crearCuenta(org.mockito.ArgumentMatchers.eq(3), anyString(),
                org.mockito.ArgumentMatchers.eq("ana@correo.com")))
                .thenReturn(UsuarioResponseDTO.builder().id(5).build());

        CrearUsuarioRequestDTO solicitud = CrearUsuarioRequestDTO.builder()
                .personaId(3).correo("ana@correo.com").build();
        servicio.crear(solicitud);

        ArgumentCaptor<String> hash = ArgumentCaptor.forClass(String.class);
        verify(usuarioDAO).crearCuenta(org.mockito.ArgumentMatchers.eq(3), hash.capture(),
                org.mockito.ArgumentMatchers.eq("ana@correo.com"));
        assertTrue(passwordHasher.matches("123456", hash.getValue()));
    }
}
