package org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.service.interfaces;

import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.AlumnoDestacadoResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.AuditoriaMetodoPagoResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.EstadoCuentaParticipanteResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.MetodoPagoAltoRecaudoResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.MetodoPagoMasUtilizadoResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.PagoAnticipadoResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.RecaudoDiarioResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.SesionDestacadaPorDiaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.TurnoSinCancelacionResponseDTO;
import org.springframework.data.domain.Page;

import java.util.List;
import java.time.LocalDate;
import org.jhanp_dev.psraerobicosbackend.modules.person.dto.response.SaldoPersonaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.AsistenciaPorFechaResponseDTO;

public interface IReporteService {
    List<MetodoPagoAltoRecaudoResponseDTO> obtenerMetodosPagoAltoRecaudo();
    List<AlumnoDestacadoResponseDTO> obtenerAlumnosDestacados();
    Page<SesionDestacadaPorDiaResponseDTO> obtenerSesionesDestacadasPorDia(int page, int size);
    List<TurnoSinCancelacionResponseDTO> obtenerTurnosSinCancelaciones();
    List<AuditoriaMetodoPagoResponseDTO> obtenerAuditoriaMetodosPago(int minOperaciones);
    Page<PagoAnticipadoResponseDTO> obtenerPagosAnticipados(int page, int size);
    List<MetodoPagoMasUtilizadoResponseDTO> obtenerMetodosPagoMasUtilizados();
    Page<EstadoCuentaParticipanteResponseDTO> obtenerEstadoCuentaPorDocumento(String numeroDocumento, int page, int size);
    Page<RecaudoDiarioResponseDTO> obtenerRecaudoDiario(int page, int size);
    Page<SaldoPersonaResponseDTO> obtenerSaldosPersonas(int page, int size);
    List<AsistenciaPorFechaResponseDTO> obtenerAsistenciasPorFecha(LocalDate fecha);
}

