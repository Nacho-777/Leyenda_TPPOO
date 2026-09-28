package util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class Conexion {
    private static final String URL = "jdbc:mysql://localhost:3307/leyenda";
    private static final String User = "root";
    private static final String Password = "";

    public static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(URL, User, Password);
    }
}
