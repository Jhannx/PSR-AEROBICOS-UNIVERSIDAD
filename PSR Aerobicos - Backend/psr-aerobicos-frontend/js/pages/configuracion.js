// Lógica de Configuración General: consulta y actualización de la tarifa vigente por sesión

document.addEventListener("DOMContentLoaded", function () {
  const navTabs = document.getElementById("config-tabs");
  const panelTarifa = document.getElementById("panel-config-tarifa");
  const panelMetodos = document.getElementById("panel-config-metodos");
  const formTarifa = document.getElementById("form-tarifa");
  const inputMonto = document.getElementById("tarifa-monto-input");
  const displayVigente = document.getElementById("tarifa-vigente-display");
  const displayFecha = document.getElementById("tarifa-fecha-actualizacion");
  const btnGuardar = document.getElementById("btn-guardar-tarifa");
  const alertBox = document.getElementById("config-alert-box");

  const rolActivo = SesionPSR.obtenerRolActivo();
  const soloLectura = rolActivo !== "ADMINISTRADOR";

  if (soloLectura) {
    if (inputMonto) {
      inputMonto.disabled = true;
    }
    if (btnGuardar) {
      btnGuardar.classList.add("d-none");
    }
  }

  // Pestañas de Configuración
  if (navTabs) {
    const botonesTab = navTabs.querySelectorAll(".psr-tab");
    botonesTab.forEach(function (boton) {
      boton.addEventListener("click", function () {
        botonesTab.forEach(function (b) {
          b.classList.remove("is-active");
        });
        boton.classList.add("is-active");

        const tab = boton.getAttribute("data-tab");
        if (panelTarifa && panelMetodos) {
          if (tab === "tarifa") {
            panelTarifa.classList.remove("d-none");
            panelMetodos.classList.remove("d-none");
          } else {
            panelTarifa.classList.add("d-none");
            panelMetodos.classList.remove("d-none");
          }
        }
      });
    });
  }

  async function cargarTarifaVigente() {
    try {
      const tarifa = await ClienteHttp.get("/tarifa");
      if (tarifa && tarifa.monto !== undefined) {
        if (inputMonto) {
          inputMonto.value = String(Math.round(Number(tarifa.monto)));
        }
        if (displayVigente) {
          displayVigente.textContent = ValidacionesPSR.formatearCOP(tarifa.monto);
        }
        const fechaAct = tarifa.actualizadoEn || tarifa.fechaActualizacion;
        if (displayFecha && fechaAct) {
          displayFecha.textContent = ValidacionesPSR.formatearFecha(fechaAct);
        }
      }
    } catch (e) {
      // Mantiene el valor precargado en la vista cuando el servidor está apagado
    }
  }

  cargarTarifaVigente();

  if (formTarifa) {
    formTarifa.addEventListener("submit", async function (evento) {
      evento.preventDefault();
      if (soloLectura) {
        return;
      }
      ValidacionesPSR.ocultarAlerta(alertBox);

      const valMonto = ValidacionesPSR.validarMontoPositivo(inputMonto ? inputMonto.value : 0);
      if (!valMonto.valido) {
        ValidacionesPSR.mostrarAlerta(alertBox, valMonto.mensaje, "error");
        return;
      }

      try {
        await ClienteHttp.put("/tarifa", {
          monto: valMonto.valor,
          usuarioId: SesionPSR.obtenerUsuarioOPorDefecto().id
        });
      } catch (error) {
        if (!error.sinConexion) {
          ValidacionesPSR.mostrarAlerta(alertBox, error.message, "error");
          return;
        }
      }

      if (displayVigente) {
        displayVigente.textContent = ValidacionesPSR.formatearCOP(valMonto.valor);
      }
      if (displayFecha) {
        displayFecha.textContent = new Date().toLocaleDateString("es-CO");
      }

      ValidacionesPSR.mostrarAlerta(
        alertBox,
        "Tarifa actualizada a " + ValidacionesPSR.formatearCOP(valMonto.valor) + ". El trigger registró el cambio en auditoría.",
        "success"
      );
    });
  }
});
