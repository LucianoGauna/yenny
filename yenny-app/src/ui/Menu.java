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
                    JOptionPane.showMessageDialog(null, "Sesión cerrada.", "Información", JOptionPane.INFORMATION_MESSAGE);
                    return false;

                case SALIR_SISTEMA:
                    int confirmar = JOptionPane.showConfirmDialog(
                            null,
                            "¿Seguro que desea salir del sistema?",
                            "Confirmación",
                            JOptionPane.YES_NO_OPTION
                    );
                    if (confirmar == JOptionPane.YES_OPTION) return true;
                    break;

                case ABM_LIBROS:           mostrarPendiente("ABM de libros"); break;
                case CAMBIAR_PRECIOS:      mostrarPendiente("Cambiar precios"); break;
                case CONFIGURAR_UMBRALES:  mostrarPendiente("Configurar umbrales"); break;
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
                    JOptionPane.showMessageDialog(null, "Sesión cerrada.", "Información", JOptionPane.INFORMATION_MESSAGE);
                    return false;

                case SALIR_SISTEMA:
                    int confirmar = JOptionPane.showConfirmDialog(
                            null,
                            "¿Seguro que desea salir del sistema?",
                            "Confirmación",
                            JOptionPane.YES_NO_OPTION
                    );
                    if (confirmar == JOptionPane.YES_OPTION) return true;
                    break;

                case REGISTRAR_VENTA:
                    new CotizadorItemVenta().mostrar(usuario.getSucursalId(), usuario.getId());
                    break;

                case BUSCAR_LIBRO:
                    Libro libro = new BuscadorLibros().seleccionarLibro();
                    if (libro != null) {
                        JOptionPane.showMessageDialog(
                                null,
                                "Elegiste: [" + libro.getId() + "] " + libro.getTitulo(),
                                "Buscar libro",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                    }
                    break;

                case VER_STOCK:
                    new StockViewer().mostrar(usuario.getSucursalId());
                    break;
            }
        }
    }


    private void mostrarPendiente(String nombreOpcion) {
        JOptionPane.showMessageDialog(
                null,
                ">> \"" + nombreOpcion + "\" (Pendiente)",
                "En construcción",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}
