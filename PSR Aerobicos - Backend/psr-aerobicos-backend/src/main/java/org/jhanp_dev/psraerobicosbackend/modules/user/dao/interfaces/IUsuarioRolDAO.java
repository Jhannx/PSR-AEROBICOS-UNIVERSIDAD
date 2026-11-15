package org.jhanp_dev.psraerobicosbackend.modules.user.dao.interfaces;

import org.jhanp_dev.psraerobicosbackend.modules.user.dto.request.UsuarioRolRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.user.dto.response.UsuarioRolResponseDTO;

import java.util.List;
import java.util.Optional;

public interface IUsuarioRolDAO {
    List<UsuarioRolResponseDTO> listarTodos();
    Optional<UsuarioRolResponseDTO> obtenerPorId(Integer id);
    List<UsuarioRolResponseDTO> listarPorUsuarioId(Integer usuarioId);
    UsuarioRolResponseDTO guardar(UsuarioRolRequestDTO dto);
    boolean actualizar(Integer id, UsuarioRolRequestDTO dto);
    boolean eliminar(Integer id);
}
