USE psr_aerobicos;

-- 1. Pagos anticipados:
-- Identifica abonos que superan el precio regular de una sesion ($5.000 COP)

SELECT 
    mc.id AS movimiento_id,
    p.numero_documento,
    CONCAT(p.nombre, ' ', p.apellidos) AS alumno,
    mc.monto_abonado,
    mc.metodo_pago,
    mc.fecha_hora
FROM movimiento_cuenta mc
INNER JOIN persona p ON p.id = mc.persona_id
WHERE mc.tipo_movimiento = 'ABONO'
  AND mc.monto_abonado > (
      SELECT monto 
      FROM tarifa 
      WHERE id = 1
  );

-- 2. Metodos de pago mas utilizados:
-- Resume cuanto entra por cada metodo (efectivo, Nequi, Daviplata, transferencia)

SELECT 
    mc.metodo_pago,
    COUNT(mc.id) AS total_pagos,
    SUM(mc.monto_abonado) AS total_recaudado,
    MAX(mc.monto_abonado) AS pago_mayor
FROM movimiento_cuenta mc
WHERE mc.tipo_movimiento = 'ABONO'
GROUP BY mc.metodo_pago
HAVING COUNT(mc.id) >= 2
ORDER BY total_recaudado DESC;

-- 3. Historial de pagos de un participante (estado de cuenta)
-- Muestra cada movimiento de un participante. Sirve para aclarar si una persona pago o no.

SELECT 
    mc.fecha_hora,
    p.numero_documento,
    CONCAT(p.nombre, ' ', p.apellidos) AS alumno,
    mc.tipo_movimiento,
    mc.monto_recibido,
    mc.monto_abonado,
    mc.metodo_pago,
    u.correo AS registrado_por
FROM movimiento_cuenta mc
INNER JOIN persona p ON p.id = mc.persona_id
INNER JOIN usuario u ON u.id = mc.usuario_id
WHERE p.numero_documento = '1102812004'
ORDER BY mc.fecha_hora DESC;

-- 4. Recaudo diario: cantidad de pagos y total recibido por dia
-- Responde cuanto se recaudo cada dia.

SELECT 
    DATE_FORMAT(mc.fecha_hora, '%d/%m/%Y') AS dia,
    COUNT(mc.id) AS total_pagos,
    SUM(mc.monto_abonado) AS recaudo_dia
FROM movimiento_cuenta mc
WHERE mc.tipo_movimiento = 'ABONO'
GROUP BY dia
ORDER BY MIN(mc.fecha_hora) DESC;
