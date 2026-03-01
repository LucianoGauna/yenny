package ui;

import dll.PrecioLibroRepository;
import domain.Tapa;
import utils.Validaciones;

import java.math.BigDecimal;

public class ConsultaPrecioVigente {
    /* Pide libroId, tapa y firmado; muestra el precio vigente. */
    public void mostrar() {
        int libroId;
        while (true) {
            String textoId = BuscadorDialog.pedirTexto(
                    "Consultar precio vigente",
                    "Ingrese ID de libro:"
            );
            if (textoId == null) return;
            textoId = textoId.trim();
            if (textoId.isEmpty()) {
                AceptarDialog.mostrar(null, "Consultar precio vigente", "Debe ingresar un ID.");
                continue;
            }
            try {
                libroId = Validaciones.parseEnteroPositivo(textoId, "ID de libro");
                break;
            } catch (IllegalArgumentException e) {
                AceptarDialog.mostrar(null, "Consultar precio vigente", e.getMessage());
            }
        }

        Tapa tapa = SelectorTapaDialog.seleccionarTapaParaLibro("ID " + libroId);
        if (tapa == null) return;

        Boolean firmadoSeleccionado = SelectorFirmaDialog.seleccionarFirmaParaLibro("ID " + libroId);
        if (firmadoSeleccionado == null) return;
        boolean firmado = firmadoSeleccionado;

        BigDecimal precio = new PrecioLibroRepository().obtenerPrecioVigente(libroId, tapa, firmado);

        if (precio == null) {
            AceptarDialog.mostrar(
                    null,
                    "Consultar precio vigente",
                    "No hay precio vigente para esa variante."
            );
        } else {
            AceptarDialog.mostrar(
                    null,
                    "Consultar precio vigente",
                    "Precio vigente: $ " + precio.toPlainString()
            );
        }
    }
}
