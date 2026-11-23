// Construcción del sidebar lateral, cabecera móvil e inicialización de validaciones

document.addEventListener("DOMContentLoaded", function () {
  if (typeof ValidacionesPSR !== "undefined") {
    ValidacionesPSR.inicializarRestriccionesNumericas(document);
  }

  const sidebarContainer = document.getElementById("app-sidebar");
  if (!sidebarContainer) {
    return;
  }

  const rutaActual = window.location.pathname.split("/").pop() || "recepcion.html";
  const usuarioActual = typeof SesionPSR !== "undefined"
    ? SesionPSR.obtenerUsuarioOPorDefecto()
    : { nombreCompleto: "Elena Vargas", roles: ["ADMINISTRADOR", "RECEPCIONISTA"] };

  const rolActivo = typeof SesionPSR !== "undefined"
    ? SesionPSR.obtenerRolActivo()
    : "ADMINISTRADOR";

  const infoRol = PSR_CONFIG.ROLES[rolActivo] || PSR_CONFIG.ROLES.ADMINISTRADOR;
  const itemsMenu = PSR_CONFIG.NAV_BY_ROLE[rolActivo] || PSR_CONFIG.NAV_BY_ROLE.ADMINISTRADOR;

  const nombreMostrar = usuarioActual.nombreCompleto || usuarioActual.nombre || "Usuario";
  const iniciales = typeof SesionPSR !== "undefined"
    ? SesionPSR.obtenerIniciales(nombreMostrar)
    : nombreMostrar.trim().charAt(0).toUpperCase();

  const puedeCambiarRol = Array.isArray(usuarioActual.roles) && usuarioActual.roles.length > 1;

  const enlacesHtml = itemsMenu
    .map(function (item) {
      const activoClass = item.page === rutaActual ? "is-active" : "";
      return (
        '<a href="' + item.page + '" class="sidebar-link ' + activoClass + '">' +
          '<i class="ph-bold ' + item.icon + '"></i>' +
          '<span>' + item.label + '</span>' +
        '</a>'
      );
    })
    .join("");

  const badgeRolHtml = puedeCambiarRol
    ? '<a href="seleccionar-rol.html" class="sidebar-role-badge ' + infoRol.badgeClass + '" title="Cambiar de rol">' +
        '<span>' + infoRol.label + '</span>' +
        '<i class="ph-bold ph-caret-up-down"></i>' +
      '</a>'
    : '<span class="sidebar-role-badge ' + infoRol.badgeClass + '">' +
        '<span>' + infoRol.label + '</span>' +
      '</span>';

  sidebarContainer.innerHTML =
    '<div class="sidebar-header">' +
      '<p class="sidebar-brand-title">PSR • Aeróbicos</p>' +
      '<p class="sidebar-brand-subtitle">Panel de gestión</p>' +
    '</div>' +
    '<nav class="sidebar-nav">' +
      enlacesHtml +
    '</nav>' +
    '<div class="sidebar-footer">' +
      '<div class="sidebar-user-row">' +
        '<div class="sidebar-user-info">' +
          '<div class="sidebar-avatar">' + iniciales + '</div>' +
          '<div class="sidebar-user-meta">' +
            '<p class="sidebar-user-name" title="' + nombreMostrar + '">' + nombreMostrar + '</p>' +
            badgeRolHtml +
          '</div>' +
        '</div>' +
        '<button type="button" id="btn-sidebar-logout" class="sidebar-logout-btn" title="Cerrar sesión">' +
          '<i class="ph-bold ph-sign-out"></i>' +
        '</button>' +
      '</div>' +
    '</div>';

  const btnLogout = document.getElementById("btn-sidebar-logout");
  if (btnLogout) {
    btnLogout.addEventListener("click", function () {
      if (typeof SesionPSR !== "undefined") {
        SesionPSR.cerrarSesion("../index.html");
      } else {
        window.location.href = "../index.html";
      }
    });
  }

  // Eventos para abrir y cerrar el sidebar en pantallas móviles
  const btnMenuMovil = document.getElementById("btn-mobile-menu");
  const backdrop = document.getElementById("sidebar-backdrop");

  function alternarSidebarMovil() {
    sidebarContainer.classList.toggle("is-open");
    if (backdrop) {
      backdrop.classList.toggle("is-open");
    }
  }

  if (btnMenuMovil) {
    btnMenuMovil.addEventListener("click", alternarSidebarMovil);
  }
  if (backdrop) {
    backdrop.addEventListener("click", alternarSidebarMovil);
  }
});
