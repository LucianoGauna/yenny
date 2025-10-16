package ui;

import dll.PrecioLibroRepository;
import dll.StockRepository;
import dll.VentaRepository;
import domain.Libro;
import domain.MedioPago;
import domain.Tapa;
import utils.Validaciones;

import javax.swing.*;
import java.math.BigDecimal;
import java.sql.SQLException;

public class CotizadorItemVenta {

    /**
     * Flujo: seleccionar libro → variante → precio vigente → cantidad → validar stock → subtotal
     * + Confirmar → registrar venta (venta + ítem) y descontar stock en transacción.
     */
    public void mostrar(int sucursalId, int cajeroId) {
        Libro libro = new BuscadorLibros().seleccionarLibro();
        if (libro == null) return;

        Tapa tapa = (Tapa) JOptionPane.showInputDialog(
                null, "Elija la tapa:", "Cotizar ítem — " + libro.getTitulo(),
                JOptionPane.QUESTION_MESSAGE, null, Tapa.values(), Tapa.BLANDA
        );
        if (tapa == null) return;

        int respuestaFirmado = JOptionPane.showConfirmDialog(
                null, "¿Es un ejemplar firmado?", "Cotizar ítem — " + libro.getTitulo(),
                JOptionPane.YES_NO_CANCEL_OPTION
        );
        if (respuestaFirmado == JOptionPane.CANCEL_OPTION || respuestaFirmado == JOptionPane.CLOSED_OPTION) return;
        boolean firmado = (respuestaFirmado == JOptionPane.YES_OPTION);

        BigDecimal precioUnitario = new PrecioLibroRepository()
                .obtenerPrecioVigente(libro.getId(), tapa, firmado);

        if (precioUnitario == null) {
            JOptionPane.showMessageDialog(
                    null,
                    "No hay precio vigente para esa variante (" + tapa + (firmado ? ", firmado" : ", no firmado") + ").",
                    "Cotizar ítem — " + libro.getTitulo(),
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        String textoCantidad = JOptionPane.showInputDialog(
                null, "Ingrese cantidad:", "Cotizar ítem — " + libro.getTitulo(),
                JOptionPane.QUESTION_MESSAGE
        );
        if (textoCantidad == null) return;
        textoCantidad = textoCantidad.trim();

        int cantidad;
        try {
            cantidad = Validaciones.parseEnteroPositivo(textoCantidad, "Cantidad");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Cotizar ítem", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int disponible = new StockRepository().obtenerCantidadDisponible(sucursalId, libro.getId());
        try {
            Validaciones.validarDisponible(cantidad, disponible);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Cotizar ítem", JOptionPane.WARNING_MESSAGE);
            return;
        }

        BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));

        Integer clienteId = new SelectorCliente().seleccionarClienteId();
        String etiquetaCliente = (clienteId == null) ? "Sin cliente" : ("ID " + clienteId);

        MedioPago medioPago = (MedioPago) JOptionPane.showInputDialog(
                null,
                "Seleccione medio de pago:",
                "Confirmar venta",
                JOptionPane.QUESTION_MESSAGE,
                null,
                MedioPago.values(),
                MedioPago.EFECTIVO
        );
        if (medioPago == null) return;

        String resumen = """
            Cliente: %s
            Libro: %s
            Variante: %s%s
            Precio unitario: $ %s
            Cantidad: %d
            Subtotal: $ %s
            Medio de pago: %s

            ¿Confirmar venta?
            """.formatted(
                etiquetaCliente,
                libro.getTitulo(),
                tapa.name(),
                (firmado ? " — firmado" : " — no firmado"),
                precioUnitario.toPlainString(),
                cantidad,
                subtotal.toPlainString(),
                medioPago.name()
        );

        int confirmar = JOptionPane.showConfirmDialog(
                null, resumen, "Confirmar venta", JOptionPane.YES_NO_OPTION
        );
        if (confirmar != JOptionPane.YES_OPTION) return;

        try {
            Integer ventaId = new VentaRepository().registrarVentaSimple(
                    sucursalId,
                    cajeroId,
                    clienteId,
                    libro.getId(),
                    tapa,
                    firmado,
                    cantidad,
                    precioUnitario,
                    medioPago
            );

            JOptionPane.showMessageDialog(
                    null,
                    "Venta registrada con éxito.\nID de venta: " + ventaId,
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Error al registrar la venta:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
