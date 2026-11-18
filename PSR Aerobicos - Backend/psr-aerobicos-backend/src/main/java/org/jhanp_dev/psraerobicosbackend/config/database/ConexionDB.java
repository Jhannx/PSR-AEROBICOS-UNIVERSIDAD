package org.jhanp_dev.psraerobicosbackend.config.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    private static final String URL = "jdbc:mysql://127.0.0.1:3306/psr_aerobicos";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    public static Connection getConexion() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
