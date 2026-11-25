// Lógica del Directorio de Personas: búsqueda dinámica y alta/edición con contacto de emergencia

document.addEventListener("DOMContentLoaded", function () {
  const inputBusqueda = document.getElementById("personas-search-input");
  const contenedorLista = document.getElementById("personas-list-container");
  const contenedorPaginacion = document.getElementById("personas-paginacion");
  const totalPersonas = document.getElementById("personas-total");
  const alertBox = document.getElementById("personas-alert-box");
  const btnAbrirCrear = document.getElementById("btn-abrir-modal-persona");

  const modalTitle = document.getElementById("modalPersonaLabel");
  const formPersona = document.getElementById("form-persona");
  const inputIdHidden = document.getElementById("persona-id-hidden");
  const inputNombre = document.getElementById("persona-nombre");
  const inputApellidos = document.getElementById("persona-apellidos");
  const selectTipoDoc = document.getElementById("persona-tipo-doc");
  const inputDocumento = document.getElementById("persona-documento");
  const inputTelefono = document.getElementById("persona-telefono");
  const inputFechaNac = document.getElementById("persona-fecha-nac");
  const inputContactoNombre = document.getElementById("contacto-nombre");
  const inputContactoTelefono = document.getElementById("contacto-telefono");
  const inputContactoObs = document.getElementById("contacto-observaciones");
  const modalError = document.getElementById("modal-persona-error");

  let temporizadorBusqueda = null;
  let paginaActual = 0;
  let solicitudActual = 0;
  let personasEnPagina = new Map();

  function crearFila(persona) {
    const fila = document.createElement("article");
    fila.className = "psr-list-row";
    fila.dataset.personaId = String(persona.id);
    fila.innerHTML = `
      <div class="d-flex align-items-center gap-3 min-w-0 flex-grow-1">
        <div class="person-avatar"></div>
        <div class="min-w-0">
          <div class="d-flex align-items-center gap-2 flex-wrap">
            <p class="fw-bold text-white mb-0 persona-nombre"></p>
            <span class="psr-badge persona-saldo"></span>
            <span class="psr-badge persona-estado"></span>
          </div>
          <p class="small text-secondary mb-0 mt-1 persona-detalle"></p>
        </div>
      </div>
      <div class="d-flex align-items-center gap-2 flex-shrink-0">
        <span class="psr-badge badge-neutral persona-fuente"></span>
        <button type="button" class="btn-icon-action" title="Editar persona" data-bs-toggle="modal" data-bs-target="#modalPersona">
          <i class="ph-bold ph-pencil-simple"></i>
        </button>
        <button type="button" class="btn-icon-action is-warning persona-cambiar-estado"></button>
      </div>`;

    const nombreCompleto = [persona.nombre, persona.apellidos].filter(Boolean).join(" ");
    fila.querySelector(".person-avatar").textContent = nombreCompleto.charAt(0).toUpperCase() || "P";
    fila.querySelector(".persona-nombre").textContent = nombreCompleto;
    fila.querySelector(".persona-detalle").textContent = [
      "Doc: " + (persona.tipoDocumento || "") + " " + (persona.numeroDocumento || ""),
      persona.telefono ? "Tel: " + persona.telefono : null,
      persona.contactoNombre ? "Emergencia: " + persona.contactoNombre + (persona.contactoTelefono ? " (" + persona.contactoTelefono + ")" : "") : null
    ].filter(Boolean).join(" · ");

    const saldo = Number(persona.saldo || 0);
    const badgeSaldo = fila.querySelector(".persona-saldo");
    badgeSaldo.classList.add(saldo < 0 ? "badge-rose" : saldo > 0 ? "badge-blue" : "badge-emerald");
    badgeSaldo.textContent = saldo < 0 ? "Debe " + ValidacionesPSR.formatearCOP(Math.abs(saldo))
      : saldo > 0 ? "Saldo a favor " + ValidacionesPSR.formatearCOP(saldo) : "Al día";

    const activo = persona.activo === true;
    const badgeEstado = fila.querySelector(".persona-estado");
    badgeEstado.classList.add(activo ? "badge-blue" : "badge-rose");
    badgeEstado.textContent = activo ? "Activo" : "Inactivo";
    fila.querySelector(".persona-fuente").textContent = persona.fuenteRegistro === "AUTOREGISTRO" ? "Autoregistro" : "Admin";

    const btnEstado = fila.querySelector(".persona-cambiar-estado");
    btnEstado.title = activo ? "Desactivar persona" : "Reactivar persona";
    btnEstado.innerHTML = activo ? '<i class="ph-bold ph-user-minus"></i>' : '<i class="ph-bold ph-user-plus"></i>';
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
    anterior.addEventListener("click", function () { cargarPersonas(paginaActual - 1); });

    const indicador = document.createElement("span");
    indicador.className = "small text-secondary";
    indicador.textContent = "Página " + (paginaActual + 1) + " de " + totalPaginas;

    const siguiente = document.createElement("button");
    siguiente.type = "button";
    siguiente.className = "btn-psr-secondary";
    siguiente.textContent = "Siguiente";
    siguiente.disabled = paginaActual >= totalPaginas - 1;
    siguiente.addEventListener("click", function () { cargarPersonas(paginaActual + 1); });

    contenedorPaginacion.append(anterior, indicador, siguiente);
  }

  async function cargarPersonas(pagina) {
    if (!contenedorLista) return false;
    const numeroSolicitud = ++solicitudActual;
    const filtro = inputBusqueda ? inputBusqueda.value.trim() : "";
    contenedorLista.textContent = "Cargando personas...";
    try {
      const respuesta = await ClienteHttp.get("/personas?buscar=" + encodeURIComponent(filtro) + "&page=" + pagina + "&size=20");
      if (numeroSolicitud !== solicitudActual) return false;
      if (!respuesta || !Array.isArray(respuesta.content) || !respuesta.page) {
        throw new Error("El servidor no devolvió una página de personas válida.");
      }
      paginaActual = Number(respuesta.page.number);
      personasEnPagina = new Map(respuesta.content.map(function (persona) { return [Number(persona.id), persona]; }));
      contenedorLista.replaceChildren();
      respuesta.content.forEach(function (persona) { contenedorLista.appendChild(crearFila(persona)); });
      if (respuesta.content.length === 0) {
        contenedorLista.textContent = "No se encontraron personas.";
      }
      if (totalPersonas) totalPersonas.textContent = String(respuesta.page.totalElements || 0) + " encontradas";
      renderizarPaginacion(Number(respuesta.page.totalPages || 0));
      ValidacionesPSR.ocultarAlerta(alertBox);
      return true;
    } catch (error) {
      if (numeroSolicitud !== solicitudActual) return false;
      personasEnPagina.clear();
      contenedorLista.replaceChildren();
      if (contenedorPaginacion) contenedorPaginacion.replaceChildren();
      if (totalPersonas) totalPersonas.textContent = "No disponible";
      ValidacionesPSR.mostrarAlerta(alertBox, error.message, "error");
      return false;
    }
  }

  if (inputBusqueda) {
    inputBusqueda.addEventListener("input", function () {
      clearTimeout(temporizadorBusqueda);
      temporizadorBusqueda = setTimeout(function () { cargarPersonas(0); }, 300);
    });
  }
  cargarPersonas(0);

  // Preparar el modal en modo Agregar Persona
  if (btnAbrirCrear && formPersona) {
    btnAbrirCrear.addEventListener("click", function () {
      formPersona.reset();
      if (inputIdHidden) {
        inputIdHidden.value = "";
      }
      if (modalTitle) {
        modalTitle.textContent = "Agregar Persona";
      }
      ValidacionesPSR.ocultarAlerta(modalError);
    });
  }

  // Editar o cambiar el estado de una persona de la página actual
  if (contenedorLista) {
    contenedorLista.addEventListener("click", async function (evento) {
      const btnEstado = evento.target.closest("button.persona-cambiar-estado");
      if (btnEstado) {
        const filaEstado = btnEstado.closest(".psr-list-row");
        const personaEstado = personasEnPagina.get(Number(filaEstado && filaEstado.dataset.personaId));
        if (!personaEstado) return;
        try {
          await ClienteHttp.patch("/personas/" + personaEstado.id + (personaEstado.activo ? "/desactivar" : "/reactivar"), {});
          const operacion = personaEstado.activo ? "desactivada" : "reactivada";
          if (await cargarPersonas(paginaActual)) {
            ValidacionesPSR.mostrarAlerta(alertBox, "Persona " + operacion + " correctamente.", "success");
          }
        } catch (error) {
          ValidacionesPSR.mostrarAlerta(alertBox, error.message, "error");
        }
        return;
      }

      const btnEditar = evento.target.closest('button[title="Editar persona"]');
      if (!btnEditar) {
        return;
      }
      const fila = btnEditar.closest(".psr-list-row");
      if (!fila) {
        return;
      }

      const persona = personasEnPagina.get(Number(fila.dataset.personaId));
      if (!persona) return;

      if (inputIdHidden) {
        inputIdHidden.value = String(persona.id);
      }
      if (modalTitle) {
        modalTitle.textContent = "Editar Persona";
      }
      inputNombre.value = persona.nombre || "";
      inputApellidos.value = persona.apellidos || "";
      selectTipoDoc.value = persona.tipoDocumento || "CC";
      inputDocumento.value = persona.numeroDocumento || "";
      inputTelefono.value = persona.telefono || "";
      inputFechaNac.value = persona.fechaNacimiento || "";
      inputContactoNombre.value = persona.contactoNombre || "";
      inputContactoTelefono.value = persona.contactoTelefono || "";
      inputContactoObs.value = persona.contactoObservaciones || "";
      ValidacionesPSR.ocultarAlerta(modalError);
    });
  }

  // Validación y envío del formulario de Persona + Contacto de Emergencia
  if (formPersona) {
    formPersona.addEventListener("submit", async function (evento) {
      evento.preventDefault();
      ValidacionesPSR.ocultarAlerta(modalError);

      const valNom = ValidacionesPSR.validarTextoObligatorio(inputNombre.value, 100);
      if (!valNom.valido) {
        ValidacionesPSR.mostrarAlerta(modalError, "Ingresa los nombres de la persona.", "error");
        inputNombre.focus();
        return;
      }

      const valApe = ValidacionesPSR.validarTextoObligatorio(inputApellidos.value, 100);
      if (!valApe.valido) {
        ValidacionesPSR.mostrarAlerta(modalError, "Ingresa los apellidos de la persona.", "error");
        inputApellidos.focus();
        return;
      }

      const valDoc = ValidacionesPSR.validarDocumento(inputDocumento.value);
      if (!valDoc.valido) {
        ValidacionesPSR.mostrarAlerta(modalError, valDoc.mensaje, "error");
        inputDocumento.focus();
        return;
      }

      const valTel = ValidacionesPSR.validarTelefono(inputTelefono.value);
      if (!valTel.valido) {
        ValidacionesPSR.mostrarAlerta(modalError, valTel.mensaje, "error");
        inputTelefono.focus();
        return;
      }

      if (!inputFechaNac.value) {
        ValidacionesPSR.mostrarAlerta(modalError, "La fecha de nacimiento es obligatoria.", "error");
        inputFechaNac.focus();
        return;
      }

      const valContNom = ValidacionesPSR.validarTextoObligatorio(inputContactoNombre.value, 150);
      if (!valContNom.valido) {
        ValidacionesPSR.mostrarAlerta(modalError, "Ingresa el nombre del contacto de emergencia.", "error");
        inputContactoNombre.focus();
        return;
      }

      const valContTel = ValidacionesPSR.validarTelefono(inputContactoTelefono.value);
      if (!valContTel.valido) {
        ValidacionesPSR.mostrarAlerta(modalError, "Contacto de emergencia: " + valContTel.mensaje, "error");
        inputContactoTelefono.focus();
        return;
      }

      if (!selectTipoDoc.value) {
        ValidacionesPSR.mostrarAlerta(modalError, "Selecciona el tipo de documento.", "error");
        selectTipoDoc.focus();
        return;
      }

      const datosPersona = {
        tipoDocumento: selectTipoDoc.value,
        numeroDocumento: valDoc.valor,
        nombre: valNom.valor,
        apellidos: valApe.valor,
        telefono: valTel.valor,
        fechaNacimiento: inputFechaNac.value,
        contactoNombre: valContNom.valor,
        contactoTelefono: valContTel.valor,
        contactoObservaciones: inputContactoObs.value.trim() || null
      };

      const esEdicion = Boolean(inputIdHidden && inputIdHidden.value);

      try {
        if (esEdicion) {
          await ClienteHttp.put("/personas/" + inputIdHidden.value, datosPersona);
        } else {
          await ClienteHttp.post("/personas", datosPersona);
        }
      } catch (error) {
        ValidacionesPSR.mostrarAlerta(modalError, error.message, "error");
        return;
      }

      const modalEl = document.getElementById("modalPersona");
      const instanciaModal = bootstrap.Modal.getInstance(modalEl);
      if (instanciaModal) {
        instanciaModal.hide();
      }

      if (await cargarPersonas(esEdicion ? paginaActual : 0)) {
        ValidacionesPSR.mostrarAlerta(
          alertBox,
          (esEdicion ? "Datos actualizados para " : "Persona registrada con su contacto de emergencia: ") +
            datosPersona.nombre + " " + datosPersona.apellidos + ".",
          "success"
        );
      }
    });
  }
});
