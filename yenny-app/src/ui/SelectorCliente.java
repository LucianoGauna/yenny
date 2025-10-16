package ui;

import dll.ClienteRepository;
import domain.Cliente;
import utils.Validaciones;

import javax.swing.*;
import java.sql.SQLException;
import java.util.List;

public class SelectorCliente {
    /* Abre un flujo para asociar un cliente */
    public Integer seleccionarClienteId() {
        int deseaAsociar = JOptionPane.showConfirmDialog(
                null,
                "¿Deseás asociar un cliente a la venta?",
                "Cliente",
                JOptionPane.YES_NO_OPTION
        );
        if (deseaAsociar != JOptionPane.YES_OPTION) return null;

        ClienteRepository repo = new ClienteRepository();

        while (true) {
            String[] opciones = {"Buscar por email", "Buscar por texto", "Registrar nuevo", "Cancelar"};
            int op = JOptionPane.showOptionDialog(
                    null,
                    "Elegí una opción para asociar cliente:",
                    "Cliente",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    opciones,
                    opciones[0]
            );

            if (op == 3 || op == JOptionPane.CLOSED_OPTION) {
                return null;
            }

            switch (op) {
                case 0 -> {
                    String email = JOptionPane.showInputDialog(null, "Ingresá email exacto:", "Cliente", JOptionPane.QUESTION_MESSAGE);
                    if (email == null) continue;
                    email = email.trim();
                    if (email.isEmpty()) {
                        mensajeInfo("El email es obligatorio.");
                        continue;
                    }
                    if (!Validaciones.esEmailValido(email)) {
                        mensajeInfo("Formato de email inválido.");
                        continue;
                    }
                    email = email.trim().toLowerCase();
                    Cliente c = repo.buscarPorEmail(email);

                    if (c == null) {
                        mensajeInfo("No existe un cliente con ese email.");
                        continue;
                    }
                    if (confirmar("Asociar a: " + etiqueta(c) + " ?")) {
                        return c.getId();
                    }
                }
                case 1 -> {
                    String texto = JOptionPane.showInputDialog(null, "Buscar por nombre, apellido o parte del email:", "Cliente", JOptionPane.QUESTION_MESSAGE);
                    if (texto == null) continue;
                    texto = texto.trim();
                    if (texto.isEmpty()) {
                        mensajeInfo("Ingresá un texto para buscar.");
                        continue;
                    }
                    List<Cliente> lista = repo.buscarPorTexto(texto);
                    if (lista.isEmpty()) {
                        mensajeInfo("No se encontraron clientes.");
                        continue;
                    }
                    OpcionCliente[] opcionesLista = lista.stream().map(OpcionCliente::new).toArray(OpcionCliente[]::new);
                    OpcionCliente elegido = (OpcionCliente) JOptionPane.showInputDialog(
                            null,
                            "Elegí un cliente:",
                            "Resultados",
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            opcionesLista,
                            opcionesLista[0]
                    );
                    if (elegido != null && confirmar("Asociar a: " + etiqueta(elegido.cliente()) + " ?")) {
                        return elegido.cliente().getId();
                    }
                }
                case 2 -> {
                    JTextField campoNombre = new JTextField();
                    JTextField campoApellido = new JTextField();
                    JTextField campoEmail = new JTextField();

                    Object[] msg = {
                            "Nombre (obligatorio):", campoNombre,
                            "Apellido (opcional):", campoApellido,
                            "Email (obligatorio):", campoEmail
                    };
                    int ok = JOptionPane.showConfirmDialog(null, msg, "Registrar cliente", JOptionPane.OK_CANCEL_OPTION);
                    if (ok != JOptionPane.OK_OPTION) continue;

                    String nombre = trimOrNull(campoNombre.getText());
                    String apellido = trimOrNull(campoApellido.getText());
                    String email = trimOrNull(campoEmail.getText());

                    if (nombre == null || nombre.isEmpty()) {
                        mensajeInfo("El nombre es obligatorio.");
                        continue;
                    }
                    if (email == null || email.isEmpty()) {
                        mensajeInfo("El email es obligatorio.");
                        continue;
                    }
                    if (!Validaciones.esEmailValido(email)) {
                        mensajeInfo("Formato de email inválido.");
                        continue;
                    }
                    email = email.trim().toLowerCase();

                    try {
                        int idNuevo = repo.insertarRapido(nombre, apellido, email);
                        mensajeOk("Cliente registrado: " + nombre + (apellido != null ? " " + apellido : "") + " <" + email + ">");
                        return idNuevo;
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(
                                null,
                                "No se pudo registrar el cliente.\n" + e.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
                default -> { /* nada */ }
            }
        }
    }

    private static String trimOrNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private static boolean confirmar(String texto) {
        int r = JOptionPane.showConfirmDialog(null, texto, "Confirmar", JOptionPane.YES_NO_OPTION);
        return r == JOptionPane.YES_OPTION;
    }

    private static void mensajeInfo(String texto) {
        JOptionPane.showMessageDialog(null, texto, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void mensajeOk(String texto) {
        JOptionPane.showMessageDialog(null, texto, "OK", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Opción visual para el combo/lista. */
    private static final class OpcionCliente {
        private final Cliente cliente;
        OpcionCliente(Cliente c) { this.cliente = c; }
        Cliente cliente() { return cliente; }
        @Override public String toString() { return etiqueta(cliente); }
    }

    private static String etiqueta(Cliente c) {
        String nombre = c.getNombre() != null ? c.getNombre() : "";
        String apellido = c.getApellido() != null ? c.getApellido() : "";
        String email = c.getEmail() != null ? c.getEmail() : "";
        String full = (nombre + " " + apellido).trim();
        return full.isEmpty() ? ("<" + email + ">") : (full + " <" + email + ">");
    }
}
