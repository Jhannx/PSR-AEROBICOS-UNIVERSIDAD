USE psr_aerobicos;

/*
1. sp_registrar_persona
Registra un nuevo participante validando que el documento no este duplicado y retorna su ID.
*/
DROP PROCEDURE IF EXISTS sp_registrar_persona;

DELIMITER //

CREATE PROCEDURE sp_registrar_persona(
    IN p_tipo_doc VARCHAR(10),
    IN p_num_doc VARCHAR(20),
    IN p_nombre VARCHAR(100),
    IN p_apellidos VARCHAR(100),
    IN p_telefono VARCHAR(20),
    IN p_fecha_nac DATE,
    IN p_obs_salud TEXT,
    IN p_fuente VARCHAR(20),
    OUT p_persona_id INT
)
BEGIN
    IF EXISTS (SELECT 1 FROM persona WHERE numero_documento = p_num_doc) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Ya existe una persona registrada con ese numero de documento';
    END IF;

    INSERT INTO persona (
        tipo_documento, numero_documento, nombre, apellidos,
        telefono, fecha_nacimiento, observaciones_salud, fuente_registro
    ) VALUES (
        p_tipo_doc, p_num_doc, p_nombre, p_apellidos,
        p_telefono, p_fecha_nac, p_obs_salud, IFNULL(p_fuente, 'ADMIN')
    );

    SET p_persona_id = LAST_INSERT_ID();
END //

DELIMITER ;

/*
2. sp_registrar_usuario_para_persona
Valida que la persona exista y no tenga cuenta previa antes de insertar en usuario y asignarle el rol MIEMBRO.
*/
DROP PROCEDURE IF EXISTS sp_registrar_usuario_para_persona;

DELIMITER //

CREATE PROCEDURE sp_registrar_usuario_para_persona(
    IN p_persona_id INT,
    IN p_password_hash VARCHAR(255),
    IN p_correo VARCHAR(150),
    OUT p_usuario_id INT
)
BEGIN
    DECLARE v_documento VARCHAR(20);
    DECLARE v_rol_id INT;

    SELECT numero_documento INTO v_documento
    FROM persona
    WHERE id = p_persona_id;

    IF v_documento IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'La persona especificada no existe en el sistema';
    END IF;

    IF EXISTS (SELECT 1 FROM usuario WHERE persona_id = p_persona_id) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'La persona ya tiene una cuenta de usuario asignada';
    END IF;

    INSERT INTO usuario (numero_documento, correo, password_hash, persona_id, activo)
    VALUES (v_documento, p_correo, p_password_hash, p_persona_id, TRUE);

    SET p_usuario_id = LAST_INSERT_ID();

    SELECT id INTO v_rol_id FROM rol WHERE nombre = 'MIEMBRO';
    IF v_rol_id IS NOT NULL THEN
        INSERT INTO usuario_rol (usuario_id, rol_id) VALUES (p_usuario_id, v_rol_id);
    END IF;
END //

DELIMITER ;

/*
3. sp_asignar_rol
Asigna un rol adicional a un usuario en la tabla usuario_rol.
*/
DROP PROCEDURE IF EXISTS sp_asignar_rol;

DELIMITER //

CREATE PROCEDURE sp_asignar_rol(
    IN p_usuario_id INT,
    IN p_rol_nombre VARCHAR(50)
)
BEGIN
    DECLARE v_rol_id INT;

    SELECT id INTO v_rol_id FROM rol WHERE nombre = p_rol_nombre;
    IF v_rol_id IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El rol especificado no existe';
    END IF;

    INSERT IGNORE INTO usuario_rol (usuario_id, rol_id)
    VALUES (p_usuario_id, v_rol_id);
END //

DELIMITER ;

/*
4. sp_registrar_llegada
Registra el check-in de un participante en una sesion activa.
*/
DROP PROCEDURE IF EXISTS sp_registrar_llegada;

DELIMITER //

