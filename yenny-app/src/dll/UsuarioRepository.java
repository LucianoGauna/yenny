package dll;

import domain.Rol;
import domain.Usuario;
import infra.Db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioRepository {

    /* Trae todos los usuarios ACTIVOS desde MySQL. */
    public List<Usuario> encontrarActivos() {
        String sql = """
            SELECT id, nombre, apellido, username, contrasenia_hash, rol, sucursal_id, activo
            FROM usuario
            WHERE activo = 1
        """;

        List<Usuario> usuarios = new ArrayList<>();

        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Usuario u = new Usuario(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("username"),
                        rs.getString("contrasenia_hash"),
                        Rol.valueOf(rs.getString("rol")),
                        rs.getInt("sucursal_id"),
                        rs.getBoolean("activo")
                );
                usuarios.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuarios;
    }
}
