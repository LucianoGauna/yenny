package dll;

import domain.MedioPago;
import domain.ReporteLibroVentas;
import domain.ReporteQuincenalResumen;
import domain.Tapa;
import infra.Db;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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

    public ReporteQuincenalResumen obtenerResumenQuincenal(Integer sucursalId) {
        LocalDate hasta = LocalDate.now();
        LocalDate desde = hasta.minusDays(14);
        Timestamp tsDesde = Timestamp.valueOf(desde.atStartOfDay());
        Timestamp tsHastaExclusivo = Timestamp.valueOf(hasta.plusDays(1).atStartOfDay());

        String sqlVentas = """
            SELECT COUNT(*) AS cant_ventas,
                   COALESCE(SUM(total), 0) AS total_recaudado,
                   COALESCE(AVG(total), 0) AS ticket_promedio
            FROM venta
            WHERE fecha >= ?
              AND fecha < ?
        """ + (sucursalId != null ? " AND sucursal_id = ?" : "");

        String sqlUnidades = """
            SELECT COALESCE(SUM(vi.cantidad), 0) AS unidades
            FROM venta_item vi
            JOIN venta v ON v.id = vi.venta_id
            WHERE v.fecha >= ?
              AND v.fecha < ?
        """ + (sucursalId != null ? " AND v.sucursal_id = ?" : "");

        int cantidadVentas = 0;
        BigDecimal totalRecaudado = BigDecimal.ZERO;
        BigDecimal ticketPromedio = BigDecimal.ZERO;
        int unidadesVendidas = 0;

        try (Connection con = Db.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(sqlVentas)) {
                ps.setTimestamp(1, tsDesde);
                ps.setTimestamp(2, tsHastaExclusivo);
                if (sucursalId != null) ps.setInt(3, sucursalId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        cantidadVentas = rs.getInt("cant_ventas");
                        totalRecaudado = rs.getBigDecimal("total_recaudado");
                        ticketPromedio = rs.getBigDecimal("ticket_promedio");
                    }
                }
            }

            try (PreparedStatement ps = con.prepareStatement(sqlUnidades)) {
                ps.setTimestamp(1, tsDesde);
                ps.setTimestamp(2, tsHastaExclusivo);
                if (sucursalId != null) ps.setInt(3, sucursalId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        unidadesVendidas = rs.getInt("unidades");
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error generando resumen quincenal.", e);
        }

        return new ReporteQuincenalResumen(
                desde,
                hasta,
                cantidadVentas,
                unidadesVendidas,
                totalRecaudado == null ? BigDecimal.ZERO : totalRecaudado,
                ticketPromedio == null ? BigDecimal.ZERO : ticketPromedio
        );
    }

    public List<ReporteLibroVentas> obtenerTopLibrosQuincenal(Integer sucursalId, int limite) {
        LocalDate hasta = LocalDate.now();
        LocalDate desde = hasta.minusDays(14);
        Timestamp tsDesde = Timestamp.valueOf(desde.atStartOfDay());
        Timestamp tsHastaExclusivo = Timestamp.valueOf(hasta.plusDays(1).atStartOfDay());

        String sql = """
            SELECT l.titulo AS titulo,
                   COALESCE(SUM(vi.cantidad), 0) AS unidades_vendidas,
                   COALESCE(SUM(vi.cantidad * vi.precio_unitario_aplicado), 0) AS monto_vendido
            FROM venta_item vi
            JOIN venta v ON v.id = vi.venta_id
            JOIN libro l ON l.id = vi.libro_id
            WHERE v.fecha >= ?
              AND v.fecha < ?
        """ + (sucursalId != null ? " AND v.sucursal_id = ? " : " ")
                + """
            GROUP BY l.id, l.titulo
            ORDER BY unidades_vendidas DESC, monto_vendido DESC, l.titulo ASC
            LIMIT ?
        """;

        List<ReporteLibroVentas> top = new ArrayList<>();

        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setTimestamp(1, tsDesde);
            ps.setTimestamp(2, tsHastaExclusivo);
            int idx = 3;
            if (sucursalId != null) {
                ps.setInt(idx++, sucursalId);
            }
            ps.setInt(idx, limite);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    top.add(new ReporteLibroVentas(
                            rs.getString("titulo"),
                            rs.getInt("unidades_vendidas"),
                            rs.getBigDecimal("monto_vendido")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error obteniendo top de libros quincenal.", e);
        }

        return top;
    }
}
