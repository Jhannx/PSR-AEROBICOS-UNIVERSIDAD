USE psr_aerobicos;

/*
1. Triggers de auditoria sobre movimiento_cuenta
Registran en historial_auditoria las modificaciones de montos, metodo de pago o eliminaciones.
*/
DROP TRIGGER IF EXISTS trg_auditoria_movimiento_update;
DROP TRIGGER IF EXISTS trg_auditoria_movimiento_delete;

DELIMITER //

CREATE TRIGGER trg_auditoria_movimiento_update
AFTER UPDATE ON movimiento_cuenta
FOR EACH ROW
BEGIN
    IF OLD.monto_abonado != NEW.monto_abonado THEN
        INSERT INTO historial_auditoria (
            tabla_afectada, registro_id, campo,
            valor_anterior, valor_nuevo, accion, usuario_id, fecha_hora
        ) VALUES (
            'movimiento_cuenta', OLD.id, 'monto_abonado',
            CAST(OLD.monto_abonado AS CHAR), CAST(NEW.monto_abonado AS CHAR),
            'UPDATE', NEW.usuario_id, NOW()
        );
    END IF;

    IF OLD.monto_recibido != NEW.monto_recibido THEN
        INSERT INTO historial_auditoria (
            tabla_afectada, registro_id, campo,
            valor_anterior, valor_nuevo, accion, usuario_id, fecha_hora
        ) VALUES (
            'movimiento_cuenta', OLD.id, 'monto_recibido',
            CAST(OLD.monto_recibido AS CHAR), CAST(NEW.monto_recibido AS CHAR),
            'UPDATE', NEW.usuario_id, NOW()
        );
    END IF;

    IF OLD.metodo_pago != NEW.metodo_pago THEN
        INSERT INTO historial_auditoria (
            tabla_afectada, registro_id, campo,
            valor_anterior, valor_nuevo, accion, usuario_id, fecha_hora
        ) VALUES (
            'movimiento_cuenta', OLD.id, 'metodo_pago',
            OLD.metodo_pago, NEW.metodo_pago,
            'UPDATE', NEW.usuario_id, NOW()
        );
    END IF;
END //

CREATE TRIGGER trg_auditoria_movimiento_delete
AFTER DELETE ON movimiento_cuenta
FOR EACH ROW
BEGIN
    INSERT INTO historial_auditoria (
        tabla_afectada, registro_id, campo,
        valor_anterior, valor_nuevo, accion, usuario_id, fecha_hora
    ) VALUES (
        'movimiento_cuenta', OLD.id, 'REGISTRO_COMPLETO',
        CONCAT('MontoRecibido: ', OLD.monto_recibido, ', MontoAbonado: ', OLD.monto_abonado, ', Metodo: ', OLD.metodo_pago),
        NULL, 'DELETE', OLD.usuario_id, NOW()
    );
END //

DELIMITER ;

/*
2. Trigger de auditoria en tarifa
Registra en historial_auditoria cuando se modifica el precio de la sesion.
*/
DROP TRIGGER IF EXISTS trg_tarifa_actualizar_y_auditar;

DELIMITER //

CREATE TRIGGER trg_tarifa_actualizar_y_auditar
BEFORE UPDATE ON tarifa
FOR EACH ROW
BEGIN
    IF OLD.monto != NEW.monto THEN
        INSERT INTO historial_auditoria (
            tabla_afectada, registro_id, campo,
            valor_anterior, valor_nuevo, accion, usuario_id, fecha_hora
        ) VALUES (
            'tarifa', OLD.id, 'monto',
            CAST(OLD.monto AS CHAR), CAST(NEW.monto AS CHAR),
            'UPDATE', NEW.actualizado_por, NOW()
        );
    END IF;
END //

DELIMITER ;

/*
3. Trigger de calculo de totales en sesion
Al registrar una asistencia incrementa el contador de asistencias y suma el recaudo en la sesion.
*/
DROP TRIGGER IF EXISTS trg_calcular_totales_sesion_insert;

DELIMITER //

CREATE TRIGGER trg_calcular_totales_sesion_insert
AFTER INSERT ON asistencia
FOR EACH ROW
BEGIN
    DECLARE v_sesion_id INT;

    SELECT sesion_id INTO v_sesion_id
    FROM llegada
    WHERE id = NEW.llegada_id;

    IF v_sesion_id IS NOT NULL THEN
        UPDATE sesion
        SET total_asistencias = total_asistencias + 1,
            total_recaudo = total_recaudo + NEW.monto_cobrado
        WHERE id = v_sesion_id;
    END IF;
END //

DELIMITER ;

/*
4. Trigger de validacion de persona para roles operativos
Verifica que un usuario con rol MIEMBRO, PROFESOR o RECEPCIONISTA tenga una persona asociada.
*/
DROP TRIGGER IF EXISTS trg_validar_persona_requerida;

DELIMITER //

CREATE TRIGGER trg_validar_persona_requerida
BEFORE INSERT ON usuario_rol
FOR EACH ROW
BEGIN
    DECLARE v_rol_nombre VARCHAR(50);
    DECLARE v_persona_id INT;

    SELECT nombre INTO v_rol_nombre FROM rol WHERE id = NEW.rol_id;

    IF v_rol_nombre IN ('MIEMBRO', 'PROFESOR', 'RECEPCIONISTA') THEN
        SELECT persona_id INTO v_persona_id FROM usuario WHERE id = NEW.usuario_id;
        IF v_persona_id IS NULL THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'El usuario con ese rol debe estar asociado a una persona registrada';
        END IF;
    END IF;
END //

DELIMITER ;
