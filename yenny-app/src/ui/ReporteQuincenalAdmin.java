package ui;

import dll.VentaRepository;
import domain.ReporteLibroVentas;
import domain.ReporteQuincenalResumen;

import java.math.BigDecimal;
import java.util.List;

public class ReporteQuincenalAdmin {
    private static final int CANCELADO = Integer.MIN_VALUE;

    public void mostrar(int sucursalAdmin) {
        Integer sucursalFiltro = pedirSucursalFiltro(sucursalAdmin);
        if (sucursalFiltro != null && sucursalFiltro == CANCELADO) return;

        VentaRepository repo = new VentaRepository();
        ReporteQuincenalResumen resumen = repo.obtenerResumenQuincenal(sucursalFiltro);
        List<ReporteLibroVentas> top = repo.obtenerTopLibrosQuincenal(sucursalFiltro, 5);

        String mensaje = construirMensaje(resumen, top, sucursalFiltro);
        AceptarDialog.mostrar(null, "Reporte quincenal", mensaje);
    }

    private Integer pedirSucursalFiltro(int sucursalAdmin) {
        String texto = BuscadorDialog.pedirTexto(
                "Reporte quincenal",
                "ID sucursal (vacío = sucursal " + sucursalAdmin + ", 0 = todas)"
        );
        if (texto == null) return CANCELADO;
        texto = texto.trim();
        if (texto.isEmpty()) return sucursalAdmin;

        try {
            int valor = Integer.parseInt(texto);
            if (valor < 0) throw new NumberFormatException();
            return valor == 0 ? null : valor;
        } catch (NumberFormatException e) {
            AceptarDialog.mostrar(null, "Reporte quincenal", "Sucursal inválida. Debe ser entero >= 0.");
            return CANCELADO;
        }
    }

    private String construirMensaje(ReporteQuincenalResumen r, List<ReporteLibroVentas> top, Integer sucursalFiltro) {
        String scope = (sucursalFiltro == null) ? "Todas las sucursales" : ("Sucursal " + sucursalFiltro);
        StringBuilder sb = new StringBuilder();
        sb.append("Período: ").append(r.fechaDesde()).append(" a ").append(r.fechaHasta()).append("\n");
        sb.append("Ámbito: ").append(scope).append("\n\n");
        sb.append("Ventas: ").append(r.cantidadVentas()).append("\n");
        sb.append("Unidades vendidas: ").append(r.unidadesVendidas()).append("\n");
        sb.append("Total recaudado: $ ").append(formatMoneda(r.totalRecaudado())).append("\n");
        sb.append("Ticket promedio: $ ").append(formatMoneda(r.ticketPromedio())).append("\n\n");
        sb.append("Top 5 libros por unidades:\n");

        if (top.isEmpty()) {
            sb.append("Sin ventas en el período.");
        } else {
            int pos = 1;
            for (ReporteLibroVentas fila : top) {
                sb.append(pos++)
                        .append(". ")
                        .append(fila.titulo())
                        .append(" — ")
                        .append(fila.unidadesVendidas())
                        .append(" u. — $ ")
                        .append(formatMoneda(fila.montoVendido()))
                        .append("\n");
            }
        }
        return sb.toString().trim();
    }

    private String formatMoneda(BigDecimal valor) {
        return (valor == null ? BigDecimal.ZERO : valor).setScale(2, java.math.RoundingMode.HALF_UP).toPlainString();
    }
}
