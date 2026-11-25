package org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.service.interfaces;

import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.dto.request.CheckinManualRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.dto.request.CheckinMiembroRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.dto.response.FilaRecepcionResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.dto.response.FilaEsperaResponseDTO;

import java.util.List;

public interface ILlegadaService {
    List<FilaRecepcionResponseDTO> obtenerColaAdmin();
    List<FilaEsperaResponseDTO> obtenerColaEspera();
    FilaRecepcionResponseDTO registrarCheckinManual(CheckinManualRequestDTO request);
    FilaRecepcionResponseDTO registrarCheckinMiembro(CheckinMiembroRequestDTO request);
}
