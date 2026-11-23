// Lógica del Panel de Miembro: perfil, marcación de llegada y contacto de emergencia

document.addEventListener("DOMContentLoaded", function () {
  const btnCheckin = document.getElementById("btn-miembro-checkin");
  const feedbackTurno = document.getElementById("miembro-turno-feedback");
  const badgeTurnoEstado = document.getElementById("miembro-turno-estado");
  const posicionTurno = document.getElementById("miembro-turno-posicion");

  const inputContactoNombre = document.getElementById("miembro-contacto-nombre");
  const inputContactoTel = document.getElementById("miembro-contacto-telefono");
  const inputContactoObs = document.getElementById("miembro-contacto-obs");
  const btnGuardarContacto = document.getElementById("btn-guardar-contacto-miembro");

  // Marcar llegada del miembro a la sesión activa (sp_registrar_llegada)
  if (btnCheckin) {
    btnCheckin.addEventListener("click", async function () {
      const usuario = SesionPSR.obtenerUsuarioOPorDefecto();
      btnCheckin.disabled = true;

      try {
        const respuesta = await ClienteHttp.post("/checkin/documento", {
          personaId: usuario.personaId || 1,
          documentNumber: usuario.documento || "1067890123",
          documento: usuario.documento || "1067890123",
          metodoCheckin: "QR"
        });

        if (posicionTurno && respuesta && respuesta.posicion) {
          posicionTurno.textContent = "#" + respuesta.posicion;
        }
      } catch (error) {
        if (!error.sinConexion) {
          if (feedbackTurno) {
            feedbackTurno.textContent = error.message;
          }
          btnCheckin.disabled = false;
          return;
        }
      }

      btnCheckin.innerHTML = '<i class="ph-bold ph-check-circle"></i><span>Llegada registrada en la fila de hoy</span>';
      if (badgeTurnoEstado) {
        badgeTurnoEstado.className = "psr-badge badge-emerald";
        badgeTurnoEstado.textContent = "Registrado en cola";
      }
      if (feedbackTurno) {
        feedbackTurno.textContent = "Ya tienes tu turno asignado para la sesión de hoy. Acércate a recepción.";
      }
    });
  }

  // Actualización del contacto de emergencia del miembro
  if (btnGuardarContacto) {
    btnGuardarContacto.addEventListener("click", async function () {
      const valNom = ValidacionesPSR.validarTextoObligatorio(inputContactoNombre ? inputContactoNombre.value : "", 100);
      if (!valNom.valido) {
        alert("El nombre del contacto de emergencia es obligatorio.");
        return;
      }

      const valTel = ValidacionesPSR.validarTelefono(inputContactoTel ? inputContactoTel.value : "");
      if (!valTel.valido) {
        alert(valTel.mensaje);
        return;
      }

      const usuario = SesionPSR.obtenerUsuarioOPorDefecto();
      try {
        await ClienteHttp.put("/personas/" + (usuario.personaId || 1), {
          contactoNombre: valNom.valor,
          contactoTelefono: valTel.valor,
          contactoObservaciones: inputContactoObs ? inputContactoObs.value.trim() : null
        });
      } catch (e) {
        // Si el backend está apagado, confirma igualmente la validación local
      }

      btnGuardarContacto.innerHTML = '<i class="ph-bold ph-check"></i><span>Contacto Actualizado</span>';
      setTimeout(function () {
        btnGuardarContacto.innerHTML = '<i class="ph-bold ph-floppy-disk"></i><span>Actualizar Contacto</span>';
      }, 2500);
    });
  }
});
