package org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.service.impl;

import lombok.RequiredArgsConstructor;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dao.interfaces.IReporteDAO;
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
import org.jhanp_dev.psraerobicosbackend.shared.exception.RecursoNoEncontradoException;
import org.jhanp_dev.psraerobicosbackend.shared.exception.ReglaNegocioException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ReporteService implements IReporteService {

    private final IReporteDAO reporteDAO;

    @Override
    public List<MetodoPagoAltoRecaudoResponseDTO> obtenerMetodosPagoAltoRecaudo() {
        List<MetodoPagoAltoRecaudoResponseDTO> datos = reporteDAO.obtenerMetodosPagoAltoRecaudo();
        if (datos.isEmpty()) {
            throw new RecursoNoEncontradoException("No se encontraron registros de metodos de pago con alto recaudo");
        }
        return datos;
    }

    @Override
    public List<AlumnoDestacadoResponseDTO> obtenerAlumnosDestacados() {
        List<AlumnoDestacadoResponseDTO> datos = reporteDAO.obtenerAlumnosDestacados();
        if (datos.isEmpty()) {
            throw new RecursoNoEncontradoException("No se encontraron registros de alumnos destacados");
        }
        return datos;
    }

    @Override
    public List<SesionDestacadaPorDiaResponseDTO> obtenerSesionesDestacadasPorDia() {
        List<SesionDestacadaPorDiaResponseDTO> datos = reporteDAO.obtenerSesionesDestacadasPorDia();
        if (datos.isEmpty()) {
            throw new RecursoNoEncontradoException("No se encontraron registros de sesiones destacadas por dia");
        }
        return datos;
    }

    @Override
    public List<TurnoSinCancelacionResponseDTO> obtenerTurnosSinCancelaciones() {
        List<TurnoSinCancelacionResponseDTO> datos = reporteDAO.obtenerTurnosSinCancelaciones();
        if (datos.isEmpty()) {
            throw new RecursoNoEncontradoException("No se encontraron registros de turnos sin cancelaciones");
        }
        return datos;
    }

    @Override
    public List<AuditoriaMetodoPagoResponseDTO> obtenerAuditoriaMetodosPago(int minOperaciones) {
        if (minOperaciones < 1) {
            throw new ReglaNegocioException("El parametro min_operaciones debe ser mayor o igual a 1");
        }
        List<AuditoriaMetodoPagoResponseDTO> datos = reporteDAO.obtenerAuditoriaMetodosPago(minOperaciones);
        if (datos.isEmpty()) {
            throw new RecursoNoEncontradoException("No se encontraron registros de auditoria para min_operaciones = " + minOperaciones);
        }
        return datos;
    }

    @Override
    public List<PagoAnticipadoResponseDTO> obtenerPagosAnticipados() {
        List<PagoAnticipadoResponseDTO> datos = reporteDAO.obtenerPagosAnticipados();
        if (datos.isEmpty()) {
            throw new RecursoNoEncontradoException("No se encontraron registros de pagos anticipados");
        }
        return datos;
    }

    @Override
    public List<MetodoPagoMasUtilizadoResponseDTO> obtenerMetodosPagoMasUtilizados() {
        List<MetodoPagoMasUtilizadoResponseDTO> datos = reporteDAO.obtenerMetodosPagoMasUtilizados();
        if (datos.isEmpty()) {
            throw new RecursoNoEncontradoException("No se encontraron registros de metodos de pago mas utilizados");
        }
        return datos;
    }

    @Override
    public List<EstadoCuentaParticipanteResponseDTO> obtenerEstadoCuentaPorDocumento(String numeroDocumento) {
        if (numeroDocumento == null || numeroDocumento.isBlank()) {
            throw new ReglaNegocioException("El numero de documento es obligatorio");
        }
        List<EstadoCuentaParticipanteResponseDTO> datos = reporteDAO.obtenerEstadoCuentaPorDocumento(numeroDocumento.trim());
        if (datos.isEmpty()) {
            throw new RecursoNoEncontradoException("No se encontraron movimientos para el documento " + numeroDocumento);
        }
        return datos;
    }

    @Override
    public List<RecaudoDiarioResponseDTO> obtenerRecaudoDiario() {
        List<RecaudoDiarioResponseDTO> datos = reporteDAO.obtenerRecaudoDiario();
        if (datos.isEmpty()) {
            throw new RecursoNoEncontradoException("No se encontraron registros de recaudo diario");
        }
        return datos;
    }

    @Override
    public List<org.jhanp_dev.psraerobicosbackend.modules.person.dto.response.SaldoPersonaResponseDTO> obtenerSaldosPersonas() {
        return reporteDAO.obtenerSaldosPersonas();
    }

    @Override
    public List<org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.AsistenciaPorFechaResponseDTO> obtenerAsistenciasPorFecha(LocalDate fecha) {
        if (fecha == null) throw new ReglaNegocioException("La fecha es obligatoria");
        return reporteDAO.obtenerAsistenciasPorFecha(fecha);
    }
}

