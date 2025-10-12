package ui;

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
        final String titulo = "Librería Yenny — Menú Administrador";
        final String[] opciones = {
                "ABM de libros",
                "Cambiar precios",
                "Configurar umbrales",
                "Reporte quincenal",
                "Cerrar sesión",
                "Salir del sistema"
        };

        while (true) {
            int eleccion = JOptionPane.showOptionDialog(
                    null,
                    "Bienvenido/a " + usuario.getNombre() + " (ADMIN)\n\n" +
                            "Seleccione una opción:",
                    titulo,
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    opciones,
                    opciones[0]
            );

            if (eleccion == JOptionPane.CLOSED_OPTION || eleccion == 4) {
                JOptionPane.showMessageDialog(null, "Sesión cerrada.", "Información", JOptionPane.INFORMATION_MESSAGE);
                return false;
            }
            if (eleccion == 5) {
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

            if (eleccion >= 0 && eleccion <= 3) {
                mostrarPendiente(opciones[eleccion]);
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
                if (eleccion == 1) {
                    new BuscadorLibros().mostrar();
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
