package ui;

import dll.StockRepository;

import javax.swing.*;
import java.util.List;

public class StockViewer {

    /** Muestra el stock de la sucursal. */
    public void mostrar(int sucursalId) {
        List<String> filas = new StockRepository().listarResumenPorSucursal(sucursalId);

        if (filas.isEmpty()) {
            JOptionPane.showMessageDialog(
                    null,
                    "No hay stock cargado para la sucursal " + sucursalId + ".",
                    "Ver stock",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Stock — Sucursal ").append(sucursalId).append("\n\n");
        for (String f : filas) {
            sb.append("• ").append(f).append("\n");
        }

        JOptionPane.showMessageDialog(
                null,
                sb.toString(),
                "Ver stock",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}
