package org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.controller;

import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.dto.request.CheckinManualRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.dto.request.CheckinMiembroRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.dto.response.FilaEsperaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.dto.response.FilaRecepcionResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.service.interfaces.ILlegadaService;
import org.jhanp_dev.psraerobicosbackend.shared.response.ApiResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LlegadaController {

    private final ILlegadaService llegadaService;

    @GetMapping("/cola/admin")
    public ResponseEntity<ApiResponseDTO<List<FilaRecepcionResponseDTO>>> obtenerColaAdmin() {
        return ResponseEntity.ok(ApiResponseDTO.ok(llegadaService.obtenerColaAdmin()));
    }

    @GetMapping("/cola/espera")
    public ResponseEntity<ApiResponseDTO<List<FilaEsperaResponseDTO>>> obtenerColaEspera() {
        return ResponseEntity.ok(ApiResponseDTO.ok(llegadaService.obtenerColaEspera()));
    }

    @PostMapping("/checkin/manual/documento")
    public ResponseEntity<ApiResponseDTO<FilaRecepcionResponseDTO>> registrarManual(
            @Valid @RequestBody CheckinManualRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.creado(llegadaService.registrarCheckinManual(request)));
    }

    @PostMapping("/checkin/documento")
    public ResponseEntity<ApiResponseDTO<FilaRecepcionResponseDTO>> registrarMiembro(
            @Valid @RequestBody CheckinMiembroRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.creado(llegadaService.registrarCheckinMiembro(request)));
    }
}
