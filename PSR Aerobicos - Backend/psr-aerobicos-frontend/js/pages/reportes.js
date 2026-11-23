// Lógica de Reportes: indicadores por periodo, recaudo diario, saldos y asistencias por fecha

document.addEventListener("DOMContentLoaded", function () {
  const navTabs = document.getElementById("reportes-tabs");
  const periodPills = document.getElementById("reportes-period-pills");
  const kpiAsistentes = document.getElementById("rep-kpi-asistentes");
  const kpiRecaudo = document.getElementById("rep-kpi-recaudo");
  const kpiMetodo = document.getElementById("rep-kpi-metodo");
  const chartCard = document.getElementById("reportes-chart-card");
  const tableTitle = document.getElementById("reportes-table-title");
  const tableHead = document.querySelector("#reportes-main-table thead");
  const tableBody = document.getElementById("reportes-table-body");
  const inputFecha = document.getElementById("reporte-fecha-input");
  const btnConsultarFecha = document.getElementById("btn-consultar-reporte-fecha");

  const METRICAS_POR_PERIODO = {
    day: { asistentes: "24", recaudo: "$120.000", metodo: "EFECTIVO" },
    week: { asistentes: "86", recaudo: "$430.000", metodo: "EFECTIVO" },
    month: { asistentes: "340", recaudo: "$1.700.000", metodo: "EFECTIVO" },
    year: { asistentes: "1.420", recaudo: "$7.100.000", metodo: "EFECTIVO" }
  };

  // Selector de periodo (Día, Semana, Mes, Año)
  if (periodPills) {
    const botonesPeriodo = periodPills.querySelectorAll(".filter-pill");
    botonesPeriodo.forEach(function (boton) {
      boton.addEventListener("click", function () {
        botonesPeriodo.forEach(function (b) {
          b.classList.remove("is-active");
        });
        boton.classList.add("is-active");

        const periodo = boton.getAttribute("data-period") || "day";
        const datos = METRICAS_POR_PERIODO[periodo] || METRICAS_POR_PERIODO.day;

        if (kpiAsistentes) {
          kpiAsistentes.textContent = datos.asistentes;
        }
        if (kpiRecaudo) {
          kpiRecaudo.textContent = datos.recaudo;
        }
        if (kpiMetodo) {
          kpiMetodo.textContent = datos.metodo;
        }
      });
    });
  }

  function renderizarTablaSegunTab(tab) {
    if (!tableHead || !tableBody || !tableTitle) {
      return;
    }

    if (tab === "cuentas") {
      tableTitle.textContent = "Estados de Cuenta de Participantes (vista_saldo_persona)";
      tableHead.innerHTML =
        "<tr>" +
          "<th>Participante</th>" +
          "<th>Documento</th>" +
          "<th>Total Abonado</th>" +
          "<th>Total Cobrado</th>" +
          "<th>Saldo Neto</th>" +
          "<th>Estado</th>" +
        "</tr>";
      tableBody.innerHTML =
        "<tr>" +
          '<td class="text-white fw-semibold">Camila Andrea Torres</td>' +
          '<td class="font-monospace">CC 1067890123</td>' +
          "<td>$25.000</td>" +
          "<td>$25.000</td>" +
          '<td class="fw-bold">$0</td>' +
          '<td><span class="psr-badge badge-emerald">AL_DIA</span></td>' +
        "</tr>" +
        "<tr>" +
          '<td class="text-white fw-semibold">Mateo Hernández Ruiz</td>' +
          '<td class="font-monospace">CC 1067912345</td>' +
          "<td>$35.000</td>" +
          "<td>$20.000</td>" +
          '<td class="fw-bold" style="color: var(--emerald-400);">+$15.000</td>' +
          '<td><span class="psr-badge badge-blue">CON_SALDO_A_FAVOR</span></td>' +
        "</tr>" +
        "<tr>" +
          '<td class="text-white fw-semibold">Valentina Mendoza Pineda</td>' +
          '<td class="font-monospace">CC 1067954321</td>' +
          "<td>$10.000</td>" +
          "<td>$15.000</td>" +
          '<td class="fw-bold" style="color: var(--rose-400);">-$5.000</td>' +
          '<td><span class="psr-badge badge-rose">DEBE</span></td>' +
        "</tr>";
      return;
    }

    if (tab === "asistencias-fecha") {
      tableTitle.textContent = "Reporte de Asistencias por Fecha (sp_reporte_asistencias_por_fecha)";
      tableHead.innerHTML =
        "<tr>" +
          "<th>Fecha</th>" +
          "<th>Participante</th>" +
          "<th>Documento</th>" +
          "<th>Hora Llegada</th>" +
          "<th>Hora Confirmación</th>" +
          "<th>Monto Cobrado</th>" +
        "</tr>";
      tableBody.innerHTML =
        "<tr>" +
          "<td>22/11/2026</td>" +
          '<td class="text-white fw-semibold">Sebastián Castro Díaz</td>' +
          '<td class="font-monospace">CC 1067811223</td>' +
          "<td>05:49 p. m.</td>" +
          "<td>05:51 p. m.</td>" +
          '<td class="fw-bold" style="color: var(--emerald-400);">$5.000</td>' +
        "</tr>" +
        "<tr>" +
          "<td>22/11/2026</td>" +
          '<td class="text-white fw-semibold">Camila Andrea Torres</td>' +
          '<td class="font-monospace">CC 1067890123</td>' +
          "<td>05:52 p. m.</td>" +
          "<td>05:55 p. m.</td>" +
          '<td class="fw-bold" style="color: var(--emerald-400);">$5.000</td>' +
        "</tr>";
      return;
    }

    tableTitle.textContent = "Detalle de Recaudo y Sesiones (vista_recaudo_diario)";
    tableHead.innerHTML =
      "<tr>" +
        "<th>Fecha</th>" +
        "<th>Sesión</th>" +
        "<th>Profesor(a)</th>" +
        "<th>Asistentes</th>" +
        "<th>Total Recaudado</th>" +
        "<th>Estado</th>" +
      "</tr>";
    tableBody.innerHTML =
      "<tr>" +
        "<td>22/11/2026</td>" +
        '<td class="font-monospace">Sesión #14 (06:00 p. m.)</td>' +
        '<td class="text-white fw-semibold">Valeria Gómez</td>' +
        "<td>9</td>" +
        '<td class="fw-bold" style="color: var(--emerald-400);">$45.000</td>' +
        '<td><span class="psr-badge badge-emerald">En curso</span></td>' +
      "</tr>" +
      "<tr>" +
        "<td>21/11/2026</td>" +
        '<td class="font-monospace">Sesión #13 (06:00 p. m.)</td>' +
        '<td class="text-white fw-semibold">Valeria Gómez</td>' +
        "<td>15</td>" +
        '<td class="fw-bold" style="color: var(--emerald-400);">$75.000</td>' +
        '<td><span class="psr-badge badge-neutral">Finalizada</span></td>' +
      "</tr>" +
      "<tr>" +
        "<td>20/11/2026</td>" +
        '<td class="font-monospace">Sesión #12 (06:00 p. m.)</td>' +
        '<td class="text-white fw-semibold">Andrés Morales</td>' +
        "<td>12</td>" +
        '<td class="fw-bold" style="color: var(--emerald-400);">$60.000</td>' +
        '<td><span class="psr-badge badge-neutral">Finalizada</span></td>' +
      "</tr>";
  }

  // Cambio entre las pestañas de Reportes
  if (navTabs) {
    const botonesTab = navTabs.querySelectorAll(".psr-tab");
    botonesTab.forEach(function (boton) {
      boton.addEventListener("click", function () {
        botonesTab.forEach(function (b) {
          b.classList.remove("is-active");
        });
        boton.classList.add("is-active");

        const tab = boton.getAttribute("data-tab") || "resumen";
        if (chartCard) {
          chartCard.classList.toggle("d-none", tab !== "resumen");
        }
        renderizarTablaSegunTab(tab);
      });
    });
  }

  if (btnConsultarFecha) {
    btnConsultarFecha.addEventListener("click", async function () {
      const fecha = inputFecha ? inputFecha.value : "";
      if (!fecha) {
        return;
      }
      try {
        await ClienteHttp.get("/reportes/asistencias-fecha?fecha=" + encodeURIComponent(fecha));
      } catch (e) {
        // Mantiene la tabla actual si el backend aún no responde
      }
    });
  }
});
