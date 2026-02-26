package ui;

import dll.PrecioLibroRepository;
import domain.Libro;
import domain.Tapa;

import java.math.BigDecimal;
import java.sql.SQLException;

public class CambiarPreciosAdmin {
    public void mostrar(int adminId) {
        Libro libro = new BuscadorLibros().seleccionarLibro();
        if (libro == null) return;

        Tapa tapa = SelectorTapaDialog.seleccionarTapaParaLibro(libro.getTitulo());
        if (tapa == null) return;

        Boolean firmaSeleccionada = SelectorFirmaDialog.seleccionarFirmaParaLibro(libro.getTitulo());
        if (firmaSeleccionada == null) return;
        boolean firmado = firmaSeleccionada;

        PrecioLibroRepository repo = new PrecioLibroRepository();
        BigDecimal precioActual = repo.obtenerPrecioVigente(libro.getId(), tapa, firmado);

        String textoNuevoPrecio = BuscadorDialog.pedirTexto(
                "Cambiar precio",
                "Ingresá el nuevo precio para " + libro.getTitulo() + ":"
        );
        if (textoNuevoPrecio == null) return;

        BigDecimal nuevoPrecio;
        try {
            nuevoPrecio = parsePrecioPositivo(textoNuevoPrecio);
        } catch (IllegalArgumentException ex) {
            AceptarDialog.mostrar(null, "Cambiar precio", ex.getMessage());
            return;
        }

        if (precioActual != null && precioActual.compareTo(nuevoPrecio) == 0) {
            AceptarDialog.mostrar(null, "Cambiar precio", "El nuevo precio es igual al precio vigente.");
            return;
        }

        String motivo = BuscadorDialog.pedirTexto(
                "Cambiar precio",
                "Motivo del cambio (opcional):"
        );
        if (motivo == null) return;
        if (motivo.trim().isEmpty()) motivo = "Actualización de precio";

        String resumen = """
                Libro: %s
                Variante: %s%s
                Precio actual: %s
                Nuevo precio: $ %s
                Motivo: %s

                ¿Confirmar actualización?
                """.formatted(
                libro.getTitulo(),
                tapa.name(),
                (firmado ? " — firmado" : " — no firmado"),
                (precioActual == null ? "Sin precio vigente" : "$ " + precioActual.toPlainString()),
                nuevoPrecio.toPlainString(),
                motivo
        );

        boolean confirmar = ConfirmacionDialog.mostrarDialogo(
                "Confirmar cambio de precio",
                resumen,
                "Confirmar",
                "Cancelar"
        );
        if (!confirmar) return;

        try {
            repo.actualizarPrecioVigente(libro.getId(), tapa, firmado, nuevoPrecio, adminId, motivo);
            AceptarDialog.mostrar(null, "Cambiar precio", "Precio actualizado correctamente.");
        } catch (SQLException e) {
            AceptarDialog.mostrar(null, "Error", "No se pudo actualizar el precio.\n" + e.getMessage());
        }
    }

    private BigDecimal parsePrecioPositivo(String valor) {
        if (valor == null) throw new IllegalArgumentException("El precio es obligatorio.");
        String normalizado = valor.trim().replace(",", ".");
        if (normalizado.isEmpty()) throw new IllegalArgumentException("El precio es obligatorio.");
        try {
            BigDecimal precio = new BigDecimal(normalizado);
            if (precio.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("El precio debe ser mayor a 0.");
            }
            return precio;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Formato de precio inválido.");
        }
    }
}
