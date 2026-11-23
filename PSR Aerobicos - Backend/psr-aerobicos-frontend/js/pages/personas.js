// Lógica del Directorio de Personas: búsqueda dinámica y alta/edición con contacto de emergencia

document.addEventListener("DOMContentLoaded", function () {
  const inputBusqueda = document.getElementById("personas-search-input");
  const contenedorLista = document.getElementById("personas-list-container");
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

  // Buscador dinámico por nombre, apellido o documento
  function filtrarPersonasEnVista(filtro) {
    if (!contenedorLista) {
      return;
    }
    const termino = String(filtro || "").trim().toLowerCase();
    const filas = contenedorLista.querySelectorAll(".psr-list-row");

    filas.forEach(function (fila) {
      const coincide = !termino || fila.textContent.toLowerCase().indexOf(termino) !== -1;
      fila.classList.toggle("d-none", !coincide);
    });
  }

  if (inputBusqueda) {
    inputBusqueda.addEventListener("input", function () {
      const filtro = inputBusqueda.value;
      filtrarPersonasEnVista(filtro);

      clearTimeout(temporizadorBusqueda);
      temporizadorBusqueda = setTimeout(async function () {
        if (filtro.trim().length < 2) {
          return;
        }
        try {
          await ClienteHttp.get("/personas?buscar=" + encodeURIComponent(filtro.trim()));
        } catch (e) {
          // Si el backend no está activo, se mantiene el filtrado local sobre la lista
        }
      }, 300);
    });
  }

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

  // Preparar el modal en modo Editar Persona al pulsar el botón de lápiz
  if (contenedorLista) {
    contenedorLista.addEventListener("click", function (evento) {
      const btnEditar = evento.target.closest('button[title="Editar persona"]');
      if (!btnEditar) {
        return;
      }
      const fila = btnEditar.closest(".psr-list-row");
      if (!fila) {
        return;
      }

      const idPersona = fila.getAttribute("data-persona-id") || "1";
      const nombreCompleto = (fila.querySelector("p.fw-bold") || {}).textContent || "";
      const partesNombre = nombreCompleto.trim().split(" ");

      if (inputIdHidden) {
        inputIdHidden.value = idPersona;
      }
      if (modalTitle) {
        modalTitle.textContent = "Editar Persona";
      }
      if (inputNombre) {
        inputNombre.value = partesNombre.slice(0, 1).join(" ");
      }
      if (inputApellidos) {
        inputApellidos.value = partesNombre.slice(1).join(" ");
      }
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

      const valContNom = ValidacionesPSR.validarTextoObligatorio(inputContactoNombre.value, 100);
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
        if (!error.sinConexion) {
          ValidacionesPSR.mostrarAlerta(modalError, error.message, "error");
          return;
        }
      }

      const modalEl = document.getElementById("modalPersona");
      const instanciaModal = bootstrap.Modal.getInstance(modalEl);
      if (instanciaModal) {
        instanciaModal.hide();
      }

      ValidacionesPSR.mostrarAlerta(
        alertBox,
        (esEdicion ? "Datos actualizados para " : "Persona registrada con su contacto de emergencia: ") +
          datosPersona.nombre + " " + datosPersona.apellidos + ".",
        "success"
      );
    });
  }
});
