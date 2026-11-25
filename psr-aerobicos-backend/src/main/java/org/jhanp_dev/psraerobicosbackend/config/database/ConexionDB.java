package org.jhanp_dev.psraerobicosbackend.config.database;

import org.jhanp_dev.psraerobicosbackend.shared.exception.DatabaseUnavailableException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConexionDB {

    private static final Properties CONFIGURACION = cargarConfiguracion();
    private static final String URL = resolver("db.url", "DB_URL", "jdbc:mysql://127.0.0.1:3306/psr_aerobicos");
    private static final String USER = resolver("db.user", "DB_USER", "root");
    private static final String PASSWORD = resolver("db.password", "DB_PASSWORD", "root");
    private static final ConexionDB INSTANCIA = new ConexionDB();

    private ConexionDB() {
    }

    public static ConexionDB getInstancia() {
        return INSTANCIA;
    }

    public Connection getConexion() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new DatabaseUnavailableException("No se pudo conectar con la base de datos.", e);
        }
    }

    private static Properties cargarConfiguracion() {
        Properties properties = new Properties();
        try (InputStream input = ConexionDB.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input != null) {
                properties.load(input);
            }
            return properties;
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static String resolver(String propiedad, String variableEntorno, String valorPorDefecto) {
        String valor = System.getProperty(propiedad);
        if (valor == null || valor.isBlank()) {
            valor = System.getenv(variableEntorno);
        }
        if (valor == null || valor.isBlank()) {
            valor = CONFIGURACION.getProperty(propiedad);
        }
        return valor == null || valor.isBlank() ? valorPorDefecto : valor.trim();
    }
}
