USE psr_aerobicos;

-- 1. Rol (Catalogo base de roles del sistema)
INSERT INTO rol (id, nombre) VALUES
(1, 'ADMINISTRADOR'),
(2, 'RECEPCIONISTA'),
(3, 'PROFESOR'),
(4, 'MIEMBRO');

-- 2. Persona (Personal y alumnos)
INSERT INTO persona (id, tipo_documento, numero_documento, nombre, apellidos, telefono, fecha_nacimiento, observaciones_salud, fuente_registro, fecha_registro, activo) VALUES
(1, 'CC', '1102812001', 'Jhan Paul', 'Corena Segura', '3001234567', '2002-05-14', NULL, 'ADMIN', '2026-08-01 08:00:00', TRUE),
(2, 'CC', '1102812002', 'Laura Vanessa', 'Mercado Hoyos', '3012345678', '1998-09-20', NULL, 'ADMIN', '2026-08-01 08:30:00', TRUE),
(3, 'CC', '1102812003', 'Carlos Andres', 'Mendoza Perez', '3023456789', '1995-03-12', NULL, 'ADMIN', '2026-08-05 09:00:00', TRUE),
(4, 'CC', '1102812004', 'Sofia Elena', 'Martinez Lopez', '3004567890', '1999-11-05', 'Asma leve', 'AUTOREGISTRO', '2026-08-10 10:00:00', TRUE),
(5, 'CC', '1102812005', 'Mateo David', 'Ramos Gomez', '3015678901', '2001-07-22', NULL, 'AUTOREGISTRO', '2026-08-15 14:00:00', TRUE),
(6, 'CC', '1102812006', 'Valentina', 'Gomez Hoyos', '3046789012', '2003-01-30', NULL, 'AUTOREGISTRO', '2026-08-20 16:30:00', TRUE),
(7, 'CC', '1102812007', 'Andres Felipe', 'Petro Hernandez', '3107890123', '1997-12-18', NULL, 'AUTOREGISTRO', '2026-09-01 08:15:00', TRUE),
(8, 'CC', '1102812008', 'Mariana Lucia', 'Morales Vega', '3118901234', '2000-04-25', 'Lesion rodilla recuperada', 'AUTOREGISTRO', '2026-09-05 11:00:00', TRUE),
(9, 'CC', '1102812009', 'Diego Alejandro', 'Soto Durango', '3129012345', '1996-08-14', NULL, 'AUTOREGISTRO', '2026-09-10 15:00:00', TRUE),
(10, 'CC', '1102812010', 'Isabella', 'Diaz Paternina', '3130123456', '2004-06-08', NULL, 'AUTOREGISTRO', '2026-09-12 17:00:00', TRUE),
(11, 'CC', '1102812011', 'Camilo Jose', 'Vergara Sierra', '3141234567', '1994-10-03', 'Hipertension controlada', 'ADMIN', '2026-09-15 09:30:00', TRUE),
(12, 'CC', '1102812012', 'Gabriela', 'Mejia Benitez', '3152345678', '2002-02-17', NULL, 'AUTOREGISTRO', '2026-09-18 10:45:00', TRUE),
(13, 'TI', '1067912013', 'Samuel David', 'Polo Arrieta', '3163456789', '2008-11-29', NULL, 'AUTOREGISTRO', '2026-09-20 14:20:00', TRUE),
(14, 'CC', '1102812014', 'Daniela', 'Ospina Castillo', '3174567890', '1999-07-07', NULL, 'AUTOREGISTRO', '2026-09-22 16:00:00', TRUE),
(15, 'CC', '1102812015', 'Juan Pablo', 'Berrocal Ruiz', '3185678901', '2001-03-21', NULL, 'AUTOREGISTRO', '2026-09-25 18:00:00', TRUE),
(16, 'CC', '1102812016', 'Natalia Andrea', 'Conde Madera', '3196789012', '2000-09-15', 'Alergia al polvo', 'AUTOREGISTRO', '2026-09-28 08:30:00', TRUE),
(17, 'CC', '1102812017', 'Esteban Manuel', 'Ruiz Montes', '3207890123', '1995-05-19', NULL, 'AUTOREGISTRO', '2026-10-01 07:45:00', TRUE),
(18, 'CC', '1102812018', 'Sara Victoria', 'Fuentes Luna', '3218901234', '2003-12-02', NULL, 'AUTOREGISTRO', '2026-10-02 09:15:00', TRUE);

