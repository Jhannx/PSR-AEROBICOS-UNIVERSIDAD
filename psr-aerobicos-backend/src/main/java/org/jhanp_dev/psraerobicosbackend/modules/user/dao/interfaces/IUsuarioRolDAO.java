package org.jhanp_dev.psraerobicosbackend.modules.user.dao.interfaces;

import org.jhanp_dev.psraerobicosbackend.modules.user.dto.response.UsuarioRolResponseDTO;

import java.util.List;
import java.util.Optional;

public interface IUsuarioRolDAO {
    List<UsuarioRolResponseDTO> listarTodos();
    Optional<UsuarioRolResponseDTO> obtenerPorId(Integer id);
    List<UsuarioRolResponseDTO> listarPorUsuarioId(Integer usuarioId);
    boolean existeAsignacion(Integer usuarioId, String rolNombre);
    void asignar(Integer usuarioId, String rolNombre);
    boolean retirar(Integer usuarioId, String rolNombre);
}
