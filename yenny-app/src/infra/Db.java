package infra;

import java.sql.*;

public final class Db {
    private static final String URL =
            "jdbc:mysql://localhost:3306/yenny?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "root92";

    private Db() {}

    public static Connection getConnection() throws SQLException {

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Falta el driver MySQL en el classpath", e);
            }


        return DriverManager.getConnection(URL, USER, PASS);
    }

    public static void ping() {
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT 1");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            System.out.println("DB OK (SELECT 1 = " + rs.getInt(1) + ")");
        } catch (SQLException e) {
            System.err.println("Error de conexión a la DB:");
            e.printStackTrace();
        }
    }
}
