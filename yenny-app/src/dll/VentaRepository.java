package dll;

import domain.MedioPago;
import domain.Tapa;
import infra.Db;

import java.math.BigDecimal;
import java.sql.*;

public class VentaRepository {
    /**
     * Registra una venta con 1 ítem y descuenta stock en una sola transacción.
     * Devuelve el ID autogenerado de la venta.
     */
    public Integer registrarVentaSimple(
            int sucursalId,
            int cajeroId,
            Integer clienteId,
            int libroId,
            Tapa tapa,
            boolean firmado,
            int cantidad,
            BigDecimal precioUnitario,
            MedioPago medioPago
    ) throws SQLException {

        final String SQL_INSERT_VENTA = """
            INSERT INTO venta (sucursal_id, cajero_id, cliente_id, fecha, total, medio_pago)
            VALUES (?, ?, ?, NOW(), ?, ?)
        """;

        final String SQL_INSERT_ITEM = """
            INSERT INTO venta_item (venta_id, libro_id, tapa, firmado, cantidad, precio_unitario_aplicado)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        final String SQL_DESCONTAR_STOCK = """
            UPDATE stock
               SET cantidad = cantidad - ?, updated_at = NOW()
             WHERE sucursal_id = ? AND libro_id = ? AND cantidad >= ?
        """;

        try (Connection con = Db.getConnection()) {
            con.setAutoCommit(false);
            try {
                BigDecimal total = precioUnitario.multiply(BigDecimal.valueOf(cantidad));

                Integer ventaId;
                try (PreparedStatement ps = con.prepareStatement(SQL_INSERT_VENTA, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, sucursalId);
                    ps.setInt(2, cajeroId);
                    if (clienteId == null) ps.setNull(3, Types.INTEGER); else ps.setInt(3, clienteId);
                    ps.setBigDecimal(4, total);
                    ps.setString(5, medioPago.name());
                    ps.executeUpdate();

                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) throw new SQLException("No se pudo obtener el ID de la venta.");
                        ventaId = keys.getInt(1);
                    }
                }

                try (PreparedStatement ps = con.prepareStatement(SQL_INSERT_ITEM)) {
                    ps.setInt(1, ventaId);
                    ps.setInt(2, libroId);
                    ps.setString(3, tapa.name());
                    ps.setBoolean(4, firmado);
                    ps.setInt(5, cantidad);
                    ps.setBigDecimal(6, precioUnitario);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = con.prepareStatement(SQL_DESCONTAR_STOCK)) {
                    ps.setInt(1, cantidad);
                    ps.setInt(2, sucursalId);
                    ps.setInt(3, libroId);
                    ps.setInt(4, cantidad);
                    int filas = ps.executeUpdate();
                    if (filas == 0) throw new SQLException("Stock insuficiente o inexistente para esa sucursal/libro.");
                }

                con.commit();
                return ventaId;

            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                try {
                    con.setAutoCommit(true);
                } catch (SQLException ignore) {}
            }
        }
    }
}
