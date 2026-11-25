package org.jhanp_dev.psraerobicosbackend.modules.aerobics.pricing.controller;

import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.pricing.dto.request.TarifaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.pricing.dto.response.TarifaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.pricing.service.interfaces.ITarifaService;
import org.jhanp_dev.psraerobicosbackend.shared.response.ApiResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tarifa")
@RequiredArgsConstructor
public class TarifaController {

    private final ITarifaService tarifaService;

    @GetMapping
    public ResponseEntity<ApiResponseDTO<TarifaResponseDTO>> obtenerVigente() {
        return ResponseEntity.ok(ApiResponseDTO.ok(tarifaService.obtenerVigente()));
    }

    @PutMapping
    public ResponseEntity<ApiResponseDTO<TarifaResponseDTO>> actualizar(@Valid @RequestBody TarifaRequestDTO request) {
        return ResponseEntity.ok(ApiResponseDTO.ok(tarifaService.actualizar(request)));
    }
}
