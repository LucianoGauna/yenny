package ui;

import dll.PrecioLibroRepository;
import domain.Tapa;

import javax.swing.*;
import java.math.BigDecimal;

public class ConsultaPrecioVigente {
    /** Pide libroId, tapa y firmado; muestra el precio vigente. */
    public void mostrar() {
        String textoId = JOptionPane.showInputDialog(
                null, "Ingrese ID de libro:", "Consultar precio vigente",
                JOptionPane.QUESTION_MESSAGE
        );
        if (textoId == null) return; // cancelar
        textoId = textoId.trim();
        if (textoId.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Debe ingresar un ID.", "Consultar precio vigente", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int libroId;
        try {
            libroId = Integer.parseInt(textoId);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "El ID debe ser numérico.", "Consultar precio vigente", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Tapa tapa = (Tapa) JOptionPane.showInputDialog(
                null,
                "Elija la tapa:",
                "Consultar precio vigente",
                JOptionPane.QUESTION_MESSAGE,
                null,
                Tapa.values(),
                Tapa.BLANDA
        );
        if (tapa == null) return;

        int respFirmado = JOptionPane.showConfirmDialog(
                null, "¿Es un ejemplar firmado?", "Consultar precio vigente",
                JOptionPane.YES_NO_CANCEL_OPTION
        );
        if (respFirmado == JOptionPane.CANCEL_OPTION || respFirmado == JOptionPane.CLOSED_OPTION) return;
        boolean firmado = (respFirmado == JOptionPane.YES_OPTION);

        BigDecimal precio = new PrecioLibroRepository().obtenerPrecioVigente(libroId, tapa, firmado);

        if (precio == null) {
            JOptionPane.showMessageDialog(
                    null,
                    "No hay precio vigente para esa variante.",
                    "Consultar precio vigente",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } else {
            JOptionPane.showMessageDialog(
                    null,
                    "Precio vigente: $ " + precio.toPlainString(),
                    "Consultar precio vigente",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
}
