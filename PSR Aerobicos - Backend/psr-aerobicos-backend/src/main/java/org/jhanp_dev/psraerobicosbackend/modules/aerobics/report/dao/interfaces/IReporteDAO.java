package org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dao.interfaces;

import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.AlumnoDestacadoResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.AuditoriaMetodoPagoResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.EstadoCuentaParticipanteResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.MetodoPagoAltoRecaudoResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.MetodoPagoMasUtilizadoResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.PagoAnticipadoResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.RecaudoDiarioResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.SesionDestacadaPorDiaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.TurnoSinCancelacionResponseDTO;

import java.util.List;
import java.time.LocalDate;
import org.jhanp_dev.psraerobicosbackend.modules.person.dto.response.SaldoPersonaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.report.dto.response.AsistenciaPorFechaResponseDTO;

public interface IReporteDAO {
    List<MetodoPagoAltoRecaudoResponseDTO> obtenerMetodosPagoAltoRecaudo();
    List<AlumnoDestacadoResponseDTO> obtenerAlumnosDestacados();
    List<SesionDestacadaPorDiaResponseDTO> obtenerSesionesDestacadasPorDia();
    List<TurnoSinCancelacionResponseDTO> obtenerTurnosSinCancelaciones();
    List<AuditoriaMetodoPagoResponseDTO> obtenerAuditoriaMetodosPago(int minOperaciones);
    List<PagoAnticipadoResponseDTO> obtenerPagosAnticipados();
    List<MetodoPagoMasUtilizadoResponseDTO> obtenerMetodosPagoMasUtilizados();
    List<EstadoCuentaParticipanteResponseDTO> obtenerEstadoCuentaPorDocumento(String numeroDocumento);
    List<RecaudoDiarioResponseDTO> obtenerRecaudoDiario();
    List<SaldoPersonaResponseDTO> obtenerSaldosPersonas();
    List<AsistenciaPorFechaResponseDTO> obtenerAsistenciasPorFecha(LocalDate fecha);
}

