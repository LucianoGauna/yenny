package domain;

public class Cliente {
    private int id;
    private String nombre;
    private String apellido;
    private String email;

    public Cliente(int id, String nombre, String email, String apellido) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getEmail() { return email; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setEmail(String email) { this.email = email; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    @Override
    public String toString() {
        return "Cliente{id=" + id + ", nombre='" + nombre + "', apellido='" + apellido +
                "', email='" + email + "}";
    }
}
