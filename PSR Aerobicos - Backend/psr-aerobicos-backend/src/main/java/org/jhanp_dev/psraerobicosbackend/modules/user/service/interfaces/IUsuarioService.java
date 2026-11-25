package org.jhanp_dev.psraerobicosbackend.modules.user.service.interfaces;

import org.jhanp_dev.psraerobicosbackend.modules.user.dto.request.CrearUsuarioRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.user.dto.request.LoginRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.user.dto.request.UsuarioRolRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.user.dto.response.UsuarioResponseDTO;
import org.springframework.data.domain.Page;

public interface IUsuarioService {
    UsuarioResponseDTO autenticar(LoginRequestDTO request);
    Page<UsuarioResponseDTO> listar(String buscar, int page, int size);
    Page<UsuarioResponseDTO> listarProfesores(int page, int size);
    /** Crea cuenta para persona existente usando su documento como clave inicial. */
    UsuarioResponseDTO crear(CrearUsuarioRequestDTO request);
    void asignarRol(Integer usuarioId, UsuarioRolRequestDTO request);
    boolean retirarRol(Integer usuarioId, String rolNombre);
}
