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

        Tapa tapa = SelectorTapaDialog.seleccionarTapaParaLibro(libro.getTitulo());
        if (tapa == null) return;

        Boolean firmaSeleccionada = SelectorFirmaDialog.seleccionarFirmaParaLibro(libro.getTitulo());
        if (firmaSeleccionada == null) return;  // canceló

        boolean firmado = firmaSeleccionada;

        BigDecimal precioUnitario = new PrecioLibroRepository()
                .obtenerPrecioVigente(libro.getId(), tapa, firmado);

        if (precioUnitario == null) {
            AceptarDialog.mostrar(null, "Cotizar ítem — " + libro.getTitulo(), "No hay precio vigente para esa variante \n (" + tapa + (firmado ? ", firmado" : ", no firmado") + ").");
            return;
        }

        String textoCantidad = BuscadorDialog.pedirTexto(
                "Cotizar ítem — " + libro.getTitulo(),
                "Ingrese cantidad:"
        );
        if (textoCantidad == null) return;
        textoCantidad = textoCantidad.trim();
        if (textoCantidad.isEmpty()) {
            AceptarDialog.mostrar(null, "Cotizar ítem", "Tenés que ingresar una cantidad.");
            return;
        }

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

        MedioPago medioPago = SelectorMedioPagoDialog.seleccionarMedioPagoParaLibro(libro.getTitulo());
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

        boolean confirmar = ConfirmacionDialog.mostrarDialogo(
                "Confirmar venta",
                resumen,
                "Confirmar",
                "Volver"
        );
        if (!confirmar) return;

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
            AceptarDialog.mostrar(null, "Éxito" + libro.getTitulo(), "Venta registrada con éxito.\nID de venta: " + ventaId);
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
