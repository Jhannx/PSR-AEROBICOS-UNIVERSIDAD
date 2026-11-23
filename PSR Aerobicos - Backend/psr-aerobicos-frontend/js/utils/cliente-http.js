// Cliente HTTP centralizado para comunicarse con los endpoints REST del backend Java

const ClienteHttp = {
  solicitar: async function (ruta, opciones) {
    const config = opciones || {};
    const metodo = config.method || "GET";
    const url = PSR_CONFIG.API_BASE_URL + ruta;

    const cabeceras = {
      "Accept": "application/json"
    };

    if (config.body !== undefined) {
      cabeceras["Content-Type"] = "application/json";
    }

    let respuesta;
    try {
      respuesta = await fetch(url, {
        method: metodo,
        headers: cabeceras,
        body: config.body !== undefined ? JSON.stringify(config.body) : undefined
      });
    } catch (errorRed) {
      const errorConexion = new Error(
        "No fue posible conectar con el servidor. Verifica que el backend y la base de datos estén en línea."
      );
      errorConexion.sinConexion = true;
      throw errorConexion;
    }

    let cuerpoJson = null;
    const tipoContenido = respuesta.headers.get("content-type") || "";
    if (tipoContenido.indexOf("application/json") !== -1) {
      try {
        cuerpoJson = await respuesta.json();
      } catch (e) {
        cuerpoJson = null;
      }
    }

    if (!respuesta.ok || (cuerpoJson && cuerpoJson.codigo && cuerpoJson.codigo >= 400)) {
      const mensajeServidor =
        (cuerpoJson && (cuerpoJson.mensaje || cuerpoJson.message || cuerpoJson.error)) ||
        "Ocurrió un error al procesar la solicitud (HTTP " + respuesta.status + ").";

      const errorHttp = new Error(mensajeServidor);
      errorHttp.codigo = (cuerpoJson && cuerpoJson.codigo) || respuesta.status;
      errorHttp.respuesta = cuerpoJson;
      throw errorHttp;
    }

    if (cuerpoJson && Object.prototype.hasOwnProperty.call(cuerpoJson, "datos")) {
      return cuerpoJson.datos;
    }

    return cuerpoJson;
  },

  get: function (ruta) {
    return this.solicitar(ruta, { method: "GET" });
  },

  post: function (ruta, datos) {
    return this.solicitar(ruta, { method: "POST", body: datos });
  },

  put: function (ruta, datos) {
    return this.solicitar(ruta, { method: "PUT", body: datos });
  },

  patch: function (ruta, datos) {
    return this.solicitar(ruta, { method: "PATCH", body: datos });
  },

  del: function (ruta) {
    return this.solicitar(ruta, { method: "DELETE" });
  }
};
