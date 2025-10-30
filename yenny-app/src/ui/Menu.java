package ui;

import domain.Libro;
import domain.Rol;
import domain.Usuario;

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
            MenuAdminDialog.Resultado r = dlg.showDialog();

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
        final String titulo = "Librería Yenny — Menú Cajero";
        final String[] opciones = {
                "Registrar venta",
                "Buscar libro",
                "Ver stock",
                "Cerrar sesión",
                "Salir del sistema"
        };

        while (true) {
            int eleccion = JOptionPane.showOptionDialog(
                    null,
                    "Bienvenido/a " + usuario.getNombre() + " (CAJERO)\n\n" +
                            "Seleccione una opción:",
                    titulo,
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    opciones,
                    opciones[0]
            );

            if (eleccion == JOptionPane.CLOSED_OPTION || eleccion == 3) {
                JOptionPane.showMessageDialog(null, "Sesión cerrada.", "Información", JOptionPane.INFORMATION_MESSAGE);
                return false;
            }
            if (eleccion == 4) {
                int confirmar = JOptionPane.showConfirmDialog(
                        null,
                        "¿Seguro que desea salir del sistema?",
                        "Confirmación",
                        JOptionPane.YES_NO_OPTION
                );
                if (confirmar == JOptionPane.YES_OPTION) {
                    return true;
                }
                continue;
            }

            if (eleccion >= 0 && eleccion <= 2) {
                if (eleccion == 0) {
                    new CotizadorItemVenta().mostrar(usuario.getSucursalId(), usuario.getId());
                } else if (eleccion == 1) {
                    Libro libro = new BuscadorLibros().seleccionarLibro();
                    if (libro != null) {
                        JOptionPane.showMessageDialog(
                                null,
                                "Elegiste: [" + libro.getId() + "] " + libro.getTitulo(),
                                "Buscar libro",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                    }
                } else if (eleccion == 2) {
                    new StockViewer().mostrar(usuario.getSucursalId());
                } else {
                    mostrarPendiente(opciones[eleccion]);
                }
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
