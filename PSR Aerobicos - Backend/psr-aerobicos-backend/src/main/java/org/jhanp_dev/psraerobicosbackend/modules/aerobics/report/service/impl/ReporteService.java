package org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.service.impl;

import lombok.RequiredArgsConstructor;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dao.interfaces.IReporteDAO;
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
import org.jhanp_dev.psraerobicosbackend.shared.exception.InvalidDataException;
import org.jhanp_dev.psraerobicosbackend.shared.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReporteService implements IReporteService {

    private final IReporteDAO reporteDAO;

    @Override
    public List<MetodoPagoAltoRecaudoResponseDTO> obtenerMetodosPagoAltoRecaudo() {
        List<MetodoPagoAltoRecaudoResponseDTO> metodosPagoAltoRecaudo = reporteDAO.obtenerMetodosPagoAltoRecaudo();
        if (metodosPagoAltoRecaudo.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron métodos de pago con alto recaudo");
        }
        return metodosPagoAltoRecaudo;
    }

    @Override
    public List<AlumnoDestacadoResponseDTO> obtenerAlumnosDestacados() {
        List<AlumnoDestacadoResponseDTO> alumnosDestacados = reporteDAO.obtenerAlumnosDestacados();
        if (alumnosDestacados.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron registros de alumnos destacados");
        }
        return alumnosDestacados;
    }

    @Override
    public Page<SesionDestacadaPorDiaResponseDTO> obtenerSesionesDestacadasPorDia(int page, int size) {
        Pageable pageable = validarPaginacion(page, size);
        List<SesionDestacadaPorDiaResponseDTO> sesionesDestacadas = reporteDAO.obtenerSesionesDestacadasPorDia(pageable);
        return new PageImpl<>(sesionesDestacadas, pageable, reporteDAO.contarSesionesDestacadasPorDia());
    }

    @Override
    public List<TurnoSinCancelacionResponseDTO> obtenerTurnosSinCancelaciones() {
        List<TurnoSinCancelacionResponseDTO> turnosSinCancelaciones = reporteDAO.obtenerTurnosSinCancelaciones();
        if (turnosSinCancelaciones.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron registros de turnos sin cancelaciones");
        }
        return turnosSinCancelaciones;
    }

    @Override
    public List<AuditoriaMetodoPagoResponseDTO> obtenerAuditoriaMetodosPago(int minOperaciones) {
        validarMinOperaciones(minOperaciones);
        List<AuditoriaMetodoPagoResponseDTO> auditoriaMetodosPago = reporteDAO.obtenerAuditoriaMetodosPago(minOperaciones);
        if (auditoriaMetodosPago.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron registros de auditoría para min_operaciones = " + minOperaciones);
        }
        return auditoriaMetodosPago;
    }

    @Override
    public Page<PagoAnticipadoResponseDTO> obtenerPagosAnticipados(int page, int size) {
        Pageable pageable = validarPaginacion(page, size);
        List<PagoAnticipadoResponseDTO> pagosAnticipados = reporteDAO.obtenerPagosAnticipados(pageable);
        return new PageImpl<>(pagosAnticipados, pageable, reporteDAO.contarPagosAnticipados());
    }

    @Override
    public List<MetodoPagoMasUtilizadoResponseDTO> obtenerMetodosPagoMasUtilizados() {
        List<MetodoPagoMasUtilizadoResponseDTO> metodosPagoMasUtilizados = reporteDAO.obtenerMetodosPagoMasUtilizados();
        if (metodosPagoMasUtilizados.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron métodos de pago más utilizados");
        }
        return metodosPagoMasUtilizados;
    }

    @Override
    public Page<EstadoCuentaParticipanteResponseDTO> obtenerEstadoCuentaPorDocumento(
            String numeroDocumento, int page, int size) {
        Pageable pageable = validarPaginacion(page, size);
        String documento = validarDocumento(numeroDocumento);
        List<EstadoCuentaParticipanteResponseDTO> movimientosCuenta =
                reporteDAO.obtenerEstadoCuentaPorDocumento(documento, pageable);
        return new PageImpl<>(movimientosCuenta, pageable, reporteDAO.contarEstadoCuentaPorDocumento(documento));
    }

    @Override
    public Page<RecaudoDiarioResponseDTO> obtenerRecaudoDiario(int page, int size) {
        Pageable pageable = validarPaginacion(page, size);
        List<RecaudoDiarioResponseDTO> recaudoDiario = reporteDAO.obtenerRecaudoDiario(pageable);
        return new PageImpl<>(recaudoDiario, pageable, reporteDAO.contarRecaudoDiario());
    }

    @Override
    public Page<SaldoPersonaResponseDTO> obtenerSaldosPersonas(int page, int size) {
        Pageable pageable = validarPaginacion(page, size);
        List<SaldoPersonaResponseDTO> saldosPersonas = reporteDAO.obtenerSaldosPersonas(pageable);
        return new PageImpl<>(saldosPersonas, pageable, reporteDAO.contarSaldosPersonas());
    }

    @Override
    public List<AsistenciaPorFechaResponseDTO> obtenerAsistenciasPorFecha(LocalDate fecha) {
        validarFecha(fecha);
        return reporteDAO.obtenerAsistenciasPorFecha(fecha);
    }

    private Pageable validarPaginacion(int page, int size) {
        if (page < 0 || size < 1 || size > 100) {
            throw new InvalidDataException("La página debe ser mayor o igual a cero y el tamaño debe estar entre 1 y 100");
        }
        return PageRequest.of(page, size);
    }

    private String validarDocumento(String numeroDocumento) {
        if (numeroDocumento == null || numeroDocumento.isBlank()) {
            throw new InvalidDataException("El número de documento es obligatorio");
        }
        String documento = numeroDocumento.trim();
        if (!documento.matches("[0-9]{1,20}")) {
            throw new InvalidDataException("El número de documento solo puede contener hasta 20 dígitos");
        }
        return documento;
    }

    private void validarMinOperaciones(int minOperaciones) {
        if (minOperaciones < 1) {
            throw new InvalidDataException("El parámetro min_operaciones debe ser mayor o igual a 1");
        }
    }

    private void validarFecha(LocalDate fecha) {
        if (fecha == null) throw new InvalidDataException("La fecha es obligatoria");
    }
}
