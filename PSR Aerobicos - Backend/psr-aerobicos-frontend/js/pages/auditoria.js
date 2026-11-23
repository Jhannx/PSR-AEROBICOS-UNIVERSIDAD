// Lógica de Auditoría de Actividad: filtrado por tabla afectada y consulta de vista_historial_cambios

document.addEventListener("DOMContentLoaded", function () {
  const contenedorFiltros = document.getElementById("auditoria-table-filters");
  const contenedorRegistros = document.getElementById("auditoria-records-list");
  const inputDesde = document.getElementById("auditoria-desde");
  const inputHasta = document.getElementById("auditoria-hasta");
  const btnRefrescar = document.getElementById("btn-refrescar-auditoria");

  let tablaSeleccionada = "all";

  function aplicarFiltroTabla() {
    if (!contenedorRegistros) {
      return;
    }
    const filas = contenedorRegistros.querySelectorAll(".audit-row");
    filas.forEach(function (fila) {
      const tablaFila = fila.getAttribute("data-table") || "";
      const visible = tablaSeleccionada === "all" || tablaFila === tablaSeleccionada;
      fila.classList.toggle("d-none", !visible);
    });
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
        aplicarFiltroTabla();
      });
    });
  }

  async function consultarAuditoria() {
    try {
      const registros = await ClienteHttp.get("/auditoria/historial-cambios");
      if (!Array.isArray(registros) || registros.length === 0 || !contenedorRegistros) {
        return;
      }

      contenedorRegistros.innerHTML = registros
        .map(function (reg) {
          const tabla = reg.tablaAfectada || "movimiento_cuenta";
          const accion = reg.accion || "UPDATE";
          const badgeColor = accion === "DELETE" ? "badge-rose" : tabla === "tarifa" ? "badge-purple" : "badge-amber";

          return (
            '<article class="audit-row" data-table="' + tabla + '">' +
              '<div class="d-flex align-items-center gap-2">' +
                '<span class="psr-badge ' + badgeColor + '">' + accion + ' · ' + tabla + '</span>' +
                '<span class="text-secondary small">Registro #' + (reg.registroId || 1) + ' · ' + (reg.campoModificado || "") + '</span>' +
              '</div>' +
              '<div class="audit-diff">' +
                '<span class="audit-old-val">' + (reg.valorAnterior || "—") + '</span>' +
                '<i class="ph-bold ph-arrow-right text-secondary"></i>' +
                '<span class="audit-new-val">' + (reg.valorNuevo || "—") + '</span>' +
              '</div>' +
              '<div class="text-end">' +
                '<span class="text-white fw-semibold small">' + (reg.realizadoPor || "Sistema") + '</span>' +
                '<span class="text-secondary small ms-2">' + ValidacionesPSR.formatearFechaHora(reg.fechaHora) + '</span>' +
              '</div>' +
            '</article>'
          );
        })
        .join("");

      aplicarFiltroTabla();
    } catch (e) {
      // Mantiene los registros de ejemplo en pantalla si el backend no está en ejecución
    }
  }

  if (btnRefrescar) {
    btnRefrescar.addEventListener("click", consultarAuditoria);
  }
  if (inputDesde) {
    inputDesde.addEventListener("change", consultarAuditoria);
  }
  if (inputHasta) {
    inputHasta.addEventListener("change", consultarAuditoria);
  }

  consultarAuditoria();
});
