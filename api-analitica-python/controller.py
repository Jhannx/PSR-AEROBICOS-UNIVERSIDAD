from fastapi import APIRouter, Query, Response
from dao import AnaliticaDAO

router = APIRouter(prefix="/api")
dao = AnaliticaDAO()


# Consulta 1: Métodos de pago donde el abono promedio supera el promedio general de abonos
@router.get("/pagos/alto-recaudo", tags=["Pagos"], summary="Métodos de pago que superan el promedio general")
def metodos_pago_alto_recaudo(response: Response):
    try:
        datos = dao.obtener_metodos_pago_alto_recaudo()
        if not datos:
            response.status_code = 404
            return {"codigo": 404, "mensaje": "No se encontraron registros", "total_registros": 0, "datos": []}

        response.status_code = 200
        return {"codigo": 200, "mensaje": "OK", "total_registros": len(datos), "datos": datos}
    except Exception as e:
        response.status_code = 500
        return {"codigo": 500, "mensaje": f"Error interno del servidor: {str(e)}", "total_registros": 0, "datos": []}


# Consulta 2: Alumnos que han ido a más clases que el promedio general de la academia
@router.get("/alumnos/destacados", tags=["Alumnos"], summary="Alumnos con más asistencias que el promedio")
def alumnos_destacados(response: Response):
    try:
        datos = dao.obtener_alumnos_destacados()
        if not datos:
            response.status_code = 404
            return {"codigo": 404, "mensaje": "No se encontraron registros", "total_registros": 0, "datos": []}

        response.status_code = 200
        return {"codigo": 200, "mensaje": "OK", "total_registros": len(datos), "datos": datos}
    except Exception as e:
        response.status_code = 500
        return {"codigo": 500, "mensaje": f"Error interno del servidor: {str(e)}", "total_registros": 0, "datos": []}


# Consulta 3: Clases específicas que tuvieron más alumnos que el promedio de su mismo día
@router.get("/sesiones/destacadas-por-dia", tags=["Sesiones"], summary="Clases que superaron el promedio de su propio día")
def sesiones_destacadas_por_dia(response: Response):
    try:
        datos = dao.obtener_sesiones_destacadas_por_dia()
        if not datos:
            response.status_code = 404
            return {"codigo": 404, "mensaje": "No se encontraron registros", "total_registros": 0, "datos": []}

        response.status_code = 200
        return {"codigo": 200, "mensaje": "OK", "total_registros": len(datos), "datos": datos}
    except Exception as e:
        response.status_code = 500
        return {"codigo": 500, "mensaje": f"Error interno del servidor: {str(e)}", "total_registros": 0, "datos": []}


# Consulta 4: Alumnos y dinero por turno, descartando clases canceladas o reembolsos
@router.get("/turnos/sin-cancelaciones", tags=["Turnos"], summary="Asistencias y dinero por turno sin clases canceladas")
def turnos_sin_cancelaciones(response: Response):
    try:
        datos = dao.obtener_turnos_sin_cancelaciones()
        if not datos:
            response.status_code = 404
            return {"codigo": 404, "mensaje": "No se encontraron registros", "total_registros": 0, "datos": []}

        response.status_code = 200
        return {"codigo": 200, "mensaje": "OK", "total_registros": len(datos), "datos": datos}
    except Exception as e:
        response.status_code = 500
        return {"codigo": 500, "mensaje": f"Error interno del servidor: {str(e)}", "total_registros": 0, "datos": []}


# Consulta 5: Métodos de pago filtrados por mínimo de operaciones (?min_operaciones=N) y tarifa base
@router.get("/pagos/auditoria-metodos", tags=["Pagos"], summary="Auditoría de métodos de pago por volumen mínimo")
def auditoria_metodos_pago(response: Response, min_operaciones: int = Query(default=2, ge=1)):
    try:
        datos = dao.obtener_auditoria_metodos_pago(min_operaciones)
        if not datos:
            response.status_code = 404
            return {"codigo": 404, "mensaje": "No se encontraron registros", "total_registros": 0, "datos": []}

        response.status_code = 200
        return {"codigo": 200, "mensaje": "OK", "total_registros": len(datos), "datos": datos}
    except Exception as e:
        response.status_code = 500
        return {"codigo": 500, "mensaje": f"Error interno del servidor: {str(e)}", "total_registros": 0, "datos": []}
