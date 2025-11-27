package ui;

import dll.LibroRepository;
import domain.Libro;

import javax.swing.*;
import java.util.List;

public class BuscadorLibros {
    public Libro seleccionarLibro() {
        String texto = BuscadorDialog.pedirTexto(
                "Buscar libro",
                "Buscar por título, autor o editorial:"
        );
        if (texto == null) return null;
        texto = texto.trim();
        if (texto.isEmpty()) {
            AceptarDialog.mostrar(null, "Buscar libro", "Tenés que ingresar \n un texto para buscar");
            return null;
        }

        List<Libro> libros = new LibroRepository().buscarActivosPorTexto(texto);
        if (libros.isEmpty()) {
            AceptarDialog.mostrar(null, "Buscar libro", "No se encontraron libros");
            return null;
        }

        Libro elegido = ListaDialog.seleccionarLibroDeLista(
                "Resultados de búsqueda",
                libros
        );
        return elegido;
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
