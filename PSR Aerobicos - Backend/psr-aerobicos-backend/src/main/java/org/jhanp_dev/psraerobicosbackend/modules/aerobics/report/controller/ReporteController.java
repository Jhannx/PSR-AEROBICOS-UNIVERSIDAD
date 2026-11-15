package org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.controller;

import lombok.RequiredArgsConstructor;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.AlumnoDestacadoResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.AuditoriaMetodoPagoResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.EstadoCuentaParticipanteResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.MetodoPagoAltoRecaudoResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.MetodoPagoMasUtilizadoResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.PagoAnticipadoResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.RecaudoDiarioResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.SesionDestacadaPorDiaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.TurnoSinCancelacionResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.service.interfaces.IReporteService;
import org.jhanp_dev.psraerobicosbackend.shared.response.ApiResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final IReporteService reporteService;

    @GetMapping("/pagos/alto-recaudo")
    public ResponseEntity<ApiResponseDTO<List<MetodoPagoAltoRecaudoResponseDTO>>> obtenerMetodosPagoAltoRecaudo() {
        return ResponseEntity.ok(ApiResponseDTO.okLista(reporteService.obtenerMetodosPagoAltoRecaudo()));
    }

    @GetMapping("/alumnos/destacados")
    public ResponseEntity<ApiResponseDTO<List<AlumnoDestacadoResponseDTO>>> obtenerAlumnosDestacados() {
        return ResponseEntity.ok(ApiResponseDTO.okLista(reporteService.obtenerAlumnosDestacados()));
    }

    @GetMapping("/sesiones/destacadas-por-dia")
    public ResponseEntity<ApiResponseDTO<List<SesionDestacadaPorDiaResponseDTO>>> obtenerSesionesDestacadasPorDia() {
        return ResponseEntity.ok(ApiResponseDTO.okLista(reporteService.obtenerSesionesDestacadasPorDia()));
    }

    @GetMapping("/turnos/sin-cancelaciones")
    public ResponseEntity<ApiResponseDTO<List<TurnoSinCancelacionResponseDTO>>> obtenerTurnosSinCancelaciones() {
        return ResponseEntity.ok(ApiResponseDTO.okLista(reporteService.obtenerTurnosSinCancelaciones()));
    }

    @GetMapping("/pagos/auditoria-metodos")
    public ResponseEntity<ApiResponseDTO<List<AuditoriaMetodoPagoResponseDTO>>> obtenerAuditoriaMetodosPago(
            @RequestParam(name = "min_operaciones", defaultValue = "2") int minOperaciones
    ) {
        return ResponseEntity.ok(ApiResponseDTO.okLista(reporteService.obtenerAuditoriaMetodosPago(minOperaciones)));
    }

    @GetMapping("/pagos/anticipados")
    public ResponseEntity<ApiResponseDTO<List<PagoAnticipadoResponseDTO>>> obtenerPagosAnticipados() {
        return ResponseEntity.ok(ApiResponseDTO.okLista(reporteService.obtenerPagosAnticipados()));
    }

    @GetMapping("/pagos/mas-utilizados")
    public ResponseEntity<ApiResponseDTO<List<MetodoPagoMasUtilizadoResponseDTO>>> obtenerMetodosPagoMasUtilizados() {
        return ResponseEntity.ok(ApiResponseDTO.okLista(reporteService.obtenerMetodosPagoMasUtilizados()));
    }

    @GetMapping("/estado-cuenta/{numeroDocumento}")
    public ResponseEntity<ApiResponseDTO<List<EstadoCuentaParticipanteResponseDTO>>> obtenerEstadoCuentaPorDocumento(
            @PathVariable String numeroDocumento
    ) {
        return ResponseEntity.ok(ApiResponseDTO.okLista(reporteService.obtenerEstadoCuentaPorDocumento(numeroDocumento)));
    }

    @GetMapping("/recaudo-diario")
    public ResponseEntity<ApiResponseDTO<List<RecaudoDiarioResponseDTO>>> obtenerRecaudoDiario() {
        return ResponseEntity.ok(ApiResponseDTO.okLista(reporteService.obtenerRecaudoDiario()));
    }
}

