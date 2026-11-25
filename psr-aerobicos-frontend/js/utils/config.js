// Configuración central del frontend y definición de navegación por rol

const PSR_CONFIG = {
  API_BASE_URL: "http://localhost:8080/api",
  ACADEMY_NAME: "PSR Aeróbicos",

  STORAGE_KEYS: {
    USUARIO: "psr_usuario",
    ROL_ACTIVO: "psr_rol_activo"
  },

  ROLES: {
    ADMINISTRADOR: {
      key: "ADMINISTRADOR",
      label: "Administrador",
      badgeClass: "badge-purple",
      description: "Control total del sistema, sesiones, roles, reportes y auditoría",
      icon: "ph-gear",
      home: "reportes.html"
    },
    RECEPCIONISTA: {
      key: "RECEPCIONISTA",
      label: "Recepción",
      badgeClass: "badge-emerald",
      description: "Check-in en cola del día, cobros, asistencias y personas",
      icon: "ph-users",
      home: "recepcion.html"
    },
    PROFESOR: {
      key: "PROFESOR",
      label: "Profesor",
      badgeClass: "badge-amber",
      description: "Consulta de sesiones programadas y horarios de clase",
      icon: "ph-graduation-cap",
      home: "sesiones.html"
    },
    MIEMBRO: {
      key: "MIEMBRO",
      label: "Miembro",
      badgeClass: "badge-blue",
      description: "Tu asistencia, turno de llegada y estado de cuenta",
      icon: "ph-user",
      home: "miembro.html"
    }
  },

  NAV_BY_ROLE: {
    ADMINISTRADOR: [
      { page: "reportes.html", label: "Reportes - Dashboard", icon: "ph-chart-bar" },
      { page: "recepcion.html", label: "Sesión - Aeróbicos", icon: "ph-user-check" },
      { page: "personas.html", label: "Personas registradas", icon: "ph-users" },
      { page: "sesiones.html", label: "Sesiones y Horarios", icon: "ph-calendar-blank" },
      { page: "roles.html", label: "Roles y Accesos", icon: "ph-key" },
      { page: "auditoria.html", label: "Auditoría de Actividad", icon: "ph-shield-check" },
      { page: "configuracion.html", label: "Configuración", icon: "ph-gear" },
      { page: "miembro.html", label: "Vista de Miembro", icon: "ph-user" }
    ],
    RECEPCIONISTA: [
      { page: "recepcion.html", label: "Sesión - Aeróbicos", icon: "ph-user-check" },
      { page: "personas.html", label: "Personas registradas", icon: "ph-users" },
      { page: "sesiones.html", label: "Sesiones y Horarios", icon: "ph-calendar-blank" },
      { page: "reportes.html", label: "Reportes - Dashboard", icon: "ph-chart-bar" },
      { page: "configuracion.html", label: "Configuración", icon: "ph-gear" }
    ],
    PROFESOR: [
      { page: "sesiones.html", label: "Sesiones y Horarios", icon: "ph-calendar-blank" },
      { page: "recepcion.html", label: "Sesión - Aeróbicos", icon: "ph-user-check" },
      { page: "personas.html", label: "Personas registradas", icon: "ph-users" },
      { page: "configuracion.html", label: "Configuración", icon: "ph-gear" }
    ],
    MIEMBRO: [
      { page: "miembro.html", label: "Mi Estado y Asistencia", icon: "ph-user-check" }
    ]
  }
};
