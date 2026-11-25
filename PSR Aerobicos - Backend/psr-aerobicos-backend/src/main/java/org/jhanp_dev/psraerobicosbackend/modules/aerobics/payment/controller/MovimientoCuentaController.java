package org.jhanp_dev.psraerobicosbackend.modules.aerobics.payment.controller;

import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.payment.dto.request.ActualizarMovimientoCuentaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.payment.dto.request.RegistrarMovimientoCuentaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.payment.dto.response.MovimientoCuentaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.payment.service.interfaces.IMovimientoCuentaService;
import org.jhanp_dev.psraerobicosbackend.shared.exception.ResourceNotFoundException;
import org.jhanp_dev.psraerobicosbackend.shared.response.ApiResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/movimientos")
@RequiredArgsConstructor
public class MovimientoCuentaController {

    private final IMovimientoCuentaService movimientoCuentaService;

    @PostMapping
    public ResponseEntity<ApiResponseDTO<MovimientoCuentaResponseDTO>> registrar(
            @Valid @RequestBody RegistrarMovimientoCuentaRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.creado(movimientoCuentaService.registrar(request)));
    }

    @GetMapping("/checkin/{llegadaId}")
    public ResponseEntity<ApiResponseDTO<List<MovimientoCuentaResponseDTO>>> listarPorLlegada(
            @PathVariable Integer llegadaId) {
        return ResponseEntity.ok(ApiResponseDTO.ok(movimientoCuentaService.listarPorLlegada(llegadaId)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<MovimientoCuentaResponseDTO>> actualizar(
            @PathVariable Integer id, @Valid @RequestBody ActualizarMovimientoCuentaRequestDTO request) {
        return ResponseEntity.ok(ApiResponseDTO.ok(movimientoCuentaService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Boolean>> eliminar(@PathVariable Integer id) {
        if (!movimientoCuentaService.eliminar(id)) {
            throw new ResourceNotFoundException("No se encontró el movimiento con ID: " + id);
        }
        return ResponseEntity.ok(ApiResponseDTO.ok(true));
    }
}
