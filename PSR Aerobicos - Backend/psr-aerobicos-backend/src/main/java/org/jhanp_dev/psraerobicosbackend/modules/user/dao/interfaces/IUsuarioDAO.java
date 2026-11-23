package org.jhanp_dev.psraerobicosbackend.modules.user.dao.interfaces;

import org.jhanp_dev.psraerobicosbackend.modules.user.dto.request.UsuarioRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.user.dto.response.UsuarioResponseDTO;

import java.util.List;
import java.util.Optional;

public interface IUsuarioDAO {
    List<UsuarioResponseDTO> listarTodos();
    List<UsuarioResponseDTO> buscar(String termino);
    Optional<UsuarioResponseDTO> obtenerPorId(Integer id);
    Optional<UsuarioResponseDTO> obtenerPorCorreo(String correo);
    Optional<UsuarioResponseDTO> obtenerPorNumeroDocumento(String numeroDocumento);
    /** Recibe siempre un hash calculado por el service y delega el alta al procedimiento almacenado. */
    UsuarioResponseDTO crearCuenta(Integer personaId, String passwordHash, String correo);
    boolean actualizar(Integer id, UsuarioRequestDTO dto);
    boolean eliminar(Integer id);
}
