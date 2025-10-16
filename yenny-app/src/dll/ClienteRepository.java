package dll;

import domain.Cliente;
import infra.Db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteRepository {
    /** Busca cliente por email exacto. Devuelve null si no existe. */
    public Cliente buscarPorEmail(String email) {
        String sql = """
            SELECT id, nombre, apellido, email
            FROM cliente
            WHERE email = ?
            LIMIT 1
        """;
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Busca por texto en nombre, apellido o email (contiene). Solo para ayudar a elegir. */
    public List<Cliente> buscarPorTexto(String texto) {
        String sql = """
            SELECT id, nombre, apellido, email
            FROM cliente
            WHERE nombre LIKE ? OR apellido LIKE ? OR email LIKE ?
            ORDER BY apellido, nombre
        """;
        List<Cliente> resultado = new ArrayList<>();
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            String patron = "%" + texto + "%";
            ps.setString(1, patron);
            ps.setString(2, patron);
            ps.setString(3, patron);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultado.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultado;
    }

    /**
     * Inserta un cliente.
     * @return id generado del cliente.
     */
    public int insertarRapido(String nombre, String apellido, String email) throws SQLException {
        String sql = """
            INSERT INTO cliente (nombre, apellido, email)
            VALUES (?, ?, ?)
        """;
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nombre);
            if (apellido == null || apellido.trim().isEmpty()) ps.setNull(2, Types.VARCHAR);
            else ps.setString(2, apellido.trim());
            if (email == null || email.trim().isEmpty()) ps.setNull(3, Types.VARCHAR);
            else ps.setString(3, email.trim());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) throw new SQLException("No se pudo obtener el ID del cliente.");
                return keys.getInt(1);
            }
        }
    }

    private Cliente mapear(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("email")
        );
    }
}
