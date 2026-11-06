CREATE DATABASE psr_aerobicos;
USE psr_aerobicos;
-- 1. Rol
CREATE TABLE rol (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre ENUM(
        'ADMINISTRADOR',
        'RECEPCIONISTA',
        'PROFESOR',
        'MIEMBRO'
    ) NOT NULL UNIQUE
);
-- 2. Persona
CREATE TABLE persona (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tipo_documento ENUM('CC', 'TI', 'CE', 'PA') NOT NULL,
    numero_documento VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    fecha_nacimiento DATE,
    observaciones_salud TEXT,
    fuente_registro ENUM('ADMIN', 'AUTOREGISTRO') NOT NULL DEFAULT 'AUTOREGISTRO',
    fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    activo BOOLEAN NOT NULL DEFAULT TRUE
);
-- 3. Contacto de emergencia (1:1 con Persona)
CREATE TABLE contacto_emergencia (
    id INT AUTO_INCREMENT PRIMARY KEY,
    persona_id INT NOT NULL UNIQUE,
    nombre VARCHAR(150),
    telefono VARCHAR(20),
    observaciones TEXT,
    actualizado_en DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (persona_id) REFERENCES persona(id) ON DELETE CASCADE,
    CHECK (
        nombre IS NOT NULL
        OR telefono IS NOT NULL
        OR observaciones IS NOT NULL
    )
);
-- 4. Usuario
CREATE TABLE usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    numero_documento VARCHAR(20) UNIQUE,
    correo VARCHAR(150) UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    persona_id INT,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (persona_id) REFERENCES persona(id) ON DELETE CASCADE,
    CHECK (
        numero_documento IS NOT NULL
        OR correo IS NOT NULL
    )
);
-- 5. Usuario_Rol (relacion N:M)
CREATE TABLE usuario_rol (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    rol_id INT NOT NULL,
    asignado_en DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (usuario_id, rol_id),
    FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
    FOREIGN KEY (rol_id) REFERENCES rol(id) ON DELETE CASCADE
);
-- 6. Horario
CREATE TABLE horario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    dia_semana TINYINT NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    CHECK (
        dia_semana BETWEEN 1 AND 7
    ),
    CHECK (hora_fin > hora_inicio)
);
-- 7. Sesion
CREATE TABLE sesion (
    id INT AUTO_INCREMENT PRIMARY KEY,
    horario_id INT,
    fecha DATE NOT NULL,
    hora_inicio DATETIME NOT NULL,
    hora_fin DATETIME,
    profesor_usuario_id INT,
    cancelada BOOLEAN NOT NULL DEFAULT FALSE,
    total_asistencias INT NOT NULL DEFAULT 0,
    total_recaudo DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    UNIQUE (horario_id, fecha),
    FOREIGN KEY (horario_id) REFERENCES horario(id) ON DELETE
    SET NULL,
        FOREIGN KEY (profesor_usuario_id) REFERENCES usuario(id) ON DELETE
    SET NULL
);
-- 8. Llegada
CREATE TABLE llegada (
    id INT AUTO_INCREMENT PRIMARY KEY,
    persona_id INT NOT NULL,
    sesion_id INT NOT NULL,
    hora_llegada DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    metodo_checkin ENUM('QR', 'DOCUMENTO', 'TERMINAL', 'MANUAL') NOT NULL DEFAULT 'MANUAL',
    UNIQUE (persona_id, sesion_id),
    FOREIGN KEY (persona_id) REFERENCES persona(id) ON DELETE CASCADE,
    FOREIGN KEY (sesion_id) REFERENCES sesion(id) ON DELETE CASCADE
);
-- 9. Asistencia
CREATE TABLE asistencia (
    id INT AUTO_INCREMENT PRIMARY KEY,
    llegada_id INT NOT NULL UNIQUE,
    monto_cobrado DECIMAL(10, 2) NOT NULL,
    marcado_por INT NOT NULL,
    hora_marcado DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (llegada_id) REFERENCES llegada(id) ON DELETE CASCADE,
    FOREIGN KEY (marcado_por) REFERENCES usuario(id) ON DELETE RESTRICT,
    CHECK (monto_cobrado >= 0.00)
);
-- 10. Movimiento de cuenta
CREATE TABLE movimiento_cuenta (
    id INT AUTO_INCREMENT PRIMARY KEY,
    persona_id INT NOT NULL,
    llegada_id INT,
    monto_recibido DECIMAL(10, 2) NOT NULL,
    monto_abonado DECIMAL(10, 2) NOT NULL,
    metodo_pago ENUM(
        'EFECTIVO',
        'NEQUI',
        'DAVIPLATA',
        'TRANSFERENCIA'
    ) NOT NULL DEFAULT 'EFECTIVO',
    tipo_movimiento ENUM('ABONO', 'REEMBOLSO') NOT NULL DEFAULT 'ABONO',
    vuelto_entregado BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_hora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario_id INT NOT NULL,
    nota TEXT,
    FOREIGN KEY (persona_id) REFERENCES persona(id) ON DELETE CASCADE,
    FOREIGN KEY (llegada_id) REFERENCES llegada(id) ON DELETE
    SET NULL,
        FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE RESTRICT,
        CHECK (
            monto_recibido >= 0.00
            AND monto_abonado >= 0.00
        )
);
-- 11. Tarifa
CREATE TABLE tarifa (
    id INT PRIMARY KEY DEFAULT 1,
    monto DECIMAL(10, 2) NOT NULL,
    actualizado_en DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    actualizado_por INT,
    CHECK (id = 1),
    CHECK (monto > 0.00),
    FOREIGN KEY (actualizado_por) REFERENCES usuario(id) ON DELETE
    SET NULL
);
-- 12. Historial de auditoria
CREATE TABLE historial_auditoria (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tabla_afectada VARCHAR(50) NOT NULL,
    registro_id INT NOT NULL,
    campo VARCHAR(50),
    valor_anterior TEXT,
    valor_nuevo TEXT,
    accion ENUM('INSERT', 'UPDATE', 'DELETE') NOT NULL,
    usuario_id INT,
    fecha_hora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE
    SET NULL
);