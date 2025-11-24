package ui;

import dll.StockRepository;
import domain.StockResumen;

import javax.swing.*;
import java.util.List;

/** Muestra el stock de la sucursal. */
public class StockViewer {
    public void mostrar(int sucursalId) {
        List<StockResumen> filas = new StockRepository().listarResumenTabla(sucursalId);

        if (filas.isEmpty()) {
            JOptionPane.showMessageDialog(
                    null,
                    "No hay stock cargado para la sucursal " + sucursalId + ".",
                    "Ver stock",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        new StockDialog(null, sucursalId, filas).setVisible(true);
    }
}
