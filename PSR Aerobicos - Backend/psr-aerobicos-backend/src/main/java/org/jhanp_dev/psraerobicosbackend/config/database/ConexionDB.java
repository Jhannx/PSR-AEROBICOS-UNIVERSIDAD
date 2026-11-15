package org.jhanp_dev.psraerobicosbackend.config.database;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Singleton para gestionar la conexion JDBC pura a la base de datos MySQL (psr_aerobicos).
 */
public class ConexionDB {

    private static ConexionDB instancia;
    private Connection conexion;

    private String url;
    private String user;
    private String password;
    private String driver;

    private ConexionDB() {
        cargarConfiguracion();
    }

    private void cargarConfiguracion() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            Properties props = new Properties();
            if (input != null) {
                props.load(input);
            }

            this.url = props.getProperty(
                    "db.url",
                    "jdbc:mysql://localhost:3306/psr_aerobicos?useSSL=false&serverTimezone=America/Bogota&allowPublicKeyRetrieval=true"
            );
            this.user = props.getProperty("db.user", "root");
            this.password = props.getProperty("db.password", "root");
            this.driver = props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");

            Class.forName(this.driver);
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo inicializar la configuracion JDBC de ConexionDB", e);
        }
    }

    /**
     * Retorna la instancia unica del Singleton.
     */
    public static synchronized ConexionDB getInstance() {
        if (instancia == null) {
            instancia = new ConexionDB();
        }
        return instancia;
    }

    /**
     * Retorna una conexion activa a MySQL; si es nula, esta cerrada o invalida, la abre nuevamente.
     */
    public synchronized Connection getConnection() throws SQLException {
        if (conexion == null || conexion.isClosed() || !conexion.isValid(2)) {
            conexion = DriverManager.getConnection(url, user, password);
            conexion.setAutoCommit(true);
        }
        return conexion;
    }

    /**
     * Cierra la conexion compartida si se encuentra abierta.
     */
    public synchronized void closeConnection() {
        if (conexion != null) {
            try {
                if (!conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexion de base de datos: " + e.getMessage());
            } finally {
                conexion = null;
            }
        }
    }
}
