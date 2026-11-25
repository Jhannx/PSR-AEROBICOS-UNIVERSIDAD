package org.jhanp_dev.psraerobicosbackend.modules.aerobics.schedule.controller;

import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.schedule.dto.request.ActualizarHorarioRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.schedule.dto.request.CrearHorarioRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.schedule.dto.response.HorarioResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.schedule.service.interfaces.IHorarioService;
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
@RequestMapping("/api/horarios")
@RequiredArgsConstructor
public class HorarioController {

    private final IHorarioService horarioService;

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<HorarioResponseDTO>>> listar() {
        return ResponseEntity.ok(ApiResponseDTO.okLista(horarioService.listar()));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<HorarioResponseDTO>> crear(@Valid @RequestBody CrearHorarioRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.creado(horarioService.crear(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<HorarioResponseDTO>> actualizar(
            @PathVariable Integer id, @Valid @RequestBody ActualizarHorarioRequestDTO request) {
        return ResponseEntity.ok(ApiResponseDTO.ok(horarioService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Boolean>> eliminar(@PathVariable Integer id) {
        if (!horarioService.eliminar(id)) {
            throw new ResourceNotFoundException("No se encontró el horario con ID: " + id);
        }
        return ResponseEntity.ok(ApiResponseDTO.ok(true));
    }
}
