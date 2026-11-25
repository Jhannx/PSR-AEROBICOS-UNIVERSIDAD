// Lógica de Roles y Accesos: creación de cuenta de usuario y asignación de roles

document.addEventListener("DOMContentLoaded", function () {
  const inputBusqueda = document.getElementById("roles-search-input");
  const contenedorUsuarios = document.getElementById("roles-users-list");
  const contenedorPaginacion = document.getElementById("roles-paginacion");
  const alertBox = document.getElementById("roles-alert-box");

  const formCrearUsuario = document.getElementById("form-crear-usuario");
  const inputBuscarPersona = document.getElementById("usuario-persona-buscar");
  const selectPersona = document.getElementById("usuario-persona-select");
  const inputCorreo = document.getElementById("usuario-correo");
  const modalError = document.getElementById("modal-usuario-error");

  let rolesDisponibles = [];
  let paginaActual = 0;
  let solicitudUsuarios = 0;
  let solicitudPersonas = 0;
  let temporizadorUsuarios = null;
  let temporizadorPersonas = null;

  function crearFila(usuario) {
    const fila = document.createElement("article");
    fila.className = "psr-list-row";
    fila.dataset.usuarioId = String(usuario.id);
    fila.innerHTML = `
      <div class="d-flex align-items-center gap-3 min-w-0">
        <div class="person-avatar"></div>
        <div class="min-w-0">
          <div class="d-flex align-items-center gap-2 flex-wrap">
            <p class="fw-bold text-white mb-0 usuario-nombre"></p>
            <span class="psr-badge usuario-estado"></span>
          </div>
          <p class="small text-secondary mb-0 mt-1 usuario-detalle"></p>
        </div>
      </div>
      <div class="d-flex flex-wrap align-items-center gap-2 usuario-roles"></div>`;

    const nombre = usuario.nombreCompleto || "Usuario #" + usuario.id;
    fila.querySelector(".person-avatar").textContent = nombre.charAt(0).toUpperCase();
    fila.querySelector(".usuario-nombre").textContent = nombre;
    const badgeEstado = fila.querySelector(".usuario-estado");
    badgeEstado.classList.add(usuario.activo ? "badge-emerald" : "badge-rose");
    badgeEstado.textContent = usuario.activo ? "Cuenta activa" : "Cuenta inactiva";
    fila.querySelector(".usuario-detalle").textContent =
      "Doc: " + (usuario.documento || usuario.numeroDocumento || "") + " · Correo: " + (usuario.correo || "Sin correo");

    const rolesUsuario = Array.isArray(usuario.roles) ? usuario.roles : [];
    const contenedorRoles = fila.querySelector(".usuario-roles");
    rolesDisponibles.forEach(function (rol) {
      const boton = document.createElement("button");
      boton.type = "button";
      boton.className = "role-toggle-pill";
      boton.dataset.role = rol;
      const asignado = rolesUsuario.indexOf(rol) !== -1;
      if (asignado) boton.classList.add("is-assigned");
      boton.innerHTML = '<i class="ph-bold ' + (asignado ? 'ph-check' : 'ph-plus') + '"></i><span></span>';
      boton.querySelector("span").textContent = PSR_CONFIG.ROLES[rol] ? PSR_CONFIG.ROLES[rol].label : rol;
      contenedorRoles.appendChild(boton);
    });
    return fila;
  }

  function renderizarPaginacion(totalPaginas) {
    if (!contenedorPaginacion) return;
    contenedorPaginacion.replaceChildren();
    if (totalPaginas <= 1) return;
    const anterior = document.createElement("button");
    anterior.type = "button";
    anterior.className = "btn-psr-secondary";
    anterior.textContent = "Anterior";
    anterior.disabled = paginaActual === 0;
    anterior.addEventListener("click", function () { cargarUsuarios(paginaActual - 1); });
    const indicador = document.createElement("span");
    indicador.className = "small text-secondary";
    indicador.textContent = "Página " + (paginaActual + 1) + " de " + totalPaginas;
    const siguiente = document.createElement("button");
    siguiente.type = "button";
    siguiente.className = "btn-psr-secondary";
    siguiente.textContent = "Siguiente";
    siguiente.disabled = paginaActual >= totalPaginas - 1;
    siguiente.addEventListener("click", function () { cargarUsuarios(paginaActual + 1); });
    contenedorPaginacion.append(anterior, indicador, siguiente);
  }

  async function cargarUsuarios(pagina) {
    if (!contenedorUsuarios) return false;
    const numeroSolicitud = ++solicitudUsuarios;
    contenedorUsuarios.textContent = "Cargando cuentas...";
    try {
      if (rolesDisponibles.length === 0) {
        const catalogoRoles = await ClienteHttp.get("/roles");
        if (!Array.isArray(catalogoRoles)) throw new Error("El servidor no devolvió el catálogo de roles.");
        rolesDisponibles = catalogoRoles.map(function (rol) { return rol.nombre; }).filter(Boolean);
      }
      const buscar = inputBusqueda ? inputBusqueda.value.trim() : "";
      const respuesta = await ClienteHttp.get("/usuarios?buscar=" + encodeURIComponent(buscar) + "&page=" + pagina + "&size=20");
      if (numeroSolicitud !== solicitudUsuarios) return false;
      if (!respuesta || !Array.isArray(respuesta.content) || !respuesta.page) {
        throw new Error("El servidor no devolvió una página de cuentas válida.");
      }
      paginaActual = Number(respuesta.page.number);
      contenedorUsuarios.replaceChildren();
      respuesta.content.forEach(function (usuario) { contenedorUsuarios.appendChild(crearFila(usuario)); });
      if (respuesta.content.length === 0) contenedorUsuarios.textContent = "No se encontraron cuentas.";
      renderizarPaginacion(Number(respuesta.page.totalPages || 0));
      ValidacionesPSR.ocultarAlerta(alertBox);
      return true;
    } catch (error) {
      if (numeroSolicitud !== solicitudUsuarios) return false;
      contenedorUsuarios.replaceChildren();
      if (contenedorPaginacion) contenedorPaginacion.replaceChildren();
      ValidacionesPSR.mostrarAlerta(alertBox, error.message, "error");
      return false;
    }
  }

  async function cargarPersonasParaCuenta() {
    if (!selectPersona) return;
    const numeroSolicitud = ++solicitudPersonas;
    const buscar = inputBuscarPersona ? inputBuscarPersona.value.trim() : "";
    try {
      const respuesta = await ClienteHttp.get("/personas?buscar=" + encodeURIComponent(buscar) + "&page=0&size=20");
      if (numeroSolicitud !== solicitudPersonas) return;
      if (!respuesta || !Array.isArray(respuesta.content)) throw new Error("No se pudo consultar el directorio de personas.");
      selectPersona.replaceChildren();
      const inicial = document.createElement("option");
      inicial.value = "";
      inicial.textContent = "Selecciona una persona";
      selectPersona.appendChild(inicial);
      respuesta.content.forEach(function (persona) {
        const opcion = document.createElement("option");
        opcion.value = String(persona.id);
        opcion.textContent = [persona.nombre, persona.apellidos].filter(Boolean).join(" ") + " (" + (persona.numeroDocumento || "") + ")";
        selectPersona.appendChild(opcion);
      });
      ValidacionesPSR.ocultarAlerta(modalError);
    } catch (error) {
      if (numeroSolicitud !== solicitudPersonas) return;
      selectPersona.replaceChildren();
      ValidacionesPSR.mostrarAlerta(modalError, error.message, "error");
    }
  }

  if (inputBusqueda) {
    inputBusqueda.addEventListener("input", function () {
      clearTimeout(temporizadorUsuarios);
      temporizadorUsuarios = setTimeout(function () { cargarUsuarios(0); }, 300);
    });
  }
  if (inputBuscarPersona) {
    inputBuscarPersona.addEventListener("input", function () {
      clearTimeout(temporizadorPersonas);
      temporizadorPersonas = setTimeout(cargarPersonasParaCuenta, 300);
    });
  }
  const modalCrearUsuario = document.getElementById("modalCrearUsuario");
  if (modalCrearUsuario) modalCrearUsuario.addEventListener("shown.bs.modal", cargarPersonasParaCuenta);
  cargarUsuarios(0);

  // Asignación o retiro de roles al hacer clic en las pastillas (sp_asignar_rol)
  if (contenedorUsuarios) {
    contenedorUsuarios.addEventListener("click", async function (evento) {
      const pill = evento.target.closest(".role-toggle-pill");
      if (!pill) {
        return;
      }
      const fila = pill.closest(".psr-list-row");
      if (!fila) {
        return;
      }

      const usuarioId = Number(fila.getAttribute("data-usuario-id") || 0);
      const nombreUsuario = (fila.querySelector("p.fw-bold") || {}).textContent || "Usuario";
      const rol = pill.getAttribute("data-role");
      const yaAsignado = pill.classList.contains("is-assigned");
      if (!Number.isInteger(usuarioId) || usuarioId <= 0) {
        ValidacionesPSR.mostrarAlerta(alertBox, "Selecciona una cuenta válida.", "error");
        return;
      }

      const asignadosActuales = fila.querySelectorAll(".role-toggle-pill.is-assigned");
      if (yaAsignado && asignadosActuales.length <= 1) {
        ValidacionesPSR.mostrarAlerta(
          alertBox,
          "Cada cuenta debe conservar al menos un rol activo en el sistema.",
          "error"
        );
        return;
      }

      try {
        if (!yaAsignado) {
          await ClienteHttp.post("/usuarios/" + usuarioId + "/roles", {
            rolNombre: rol,
            roleName: rol
          });
        } else {
          await ClienteHttp.del("/usuarios/" + usuarioId + "/roles/" + rol);
        }
      } catch (error) {
        ValidacionesPSR.mostrarAlerta(alertBox, error.message, "error");
        return;
      }

      if (await cargarUsuarios(paginaActual)) {
        ValidacionesPSR.mostrarAlerta(
          alertBox,
          (!yaAsignado ? "Rol " + rol + " asignado a " : "Rol " + rol + " retirado de ") + nombreUsuario + ".",
          "success"
        );
      }
    });
  }

  // Creación de cuenta de usuario para una persona existente (sp_registrar_usuario_para_persona)
  if (formCrearUsuario) {
    formCrearUsuario.addEventListener("submit", async function (evento) {
      evento.preventDefault();
      ValidacionesPSR.ocultarAlerta(modalError);

      const personaId = Number(selectPersona ? selectPersona.value : 0);
      if (!Number.isInteger(personaId) || personaId <= 0) {
        ValidacionesPSR.mostrarAlerta(modalError, "Selecciona una persona para crear la cuenta.", "error");
        if (selectPersona) selectPersona.focus();
        return;
      }
      const valCorreo = ValidacionesPSR.validarCorreoOpcional(inputCorreo.value, true);
      if (!valCorreo.valido) {
        ValidacionesPSR.mostrarAlerta(modalError, valCorreo.mensaje, "error");
        inputCorreo.focus();
        return;
      }

      try {
        await ClienteHttp.post("/usuarios", {
          personaId: personaId,
          correo: valCorreo.valor
        });
      } catch (error) {
        ValidacionesPSR.mostrarAlerta(modalError, error.message, "error");
        return;
      }

      const modalEl = document.getElementById("modalCrearUsuario");
      const instanciaModal = bootstrap.Modal.getInstance(modalEl);
      if (instanciaModal) {
        instanciaModal.hide();
      }

      formCrearUsuario.reset();
      if (await cargarUsuarios(0)) {
        ValidacionesPSR.mostrarAlerta(
          alertBox,
          "Cuenta de acceso creada correctamente con el rol inicial MIEMBRO.",
          "success"
        );
      }
    });
  }
});
