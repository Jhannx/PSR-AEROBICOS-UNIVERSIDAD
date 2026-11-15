USE psr_aerobicos;

/*
1. Procedimiento de insercion y validacion: sp_registrar_usuario_para_persona
Valida que la persona exista y no tenga cuenta previa antes de insertar en usuario y asignarle el rol MIEMBRO.
*/

DELIMITER //

CREATE PROCEDURE sp_registrar_usuario_para_persona(
    IN p_persona_id INT, IN p_password_hash VARCHAR(255), IN p_correo VARCHAR(150),
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
2. Procedimiento de extraccion: sp_reporte_asistencias_por_fecha
Consulta el listado de asistencias de una fecha cruzando asistencia, llegada, sesion, persona y usuario.
*/

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
3. Procedimiento con control transaccional: sp_crear_persona_con_contacto
Inserta una persona y su contacto de emergencia dentro de una transaccion.
Si ocurre un error en cualquiera de las dos inserciones ejecuta ROLLBACK, de lo contrario aplica COMMIT.
*/

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

