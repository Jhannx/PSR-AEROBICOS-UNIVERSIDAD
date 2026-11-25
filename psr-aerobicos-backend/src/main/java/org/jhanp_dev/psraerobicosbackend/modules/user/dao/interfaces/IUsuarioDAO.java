package org.jhanp_dev.psraerobicosbackend.modules.user.dao.interfaces;

import org.jhanp_dev.psraerobicosbackend.modules.user.dto.response.UsuarioResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.user.entity.Usuario;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IUsuarioDAO {
    List<UsuarioResponseDTO> listarPaginado(String termino, Pageable pageable);
    long contar(String termino);
    List<UsuarioResponseDTO> listarProfesoresPaginado(Pageable pageable);
    long contarProfesores();
    Optional<UsuarioResponseDTO> obtenerPorId(Integer id);
    Optional<UsuarioResponseDTO> obtenerPorCorreo(String correo);
    Optional<UsuarioResponseDTO> obtenerPorNumeroDocumento(String numeroDocumento);
    Optional<Usuario> obtenerParaAutenticacion(String identificador);
    /** Recibe siempre un hash calculado por el service y delega el alta al procedimiento almacenado. */
    UsuarioResponseDTO crearCuenta(Integer personaId, String passwordHash, String correo);
}
