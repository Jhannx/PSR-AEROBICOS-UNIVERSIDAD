package org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.dao.interfaces.IFilaDAO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.dao.interfaces.ILlegadaDAO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.dto.request.CheckinManualRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.dto.request.CheckinMiembroRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.dto.response.FilaEsperaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.dto.response.FilaRecepcionResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.dto.response.LlegadaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.enums.MetodoCheckin;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.checkin.service.interfaces.ILlegadaService;
import org.jhanp_dev.psraerobicosbackend.modules.person.dao.interfaces.IPersonaDAO;
import org.jhanp_dev.psraerobicosbackend.modules.person.dto.response.PersonaResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.session.dao.interfaces.ISesionDAO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.session.service.interfaces.ISesionService;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.session.dto.response.SesionResponseDTO;
import org.jhanp_dev.psraerobicosbackend.shared.exception.InvalidDataException;
import org.jhanp_dev.psraerobicosbackend.shared.exception.ResourceNotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LlegadaService implements ILlegadaService {

    private static final ZoneId ZONA_HORARIA = ZoneId.of("America/Bogota");

    private final IFilaDAO filaDAO;
    private final ILlegadaDAO llegadaDAO;
    private final IPersonaDAO personaDAO;
    private final ISesionDAO sesionDAO;
    private final ISesionService sesionService;

    @Override
    public List<FilaRecepcionResponseDTO> obtenerColaAdmin() {
        return filaDAO.listarFilaRecepcion();
    }

    @Override
    public List<FilaEsperaResponseDTO> obtenerColaEspera() {
        return filaDAO.listarFilaEspera();
    }

    @Override
    public FilaRecepcionResponseDTO registrarCheckinManual(CheckinManualRequestDTO request) {
        PersonaResponseDTO persona = obtenerPersonaParaCheckinManual(request);
        MetodoCheckin metodo = request.getMetodoCheckin() == null ? MetodoCheckin.MANUAL : request.getMetodoCheckin();
        return registrarYObtenerFila(persona, metodo);
    }

    @Override
    public FilaRecepcionResponseDTO registrarCheckinMiembro(CheckinMiembroRequestDTO request) {
        PersonaResponseDTO persona = obtenerPersonaParaCheckinMiembro(request);
        MetodoCheckin metodo = request.getMetodoCheckin() == null ? MetodoCheckin.QR : request.getMetodoCheckin();
        return registrarYObtenerFila(persona, metodo);
    }

    private PersonaResponseDTO obtenerPersonaParaCheckinManual(CheckinManualRequestDTO request) {
        validarSolicitud(request);
        String documento = obtenerDocumento(request.getDocumento(), request.getDocumentNumber());
        if (documento == null) {
            throw new InvalidDataException("El número de documento es obligatorio para el check-in");
        }
        PersonaResponseDTO persona = personaDAO.obtenerPorNumeroDocumento(documento)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró una persona con ese documento"));
        validarPersonaActiva(persona);
        if (request.getPersonaId() != null && !request.getPersonaId().equals(persona.getId())) {
            throw new InvalidDataException("El documento no corresponde a la persona indicada");
        }
        return persona;
    }

    private PersonaResponseDTO obtenerPersonaParaCheckinMiembro(CheckinMiembroRequestDTO request) {
        validarSolicitud(request);
        if (request.getPersonaId() == null || request.getPersonaId() < 1) {
            throw new InvalidDataException("La persona del check-in es obligatoria");
        }
        PersonaResponseDTO persona = personaDAO.obtenerPorId(request.getPersonaId())
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la persona indicada"));
        validarPersonaActiva(persona);
        validarDocumentoCoincide(request.getDocumento(), request.getDocumentNumber(), persona);
        return persona;
    }

    private void validarDocumentoCoincide(String documentoEnviado, String documentNumber, PersonaResponseDTO persona) {
        String documento = obtenerDocumento(documentoEnviado, documentNumber);
        if (documento != null && !documento.equals(persona.getNumeroDocumento())) {
            throw new InvalidDataException("El documento no corresponde a la persona indicada");
        }
    }

    private FilaRecepcionResponseDTO registrarYObtenerFila(PersonaResponseDTO persona, MetodoCheckin metodo) {
        SesionResponseDTO sesion = obtenerSesionParaCheckin();
        LlegadaResponseDTO llegada = llegadaDAO.registrar(persona.getId(), sesion.getId(), metodo);
        return filaDAO.listarFilaRecepcion().stream()
                .filter(fila -> llegada.getId().equals(fila.getLlegadaId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No se encontró la llegada en la fila de recepción"));
    }

    private SesionResponseDTO obtenerSesionParaCheckin() {
        LocalDate hoy = LocalDate.now(ZONA_HORARIA);
        sesionService.asegurarSesionesDelDia(hoy);
        LocalDateTime ahora = LocalDateTime.now(ZONA_HORARIA);
        List<SesionResponseDTO> sesiones = sesionDAO.listarPorFechaPaginado(hoy, PageRequest.of(0, 100));

        return sesiones.stream()
                .filter(sesion -> !Boolean.TRUE.equals(sesion.getCancelada()))
                .filter(sesion -> sesion.getHoraFin() != null && !sesion.getHoraFin().isBefore(ahora))
                .min(Comparator.comparing(SesionResponseDTO::getHoraInicio,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .orElseThrow(() -> new ResourceNotFoundException("No hay una sesión activa o próxima para hoy"));
    }

    private void validarPersonaActiva(PersonaResponseDTO persona) {
        if (!Boolean.TRUE.equals(persona.getActivo())) {
            throw new InvalidDataException("No se puede registrar el check-in de una persona inactiva");
        }
    }

    private String obtenerDocumento(String documentoEnviado, String documentNumber) {
        String documento = documentoEnviado;
        if (documento == null || documento.isBlank()) {
            documento = documentNumber;
        }
        if (documento == null || documento.isBlank()) return null;
        String normalizado = documento.trim();
        if (!normalizado.matches("[0-9]{1,20}")) {
            throw new InvalidDataException("El número de documento debe contener hasta 20 dígitos");
        }
        return normalizado;
    }

    private void validarSolicitud(CheckinManualRequestDTO request) {
        if (request == null) {
            throw new InvalidDataException("Los datos del check-in son obligatorios");
        }
    }

    private void validarSolicitud(CheckinMiembroRequestDTO request) {
        if (request == null) {
            throw new InvalidDataException("Los datos del check-in son obligatorios");
        }
    }
}
