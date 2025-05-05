/*
Maffioli Gianmarco 757587 VA
Rolla Francesca 757922 VA
Borsani Nicolò 757584 VA
Isopo Silvia 757368 VA
Mariani Amati Federico 756811 VA
 */
package src.bookrecommender;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import static src.bookrecommender.BookRecommender.*;
/**
 * La classe {@code visualizzaLibro}gestisce la visualizzazione delle informazioni relative ai libri.
 * Permette di visualizzare i dettagli di un libro, come le valutazioni, le note degli utenti e i suggerimenti di altri libri.
 * Gli utenti possono scegliere il libro da visualizzare, vedere le valutazioni medie per vari aspetti, leggere le note degli utenti
 * e ottenere suggerimenti di libri correlati.
 */

public class visualizzaLibro {
    /**
     * Visualizza le informazioni di un libro selezionato dall'utente, incluse le valutazioni, le note degli utenti e i suggerimenti.
     *
     * @param counter Lista degli indici dei libri disponibili per la visualizzazione. Ogni elemento rappresenta l'indice di un libro.
     *               L'utente può scegliere un libro inserendo l'indice corrispondente.
     *               Se l'utente inserisce "STOP", il programma termina la visualizzazione.
     */

    public static void Visualizza(ArrayList<Integer> counter) {
        ArrayList<String> note;
        String scelta = "", indice = "", temp;
        String [] tipo = {"Stile","Contenuto","Gradevolezza","Originalità","Edizione","Finale"};
        boolean flag;
        while(!scelta.equals("N")) {
            Scanner in = new Scanner(System.in);
            if(scelta.equals("S")) {
                System.out.println("Vuoi visualizzare un'altro libro? (" + ANSI_GREEN + "S" + ANSI_RESET + "/" + ANSI_RED + "N" + ANSI_RESET + ")");
                scelta = in.nextLine().trim().toUpperCase().replaceAll(" ", "");
            }
            else
                scelta = "S";
            flag = true;
            switch (scelta){
                case "S":
                    while(flag) {
                        System.out.println("Che libro vuoi visualizzare? Inserisci l'indice corrispondente altrimenti digita (" + ANSI_RED + "STOP" + ANSI_RESET + ")");
                        indice = in.nextLine().trim().toUpperCase().replaceAll(" ", "");
                        if(indice.equals("STOP")) {
                            scelta = "N";
                            break;
                        }else {
                            try {
                                if (Integer.parseInt(indice) <= counter.size() && Integer.parseInt(indice) > 0)
                                    flag = false;
                                else
                                    System.out.println(ANSI_RED + "Inserimento errato! Inserire un numero compreso tra 1 e " + counter.size() + ANSI_RESET);
                            } catch (NumberFormatException e) {
                                System.err.println("Input non valido. Assicurati di inserire un numero intero.");
                            }
                        }
                    }
                    if(indice.equals("STOP")) {
                        break;
                    }
                    indice = String.valueOf(counter.get(Integer.parseInt(indice) - 1));
                    if(esisteValSug(indice,ValutazioniLibriData)) {
                        for (int i = 2, j = 0; i < 14; i++) {
                            if(i%2 == 0) {
                                temp = mediaVoto(indice, i);
                                System.out.println("La media della valutazione " + ANSI_BLUE + tipo[j] + ANSI_RESET + " è di " + ANSI_BLUE + temp + ANSI_RESET);
                            }else{
                                note = noteVoto(indice,i);
                                if(note.isEmpty()){
                                    System.out.println(ANSI_RED + "Non ci sono note degli utenti associati a " + ANSI_BLUE + tipo[j] + ANSI_RESET);
                                }else{
                                    for (String s : note) {
                                        System.out.println(s);
                                    }
                                }
                                j++;
                            }
                        }
                    }else
                        System.out.println(ANSI_RED + "Non ci sono valutazioni associate a questo libro!" + ANSI_RESET);
                    if(esisteValSug(indice,ConsigliLibriData)){
                        suggerimenti(indice);
                    }else{
                        System.out.println(ANSI_RED + "Non ci sono libri suggeriti per il libro selezionato!" + ANSI_RESET);
                    }
                    break;
                    case "N":
                        break;
                        default:
                            System.out.println(ANSI_RED + "Inserimento errato! Riprovare" + ANSI_RESET);
            }
        }
    }
    /**
     * Calcola la media dei voti di un libro per una specifica valutazione (ad esempio, "Stile", "Contenuto", ecc.).
     *
     * @param indice L'indice del libro per il quale calcolare la media dei voti.
     * @param id L'identificativo della valutazione (colonna) per cui calcolare la media.
     * @return La media dei voti {@code String} formattata come intero con un decimale.
     */

