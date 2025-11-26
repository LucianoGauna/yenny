import dll.UsuarioRepository;
import domain.Usuario;
import ui.AceptarDialog;
import ui.Login;
import ui.Menu;

import javax.swing.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        infra.Db.ping();

        /* Cargar usuarios activos desde MySQL */
        List<Usuario> usuarios = new UsuarioRepository().encontrarActivos();

        Login login = new Login();
        Menu menu = new Menu();

        while (true) {
            /* Login (puede devolver null si el usuario cancela) */
            Usuario logueado = login.mostrar(usuarios);
            if (logueado == null) {
                AceptarDialog.mostrar(null, "Librería Yenny", "Programa finalizado");
                break;
            }

            /* Menú según rol */
            boolean salirDelSistema = menu.mostrar(logueado);
            if (salirDelSistema) {
                AceptarDialog.mostrar(null, "Librería Yenny", "Hasta luego!");
                break;
            }
        }
    }
}
