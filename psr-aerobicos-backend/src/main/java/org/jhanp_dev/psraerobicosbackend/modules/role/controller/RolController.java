package org.jhanp_dev.psraerobicosbackend.modules.role.controller;

import lombok.RequiredArgsConstructor;
import org.jhanp_dev.psraerobicosbackend.modules.role.dto.response.RolResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.role.service.interfaces.IRolService;
import org.jhanp_dev.psraerobicosbackend.shared.response.ApiResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RolController {

    private final IRolService rolService;

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<RolResponseDTO>>> listar() {
        return ResponseEntity.ok(ApiResponseDTO.okLista(rolService.listarTodos()));
    }
}
