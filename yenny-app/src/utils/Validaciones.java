package utils;

import java.util.regex.Pattern;

public class Validaciones {
    private static final Pattern EMAIL_REGEX =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,63}$");

    /** Parsea y valida que sea un entero > 0. Lanza IllegalArgumentException con mensaje claro. */
    public static int parseEnteroPositivo(String valor, String nombreCampo) {
        if (valor == null) throw new IllegalArgumentException(nombreCampo + " es obligatorio.");
        String v = valor.trim();
        if (v.isEmpty()) throw new IllegalArgumentException(nombreCampo + " es obligatorio.");
        try {
            int n = Integer.parseInt(v);
            if (n <= 0) throw new IllegalArgumentException(nombreCampo + " debe ser un entero positivo.");
            return n;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(nombreCampo + " debe ser un entero positivo.");
        }
    }

    /** Verifica que el texto no sea nulo/ vacío. */
    public static void requireNoVacio(String valor, String nombreCampo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(nombreCampo + " es obligatorio.");
        }
    }

    /** Chequea stock disponible. */
    public static void validarDisponible(int pedido, int disponible) {
        if (pedido > disponible) {
            throw new IllegalArgumentException("Stock insuficiente. Disponible: " + disponible);
        }
    }

    /* Validar email válido */
    public static boolean esEmailValido(String email) {
        if (email == null) return false;
        String e = email.trim();
        if (e.isEmpty() || e.length() > 56) return false;
        e = e.toLowerCase();
        return EMAIL_REGEX.matcher(e).matches();
    }
}
