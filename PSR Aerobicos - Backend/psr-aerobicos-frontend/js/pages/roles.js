// Lógica de Roles y Accesos: creación de cuenta de usuario y asignación de roles

document.addEventListener("DOMContentLoaded", function () {
  const inputBusqueda = document.getElementById("roles-search-input");
  const contenedorUsuarios = document.getElementById("roles-users-list");
  const alertBox = document.getElementById("roles-alert-box");

  const formCrearUsuario = document.getElementById("form-crear-usuario");
  const selectPersona = document.getElementById("usuario-persona-select");
  const inputCorreo = document.getElementById("usuario-correo");
  const modalError = document.getElementById("modal-usuario-error");

  // Buscador de cuentas por nombre o documento
  if (inputBusqueda && contenedorUsuarios) {
    inputBusqueda.addEventListener("input", function () {
      const filtro = inputBusqueda.value.trim().toLowerCase();
      const filas = contenedorUsuarios.querySelectorAll(".psr-list-row");
      filas.forEach(function (fila) {
        const coincide = !filtro || fila.textContent.toLowerCase().indexOf(filtro) !== -1;
        fila.classList.toggle("d-none", !coincide);
      });
    });
  }

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
        if (!error.sinConexion) {
          ValidacionesPSR.mostrarAlerta(alertBox, error.message, "error");
          return;
        }
      }

      pill.classList.toggle("is-assigned", !yaAsignado);
      const icono = pill.querySelector("i");
      if (icono) {
        icono.className = !yaAsignado ? "ph-bold ph-check" : "ph-bold ph-plus";
      }

      ValidacionesPSR.mostrarAlerta(
        alertBox,
        (!yaAsignado ? "Rol " + rol + " asignado a " : "Rol " + rol + " retirado de ") + nombreUsuario + ".",
        "success"
      );
    });
  }

  // Creación de cuenta de usuario para una persona existente (sp_registrar_usuario_para_persona)
  if (formCrearUsuario) {
    formCrearUsuario.addEventListener("submit", async function (evento) {
      evento.preventDefault();
      ValidacionesPSR.ocultarAlerta(modalError);

      const personaId = Number(selectPersona ? selectPersona.value : 0);
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
        if (!error.sinConexion) {
          ValidacionesPSR.mostrarAlerta(modalError, error.message, "error");
          return;
        }
      }

      const modalEl = document.getElementById("modalCrearUsuario");
      const instanciaModal = bootstrap.Modal.getInstance(modalEl);
      if (instanciaModal) {
        instanciaModal.hide();
      }

      formCrearUsuario.reset();
      ValidacionesPSR.mostrarAlerta(
        alertBox,
        "Cuenta de acceso creada correctamente con el rol inicial MIEMBRO.",
        "success"
      );
    });
  }
});
