// Validaciones de formularios, bloqueo de letras en campos numéricos y formatos

const ValidacionesPSR = {
  soloDigitos: function (valor) {
    return String(valor || "").replace(/\D+/g, "");
  },

  bloquearLetrasEnInputNumerico: function (input) {
    if (!input || input.dataset.numericBound === "true") {
      return;
    }
    input.dataset.numericBound = "true";

    input.addEventListener("keydown", function (evento) {
      const teclasPermitidas = [
        "Backspace",
        "Delete",
        "Tab",
        "Escape",
        "Enter",
        "ArrowLeft",
        "ArrowRight",
        "ArrowUp",
        "ArrowDown",
        "Home",
        "End"
      ];

      if (teclasPermitidas.indexOf(evento.key) !== -1) {
        return;
      }

      if ((evento.ctrlKey || evento.metaKey) && ["a", "c", "v", "x"].indexOf(evento.key.toLowerCase()) !== -1) {
        return;
      }

      if (!/^\d$/.test(evento.key)) {
        evento.preventDefault();
      }
    });

    input.addEventListener("input", function () {
      const limpio = ValidacionesPSR.soloDigitos(input.value);
      if (input.value !== limpio) {
        input.value = limpio;
      }
    });

    input.addEventListener("paste", function (evento) {
      evento.preventDefault();
      const textoPegado = (evento.clipboardData || window.clipboardData).getData("text");
      const digitos = ValidacionesPSR.soloDigitos(textoPegado);
      const maxLen = input.maxLength > 0 ? input.maxLength : 20;
      input.value = (input.value + digitos).slice(0, maxLen);
    });
  },

  inicializarRestriccionesNumericas: function (raiz) {
    const contenedor = raiz || document;
    const camposNumericos = contenedor.querySelectorAll('input[inputmode="numeric"], input[data-numeric="true"]');
    camposNumericos.forEach(function (campo) {
      ValidacionesPSR.bloquearLetrasEnInputNumerico(campo);
    });
  },

  validarTextoObligatorio: function (valor, longitudMaxima) {
    const texto = String(valor || "").trim();
    if (!texto) {
      return { valido: false, mensaje: "Este campo es obligatorio." };
    }
    if (longitudMaxima && texto.length > longitudMaxima) {
      return { valido: false, mensaje: "No puede superar los " + longitudMaxima + " caracteres." };
    }
    return { valido: true, valor: texto };
  },

  validarDocumento: function (valor) {
    const limpio = this.soloDigitos(valor);
    if (!limpio) {
      return { valido: false, mensaje: "El número de documento es obligatorio." };
    }
    if (limpio.length < 6 || limpio.length > 20) {
      return { valido: false, mensaje: "El documento debe tener entre 6 y 20 dígitos numéricos." };
    }
    return { valido: true, valor: limpio };
  },

  validarTelefono: function (valor) {
    const limpio = this.soloDigitos(valor);
    if (!limpio) {
      return { valido: false, mensaje: "El número de teléfono es obligatorio." };
    }
    if (limpio.length < 7 || limpio.length > 20) {
      return { valido: false, mensaje: "El teléfono debe tener entre 7 y 20 dígitos numéricos." };
    }
    return { valido: true, valor: limpio };
  },

  validarCorreoOpcional: function (valor, obligatorio) {
    const texto = String(valor || "").trim();
    if (!texto) {
      if (obligatorio) {
        return { valido: false, mensaje: "El correo electrónico es obligatorio." };
      }
      return { valido: true, valor: "" };
    }
    if (texto.length > 150) {
      return { valido: false, mensaje: "El correo no puede exceder 150 caracteres." };
    }
    const patronCorreo = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!patronCorreo.test(texto)) {
      return { valido: false, mensaje: "Ingresa un correo electrónico válido." };
    }
    return { valido: true, valor: texto };
  },

  validarMontoPositivo: function (valor) {
    const limpio = this.soloDigitos(valor);
    const numero = Number(limpio);
    if (!limpio || isNaN(numero) || numero <= 0) {
      return { valido: false, mensaje: "El monto debe ser un valor numérico mayor a cero." };
    }
    if (limpio.length > 8) {
      return { valido: false, mensaje: "El monto no puede superar ocho dígitos enteros." };
    }
    return { valido: true, valor: numero };
  },

  mostrarAlerta: function (elementoAlerta, mensaje, tipo) {
    if (!elementoAlerta) {
      return;
    }
    elementoAlerta.classList.remove("d-none", "psr-alert-error", "psr-alert-success");
    if (tipo === "success") {
      elementoAlerta.classList.add("psr-alert-success");
    } else {
      elementoAlerta.classList.add("psr-alert-error");
    }

    const spanInterno = elementoAlerta.querySelector("span");
    if (spanInterno) {
      spanInterno.textContent = mensaje;
    } else {
      elementoAlerta.textContent = mensaje;
    }
  },

  ocultarAlerta: function (elementoAlerta) {
    if (!elementoAlerta) {
      return;
    }
    elementoAlerta.classList.add("d-none");
  },

  mostrarAlertaConexion: function (mensaje) {
    if (!document.body) return;

    let alerta = document.getElementById("psr-alerta-conexion-global");
    if (!alerta) {
      alerta = document.createElement("div");
      alerta.id = "psr-alerta-conexion-global";
      alerta.className = "psr-alert psr-alert-error psr-alert-conexion-global d-none";
      alerta.setAttribute("role", "alert");

      const texto = document.createElement("span");
      texto.dataset.conexionMensaje = "true";
      alerta.appendChild(texto);

      const cerrar = document.createElement("button");
      cerrar.type = "button";
      cerrar.className = "btn-close";
      cerrar.setAttribute("aria-label", "Cerrar aviso de conexión");
      cerrar.addEventListener("click", function () {
        alerta.classList.add("d-none");
      });
      alerta.appendChild(cerrar);
      document.body.appendChild(alerta);
    }

    alerta.querySelector("[data-conexion-mensaje]").textContent = mensaje;
    alerta.classList.remove("d-none");

    let modal = document.getElementById("psr-modal-error-conexion");
    if (!modal) {
      modal = document.createElement("div");
      modal.id = "psr-modal-error-conexion";
      modal.className = "modal fade";
      modal.tabIndex = -1;
      modal.setAttribute("aria-labelledby", "psr-modal-error-conexion-titulo");
      modal.setAttribute("aria-hidden", "true");
      modal.innerHTML =
        '<div class="modal-dialog modal-dialog-centered">' +
          '<div class="modal-content psr-modal">' +
            '<div class="modal-header">' +
              '<h2 class="modal-title" id="psr-modal-error-conexion-titulo">Conexión no disponible</h2>' +
              '<button type="button" class="btn-close-psr" data-bs-dismiss="modal" aria-label="Cerrar">' +
                '<i class="ph-bold ph-x"></i>' +
              '</button>' +
            '</div>' +
            '<div class="modal-body">' +
              '<div class="psr-alert psr-alert-error mb-3" role="alert">' +
                '<i class="ph-bold ph-warning-circle" aria-hidden="true"></i>' +
                '<span data-conexion-modal-mensaje></span>' +
              '</div>' +
              '<p class="psr-conexion-modal-ayuda mb-0">Comprueba que la base de datos esté encendida y vuelve a intentarlo.</p>' +
            '</div>' +
            '<div class="modal-footer">' +
              '<button type="button" class="btn-psr-primary" data-bs-dismiss="modal">Entendido</button>' +
            '</div>' +
          '</div>' +
        '</div>';
      document.body.appendChild(modal);
    }

    modal.querySelector("[data-conexion-modal-mensaje]").textContent = mensaje;
    if (window.bootstrap && window.bootstrap.Modal && !modal.classList.contains("show")) {
      window.bootstrap.Modal.getOrCreateInstance(modal).show();
    }
  },

  formatearCOP: function (monto) {
    const numero = Number(monto || 0);
    if (isNaN(numero)) {
      return "$0";
    }
    return "$" + Math.round(numero).toLocaleString("es-CO");
  },

  formatearFecha: function (fechaTexto) {
    if (!fechaTexto) {
      return "—";
    }
    const partes = String(fechaTexto).slice(0, 10).split("-");
    if (partes.length === 3) {
      return partes[2] + "/" + partes[1] + "/" + partes[0];
    }
    return fechaTexto;
  },

  formatearHora: function (fechaHoraTexto) {
    if (!fechaHoraTexto) {
      return "—";
    }
    const texto = String(fechaHoraTexto);
    if (/^\d{2}:\d{2}/.test(texto)) {
      const [hStr, mStr] = texto.split(":");
      const h = Number(hStr);
      const sufijo = h >= 12 ? "p. m." : "a. m.";
      const h12 = h % 12 === 0 ? 12 : h % 12;
      return String(h12).padStart(2, "0") + ":" + mStr + " " + sufijo;
    }
    const fecha = new Date(texto);
    if (isNaN(fecha.getTime())) {
      return texto;
    }
    return fecha.toLocaleTimeString("es-CO", { hour: "2-digit", minute: "2-digit" });
  },

  formatearFechaHora: function (fechaHoraTexto) {
    if (!fechaHoraTexto) {
      return "—";
    }
    const fecha = new Date(fechaHoraTexto);
    if (isNaN(fecha.getTime())) {
      return String(fechaHoraTexto);
    }
    return (
      fecha.toLocaleDateString("es-CO") +
      " " +
      fecha.toLocaleTimeString("es-CO", { hour: "2-digit", minute: "2-digit" })
    );
  }
};
