package ui;

import domain.Usuario;

import javax.swing.*;
import java.util.List;

public class Login {
    public Usuario mostrar(List<Usuario> usuarios) {
        while (true) {
            JTextField campoUsername = new JTextField();
            JPasswordField campoContrasenia = new JPasswordField();

            Object[] message = {
                    "Usuario (email/username):", campoUsername,
                    "Contraseña:", campoContrasenia
            };

            int opcion = JOptionPane.showConfirmDialog(
                    null,
                    message,
                    "Login - Librería Yenny",
                    JOptionPane.OK_CANCEL_OPTION
            );

            if (opcion != JOptionPane.OK_OPTION) {
                return null;
            }

            String username = campoUsername.getText().trim();
            String contrasenia = new String(campoContrasenia.getPassword()).trim();

            Usuario u = autenticar(usuarios, username, contrasenia);
            if (u != null) {
                JOptionPane.showMessageDialog(
                        null,
                        "Bienvenido/a " + u.getNombre() + " (" + u.getRol() + ")",
                        "Login correcto",
                        JOptionPane.INFORMATION_MESSAGE
                );
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
