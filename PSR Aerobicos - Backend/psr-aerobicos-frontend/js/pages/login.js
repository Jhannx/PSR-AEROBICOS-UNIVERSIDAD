// Lógica de la pantalla de inicio de sesión

document.addEventListener("DOMContentLoaded", function () {
  ValidacionesPSR.inicializarRestriccionesNumericas(document);

  const formLogin = document.getElementById("login-form");
  const inputIdentificador = document.getElementById("login-identificador");
  const inputPassword = document.getElementById("login-password");
  const btnTogglePassword = document.getElementById("btn-toggle-password");
  const iconTogglePassword = document.getElementById("icon-toggle-password");
  const alertError = document.getElementById("login-error-alert");
  const btnSubmit = document.getElementById("btn-login-submit");
  const labelSubmit = document.getElementById("btn-login-label");

  // Mostrar u ocultar la contraseña
  if (btnTogglePassword && inputPassword) {
    btnTogglePassword.addEventListener("click", function () {
      const esPassword = inputPassword.type === "password";
      inputPassword.type = esPassword ? "text" : "password";
      if (iconTogglePassword) {
        iconTogglePassword.className = esPassword ? "ph-bold ph-eye-slash" : "ph-bold ph-eye";
      }
    });
  }

  function redirigirSegunRoles(usuario) {
    if (!SesionPSR.guardarSesion(usuario)) {
      throw new Error("El servidor no devolvió una cuenta con roles válidos.");
    }
    if (SesionPSR.requiereSeleccionDeRol()) {
      window.location.href = "pages/seleccionar-rol.html";
    } else {
      const rolUnico = SesionPSR.obtenerRolActivo();
      const paginaInicio = SesionPSR.obtenerRutaInicioRol(rolUnico);
      window.location.href = "pages/" + paginaInicio;
    }
  }

  if (formLogin) {
    formLogin.addEventListener("submit", async function (evento) {
      evento.preventDefault();
      ValidacionesPSR.ocultarAlerta(alertError);

      const valId = ValidacionesPSR.validarTextoObligatorio(inputIdentificador.value, 150);
      if (!valId.valido) {
        ValidacionesPSR.mostrarAlerta(alertError, "Ingresa tu número de documento o correo electrónico.", "error");
        inputIdentificador.focus();
        return;
      }

      const valPass = ValidacionesPSR.validarTextoObligatorio(inputPassword.value, 100);
      if (!valPass.valido) {
        ValidacionesPSR.mostrarAlerta(alertError, "Ingresa tu contraseña para continuar.", "error");
        inputPassword.focus();
        return;
      }

      btnSubmit.disabled = true;
      if (labelSubmit) {
        labelSubmit.textContent = "Ingresando...";
      }

      try {
        const respuestaUsuario = await ClienteHttp.post("/auth/login", {
          identificador: valId.valor,
          documento: valId.valor,
          password: valPass.valor
        });

        redirigirSegunRoles(respuestaUsuario);
      } catch (error) {
        ValidacionesPSR.mostrarAlerta(
          alertError,
          error.message || "Documento/correo o contraseña incorrectos.",
          "error"
        );
      } finally {
        btnSubmit.disabled = false;
        if (labelSubmit) {
          labelSubmit.textContent = "Ingresar";
        }
      }
    });
  }
});
