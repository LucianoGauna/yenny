package dll;

import infra.Db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibroRepository {

    /* Busca libros activos cuyo título/autor/editorial contengan el texto */
    public List<String> buscarPorTexto(String texto) {
        String sql = """
            SELECT id, titulo, autor, categoria
            FROM libro
            WHERE activo = 1
              AND (titulo LIKE ? OR autor LIKE ? OR editorial LIKE ?)
            ORDER BY titulo
        """;

        List<String> resultado = new ArrayList<>();

        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String patron = "%" + texto + "%";
            ps.setString(1, patron);
            ps.setString(2, patron);
            ps.setString(3, patron);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String linea = "[" + rs.getInt("id") + "] "
                            + rs.getString("titulo") + " — "
                            + rs.getString("autor") + " — "
                            + rs.getString("categoria");
                    resultado.add(linea);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Mejorar esto?
        }
        return resultado;
    }
}
