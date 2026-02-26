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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
            AceptarDialog.mostrar(
                    null,
                    "Cotizar ítem — " + libro.getTitulo(),
                    "No hay precio vigente para esa variante \n(" + tapa + (firmado ? ", firmado" : ", no firmado") + ")."
            );
            return;
        }

        int disponible = new StockRepository().obtenerCantidadDisponible(sucursalId, libro.getId());
        int cantidad;

        while (true) {
            String textoCantidad = BuscadorDialog.pedirTexto(
                    "Cotizar ítem — " + libro.getTitulo(),
                    "Ingrese cantidad:"
            );
            if (textoCantidad == null) {
                return;
            }

            textoCantidad = textoCantidad.trim();
            if (textoCantidad.isEmpty()) {
                AceptarDialog.mostrar(null, "Cotizar ítem", "Tenés que ingresar una cantidad.");
                continue;
            }

            try {
                cantidad = Validaciones.parseEnteroPositivo(textoCantidad, "Cantidad");
            } catch (IllegalArgumentException ex) {
                AceptarDialog.mostrar(null, "Cotizar ítem", ex.getMessage());
                continue;
            }

            try {
                Validaciones.validarDisponible(cantidad, disponible);
                break;
            } catch (IllegalArgumentException ex) {
                AceptarDialog.mostrar(
                        null,
                        "Stock insuficiente",
                        ex.getMessage() + "\nPor favor ingresá una cantidad menor o igual a " + disponible + "."
                );
            }
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
            String comprobante = construirComprobante(
                    ventaId,
                    sucursalId,
                    cajeroId,
                    etiquetaCliente,
                    libro,
                    tapa,
                    firmado,
                    cantidad,
                    precioUnitario,
                    subtotal,
                    medioPago
            );

            AceptarDialog.mostrar(null, "Venta registrada", "Venta registrada con éxito.\nID de venta: " + ventaId);
            AceptarDialog.mostrar(null, "Comprobante", comprobante);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Error al registrar la venta:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private String construirComprobante(
            Integer ventaId,
            int sucursalId,
            int cajeroId,
            String etiquetaCliente,
            Libro libro,
            Tapa tapa,
            boolean firmado,
            int cantidad,
            BigDecimal precioUnitario,
            BigDecimal subtotal,
            MedioPago medioPago
    ) {
        String fechaHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        return """
                LIBRERIA YENNY
                COMPROBANTE DE VENTA

                Venta ID: %d
                Fecha: %s
                Sucursal: %d
                Cajero: %d
                Cliente: %s

                Libro: [%d] %s
                Variante: %s%s
                Cantidad: %d
                Precio unitario: $ %s
                Subtotal: $ %s
                Medio de pago: %s
                """.formatted(
                ventaId,
                fechaHora,
                sucursalId,
                cajeroId,
                etiquetaCliente,
                libro.getId(),
                libro.getTitulo(),
                tapa.name(),
                (firmado ? " — firmado" : " — no firmado"),
                cantidad,
                precioUnitario.toPlainString(),
                subtotal.toPlainString(),
                medioPago.name()
        );
    }

}
