package ui;

import domain.Usuario;

import javax.swing.*;
import java.util.List;

public class Login {
    public Usuario mostrar(List<Usuario> usuarios) {
        while (true) {
            LoginDialog dialog = new LoginDialog();
            LoginDialog.Credenciales cred = dialog.pedir();

            if (cred == null) {
                return null;
            }

            String username = cred.username();
            String contrasenia = cred.password();

            Usuario u = autenticar(usuarios, username, contrasenia);
            if (u != null) {
                AceptarDialog.mostrar(null, "Login correcto", "Bienvenido/a " + u.getNombre() + " (" + u.getRol() + ")");
                return u;
            }

            JOptionPane.showMessageDialog(
                    null,
                    "Credenciales inválidas o usuario inactivo. Intente nuevamente.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    private Usuario autenticar(List<Usuario> usuarios, String username, String password) {
        for (Usuario u : usuarios) {
            if (u.isActivo()
                    && u.getUsername().equals(username)
                    && u.getContraseniaHash().equals(password)) {
                return u;
            }
        }
        return null;
    }
}
