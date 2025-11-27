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
        boolean deseaAsociar = ConfirmacionDialog.mostrarDialogo(
                "Cliente",
                "¿Deseás asociar un cliente a la venta?",
                "Sí, asociar",
                "No"
        );
        if (!deseaAsociar) return null;

        ClienteRepository repo = new ClienteRepository();

        while (true) {
            int op = MenuAsociarCliente.elegirOpcion();

            if (op == MenuAsociarCliente.OPCION_CANCELAR) {
                return null;
            }

            switch (op) {
                case MenuAsociarCliente.OPCION_BUSCAR_EMAIL -> {
                    String email = BuscadorDialog.pedirTexto(
                            "Cliente - Buscar por email",
                            "Ingresá email exacto:"
                    );
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
                    email = email.toLowerCase();
                    Cliente c = repo.buscarPorEmail(email);

                    if (c == null) {
                        mensajeInfo("No existe un cliente con ese email.");
                        continue;
                    }
                    if (confirmar("Asociar a: " + etiqueta(c) + " ?")) {
                        return c.getId();
                    }
                }
                case MenuAsociarCliente.OPCION_BUSCAR_TEXTO -> {
                    String texto = BuscadorDialog.pedirTexto(
                            "Cliente - Buscar por texto",
                            "Buscar por nombre, apellido o parte del email:"
                    );
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
                    OpcionCliente[] opcionesLista = lista.stream()
                            .map(OpcionCliente::new)
                            .toArray(OpcionCliente[]::new);
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
                case MenuAsociarCliente.OPCION_REGISTRAR_NUEVO -> {
                    RegistroDialog.DatosCliente datos = RegistroDialog.mostrarRegistro();
                    if (datos == null) continue;

                    String nombre   = datos.getNombre();
                    String apellido = datos.getApellido();
                    String email    = datos.getEmail();

                    try {
                        int idNuevo = repo.insertarRapido(nombre, apellido, email);
                        mensajeOk("Cliente registrado: " + nombre
                                + (apellido != null ? " " + apellido : "")
                                + " <" + email + ">");
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
                default -> {
                    return null;
                }
            }
        }
    }

    private static String trimOrNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private static boolean confirmar(String texto) {
        return ConfirmacionDialog.mostrarDialogo(
                "Confirmar cliente",
                texto,
                "Asociar",
                "Volver"
        );
    }

    private static void mensajeInfo(String texto) {
        AceptarDialog.mostrar(
                null,
                "Información",
                texto
        );
    }

    private static void mensajeOk(String texto) {
        AceptarDialog.mostrar(
                null,
                "OK",
                texto
        );
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