-- 3. Contacto de emergencia (1:1 con Persona)
INSERT INTO contacto_emergencia (id, persona_id, nombre, telefono, observaciones, actualizado_en) VALUES
(1, 1, 'Marta Segura', '3009991111', 'Madre', '2026-08-01 08:00:00'),
(2, 2, 'Pedro Mercado', '3019992222', 'Padre', '2026-08-01 08:30:00'),
(3, 3, 'Lucia Mendoza', '3029993333', 'Hermana', '2026-08-05 09:00:00'),
(4, 4, 'Jorge Martinez', '3009994444', 'Padre', '2026-08-10 10:00:00'),
(5, 5, 'Claudia Ramos', '3019995555', 'Madre', '2026-08-15 14:00:00'),
(6, 6, 'Hernan Gomez', '3049996666', 'Tio', '2026-08-20 16:30:00'),
(7, 7, 'Gloria Petro', '3109997777', 'Esposa', '2026-09-01 08:15:00'),
(8, 8, 'Felipe Morales', '3119998888', 'Hermano', '2026-09-05 11:00:00'),
(9, 9, 'Sandra Soto', '3129999999', 'Madre', '2026-09-10 15:00:00'),
(10, 10, 'Marcos Diaz', '3139990000', 'Padre', '2026-09-12 17:00:00'),
(11, 11, 'Carmen Vergara', '3149991122', 'Hermana', '2026-09-15 09:30:00'),
(12, 12, 'Patricia Benitez', '3159992233', 'Madre', '2026-09-18 10:45:00'),
(13, 13, 'Ricardo Polo', '3169993344', 'Padre acudiente', '2026-09-20 14:20:00'),
(14, 14, 'Alvaro Ospina', '3179994455', 'Padre', '2026-09-22 16:00:00'),
(15, 15, 'Elena Berrocal', '3189995566', 'Hermana', '2026-09-25 18:00:00');

-- 4. Usuario (Cuentas del personal y miembros)
INSERT INTO usuario (id, numero_documento, correo, password_hash, persona_id, activo, fecha_creacion) VALUES
(1, '1102812001', 'admin@psr.com', '$2a$10$abcdefghijklmnopqrstuv1', 1, TRUE, '2026-08-01 08:00:00'),
(2, '1102812002', 'recepcion@psr.com', '$2a$10$abcdefghijklmnopqrstuv2', 2, TRUE, '2026-08-01 08:30:00'),
(3, '1102812003', 'carlos.mendoza@psr.com', '$2a$10$abcdefghijklmnopqrstuv3', 3, TRUE, '2026-08-05 09:00:00'),
(4, '1102812004', 'sofia.martinez@gmail.com', '$2a$10$abcdefghijklmnopqrstuv4', 4, TRUE, '2026-08-10 10:00:00'),
(5, '1102812005', 'mateo.ramos@gmail.com', '$2a$10$abcdefghijklmnopqrstuv5', 5, TRUE, '2026-08-15 14:00:00'),
(6, '1102812006', 'valentina.gomez@gmail.com', '$2a$10$abcdefghijklmnopqrstuv6', 6, TRUE, '2026-08-20 16:30:00'),
(7, '1102812007', 'andres.petro@gmail.com', '$2a$10$abcdefghijklmnopqrstuv7', 7, TRUE, '2026-09-01 08:15:00'),
(8, '1102812008', 'mariana.morales@gmail.com', '$2a$10$abcdefghijklmnopqrstuv8', 8, TRUE, '2026-09-05 11:00:00'),
(9, '1102812009', 'diego.soto@gmail.com', '$2a$10$abcdefghijklmnopqrstuv9', 9, TRUE, '2026-09-10 15:00:00'),
(10, '1102812010', 'isabella.diaz@gmail.com', '$2a$10$abcdefghijklmnopqrstuv0', 10, TRUE, '2026-09-12 17:00:00'),
(11, '1102812011', 'camilo.vergara@gmail.com', '$2a$10$abcdefghijklmnopqrstua1', 11, TRUE, '2026-09-15 09:30:00'),
(12, '1102812012', 'gabriela.mejia@gmail.com', '$2a$10$abcdefghijklmnopqrstua2', 12, TRUE, '2026-09-18 10:45:00'),
(13, '1067912013', 'samuel.polo@gmail.com', '$2a$10$abcdefghijklmnopqrstua3', 13, TRUE, '2026-09-20 14:20:00'),
(14, '1102812014', 'daniela.ospina@gmail.com', '$2a$10$abcdefghijklmnopqrstua4', 14, TRUE, '2026-09-22 16:00:00'),
(15, '1102812015', 'juan.berrocal@gmail.com', '$2a$10$abcdefghijklmnopqrstua5', 15, TRUE, '2026-09-25 18:00:00');

