package org.jhanp_dev.psraerobicosbackend.modules.user.service.interfaces;

import org.jhanp_dev.psraerobicosbackend.modules.user.dto.request.UsuarioRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.user.dto.request.UsuarioRolRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.user.dto.response.UsuarioResponseDTO;

import java.util.List;
import java.util.Optional;

public interface IUsuarioService {
    UsuarioResponseDTO autenticar(UsuarioRequestDTO request);
    List<UsuarioResponseDTO> listar(String buscar);
    List<UsuarioResponseDTO> listarProfesores();
    /** Crea cuenta para persona existente; ignora cualquier clave enviada y usa el documento como clave inicial. */
    UsuarioResponseDTO crear(UsuarioRequestDTO request);
    void asignarRol(Integer usuarioId, UsuarioRolRequestDTO request);
    boolean retirarRol(Integer usuarioId, String rolNombre);
}
