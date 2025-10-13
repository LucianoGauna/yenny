package dll;

import domain.Tapa;
import infra.Db;

import java.math.BigDecimal;
import java.sql.*;

public class PrecioLibroRepository {
    /**
     * Devuelve el precio vigente para un libro/variante (tapa + firmado).
     * Si no hay precio vigente, retorna null.
     */
    public BigDecimal obtenerPrecioVigente(int libroId, Tapa tapa, boolean firmado) {
        String sql = """
            SELECT precio
            FROM precio_libro
            WHERE libro_id = ?
              AND tapa = ?
              AND firmado = ?
              AND (vigente_hasta IS NULL OR vigente_hasta > NOW())
            ORDER BY vigente_desde DESC
            LIMIT 1
        """;

        try (Connection conexion = Db.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, libroId);
            ps.setString(2, tapa.name());     // 'DURA' o 'BLANDA'
            ps.setBoolean(3, firmado);        // true/false → 1/0

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("precio");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Mejorar esto?
        }
        return null;
    }
}
