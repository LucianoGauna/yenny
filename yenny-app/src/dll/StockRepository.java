package dll;

import domain.StockResumen;
import infra.Db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StockRepository {
    /* Devuelve líneas listas para mostrar. */
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

    /* Devuelve la cantidad disponible de un libro en una sucursal. Si no hay fila, devuelve 0. */
    public int obtenerCantidadDisponible(int sucursalId, int libroId) {
        String sql = """
            SELECT cantidad
            FROM stock
            WHERE sucursal_id = ? AND libro_id = ?
        """;

        try (Connection conexion = Db.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, sucursalId);
            ps.setInt(2, libroId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cantidad");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); /* Mejorar esto? */
        }
        return 0;
    }

    public List<StockResumen> listarResumenTabla(int sucursalId) {
        String sql = """
        SELECT l.titulo AS titulo, s.cantidad AS cantidad, s.umbral AS umbral
        FROM stock s
        JOIN libro l ON l.id = s.libro_id
        WHERE s.sucursal_id = ?
        ORDER BY l.titulo
    """;

        List<StockResumen> res = new ArrayList<>();
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, sucursalId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    res.add(new StockResumen(
                            rs.getString("titulo"),
                            rs.getInt("cantidad"),
                            rs.getInt("umbral")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error consultando stock de la sucursal " + sucursalId, e);
        }
        return res;
    }
}