-- 5. Usuario_Rol (Asignacion de roles)
INSERT INTO usuario_rol (id, usuario_id, rol_id, asignado_en) VALUES
(1, 1, 1, '2026-08-01 08:00:00'),
(2, 1, 2, '2026-08-01 08:00:00'),
(3, 2, 2, '2026-08-01 08:30:00'),
(4, 3, 3, '2026-08-05 09:00:00'),
(5, 4, 4, '2026-08-10 10:00:00'),
(6, 5, 4, '2026-08-15 14:00:00'),
(7, 6, 4, '2026-08-20 16:30:00'),
(8, 7, 4, '2026-09-01 08:15:00'),
(9, 8, 4, '2026-09-05 11:00:00'),
(10, 9, 4, '2026-09-10 15:00:00'),
(11, 10, 4, '2026-09-12 17:00:00'),
(12, 11, 4, '2026-09-15 09:30:00'),
(13, 12, 4, '2026-09-18 10:45:00'),
(14, 13, 4, '2026-09-20 14:20:00'),
(15, 14, 4, '2026-09-22 16:00:00'),
(16, 15, 4, '2026-09-25 18:00:00');

-- 6. Horario (Franjas vespertinas y nocturnas entre 5:00 pm y 9:00 pm)
INSERT INTO horario (id, dia_semana, hora_inicio, hora_fin, activo) VALUES
(1, 1, '17:00:00', '18:00:00', TRUE),
(2, 1, '19:00:00', '20:00:00', TRUE),
(3, 1, '20:00:00', '21:00:00', TRUE),
(4, 2, '17:30:00', '18:30:00', TRUE),
(5, 2, '19:00:00', '20:00:00', TRUE),
(6, 2, '20:00:00', '21:00:00', TRUE),
(7, 3, '17:00:00', '18:00:00', TRUE),
(8, 3, '19:00:00', '20:00:00', TRUE),
(9, 3, '20:00:00', '21:00:00', TRUE),
(10, 4, '17:30:00', '18:30:00', TRUE),
(11, 4, '19:00:00', '20:00:00', TRUE),
(12, 4, '20:00:00', '21:00:00', TRUE),
(13, 5, '17:00:00', '18:00:00', TRUE),
(14, 5, '18:30:00', '19:30:00', TRUE),
(15, 5, '19:30:00', '20:30:00', TRUE);

