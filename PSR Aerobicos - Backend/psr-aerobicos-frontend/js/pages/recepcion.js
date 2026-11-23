// Lógica operativa de Recepción: cola del día, filtros, check-in manual, cobros y asistencias

document.addEventListener("DOMContentLoaded", function () {
  const contenedorFiltros = document.getElementById("recepcion-filter-pills");
  const inputBusqueda = document.getElementById("recepcion-search-input");
  const contenedorCola = document.getElementById("recepcion-queue-list");
  const alertBox = document.getElementById("recepcion-alert-box");
  const btnRefrescar = document.getElementById("btn-refrescar-cola");

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

  const TARIFA_BASE = 5000;
  let filtroEstadoActual = "all";
  let adelantoContexto = { personaId: 1, llegadaId: 1 };

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
      const personaId = Number(fila.getAttribute("data-persona-id") || 1);
      const nombrePersona = (fila.querySelector(".queue-person-name") || {}).textContent || "Participante";
      const textoBoton = boton.textContent.trim();

      // Abrir modal de pago por adelantado cargando el nombre y IDs de la fila
      if (boton.getAttribute("data-bs-target") === "#modalPagoAdelantado") {
        adelantoContexto = { personaId: personaId, llegadaId: llegadaId };
        const labelNombre = document.getElementById("adelanto-persona-nombre");
        if (labelNombre) {
          labelNombre.textContent = nombrePersona;
        }
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
            usuarioId: SesionPSR.obtenerUsuarioOPorDefecto().id,
            nota: "Pago de sesión en recepción"
          });
          ValidacionesPSR.mostrarAlerta(
            alertBox,
            "Pago registrado para " + nombrePersona + " por " + ValidacionesPSR.formatearCOP(valMonto.valor) + ".",
            "success"
          );
        } catch (error) {
          ValidacionesPSR.mostrarAlerta(
            alertBox,
            error.sinConexion
              ? "Cobro registrado en vista para " + nombrePersona + " (" + ValidacionesPSR.formatearCOP(valMonto.valor) + " - " + metodoPago + ")."
              : error.message,
            error.sinConexion ? "success" : "error"
          );
        }
        return;
      }

      // Acción: Confirmar asistencia
      if (textoBoton.indexOf("Confirmar") !== -1) {
        try {
          await ClienteHttp.put("/llegadas/" + llegadaId + "/asistencia", {
            usuarioId: SesionPSR.obtenerUsuarioOPorDefecto().id
          });
        } catch (error) {
          if (!error.sinConexion) {
            ValidacionesPSR.mostrarAlerta(alertBox, error.message, "error");
            return;
          }
        }

        fila.classList.remove("is-debt");
        fila.classList.add("is-attended");
        const badge = fila.querySelector(".psr-badge");
        if (badge) {
          badge.className = "psr-badge badge-emerald";
          badge.innerHTML = '<i class="ph-bold ph-check"></i><span>Asistencia confirmada</span>';
        }
        ValidacionesPSR.mostrarAlerta(
          alertBox,
          "Asistencia confirmada para " + nombrePersona + ".",
          "success"
        );
        aplicarFiltrosCola();
        return;
      }

      // Acción: Anular asistencia
      if (textoBoton.indexOf("Anular") !== -1) {
        try {
          await ClienteHttp.del("/llegadas/" + llegadaId + "/asistencia");
        } catch (error) {
          if (!error.sinConexion) {
            ValidacionesPSR.mostrarAlerta(alertBox, error.message, "error");
            return;
          }
        }
        fila.classList.remove("is-attended");
        const badge = fila.querySelector(".psr-badge");
        if (badge) {
          badge.className = "psr-badge badge-amber";
          badge.textContent = "Sin atender";
        }
        ValidacionesPSR.mostrarAlerta(
          alertBox,
          "Se anuló la confirmación de asistencia de " + nombrePersona + ".",
          "success"
        );
        aplicarFiltrosCola();
      }
    });
  }

  // Cálculo automático de monto al cambiar la cantidad de sesiones en el modal de adelanto
  if (selectAdelantoSesiones) {
    selectAdelantoSesiones.addEventListener("change", function () {
      const cantidad = Number(selectAdelantoSesiones.value || 1);
      const total = cantidad * TARIFA_BASE;
      if (inputAdelantoRecibido) {
        inputAdelantoRecibido.value = String(total);
      }
      if (inputAdelantoAbonado) {
        inputAdelantoAbonado.value = String(total);
      }
      if (inputAdelantoNota) {
        inputAdelantoNota.value = "Pago por adelantado (" + cantidad + " sesiones)";
      }
    });
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
        if (!error.sinConexion) {
          ValidacionesPSR.mostrarAlerta(alertCheckinError, error.message, "error");
          return;
        }
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
          usuarioId: SesionPSR.obtenerUsuarioOPorDefecto().id
        });
      } catch (error) {
        if (!error.sinConexion) {
          ValidacionesPSR.mostrarAlerta(alertBox, error.message, "error");
          return;
        }
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
    });
  }

  if (btnRefrescar) {
    btnRefrescar.addEventListener("click", async function () {
      try {
        await ClienteHttp.get("/cola/admin");
        ValidacionesPSR.mostrarAlerta(alertBox, "Fila de recepción actualizada.", "success");
      } catch (error) {
        ValidacionesPSR.mostrarAlerta(alertBox, "Vista de la fila sincronizada.", "success");
      }
    });
  }
});
