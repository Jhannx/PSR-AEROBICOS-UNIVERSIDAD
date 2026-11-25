package org.jhanp_dev.psraerobicosbackend.modules.aerobics.session.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.session.dao.interfaces.ISesionDAO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.session.dto.request.SesionRequestDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.session.dto.response.SesionResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.session.service.interfaces.ISesionService;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.schedule.dao.interfaces.IHorarioDAO;
import org.jhanp_dev.psraerobicosbackend.modules.aerobics.schedule.dto.response.HorarioResponseDTO;
import org.jhanp_dev.psraerobicosbackend.modules.user.dao.interfaces.IUsuarioDAO;
import org.jhanp_dev.psraerobicosbackend.modules.user.dto.response.UsuarioResponseDTO;
import org.jhanp_dev.psraerobicosbackend.shared.exception.InvalidDataException;
import org.jhanp_dev.psraerobicosbackend.shared.exception.ConflictException;
import org.jhanp_dev.psraerobicosbackend.shared.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class SesionService implements ISesionService {
    private static final ZoneId ZONA = ZoneId.of("America/Bogota");
    private static final int DIAS_PROGRAMADOS = 30;

    private final ISesionDAO sesionDAO;
    private final IHorarioDAO horarioDAO;
    private final IUsuarioDAO usuarioDAO;

    @Override
    public Page<SesionResponseDTO> listar(int page, int size) {
        asegurarProgramacion(LocalDate.now(ZONA), LocalDate.now(ZONA).plusDays(DIAS_PROGRAMADOS));
        if (page < 0 || size < 1 || size > 100) {
            throw new InvalidDataException("La página debe ser mayor o igual a cero y el tamaño debe estar entre 1 y 100");
        }
        Pageable pageable = PageRequest.of(page, size);
        log.debug("Consultando sesiones, página {} y tamaño {}", page, size);
        List<SesionResponseDTO> sesiones = sesionDAO.listarPaginado(pageable);
        return new PageImpl<>(sesiones, pageable, sesionDAO.contar());
    }

    @Override
    public Page<SesionResponseDTO> listarPorFecha(LocalDate fecha, int page, int size) {
        if (fecha == null) {
            throw new InvalidDataException("La fecha de la sesión es obligatoria");
        }
        asegurarSesionesDelDia(fecha);
        if (page < 0 || size < 1 || size > 100) {
            throw new InvalidDataException("La página debe ser mayor o igual a cero y el tamaño debe estar entre 1 y 100");
        }
        Pageable pageable = PageRequest.of(page, size);
        log.debug("Consultando sesiones para {}, página {} y tamaño {}", fecha, page, size);
        List<SesionResponseDTO> sesionesFecha = sesionDAO.listarPorFechaPaginado(fecha, pageable);
        return new PageImpl<>(sesionesFecha, pageable, sesionDAO.contarPorFecha(fecha));
    }

    @Override
    public Page<SesionResponseDTO> listarFiltrado(LocalDate fecha, String estado, int page, int size) {
        if (page < 0 || size < 1 || size > 100) {
            throw new InvalidDataException("La página debe ser mayor o igual a cero y el tamaño debe estar entre 1 y 100");
        }
        String filtro = estado == null || estado.isBlank() || "all".equalsIgnoreCase(estado)
                ? null : estado.trim().toUpperCase(java.util.Locale.ROOT);
        if (filtro != null && !List.of("PROGRAMADA", "EN_CURSO", "FINALIZADA", "CANCELADA").contains(filtro)) {
            throw new InvalidDataException("El estado de sesión indicado no existe");
        }
        LocalDate hoy = LocalDate.now(ZONA);
        if (fecha == null) {
            asegurarProgramacion(hoy, hoy.plusDays(DIAS_PROGRAMADOS));
        } else {
            asegurarSesionesDelDia(fecha);
        }
        Pageable pageable = PageRequest.of(page, size);
        LocalDateTime ahora = LocalDateTime.now(ZONA);
        return new PageImpl<>(sesionDAO.listarFiltrado(fecha, filtro, ahora, pageable), pageable,
                sesionDAO.contarFiltrado(fecha, filtro, ahora));
    }

    @Override
    public void asegurarSesionesDelDia(LocalDate fecha) {
        LocalDate hoy = LocalDate.now(ZONA);
        if (fecha != null && !fecha.isBefore(hoy) && !fecha.isAfter(hoy.plusYears(1))) {
            asegurarProgramacion(fecha, fecha);
        }
    }

    private void asegurarProgramacion(LocalDate desde, LocalDate hasta) {
        List<HorarioResponseDTO> horarios = horarioDAO.listarTodos().stream()
                .filter(horario -> Boolean.TRUE.equals(horario.getActivo()))
                .toList();
        if (horarios.isEmpty()) return;
        Set<String> existentes = sesionDAO.listarHorariosProgramados(desde, hasta);
        for (LocalDate fecha = desde; !fecha.isAfter(hasta); fecha = fecha.plusDays(1)) {
            for (HorarioResponseDTO horario : horarios) {
                if (horario.getDiaSemana() == null || horario.getDiaSemana() != fecha.getDayOfWeek().getValue()) continue;
                String clave = horario.getId() + ":" + fecha;
                if (existentes.contains(clave)) continue;
                try {
                    sesionDAO.guardar(SesionRequestDTO.builder()
                            .horarioId(horario.getId())
                            .fecha(fecha)
                            .horaInicio(horario.getHoraInicio())
                            .horaFin(horario.getHoraFin())
                            .build());
                    existentes.add(clave);
                } catch (RuntimeException ex) {
                    if (!sesionDAO.existePorHorarioYFecha(horario.getId(), fecha)) throw ex;
                }
            }
        }
    }

    @Override
    public SesionResponseDTO crear(SesionRequestDTO request) {
        validarSolicitud(request);
        validarHorarioSiInformado(request.getHorarioId());
        validarProfesorSiInformado(request.getProfesorUsuarioId());
        if (request.getHorarioId() != null && sesionDAO.existePorHorarioYFecha(request.getHorarioId(), request.getFecha())) {
            throw new ConflictException("Ya existe una sesión para ese horario y fecha");
        }
        log.debug("Creando sesión para la fecha {}", request.getFecha());
        return sesionDAO.guardar(request);
    }

    @Override
    public boolean cancelar(Integer id, Integer usuarioId) {
        validarId(id, "sesión");
        validarId(usuarioId, "usuario");
        log.debug("Cancelando sesión {} por el usuario {}", id, usuarioId);
        return sesionDAO.cancelar(id, usuarioId);
    }

    private void validarSolicitud(SesionRequestDTO request) {
        if (request == null) {
            throw new InvalidDataException("Los datos de la sesión son obligatorios");
        }
        validarFechaYHoras(request);
        validarIdsRelacionados(request);
    }

    private void validarFechaYHoras(SesionRequestDTO request) {
        if (request.getFecha() == null) {
            throw new InvalidDataException("La fecha de la sesión es obligatoria");
        }
        if (request.getHoraInicio() == null || request.getHoraFin() == null) {
            throw new InvalidDataException("Las horas de inicio y fin son obligatorias");
        }
        if (!request.getHoraFin().isAfter(request.getHoraInicio())) {
            throw new InvalidDataException("La hora de fin debe ser posterior a la hora de inicio");
        }
    }

    private void validarIdsRelacionados(SesionRequestDTO request) {
        if (request.getHorarioId() != null && request.getHorarioId() < 1) {
            throw new InvalidDataException("El ID del horario debe ser mayor que cero");
        }
        if (request.getProfesorUsuarioId() != null && request.getProfesorUsuarioId() < 1) {
            throw new InvalidDataException("El ID del profesor debe ser mayor que cero");
        }
    }

    private void validarHorarioSiInformado(Integer horarioId) {
        if (horarioId == null) return;
        HorarioResponseDTO horario = horarioDAO.obtenerPorId(horarioId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el horario indicado"));
        if (!Boolean.TRUE.equals(horario.getActivo())) {
            throw new InvalidDataException("No se puede programar una sesión en un horario inactivo");
        }
    }

    private void validarProfesorSiInformado(Integer profesorUsuarioId) {
        if (profesorUsuarioId == null) return;
        UsuarioResponseDTO profesor = usuarioDAO.obtenerPorId(profesorUsuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el usuario profesor indicado"));
        if (!Boolean.TRUE.equals(profesor.getActivo())) {
            throw new InvalidDataException("No se puede asignar un usuario inactivo como profesor");
        }
        if (profesor.getRoles() == null || !profesor.getRoles().contains("PROFESOR")) {
            throw new InvalidDataException("El usuario seleccionado no tiene el rol de profesor");
        }
    }

    private void validarId(Integer id, String campo) {
        if (id == null || id < 1) {
            throw new InvalidDataException("El ID de " + campo + " debe ser mayor que cero");
        }
    }
}
