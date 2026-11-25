// Lógica de Reportes con consumo paginado de endpoints reales

document.addEventListener("DOMContentLoaded", function () {
  const navTabs = document.getElementById("reportes-tabs");
  const periodPills = document.getElementById("reportes-period-pills");
  const alertBox = document.getElementById("reportes-alert-box");

  const kpiAsistentes = document.getElementById("rep-kpi-asistentes");
  const kpiAsistentesSub = document.getElementById("rep-kpi-asistentes-sub");
  const kpiRecaudo = document.getElementById("rep-kpi-recaudo");
  const kpiRecaudoSub = document.getElementById("rep-kpi-recaudo-sub");
  const kpiMetodo = document.getElementById("rep-kpi-metodo");
  const kpiMetodoSub = document.getElementById("rep-kpi-metodo-sub");
  const kpiFrecuente = document.getElementById("rep-kpi-frecuente");
  const kpiFrecuenteSub = document.getElementById("rep-kpi-frecuente-sub");

  const chartCard = document.getElementById("reportes-chart-card");
  const contenedorBarras = document.getElementById("reportes-weekly-bars");
  const tableTitle = document.getElementById("reportes-table-title");
  const tableHead = document.querySelector("#reportes-main-table thead");
  const tableBody = document.getElementById("reportes-table-body");
  const contenedorPaginacion = document.getElementById("reportes-paginacion");

  const inputFecha = document.getElementById("reporte-fecha-input");
  const btnConsultarFecha = document.getElementById("btn-consultar-reporte-fecha");

  const REGISTROS_POR_PAGINA = 20;
  let tabActual = "resumen";
  let periodoActual = "day";
  let paginaActual = 0;

  let sesionesDestacadasCache = [];
  let recaudoDiarioCache = [];

  function escaparHtml(texto) {
    return String(texto == null ? "" : texto)
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;");
  }

  function obtenerFechaIsoHoy() {
    const hoy = new Date();
    return (
      hoy.getFullYear() +
      "-" +
      String(hoy.getMonth() + 1).padStart(2, "0") +
      "-" +
      String(hoy.getDate()).padStart(2, "0")
    );
  }

  function filtrarPorPeriodo(lista, campoFecha, periodo) {
    if (!Array.isArray(lista) || lista.length === 0) {
      return [];
    }
    const ahora = new Date();
    const inicio = new Date(ahora.getFullYear(), ahora.getMonth(), ahora.getDate());
    if (periodo === "week") inicio.setDate(inicio.getDate() - ((inicio.getDay() + 6) % 7));
    if (periodo === "month") inicio.setDate(1);
    if (periodo === "year") inicio.setMonth(0, 1);
    const fin = new Date(ahora.getFullYear(), ahora.getMonth(), ahora.getDate() + 1);

    return lista.filter(function (item) {
      const valorFecha = item[campoFecha];
      if (!valorFecha) {
        return false;
      }
      const texto = String(valorFecha).slice(0, 10);
      const partes = texto.includes("/") ? texto.split("/") : texto.split("-").reverse();
      const fechaItem = new Date(Number(partes[2]), Number(partes[1]) - 1, Number(partes[0]));
      if (Number.isNaN(fechaItem.getTime())) {
        return false;
      }
      return fechaItem >= inicio && fechaItem < fin;
    });
  }

  function actualizarIndicadoresPorPeriodo() {
    const sesionesPeriodo = filtrarPorPeriodo(sesionesDestacadasCache, "fecha", periodoActual);
    const recaudosPeriodo = filtrarPorPeriodo(recaudoDiarioCache, "dia", periodoActual);

    const totalAsistencias = sesionesPeriodo.reduce(function (acc, s) {
      return acc + Number(s.totalAsistenciasSesion || 0);
    }, 0);
    const promedioAsistencias =
      sesionesPeriodo.length > 0 ? Math.round(totalAsistencias / sesionesPeriodo.length) : 0;

    const totalRecaudo = recaudosPeriodo.reduce(function (acc, r) {
      return acc + Number(r.recaudoDia || 0);
    }, 0);
    const totalPagos = recaudosPeriodo.reduce(function (acc, r) {
      return acc + Number(r.totalPagos || 0);
    }, 0);

    if (kpiAsistentes) {
      kpiAsistentes.textContent = String(totalAsistencias);
    }
    if (kpiAsistentesSub) {
      kpiAsistentesSub.textContent =
        "Promedio por sesión: " + promedioAsistencias + (promedioAsistencias === 1 ? " persona" : " personas");
    }
    if (kpiRecaudo) {
      kpiRecaudo.textContent = ValidacionesPSR.formatearCOP(totalRecaudo);
    }
    if (kpiRecaudoSub) {
      kpiRecaudoSub.textContent = totalPagos + (totalPagos === 1 ? " pago registrado" : " pagos registrados");
    }
  }

  function normalizarClaveDia(item) {
    const textoDia = String(item.diaSemana || "").toUpperCase();
    if (textoDia.indexOf("LUN") !== -1 || textoDia.indexOf("MON") !== -1) return "LUN";
    if (textoDia.indexOf("MAR") !== -1 || textoDia.indexOf("TUE") !== -1) return "MAR";
    if (textoDia.indexOf("MIE") !== -1 || textoDia.indexOf("MIÉ") !== -1 || textoDia.indexOf("WED") !== -1) return "MIE";
    if (textoDia.indexOf("JUE") !== -1 || textoDia.indexOf("THU") !== -1) return "JUE";
    if (textoDia.indexOf("VIE") !== -1 || textoDia.indexOf("FRI") !== -1) return "VIE";
    if (textoDia.indexOf("SAB") !== -1 || textoDia.indexOf("SÁB") !== -1 || textoDia.indexOf("SAT") !== -1) return "SAB";
    if (textoDia.indexOf("DOM") !== -1 || textoDia.indexOf("SUN") !== -1) return "DOM";

    if (item.fecha) {
      const f = new Date(String(item.fecha).slice(0, 10) + "T12:00:00");
      if (!Number.isNaN(f.getTime())) {
        const mapa = ["DOM", "LUN", "MAR", "MIE", "JUE", "VIE", "SAB"];
        return mapa[f.getDay()] || "LUN";
      }
    }
    return "LUN";
  }

  function actualizarGraficoSemanal(sesiones) {
    if (!contenedorBarras) {
      return;
    }
    const acumulados = { LUN: 0, MAR: 0, MIE: 0, JUE: 0, VIE: 0, SAB: 0, DOM: 0 };
    sesiones.forEach(function (s) {
      const clave = normalizarClaveDia(s);
      acumulados[clave] = (acumulados[clave] || 0) + Number(s.totalAsistenciasSesion || 0);
    });

    const maximo = Math.max(
      1,
      acumulados.LUN,
      acumulados.MAR,
      acumulados.MIE,
      acumulados.JUE,
      acumulados.VIE,
      acumulados.SAB,
      acumulados.DOM
    );

    Object.keys(acumulados).forEach(function (clave) {
      const barra = contenedorBarras.querySelector('[data-day-bar="' + clave + '"]');
      if (barra) {
        const porcentaje = acumulados[clave] > 0 ? Math.max(12, Math.round((acumulados[clave] / maximo) * 100)) : 4;
        barra.style.height = porcentaje + "%";
        barra.title = clave + ": " + acumulados[clave] + " asistencias";
      }
    });
  }

  async function obtenerTodasLasPaginas(ruta) {
    const registros = [];
    let pagina = 0;
    let totalPaginas = 1;
    while (pagina < totalPaginas) {
      const respuesta = await ClienteHttp.get(ruta + "?page=" + pagina + "&size=100");
      registros.push(...(Array.isArray(respuesta) ? respuesta : respuesta.content || []));
      totalPaginas = respuesta && respuesta.page ? Number(respuesta.page.totalPages || 0) : 1;
      pagina += 1;
    }
    return registros;
  }

  async function cargarIndicadoresYGrafico() {
    const promesas = await Promise.allSettled([
      obtenerTodasLasPaginas("/reportes/recaudo-diario"),
      obtenerTodasLasPaginas("/reportes/sesiones/destacadas-por-dia"),
      ClienteHttp.get("/reportes/pagos/mas-utilizados"),
      ClienteHttp.get("/reportes/alumnos/destacados")
    ]);

    const resRecaudo = promesas[0].status === "fulfilled" ? promesas[0].value : null;
    const resSesiones = promesas[1].status === "fulfilled" ? promesas[1].value : null;
    const resMetodos = promesas[2].status === "fulfilled" ? promesas[2].value : null;
    const resAlumnos = promesas[3].status === "fulfilled" ? promesas[3].value : null;

    recaudoDiarioCache = Array.isArray(resRecaudo) ? resRecaudo : [];
    sesionesDestacadasCache = Array.isArray(resSesiones) ? resSesiones : [];

    const metodos = Array.isArray(resMetodos)
      ? resMetodos
      : resMetodos && Array.isArray(resMetodos.content)
        ? resMetodos.content
        : [];

    const alumnos = Array.isArray(resAlumnos)
      ? resAlumnos
      : resAlumnos && Array.isArray(resAlumnos.content)
        ? resAlumnos.content
        : [];

    actualizarIndicadoresPorPeriodo();
    actualizarGraficoSemanal(filtrarPorPeriodo(sesionesDestacadasCache, "fecha", periodoActual));
    if (tabActual === "resumen") cargarTablaRecaudoDiario(0);

    if (metodos.length > 0) {
      const principal = metodos[0];
      const totalTransacciones = metodos.reduce(function (acc, m) {
        return acc + Number(m.totalPagos || 0);
      }, 0);
      const pct =
        totalTransacciones > 0
          ? Math.round((Number(principal.totalPagos || 0) / totalTransacciones) * 100)
          : 100;

      if (kpiMetodo) {
        kpiMetodo.textContent = principal.metodoPago || "EFECTIVO";
      }
      if (kpiMetodoSub) {
        kpiMetodoSub.textContent = pct + "% de las transacciones (" + (principal.totalPagos || 0) + " pagos)";
      }
    }

    if (alumnos.length > 0) {
      const topAlumno = alumnos[0];
      if (kpiFrecuente) {
        kpiFrecuente.textContent = topAlumno.nombreCompleto || "—";
      }
      if (kpiFrecuenteSub) {
        kpiFrecuenteSub.textContent =
          (topAlumno.totalAsistencias || 0) + " asistencias · " +
          ValidacionesPSR.formatearCOP(topAlumno.montoTotalAcumulado || 0);
      }
    }
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
      '<button type="button" class="btn-psr-secondary py-1 px-3" id="btn-reportes-prev" ' +
        (actual <= 0 ? "disabled" : "") +
      ">Anterior</button>" +
      '<span class="small text-secondary">Página ' +
        (actual + 1) +
        " de " +
        totalPaginas +
        " (" +
        totalElementos +
        " registros)</span>" +
      '<button type="button" class="btn-psr-secondary py-1 px-3" id="btn-reportes-next" ' +
        (actual + 1 >= totalPaginas ? "disabled" : "") +
      ">Siguiente</button>";

    const btnPrev = document.getElementById("btn-reportes-prev");
    const btnNext = document.getElementById("btn-reportes-next");
    if (btnPrev) {
      btnPrev.addEventListener("click", function () {
        if (paginaActual > 0) {
          cargarTablaSegunTab(tabActual, paginaActual - 1);
        }
      });
    }
    if (btnNext) {
      btnNext.addEventListener("click", function () {
        if (paginaActual + 1 < totalPaginas) {
          cargarTablaSegunTab(tabActual, paginaActual + 1);
        }
      });
    }
  }

  async function cargarTablaRecaudoDiario(pagina) {
    if (!tableHead || !tableBody || !tableTitle) {
      return;
    }
    tableTitle.textContent = "Detalle de recaudo · " + ({ day: "Hoy", week: "Esta semana", month: "Este mes", year: "Este año" }[periodoActual] || "Periodo");
    tableHead.innerHTML =
      "<tr>" +
        "<th>Fecha</th>" +
        "<th>Pagos Registrados</th>" +
        "<th>Total Recaudado</th>" +
        "<th>Promedio por Pago</th>" +
      "</tr>";

    try {
      const filtrados = filtrarPorPeriodo(recaudoDiarioCache, "dia", periodoActual);
      const filas = filtrados.slice(pagina * REGISTROS_POR_PAGINA, (pagina + 1) * REGISTROS_POR_PAGINA);
      const metaPagina = {
        number: pagina,
        totalPages: Math.ceil(filtrados.length / REGISTROS_POR_PAGINA),
        totalElements: filtrados.length
      };

      if (filas.length === 0) {
        tableBody.innerHTML =
          '<tr><td colspan="4" class="text-center text-secondary py-4">No hay recaudo en el periodo seleccionado.</td></tr>';
        renderizarPaginacion(null);
        return;
      }

      tableBody.innerHTML = filas
        .map(function (r) {
          const totalPagos = Number(r.totalPagos || 0);
          const recaudoDia = Number(r.recaudoDia || 0);
          const promedio = totalPagos > 0 ? Math.round(recaudoDia / totalPagos) : 0;

          return (
            "<tr>" +
              "<td>" + ValidacionesPSR.formatearFecha(r.dia) + "</td>" +
              '<td class="font-monospace">' + totalPagos + "</td>" +
              '<td class="fw-bold" style="color: var(--emerald-400);">' +
                ValidacionesPSR.formatearCOP(recaudoDia) +
              "</td>" +
              "<td>" + ValidacionesPSR.formatearCOP(promedio) + "</td>" +
            "</tr>"
          );
        })
        .join("");

      renderizarPaginacion(metaPagina);
    } catch (error) {
      tableBody.innerHTML =
        '<tr><td colspan="4" class="text-center text-secondary py-4">No fue posible consultar el recaudo diario.</td></tr>';
      renderizarPaginacion(null);
      ValidacionesPSR.mostrarAlerta(alertBox, error.message, "error");
    }
  }

  async function cargarTablaSaldosPersonas(pagina) {
    if (!tableHead || !tableBody || !tableTitle) {
      return;
    }
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

    try {
      const respuesta = await ClienteHttp.get(
        "/reportes/saldos-personas?page=" + pagina + "&size=" + REGISTROS_POR_PAGINA
      );
      const filas = Array.isArray(respuesta)
        ? respuesta
        : respuesta && Array.isArray(respuesta.content)
          ? respuesta.content
          : [];
      const metaPagina = respuesta && respuesta.page ? respuesta.page : null;

      if (filas.length === 0) {
        tableBody.innerHTML =
          '<tr><td colspan="6" class="text-center text-secondary py-4">No hay estados de cuenta disponibles.</td></tr>';
        renderizarPaginacion(null);
        return;
      }

      tableBody.innerHTML = filas
        .map(function (item) {
          const nombre = escaparHtml(item.nombreCompleto || "Participante");
          const documento = escaparHtml(item.numeroDocumento || "—");
          const abonado = Number(item.totalAbonado || 0);
          const cobrado = Number(item.totalCobrado || 0);
          const saldo = Number(item.saldo || 0);
          const estado = escaparHtml(item.estadoCuenta || "AL_DIA");

          let colorSaldo = "";
          let prefijoSaldo = "";
          let claseBadge = "badge-emerald";

          if (saldo > 0 || estado === "CON_SALDO_A_FAVOR") {
            colorSaldo = "color: var(--emerald-400);";
            prefijoSaldo = "+";
            claseBadge = "badge-blue";
          } else if (saldo < 0 || estado === "DEBE") {
            colorSaldo = "color: var(--rose-400);";
            prefijoSaldo = "-";
            claseBadge = "badge-rose";
          }

          return (
            "<tr>" +
              '<td class="text-white fw-semibold">' + nombre + "</td>" +
              '<td class="font-monospace">' + documento + "</td>" +
              "<td>" + ValidacionesPSR.formatearCOP(abonado) + "</td>" +
              "<td>" + ValidacionesPSR.formatearCOP(cobrado) + "</td>" +
              '<td class="fw-bold" style="' + colorSaldo + '">' +
                prefijoSaldo + ValidacionesPSR.formatearCOP(Math.abs(saldo)) +
              "</td>" +
              '<td><span class="psr-badge ' + claseBadge + '">' + estado + "</span></td>" +
            "</tr>"
          );
        })
        .join("");

      renderizarPaginacion(metaPagina);
    } catch (error) {
      tableBody.innerHTML =
        '<tr><td colspan="6" class="text-center text-secondary py-4">No fue posible consultar los estados de cuenta.</td></tr>';
      renderizarPaginacion(null);
      ValidacionesPSR.mostrarAlerta(alertBox, error.message, "error");
    }
  }

  async function cargarTablaAsistenciasPorFecha() {
    if (!tableHead || !tableBody || !tableTitle) {
      return;
    }
    renderizarPaginacion(null);

    if (inputFecha && !inputFecha.value) {
      inputFecha.value = obtenerFechaIsoHoy();
    }
    const fechaConsulta = inputFecha && inputFecha.value ? inputFecha.value : obtenerFechaIsoHoy();

    tableTitle.textContent =
      "Reporte de Asistencias por Fecha (" + ValidacionesPSR.formatearFecha(fechaConsulta) + ")";
    tableHead.innerHTML =
      "<tr>" +
        "<th>Fecha</th>" +
        "<th>Horario Clase</th>" +
        "<th>Participante</th>" +
        "<th>Documento</th>" +
        "<th>Hora Llegada</th>" +
        "<th>Monto Cobrado</th>" +
        "<th>Confirmado Por</th>" +
      "</tr>";

    try {
      const respuesta = await ClienteHttp.get(
        "/reportes/asistencias-fecha?fecha=" + encodeURIComponent(fechaConsulta)
      );
      const filas = Array.isArray(respuesta)
        ? respuesta
        : respuesta && Array.isArray(respuesta.content)
          ? respuesta.content
          : [];

      if (filas.length === 0) {
        tableBody.innerHTML =
          '<tr><td colspan="7" class="text-center text-secondary py-4">No se registraron asistencias para la fecha seleccionada.</td></tr>';
        return;
      }

      tableBody.innerHTML = filas
        .map(function (a) {
          return (
            "<tr>" +
              "<td>" + ValidacionesPSR.formatearFecha(a.fechaSesion || fechaConsulta) + "</td>" +
              '<td class="font-monospace">' + ValidacionesPSR.formatearHora(a.horaClase) + "</td>" +
              '<td class="text-white fw-semibold">' + escaparHtml(a.alumno || "Participante") + "</td>" +
              '<td class="font-monospace">' + escaparHtml(a.numeroDocumento || "—") + "</td>" +
              "<td>" + ValidacionesPSR.formatearHora(a.horaLlegada) + " (" + escaparHtml(a.metodoCheckin || "MANUAL") + ")</td>" +
              '<td class="fw-bold" style="color: var(--emerald-400);">' +
                ValidacionesPSR.formatearCOP(a.montoCobrado || 0) +
              "</td>" +
              "<td>" + escaparHtml(a.confirmadoPor || "Recepción") + "</td>" +
            "</tr>"
          );
        })
        .join("");
    } catch (error) {
      tableBody.innerHTML =
        '<tr><td colspan="7" class="text-center text-secondary py-4">No fue posible consultar las asistencias de la fecha indicada.</td></tr>';
      ValidacionesPSR.mostrarAlerta(alertBox, error.message, "error");
    }
  }

  function cargarTablaSegunTab(tab, pagina) {
    tabActual = tab || "resumen";
    paginaActual = typeof pagina === "number" && pagina >= 0 ? pagina : 0;
    ValidacionesPSR.ocultarAlerta(alertBox);

    if (tabActual === "cuentas") {
      cargarTablaSaldosPersonas(paginaActual);
      return;
    }
    if (tabActual === "asistencias-fecha") {
      cargarTablaAsistenciasPorFecha();
      return;
    }
    cargarTablaRecaudoDiario(paginaActual);
  }

  // Selector de periodo (Día, Semana, Mes, Año)
  if (periodPills) {
    const botonesPeriodo = periodPills.querySelectorAll(".filter-pill");
    botonesPeriodo.forEach(function (boton) {
      boton.addEventListener("click", function () {
        botonesPeriodo.forEach(function (b) {
          b.classList.remove("is-active");
        });
        boton.classList.add("is-active");
        periodoActual = boton.getAttribute("data-period") || "day";
        actualizarIndicadoresPorPeriodo();
        actualizarGraficoSemanal(filtrarPorPeriodo(sesionesDestacadasCache, "fecha", periodoActual));
        if (tabActual === "resumen") cargarTablaRecaudoDiario(0);
      });
    });
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
        cargarTablaSegunTab(tab, 0);
      });
    });
  }

  if (btnConsultarFecha) {
    btnConsultarFecha.addEventListener("click", function () {
      if (navTabs) {
        const botonesTab = navTabs.querySelectorAll(".psr-tab");
        botonesTab.forEach(function (b) {
          b.classList.toggle("is-active", b.getAttribute("data-tab") === "asistencias-fecha");
        });
      }
      if (chartCard) {
        chartCard.classList.add("d-none");
      }
      cargarTablaSegunTab("asistencias-fecha", 0);
    });
  }

  cargarIndicadoresYGrafico();
  cargarTablaSegunTab("resumen", 0);
});
