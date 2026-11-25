package org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.controller;

import lombok.RequiredArgsConstructor;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.AlumnoDestacadoResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.AsistenciaPorFechaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.AuditoriaMetodoPagoResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.EstadoCuentaParticipanteResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.MetodoPagoAltoRecaudoResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.MetodoPagoMasUtilizadoResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.PagoAnticipadoResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.RecaudoDiarioResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.SesionDestacadaPorDiaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.TurnoSinCancelacionResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.service.interfaces.IReporteService;
import org.jhanp_dev.psraerobicosbackend.modules.person.dto.response.SaldoPersonaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.shared.response.ApiResponseDTO;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
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
    public ResponseEntity<ApiResponseDTO<PagedModel<SesionDestacadaPorDiaResponseDTO>>> obtenerSesionesDestacadasPorDia(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(ApiResponseDTO.ok(new PagedModel<>(reporteService.obtenerSesionesDestacadasPorDia(page, size))));
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
    public ResponseEntity<ApiResponseDTO<PagedModel<PagoAnticipadoResponseDTO>>> obtenerPagosAnticipados(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(ApiResponseDTO.ok(new PagedModel<>(reporteService.obtenerPagosAnticipados(page, size))));
    }

    @GetMapping("/pagos/mas-utilizados")
    public ResponseEntity<ApiResponseDTO<List<MetodoPagoMasUtilizadoResponseDTO>>> obtenerMetodosPagoMasUtilizados() {
        return ResponseEntity.ok(ApiResponseDTO.okLista(reporteService.obtenerMetodosPagoMasUtilizados()));
    }

    @GetMapping("/estado-cuenta/{numeroDocumento}")
    public ResponseEntity<ApiResponseDTO<PagedModel<EstadoCuentaParticipanteResponseDTO>>> obtenerEstadoCuentaPorDocumento(
            @PathVariable String numeroDocumento,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(ApiResponseDTO.ok(
                new PagedModel<>(reporteService.obtenerEstadoCuentaPorDocumento(numeroDocumento, page, size))));
    }

    @GetMapping("/recaudo-diario")
    public ResponseEntity<ApiResponseDTO<PagedModel<RecaudoDiarioResponseDTO>>> obtenerRecaudoDiario(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(ApiResponseDTO.ok(new PagedModel<>(reporteService.obtenerRecaudoDiario(page, size))));
    }

    @GetMapping("/saldos-personas")
    public ResponseEntity<ApiResponseDTO<PagedModel<SaldoPersonaResponseDTO>>> obtenerSaldosPersonas(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(ApiResponseDTO.ok(new PagedModel<>(reporteService.obtenerSaldosPersonas(page, size))));
    }

    @GetMapping("/asistencias-fecha")
    public ResponseEntity<ApiResponseDTO<List<AsistenciaPorFechaResponseDTO>>> obtenerAsistenciasPorFecha(
            @RequestParam LocalDate fecha
    ) {
        return ResponseEntity.ok(ApiResponseDTO.okLista(reporteService.obtenerAsistenciasPorFecha(fecha)));
    }
}

