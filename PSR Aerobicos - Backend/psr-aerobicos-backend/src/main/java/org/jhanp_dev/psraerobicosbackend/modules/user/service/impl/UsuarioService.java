package org.jhanp_dev.psraerobicosbackend.modules.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.jhanp_dev.psraerobicosbackend.modules.person.dao.interfaces.IPersonaDAO;
import org.jhanp_dev.psraerobicosbackend.modules.person.dto.response.PersonaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.role.enums.RolNombre;
import org.jhanp_dev.psraerobicosbackend.modules.user.dao.interfaces.IUsuarioDAO;
import org.jhanp_dev.psraerobicosbackend.modules.user.dao.interfaces.IUsuarioRolDAO;
import org.jhanp_dev.psraerobicosbackend.modules.user.dto.request.CrearUsuarioRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.user.dto.request.LoginRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.user.dto.request.UsuarioRolRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.user.dto.response.UsuarioResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.user.entity.Usuario;
import org.jhanp_dev.psraerobicosbackend.modules.user.service.interfaces.IUsuarioService;
import org.jhanp_dev.psraerobicosbackend.shared.exception.ConflictException;
import org.jhanp_dev.psraerobicosbackend.shared.exception.InvalidCredentialsException;
import org.jhanp_dev.psraerobicosbackend.shared.exception.InvalidDataException;
import org.jhanp_dev.psraerobicosbackend.shared.exception.ResourceNotFoundException;
import org.jhanp_dev.psraerobicosbackend.shared.security.PasswordHasher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService implements IUsuarioService {

    private final IUsuarioDAO usuarioDAO;
    private final IUsuarioRolDAO usuarioRolDAO;
    private final IPersonaDAO personaDAO;
    private final PasswordHasher passwordHasher;

    @Override
    public UsuarioResponseDTO autenticar(LoginRequestDTO request) {
        String identificador = validarCredenciales(request);
        Usuario usuario = usuarioDAO.obtenerParaAutenticacion(identificador.trim())
                .orElseThrow(InvalidCredentialsException::new);
        if (!Boolean.TRUE.equals(usuario.getActivo())
                || !passwordHasher.matches(request.getPassword(), usuario.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        if (usuario.getPersonaId() != null) {
            PersonaResponseDTO persona = personaDAO.obtenerPorId(usuario.getPersonaId())
                    .orElseThrow(InvalidCredentialsException::new);
            if (!Boolean.TRUE.equals(persona.getActivo())) {
                throw new InvalidCredentialsException();
            }
        }
        return usuarioDAO.obtenerPorId(usuario.getId())
                .orElseThrow(InvalidCredentialsException::new);
    }

    @Override
    public Page<UsuarioResponseDTO> listar(String buscar, int page, int size) {
        Pageable pageable = validarPagina(page, size);
        List<UsuarioResponseDTO> usuarios = usuarioDAO.listarPaginado(buscar, pageable);
        return new PageImpl<>(usuarios, pageable, usuarioDAO.contar(buscar));
    }

    @Override
    public Page<UsuarioResponseDTO> listarProfesores(int page, int size) {
        Pageable pageable = validarPagina(page, size);
        List<UsuarioResponseDTO> profesores = usuarioDAO.listarProfesoresPaginado(pageable);
        return new PageImpl<>(profesores, pageable, usuarioDAO.contarProfesores());
    }

    @Override
    public UsuarioResponseDTO crear(CrearUsuarioRequestDTO request) {
        validarSolicitudCreacion(request);
        PersonaResponseDTO persona = obtenerPersonaActiva(request.getPersonaId());
        validarDocumentoDisponible(persona.getNumeroDocumento());
        String correo = normalizarCorreo(request.getCorreo());
        validarCorreoDisponible(correo);
        return usuarioDAO.crearCuenta(persona.getId(),
                passwordHasher.hash(persona.getNumeroDocumento()), correo);
    }

    @Override
    public void asignarRol(Integer usuarioId, UsuarioRolRequestDTO request) {
        validarUsuarioId(usuarioId);
        if (request == null) {
            throw new InvalidDataException("El rol es obligatorio");
        }
        String nombre = request.getRolNombre() != null ? request.getRolNombre() : request.getRoleName();
        RolNombre rol = validarRol(nombre);
        usuarioDAO.obtenerPorId(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el usuario indicado"));
        if (usuarioRolDAO.existeAsignacion(usuarioId, rol.name())) {
            throw new ConflictException("El usuario ya tiene asignado ese rol");
        }
        usuarioRolDAO.asignar(usuarioId, rol.name());
    }

    @Override
    public boolean retirarRol(Integer usuarioId, String rolNombre) {
        validarUsuarioId(usuarioId);
        RolNombre rol = validarRol(rolNombre);
        usuarioDAO.obtenerPorId(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el usuario indicado"));
        return usuarioRolDAO.retirar(usuarioId, rol.name());
    }

    private Pageable validarPagina(int page, int size) {
        if (page < 0 || size < 1 || size > 100) {
            throw new InvalidDataException("La página debe ser mayor o igual a cero y el tamaño debe estar entre 1 y 100");
        }
        return PageRequest.of(page, size);
    }

    private String validarCredenciales(LoginRequestDTO request) {
        if (request == null) throw new InvalidDataException("Las credenciales son obligatorias");
        String identificador = request.getIdentificador() != null
                ? request.getIdentificador() : request.getDocumento();
        if (identificador == null || identificador.isBlank()
                || request.getPassword() == null || request.getPassword().isBlank()) {
            throw new InvalidDataException("El documento o correo y la contraseña son obligatorios");
        }
        if (identificador.length() > 150) {
            throw new InvalidDataException("El documento o correo no puede superar 150 caracteres");
        }
        return identificador.trim();
    }

    private void validarSolicitudCreacion(CrearUsuarioRequestDTO request) {
        if (request == null || request.getPersonaId() == null || request.getPersonaId() < 1) {
            throw new InvalidDataException("Debe seleccionar una persona válida");
        }
        if (request.getCorreo() == null || request.getCorreo().isBlank()) {
            throw new InvalidDataException("El correo es obligatorio");
        }
    }

    private PersonaResponseDTO obtenerPersonaActiva(Integer personaId) {
        PersonaResponseDTO persona = personaDAO.obtenerPorId(personaId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la persona indicada"));
        if (!Boolean.TRUE.equals(persona.getActivo())) {
            throw new InvalidDataException("No se puede crear una cuenta para una persona inactiva");
        }
        return persona;
    }

    private void validarDocumentoDisponible(String numeroDocumento) {
        if (usuarioDAO.obtenerPorNumeroDocumento(numeroDocumento).isPresent()) {
            throw new ConflictException("La persona ya tiene una cuenta de usuario");
        }
    }

    private void validarCorreoDisponible(String correo) {
        if (usuarioDAO.obtenerPorCorreo(correo).isPresent()) {
            throw new ConflictException("Ya existe una cuenta con ese correo");
        }
    }

    private String normalizarCorreo(String correo) {
        String normalizado = correo.trim().toLowerCase(java.util.Locale.ROOT);
        if (normalizado.length() > 150 || !normalizado.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new InvalidDataException("El correo no tiene un formato válido");
        }
        return normalizado;
    }

    private void validarUsuarioId(Integer usuarioId) {
        if (usuarioId == null || usuarioId < 1) {
            throw new InvalidDataException("El ID del usuario debe ser mayor que cero");
        }
    }

    private RolNombre validarRol(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new InvalidDataException("El rol es obligatorio");
        }
        try {
            return RolNombre.valueOf(nombre.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new InvalidDataException("El rol indicado no existe");
        }
    }
}
