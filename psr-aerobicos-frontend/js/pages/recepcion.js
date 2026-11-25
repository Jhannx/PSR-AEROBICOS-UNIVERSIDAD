// Lógica operativa de Recepción: cola del día, filtros, check-in manual, cobros y asistencias

document.addEventListener("DOMContentLoaded", function () {
  const contenedorFiltros = document.getElementById("recepcion-filter-pills");
  const inputBusqueda = document.getElementById("recepcion-search-input");
  const contenedorCola = document.getElementById("recepcion-queue-list");
  const alertBox = document.getElementById("recepcion-alert-box");
  const btnRefrescar = document.getElementById("btn-refrescar-cola");

  const labelTarifa = document.getElementById("recepcion-tarifa-label");
  const badgeSesionTexto = document.getElementById("recepcion-session-badge-text");
  const kpiColaTotal = document.getElementById("kpi-cola-total");
  const kpiColaSubtext = document.getElementById("kpi-cola-subtext");
  const kpiColaAsistencias = document.getElementById("kpi-cola-asistencias");
  const kpiRecaudoSesion = document.getElementById("kpi-recaudo-sesion");
  const kpiRecaudoCaja = document.getElementById("kpi-recaudo-caja");

  const formCheckinManual = document.getElementById("form-checkin-manual");
  const inputCheckinDoc = document.getElementById("checkin-manual-documento");
  const selectCheckinMetodo = document.getElementById("checkin-manual-metodo");
  const alertCheckinError = document.getElementById("checkin-manual-error");

  const formPagoAdelantado = document.getElementById("form-pago-adelantado");
  const selectAdelantoSesiones = document.getElementById("adelanto-sesiones");
  const inputAdelantoRecibido = document.getElementById("adelanto-monto-recibido");
  const inputAdelantoAbonado = document.getElementById("adelanto-monto-abonado");
  const inputAdelantoNota = document.getElementById("adelanto-nota");
  const selectAdelantoMetodo = document.getElementById("adelanto-metodo");

  let tarifaBase = 5000;
  let filtroEstadoActual = "all";
  let adelantoContexto = { personaId: null, llegadaId: null };
  let colaActual = [];

  function escaparHtml(texto) {
    return String(texto == null ? "" : texto)
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;");
  }

  function formatearHoraSimple(valor) {
    if (!valor) {
      return "—";
    }
    const texto = String(valor);
    if (texto.indexOf("T") !== -1) {
      return ValidacionesPSR.formatearFechaHora(texto);
    }
    const partes = texto.split(":");
    if (partes.length >= 2) {
      const h = Number(partes[0]);
      const m = partes[1];
      if (!Number.isNaN(h)) {
        const sufijo = h >= 12 ? "p. m." : "a. m.";
        const h12 = h % 12 === 0 ? 12 : h % 12;
        return (h12 < 10 ? "0" + h12 : h12) + ":" + m + " " + sufijo;
      }
    }
    return texto;
  }

  function actualizarOpcionesAdelanto() {
    if (!selectAdelantoSesiones) {
      return;
    }
    const cantidades = [1, 2, 3, 5, 10];
    const valorActual = Number(selectAdelantoSesiones.value || 1);
    selectAdelantoSesiones.innerHTML = cantidades
      .map(function (c) {
        const etiqueta = c === 1 ? "1 sesión" : c + " sesiones";
        return (
          '<option value="' + c + '">' +
            etiqueta + " (" + ValidacionesPSR.formatearCOP(c * tarifaBase) + ")" +
          "</option>"
        );
      })
      .join("");
    selectAdelantoSesiones.value = String(cantidades.indexOf(valorActual) !== -1 ? valorActual : 1);
    recalcularMontosAdelanto();
  }

  function recalcularMontosAdelanto() {
    if (!selectAdelantoSesiones) {
      return;
    }
    const cantidad = Number(selectAdelantoSesiones.value || 1);
    const total = cantidad * tarifaBase;
    if (inputAdelantoRecibido) {
      inputAdelantoRecibido.value = String(total);
    }
    if (inputAdelantoAbonado) {
      inputAdelantoAbonado.value = String(total);
    }
    if (inputAdelantoNota) {
      inputAdelantoNota.value =
        "Pago por adelantado (" + cantidad + (cantidad === 1 ? " sesión)" : " sesiones)");
    }
  }

  async function cargarTarifaVigente() {
    try {
      const datos = await ClienteHttp.get("/tarifa");
      if (datos && datos.monto != null) {
        const montoNum = Number(datos.monto);
        if (!Number.isNaN(montoNum) && montoNum > 0) {
          tarifaBase = montoNum;
        }
      }
    } catch (error) {
      // Si el endpoint aún no responde, conserva la tarifa base por defecto
    }
    if (labelTarifa) {
      labelTarifa.textContent = ValidacionesPSR.formatearCOP(tarifaBase);
    }
    actualizarOpcionesAdelanto();
  }

  function aplicarFiltrosCola() {
    if (!contenedorCola) {
      return;
    }
    const texto = (inputBusqueda ? inputBusqueda.value : "").trim().toLowerCase();
    const filas = contenedorCola.querySelectorAll(".queue-row");

    filas.forEach(function (fila) {
      const esAtendido = fila.classList.contains("is-attended");
      const tieneDeuda = fila.classList.contains("is-debt");
      const contenidoTexto = fila.textContent.toLowerCase();

      let cumpleEstado = true;
      if (filtroEstadoActual === "pending") {
        cumpleEstado = !esAtendido;
      } else if (filtroEstadoActual === "balance") {
        cumpleEstado = tieneDeuda;
      } else if (filtroEstadoActual === "settled") {
        cumpleEstado = !tieneDeuda;
      }

      const cumpleBusqueda = !texto || contenidoTexto.indexOf(texto) !== -1;
      fila.classList.toggle("d-none", !(cumpleEstado && cumpleBusqueda));
    });
  }

  function actualizarContadoresYResumen(filas) {
    const total = filas.length;
    let atendidos = 0;
    let conDeuda = 0;
    let recaudoSesion = 0;
    let recaudoCaja = 0;

    filas.forEach(function (item) {
      const esAtendido = Boolean(item.atendido);
      const saldo = Number(item.saldo || 0);
      const montoCobrado = Number(item.montoCobrado || 0);

      if (esAtendido) {
        atendidos += 1;
        recaudoSesion += montoCobrado > 0 ? montoCobrado : tarifaBase;
      }
      if (montoCobrado > 0) {
        recaudoCaja += montoCobrado;
      }
      if (saldo < 0 || item.estadoCuenta === "DEBE") {
        conDeuda += 1;
      }
    });

    const sinAtender = total - atendidos;
    const alDia = total - conDeuda;

    if (kpiColaTotal) {
      kpiColaTotal.textContent = String(total);
    }
    if (kpiColaSubtext) {
      kpiColaSubtext.textContent = sinAtender + " sin atender · " + atendidos + " atendidos";
    }
    if (kpiColaAsistencias) {
      kpiColaAsistencias.textContent = String(atendidos);
    }
    if (kpiRecaudoSesion) {
      kpiRecaudoSesion.textContent = ValidacionesPSR.formatearCOP(recaudoSesion);
    }
    if (kpiRecaudoCaja) {
      kpiRecaudoCaja.textContent = ValidacionesPSR.formatearCOP(recaudoCaja > 0 ? recaudoCaja : recaudoSesion);
    }

    if (contenedorFiltros) {
      const btnAll = contenedorFiltros.querySelector('[data-filter="all"]');
      const btnPending = contenedorFiltros.querySelector('[data-filter="pending"]');
      const btnBalance = contenedorFiltros.querySelector('[data-filter="balance"]');
      const btnSettled = contenedorFiltros.querySelector('[data-filter="settled"]');

      if (btnAll) btnAll.textContent = "TODOS (" + total + ")";
      if (btnPending) btnPending.textContent = "SIN ATENDER (" + sinAtender + ")";
      if (btnBalance) btnBalance.textContent = "SALDO PENDIENTE (" + conDeuda + ")";
      if (btnSettled) btnSettled.textContent = "AL DÍA (" + alDia + ")";
    }

    if (badgeSesionTexto && filas.length > 0 && filas[0].fechaSesion) {
      badgeSesionTexto.textContent = "Sesión · " + ValidacionesPSR.formatearFecha(filas[0].fechaSesion);
    }
  }

  function renderizarCola(filas) {
    colaActual = Array.isArray(filas) ? filas : [];
    actualizarContadoresYResumen(colaActual);

    if (!contenedorCola) {
      return;
    }

    if (colaActual.length === 0) {
      contenedorCola.innerHTML =
        '<div class="psr-card text-center text-secondary py-4">' +
          "No hay participantes registrados en la fila de hoy." +
        "</div>";
      return;
    }

    contenedorCola.innerHTML = colaActual
      .map(function (item, indice) {
        const llegadaId = item.llegadaId || 0;
        const personaId = item.personaId || 0;
        const posicion = item.posicionTurno != null ? item.posicionTurno : indice + 1;
        const nombre = escaparHtml(item.alumno || "Participante");
        const doc = escaparHtml(item.numeroDocumento || "—");
        const horaLlegada = formatearHoraSimple(item.horaLlegada);
        const horaMarcado = formatearHoraSimple(item.horaMarcado);
        const metodo = escaparHtml(item.metodoCheckin || "MANUAL");
        const esAtendido = Boolean(item.atendido);
        const saldo = Number(item.saldo || 0);
        const tieneDeuda = !esAtendido && (saldo < 0 || item.estadoCuenta === "DEBE");
        const cubiertoPorSaldo = !esAtendido && !tieneDeuda && (saldo >= tarifaBase || item.estadoCuenta === "CON_SALDO_A_FAVOR");

        let clasesFila = "queue-row";
        if (esAtendido) {
          clasesFila += " is-attended";
        } else if (tieneDeuda) {
          clasesFila += " is-debt";
        }

        let badgeHtml = "";
        let metaHtml = "";
        let columnaPagoHtml = "";
        let columnaAccionesHtml = "";

        if (esAtendido) {
          const montoPagado = Number(item.montoCobrado != null ? item.montoCobrado : tarifaBase);
          badgeHtml =
            '<span class="psr-badge badge-emerald">' +
              '<i class="ph-bold ph-check"></i>' +
              "<span>Asistencia confirmada · Cobrado " + ValidacionesPSR.formatearCOP(montoPagado) + "</span>" +
            "</span>";
          metaHtml =
            "Doc: " + doc + " · Llegada: " + horaLlegada +
            (item.horaMarcado ? " · Marcado: " + horaMarcado : "") +
            " · Método: " + metodo;
          columnaAccionesHtml =
            '<div class="queue-actions-col">' +
              '<button type="button" class="btn-psr-outline-danger" title="Anular confirmación">' +
                '<i class="ph-bold ph-arrow-counter-clockwise"></i>' +
                "<span>Anular</span>" +
              "</button>" +
            "</div>";
        } else if (cubiertoPorSaldo) {
          badgeHtml =
            '<span class="psr-badge badge-blue">' +
              "Cubierto por saldo (+" + ValidacionesPSR.formatearCOP(saldo) + ")" +
            "</span>";
          metaHtml =
            "Doc: " + doc + " · Llegada: " + horaLlegada + " · Método: " + metodo +
            ' · A pagar hoy: <strong class="text-white">$0</strong>';
          columnaAccionesHtml =
            '<div class="queue-actions-col">' +
              '<button type="button" class="btn-psr-secondary py-2 px-3" data-bs-toggle="modal" data-bs-target="#modalPagoAdelantado">' +
                '<i class="ph-bold ph-calendar-plus"></i>' +
                "<span>Adelanto</span>" +
              "</button>" +
              '<button type="button" class="btn-psr-primary py-2 px-3">' +
                '<i class="ph-bold ph-check"></i>' +
                "<span>Confirmar Asistencia</span>" +
              "</button>" +
            "</div>";
        } else {
          const deudaAnterior = saldo < 0 ? Math.abs(saldo) : 0;
          const montoSugerido = deudaAnterior + tarifaBase;

          if (tieneDeuda) {
            badgeHtml =
              '<span class="psr-badge badge-rose">' +
                "Debe " + ValidacionesPSR.formatearCOP(deudaAnterior) + " anterior" +
              "</span>";
            metaHtml =
              "Doc: " + doc + " · Llegada: " + horaLlegada + " · Método: " + metodo +
              ' · Total con hoy: <strong class="text-white">' + ValidacionesPSR.formatearCOP(montoSugerido) + "</strong>";
          } else {
            badgeHtml = '<span class="psr-badge badge-amber">Sin atender</span>';
            metaHtml =
              "Doc: " + doc + " · Llegada: " + horaLlegada + " · Método: " + metodo +
              ' · A pagar hoy: <strong class="text-white">' + ValidacionesPSR.formatearCOP(montoSugerido) + "</strong>";
          }

          columnaPagoHtml =
            '<div class="queue-payment-col">' +
              '<div class="currency-input-group" style="width: 125px;">' +
                '<span class="currency-prefix">$</span>' +
                '<input type="text" inputmode="numeric" value="' + montoSugerido + '" maxlength="8" aria-label="Monto recibido">' +
              "</div>" +
              '<select class="psr-select py-2" style="width: 145px;" aria-label="Método de pago">' +
                '<option value="EFECTIVO">Efectivo</option>' +
                '<option value="TRANSFERENCIA">Transferencia</option>' +
              "</select>" +
            "</div>";

          columnaAccionesHtml =
            '<div class="queue-actions-col">' +
              '<button type="button" class="btn-psr-secondary py-2 px-3" title="Registrar pago">' +
                '<i class="ph-bold ph-currency-dollar"></i>' +
                "<span>Cobrar</span>" +
              "</button>" +
              '<button type="button" class="btn-psr-secondary py-2 px-3" data-bs-toggle="modal" data-bs-target="#modalPagoAdelantado" title="Abonar sesiones por adelantado">' +
                '<i class="ph-bold ph-calendar-plus"></i>' +
                "<span>Adelanto</span>" +
              "</button>" +
              '<button type="button" class="btn-psr-primary py-2 px-3" title="Confirmar asistencia">' +
                '<i class="ph-bold ph-check"></i>' +
                "<span>Confirmar</span>" +
              "</button>" +
            "</div>";
        }

        return (
          '<article class="' + clasesFila + '" data-llegada-id="' + llegadaId + '" data-persona-id="' + personaId + '">' +
            '<span class="queue-position">#' + posicion + "</span>" +
            '<div class="queue-person-col">' +
              '<div class="d-flex align-items-center gap-2 flex-wrap">' +
                '<p class="queue-person-name">' + nombre + "</p>" +
                badgeHtml +
              "</div>" +
              '<p class="queue-person-meta">' + metaHtml + "</p>" +
            "</div>" +
            columnaPagoHtml +
            columnaAccionesHtml +
          "</article>"
        );
      })
      .join("");

    ValidacionesPSR.inicializarRestriccionesNumericas(contenedorCola);
    aplicarFiltrosCola();
  }

  async function cargarColaAdmin(mostrarMensaje) {
    try {
      const respuesta = await ClienteHttp.get("/cola/admin");
      const filas = Array.isArray(respuesta)
        ? respuesta
        : respuesta && Array.isArray(respuesta.content)
          ? respuesta.content
          : [];
      renderizarCola(filas);
      if (mostrarMensaje) {
        ValidacionesPSR.mostrarAlerta(alertBox, "Fila de recepción actualizada.", "success");
      }
    } catch (error) {
      renderizarCola([]);
      ValidacionesPSR.mostrarAlerta(alertBox, error.message, "error");
    }
  }

  // Cambio de filtro por pastillas (TODOS, SIN ATENDER, SALDO PENDIENTE, AL DÍA)
  if (contenedorFiltros) {
    const botonesFiltro = contenedorFiltros.querySelectorAll(".filter-pill");
    botonesFiltro.forEach(function (boton) {
      boton.addEventListener("click", function () {
        botonesFiltro.forEach(function (b) {
          b.classList.remove("is-active");
        });
        boton.classList.add("is-active");
        filtroEstadoActual = boton.getAttribute("data-filter") || "all";
        aplicarFiltrosCola();
      });
    });
  }

  if (inputBusqueda) {
    inputBusqueda.addEventListener("input", aplicarFiltrosCola);
  }

  // Delegación de eventos en las filas de la cola (Cobrar, Confirmar, Anular, Adelanto)
  if (contenedorCola) {
    contenedorCola.addEventListener("click", async function (evento) {
      const boton = evento.target.closest("button");
      if (!boton) {
        return;
      }
      const fila = boton.closest(".queue-row");
      if (!fila) {
        return;
      }

      const llegadaId = Number(fila.getAttribute("data-llegada-id") || 0);
      const personaId = Number(fila.getAttribute("data-persona-id") || 0);
      const nombrePersona = (fila.querySelector(".queue-person-name") || {}).textContent || "Participante";
      const textoBoton = boton.textContent.trim();
      if (!Number.isInteger(llegadaId) || llegadaId <= 0 || !Number.isInteger(personaId) || personaId <= 0) {
        ValidacionesPSR.mostrarAlerta(alertBox, "No se encontró el registro de la fila. Actualiza la vista.", "error");
        return;
      }

      // Abrir modal de pago por adelantado cargando el nombre y IDs de la fila
      if (boton.getAttribute("data-bs-target") === "#modalPagoAdelantado") {
        adelantoContexto = { personaId: personaId, llegadaId: llegadaId };
        const labelNombre = document.getElementById("adelanto-persona-nombre");
        if (labelNombre) {
          labelNombre.textContent = nombrePersona;
        }
        recalcularMontosAdelanto();
        return;
      }

      // Acción: Cobrar
      if (textoBoton.indexOf("Cobrar") !== -1) {
        const inputMonto = fila.querySelector('input[inputmode="numeric"]');
        const selectMetodo = fila.querySelector("select");
        const valMonto = ValidacionesPSR.validarMontoPositivo(inputMonto ? inputMonto.value : 0);

        if (!valMonto.valido) {
          ValidacionesPSR.mostrarAlerta(alertBox, valMonto.mensaje, "error");
          return;
        }

        const metodoPago = selectMetodo ? selectMetodo.value : "EFECTIVO";
        try {
          await ClienteHttp.post("/movimientos", {
            personaId: personaId,
            llegadaId: llegadaId,
            montoRecibido: valMonto.valor,
            montoAbonado: valMonto.valor,
            metodoPago: metodoPago,
            tipoMovimiento: "ABONO",
            vueltoEntregado: true,
            usuarioId: SesionPSR.obtenerUsuario().id,
            nota: "Pago de sesión en recepción"
          });
          ValidacionesPSR.mostrarAlerta(
            alertBox,
            "Pago registrado para " + nombrePersona + " por " + ValidacionesPSR.formatearCOP(valMonto.valor) + ".",
            "success"
          );
          await cargarColaAdmin(false);
        } catch (error) {
          ValidacionesPSR.mostrarAlerta(alertBox, error.message, "error");
        }
        return;
      }

      // Acción: Confirmar asistencia
      if (textoBoton.indexOf("Confirmar") !== -1) {
        try {
          await ClienteHttp.put("/llegadas/" + llegadaId + "/asistencia", {
            usuarioId: SesionPSR.obtenerUsuario().id
          });
          ValidacionesPSR.mostrarAlerta(
            alertBox,
            "Asistencia confirmada para " + nombrePersona + ".",
            "success"
          );
          await cargarColaAdmin(false);
        } catch (error) {
          ValidacionesPSR.mostrarAlerta(alertBox, error.message, "error");
        }
        return;
      }

      // Acción: Anular asistencia
      if (textoBoton.indexOf("Anular") !== -1) {
        try {
          await ClienteHttp.del("/llegadas/" + llegadaId + "/asistencia");
          ValidacionesPSR.mostrarAlerta(
            alertBox,
            "Se anuló la confirmación de asistencia de " + nombrePersona + ".",
            "success"
          );
          await cargarColaAdmin(false);
        } catch (error) {
          ValidacionesPSR.mostrarAlerta(alertBox, error.message, "error");
        }
      }
    });
  }

  // Cálculo automático de monto al cambiar la cantidad de sesiones en el modal de adelanto
  if (selectAdelantoSesiones) {
    selectAdelantoSesiones.addEventListener("change", recalcularMontosAdelanto);
  }

  // Envío del modal de Check-in Manual
  if (formCheckinManual) {
    formCheckinManual.addEventListener("submit", async function (evento) {
      evento.preventDefault();
      ValidacionesPSR.ocultarAlerta(alertCheckinError);

      const valDoc = ValidacionesPSR.validarDocumento(inputCheckinDoc.value);
      if (!valDoc.valido) {
        ValidacionesPSR.mostrarAlerta(alertCheckinError, valDoc.mensaje, "error");
        inputCheckinDoc.focus();
        return;
      }

      const metodo = selectCheckinMetodo ? selectCheckinMetodo.value : "MANUAL";

      try {
        await ClienteHttp.post("/checkin/manual/documento", {
          documentNumber: valDoc.valor,
          documento: valDoc.valor,
          metodoCheckin: metodo
        });
      } catch (error) {
        ValidacionesPSR.mostrarAlerta(alertCheckinError, error.message, "error");
        return;
      }

      const modalEl = document.getElementById("modalCheckinManual");
      const instanciaModal = bootstrap.Modal.getInstance(modalEl);
      if (instanciaModal) {
        instanciaModal.hide();
      }

      formCheckinManual.reset();
      ValidacionesPSR.mostrarAlerta(
        alertBox,
        "Llegada registrada en la fila para el documento " + valDoc.valor + " (" + metodo + ").",
        "success"
      );
      await cargarColaAdmin(false);
    });
  }

  // Envío del modal de Pago por Adelantado
  if (formPagoAdelantado) {
    formPagoAdelantado.addEventListener("submit", async function (evento) {
      evento.preventDefault();

      const valRecibido = ValidacionesPSR.validarMontoPositivo(inputAdelantoRecibido.value);
      const valAbonado = ValidacionesPSR.validarMontoPositivo(inputAdelantoAbonado.value);

      if (!valRecibido.valido || !valAbonado.valido) {
        ValidacionesPSR.mostrarAlerta(alertBox, "Ingresa montos válidos mayores a cero.", "error");
        return;
      }
      if (!adelantoContexto.personaId || !adelantoContexto.llegadaId) {
        ValidacionesPSR.mostrarAlerta(alertBox, "Selecciona una persona de la fila para registrar el abono.", "error");
        return;
      }

      const nombrePersona = (document.getElementById("adelanto-persona-nombre") || {}).textContent || "Participante";
      const metodo = selectAdelantoMetodo ? selectAdelantoMetodo.value : "EFECTIVO";

      try {
        await ClienteHttp.post("/movimientos", {
          personaId: adelantoContexto.personaId,
          llegadaId: adelantoContexto.llegadaId,
          montoRecibido: valRecibido.valor,
          montoAbonado: valAbonado.valor,
          metodoPago: metodo,
          tipoMovimiento: "ABONO",
          vueltoEntregado: true,
          nota: inputAdelantoNota.value.trim() || "Pago por adelantado",
          usuarioId: SesionPSR.obtenerUsuario().id
        });
      } catch (error) {
        ValidacionesPSR.mostrarAlerta(alertBox, error.message, "error");
        return;
      }

      const modalEl = document.getElementById("modalPagoAdelantado");
      const instanciaModal = bootstrap.Modal.getInstance(modalEl);
      if (instanciaModal) {
        instanciaModal.hide();
      }

      ValidacionesPSR.mostrarAlerta(
        alertBox,
        "Abono de " + ValidacionesPSR.formatearCOP(valAbonado.valor) + " registrado para " + nombrePersona + ".",
        "success"
      );
      await cargarColaAdmin(false);
    });
  }

  if (btnRefrescar) {
    btnRefrescar.addEventListener("click", function () {
      cargarColaAdmin(true);
    });
  }

  cargarTarifaVigente();
  cargarColaAdmin(false);
});
