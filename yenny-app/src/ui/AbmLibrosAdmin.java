package ui;

import dll.LibroRepository;
import domain.Categoria;
import domain.Libro;
import utils.Validaciones;

import javax.swing.*;
import java.sql.SQLException;
import java.util.List;

public class AbmLibrosAdmin {
    private static final int OPCION_CREAR = 0;
    private static final int OPCION_MODIFICAR = 1;
    private static final int OPCION_BAJA_LOGICA = 2;
    private static final int OPCION_REACTIVAR = 3;

    public void mostrar() {
        while (true) {
            Integer opcion = pedirOpcion();
            if (opcion == null) return;

            switch (opcion) {
                case OPCION_CREAR -> crearLibro();
                case OPCION_MODIFICAR -> modificarLibro();
                case OPCION_BAJA_LOGICA -> darDeBajaLibro();
                case OPCION_REACTIVAR -> reactivarLibro();
                default -> {
                    return;
                }
            }
        }
    }

    private Integer pedirOpcion() {
        String[] opciones = {"Crear libro", "Modificar libro", "Dar de baja", "Reactivar libro", "Volver"};
        int seleccion = JOptionPane.showOptionDialog(
                null,
                "Elegí una opción del ABM de libros",
                "ABM de libros",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]
        );
        if (seleccion < 0 || seleccion == 4) return null;
        return seleccion;
    }

    private void crearLibro() {
        LibroRepository repo = new LibroRepository();

        String titulo = pedirTextoObligatorio("Crear libro", "Título:");
        if (titulo == null) return;

        String autor = pedirTextoObligatorio("Crear libro", "Autor:");
        if (autor == null) return;

        String editorial = pedirTextoObligatorio("Crear libro", "Editorial:");
        if (editorial == null) return;

        Categoria categoria = pedirCategoria("Crear libro", null);
        if (categoria == null) return;

        if (repo.existeDuplicadoTituloAutor(titulo, autor)) {
            AceptarDialog.mostrar(
                    null,
                    "Crear libro",
                    "Ya existe un libro con el mismo título y autor."
            );
            return;
        }

        String resumen = """
                Título: %s
                Autor: %s
                Editorial: %s
                Categoría: %s

                ¿Confirmar alta?
                """.formatted(titulo, autor, editorial, categoria.name());

        boolean confirmar = ConfirmacionDialog.mostrarDialogo(
                "Confirmar alta de libro",
                resumen,
                "Crear",
                "Cancelar"
        );
        if (!confirmar) return;

        try {
            int id = repo.insertar(titulo, autor, editorial, categoria);
            AceptarDialog.mostrar(null, "ABM de libros", "Libro creado correctamente.\nID: " + id);
        } catch (SQLException e) {
            AceptarDialog.mostrar(null, "Error", "No se pudo crear el libro.\n" + e.getMessage());
        }
    }

    private void modificarLibro() {
        LibroRepository repo = new LibroRepository();
        Libro libro = seleccionarLibroPorTexto(true, "Modificar libro", "Buscar libro activo a modificar:");
        if (libro == null) return;

        String titulo = pedirTextoConDefault("Modificar libro", "Título:", libro.getTitulo());
        if (titulo == null) return;
        if (titulo.trim().isEmpty()) {
            AceptarDialog.mostrar(null, "Modificar libro", "El título es obligatorio.");
            return;
        }

        String autor = pedirTextoConDefault("Modificar libro", "Autor:", libro.getAutor());
        if (autor == null) return;
        if (autor.trim().isEmpty()) {
            AceptarDialog.mostrar(null, "Modificar libro", "El autor es obligatorio.");
            return;
        }

        String editorial = pedirTextoConDefault("Modificar libro", "Editorial:", libro.getEditorial());
        if (editorial == null) return;
        if (editorial.trim().isEmpty()) {
            AceptarDialog.mostrar(null, "Modificar libro", "La editorial es obligatoria.");
            return;
        }

        Categoria categoria = pedirCategoria("Modificar libro", libro.getCategoria());
        if (categoria == null) return;

        if (repo.existeDuplicadoTituloAutorExceptoId(titulo, autor, libro.getId())) {
            AceptarDialog.mostrar(
                    null,
                    "Modificar libro",
                    "Ya existe otro libro con el mismo título y autor."
            );
            return;
        }

        String resumen = """
                Libro ID: %d
                Título: %s
                Autor: %s
                Editorial: %s
                Categoría: %s

                ¿Confirmar cambios?
                """.formatted(libro.getId(), titulo.trim(), autor.trim(), editorial.trim(), categoria.name());

        boolean confirmar = ConfirmacionDialog.mostrarDialogo(
                "Confirmar modificación",
                resumen,
                "Guardar",
                "Cancelar"
        );
        if (!confirmar) return;

        try {
            repo.actualizar(libro.getId(), titulo, autor, editorial, categoria);
            AceptarDialog.mostrar(null, "ABM de libros", "Libro actualizado correctamente.");
        } catch (SQLException e) {
            AceptarDialog.mostrar(null, "Error", "No se pudo actualizar el libro.\n" + e.getMessage());
        }
    }

    private void darDeBajaLibro() {
        Libro libro = seleccionarLibroPorTexto(true, "Dar de baja libro", "Buscar libro activo a dar de baja:");
        if (libro == null) return;

        boolean confirmar = ConfirmacionDialog.mostrarDialogo(
                "Confirmar baja lógica",
                "¿Dar de baja el libro [" + libro.getId() + "] " + libro.getTitulo() + "?",
                "Dar de baja",
                "Cancelar"
        );
        if (!confirmar) return;

        try {
            new LibroRepository().actualizarActivo(libro.getId(), false);
            AceptarDialog.mostrar(null, "ABM de libros", "Libro dado de baja correctamente.");
        } catch (SQLException e) {
            AceptarDialog.mostrar(null, "Error", "No se pudo dar de baja el libro.\n" + e.getMessage());
        }
    }

    private void reactivarLibro() {
        Libro libro = seleccionarLibroPorTexto(false, "Reactivar libro", "Buscar libro inactivo a reactivar:");
        if (libro == null) return;

        boolean confirmar = ConfirmacionDialog.mostrarDialogo(
                "Confirmar reactivación",
                "¿Reactivar el libro [" + libro.getId() + "] " + libro.getTitulo() + "?",
                "Reactivar",
                "Cancelar"
        );
        if (!confirmar) return;

        try {
            new LibroRepository().actualizarActivo(libro.getId(), true);
            AceptarDialog.mostrar(null, "ABM de libros", "Libro reactivado correctamente.");
        } catch (SQLException e) {
            AceptarDialog.mostrar(null, "Error", "No se pudo reactivar el libro.\n" + e.getMessage());
        }
    }

    private Libro seleccionarLibroPorTexto(boolean activo, String titulo, String etiqueta) {
        String texto = BuscadorDialog.pedirTexto(titulo, etiqueta);
        if (texto == null) return null;
        texto = texto.trim();
        if (texto.isEmpty()) {
            AceptarDialog.mostrar(null, titulo, "Tenés que ingresar un texto para buscar.");
            return null;
        }

        LibroRepository repo = new LibroRepository();
        List<Libro> libros = activo ? repo.buscarActivosPorTexto(texto) : repo.buscarInactivosPorTexto(texto);
        if (libros.isEmpty()) {
            AceptarDialog.mostrar(null, titulo, "No se encontraron libros.");
            return null;
        }

        return ListaDialog.seleccionarLibroDeLista("Seleccionar libro", libros);
    }

    private String pedirTextoObligatorio(String titulo, String etiqueta) {
        String texto = BuscadorDialog.pedirTexto(titulo, etiqueta);
        if (texto == null) return null;
        texto = texto.trim();
        try {
            Validaciones.requireNoVacio(texto, etiqueta.replace(":", ""));
            return texto;
        } catch (IllegalArgumentException e) {
            AceptarDialog.mostrar(null, titulo, e.getMessage());
            return null;
        }
    }

    private String pedirTextoConDefault(String titulo, String etiqueta, String valorActual) {
        String texto = BuscadorDialog.pedirTexto(titulo, etiqueta + " (actual: " + valorActual + ")");
        if (texto == null) return null;
        texto = texto.trim();
        return texto.isEmpty() ? valorActual : texto;
    }

    private Categoria pedirCategoria(String titulo, Categoria actual) {
        return (Categoria) JOptionPane.showInputDialog(
                null,
                "Elegí la categoría:",
                titulo,
                JOptionPane.QUESTION_MESSAGE,
                null,
                Categoria.values(),
                actual != null ? actual : Categoria.FICCION
        );
    }
}