    public static String mediaVoto(String indice, int id){
        String voto = "0";
        int counter = 0;
        try (CSVReader reader = new CSVReaderBuilder(new FileReader(ValutazioniLibriData)).build()) {
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                if(nextLine[0].equals(indice)) {
                    counter++;
                    voto = String.valueOf(Double.parseDouble(voto) + Double.parseDouble(nextLine[id]));
                }
            }
        } catch (IOException | CsvValidationException ignored) {}
        if(counter != 0)
            voto = new DecimalFormat("#.#").format(Double.parseDouble(voto) / counter);
        return voto;
    }
    /**
     * Recupera tutte le note degli utenti associate a un libro per una specifica valutazione.
     *
     * @param indice L'indice del libro per il quale recuperare le note degli utenti.
     * @param id L'identificativo della valutazione (colonna) per cui recuperare le note.
     * @return Una {@code ArrayList<String>}, dove ogni stringa è una nota di un utente associata al libro.
     */

    public static ArrayList<String> noteVoto(String indice, int id){
        ArrayList<String> note = new ArrayList<>();
        try (CSVReader reader = new CSVReaderBuilder(new FileReader(ValutazioniLibriData)).build()) {
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                if(nextLine[0].equals(indice)) {
                   note.add("L'utente " + ANSI_PURPLE + nextLine[1] + ANSI_RESET + " ha scritto: \n" + nextLine[id] + ANSI_GREEN + "\n--------------" + ANSI_RESET);
                }
            }
        } catch (IOException | CsvValidationException ignored) {}
        return note;
    }
    /**
     * Recupera e visualizza i suggerimenti di libri correlati per un libro specificato.
     * I suggerimenti sono basati su valutazioni di altri utenti.
     *
     * @param indice L'indice del libro per cui recuperare i suggerimenti.
     */

    public static void suggerimenti(String indice){
        try (CSVReader reader = new CSVReaderBuilder(new FileReader(ConsigliLibriData)).build()) {
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                if(nextLine[0].equals(indice)) {
                    System.out.println("L'utente " + ANSI_BLUE + nextLine[1] + ANSI_RESET + " ha consigliato i seguenti libri: ");
                    int[] numeri;
                    if (nextLine.length > 3 && nextLine.length <= 5) {
                        String [] daOrdinare = Arrays.copyOfRange(nextLine, 2, nextLine.length);
                        numeri = new int[daOrdinare.length];
                        for (int k = 0; k < daOrdinare.length; k++) {
                            numeri[k] = Integer.parseInt(daOrdinare[k]);
                        }
                        Arrays.sort(numeri);
                    }else{
                        numeri = new int[]{Integer.parseInt(nextLine[2])};
                    }
                    try (CSVReader reader2 = new CSVReaderBuilder(new FileReader(LibriData)).build()) {
                        String[] nextLine2;
                        for(int k = 0, c = 0; (nextLine2 = reader2.readNext()) != null && c < numeri.length; k++) {
                            if(k == numeri[c]){
                                c++;
                                System.out.println("(" + c + ")" + StampaInfoFull(nextLine2));
                            }
                        }
                    } catch (IOException | CsvValidationException ignored) {}
                }
            }
        } catch (IOException | CsvValidationException ignored) {}
    }

    /**
     * Verifica se esistono valutazioni o suggerimenti associati a un libro specificato.
     *
     * @param indice L'indice del libro da verificare.
     * @param directory La directory (file CSV) da cui recuperare i dati (valutazioni o suggerimenti).
     * @return {@code true} se esistono valutazioni o suggerimenti associati al libro; {@code false} altrimenti.
     */

    public static boolean esisteValSug(String indice, String directory){
        try (CSVReader reader = new CSVReaderBuilder(new FileReader(directory)).build()) {
            String[] nextLine;
            while((nextLine = reader.readNext()) != null) {
                if(nextLine[0].equals(indice)){
                    return true;
                }
            }
        } catch (IOException | CsvValidationException ignored) {}
        return false;
    }
}
