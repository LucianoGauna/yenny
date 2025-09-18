import domain.Rol;
import domain.Usuario;

public class Main {
    public static void main(String[] args) {
        Usuario admin = new Usuario(
                1, "Ana", "Admin",
                "admin", "admin123",
                Rol.ADMIN, 1, true
        );

        Usuario cajero = new Usuario(
                2, "Carlos", "Cajero",
                "cajero", "cajero123",
                Rol.CAJERO, 1, true
        );

        System.out.println(admin);
        System.out.println(cajero);
    }
}