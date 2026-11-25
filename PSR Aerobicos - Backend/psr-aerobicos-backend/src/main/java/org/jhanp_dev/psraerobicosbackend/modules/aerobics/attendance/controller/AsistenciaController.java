package org.jhanp_dev.psraerobicosbackend.modules.aerobics.attendance.controller;

import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.attendance.dto.request.AsistenciaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.attendance.dto.response.AsistenciaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.attendance.service.interfaces.IAsistenciaService;
import org.jhanp_dev.psraerobicosbackend.shared.exception.ResourceNotFoundException;
import org.jhanp_dev.psraerobicosbackend.shared.response.ApiResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/llegadas/{llegadaId}/asistencia")
@RequiredArgsConstructor
public class AsistenciaController {

    private final IAsistenciaService asistenciaService;

    @GetMapping
    public ResponseEntity<ApiResponseDTO<AsistenciaResponseDTO>> obtener(@PathVariable Integer llegadaId) {
        AsistenciaResponseDTO asistencia = asistenciaService.obtenerPorLlegada(llegadaId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró una asistencia para la llegada indicada"));
        return ResponseEntity.ok(ApiResponseDTO.ok(asistencia));
    }

    @PutMapping
    public ResponseEntity<ApiResponseDTO<AsistenciaResponseDTO>> confirmar(
            @PathVariable Integer llegadaId, @Valid @RequestBody AsistenciaRequestDTO request) {
        return ResponseEntity.ok(ApiResponseDTO.ok(asistenciaService.confirmar(llegadaId, request)));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponseDTO<Boolean>> anular(@PathVariable Integer llegadaId) {
        if (!asistenciaService.anular(llegadaId)) {
            throw new ResourceNotFoundException("No se encontró una asistencia para la llegada indicada");
        }
        return ResponseEntity.ok(ApiResponseDTO.ok(true));
    }
}
