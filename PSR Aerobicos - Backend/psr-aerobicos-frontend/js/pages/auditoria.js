// Lógica de Auditoría de Actividad: consulta paginada de vista_historial_cambios y filtros

document.addEventListener("DOMContentLoaded", function () {
  const contenedorFiltros = document.getElementById("auditoria-table-filters");
  const contenedorRegistros = document.getElementById("auditoria-records-list");
  const contenedorPaginacion = document.getElementById("auditoria-paginacion");
  const alertBox = document.getElementById("auditoria-alert-box");
  const inputDesde = document.getElementById("auditoria-desde");
  const inputHasta = document.getElementById("auditoria-hasta");
  const btnRefrescar = document.getElementById("btn-refrescar-auditoria");

  const REGISTROS_POR_PAGINA = 20;
  let paginaActual = 0;
  let tablaSeleccionada = "all";
  let registrosTodos = [];

  function escaparHtml(texto) {
    return String(texto == null ? "" : texto)
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;");
  }

  function cumpleFiltrosLocales(registro) {
    const tabla = String(registro.tablaAfectada || "");
    if (tablaSeleccionada !== "all" && tabla !== tablaSeleccionada) {
      return false;
    }
    const fechaIso = String(registro.fechaHora || "").slice(0, 10);
    const desde = inputDesde ? inputDesde.value.trim() : "";
    const hasta = inputHasta ? inputHasta.value.trim() : "";

    if (desde && fechaIso && fechaIso < desde) {
      return false;
    }
    if (hasta && fechaIso && fechaIso > hasta) {
      return false;
    }
    return true;
  }

  function renderizarRegistros() {
    if (!contenedorRegistros) {
      return;
    }

    const filtrados = registrosTodos.filter(cumpleFiltrosLocales);
    const pagina = filtrados.slice(paginaActual * REGISTROS_POR_PAGINA, (paginaActual + 1) * REGISTROS_POR_PAGINA);
    renderizarPaginacion({
      number: paginaActual,
      totalPages: Math.ceil(filtrados.length / REGISTROS_POR_PAGINA),
      totalElements: filtrados.length
    });

    if (pagina.length === 0) {
      contenedorRegistros.innerHTML =
        '<div class="psr-card text-center text-secondary py-4">' +
          "No se encontraron registros de auditoría para los filtros seleccionados." +
        "</div>";
      return;
    }

    contenedorRegistros.innerHTML = pagina
      .map(function (reg) {
        const tabla = escaparHtml(reg.tablaAfectada || "movimiento_cuenta");
        const accion = escaparHtml(reg.accion || "UPDATE");
        const badgeColor =
          accion === "DELETE"
            ? "badge-rose"
            : tabla === "tarifa"
              ? "badge-purple"
              : "badge-amber";
        const campo = escaparHtml(reg.campo || reg.campoModificado || "");
        const responsable = escaparHtml(
          reg.usuarioResponsable ||
          reg.usernameResponsable ||
          reg.realizadoPor ||
          (reg.usuarioId ? "Usuario #" + reg.usuarioId : "Sistema")
        );
        const valorAnterior = escaparHtml(reg.valorAnterior != null ? reg.valorAnterior : "—");
        const valorNuevo = escaparHtml(reg.valorNuevo != null ? reg.valorNuevo : "ELIMINADO");
        const registroId = reg.registroId != null ? reg.registroId : reg.id != null ? reg.id : 1;

        return (
          '<article class="audit-row" data-table="' + tabla + '">' +
            '<div class="d-flex align-items-center gap-2">' +
              '<span class="psr-badge ' + badgeColor + '">' + accion + " · " + tabla + "</span>" +
              '<span class="text-secondary small">Registro #' + registroId + (campo ? " · " + campo : "") + "</span>" +
            "</div>" +
            '<div class="audit-diff">' +
              '<span class="audit-old-val">' + valorAnterior + "</span>" +
              '<i class="ph-bold ph-arrow-right text-secondary"></i>' +
              '<span class="audit-new-val">' + valorNuevo + "</span>" +
            "</div>" +
            '<div class="text-end">' +
              '<span class="text-white fw-semibold small">' + responsable + "</span>" +
              '<span class="text-secondary small ms-2">' + ValidacionesPSR.formatearFechaHora(reg.fechaHora) + "</span>" +
            "</div>" +
          "</article>"
        );
      })
      .join("");
  }

  function renderizarPaginacion(metaPagina) {
    if (!contenedorPaginacion) {
      return;
    }
    if (!metaPagina || metaPagina.totalPages <= 1) {
      contenedorPaginacion.innerHTML = "";
      return;
    }

    const actual = Number(metaPagina.number || 0);
    const totalPaginas = Number(metaPagina.totalPages || 1);
    const totalElementos = Number(metaPagina.totalElements || 0);

    contenedorPaginacion.innerHTML =
      '<button type="button" class="btn-psr-secondary py-1 px-3" id="btn-auditoria-prev" ' +
        (actual <= 0 ? "disabled" : "") +
      ">Anterior</button>" +
      '<span class="small text-secondary">Página ' +
        (actual + 1) +
        " de " +
        totalPaginas +
        " (" +
        totalElementos +
        " eventos)</span>" +
      '<button type="button" class="btn-psr-secondary py-1 px-3" id="btn-auditoria-next" ' +
        (actual + 1 >= totalPaginas ? "disabled" : "") +
      ">Siguiente</button>";

    const btnPrev = document.getElementById("btn-auditoria-prev");
    const btnNext = document.getElementById("btn-auditoria-next");

    if (btnPrev) {
      btnPrev.addEventListener("click", function () {
        if (paginaActual > 0) {
          paginaActual -= 1;
          renderizarRegistros();
        }
      });
    }
    if (btnNext) {
      btnNext.addEventListener("click", function () {
        if (paginaActual + 1 < totalPaginas) {
          paginaActual += 1;
          renderizarRegistros();
        }
      });
    }
  }

  async function consultarAuditoria(pagina) {
    paginaActual = typeof pagina === "number" && pagina >= 0 ? pagina : 0;
    ValidacionesPSR.ocultarAlerta(alertBox);

    try {
      registrosTodos = [];
      let paginaRemota = 0;
      let totalPaginas = 1;
      while (paginaRemota < totalPaginas) {
        const respuesta = await ClienteHttp.get(
          "/auditoria/historial-cambios?page=" + paginaRemota + "&size=100"
        );
        registrosTodos.push(...(Array.isArray(respuesta) ? respuesta : respuesta.content || []));
        totalPaginas = respuesta && respuesta.page ? Number(respuesta.page.totalPages || 0) : 1;
        paginaRemota += 1;
      }
      renderizarRegistros();
    } catch (error) {
      registrosTodos = [];
      renderizarRegistros();
      ValidacionesPSR.mostrarAlerta(alertBox, error.message, "error");
    }
  }

  if (contenedorFiltros) {
    const botones = contenedorFiltros.querySelectorAll(".filter-pill");
    botones.forEach(function (boton) {
      boton.addEventListener("click", function () {
        botones.forEach(function (b) {
          b.classList.remove("is-active");
        });
        boton.classList.add("is-active");
        tablaSeleccionada = boton.getAttribute("data-table") || "all";
        paginaActual = 0;
        renderizarRegistros();
      });
    });
  }

  if (btnRefrescar) {
    btnRefrescar.addEventListener("click", function () {
      consultarAuditoria(paginaActual);
    });
  }
  if (inputDesde) {
    inputDesde.addEventListener("change", function () { paginaActual = 0; renderizarRegistros(); });
  }
  if (inputHasta) {
    inputHasta.addEventListener("change", function () { paginaActual = 0; renderizarRegistros(); });
  }

  consultarAuditoria(0);
});
