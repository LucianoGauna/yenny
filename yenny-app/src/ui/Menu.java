package ui;

import domain.*;

import javax.swing.*;

public class Menu {
    public boolean mostrar(Usuario usuario) {
        if (usuario == null) return false;

        return (usuario.getRol() == Rol.ADMIN)
                ? mostrarMenuAdmin(usuario)
                : mostrarMenuCajero(usuario);
    }

    private boolean mostrarMenuAdmin(Usuario usuario) {
        while (true) {
            MenuAdminDialog dlg = new MenuAdminDialog(null, usuario);
            ResultadoMenuAdmin r = dlg.showDialog();

            switch (r) {
                case CERRAR_SESION:
                case CERRADO_VENTANA:
                    AceptarDialog.mostrar(null, "Información", "Sesión cerrada.");
                    return false;
                case SALIR_SISTEMA:
                    boolean quiereSalir = ConfirmacionDialog.mostrarDialogo(
                            "Confirmación",
                            "¿Seguro que desea salir del sistema?",
                            "Sí, salir",
                            "No, quedarse"
                    );

                    if (quiereSalir) return true;
                    break;

                case ABM_LIBROS:           mostrarPendiente("ABM de libros"); break;
                case CAMBIAR_PRECIOS:
                    new CambiarPreciosAdmin().mostrar(usuario.getId());
                    break;
                case CONFIGURAR_UMBRALES:
                    new ConfigurarUmbralesAdmin().mostrar(usuario.getSucursalId());
                    break;
                case REPORTE_QUINCENAL:    mostrarPendiente("Reporte quincenal"); break;
            }
        }
    }

    private boolean mostrarMenuCajero(Usuario usuario) {
        while (true) {
            MenuCajeroDialog dlg = new MenuCajeroDialog(null, usuario);
            ResultadoMenuCajero r = dlg.showDialog();

            switch (r) {
                case CERRAR_SESION:
                case CERRADO_VENTANA:
                    AceptarDialog.mostrar(null, "Información", "Sesión cerrada.");
                    return false;

                case SALIR_SISTEMA:
                    boolean quiereSalir = ConfirmacionDialog.mostrarDialogo(
                            "Confirmación",
                            "¿Seguro que desea salir del sistema?",
                            "Sí, salir",
                            "No, quedarse"
                    );

                    if (quiereSalir) return true;
                    break;


                case REGISTRAR_VENTA:
                    new CotizadorItemVenta().mostrar(usuario.getSucursalId(), usuario.getId());
                    break;

                case CONSULTAR_PRECIO:
                    new ConsultaPrecioVigente().mostrar();
                    break;

                case BUSCAR_LIBRO:
                    Libro libro = new BuscadorLibros().seleccionarLibro();
                    if (libro != null) {
                        AceptarDialog.mostrar(null, "Buscar libro", "Elegiste: [" + libro.getId() + "] " + libro.getTitulo());
                    }
                    break;

                case VER_STOCK:
                    new StockViewer().mostrar(usuario.getSucursalId());
                    break;
            }
        }
    }


    private void mostrarPendiente(String nombreOpcion) {
        AceptarDialog.mostrar(null, "En construcción", "» \"" + nombreOpcion + "\" (Pendiente)");
    }
}
