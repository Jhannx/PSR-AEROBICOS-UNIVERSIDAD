package org.jhanp_dev.psraerobicosbackend.modules.aerobics.history.controller;

import lombok.RequiredArgsConstructor;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.history.dto.response.HistorialAuditoriaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.history.service.interfaces.IHistorialAuditoriaService;
import org.jhanp_dev.psraerobicosbackend.shared.response.ApiResponseDTO;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auditoria")
@RequiredArgsConstructor
public class HistorialAuditoriaController {

    private final IHistorialAuditoriaService historialAuditoriaService;

    @GetMapping("/historial-cambios")
    public ResponseEntity<ApiResponseDTO<PagedModel<HistorialAuditoriaResponseDTO>>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponseDTO.ok(
                new PagedModel<>(historialAuditoriaService.listar(page, size))));
    }
}