-- 7. Sesion (Clases ejecutadas en Octubre 2026 entre 5:00 pm y 9:00 pm)
INSERT INTO sesion (id, horario_id, fecha, hora_inicio, hora_fin, profesor_usuario_id, cancelada, total_asistencias, total_recaudo) VALUES
(1, 1, '2026-10-05', '2026-10-05 17:00:00', '2026-10-05 18:00:00', 3, FALSE, 3, 17000.00),
(2, 2, '2026-10-05', '2026-10-05 19:00:00', '2026-10-05 20:00:00', 3, FALSE, 2, 9000.00),
(3, 3, '2026-10-05', '2026-10-05 20:00:00', '2026-10-05 21:00:00', 3, FALSE, 1, 5000.00),
(4, 4, '2026-10-06', '2026-10-06 17:30:00', '2026-10-06 18:30:00', 3, FALSE, 2, 9000.00),
(5, 5, '2026-10-06', '2026-10-06 19:00:00', '2026-10-06 20:00:00', 3, FALSE, 2, 12000.00),
(6, 6, '2026-10-06', '2026-10-06 20:00:00', '2026-10-06 21:00:00', 3, FALSE, 1, 5000.00),
(7, 7, '2026-10-07', '2026-10-07 17:00:00', '2026-10-07 18:00:00', 3, FALSE, 1, 5000.00),
(8, 8, '2026-10-07', '2026-10-07 19:00:00', '2026-10-07 20:00:00', 3, FALSE, 2, 12000.00),
(9, 9, '2026-10-07', '2026-10-07 20:00:00', '2026-10-07 21:00:00', 3, FALSE, 1, 4000.00),
(10, 10, '2026-10-08', '2026-10-08 17:30:00', '2026-10-08 18:30:00', 3, FALSE, 1, 5000.00),
(11, 11, '2026-10-08', '2026-10-08 19:00:00', '2026-10-08 20:00:00', 3, FALSE, 1, 5000.00),
(12, 12, '2026-10-08', '2026-10-08 20:00:00', '2026-10-08 21:00:00', 3, FALSE, 0, 0.00),
(13, 13, '2026-10-09', '2026-10-09 17:00:00', '2026-10-09 18:00:00', 3, FALSE, 0, 0.00),
(14, 14, '2026-10-09', '2026-10-09 18:30:00', '2026-10-09 19:30:00', 3, FALSE, 1, 7000.00),
(15, 15, '2026-10-09', '2026-10-09 19:30:00', '2026-10-09 20:30:00', 3, FALSE, 0, 0.00);

-- 8. Llegada (Check-in de alumnos en sesiones)
INSERT INTO llegada (id, persona_id, sesion_id, hora_llegada, metodo_checkin) VALUES
(1, 4, 1, '2026-10-05 16:50:00', 'QR'),
(2, 5, 1, '2026-10-05 16:52:00', 'DOCUMENTO'),
(3, 6, 1, '2026-10-05 16:55:00', 'MANUAL'),
(4, 7, 2, '2026-10-05 18:45:00', 'QR'),
(5, 8, 2, '2026-10-05 18:50:00', 'DOCUMENTO'),
(6, 9, 3, '2026-10-05 19:50:00', 'TERMINAL'),
(7, 4, 4, '2026-10-06 17:20:00', 'QR'),
(8, 5, 4, '2026-10-06 17:24:00', 'DOCUMENTO'),
(9, 6, 5, '2026-10-06 18:48:00', 'MANUAL'),
(10, 10, 5, '2026-10-06 18:52:00', 'QR'),
(11, 12, 6, '2026-10-06 19:55:00', 'DOCUMENTO'),
(12, 4, 7, '2026-10-07 16:50:00', 'QR'),
(13, 5, 8, '2026-10-07 18:45:00', 'DOCUMENTO'),
(14, 6, 8, '2026-10-07 18:52:00', 'QR'),
(15, 7, 9, '2026-10-07 19:53:00', 'TERMINAL'),
(16, 4, 10, '2026-10-08 17:22:00', 'QR'),
(17, 5, 11, '2026-10-08 18:49:00', 'MANUAL'),
(18, 6, 14, '2026-10-09 18:20:00', 'QR');

