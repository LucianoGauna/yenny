package ui;

import dll.PrecioLibroRepository;
import dll.StockRepository;
import domain.Libro;
import domain.Tapa;

import javax.swing.*;
import java.math.BigDecimal;

public class CotizadorItemVenta {

    /** Flujo: seleccionar libro → variante → precio vigente → cantidad → validar stock → subtotal. */
    public void mostrar(int sucursalId) {
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
            cantidad = Integer.parseInt(textoCantidad);
            if (cantidad <= 0) throw new NumberFormatException("Cantidad no positiva");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    null, "La cantidad debe ser un entero positivo.", "Cotizar ítem",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        int disponible = new StockRepository().obtenerCantidadDisponible(sucursalId, libro.getId());
        if (cantidad > disponible) {
            JOptionPane.showMessageDialog(
                    null,
                    "Stock insuficiente.\nDisponible en sucursal: " + disponible + " unidad(es).",
                    "Cotizar ítem — " + libro.getTitulo(),
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));

        String resumen = """
                Libro: %s
                Variante: %s%s
                Precio unitario: $ %s
                Cantidad: %d
                Stock disponible: %d
                Subtotal: $ %s
                """.formatted(
                libro.getTitulo(),
                tapa.name(),
                (firmado ? " — firmado" : " — no firmado"),
                precioUnitario.toPlainString(),
                cantidad,
                disponible,
                subtotal.toPlainString()
        );

        JOptionPane.showMessageDialog(null, resumen, "Cotización", JOptionPane.INFORMATION_MESSAGE);

        // Próximo paso (siguiente micro): grabar venta + ítem + descontar stock (en transacción).
    }
}
