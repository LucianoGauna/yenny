package ui;

import dll.LibroRepository;

import javax.swing.*;
import java.util.List;

public class BuscadorLibros {
    /** Pide un texto, busca y muestra resultados en un JOptionPane. */
    public void mostrar() {
        String texto = JOptionPane.showInputDialog(
                null,
                "Buscar por título, autor o editorial:",
                "Buscar libro",
                JOptionPane.QUESTION_MESSAGE
        );

        if (texto == null) return; // cancelado
        texto = texto.trim();
        if (texto.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Ingresá un texto para buscar.", "Buscar libro", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        List<String> filas = new LibroRepository().buscarPorTexto(texto);

        if (filas.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No se encontraron libros.", "Buscar libro", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder("Resultados (" + filas.size() + ")\n\n");
        for (String f : filas) sb.append("• ").append(f).append("\n");

        JOptionPane.showMessageDialog(null, sb.toString(), "Buscar libro", JOptionPane.INFORMATION_MESSAGE);
    }
}
