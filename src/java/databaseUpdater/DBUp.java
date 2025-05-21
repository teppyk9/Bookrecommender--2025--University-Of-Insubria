package databaseUpdater;


import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DBUp {

    private static final Map<String, Integer> MAPPATURA_MESI = new HashMap<>() {{
        put("Gennaio", 1);
        put("Febbraio", 2);
        put("Marzo", 3);
        put("Aprile", 4);
        put("Maggio", 5);
        put("Giugno", 6);
        put("Luglio", 7);
        put("Agosto", 8);
        put("Settembre", 9);
        put("Ottobre", 10);
        put("Novembre", 11);
        put("Dicembre", 12);
    }};

    public static void main(String[] args) {
        String csvPath = "libri.csv";  // Percorso del file
        String jdbcUrl = "jdbc:postgresql://localhost:5432/tuo_database";
        String user = "tuo_utente";
        String password = "tua_password";

        try (
                Connection conn = DriverManager.getConnection(jdbcUrl, user, password);
                CSVReader reader = new CSVReader(new FileReader(csvPath))
        ) {
            String[] row;
            int lineCount = 0;

            // Preparazione statement
            String sql = "INSERT INTO LIBRI (TITOLO, AUTORE, DESCRIZIONE, CATEGORIA, EDITORE, PREZZO, ANNOPUBBLICAZIONE, MESEPUBBLICAZIONE) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);

            while ((row = reader.readNext()) != null) {
                lineCount++;
                if (row.length != 8) {
                    System.err.println("Linea " + lineCount + " ignorata: numero di colonne errato.");
                    continue;
                }

                try {
                    stmt.setString(1, row[0].isBlank() ? null : row[0]); // TITOLO
                    stmt.setString(2, row[1].isBlank() ? null : row[1]); // AUTORE
                    stmt.setString(3, row[2].isBlank() ? null : row[2]); // DESCRIZIONE
                    stmt.setString(4, row[3].isBlank() ? null : row[3]); // CATEGORIA
                    stmt.setString(5, row[4].isBlank() ? null : row[4]); // EDITORE

                    if (row[5].isBlank()) {
                        stmt.setNull(6, Types.NUMERIC); // PREZZO
                    } else {
                        stmt.setBigDecimal(6, new BigDecimal(row[5]));
                    }

                    if (row[6].isBlank()) {
                        stmt.setNull(7, Types.SMALLINT); // ANNOPUBBLICAZIONE
                    } else {
                        stmt.setShort(7, Short.parseShort(row[6]));
                    }

                    if (row[7].isBlank()) {
                        stmt.setNull(8, Types.SMALLINT); // MESEPUBBLICAZIONE
                    } else {
                        Integer mese = MAPPATURA_MESI.get(row[7].trim());
                        if (mese == null) {
                            stmt.setNull(8, Types.SMALLINT);
                        } else {
                            stmt.setInt(8, mese);
                        }
                    }

                    stmt.executeUpdate();
                } catch (Exception e) {
                    System.err.println("Errore alla riga " + lineCount + ": " + e.getMessage());
                }
            }

            System.out.println("Importazione completata.");
        } catch (IOException | CsvValidationException | SQLException e) {
            e.printStackTrace();
        }
    }
}
