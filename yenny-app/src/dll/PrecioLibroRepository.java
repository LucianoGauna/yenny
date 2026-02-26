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
            ps.setString(2, tapa.name());
            ps.setBoolean(3, firmado);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("precio");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); /* Mejorar esto? */
        }
        return null;
    }

    /**
     * Cierra el precio vigente (si existe), crea uno nuevo y registra historial en una transacción.
     */
    public void actualizarPrecioVigente(
            int libroId,
            Tapa tapa,
            boolean firmado,
            BigDecimal nuevoPrecio,
            int usuarioId,
            String motivo
    ) throws SQLException {
        final String SQL_SELECT_VIGENTE = """
            SELECT id, precio
            FROM precio_libro
            WHERE libro_id = ?
              AND tapa = ?
              AND firmado = ?
              AND (vigente_hasta IS NULL OR vigente_hasta > NOW())
            ORDER BY vigente_desde DESC
            LIMIT 1
        """;

        final String SQL_CERRAR_VIGENTE = """
            UPDATE precio_libro
            SET vigente_hasta = NOW()
            WHERE id = ?
        """;

        final String SQL_INSERT_NUEVO = """
            INSERT INTO precio_libro (libro_id, tapa, firmado, precio, vigente_desde, vigente_hasta)
            VALUES (?, ?, ?, ?, NOW(), NULL)
        """;

        final String SQL_INSERT_HISTORIAL = """
            INSERT INTO historial_precio (precio_libro_id, usuario_id, precio_anterior, precio_nuevo, motivo, fecha_hora)
            VALUES (?, ?, ?, ?, ?, NOW())
        """;

        try (Connection con = Db.getConnection()) {
            con.setAutoCommit(false);
            try {
                Integer precioVigenteId = null;
                BigDecimal precioAnterior = null;

                try (PreparedStatement ps = con.prepareStatement(SQL_SELECT_VIGENTE)) {
                    ps.setInt(1, libroId);
                    ps.setString(2, tapa.name());
                    ps.setBoolean(3, firmado);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            precioVigenteId = rs.getInt("id");
                            precioAnterior = rs.getBigDecimal("precio");
                        }
                    }
                }

                if (precioVigenteId != null) {
                    try (PreparedStatement ps = con.prepareStatement(SQL_CERRAR_VIGENTE)) {
                        ps.setInt(1, precioVigenteId);
                        ps.executeUpdate();
                    }
                }

                Integer nuevoPrecioId;
                try (PreparedStatement ps = con.prepareStatement(SQL_INSERT_NUEVO, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, libroId);
                    ps.setString(2, tapa.name());
                    ps.setBoolean(3, firmado);
                    ps.setBigDecimal(4, nuevoPrecio);
                    ps.executeUpdate();

                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) throw new SQLException("No se pudo obtener el ID del nuevo precio.");
                        nuevoPrecioId = keys.getInt(1);
                    }
                }

                try (PreparedStatement ps = con.prepareStatement(SQL_INSERT_HISTORIAL)) {
                    ps.setInt(1, nuevoPrecioId);
                    if (usuarioId <= 0) ps.setNull(2, Types.INTEGER); else ps.setInt(2, usuarioId);
                    if (precioAnterior == null) ps.setNull(3, Types.DECIMAL); else ps.setBigDecimal(3, precioAnterior);
                    ps.setBigDecimal(4, nuevoPrecio);
                    ps.setString(5, (motivo == null || motivo.isBlank()) ? "Actualización de precio" : motivo.trim());
                    ps.executeUpdate();
                }

                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                try {
                    con.setAutoCommit(true);
                } catch (SQLException ignore) {
                }
            }
        }
    }
}
