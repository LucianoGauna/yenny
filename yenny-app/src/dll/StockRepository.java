package dll;

import infra.Db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StockRepository {
    /** Devuelve líneas listas para mostrar. */
    public List<String> listarResumenPorSucursal(int sucursalId) {
        String sql = """
            SELECT l.titulo AS titulo, s.cantidad AS cantidad, s.umbral AS umbral
            FROM stock s
            JOIN libro l ON l.id = s.libro_id
            WHERE s.sucursal_id = ?
            ORDER BY l.titulo
        """;

        List<String> resultado = new ArrayList<>();

        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, sucursalId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String linea = rs.getString("titulo")
                            + " — cant: " + rs.getInt("cantidad")
                            + " (umbral: " + rs.getInt("umbral") + ")";
                    resultado.add(linea);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // TODO: Mejorar esto?
        }
        return resultado;
    }
}
