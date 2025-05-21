package databaseUpdater;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;

public class DBUp {

    private static final Map<String, Integer> MAPPATURA_MESI = new HashMap<>();
    static {
        MAPPATURA_MESI.put("Gennaio", 1);
        MAPPATURA_MESI.put("Febbraio", 2);
        MAPPATURA_MESI.put("Marzo", 3);
        MAPPATURA_MESI.put("Aprile", 4);
        MAPPATURA_MESI.put("Maggio", 5);
        MAPPATURA_MESI.put("Giugno", 6);
        MAPPATURA_MESI.put("Luglio", 7);
        MAPPATURA_MESI.put("Agosto", 8);
        MAPPATURA_MESI.put("Settembre", 9);
        MAPPATURA_MESI.put("Ottobre", 10);
        MAPPATURA_MESI.put("Novembre", 11);
        MAPPATURA_MESI.put("Dicembre", 12);
        MAPPATURA_MESI.put("January", 1);
        MAPPATURA_MESI.put("February", 2);
        MAPPATURA_MESI.put("March", 3);
        MAPPATURA_MESI.put("April", 4);
        MAPPATURA_MESI.put("May", 5);
        MAPPATURA_MESI.put("June", 6);
        MAPPATURA_MESI.put("July", 7);
        MAPPATURA_MESI.put("August", 8);
        MAPPATURA_MESI.put("September", 9);
        MAPPATURA_MESI.put("October", 10);
        MAPPATURA_MESI.put("November", 11);
        MAPPATURA_MESI.put("December", 12);
    }

    public static void main(String[] args) {
        String csvPath = "C:/Users/gmtep/Desktop/Programmi/Maffioli_757587_B/data/Libri.dati.csv";  // Percorso del file
        String url = "jdbc:postgresql://localhost:5432/bookrecommender";
        String user = "postgres";
        String password = "1234";

        String sql = """
                INSERT INTO libri (titolo, autore, descrizione, categoria, editore, prezzo, mesepubblicazione, annopubblicazione)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement ps = conn.prepareStatement(sql);
             BufferedReader reader = new BufferedReader(new FileReader(csvPath))) {

            String line;
            int riga = 0;

            while ((line = reader.readLine()) != null) {
                riga++;
                // Gestione righe malformate
                String[] campi = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (campi.length < 8) {
                    System.out.println("Riga ignorata (troppo corta) alla linea " + riga);
                    continue;
                }

                try {
                    // I campi sono: Titolo, Autore, Descrizione, Categoria, Editore, Prezzo, Mese, Anno
                    for (int i = 0; i < campi.length; i++) {
                        campi[i] = campi[i].trim(); // lasciamo virgolette se ci sono
                    }

                    ps.setString(1, toNull(campi[0]));
                    ps.setString(2, toNull(campi[1]));
                    ps.setString(3, toNull(campi[2]));
                    ps.setString(4, toNull(campi[3]));
                    ps.setString(5, toNull(campi[4]));

                    // Prezzo
                    if (campi[5].isEmpty()) {
                        ps.setNull(6, java.sql.Types.DECIMAL);
                    } else {
                        ps.setBigDecimal(6, new java.math.BigDecimal(campi[5]));
                    }

                    // Mese
                    Integer meseNum = MAPPATURA_MESI.get(campi[6].replace("\"", "").trim());
                    if (meseNum == null) {
                        ps.setNull(7, java.sql.Types.SMALLINT);
                    } else {
                        ps.setInt(7, meseNum);
                    }

                    // Anno
                    if (campi[7].isEmpty()) {
                        ps.setNull(8, java.sql.Types.SMALLINT);
                    } else {
                        ps.setInt(8, Integer.parseInt(campi[7]));
                    }

                    ps.executeUpdate();

                } catch (Exception e) {
                    System.out.println("Errore alla linea " + riga + ": " + e.getMessage());
                }
            }

            System.out.println("Importazione completata.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String toNull(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s;
    }
}
