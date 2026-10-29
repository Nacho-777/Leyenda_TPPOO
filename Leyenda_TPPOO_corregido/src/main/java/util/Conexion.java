package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Conexion {
    private static final String URL = "jdbc:mysql://localhost:3306/leyenda";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private Conexion() {
    }

    public static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
