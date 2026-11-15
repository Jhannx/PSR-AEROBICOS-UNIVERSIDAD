package org.jhanp_dev.psraerobicosbackend.modules.user.dao.interfaces;

import org.jhanp_dev.psraerobicosbackend.modules.user.dto.request.UsuarioRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.user.dto.response.UsuarioResponseDTO;

import java.util.List;
import java.util.Optional;

public interface IUsuarioDAO {
    List<UsuarioResponseDTO> listarTodos();
    Optional<UsuarioResponseDTO> obtenerPorId(Integer id);
    Optional<UsuarioResponseDTO> obtenerPorCorreo(String correo);
    Optional<UsuarioResponseDTO> obtenerPorNumeroDocumento(String numeroDocumento);
    UsuarioResponseDTO guardar(UsuarioRequestDTO dto);
    boolean actualizar(Integer id, UsuarioRequestDTO dto);
    boolean eliminar(Integer id);
}
