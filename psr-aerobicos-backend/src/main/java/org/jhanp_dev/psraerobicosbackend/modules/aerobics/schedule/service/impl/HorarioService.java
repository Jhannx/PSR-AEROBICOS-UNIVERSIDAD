package org.jhanp_dev.psraerobicosbackend.modules.aerobics.schedule.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.schedule.dao.interfaces.IHorarioDAO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.schedule.dto.request.ActualizarHorarioRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.schedule.dto.request.CrearHorarioRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.schedule.dto.response.HorarioResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.schedule.service.interfaces.IHorarioService;
import org.jhanp_dev.psraerobicosbackend.shared.exception.InvalidDataException;
import org.jhanp_dev.psraerobicosbackend.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HorarioService implements IHorarioService {

    private final IHorarioDAO horarioDAO;

    @Override
    public List<HorarioResponseDTO> listar() {
        log.debug("Consultando horarios");
        return horarioDAO.listarTodos();
    }

    @Override
    public HorarioResponseDTO crear(CrearHorarioRequestDTO request) {
        validarSolicitud(request);
        log.debug("Creando horario para el día {}", request.getDiaSemana());
        return horarioDAO.guardar(request);
    }

    @Override
    public HorarioResponseDTO actualizar(Integer id, ActualizarHorarioRequestDTO request) {
        validarId(id);
        validarSolicitud(request);

        if (!horarioDAO.actualizar(id, request)) {
            log.warn("No se encontró el horario con ID {} para actualizar", id);
            throw new ResourceNotFoundException("No se encontró el horario con ID: " + id);
        }

        return horarioDAO.obtenerPorId(id).orElseThrow(() -> {
            log.warn("El horario con ID {} no se encontró después de actualizarlo", id);
            return new ResourceNotFoundException("No se encontró el horario con ID: " + id);
        });
    }

    @Override
    public boolean eliminar(Integer id) {
        validarId(id);
        boolean eliminado = horarioDAO.eliminar(id);
        if (!eliminado) {
            log.debug("No se encontró el horario con ID {} para desactivarlo", id);
        }
        return eliminado;
    }

    private void validarSolicitud(CrearHorarioRequestDTO request) {
        if (request == null) {
            throw new InvalidDataException("Los datos del horario son obligatorios");
        }
        validarDiaSemana(request.getDiaSemana());
        validarRangoHoras(request.getHoraInicio(), request.getHoraFin());
    }

    private void validarSolicitud(ActualizarHorarioRequestDTO request) {
        if (request == null) {
            throw new InvalidDataException("Los datos del horario son obligatorios");
        }
        validarDiaSemana(request.getDiaSemana());
        validarRangoHoras(request.getHoraInicio(), request.getHoraFin());
        validarEstado(request.getActivo());
    }

    private void validarDiaSemana(Integer diaSemana) {
        if (diaSemana == null || diaSemana < 1 || diaSemana > 7) {
            throw new InvalidDataException("El día de la semana debe estar entre 1 y 7");
        }
    }

    private void validarRangoHoras(java.time.LocalTime horaInicio, java.time.LocalTime horaFin) {
        if (horaInicio == null || horaFin == null) {
            throw new InvalidDataException("Las horas de inicio y fin son obligatorias");
        }
        if (!horaFin.isAfter(horaInicio)) {
            throw new InvalidDataException("La hora de fin debe ser posterior a la hora de inicio");
        }
    }

    private void validarEstado(Boolean activo) {
        if (activo == null) {
            throw new InvalidDataException("El estado del horario es obligatorio al actualizarlo");
        }
    }

    private void validarId(Integer id) {
        if (id == null || id < 1) {
            throw new InvalidDataException("El ID del horario debe ser mayor que cero");
        }
    }
}
