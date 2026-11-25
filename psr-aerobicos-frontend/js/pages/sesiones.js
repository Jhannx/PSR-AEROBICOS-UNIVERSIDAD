// Lógica de Sesiones de Clase y Horarios Recurrentes con soporte de paginación

document.addEventListener("DOMContentLoaded", function () {
  const navTabs = document.getElementById("sesiones-tabs");
  const seccionSesiones = document.getElementById("tab-content-sesiones");
  const seccionHorarios = document.getElementById("tab-content-horarios");
  const contenedorFiltrosEstado = document.getElementById("sesiones-status-filters");
  const gridSesiones = document.getElementById("sesiones-cards-grid");
  const paginacionSesiones = document.getElementById("sesiones-paginacion");
  const bandTitle = document.getElementById("sesiones-band-title");
  const bandCount = document.getElementById("sesiones-band-count");
  const alertBox = document.getElementById("sesiones-alert-box");

  const inputFiltroFecha = document.getElementById("sesiones-filtro-fecha");
  const btnFiltrarSesiones = document.getElementById("btn-filtrar-sesiones");
  const btnLimpiarFiltro = document.getElementById("btn-limpiar-filtro-sesiones");

  const tablaHorariosBody = document.getElementById("horarios-table-body");
  const selectProfesor = document.getElementById("sesion-profesor-id");
  const selectHorarioBase = document.getElementById("sesion-horario-id");

  const formCrearSesion = document.getElementById("form-crear-sesion");
  const formHorario = document.getElementById("form-horario");
  const btnAbrirNuevoHorario = document.getElementById("btn-abrir-nuevo-horario");
  const modalHorarioTitulo = document.getElementById("modalNuevoHorarioLabel");
  const inputHorarioIdHidden = document.getElementById("horario-id-hidden");

  const NOMBRES_DIAS = {
    1: "Lunes",
    2: "Martes",
    3: "Miércoles",
    4: "Jueves",
    5: "Viernes",
    6: "Sábado",
    7: "Domingo"
  };

  const REGISTROS_POR_PAGINA = 20;
  let paginaActualSesiones = 0;
  let estadoFiltroActual = "all";
  let horariosCargados = [];

  function escaparHtml(texto) {
    return String(texto == null ? "" : texto)
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;");
  }

  function formatearHora12(horaStr) {
    if (!horaStr) {
      return "—";
    }
    const hora = String(horaStr).includes("T") ? String(horaStr).split("T")[1] : String(horaStr);
    const partes = hora.split(":");
    if (partes.length < 2) {
      return String(horaStr);
    }
    const h = Number(partes[0]);
    const m = partes[1];
    if (Number.isNaN(h)) {
      return String(horaStr);
    }
    const sufijo = h >= 12 ? "p. m." : "a. m.";
    const h12 = h % 12 === 0 ? 12 : h % 12;
    return (h12 < 10 ? "0" + h12 : h12) + ":" + m + " " + sufijo;
  }

  function obtenerEstadoSesion(sesion) {
    if (sesion.cancelada) {
      return { codigo: "CANCELADA", texto: "Cancelada", badge: "badge-rose", claseCard: "is-cancelled" };
    }
    const ahora = new Date();
    const hoyIso =
      ahora.getFullYear() +
      "-" +
      String(ahora.getMonth() + 1).padStart(2, "0") +
      "-" +
      String(ahora.getDate()).padStart(2, "0");
    const fechaSesion = String(sesion.fecha || "");

    if (fechaSesion && fechaSesion < hoyIso) {
      return { codigo: "FINALIZADA", texto: "Finalizada", badge: "badge-neutral", claseCard: "" };
    }
    if (fechaSesion && fechaSesion > hoyIso) {
      return { codigo: "PROGRAMADA", texto: "Programada", badge: "badge-amber", claseCard: "" };
    }

    const minutosAhora = ahora.getHours() * 60 + ahora.getMinutes();
    const partesIni = String(sesion.horaInicio || "00:00").split("T").pop().split(":");
    const partesFin = String(sesion.horaFin || "23:59").split("T").pop().split(":");
    const minIni = Number(partesIni[0] || 0) * 60 + Number(partesIni[1] || 0);
    const minFin = Number(partesFin[0] || 23) * 60 + Number(partesFin[1] || 59);

    if (minutosAhora >= minIni && minutosAhora <= minFin) {
      return { codigo: "EN_CURSO", texto: "En curso", badge: "badge-emerald", claseCard: "is-in-progress" };
    }
    if (minutosAhora < minIni) {
      return { codigo: "PROGRAMADA", texto: "Programada", badge: "badge-amber", claseCard: "" };
    }
    return { codigo: "FINALIZADA", texto: "Finalizada", badge: "badge-neutral", claseCard: "" };
  }

  function renderizarPaginacionSesiones(metaPagina) {
    if (!paginacionSesiones) {
      return;
    }
    if (!metaPagina || metaPagina.totalPages <= 1) {
      paginacionSesiones.innerHTML = "";
      return;
    }
    const actual = Number(metaPagina.number || 0);
    const totalPaginas = Number(metaPagina.totalPages || 1);
    const totalElementos = Number(metaPagina.totalElements || 0);

    paginacionSesiones.innerHTML =
      '<button type="button" class="btn-psr-secondary py-1 px-3" id="btn-sesiones-prev" ' +
        (actual <= 0 ? "disabled" : "") +
      ">Anterior</button>" +
      '<span class="small text-secondary">Página ' +
        (actual + 1) +
        " de " +
        totalPaginas +
        " (" +
        totalElementos +
        " sesiones)</span>" +
      '<button type="button" class="btn-psr-secondary py-1 px-3" id="btn-sesiones-next" ' +
        (actual + 1 >= totalPaginas ? "disabled" : "") +
      ">Siguiente</button>";

    const btnPrev = document.getElementById("btn-sesiones-prev");
    const btnNext = document.getElementById("btn-sesiones-next");
    if (btnPrev) {
      btnPrev.addEventListener("click", function () {
        if (paginaActualSesiones > 0) {
          cargarSesiones(paginaActualSesiones - 1);
        }
      });
    }
    if (btnNext) {
      btnNext.addEventListener("click", function () {
        if (paginaActualSesiones + 1 < totalPaginas) {
          cargarSesiones(paginaActualSesiones + 1);
        }
      });
    }
  }

  function renderizarSesiones(sesiones, metaPagina, fechaFiltro) {
    if (!gridSesiones) {
      return;
    }
    const total = metaPagina && metaPagina.totalElements != null ? metaPagina.totalElements : sesiones.length;
    if (bandTitle) {
      bandTitle.textContent = fechaFiltro
        ? "Sesiones del " + ValidacionesPSR.formatearFecha(fechaFiltro)
        : "Listado General de Sesiones";
    }
    if (bandCount) {
      bandCount.textContent = total + (total === 1 ? " sesión" : " sesiones");
    }

    if (sesiones.length === 0) {
      gridSesiones.innerHTML =
        '<div class="text-secondary small py-3">No se encontraron sesiones para el criterio seleccionado.</div>';
      renderizarPaginacionSesiones(null);
      return;
    }

    gridSesiones.innerHTML = sesiones
      .map(function (sesion) {
        const estado = obtenerEstadoSesion(sesion);
        const rangoHora = formatearHora12(sesion.horaInicio) + " - " + formatearHora12(sesion.horaFin);
        const fechaTexto = ValidacionesPSR.formatearFecha(sesion.fecha);
        const instructor = escaparHtml(
          sesion.instructor ||
          (sesion.profesorUsuarioId ? "Profesor #" + sesion.profesorUsuarioId : "Sin asignar")
        );
        const totalAsistencias = Number(sesion.totalAsistencias || 0);
        const totalRecaudo = Number(sesion.totalRecaudo || 0);
        const puedeCancelar = !sesion.cancelada && estado.codigo !== "FINALIZADA";

        return (
          '<article class="session-card ' + estado.claseCard + '" data-sesion-id="' + sesion.id + '" data-estado="' + estado.codigo + '">' +
            "<div>" +
              '<div class="d-flex align-items-start justify-content-between gap-2 mb-2">' +
                '<h3 class="session-time-title">' + rangoHora + "</h3>" +
                '<span class="psr-badge ' + estado.badge + '">' + estado.texto + "</span>" +
              "</div>" +
              '<p class="session-meta-line">' +
                'Fecha: <strong class="text-white">' + fechaTexto + "</strong>" +
              "</p>" +
              '<p class="session-meta-line">' +
                'Profesor(a): <strong class="text-white">' + instructor + "</strong>" +
              "</p>" +
              '<p class="session-meta-line">' +
                'Recaudo acumulado: <strong style="color: var(--emerald-400);">' +
                  ValidacionesPSR.formatearCOP(totalRecaudo) +
                "</strong>" +
              "</p>" +
            "</div>" +
            '<div class="session-card-footer">' +
              '<div class="d-flex align-items-center gap-3">' +
                '<span style="color: var(--emerald-400);">' +
                  '<i class="ph-bold ph-check-circle"></i> ' + totalAsistencias + " asistieron" +
                "</span>" +
              "</div>" +
              (puedeCancelar
                ? '<button type="button" class="btn-psr-outline-danger py-1 px-2">' +
                    '<i class="ph-bold ph-prohibit"></i>' +
                    "<span>Cancelar</span>" +
                  "</button>"
                : '<span class="small text-secondary">' + (sesion.cancelada ? "Cancelada" : "Cerrada") + "</span>") +
            "</div>" +
          "</article>"
        );
      })
      .join("");

    renderizarPaginacionSesiones(metaPagina);
  }

  async function cargarSesiones(pagina) {
    paginaActualSesiones = typeof pagina === "number" && pagina >= 0 ? pagina : 0;
    const fechaFiltro = inputFiltroFecha ? inputFiltroFecha.value.trim() : "";
    const parametros = new URLSearchParams({ page: String(paginaActualSesiones), size: String(REGISTROS_POR_PAGINA) });
    if (fechaFiltro) parametros.set("fecha", fechaFiltro);
    if (estadoFiltroActual !== "all") parametros.set("estado", estadoFiltroActual);
    const endpoint = "/sesiones?" + parametros.toString();

    try {
      const respuesta = await ClienteHttp.get(endpoint);
      const lista = Array.isArray(respuesta)
        ? respuesta
        : respuesta && Array.isArray(respuesta.content)
          ? respuesta.content
          : [];
      const metaPagina = respuesta && respuesta.page ? respuesta.page : null;
      renderizarSesiones(lista, metaPagina, fechaFiltro);
    } catch (error) {
      renderizarSesiones([], null, fechaFiltro);
      ValidacionesPSR.mostrarAlerta(alertBox, error.message, "error");
    }
  }

  function renderizarHorarios(horarios) {
    horariosCargados = Array.isArray(horarios) ? horarios : [];

    if (selectHorarioBase) {
      const opcionesActivas = horariosCargados
        .filter(function (h) {
          return h.activo !== false;
        })
        .map(function (h) {
          const nombreDia = NOMBRES_DIAS[h.diaSemana] || "Día " + h.diaSemana;
          return (
            '<option value="' + h.id + '">' +
              nombreDia + " " + formatearHora12(h.horaInicio) + " - " + formatearHora12(h.horaFin) +
            "</option>"
          );
        })
        .join("");

      selectHorarioBase.innerHTML =
        '<option value="">Sesión extraordinaria (Sin horario base)</option>' + opcionesActivas;
    }

    if (!tablaHorariosBody) {
      return;
    }

    if (horariosCargados.length === 0) {
      tablaHorariosBody.innerHTML =
        '<tr><td colspan="6" class="text-center text-secondary py-4">No hay horarios recurrentes registrados.</td></tr>';
      return;
    }

    tablaHorariosBody.innerHTML = horariosCargados
      .map(function (h) {
        const nombreDia = NOMBRES_DIAS[h.diaSemana] || "Día " + h.diaSemana;
        const activo = h.activo !== false;
        const badgeEstado = activo
          ? '<span class="psr-badge badge-emerald">Activo</span>'
          : '<span class="psr-badge badge-neutral">Inactivo</span>';

        return (
          '<tr data-horario-id="' + h.id + '">' +
            '<td class="font-monospace">#' + h.id + "</td>" +
            '<td class="fw-bold text-white">' + nombreDia + "</td>" +
            "<td>" + formatearHora12(h.horaInicio) + "</td>" +
            "<td>" + formatearHora12(h.horaFin) + "</td>" +
            "<td>" + badgeEstado + "</td>" +
            '<td class="text-end">' +
              '<div class="d-inline-flex align-items-center gap-1">' +
                '<button type="button" class="btn-icon-action" data-action="editar-horario" title="Editar horario">' +
                  '<i class="ph-bold ph-pencil-simple"></i>' +
                "</button>" +
                (activo
                  ? '<button type="button" class="btn-icon-action is-danger" data-action="desactivar-horario" title="Desactivar horario">' +
                      '<i class="ph-bold ph-trash"></i>' +
                    "</button>"
                  : "") +
              "</div>" +
            "</td>" +
          "</tr>"
        );
      })
      .join("");
  }

  async function cargarHorarios() {
    try {
      const respuesta = await ClienteHttp.get("/horarios");
      const lista = Array.isArray(respuesta)
        ? respuesta
        : respuesta && Array.isArray(respuesta.content)
          ? respuesta.content
          : [];
      renderizarHorarios(lista);
    } catch (error) {
      renderizarHorarios([]);
    }
  }

  async function cargarProfesores() {
    if (!selectProfesor) {
      return;
    }
    try {
      const respuesta = await ClienteHttp.get("/usuarios/profesores?page=0&size=100");
      const profesores = Array.isArray(respuesta)
        ? respuesta
        : respuesta && Array.isArray(respuesta.content)
          ? respuesta.content
          : [];

      if (profesores.length === 0) {
        selectProfesor.innerHTML = '<option value="">Sin profesores disponibles</option>';
        return;
      }

      selectProfesor.innerHTML = profesores
        .map(function (p) {
          const nombre = escaparHtml(p.nombreCompleto || p.username || "Profesor #" + p.id);
          return '<option value="' + p.id + '">' + nombre + "</option>";
        })
        .join("");
    } catch (error) {
      selectProfesor.innerHTML = '<option value="">No fue posible cargar profesores</option>';
    }
  }

  // Cambio entre las pestañas Sesiones de Clase y Horarios Recurrentes
  if (navTabs) {
    const botonesTab = navTabs.querySelectorAll(".psr-tab");
    botonesTab.forEach(function (boton) {
      boton.addEventListener("click", function () {
        botonesTab.forEach(function (b) {
          b.classList.remove("is-active");
        });
        boton.classList.add("is-active");

        const tabActiva = boton.getAttribute("data-tab");
        if (seccionSesiones && seccionHorarios) {
          seccionSesiones.classList.toggle("d-none", tabActiva !== "sesiones");
          seccionHorarios.classList.toggle("d-none", tabActiva !== "horarios");
        }
      });
    });
  }

  // Filtrado de tarjetas de sesión por estado
  if (contenedorFiltrosEstado) {
    const botonesEstado = contenedorFiltrosEstado.querySelectorAll(".filter-pill");
    botonesEstado.forEach(function (boton) {
      boton.addEventListener("click", function () {
        botonesEstado.forEach(function (b) {
          b.classList.remove("is-active");
        });
        boton.classList.add("is-active");
        estadoFiltroActual = boton.getAttribute("data-status") || "all";
        cargarSesiones(0);
      });
    });
  }

  if (btnFiltrarSesiones) {
    btnFiltrarSesiones.addEventListener("click", function () {
      cargarSesiones(0);
    });
  }

  if (btnLimpiarFiltro) {
    btnLimpiarFiltro.addEventListener("click", function () {
      if (inputFiltroFecha) {
        inputFiltroFecha.value = "";
      }
      cargarSesiones(0);
    });
  }

  // Cancelación puntual de una sesión de clase
  if (gridSesiones) {
    gridSesiones.addEventListener("click", async function (evento) {
      const btnCancelar = evento.target.closest("button.btn-psr-outline-danger");
      if (!btnCancelar) {
        return;
      }
      const tarjeta = btnCancelar.closest(".session-card");
      if (!tarjeta) {
        return;
      }
      const sesionId = tarjeta.getAttribute("data-sesion-id");

      try {
        await ClienteHttp.del(
          "/sesiones/" + sesionId + "?usuarioId=" + SesionPSR.obtenerUsuario().id
        );
        ValidacionesPSR.mostrarAlerta(alertBox, "Sesión #" + sesionId + " cancelada correctamente.", "success");
        await cargarSesiones(paginaActualSesiones);
      } catch (error) {
        ValidacionesPSR.mostrarAlerta(alertBox, error.message, "error");
      }
    });
  }

  // Acciones en la tabla de horarios recurrentes (Editar / Desactivar)
  if (tablaHorariosBody) {
    tablaHorariosBody.addEventListener("click", async function (evento) {
      const boton = evento.target.closest("button[data-action]");
      if (!boton) {
        return;
      }
      const fila = boton.closest("tr[data-horario-id]");
      if (!fila) {
        return;
      }
      const horarioId = Number(fila.getAttribute("data-horario-id") || 0);
      const accion = boton.getAttribute("data-action");
      const horario = horariosCargados.find(function (h) {
        return Number(h.id) === horarioId;
      });

      if (!horario) {
        return;
      }

      if (accion === "editar-horario") {
        if (inputHorarioIdHidden) inputHorarioIdHidden.value = String(horario.id);
        if (modalHorarioTitulo) modalHorarioTitulo.textContent = "Editar Horario #" + horario.id;
        const selectDia = document.getElementById("horario-dia");
        const inputInicio = document.getElementById("horario-inicio");
        const inputFin = document.getElementById("horario-fin");
        if (selectDia) selectDia.value = String(horario.diaSemana || 1);
        if (inputInicio) inputInicio.value = String(horario.horaInicio || "18:00").slice(0, 5);
        if (inputFin) inputFin.value = String(horario.horaFin || "19:00").slice(0, 5);

        const modalEl = document.getElementById("modalNuevoHorario");
        const instancia = bootstrap.Modal.getOrCreateInstance(modalEl);
        instancia.show();
        return;
      }

      if (accion === "desactivar-horario") {
        try {
          await ClienteHttp.del("/horarios/" + horarioId);
          ValidacionesPSR.mostrarAlerta(alertBox, "Horario #" + horarioId + " desactivado.", "success");
          await cargarHorarios();
        } catch (error) {
          ValidacionesPSR.mostrarAlerta(alertBox, error.message, "error");
        }
      }
    });
  }

  if (btnAbrirNuevoHorario) {
    btnAbrirNuevoHorario.addEventListener("click", function () {
      if (inputHorarioIdHidden) inputHorarioIdHidden.value = "";
      if (modalHorarioTitulo) modalHorarioTitulo.textContent = "Registrar Horario Recurrente";
      if (formHorario) formHorario.reset();
    });
  }

  // Envío del modal Crear Sesión Manual
  if (formCrearSesion) {
    formCrearSesion.addEventListener("submit", async function (evento) {
      evento.preventDefault();
      const fecha = document.getElementById("sesion-fecha").value;
      const horaInicio = document.getElementById("sesion-hora-inicio").value;
      const horaFin = document.getElementById("sesion-hora-fin").value;
      const profesorId = document.getElementById("sesion-profesor-id").value;
      const horarioId = document.getElementById("sesion-horario-id").value;

      if (!fecha || !horaInicio || !horaFin || horaFin <= horaInicio || !Number.isInteger(Number(profesorId)) || Number(profesorId) <= 0) {
        ValidacionesPSR.mostrarAlerta(
          alertBox,
          "Verifica la fecha, el profesor y que la hora de fin sea posterior a la hora de inicio.",
          "error"
        );
        return;
      }

      try {
        await ClienteHttp.post("/sesiones", {
          fecha: fecha,
          horaInicio: horaInicio,
          horaFin: horaFin,
          profesorUsuarioId: Number(profesorId),
          horarioId: horarioId ? Number(horarioId) : null
        });
      } catch (error) {
        ValidacionesPSR.mostrarAlerta(alertBox, error.message, "error");
        return;
      }

      const modalEl = document.getElementById("modalCrearSesion");
      const instanciaModal = bootstrap.Modal.getInstance(modalEl);
      if (instanciaModal) {
        instanciaModal.hide();
      }
      ValidacionesPSR.mostrarAlerta(alertBox, "Sesión manual creada correctamente.", "success");
      await cargarSesiones(0);
    });
  }

  // Envío del modal Crear / Editar Horario Recurrente
  if (formHorario) {
    formHorario.addEventListener("submit", async function (evento) {
      evento.preventDefault();
      const horarioIdEdicion = inputHorarioIdHidden ? Number(inputHorarioIdHidden.value || 0) : 0;
      const dia = document.getElementById("horario-dia").value;
      const horaInicio = document.getElementById("horario-inicio").value;
      const horaFin = document.getElementById("horario-fin").value;

      if (!Number.isInteger(Number(dia)) || Number(dia) < 1 || Number(dia) > 7 || !horaInicio || !horaFin || horaFin <= horaInicio) {
        ValidacionesPSR.mostrarAlerta(
          alertBox,
          "Selecciona un día y verifica que la hora de fin sea posterior a la hora de inicio.",
          "error"
        );
        return;
      }

      const cuerpoHorario = {
        diaSemana: Number(dia),
        horaInicio: horaInicio,
        horaFin: horaFin,
        activo: true
      };

      try {
        if (horarioIdEdicion > 0) {
          await ClienteHttp.put("/horarios/" + horarioIdEdicion, cuerpoHorario);
        } else {
          await ClienteHttp.post("/horarios", cuerpoHorario);
        }
      } catch (error) {
        ValidacionesPSR.mostrarAlerta(alertBox, error.message, "error");
        return;
      }

      const modalEl = document.getElementById("modalNuevoHorario");
      const instanciaModal = bootstrap.Modal.getInstance(modalEl);
      if (instanciaModal) {
        instanciaModal.hide();
      }
      ValidacionesPSR.mostrarAlerta(
        alertBox,
        horarioIdEdicion > 0 ? "Horario actualizado correctamente." : "Horario recurrente registrado.",
        "success"
      );
      await cargarHorarios();
      await cargarSesiones(0);
    });
  }

  cargarProfesores();
  cargarHorarios();
  cargarSesiones(0);
});
