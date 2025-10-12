package ui;

import dll.LibroRepository;
import domain.Libro;

import javax.swing.*;
import java.util.List;

public class BuscadorLibros {
    public Libro seleccionarLibro() {
        String texto = JOptionPane.showInputDialog(null, "Buscar por título, autor o editorial:",
                "Buscar libro", JOptionPane.QUESTION_MESSAGE);
        if (texto == null) return null;
        texto = texto.trim();
        if (texto.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Ingresá un texto para buscar.", "Buscar libro",
                    JOptionPane.INFORMATION_MESSAGE);
            return null;
        }

        List<Libro> libros = new LibroRepository().buscarActivosPorTexto(texto);
        if (libros.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No se encontraron libros.", "Buscar libro",
                    JOptionPane.INFORMATION_MESSAGE);
            return null;
        }

        OpcionLibro[] opciones = armarOpciones(libros);
        OpcionLibro elegido = (OpcionLibro) JOptionPane.showInputDialog(
                null, "Elegí un libro:", "Resultados de búsqueda",
                JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);
        return (elegido == null) ? null : elegido.libro();
    }

    public Integer seleccionarLibroId() {
        Libro libro = seleccionarLibro();
        return (libro == null) ? null : libro.getId();
    }

    private OpcionLibro[] armarOpciones(List<Libro> libros) {
        OpcionLibro[] opciones = new OpcionLibro[libros.size()];
        for (int i = 0; i < libros.size(); i++) {
            opciones[i] = new OpcionLibro(libros.get(i));
        }
        return opciones;
    }

    private static final class OpcionLibro {
        private final Libro libro;
        OpcionLibro(Libro libro) { this.libro = libro; }
        Libro libro() { return libro; }
        @Override public String toString() {
            return "[" + libro.getId() + "] " + libro.getTitulo() + " — "
                    + libro.getAutor() + " — " + libro.getEditorial() + " — "
                    + libro.getCategoria();
        }
    }
}
