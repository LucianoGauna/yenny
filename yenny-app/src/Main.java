import domain.Rol;
import domain.Usuario;

public class Main {
    public static void main(String[] args) {
        Usuario admin  = new Usuario(1, "admin",  "admin123",  Rol.ADMIN);
        Usuario cajero = new Usuario(2, "cajero", "cajero123", Rol.CAJERO);
        System.out.println(admin);
        System.out.println(cajero);
    }
}