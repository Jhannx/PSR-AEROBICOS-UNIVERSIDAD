package org.jhanp_dev.psraerobicosbackend.modules.person.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jhanp_dev.psraerobicosbackend.modules.person.dao.interfaces.IPersonaDAO;
import org.jhanp_dev.psraerobicosbackend.modules.person.dto.request.ActualizarPersonaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.person.dto.request.AutoregistroPersonaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.person.dto.request.CrearPersonaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.person.dto.response.PersonaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.person.dto.response.SaldoPersonaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.person.enums.FuenteRegistro;
import org.jhanp_dev.psraerobicosbackend.modules.person.service.interfaces.IPersonaService;
import org.jhanp_dev.psraerobicosbackend.modules.user.dao.interfaces.IUsuarioDAO;
import org.jhanp_dev.psraerobicosbackend.shared.exception.ConflictException;
import org.jhanp_dev.psraerobicosbackend.shared.exception.InvalidDataException;
import org.jhanp_dev.psraerobicosbackend.shared.exception.ResourceNotFoundException;
import org.jhanp_dev.psraerobicosbackend.shared.security.PasswordHasher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonaService implements IPersonaService {

    private final IPersonaDAO personaDAO;
    private final IUsuarioDAO usuarioDAO;
    private final PasswordHasher passwordHasher;

    @Override
    public Page<PersonaResponseDTO> listar(String buscar, int page, int size) {
        if (page < 0 || size < 1 || size > 100) {
            throw new InvalidDataException("La página debe ser mayor o igual a cero y el tamaño debe estar entre 1 y 100");
        }
        Pageable pageable = PageRequest.of(page, size);
        List<PersonaResponseDTO> personas = personaDAO.listarPaginado(buscar, pageable);
        return new PageImpl<>(personas, pageable, personaDAO.contar(buscar));
    }

    @Override
    public Optional<PersonaResponseDTO> obtener(Integer id) {
        validarId(id);
        return personaDAO.obtenerPorId(id);
    }

    @Override
    public Optional<SaldoPersonaResponseDTO> obtenerSaldo(Integer id) {
        validarId(id);
        return personaDAO.obtenerSaldo(id);
    }

    @Override
    public PersonaResponseDTO crearComoAdmin(CrearPersonaRequestDTO request) {
        validarCreacion(request);
        validarDocumentoDisponible(request.getNumeroDocumento());
        validarCorreoDisponible(request.getCorreo(), null);
        log.debug("Registrando persona desde administración");
        return personaDAO.crearConContacto(request, FuenteRegistro.ADMIN,
                passwordHasher.hash(request.getNumeroDocumento()));
    }

    @Override
    public PersonaResponseDTO autoregistrar(AutoregistroPersonaRequestDTO request) {
        validarSolicitud(request, "Los datos de la persona son obligatorios");
        CrearPersonaRequestDTO datosPersona = crearDesdeAutoregistro(request);
        validarCreacion(datosPersona);
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new InvalidDataException("La contraseña es obligatoria para el autorregistro");
        }
        validarDocumentoDisponible(datosPersona.getNumeroDocumento());
        validarCorreoDisponible(datosPersona.getCorreo(), null);
        log.debug("Autoregistrando persona");
        return personaDAO.crearConContacto(datosPersona, FuenteRegistro.AUTOREGISTRO,
                passwordHasher.hash(request.getPassword()));
    }

    @Override
    public PersonaResponseDTO actualizar(Integer id, ActualizarPersonaRequestDTO request) {
        validarId(id);
        validarActualizacion(request);
        if (request.getNumeroDocumento() != null) {
            validarDocumentoDisponibleParaActualizacion(id, request.getNumeroDocumento());
        }
        if (request.getCorreo() != null) validarCorreoDisponible(request.getCorreo(), id);
        if (!personaDAO.actualizar(id, request)) {
            throw new ResourceNotFoundException("No se encontró la persona con ID: " + id);
        }
        log.debug("Persona {} y contacto de emergencia actualizados", id);
        return personaDAO.obtenerPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la persona con ID: " + id));
    }

    @Override
    public boolean cambiarEstado(Integer id, boolean activo) {
        validarId(id);
        log.debug("Cambiando estado de la persona {} a {}", id, activo);
        return personaDAO.cambiarEstado(id, activo);
    }

    private void validarCreacion(CrearPersonaRequestDTO request) {
        validarSolicitud(request, "Los datos de la persona son obligatorios");
        normalizarCreacion(request);
        validarCamposObligatorios(request);
        validarFormatoYLongitudes(request);
        validarContactoEmergencia(request);
    }

    private void validarActualizacion(ActualizarPersonaRequestDTO request) {
        validarSolicitud(request, "Los datos para actualizar la persona son obligatorios");
        if (request.getPassword() != null) {
            throw new InvalidDataException("La contraseña no se actualiza en este endpoint");
        }
        normalizarActualizacion(request);
        validarCamposNoVacios(request);
        validarFormatoYLongitudes(request);
        if (sinCamposActualizables(request)) {
            throw new InvalidDataException("Debe indicar al menos un campo para actualizar");
        }
    }

    private void validarSolicitud(Object request, String mensaje) {
        if (request == null) throw new InvalidDataException(mensaje);
    }

    private CrearPersonaRequestDTO crearDesdeAutoregistro(AutoregistroPersonaRequestDTO request) {
        return CrearPersonaRequestDTO.builder()
                .tipoDocumento(request.getTipoDocumento())
                .numeroDocumento(request.getNumeroDocumento())
                .nombre(request.getNombre())
                .apellidos(request.getApellidos())
                .telefono(request.getTelefono())
                .fechaNacimiento(request.getFechaNacimiento())
                .observacionesSalud(request.getObservacionesSalud())
                .activo(request.getActivo())
                .correo(request.getCorreo())
                .contactoNombre(request.getContactoNombre())
                .contactoTelefono(request.getContactoTelefono())
                .contactoObservaciones(request.getContactoObservaciones())
                .build();
    }

    private void normalizarCreacion(CrearPersonaRequestDTO request) {
        request.setNumeroDocumento(trim(request.getNumeroDocumento()));
        request.setNombre(trim(request.getNombre()));
        request.setApellidos(trim(request.getApellidos()));
        request.setTelefono(trim(request.getTelefono()));
        request.setCorreo(normalizarCorreo(normalizarOpcional(request.getCorreo())));
        request.setObservacionesSalud(normalizarOpcional(request.getObservacionesSalud()));
        request.setContactoNombre(normalizarOpcional(request.getContactoNombre()));
        request.setContactoTelefono(normalizarOpcional(request.getContactoTelefono()));
        request.setContactoObservaciones(normalizarOpcional(request.getContactoObservaciones()));
    }

    private void normalizarActualizacion(ActualizarPersonaRequestDTO request) {
        request.setNumeroDocumento(trim(request.getNumeroDocumento()));
        request.setNombre(trim(request.getNombre()));
        request.setApellidos(trim(request.getApellidos()));
        request.setTelefono(trim(request.getTelefono()));
        request.setCorreo(normalizarCorreo(trim(request.getCorreo())));
        request.setObservacionesSalud(trim(request.getObservacionesSalud()));
        request.setContactoNombre(trim(request.getContactoNombre()));
        request.setContactoTelefono(trim(request.getContactoTelefono()));
        request.setContactoObservaciones(trim(request.getContactoObservaciones()));
    }

    private void validarCamposObligatorios(CrearPersonaRequestDTO request) {
        if (request.getTipoDocumento() == null) {
            throw new InvalidDataException("El tipo de documento es obligatorio");
        }
        validarObligatorio(request.getNumeroDocumento(), "El número de documento es obligatorio");
        validarObligatorio(request.getNombre(), "El nombre es obligatorio");
        validarObligatorio(request.getApellidos(), "Los apellidos son obligatorios");
        validarObligatorio(request.getTelefono(), "El teléfono es obligatorio");
    }

    private void validarCamposNoVacios(ActualizarPersonaRequestDTO request) {
        validarNoVacioSiInformado(request.getNumeroDocumento(), "El número de documento no puede estar vacío");
        validarNoVacioSiInformado(request.getNombre(), "El nombre no puede estar vacío");
        validarNoVacioSiInformado(request.getApellidos(), "Los apellidos no pueden estar vacíos");
        validarNoVacioSiInformado(request.getTelefono(), "El teléfono no puede estar vacío");
        validarNoVacioSiInformado(request.getCorreo(), "El correo no puede estar vacío");
        validarNoVacioSiInformado(request.getContactoNombre(), "El nombre del contacto no puede estar vacío");
        validarNoVacioSiInformado(request.getContactoTelefono(), "El teléfono del contacto no puede estar vacío");
    }

    private void validarFormatoYLongitudes(CrearPersonaRequestDTO request) {
        validarSoloDigitos(request.getNumeroDocumento(), "El número de documento");
        validarSoloDigitos(request.getTelefono(), "El teléfono");
        validarSoloDigitos(request.getContactoTelefono(), "El teléfono del contacto");
        validarLongitud(request.getNumeroDocumento(), 20, "El número de documento");
        validarLongitud(request.getNombre(), 100, "El nombre");
        validarLongitud(request.getApellidos(), 100, "Los apellidos");
        validarLongitud(request.getTelefono(), 20, "El teléfono");
        validarLongitud(request.getCorreo(), 150, "El correo");
        validarLongitud(request.getContactoNombre(), 150, "El nombre del contacto");
        validarLongitud(request.getContactoTelefono(), 20, "El teléfono del contacto");
    }

    private void validarFormatoYLongitudes(ActualizarPersonaRequestDTO request) {
        validarSoloDigitos(request.getNumeroDocumento(), "El número de documento");
        validarSoloDigitos(request.getTelefono(), "El teléfono");
        validarSoloDigitos(request.getContactoTelefono(), "El teléfono del contacto");
        validarLongitud(request.getNumeroDocumento(), 20, "El número de documento");
        validarLongitud(request.getNombre(), 100, "El nombre");
        validarLongitud(request.getApellidos(), 100, "Los apellidos");
        validarLongitud(request.getTelefono(), 20, "El teléfono");
        validarLongitud(request.getCorreo(), 150, "El correo");
        validarLongitud(request.getContactoNombre(), 150, "El nombre del contacto");
        validarLongitud(request.getContactoTelefono(), 20, "El teléfono del contacto");
    }

    private void validarContactoEmergencia(CrearPersonaRequestDTO request) {
        if (request.getContactoNombre() == null && request.getContactoTelefono() == null
                && request.getContactoObservaciones() == null) {
            throw new InvalidDataException("Debe indicar al menos un dato del contacto de emergencia");
        }
    }

    private boolean sinCamposActualizables(ActualizarPersonaRequestDTO request) {
        return request.getTipoDocumento() == null && request.getNumeroDocumento() == null
                && request.getNombre() == null && request.getApellidos() == null
                && request.getTelefono() == null && request.getFechaNacimiento() == null
                && request.getObservacionesSalud() == null && request.getActivo() == null
                && request.getCorreo() == null && request.getContactoNombre() == null
                && request.getContactoTelefono() == null && request.getContactoObservaciones() == null;
    }

    private void validarObligatorio(String texto, String mensaje) {
        if (texto == null || texto.isBlank()) throw new InvalidDataException(mensaje);
    }

    private void validarNoVacioSiInformado(String texto, String mensaje) {
        if (texto != null && texto.isBlank()) throw new InvalidDataException(mensaje);
    }

    private void validarSoloDigitos(String texto, String campo) {
        if (texto != null && !texto.matches("[0-9]+")) {
            throw new InvalidDataException(campo + " solo puede contener dígitos");
        }
    }

    private void validarLongitud(String texto, int limite, String campo) {
        if (texto != null && texto.length() > limite) {
            throw new InvalidDataException(campo + " no puede superar " + limite + " caracteres");
        }
    }

    private String normalizarCorreo(String correo) {
        return correo == null ? null : correo.toLowerCase(java.util.Locale.ROOT);
    }

    private String normalizarOpcional(String texto) {
        return texto == null || texto.isBlank() ? null : texto.trim();
    }

    private String trim(String texto) {
        return texto == null ? null : texto.trim();
    }

    private void validarDocumentoDisponible(String numeroDocumento) {
        if (personaDAO.obtenerPorNumeroDocumento(numeroDocumento).isPresent()) {
            throw new ConflictException("Ya existe una persona con ese número de documento");
        }
        if (usuarioDAO.obtenerPorNumeroDocumento(numeroDocumento).isPresent()) {
            throw new ConflictException("Ya existe una cuenta con ese número de documento");
        }
    }

    private void validarDocumentoDisponibleParaActualizacion(Integer id, String numeroDocumento) {
        personaDAO.obtenerPorNumeroDocumento(numeroDocumento)
                .filter(persona -> !id.equals(persona.getId()))
                .ifPresent(persona -> {
                    throw new ConflictException("Ya existe una persona con ese número de documento");
                });
        usuarioDAO.obtenerPorNumeroDocumento(numeroDocumento)
                .filter(usuario -> !id.equals(usuario.getPersonaId()))
                .ifPresent(usuario -> {
                    throw new ConflictException("Ya existe una cuenta con ese número de documento");
                });
    }

    private void validarCorreoDisponible(String correo, Integer personaIdActual) {
        if (correo == null) return;
        usuarioDAO.obtenerPorCorreo(correo)
                .filter(usuario -> personaIdActual == null || !personaIdActual.equals(usuario.getPersonaId()))
                .ifPresent(usuario -> {
                    throw new ConflictException("Ya existe una cuenta con ese correo");
                });
    }

    private void validarId(Integer id) {
        if (id == null || id < 1) {
            throw new InvalidDataException("El ID de la persona debe ser mayor que cero");
        }
    }
}