CREATE PROCEDURE sp_registrar_llegada(
    IN p_persona_id INT,
    IN p_sesion_id INT,
    IN p_metodo_checkin VARCHAR(20),
    OUT p_llegada_id INT
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM persona WHERE id = p_persona_id AND activo = TRUE) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'La persona no existe o se encuentra inactiva';
    END IF;

    IF NOT EXISTS (SELECT 1 FROM sesion WHERE id = p_sesion_id AND cancelada = FALSE) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'La sesion no existe o se encuentra cancelada';
    END IF;

    SELECT id INTO p_llegada_id
    FROM llegada
    WHERE persona_id = p_persona_id AND sesion_id = p_sesion_id;

    IF p_llegada_id IS NULL THEN
        INSERT INTO llegada (persona_id, sesion_id, hora_llegada, metodo_checkin)
        VALUES (p_persona_id, p_sesion_id, NOW(), IFNULL(p_metodo_checkin, 'MANUAL'));

        SET p_llegada_id = LAST_INSERT_ID();
    END IF;
END //

DELIMITER ;

/*
5. sp_registrar_asistencia
Confirma la llegada del participante y registra el cobro con la tarifa vigente.
*/
DROP PROCEDURE IF EXISTS sp_registrar_asistencia;

DELIMITER //

CREATE PROCEDURE sp_registrar_asistencia(
    IN p_llegada_id INT,
    IN p_usuario_id INT,
    OUT p_asistencia_id INT
)
BEGIN
    DECLARE v_monto DECIMAL(10, 2);

    IF NOT EXISTS (SELECT 1 FROM llegada WHERE id = p_llegada_id) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El registro de llegada no existe';
    END IF;

    IF EXISTS (SELECT 1 FROM asistencia WHERE llegada_id = p_llegada_id) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'La llegada ya fue confirmada previamente';
    END IF;

    SELECT monto INTO v_monto FROM tarifa WHERE id = 1;
    IF v_monto IS NULL THEN
        SET v_monto = 5000.00;
    END IF;

    INSERT INTO asistencia (llegada_id, monto_cobrado, marcado_por, hora_marcado)
    VALUES (p_llegada_id, v_monto, p_usuario_id, NOW());

    SET p_asistencia_id = LAST_INSERT_ID();
END //

DELIMITER ;

/*
6. sp_registrar_movimiento
Registra un abono o pago en la cuenta de un participante.
*/
DROP PROCEDURE IF EXISTS sp_registrar_movimiento;

DELIMITER //

CREATE PROCEDURE sp_registrar_movimiento(
    IN p_persona_id INT,
    IN p_llegada_id INT,
    IN p_monto_recibido DECIMAL(10, 2),
    IN p_monto_abonado DECIMAL(10, 2),
    IN p_metodo_pago VARCHAR(20),
    IN p_usuario_id INT,
    IN p_nota TEXT,
    OUT p_movimiento_id INT
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM persona WHERE id = p_persona_id AND activo = TRUE) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'La persona no existe o esta inactiva';
    END IF;

    INSERT INTO movimiento_cuenta (
        persona_id, llegada_id, monto_recibido, monto_abonado,
        metodo_pago, tipo_movimiento, vuelto_entregado, usuario_id, nota
    ) VALUES (
        p_persona_id, p_llegada_id, p_monto_recibido, p_monto_abonado,
        IFNULL(p_metodo_pago, 'EFECTIVO'), 'ABONO', TRUE, p_usuario_id, p_nota
    );

    SET p_movimiento_id = LAST_INSERT_ID();
END //

DELIMITER ;

/*
7. sp_crear_sesion_manual
Crea una sesion de aerobicos verificando que no se duplique el horario en la misma fecha.
*/
DROP PROCEDURE IF EXISTS sp_crear_sesion_manual;

DELIMITER //

