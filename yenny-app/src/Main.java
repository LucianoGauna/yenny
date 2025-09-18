import domain.Rol;
import domain.Usuario;
import ui.Login;
import ui.Menu;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Usuario> usuarios = new ArrayList<>();
        usuarios.add(new Usuario(
                1, "Ana", "Gomez",
                "admin", "admin123",
                Rol.ADMIN, 1, true
        ));
        usuarios.add(new Usuario(
                2, "Carlos", "Fernandez",
                "cajero", "cajero123",
                Rol.CAJERO, 1, true
        ));

        Login login = new Login();
        Menu menu = new Menu();

        while (true) {
            // Login (puede devolver null si el usuario cancela)
            Usuario logueado = login.mostrar(usuarios);
            if (logueado == null) {
                JOptionPane.showMessageDialog(null, "Programa finalizado.", "Librería Yenny", JOptionPane.INFORMATION_MESSAGE);
                break;
            }

            // Menú según rol
            boolean salirDelSistema = menu.mostrar(logueado);
            if (salirDelSistema) {
                JOptionPane.showMessageDialog(null, "¡Hasta luego!", "Librería Yenny", JOptionPane.INFORMATION_MESSAGE);
                break;
            }
        }
    }
}
