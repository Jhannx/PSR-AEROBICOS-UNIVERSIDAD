// Manejo de sesión del usuario autenticado y selección de rol activo en localStorage

const SesionPSR = {
  guardarSesion: function (datosUsuario) {
    if (!datosUsuario) {
      return;
    }

    const roles = Array.isArray(datosUsuario.roles)
      ? datosUsuario.roles.filter(function (rol) { return Boolean(PSR_CONFIG.ROLES[rol]); })
      : [];
    const usuarioId = Number(datosUsuario.id || datosUsuario.usuarioId);
    if (!Number.isInteger(usuarioId) || usuarioId <= 0 || roles.length === 0) {
      return false;
    }

    const usuarioNormalizado = {
      sesionVersion: 2,
      id: usuarioId,
      personaId: datosUsuario.personaId || null,
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
    return true;
  },

  obtenerUsuario: function () {
    try {
      const texto = localStorage.getItem(PSR_CONFIG.STORAGE_KEYS.USUARIO);
      if (texto) {
        const usuario = JSON.parse(texto);
        if (usuario.sesionVersion === 2 && Number.isInteger(usuario.id) && usuario.id > 0 &&
            Array.isArray(usuario.roles) && usuario.roles.some(function (rol) { return Boolean(PSR_CONFIG.ROLES[rol]); })) {
          return usuario;
        }
        localStorage.removeItem(PSR_CONFIG.STORAGE_KEYS.USUARIO);
        localStorage.removeItem(PSR_CONFIG.STORAGE_KEYS.ROL_ACTIVO);
      }
    } catch (e) {
      localStorage.removeItem(PSR_CONFIG.STORAGE_KEYS.USUARIO);
    }
    return null;
  },

  estaAutenticado: function () {
    return this.obtenerUsuario() !== null;
  },

  obtenerRolesDisponibles: function () {
    const usuario = this.obtenerUsuario();
    if (Array.isArray(usuario.roles) && usuario.roles.length > 0) {
      return usuario.roles.filter(function (rol) { return Boolean(PSR_CONFIG.ROLES[rol]); });
    }
    return [];
  },

  obtenerRolActivo: function () {
    const rolGuardado = localStorage.getItem(PSR_CONFIG.STORAGE_KEYS.ROL_ACTIVO);
    const disponibles = this.obtenerRolesDisponibles();
    if (rolGuardado && disponibles.indexOf(rolGuardado) !== -1) {
      return rolGuardado;
    }
    return disponibles[0] || null;
  },

  establecerRolActivo: function (nuevoRol) {
    if (this.obtenerRolesDisponibles().indexOf(nuevoRol) !== -1) {
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
    return info ? info.home : null;
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
