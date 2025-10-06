package domain;

public class Libro {
    private int id;
    private String titulo;
    private String autor;
    private String editorial;
    private Categoria categoria;
    private boolean activo;

    public Libro(int id, String titulo, String autor, String editorial,
                 Categoria categoria, boolean activo) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.editorial = editorial;
        this.categoria = categoria;
        this.activo = activo;
    }

    public int getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public String getEditorial() { return editorial; }
    public Categoria getCategoria() { return categoria; }
    public boolean isActivo() { return activo; }

    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setAutor(String autor) { this.autor = autor; }
    public void setEditorial(String editorial) { this.editorial = editorial; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
    public void setActivo(boolean activo) { this.activo = activo; }

    @Override
    public String toString() {
        return "Libro{id=" + id + ", titulo='" + titulo + "', autor='" + autor +
                "', editorial='" + editorial + "', categoria=" + categoria +
                ", activo=" + activo + "}";
    }
}