-- 9. Asistencia (Confirmacion y cobro de clase con tarifas de 4k, 5k y 7k)
INSERT INTO asistencia (id, llegada_id, monto_cobrado, marcado_por, hora_marcado) VALUES
(1, 1, 5000.00, 2, '2026-10-05 16:55:00'),
(2, 2, 5000.00, 2, '2026-10-05 16:56:00'),
(3, 3, 7000.00, 2, '2026-10-05 16:58:00'),
(4, 4, 4000.00, 2, '2026-10-05 18:50:00'),
(5, 5, 5000.00, 2, '2026-10-05 18:53:00'),
(6, 6, 5000.00, 2, '2026-10-05 19:55:00'),
(7, 7, 5000.00, 2, '2026-10-06 17:25:00'),
(8, 8, 4000.00, 2, '2026-10-06 17:27:00'),
(9, 9, 7000.00, 2, '2026-10-06 18:52:00'),
(10, 10, 5000.00, 2, '2026-10-06 18:55:00'),
(11, 11, 5000.00, 2, '2026-10-06 19:58:00'),
(12, 12, 5000.00, 2, '2026-10-07 16:53:00'),
(13, 13, 5000.00, 2, '2026-10-07 18:48:00'),
(14, 14, 7000.00, 2, '2026-10-07 18:55:00'),
(15, 15, 4000.00, 2, '2026-10-07 19:56:00'),
(16, 16, 5000.00, 2, '2026-10-08 17:25:00'),
(17, 17, 5000.00, 2, '2026-10-08 18:52:00'),
(18, 18, 7000.00, 2, '2026-10-09 18:25:00');

-- 10. Movimiento de cuenta (Abonos, pagos y devoluciones)
INSERT INTO movimiento_cuenta (id, persona_id, llegada_id, monto_recibido, monto_abonado, metodo_pago, tipo_movimiento, vuelto_entregado, fecha_hora, usuario_id, nota) VALUES
(1, 11, NULL, 50000.00, 50000.00, 'TRANSFERENCIA', 'ABONO', TRUE, '2026-09-15 18:30:00', 2, 'Abono anticipado paquete 10 clases'),
(2, 4, 1, 10000.00, 5000.00, 'EFECTIVO', 'ABONO', TRUE, '2026-10-05 16:55:00', 2, 'Pago sesion 1'),
(3, 5, 2, 5000.00, 5000.00, 'NEQUI', 'ABONO', TRUE, '2026-10-05 16:56:00', 2, 'Pago sesion 1'),
(4, 6, 3, 20000.00, 7000.00, 'DAVIPLATA', 'ABONO', TRUE, '2026-10-05 16:58:00', 2, 'Pago sesion especial'),
(5, 7, 4, 10000.00, 4000.00, 'EFECTIVO', 'ABONO', TRUE, '2026-10-05 18:50:00', 2, 'Pago sesion descuento'),
(6, 8, 5, 5000.00, 5000.00, 'NEQUI', 'ABONO', TRUE, '2026-10-05 18:53:00', 2, 'Pago sesion 2'),
(7, 9, 6, 10000.00, 5000.00, 'EFECTIVO', 'ABONO', TRUE, '2026-10-05 19:55:00', 2, 'Pago sesion 3'),
(8, 4, 7, 5000.00, 5000.00, 'NEQUI', 'ABONO', TRUE, '2026-10-06 17:25:00', 2, 'Pago sesion 4'),
(9, 5, 8, 5000.00, 4000.00, 'TRANSFERENCIA', 'ABONO', TRUE, '2026-10-06 17:27:00', 2, 'Pago sesion descuento'),
(10, 10, 10, 5000.00, 5000.00, 'EFECTIVO', 'ABONO', TRUE, '2026-10-06 18:55:00', 2, 'Pago sesion 5'),
(11, 12, 11, 15000.00, 5000.00, 'NEQUI', 'ABONO', TRUE, '2026-10-06 19:58:00', 2, 'Pago sesion 6'),
(12, 4, 12, 5000.00, 5000.00, 'EFECTIVO', 'ABONO', TRUE, '2026-10-07 16:53:00', 2, 'Pago sesion 7'),
(13, 5, 13, 5000.00, 5000.00, 'DAVIPLATA', 'ABONO', TRUE, '2026-10-07 18:48:00', 2, 'Pago sesion 8'),
(14, 4, 16, 5000.00, 5000.00, 'NEQUI', 'ABONO', TRUE, '2026-10-08 17:25:00', 2, 'Pago sesion 10'),
(15, 5, 17, 5000.00, 5000.00, 'EFECTIVO', 'ABONO', TRUE, '2026-10-08 18:52:00', 2, 'Pago sesion 11'),
(16, 7, NULL, 5000.00, 5000.00, 'EFECTIVO', 'REEMBOLSO', TRUE, '2026-10-08 19:30:00', 2, 'Devolucion por saldo a favor no usado');

