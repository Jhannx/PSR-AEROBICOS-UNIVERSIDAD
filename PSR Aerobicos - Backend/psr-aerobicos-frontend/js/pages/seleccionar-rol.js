// Lógica del selector de rol activo (relación N:M usuario - rol)

document.addEventListener("DOMContentLoaded", function () {
  const usuario = SesionPSR.obtenerUsuario();
  const rolesDisponibles = SesionPSR.obtenerRolesDisponibles();
  if (!usuario || rolesDisponibles.length === 0) {
    window.location.href = "../index.html";
    return;
  }
  const rolActual = localStorage.getItem(PSR_CONFIG.STORAGE_KEYS.ROL_ACTIVO);

  const tituloSaludo = document.getElementById("role-greeting-title");
  const subtituloSaludo = document.getElementById("role-greeting-subtitle");
  const btnCancelar = document.getElementById("btn-cancelar-cambio-rol");
  const contenedorTarjetas = document.getElementById("role-cards-list");
  const btnConfirmar = document.getElementById("btn-confirmar-rol");

  let rolSeleccionado = rolesDisponibles.indexOf(rolActual) !== -1 ? rolActual : rolesDisponibles[0];

  if (tituloSaludo) {
    tituloSaludo.textContent = rolActual
      ? "Cambiar de rol"
      : "Hola, " + (usuario.nombreCompleto || "de nuevo");
  }

  if (subtituloSaludo) {
    subtituloSaludo.textContent = rolActual
      ? "Elige con qué rol deseas continuar navegando"
      : "Tienes más de un rol asignado — ¿con cuál entras hoy?";
  }

  if (btnCancelar) {
    if (rolActual) {
      btnCancelar.href = SesionPSR.obtenerRutaInicioRol(rolActual);
      btnCancelar.classList.remove("d-none");
    } else {
      btnCancelar.classList.add("d-none");
    }
  }

  function renderizarTarjetasRoles() {
    if (!contenedorTarjetas) {
      return;
    }

    contenedorTarjetas.innerHTML = rolesDisponibles
      .map(function (claveRol) {
        const meta = PSR_CONFIG.ROLES[claveRol];
        if (!meta) {
          return "";
        }
        const esSeleccionado = rolSeleccionado === claveRol;
        const esActivoActual = rolActual === claveRol;

        return (
          '<button type="button" class="role-card ' + (esSeleccionado ? "is-selected" : "") + '" data-role="' + claveRol + '">' +
            '<div class="role-card-icon">' +
              '<i class="ph-bold ' + meta.icon + '"></i>' +
            '</div>' +
            '<div class="flex-grow-1">' +
              '<div class="d-flex align-items-center gap-2">' +
                '<p class="role-card-title">' + meta.label + '</p>' +
                (esActivoActual ? '<span class="psr-badge badge-neutral">Activo</span>' : '') +
              '</div>' +
              '<p class="role-card-desc">' + meta.description + '</p>' +
            '</div>' +
          '</button>'
        );
      })
      .join("");

    const botones = contenedorTarjetas.querySelectorAll(".role-card");
    botones.forEach(function (boton) {
      boton.addEventListener("click", function () {
        rolSeleccionado = boton.getAttribute("data-role");
        renderizarTarjetasRoles();
      });
    });
  }

  renderizarTarjetasRoles();

  if (btnConfirmar) {
    btnConfirmar.addEventListener("click", function () {
      if (!rolSeleccionado) {
        return;
      }
      SesionPSR.establecerRolActivo(rolSeleccionado);
      const destino = SesionPSR.obtenerRutaInicioRol(rolSeleccionado);
      window.location.href = destino;
    });
  }
});
