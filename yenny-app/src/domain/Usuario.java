package domain;
import domain.Rol;

public class Usuario {
    private int id;
    private String nombre;
    private String apellido;
    private String username;
    private String contraseniaHash;
    private Rol rol;
    private int sucursalId;
    private boolean activo;

    public Usuario(int id, String nombre, String apellido, String username, String contraseniaHash, Rol rol, int sucursalId, boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.username = username;
        this.contraseniaHash = contraseniaHash;
        this.rol = rol;
        this.sucursalId = sucursalId;
        this.activo = activo;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getContraseniaHash() { return contraseniaHash; }
    public void setContraseniaHash(String contraseniaHash) { this.contraseniaHash = contraseniaHash; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }

    public int getSucursalId() { return sucursalId; }
    public void setSucursalId(int sucursalId) { this.sucursalId = sucursalId; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", username='" + username + '\'' +
                ", rol=" + rol +
                ", sucursalId=" + sucursalId +
                ", activo=" + activo +
                '}';
    }
}