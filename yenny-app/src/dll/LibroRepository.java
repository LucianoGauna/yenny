package dll;

import domain.Categoria;
import domain.Libro;
import infra.Db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibroRepository {
    /* Busca libros activos cuyo título/autor/editorial contengan el texto */
    public List<Libro> buscarActivosPorTexto(String texto) {
        return buscarPorTextoYActivo(texto, true);
    }

    public List<Libro> buscarInactivosPorTexto(String texto) {
        return buscarPorTextoYActivo(texto, false);
    }

    public List<Libro> buscarPorTextoYActivo(String texto, boolean activo) {
        String sql = """
            SELECT id, titulo, autor, editorial, categoria, activo
            FROM libro
            WHERE activo = ?
              AND (titulo LIKE ? OR autor LIKE ? OR editorial LIKE ?)
            ORDER BY id
        """;

        List<Libro> resultado = new ArrayList<>();

        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String patron = "%" + texto + "%";
            ps.setBoolean(1, activo);
            ps.setString(2, patron);
            ps.setString(3, patron);
            ps.setString(4, patron);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultado.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error buscando libros.", e);
        }
        return resultado;
    }

    public int insertar(String titulo, String autor, String editorial, Categoria categoria) throws SQLException {
        String sql = """
            INSERT INTO libro (titulo, autor, editorial, categoria, activo)
            VALUES (?, ?, ?, ?, 1)
        """;

        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, titulo.trim());
            ps.setString(2, autor.trim());
            ps.setString(3, editorial.trim());
            ps.setString(4, categoria.name());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) throw new SQLException("No se pudo obtener el ID del libro.");
                return keys.getInt(1);
            }
        }
    }

    public boolean existeDuplicadoTituloAutor(String titulo, String autor) {
        String sql = """
            SELECT 1
            FROM libro
            WHERE UPPER(TRIM(titulo)) = UPPER(TRIM(?))
              AND UPPER(TRIM(autor)) = UPPER(TRIM(?))
            LIMIT 1
        """;

        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, titulo);
            ps.setString(2, autor);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error validando duplicado de libro.", e);
        }
    }

    public boolean existeDuplicadoTituloAutorExceptoId(String titulo, String autor, int libroId) {
        String sql = """
            SELECT 1
            FROM libro
            WHERE UPPER(TRIM(titulo)) = UPPER(TRIM(?))
              AND UPPER(TRIM(autor)) = UPPER(TRIM(?))
              AND id <> ?
            LIMIT 1
        """;

        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, titulo);
            ps.setString(2, autor);
            ps.setInt(3, libroId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error validando duplicado de libro.", e);
        }
    }

    public void actualizar(int libroId, String titulo, String autor, String editorial, Categoria categoria) throws SQLException {
        String sql = """
            UPDATE libro
            SET titulo = ?, autor = ?, editorial = ?, categoria = ?
            WHERE id = ?
        """;

        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, titulo.trim());
            ps.setString(2, autor.trim());
            ps.setString(3, editorial.trim());
            ps.setString(4, categoria.name());
            ps.setInt(5, libroId);
            int filas = ps.executeUpdate();
            if (filas == 0) throw new SQLException("No existe libro con ID " + libroId + ".");
        }
    }

    public void actualizarActivo(int libroId, boolean activo) throws SQLException {
        String sql = """
            UPDATE libro
            SET activo = ?
            WHERE id = ?
        """;

        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, activo);
            ps.setInt(2, libroId);
            int filas = ps.executeUpdate();
            if (filas == 0) throw new SQLException("No existe libro con ID " + libroId + ".");
        }
    }

    private Libro mapear(ResultSet rs) throws SQLException {
        return new Libro(
                rs.getInt("id"),
                rs.getString("titulo"),
                rs.getString("autor"),
                rs.getString("editorial"),
                Categoria.valueOf(rs.getString("categoria")),
                rs.getBoolean("activo")
        );
    }
}
