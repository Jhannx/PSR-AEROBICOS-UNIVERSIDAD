import mysql.connector


class ConexionDB:
    instancia = None

    def __init__(self):
        self.conexion = None

    @classmethod
    def obtener_instancia(cls):
        if cls.instancia is None:
            cls.instancia = ConexionDB()
        return cls.instancia

    def obtener_conexion(self):
        if self.conexion is None or not self.conexion.is_connected():
            self.conexion = mysql.connector.connect(
                host="localhost",
                port=3306,
                user="root",
                password="root",
                database="psr_aerobicos",
                autocommit=True
            )
        return self.conexion
