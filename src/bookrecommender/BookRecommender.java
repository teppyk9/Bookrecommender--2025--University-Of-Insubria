/*
Maffioli Gianmarco 757587 VA
Rolla Francesca 757922 VA
Borsani Nicolò 757584 VA
Isopo Silvia 757368 VA
Mariani Amati Federico 756811 VA
 */
package bookrecommender;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

/**
 * Un sistema per la valutazione e raccomandazione di libri,
 * in grado di permettere agli utenti registrati di inserire recensioni
 * e a tutti gli utenti di consultare le valutazioni e ricevere consigli di lettura.
 */
public class BookRecommender {
    /**
     * Costante ANSI per resettare il colore del testo.
     */
    public static final String ANSI_RESET = "\u001B[0m";
    /**
     * Costante ANSI per il colore rosso del testo.
     */
    public static final String ANSI_RED = "\u001B[31m";
    /**
     * Costante ANSI per il colore verde del testo.
     */
    public static final String ANSI_GREEN = "\033[32m";
    /**
     * Costante ANSI per il colore giallo del testo.
     */
    public static final String ANSI_YELLOW = "\u001B[33m";
    /**
     * Costante ANSI per il colore blu del testo.
     */
    public static final String ANSI_BLUE = "\u001B[34m";
    /**
     * Costante ANSI per il colore viola del testo.
     */
    public static final String ANSI_PURPLE = "\u001B[35m";

    /**
     * Percorso del file CSV contenente i dati degli utenti registrati.
     */
    public static final String UtentiRegistratiData;
    /**
     * Percorso del file CSV contenente i dati delle librerie.
     */
    public static final String LibrerieData;
    /**
     * Percorso del file CSV contenente i dati dei libri.
     */
    public static final String LibriData;
    /**
     * Percorso del file CSV contenente le valutazioni dei libri.
     */
    public static final String ValutazioniLibriData;

    /**
     * Percorso del file CSV contenente i consigli sui libri.
     */
    public static final String ConsigliLibriData;

    static {
        String baseDirectory = getBaseDirectory();

        UtentiRegistratiData = new File(baseDirectory, "data/UtentiRegistrati.dati.csv").getAbsolutePath();
        LibrerieData = new File(baseDirectory, "data/Librerie.dati.csv").getAbsolutePath();
        LibriData = new File(baseDirectory, "data/Libri.dati.csv").getAbsolutePath();
        ValutazioniLibriData = new File(baseDirectory, "data/ValutazioniLibri.dati.csv").getAbsolutePath();
        ConsigliLibriData = new File(baseDirectory, "data/ConsigliLibri.dati.csv").getAbsolutePath();

    }

    /**
     * Metodo principale dell'applicazione. Gestisce il menu principale e le interazioni con l'utente.
     * L'utente può cercare un libro, registrarsi, accedere all'area riservata o terminare il programma.
     *
     * @param args Argomento che permette al programma di accettare input dalla riga di comando quando viene eseguito.
     */
    public static void main(String[] args) {
        String scelta = "";
        while (!scelta.equalsIgnoreCase("STOP")) {
            Scanner s = new Scanner(System.in);
            System.out.println(ANSI_YELLOW + "BENVENUTO NEL MENU'! ");
            System.out.println(ANSI_RESET + "Cosa vuoi fare? Digita:\n(1) Per cercare un libro\n(2) Per registrarti\n(3) Per accedere\nSe vuoi terminare il programma digita (" + ANSI_RED + "STOP" + ANSI_RESET + ")");
            scelta = s.nextLine().trim().toUpperCase().replaceAll(" ", "");

            switch (scelta) {
                case "1":
                    cercaLibro.cerca(0);
                    break;

                case "2":
                    registrazione.reg();
                    break;

                case "3":
                    AreaRiservata.start();
                    break;

                case "STOP":
                    break;

                default:
                    System.out.println(ANSI_RED + "Scelta non valida! Riprova" + ANSI_RESET);
            }
        }
        System.out.println(ANSI_PURPLE + "Grazie, e buona giornata!" + ANSI_RESET);
    }

    /**
     * Verifica se una coppia di stringhe esiste nel file CSV specificato.
     *
     * @param temp1 Il primo valore da cercare.
     * @param temp2 Il secondo valore da cercare.
     * @param directory Il percorso del file CSV in cui cercare.
     * @return {@code true} se la coppia di valori esiste, {@code false} altrimenti.
     */
    public static boolean esiste(String temp1, String temp2, String directory) {
        try (CSVReader reader = new CSVReaderBuilder(new FileReader(directory)).build()) {
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                if (nextLine[0].equals(temp1) && nextLine[1].equals(temp2)) {
                    return true;
                }
            }
        } catch (IOException | CsvValidationException ignored) {}
        return false;
    }

/**
 * Stampa le informazioni complete di un libro in un formato leggibile.
 *
 * @param data Un array di stringhe contenente i dati del libro.
 * @return Una {@code String[]} formattata con le informazioni del libro.
 */
public static String StampaInfoFull(String[] data) {
    return (data[0] + " >>> " + data[1] + " >>> " + data[3] + " >>> " + data[4] + " >>> " + data[7]);
}

    /**
     * Ottieni la directory di base (Maffioli_757587).
     * Questo metodo determina il percorso assoluto della directory di base
     * in cui si trovano i file di dati dell'applicazione.
     *
     * @return Percorso assoluto {@code String} della directory di base.
     * @throws RuntimeException Se non è possibile determinare la directory di base.
     */
    private static String getBaseDirectory() {
        try {
            File jarFile = new File(BookRecommender.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            return jarFile.getParentFile().getParentFile().getAbsolutePath();
        } catch (Exception e) {
            throw new RuntimeException("Impossibile determinare la directory di base.");
        }
    }
}