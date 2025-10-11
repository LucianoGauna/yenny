import dll.UsuarioRepository;
import domain.Usuario;
import ui.Login;
import ui.Menu;

import javax.swing.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        infra.Db.ping();

        // Cargar usuarios activos desde MySQL
        List<Usuario> usuarios = new UsuarioRepository().encontrarActivos();
        System.out.println(usuarios);

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
