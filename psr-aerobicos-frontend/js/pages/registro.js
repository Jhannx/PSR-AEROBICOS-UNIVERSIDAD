// Lógica de autoregistro público de participante con contacto de emergencia

document.addEventListener("DOMContentLoaded", function () {
  ValidacionesPSR.inicializarRestriccionesNumericas(document);

  const formRegistro = document.getElementById("registro-form");
  const alertBox = document.getElementById("registro-alert");
  const btnSubmit = document.getElementById("btn-registro-submit");

  const inputNombre = document.getElementById("reg-nombre");
  const inputApellidos = document.getElementById("reg-apellidos");
  const selectTipoDoc = document.getElementById("reg-tipo-doc");
  const inputDocumento = document.getElementById("reg-documento");
  const inputTelefono = document.getElementById("reg-telefono");
  const inputFechaNac = document.getElementById("reg-fecha-nacimiento");
  const inputCorreo = document.getElementById("reg-correo");
  const inputPassword = document.getElementById("reg-password");
  const inputContactoNombre = document.getElementById("reg-contacto-nombre");
  const inputContactoTel = document.getElementById("reg-contacto-telefono");
  const inputContactoObs = document.getElementById("reg-contacto-obs");

  if (!formRegistro) {
    return;
  }

  formRegistro.addEventListener("submit", async function (evento) {
    evento.preventDefault();
    ValidacionesPSR.ocultarAlerta(alertBox);

    const valNombre = ValidacionesPSR.validarTextoObligatorio(inputNombre.value, 100);
    if (!valNombre.valido) {
      ValidacionesPSR.mostrarAlerta(alertBox, "El nombre es obligatorio (máximo 100 caracteres).", "error");
      inputNombre.focus();
      return;
    }

    const valApellidos = ValidacionesPSR.validarTextoObligatorio(inputApellidos.value, 100);
    if (!valApellidos.valido) {
      ValidacionesPSR.mostrarAlerta(alertBox, "Los apellidos son obligatorios (máximo 100 caracteres).", "error");
      inputApellidos.focus();
      return;
    }

    const valDoc = ValidacionesPSR.validarDocumento(inputDocumento.value);
    if (!valDoc.valido) {
      ValidacionesPSR.mostrarAlerta(alertBox, valDoc.mensaje, "error");
      inputDocumento.focus();
      return;
    }

    const valTel = ValidacionesPSR.validarTelefono(inputTelefono.value);
    if (!valTel.valido) {
      ValidacionesPSR.mostrarAlerta(alertBox, valTel.mensaje, "error");
      inputTelefono.focus();
      return;
    }

    if (!inputFechaNac.value) {
      ValidacionesPSR.mostrarAlerta(alertBox, "Selecciona tu fecha de nacimiento.", "error");
      inputFechaNac.focus();
      return;
    }

    if (!inputPassword.value) {
      ValidacionesPSR.mostrarAlerta(alertBox, "Debes elegir una contraseña para tu cuenta.", "error");
      inputPassword.focus();
      return;
    }

    const valCorreo = ValidacionesPSR.validarCorreoOpcional(inputCorreo.value, false);
    if (!valCorreo.valido) {
      ValidacionesPSR.mostrarAlerta(alertBox, valCorreo.mensaje, "error");
      inputCorreo.focus();
      return;
    }

    const valContactoNom = ValidacionesPSR.validarTextoObligatorio(inputContactoNombre.value, 150);
    if (!valContactoNom.valido) {
      ValidacionesPSR.mostrarAlerta(alertBox, "El nombre del contacto de emergencia es obligatorio.", "error");
      inputContactoNombre.focus();
      return;
    }

    const valContactoTel = ValidacionesPSR.validarTelefono(inputContactoTel.value);
    if (!valContactoTel.valido) {
      ValidacionesPSR.mostrarAlerta(alertBox, "Teléfono de emergencia: " + valContactoTel.mensaje, "error");
      inputContactoTel.focus();
      return;
    }

    const payload = {
      tipoDocumento: selectTipoDoc.value,
      numeroDocumento: valDoc.valor,
      nombre: valNombre.valor,
      apellidos: valApellidos.valor,
      telefono: valTel.valor,
      fechaNacimiento: inputFechaNac.value,
      correo: valCorreo.valor || null,
      password: inputPassword.value,
      contactoNombre: valContactoNom.valor,
      contactoTelefono: valContactoTel.valor,
      contactoObservaciones: inputContactoObs.value.trim() || null
    };

    btnSubmit.disabled = true;
    try {
      await ClienteHttp.post("/personas/autoregistro", payload);
      ValidacionesPSR.mostrarAlerta(
        alertBox,
        "Registro completado con éxito. Ahora puedes iniciar sesión.",
        "success"
      );
      formRegistro.reset();
    } catch (error) {
      ValidacionesPSR.mostrarAlerta(alertBox, error.message, "error");
    } finally {
      btnSubmit.disabled = false;
    }
  });
});
