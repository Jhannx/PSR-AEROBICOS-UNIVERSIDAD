// Manejo de sesión del usuario autenticado y selección de rol activo en localStorage

const SesionPSR = {
  guardarSesion: function (datosUsuario) {
    if (!datosUsuario) {
      return;
    }

    const roles = Array.isArray(datosUsuario.roles) && datosUsuario.roles.length > 0
      ? datosUsuario.roles
      : ["MIEMBRO"];

    const usuarioNormalizado = {
      id: datosUsuario.id || datosUsuario.usuarioId || 1,
      personaId: datosUsuario.personaId || datosUsuario.id || 1,
      documento: datosUsuario.documento || datosUsuario.numeroDocumento || "",
      nombreCompleto: datosUsuario.nombreCompleto || datosUsuario.nombre || "Usuario",
      correo: datosUsuario.correo || "",
      roles: roles
    };

    localStorage.setItem(PSR_CONFIG.STORAGE_KEYS.USUARIO, JSON.stringify(usuarioNormalizado));

    if (roles.length === 1) {
      localStorage.setItem(PSR_CONFIG.STORAGE_KEYS.ROL_ACTIVO, roles[0]);
    } else {
      localStorage.removeItem(PSR_CONFIG.STORAGE_KEYS.ROL_ACTIVO);
    }
  },

  obtenerUsuario: function () {
    try {
      const texto = localStorage.getItem(PSR_CONFIG.STORAGE_KEYS.USUARIO);
      if (texto) {
        return JSON.parse(texto);
      }
    } catch (e) {
      localStorage.removeItem(PSR_CONFIG.STORAGE_KEYS.USUARIO);
    }
    return null;
  },

  obtenerUsuarioOPorDefecto: function () {
    const usuario = this.obtenerUsuario();
    if (usuario) {
      return usuario;
    }
    return {
      id: 1,
      personaId: 1,
      documento: "1102812345",
      nombreCompleto: "Elena Vargas",
      correo: "elena.vargas@psr.com",
      roles: ["ADMINISTRADOR", "RECEPCIONISTA"]
    };
  },

  estaAutenticado: function () {
    return this.obtenerUsuario() !== null;
  },

  obtenerRolesDisponibles: function () {
    const usuario = this.obtenerUsuarioOPorDefecto();
    if (Array.isArray(usuario.roles) && usuario.roles.length > 0) {
      return usuario.roles;
    }
    return ["ADMINISTRADOR"];
  },

  obtenerRolActivo: function () {
    const rolGuardado = localStorage.getItem(PSR_CONFIG.STORAGE_KEYS.ROL_ACTIVO);
    if (rolGuardado && PSR_CONFIG.ROLES[rolGuardado]) {
      return rolGuardado;
    }
    const disponibles = this.obtenerRolesDisponibles();
    return disponibles[0] || "ADMINISTRADOR";
  },

  establecerRolActivo: function (nuevoRol) {
    if (PSR_CONFIG.ROLES[nuevoRol]) {
      localStorage.setItem(PSR_CONFIG.STORAGE_KEYS.ROL_ACTIVO, nuevoRol);
      return true;
    }
    return false;
  },

  requiereSeleccionDeRol: function () {
    const usuario = this.obtenerUsuario();
    if (!usuario || !Array.isArray(usuario.roles)) {
      return false;
    }
    const rolGuardado = localStorage.getItem(PSR_CONFIG.STORAGE_KEYS.ROL_ACTIVO);
    return usuario.roles.length > 1 && !rolGuardado;
  },

  obtenerRutaInicioRol: function (rol) {
    const clave = rol || this.obtenerRolActivo();
    const info = PSR_CONFIG.ROLES[clave];
    return info ? info.home : "recepcion.html";
  },

  cerrarSesion: function (rutaLogin) {
    localStorage.removeItem(PSR_CONFIG.STORAGE_KEYS.USUARIO);
    localStorage.removeItem(PSR_CONFIG.STORAGE_KEYS.ROL_ACTIVO);
    window.location.href = rutaLogin || "../index.html";
  },

  obtenerIniciales: function (nombreCompleto) {
    if (!nombreCompleto || typeof nombreCompleto !== "string") {
      return "US";
    }
    const partes = nombreCompleto.trim().split(/\s+/);
    if (partes.length === 1) {
      return partes[0].charAt(0).toUpperCase();
    }
    return (partes[0].charAt(0) + partes[1].charAt(0)).toUpperCase();
  }
};
