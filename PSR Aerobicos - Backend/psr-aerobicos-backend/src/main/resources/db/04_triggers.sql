USE psr_aerobicos;

/*
1. Triggers de auditoria sobre movimiento_cuenta
Registran en historial_auditoria cuando se modifica el monto o se elimina un pago.
*/

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
2. Trigger de calculo de totales en sesion
Al registrar una asistencia suma 1 al total de asistencias y acumula el valor cobrado en la sesion.
*/

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

