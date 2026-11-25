// Lógica del Panel de Miembro: perfil, estado de cuenta, turno en fila y contacto de emergencia

document.addEventListener("DOMContentLoaded", function () {
  const alertBox = document.getElementById("miembro-alert-box");
  const avatarInitials = document.getElementById("miembro-avatar-initials");
  const labelNombreCompleto = document.getElementById("miembro-nombre-completo");
  const labelDocumento = document.getElementById("miembro-documento-label");
  const labelFechaNacimiento = document.getElementById("miembro-fecha-nacimiento");
  const badgeEstadoCuenta = document.getElementById("miembro-estado-cuenta-badge");
  const labelSesionesFavor = document.getElementById("miembro-sesiones-favor");
  const labelTarifaHoy = document.getElementById("miembro-tarifa-hoy");

  const btnCheckin = document.getElementById("btn-miembro-checkin");
  const feedbackTurno = document.getElementById("miembro-turno-feedback");
  const badgeTurnoEstado = document.getElementById("miembro-turno-estado");
  const posicionTurno = document.getElementById("miembro-turno-posicion");
  const labelTurnoSesion = document.getElementById("miembro-turno-sesion");
  const labelTurnoHora = document.getElementById("miembro-turno-hora");

  const inputContactoNombre = document.getElementById("miembro-contacto-nombre");
  const inputContactoTel = document.getElementById("miembro-contacto-telefono");
  const inputContactoObs = document.getElementById("miembro-contacto-obs");
  const btnGuardarContacto = document.getElementById("btn-guardar-contacto-miembro");

  let personaCargada = null;
  let tarifaVigente = 5000;

  function obtenerIniciales(nombre) {
    const partes = String(nombre || "")
      .trim()
      .split(/\s+/)
      .filter(Boolean);
    if (partes.length === 0) {
      return "MB";
    }
    if (partes.length === 1) {
      return partes[0].slice(0, 2).toUpperCase();
    }
    return (partes[0][0] + partes[partes.length - 1][0]).toUpperCase();
  }

  function renderizarPerfilPersona(persona) {
    personaCargada = persona;
    const nombreCompleto =
      persona.nombreCompleto || [persona.nombre, persona.apellidos].filter(Boolean).join(" ");

    if (avatarInitials) {
      avatarInitials.textContent = obtenerIniciales(nombreCompleto);
    }
    if (labelNombreCompleto) {
      labelNombreCompleto.textContent = nombreCompleto || "Miembro PSR";
    }
    if (labelDocumento) {
      const tipoDoc = persona.tipoDocumento || "CC";
      const numDoc = persona.numeroDocumento || "—";
      const tel = persona.telefono || "—";
      labelDocumento.textContent = "Doc: " + tipoDoc + " " + numDoc + " · Tel: " + tel;
    }
    if (labelFechaNacimiento) {
      labelFechaNacimiento.textContent =
        "Nacimiento: " + ValidacionesPSR.formatearFecha(persona.fechaNacimiento);
    }
    if (inputContactoNombre) {
      inputContactoNombre.value = persona.contactoNombre || "";
    }
    if (inputContactoTel) {
      inputContactoTel.value = persona.contactoTelefono || "";
    }
    if (inputContactoObs) {
      inputContactoObs.value = persona.contactoObservaciones || "";
    }
  }

  function renderizarSaldoYTarifa(saldoDto, tarifaMonto) {
    if (tarifaMonto > 0) {
      tarifaVigente = tarifaMonto;
    }
    if (labelTarifaHoy) {
      labelTarifaHoy.textContent = ValidacionesPSR.formatearCOP(tarifaVigente);
    }

    const saldo = saldoDto && saldoDto.saldo != null ? Number(saldoDto.saldo) : 0;
    const estadoCuenta = (saldoDto && saldoDto.estadoCuenta) || "AL_DIA";
    const sesionesFavor =
      saldo > 0 && tarifaVigente > 0 ? Math.floor(saldo / tarifaVigente) : 0;

    if (labelSesionesFavor) {
      labelSesionesFavor.textContent = String(sesionesFavor);
    }

    if (!badgeEstadoCuenta) {
      return;
    }

    if (saldo > 0 || estadoCuenta === "CON_SALDO_A_FAVOR") {
      badgeEstadoCuenta.className = "psr-badge badge-blue py-2 px-3 fs-6";
      badgeEstadoCuenta.innerHTML =
        '<i class="ph-bold ph-wallet"></i>' +
        "<span>Saldo a favor: +" + ValidacionesPSR.formatearCOP(saldo) + "</span>";
    } else if (saldo < 0 || estadoCuenta === "DEBE") {
      badgeEstadoCuenta.className = "psr-badge badge-rose py-2 px-3 fs-6";
      badgeEstadoCuenta.innerHTML =
        '<i class="ph-bold ph-warning-circle"></i>' +
        "<span>Saldo pendiente: -" + ValidacionesPSR.formatearCOP(Math.abs(saldo)) + "</span>";
    } else {
      badgeEstadoCuenta.className = "psr-badge badge-emerald py-2 px-3 fs-6";
      badgeEstadoCuenta.innerHTML =
        '<i class="ph-bold ph-check-circle"></i>' +
        "<span>Cuenta al día · Saldo: $0</span>";
    }
  }

  function renderizarTurnoEnCola(listaEspera, usuario) {
    if (!Array.isArray(listaEspera) || !usuario) {
      return;
    }
    const nombreUsuario = String(
      (personaCargada && personaCargada.nombreCompleto) || usuario.nombreCompleto || ""
    )
      .trim()
      .toLowerCase();

    const turnoMiembro = listaEspera.find(function (item) {
      if (item.personaId && Number(item.personaId) === Number(usuario.personaId)) {
        return true;
      }
      if (item.numeroDocumento && String(item.numeroDocumento) === String(usuario.documento)) {
        return true;
      }
      return (
        nombreUsuario &&
        String(item.alumno || "")
          .trim()
          .toLowerCase() === nombreUsuario
      );
    });

    if (!turnoMiembro) {
      return;
    }

    if (posicionTurno) {
      posicionTurno.textContent = "#" + (turnoMiembro.posicionTurno || 1);
    }
    if (badgeTurnoEstado) {
      badgeTurnoEstado.className = turnoMiembro.atendido
        ? "psr-badge badge-emerald"
        : "psr-badge badge-amber";
      badgeTurnoEstado.textContent = turnoMiembro.atendido ? "Atendido" : "En espera";
    }
    if (labelTurnoSesion) {
      const rango =
        turnoMiembro.horaInicio && turnoMiembro.horaFin
          ? ValidacionesPSR.formatearHora(turnoMiembro.horaInicio) +
            " – " +
            ValidacionesPSR.formatearHora(turnoMiembro.horaFin)
          : ValidacionesPSR.formatearFecha(turnoMiembro.fechaSesion);
      labelTurnoSesion.textContent = "Sesión " + rango;
    }
    if (labelTurnoHora) {
      labelTurnoHora.textContent =
        "Llegada registrada: " + ValidacionesPSR.formatearHora(turnoMiembro.horaLlegada);
    }
    if (btnCheckin) {
      btnCheckin.disabled = true;
      btnCheckin.innerHTML =
        '<i class="ph-bold ph-check-circle"></i><span>Llegada registrada en la fila de hoy</span>';
    }
  }

  async function cargarDatosMiembro() {
    const usuario = SesionPSR.obtenerUsuario();
    if (!usuario) {
      window.location.href = "../index.html";
      return;
    }

    if (labelNombreCompleto && usuario.nombreCompleto) {
      labelNombreCompleto.textContent = usuario.nombreCompleto;
    }
    if (avatarInitials && usuario.nombreCompleto) {
      avatarInitials.textContent = obtenerIniciales(usuario.nombreCompleto);
    }
    if (labelDocumento && usuario.documento) {
      labelDocumento.textContent = "Doc: " + usuario.documento;
    }

    const personaId = usuario.personaId;
    const promesas = await Promise.allSettled([
      personaId ? ClienteHttp.get("/personas/" + personaId) : Promise.reject(new Error("Sin personaId")),
      personaId ? ClienteHttp.get("/personas/" + personaId + "/saldo") : Promise.reject(new Error("Sin personaId")),
      ClienteHttp.get("/tarifa"),
      ClienteHttp.get("/cola/espera")
    ]);

    if (promesas[0].status === "fulfilled" && promesas[0].value) {
      renderizarPerfilPersona(promesas[0].value);
    }

    const saldoDto = promesas[1].status === "fulfilled" ? promesas[1].value : null;
    const tarifaDto = promesas[2].status === "fulfilled" ? promesas[2].value : null;
    const montoTarifa = tarifaDto && tarifaDto.monto != null ? Number(tarifaDto.monto) : 5000;
    renderizarSaldoYTarifa(saldoDto, montoTarifa);

    if (promesas[3].status === "fulfilled" && promesas[3].value) {
      const cola = Array.isArray(promesas[3].value)
        ? promesas[3].value
        : Array.isArray(promesas[3].value.content)
          ? promesas[3].value.content
          : [];
      renderizarTurnoEnCola(cola, usuario);
    }
  }

  // Marcar llegada del miembro a la sesión activa (sp_registrar_llegada)
  if (btnCheckin) {
    btnCheckin.addEventListener("click", async function () {
      const usuario = SesionPSR.obtenerUsuario();
      const documento =
        (personaCargada && personaCargada.numeroDocumento) || (usuario && usuario.documento);
      const personaId = (personaCargada && personaCargada.id) || (usuario && usuario.personaId);

      if (!usuario || !personaId || !documento) {
        if (feedbackTurno) {
          feedbackTurno.textContent =
            "No se encontraron los datos de tu cuenta. Vuelve a iniciar sesión.";
        }
        return;
      }
      btnCheckin.disabled = true;
      if (feedbackTurno) feedbackTurno.classList.remove("text-danger");

      try {
        const respuesta = await ClienteHttp.post("/checkin/documento", {
          personaId: personaId,
          documentNumber: documento,
          documento: documento,
          metodoCheckin: "QR"
        });

        const turnoRecibido =
          respuesta && (respuesta.posicionTurno || respuesta.turno || respuesta.posicion);
        if (posicionTurno && turnoRecibido) {
          posicionTurno.textContent = "#" + turnoRecibido;
        }
      } catch (error) {
        if (feedbackTurno) {
          feedbackTurno.textContent = error.message;
          feedbackTurno.classList.add("text-danger");
        }
        btnCheckin.disabled = false;
        return;
      }

      btnCheckin.innerHTML =
        '<i class="ph-bold ph-check-circle"></i><span>Llegada registrada en la fila de hoy</span>';
      if (badgeTurnoEstado) {
        badgeTurnoEstado.className = "psr-badge badge-emerald";
        badgeTurnoEstado.textContent = "Registrado en cola";
      }
      if (feedbackTurno) {
        feedbackTurno.textContent =
          "Ya tienes tu turno asignado para la sesión de hoy. Acércate a recepción.";
      }
      await cargarDatosMiembro();
    });
  }

  // Actualización del contacto de emergencia del miembro
  if (btnGuardarContacto) {
    btnGuardarContacto.addEventListener("click", async function () {
      ValidacionesPSR.ocultarAlerta(alertBox);

      const valNom = ValidacionesPSR.validarTextoObligatorio(
        inputContactoNombre ? inputContactoNombre.value : "",
        100
      );
      if (!valNom.valido) {
        ValidacionesPSR.mostrarAlerta(
          alertBox,
          "El nombre del contacto de emergencia es obligatorio.",
          "error"
        );
        return;
      }

      const valTel = ValidacionesPSR.validarTelefono(
        inputContactoTel ? inputContactoTel.value : ""
      );
      if (!valTel.valido) {
        ValidacionesPSR.mostrarAlerta(alertBox, valTel.mensaje, "error");
        return;
      }

      const usuario = SesionPSR.obtenerUsuario();
      const personaId = (personaCargada && personaCargada.id) || (usuario && usuario.personaId);
      if (!personaId) {
        ValidacionesPSR.mostrarAlerta(
          alertBox,
          "No se encontró tu cuenta. Vuelve a iniciar sesión.",
          "error"
        );
        return;
      }

      const cuerpoActualizacion = {
        contactoNombre: valNom.valor,
        contactoTelefono: valTel.valor,
        contactoObservaciones: inputContactoObs ? inputContactoObs.value.trim() || null : null
      };

      try {
        const personaActualizada = await ClienteHttp.put("/personas/" + personaId, cuerpoActualizacion);
        renderizarPerfilPersona(personaActualizada);
      } catch (error) {
        ValidacionesPSR.mostrarAlerta(alertBox, error.message, "error");
        return;
      }

      ValidacionesPSR.mostrarAlerta(
        alertBox,
        "Contacto de emergencia actualizado correctamente.",
        "success"
      );
      btnGuardarContacto.innerHTML = '<i class="ph-bold ph-check"></i><span>Contacto Actualizado</span>';
      setTimeout(function () {
        btnGuardarContacto.innerHTML =
          '<i class="ph-bold ph-floppy-disk"></i><span>Actualizar Contacto</span>';
      }, 2500);
    });
  }

  cargarDatosMiembro();
});