CREATE PROCEDURE sp_crear_sesion_manual(
    IN p_horario_id INT,
    IN p_fecha DATE,
    IN p_hora_inicio DATETIME,
    IN p_hora_fin DATETIME,
    IN p_profesor_usuario_id INT,
    OUT p_sesion_id INT
)
BEGIN
    IF p_horario_id IS NOT NULL AND EXISTS (
        SELECT 1 FROM sesion WHERE horario_id = p_horario_id AND fecha = p_fecha
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Ya existe una sesion programada para ese horario y fecha';
    END IF;

    INSERT INTO sesion (horario_id, fecha, hora_inicio, hora_fin, profesor_usuario_id)
    VALUES (p_horario_id, p_fecha, p_hora_inicio, p_hora_fin, p_profesor_usuario_id);

    SET p_sesion_id = LAST_INSERT_ID();
END //

DELIMITER ;

/*
8. sp_cancelar_sesion
Marca una sesion como cancelada y deja registro en historial_auditoria.
*/
DROP PROCEDURE IF EXISTS sp_cancelar_sesion;

DELIMITER //

CREATE PROCEDURE sp_cancelar_sesion(
    IN p_sesion_id INT,
    IN p_usuario_id INT
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM sesion WHERE id = p_sesion_id AND cancelada = FALSE) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'La sesion no existe o ya se encuentra cancelada';
    END IF;

    UPDATE sesion SET cancelada = TRUE WHERE id = p_sesion_id;

    INSERT INTO historial_auditoria (
        tabla_afectada, registro_id, campo, valor_anterior, valor_nuevo, accion, usuario_id
    ) VALUES (
        'sesion', p_sesion_id, 'cancelada', 'FALSE', 'TRUE', 'UPDATE', p_usuario_id
    );
END //

DELIMITER ;

/*
9. sp_reporte_asistencias_por_fecha
Consulta el listado de asistencias de una fecha cruzando asistencia, llegada, sesion, persona y usuario.
*/
DROP PROCEDURE IF EXISTS sp_reporte_asistencias_por_fecha;

DELIMITER //

CREATE PROCEDURE sp_reporte_asistencias_por_fecha(
    IN p_fecha DATE
)
BEGIN
    SELECT 
        a.id AS asistencia_id,
        s.fecha AS fecha_sesion,
        TIME(s.hora_inicio) AS hora_clase,
        CONCAT(p.nombre, ' ', p.apellidos) AS alumno,
        p.numero_documento,
        p.telefono,
        l.hora_llegada,
        l.metodo_checkin,
        a.monto_cobrado,
        u.correo AS confirmado_por
    FROM asistencia a
    INNER JOIN llegada l ON l.id = a.llegada_id
    INNER JOIN sesion s ON s.id = l.sesion_id
    INNER JOIN persona p ON p.id = l.persona_id
    INNER JOIN usuario u ON u.id = a.marcado_por
    WHERE s.fecha = p_fecha
    ORDER BY l.hora_llegada ASC;
END //

DELIMITER ;

/*
10. sp_crear_persona_con_contacto
Inserta una persona y su contacto de emergencia dentro de una transaccion con COMMIT y ROLLBACK.
*/
DROP PROCEDURE IF EXISTS sp_crear_persona_con_contacto;

DELIMITER //

CREATE PROCEDURE sp_crear_persona_con_contacto(
    IN p_tipo_doc VARCHAR(10),
    IN p_num_doc VARCHAR(20),
    IN p_nombre VARCHAR(100),
    IN p_apellidos VARCHAR(100),
    IN p_telefono VARCHAR(20),
    IN p_contacto_nom VARCHAR(150),
    IN p_contacto_tel VARCHAR(20),
    IN p_contacto_obs TEXT,
    OUT p_resultado VARCHAR(120)
)
BEGIN
    DECLARE v_persona_id INT;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_resultado = 'ERROR: Transaccion revertida con ROLLBACK';
    END;

    START TRANSACTION;

    INSERT INTO persona (tipo_documento, numero_documento, nombre, apellidos, telefono, fuente_registro)
    VALUES (p_tipo_doc, p_num_doc, p_nombre, p_apellidos, p_telefono, 'ADMIN');

    SET v_persona_id = LAST_INSERT_ID();

    INSERT INTO contacto_emergencia (persona_id, nombre, telefono, observaciones)
    VALUES (v_persona_id, p_contacto_nom, p_contacto_tel, p_contacto_obs);

    COMMIT;
    SET p_resultado = CONCAT('OK: Persona creada con ID ', v_persona_id);
END //

DELIMITER ;
