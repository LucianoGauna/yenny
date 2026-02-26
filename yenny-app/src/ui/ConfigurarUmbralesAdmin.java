package ui;

import dll.StockRepository;
import domain.Libro;

import java.sql.SQLException;

public class ConfigurarUmbralesAdmin {
    public void mostrar(int sucursalPorDefecto) {
        Libro libro = new BuscadorLibros().seleccionarLibro();
        if (libro == null) return;

        Integer sucursalId = pedirSucursalId(sucursalPorDefecto);
        if (sucursalId == null) return;

        StockRepository repo = new StockRepository();
        Integer umbralActual = repo.obtenerUmbralActual(sucursalId, libro.getId());
        if (umbralActual == null) {
            AceptarDialog.mostrar(
                    null,
                    "Configurar umbral",
                    "No existe fila de stock para esa sucursal y libro.\nSucursal: " + sucursalId + " | Libro: " + libro.getId()
            );
            return;
        }

        Integer nuevoUmbral = pedirUmbralNuevo(libro.getTitulo(), umbralActual);
        if (nuevoUmbral == null) return;

        String resumen = """
                Sucursal: %d
                Libro: [%d] %s
                Umbral actual: %d
                Nuevo umbral: %d

                ¿Confirmar actualización?
                """.formatted(
                sucursalId,
                libro.getId(),
                libro.getTitulo(),
                umbralActual,
                nuevoUmbral
        );

        boolean confirmar = ConfirmacionDialog.mostrarDialogo(
                "Configurar umbral",
                resumen,
                "Confirmar",
                "Cancelar"
        );
        if (!confirmar) return;

        try {
            repo.actualizarUmbral(sucursalId, libro.getId(), nuevoUmbral);
            AceptarDialog.mostrar(null, "Configurar umbral", "Umbral actualizado correctamente.");
        } catch (SQLException e) {
            AceptarDialog.mostrar(null, "Error", "No se pudo actualizar el umbral.\n" + e.getMessage());
        }
    }

    private Integer pedirSucursalId(int sucursalPorDefecto) {
        String txt = BuscadorDialog.pedirTexto(
                "Configurar umbral",
                "ID de sucursal (vacío = " + sucursalPorDefecto + ")"
        );
        if (txt == null) return null;
        txt = txt.trim();
        if (txt.isEmpty()) return sucursalPorDefecto;

        try {
            int id = Integer.parseInt(txt);
            if (id <= 0) throw new NumberFormatException();
            return id;
        } catch (NumberFormatException e) {
            AceptarDialog.mostrar(null, "Configurar umbral", "El ID de sucursal debe ser un entero positivo.");
            return null;
        }
    }

    private Integer pedirUmbralNuevo(String tituloLibro, int umbralActual) {
        String txt = BuscadorDialog.pedirTexto(
                "Configurar umbral — " + tituloLibro,
                "Nuevo umbral (actual: " + umbralActual + ")"
        );
        if (txt == null) return null;
        txt = txt.trim();
        if (txt.isEmpty()) {
            AceptarDialog.mostrar(null, "Configurar umbral", "Debés ingresar un valor.");
            return null;
        }

        try {
            int umbral = Integer.parseInt(txt);
            if (umbral < 0) throw new NumberFormatException();
            return umbral;
        } catch (NumberFormatException e) {
            AceptarDialog.mostrar(null, "Configurar umbral", "El umbral debe ser un entero mayor o igual a 0.");
            return null;
        }
    }
}
