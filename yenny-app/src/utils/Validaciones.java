package utils;

public class Validaciones {
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
}
