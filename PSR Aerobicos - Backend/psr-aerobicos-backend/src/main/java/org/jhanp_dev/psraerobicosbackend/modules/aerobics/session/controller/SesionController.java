package org.jhanp_dev.psraerobicosbackend.modules.aerobics.session.controller;

import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.session.dto.request.SesionRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.session.dto.response.SesionResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.session.service.interfaces.ISesionService;
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

import java.time.LocalDate;

@RestController
@RequestMapping("/api/sesiones")
@RequiredArgsConstructor
public class SesionController {

    private final ISesionService sesionService;

    @GetMapping
    public ResponseEntity<ApiResponseDTO<PagedModel<SesionResponseDTO>>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) LocalDate fecha,
            @RequestParam(required = false) String estado) {
        return ResponseEntity.ok(ApiResponseDTO.ok(new PagedModel<>(sesionService.listarFiltrado(fecha, estado, page, size))));
    }

    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<ApiResponseDTO<PagedModel<SesionResponseDTO>>> listarPorFecha(
            @PathVariable LocalDate fecha,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponseDTO.ok(new PagedModel<>(sesionService.listarPorFecha(fecha, page, size))));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<SesionResponseDTO>> crear(@Valid @RequestBody SesionRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.creado(sesionService.crear(request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Boolean>> cancelar(
            @PathVariable Integer id, @RequestParam Integer usuarioId) {
        if (!sesionService.cancelar(id, usuarioId)) {
            throw new ResourceNotFoundException("No se encontró la sesión con ID: " + id);
        }
        return ResponseEntity.ok(ApiResponseDTO.ok(true));
    }
}
