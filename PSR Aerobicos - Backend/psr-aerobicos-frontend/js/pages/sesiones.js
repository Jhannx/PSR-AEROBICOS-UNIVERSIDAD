// Lógica de Sesiones de Clase y Horarios Recurrentes

document.addEventListener("DOMContentLoaded", function () {
  const navTabs = document.getElementById("sesiones-tabs");
  const seccionSesiones = document.getElementById("tab-content-sesiones");
  const seccionHorarios = document.getElementById("tab-content-horarios");
  const contenedorFiltrosEstado = document.getElementById("sesiones-status-filters");
  const gridSesiones = document.getElementById("sesiones-cards-grid");

  const formCrearSesion = document.getElementById("form-crear-sesion");
  const formHorario = document.getElementById("form-horario");

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
  if (contenedorFiltrosEstado && gridSesiones) {
    const botonesEstado = contenedorFiltrosEstado.querySelectorAll(".filter-pill");
    botonesEstado.forEach(function (boton) {
      boton.addEventListener("click", function () {
        botonesEstado.forEach(function (b) {
          b.classList.remove("is-active");
        });
        boton.classList.add("is-active");

        const estadoFiltro = boton.getAttribute("data-status") || "all";
        const tarjetas = gridSesiones.querySelectorAll(".session-card");

        tarjetas.forEach(function (tarjeta) {
          if (estadoFiltro === "all") {
            tarjeta.classList.remove("d-none");
            return;
          }
          const textoBadge = ((tarjeta.querySelector(".psr-badge") || {}).textContent || "").toUpperCase();
          const coincide =
            (estadoFiltro === "EN_CURSO" && textoBadge.indexOf("CURSO") !== -1) ||
            (estadoFiltro === "PROGRAMADA" && textoBadge.indexOf("PROGRAMADA") !== -1) ||
            (estadoFiltro === "FINALIZADA" && textoBadge.indexOf("FINALIZADA") !== -1) ||
            (estadoFiltro === "CANCELADA" && textoBadge.indexOf("CANCELADA") !== -1);

          tarjeta.classList.toggle("d-none", !coincide);
        });
      });
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
        await ClienteHttp.del("/sesiones/" + sesionId);
      } catch (error) {
        if (!error.sinConexion) {
          return;
        }
      }

      tarjeta.classList.remove("is-in-progress");
      tarjeta.classList.add("is-cancelled");
      const badge = tarjeta.querySelector(".psr-badge");
      if (badge) {
        badge.className = "psr-badge badge-rose";
        badge.textContent = "Cancelada";
      }
      btnCancelar.remove();
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

      if (!fecha || !horaInicio || !horaFin || horaFin <= horaInicio) {
        alert("Verifica la fecha y que la hora de fin sea posterior a la hora de inicio.");
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
        if (!error.sinConexion) {
          alert(error.message);
          return;
        }
      }

      const modalEl = document.getElementById("modalCrearSesion");
      const instanciaModal = bootstrap.Modal.getInstance(modalEl);
      if (instanciaModal) {
        instanciaModal.hide();
      }
    });
  }

  // Envío del modal Nuevo Horario Recurrente
  if (formHorario) {
    formHorario.addEventListener("submit", async function (evento) {
      evento.preventDefault();
      const dia = document.getElementById("horario-dia").value;
      const horaInicio = document.getElementById("horario-inicio").value;
      const horaFin = document.getElementById("horario-fin").value;

      if (!horaInicio || !horaFin || horaFin <= horaInicio) {
        alert("La hora de finalización debe ser mayor a la hora de inicio.");
        return;
      }

      try {
        await ClienteHttp.post("/horarios", {
          diaSemana: Number(dia),
          horaInicio: horaInicio,
          horaFin: horaFin,
          activo: true
        });
      } catch (error) {
        if (!error.sinConexion) {
          alert(error.message);
          return;
        }
      }

      const modalEl = document.getElementById("modalNuevoHorario");
      const instanciaModal = bootstrap.Modal.getInstance(modalEl);
      if (instanciaModal) {
        instanciaModal.hide();
      }
    });
  }
});
