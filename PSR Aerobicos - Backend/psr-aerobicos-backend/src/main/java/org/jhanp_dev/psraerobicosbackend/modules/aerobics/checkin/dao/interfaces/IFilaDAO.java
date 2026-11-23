package org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.dao.interfaces;

import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.dto.response.FilaEsperaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.dto.response.FilaRecepcionResponseDTO;

import java.util.List;

public interface IFilaDAO {
    List<FilaRecepcionResponseDTO> listarFilaRecepcion();
    List<FilaEsperaResponseDTO> listarFilaEspera();
}
