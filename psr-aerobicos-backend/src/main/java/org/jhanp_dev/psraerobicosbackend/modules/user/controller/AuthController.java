package org.jhanp_dev.psraerobicosbackend.modules.user.controller;

import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.jhanp_dev.psraerobicosbackend.modules.user.dto.request.LoginRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.user.dto.response.UsuarioResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.user.service.interfaces.IUsuarioService;
import org.jhanp_dev.psraerobicosbackend.shared.response.ApiResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IUsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO<UsuarioResponseDTO>> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(ApiResponseDTO.ok(usuarioService.autenticar(request)));
    }
}
