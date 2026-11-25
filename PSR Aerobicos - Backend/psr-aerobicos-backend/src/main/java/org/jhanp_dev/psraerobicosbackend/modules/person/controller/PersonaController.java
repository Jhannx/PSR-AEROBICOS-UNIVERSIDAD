package org.jhanp_dev.psraerobicosbackend.modules.person.controller;

import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.jhanp_dev.psraerobicosbackend.modules.person.dto.request.ActualizarPersonaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.person.dto.request.AutoregistroPersonaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.person.dto.request.CrearPersonaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.person.dto.response.PersonaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.person.dto.response.SaldoPersonaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.person.service.interfaces.IPersonaService;
import org.jhanp_dev.psraerobicosbackend.shared.exception.ResourceNotFoundException;
import org.jhanp_dev.psraerobicosbackend.shared.response.ApiResponseDTO;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/personas")
@RequiredArgsConstructor
public class PersonaController {

    private final IPersonaService personaService;

    @GetMapping
    public ResponseEntity<ApiResponseDTO<PagedModel<PersonaResponseDTO>>> listar(
            @RequestParam(required = false) String buscar,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponseDTO.ok(new PagedModel<>(personaService.listar(buscar, page, size))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<PersonaResponseDTO>> obtener(@PathVariable Integer id) {
        PersonaResponseDTO persona = personaService.obtener(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la persona con ID: " + id));
        return ResponseEntity.ok(ApiResponseDTO.ok(persona));
    }

    @GetMapping("/{id}/saldo")
    public ResponseEntity<ApiResponseDTO<SaldoPersonaResponseDTO>> obtenerSaldo(@PathVariable Integer id) {
        SaldoPersonaResponseDTO saldo = personaService.obtenerSaldo(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el saldo de la persona con ID: " + id));
        return ResponseEntity.ok(ApiResponseDTO.ok(saldo));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<PersonaResponseDTO>> crear(
            @Valid @RequestBody CrearPersonaRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.creado(personaService.crearComoAdmin(request)));
    }

    @PostMapping("/autoregistro")
    public ResponseEntity<ApiResponseDTO<PersonaResponseDTO>> autoregistrar(
            @Valid @RequestBody AutoregistroPersonaRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.creado(personaService.autoregistrar(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<PersonaResponseDTO>> actualizar(
            @PathVariable Integer id, @Valid @RequestBody ActualizarPersonaRequestDTO request) {
        return ResponseEntity.ok(ApiResponseDTO.ok(personaService.actualizar(id, request)));
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<ApiResponseDTO<Boolean>> desactivar(@PathVariable Integer id) {
        if (!personaService.cambiarEstado(id, false)) {
            throw new ResourceNotFoundException("No se encontró la persona con ID: " + id);
        }
        return ResponseEntity.ok(ApiResponseDTO.ok(true));
    }

    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<ApiResponseDTO<Boolean>> reactivar(@PathVariable Integer id) {
        if (!personaService.cambiarEstado(id, true)) {
            throw new ResourceNotFoundException("No se encontró la persona con ID: " + id);
        }
        return ResponseEntity.ok(ApiResponseDTO.ok(true));
    }
}
