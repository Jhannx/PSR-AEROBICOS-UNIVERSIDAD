USE psr_aerobicos;

-- Sincroniza las claves BCrypt de las cuentas semilla con 02_dml_15_registros.sql.
-- La clave de demostración de cada cuenta es su número de documento.
UPDATE usuario SET password_hash = '$2a$10$JxSGuCsSLjk2cLwyuUY6nO4lp3Xo6eQQWVBzsykHixZ4fa2GHnXpq' WHERE id = 1 AND numero_documento = '1102812001';
UPDATE usuario SET password_hash = '$2a$10$bjCc70/q/XgdQbs/qUNW3.EXn8osQQ6pfEls5ZKfiPgI1MZIplzPW' WHERE id = 2 AND numero_documento = '1102812002';
UPDATE usuario SET password_hash = '$2a$10$fr3nT01cpDiZ9gXUD2H2yutUYzFRxfi5p.9NCgrIInAIHbGCrIBny' WHERE id = 3 AND numero_documento = '1102812003';
UPDATE usuario SET password_hash = '$2a$10$.jCh8NW47FJxK9FECFFe4OyA6m9zV53dtfAWsxyC0Ub8yjP6.l.AG' WHERE id = 4 AND numero_documento = '1102812004';
UPDATE usuario SET password_hash = '$2a$10$2/JzW20xBYexVJak7ZhxqubrmqKYr3jQY5V0s4jKYlmg1Yb4p0GqG' WHERE id = 5 AND numero_documento = '1102812005';
UPDATE usuario SET password_hash = '$2a$10$HFbpWua5kTDuCJHyvTnhi.aV3GLiOeFRDvNM98aCah25b8kHrn85i' WHERE id = 6 AND numero_documento = '1102812006';
UPDATE usuario SET password_hash = '$2a$10$g6I50VyzSOUmXS.mlbE33uSjCYuSO.gcNf1cTmacloLyMkr.NP4Hu' WHERE id = 7 AND numero_documento = '1102812007';
UPDATE usuario SET password_hash = '$2a$10$Mk4O5pcDSBIgqizcG.e.8.q5BUiUYYxx8b3HGvzJUq4ZUfjcEmM4C' WHERE id = 8 AND numero_documento = '1102812008';
UPDATE usuario SET password_hash = '$2a$10$7CwUwnBDIWl.0VcB1Sg9KOitNGDjvUziZe2DdoJEGYOGU.YIcgW/q' WHERE id = 9 AND numero_documento = '1102812009';
UPDATE usuario SET password_hash = '$2a$10$cRlp7Eq6GWfpav8TwAjIWe8fVRBHScuEMrQ/RwErSU14L57UcaPGW' WHERE id = 10 AND numero_documento = '1102812010';
UPDATE usuario SET password_hash = '$2a$10$7nQaqPizisIaIa1GHHo77ujmsArg5lGf9GMrJncX6WOtsD/sW4SwG' WHERE id = 11 AND numero_documento = '1102812011';
UPDATE usuario SET password_hash = '$2a$10$G4ab8FhwGIjvE6eq0GYQFuMPbi5ckrHXeN1HeoXTVrc1ZuDuQAwn6' WHERE id = 12 AND numero_documento = '1102812012';
UPDATE usuario SET password_hash = '$2a$10$bAhTBA6Jq9PGvihIkGE5qODZ1Inexo3BGjomy2QgEqFIK7.hP7Tpy' WHERE id = 13 AND numero_documento = '1067912013';
UPDATE usuario SET password_hash = '$2a$10$69aZGIQfciE1jR3HTAc3beoqr7KHMkdWh30KL/Ndz0dR2.LAOJ4zm' WHERE id = 14 AND numero_documento = '1102812014';
UPDATE usuario SET password_hash = '$2a$10$bLYpeSAR0wPGP42oiN.D2uy7Lc0Wj05KcRVYTmeVmR8.ZvRBJGuYe' WHERE id = 15 AND numero_documento = '1102812015';
