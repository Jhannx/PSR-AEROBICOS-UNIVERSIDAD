package org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.service.interfaces;

import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.dto.request.LlegadaRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.dto.response.FilaRecepcionResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.dto.response.FilaEsperaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.dto.response.LlegadaResponseDTO;

import java.util.List;

public interface ILlegadaService {
    List<FilaRecepcionResponseDTO> obtenerColaAdmin();
    List<FilaEsperaResponseDTO> obtenerColaEspera();
    LlegadaResponseDTO registrarCheckin(LlegadaRequestDTO request, boolean manual);
}
