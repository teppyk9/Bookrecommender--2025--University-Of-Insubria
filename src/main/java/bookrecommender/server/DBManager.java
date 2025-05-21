package bookrecommender.server;


import bookrecommender.common.Libro;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBManager {
    private static final String URL = "jdbc:postgresql://localhost:5432/bookrecommender";
    private static final String USER = "postgres";
    private static final String PASSWORD = "1234";

    public DBManager() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public List<Libro> cercaLibriPerTitolo(String titolo) {
        List<Libro> risultati = new ArrayList<>();
        String query = "SELECT * FROM LIBRI WHERE LOWER(TITOLO) LIKE LOWER(?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + titolo + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Libro libro = new Libro(
                            rs.getInt("ID"),
                            rs.getString("TITOLO"),
                            rs.getString("AUTORE"),
                            rs.getString("DESCRIZIONE"),
                            rs.getString("CATEGORIA"),
                            rs.getString("EDITORE"),
                            rs.getFloat("PREZZO"),
                            rs.getShort("ANNOPUBBLICAZIONE"),
                            rs.getShort("MESEPUBBLICAZIONE")
                    );
                    risultati.add(libro);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return risultati;
    }
}
