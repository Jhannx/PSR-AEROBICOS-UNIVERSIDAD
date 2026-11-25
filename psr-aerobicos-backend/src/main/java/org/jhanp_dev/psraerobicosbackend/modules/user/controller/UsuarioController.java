package org.jhanp_dev.psraerobicosbackend.modules.user.controller;

import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.jhanp_dev.psraerobicosbackend.modules.user.dto.request.CrearUsuarioRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.user.dto.request.UsuarioRolRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.user.dto.response.UsuarioResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.user.service.interfaces.IUsuarioService;
import org.jhanp_dev.psraerobicosbackend.shared.exception.ResourceNotFoundException;
import org.jhanp_dev.psraerobicosbackend.shared.response.ApiResponseDTO;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final IUsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<ApiResponseDTO<PagedModel<UsuarioResponseDTO>>> listar(
            @RequestParam(required = false) String buscar,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponseDTO.ok(new PagedModel<>(usuarioService.listar(buscar, page, size))));
    }

    @GetMapping("/profesores")
    public ResponseEntity<ApiResponseDTO<PagedModel<UsuarioResponseDTO>>> listarProfesores(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponseDTO.ok(new PagedModel<>(usuarioService.listarProfesores(page, size))));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<UsuarioResponseDTO>> crear(
            @Valid @RequestBody CrearUsuarioRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.creado(usuarioService.crear(request)));
    }

    @PostMapping("/{id}/roles")
    public ResponseEntity<ApiResponseDTO<Boolean>> asignarRol(
            @PathVariable Integer id, @Valid @RequestBody UsuarioRolRequestDTO request) {
        usuarioService.asignarRol(id, request);
        return ResponseEntity.ok(ApiResponseDTO.ok(true));
    }

    @DeleteMapping("/{id}/roles/{rolNombre}")
    public ResponseEntity<ApiResponseDTO<Boolean>> retirarRol(
            @PathVariable Integer id, @PathVariable String rolNombre) {
        if (!usuarioService.retirarRol(id, rolNombre)) {
            throw new ResourceNotFoundException("El usuario no tiene asignado ese rol");
        }
        return ResponseEntity.ok(ApiResponseDTO.ok(true));
    }
}