-- 11. Tarifa (Precio vigente por sesion)
INSERT INTO tarifa (id, monto, actualizado_en, actualizado_por) VALUES
(1, 5000.00, '2026-08-01 18:00:00', 1);

-- 12. Historial de auditoria (Auditoria de eventos del sistema)
INSERT INTO historial_auditoria (id, tabla_afectada, registro_id, campo, valor_anterior, valor_nuevo, accion, usuario_id, fecha_hora) VALUES
(1, 'tarifa', 1, 'monto', '4500.00', '5000.00', 'UPDATE', 1, '2026-08-01 18:00:00'),
(2, 'persona', 4, 'telefono', '3000000000', '3004567890', 'UPDATE', 2, '2026-08-10 18:15:00'),
(3, 'persona', 8, 'observaciones_salud', NULL, 'Lesion rodilla recuperada', 'UPDATE', 2, '2026-09-05 18:30:00'),
(4, 'horario', 6, 'hora_fin', '20:50:00', '21:00:00', 'UPDATE', 1, '2026-09-10 17:00:00'),
(5, 'sesion', 3, 'profesor_usuario_id', NULL, '3', 'UPDATE', 1, '2026-10-04 18:00:00'),
(6, 'usuario', 11, 'activo', '0', '1', 'UPDATE', 1, '2026-09-15 18:40:00'),
(7, 'persona', 16, 'telefono', '3190000000', '3196789012', 'UPDATE', 2, '2026-09-28 17:45:00'),
(8, 'sesion', 6, 'hora_fin', NULL, '2026-10-06 21:00:00', 'UPDATE', 2, '2026-10-06 21:05:00'),
(9, 'persona', 14, 'apellidos', 'Ospina', 'Ospina Castillo', 'UPDATE', 2, '2026-09-22 17:10:00'),
(10, 'tarifa', 1, 'monto', '4000.00', '4500.00', 'UPDATE', 1, '2026-07-01 18:00:00'),
(11, 'horario', 2, 'hora_inicio', '18:50:00', '19:00:00', 'UPDATE', 1, '2026-09-01 17:00:00'),
(12, 'sesion', 1, 'total_asistencias', '2', '3', 'UPDATE', 2, '2026-10-05 18:05:00'),
(13, 'sesion', 5, 'total_recaudo', '5000.00', '10000.00', 'UPDATE', 2, '2026-10-06 20:05:00'),
(14, 'usuario', 5, 'correo', 'mateo@temp.com', 'mateo.ramos@gmail.com', 'UPDATE', 1, '2026-08-16 17:00:00'),
(15, 'persona', 11, 'observaciones_salud', NULL, 'Hipertension controlada', 'UPDATE', 2, '2026-09-15 18:35:00');
