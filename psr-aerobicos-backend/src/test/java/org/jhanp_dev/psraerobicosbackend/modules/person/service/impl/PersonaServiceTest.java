package org.jhanp_dev.psraerobicosbackend.modules.person.service.impl;

import org.jhanp_dev.psraerobicosbackend.modules.person.dao.interfaces.IPersonaDAO;
import org.jhanp_dev.psraerobicosbackend.modules.person.dto.request.ActualizarPersonaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.person.dto.request.AutoregistroPersonaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.person.dto.request.CrearPersonaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.person.dto.response.PersonaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.person.enums.FuenteRegistro;
import org.jhanp_dev.psraerobicosbackend.modules.person.enums.TipoDocumento;
import org.jhanp_dev.psraerobicosbackend.modules.user.dao.interfaces.IUsuarioDAO;
import org.jhanp_dev.psraerobicosbackend.shared.exception.ConflictException;
import org.jhanp_dev.psraerobicosbackend.shared.exception.InvalidDataException;
import org.jhanp_dev.psraerobicosbackend.shared.security.PasswordHasher;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PersonaServiceTest {

    @Test
    void aplicaLaContraseñaCorrespondienteACadaRegistro() {
        IPersonaDAO personaDAO = mock(IPersonaDAO.class);
        IUsuarioDAO usuarioDAO = mock(IUsuarioDAO.class);
        PasswordHasher passwordHasher = new PasswordHasher();
        PersonaService personaService = new PersonaService(personaDAO, usuarioDAO, passwordHasher);
        when(personaDAO.obtenerPorNumeroDocumento(any())).thenReturn(Optional.empty());
        when(usuarioDAO.obtenerPorNumeroDocumento(any())).thenReturn(Optional.empty());
        when(personaDAO.crearConContacto(any(), any(), any()))
                .thenReturn(PersonaResponseDTO.builder().id(1).build());

        CrearPersonaRequestDTO altaAdmin = solicitud("123456");
        personaService.crearComoAdmin(altaAdmin);
        ArgumentCaptor<String> hashAdmin = ArgumentCaptor.forClass(String.class);
        verify(personaDAO).crearConContacto(eq(altaAdmin), eq(FuenteRegistro.ADMIN), hashAdmin.capture());
        assertTrue(passwordHasher.matches("123456", hashAdmin.getValue()));
        assertNull(altaAdmin.getContactoObservaciones());

        AutoregistroPersonaRequestDTO autoregistro = AutoregistroPersonaRequestDTO.builder()
                .tipoDocumento(TipoDocumento.CC)
                .numeroDocumento("789012")
                .nombre("Ana")
                .apellidos("Pérez")
                .telefono("3001234567")
                .contactoNombre("Luis Pérez")
                .contactoTelefono("3101234567")
                .password("claveElegida")
                .build();
        personaService.autoregistrar(autoregistro);
        ArgumentCaptor<CrearPersonaRequestDTO> datosAutoregistro =
                ArgumentCaptor.forClass(CrearPersonaRequestDTO.class);
        ArgumentCaptor<String> hashAutoregistro = ArgumentCaptor.forClass(String.class);
        verify(personaDAO).crearConContacto(datosAutoregistro.capture(), eq(FuenteRegistro.AUTOREGISTRO),
                hashAutoregistro.capture());
        assertEquals("789012", datosAutoregistro.getValue().getNumeroDocumento());
        assertTrue(passwordHasher.matches("claveElegida", hashAutoregistro.getValue()));
    }

    @Test
    void permiteActualizarSoloElContactoDeEmergencia() {
        IPersonaDAO personaDAO = mock(IPersonaDAO.class);
        PersonaService personaService = new PersonaService(personaDAO, mock(IUsuarioDAO.class), new PasswordHasher());
        ActualizarPersonaRequestDTO contacto = ActualizarPersonaRequestDTO.builder()
                .contactoNombre("Marta Pérez")
                .contactoTelefono("3109876543")
                .contactoObservaciones("")
                .build();
        when(personaDAO.actualizar(1, contacto)).thenReturn(true);
        when(personaDAO.obtenerPorId(1)).thenReturn(Optional.of(PersonaResponseDTO.builder().id(1).build()));

        personaService.actualizar(1, contacto);

        verify(personaDAO).actualizar(1, contacto);
    }

    @Test
    void rechazaDocumentoConCaracteresNoNumericos() {
        IPersonaDAO personaDAO = mock(IPersonaDAO.class);
        PersonaService personaService = new PersonaService(personaDAO, mock(IUsuarioDAO.class), new PasswordHasher());

        assertThrows(InvalidDataException.class, () -> personaService.crearComoAdmin(solicitud("12A456")));
    }

    @Test
    void rechazaDocumentoQueYaPerteneceAOtraPersonaAlActualizar() {
        IPersonaDAO personaDAO = mock(IPersonaDAO.class);
        PersonaService personaService = new PersonaService(personaDAO, mock(IUsuarioDAO.class), new PasswordHasher());
        ActualizarPersonaRequestDTO request =
                ActualizarPersonaRequestDTO.builder().numeroDocumento("654321").build();
        when(personaDAO.obtenerPorNumeroDocumento("654321"))
                .thenReturn(Optional.of(PersonaResponseDTO.builder().id(2).build()));

        assertThrows(ConflictException.class, () -> personaService.actualizar(1, request));
    }

    private CrearPersonaRequestDTO solicitud(String documento) {
        return CrearPersonaRequestDTO.builder()
                .tipoDocumento(TipoDocumento.CC)
                .numeroDocumento(documento)
                .nombre("Ana")
                .apellidos("Pérez")
                .telefono("3001234567")
                .contactoNombre("Luis Pérez")
                .contactoTelefono("3101234567")
                .contactoObservaciones("")
                .build();
    }
}
