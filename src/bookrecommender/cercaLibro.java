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
import java.util.ArrayList;
import java.util.Scanner;

import static src.bookrecommender.BookRecommender.*;

/**
 * La classe {@code cercaLibro} gestisce la ricerca di libri nel sistema.
 * Permette agli utenti di cercare libri in base a titolo, autore o anno di pubblicazione.
 */
public class cercaLibro {

    /**
     * Metodo principale per cercare un libro.
     * Gestisce l'interazione con l'utente e le scelte di ricerca.
     *
     * @param id Identificatore per determinare il contesto della ricerca (0 per ricerca normale, 1 per aggiunta).
     * @return L'indice del libro {@code int indice} trovato o {@code 0} se non trovato o se la ricerca non richiede un output quando {@code id = 0}.
     */
    public static int cerca(int id) {
        Scanner s = new Scanner(System.in);
        String scelta = "";
        String temp;
        String temp1;
        int indice;

        while (!scelta.equals("STOP")) {
            if (id == 0) System.out.println(ANSI_YELLOW + "BENVENUTO NELLA FUNZIONE RICERCA!" + ANSI_RESET);
            System.out.println("Vuoi cercare un libro? Digita:\n(1) Per cercare un libro in base al" + ANSI_BLUE + " titolo" + ANSI_RESET + "\n(2) Per cercare un libro in base all'" + ANSI_BLUE + "autore" + ANSI_RESET + "\n(3) Per cercare un libro in base all'" + ANSI_BLUE + "autore" + ANSI_RESET + " e all'" + ANSI_BLUE + "anno di pubblicazione" + ANSI_RESET + "\nDigita (" + ANSI_RED + "STOP" + ANSI_RESET + ") per tornare indietro");
            scelta = s.nextLine().toUpperCase().trim().replaceAll(" ", "");

            switch (scelta) {
                case "1":
                    System.out.print("Inserisci il titolo del libro che vuoi cercare: ");
                    temp = s.nextLine();
                    indice = cerca(temp, 0, id);
                    if (id == 1) return indice;
                    break;
                case "2":
                    System.out.print("Inserisci il nome dell'autore che vuoi cercare: ");
                    temp = s.nextLine();
                    indice = cerca(temp, 1, id);
                    if (id == 1) return indice;
                    break;
                case "3":
                    System.out.print("Inserisci il nome dell'autore che vuoi cercare: ");
                    temp = s.nextLine();
                    System.out.print("Inserisci l'anno di produzione desiderato: ");
                    temp1 = s.nextLine();
                    indice = cercaA(temp, temp1, id);
                    if (id == 1) return indice;
                    break;
                case "STOP":
                    break;
                default:
                    System.out.println(ANSI_RED + "Scelta non valida! Riprova" + ANSI_RESET);
            }
        }
        if (id == 0) System.out.println(ANSI_PURPLE + "Ricerca conclusa! Grazie per aver utilizzato questo servizio" + ANSI_RESET);
        return 0;
    }

    /**
     * Cerca un libro in base a un parametro specificato (titolo o autore).
     *
     * @param temp Il valore da cercare (titolo o autore).
     * @param t    Indice del campo da cercare (0 per titolo, 1 per autore).
     * @param id   Identificatore per determinare il contesto della ricerca.
     * @return Viene richiamato il metodo {{@code risultato}} a cui vengono passati i parametri {@code int id} e {@code ArrayList<Integer> counter}
     */
    public static int cerca(String temp, int t, int id) {
        ArrayList<Integer> counter = new ArrayList<>();
        try (CSVReader reader = new CSVReaderBuilder(new FileReader(LibriData)).build()) {
            String[] nextLine;
            for (int j = 0; (nextLine = reader.readNext()) != null; j++) {
                if (nextLine[t].toLowerCase().contains(temp.toLowerCase())) {
                    counter.add(j);
                    System.out.println("(" + counter.size() + ")" + StampaInfoFull(nextLine));
                }
            }
            System.out.println("Ricerca Completata!");
        } catch (IOException | CsvValidationException ignored) {}
        return risultato(id, counter);
    }
 /**
 * Cerca un libro in base all'autore e all'anno di pubblicazione.
 *
 * @param autorea Nome dell'autore da cercare.
 * @param annoa   Anno di pubblicazione da cercare.
 * @param id      Identificatore per determinare il contesto della ricerca.
 * @return Viene richiamato il metodo {{@code risultato}} a cui vengono passati i parametri {@code int id} e {@code ArrayList<Integer> counter}
 */
public static int cercaA(String autorea, String annoa, int id) {
    ArrayList<Integer> counter = new ArrayList<>();
    try (CSVReader reader = new CSVReaderBuilder(new FileReader(LibriData)).build()) {
        String[] nextLine;
        for (int i = 0; (nextLine = reader.readNext()) != null; i++) {
            if (nextLine[1].toLowerCase().contains(autorea.toLowerCase()) && nextLine[7].toLowerCase().contains(annoa.toLowerCase())) {
                counter.add(i);
                System.out.println("(" + counter.size() + ")" + StampaInfoFull(nextLine));
            }
        }
        System.out.println("Ricerca Completata!");
    } catch (IOException | CsvValidationException ignored) {}
    return risultato(id, counter);
    }

    /**
     * Gestisce il risultato della ricerca, permettendo all'utente di visualizzare o selezionare un libro.
     *
     * @param id     Identificatore per determinare il contesto della ricerca.
     * @param counter Lista degli indici dei libri trovati.
     * @return L'indice del libro selezionato contenuto in {@code counter} o {@code 0} se non ci sono libri disponibili
     * o se la ricerca non richiede un output quando {@code id = 0}.
     */
    public static int risultato(int id, ArrayList<Integer> counter) {
        Scanner in = new Scanner(System.in);
        String scelta = "";
        if (!counter.isEmpty()) {
            while (!scelta.equals("N")) {
                if (id == 0) {
                    System.out.println("Vuoi visualizzare un libro? (" + ANSI_GREEN + "S" + ANSI_RESET + "/" + ANSI_RED + "N" + ANSI_RESET + ")");
                    scelta = in.nextLine().toUpperCase().trim().replaceAll(" ", "");
                    switch (scelta) {
                        case "S":
                            visualizzaLibro.Visualizza(counter);
                            scelta = "N";
                            break;
                        case "N":
                            break;
                        default:
                            System.out.println(ANSI_RED + "Inserimento errato! " + ANSI_RESET + "(" + ANSI_GREEN + "S" + ANSI_RESET + "/" + ANSI_RED + "N" + ANSI_RESET + ")");
                    }
                } else {
                    while (true) {
                        System.out.println("Inserisci l'indice del libro che vuoi aggiungere altrimenti digita (" + ANSI_RED + "STOP" + ANSI_RESET + ")");
                        scelta = in.nextLine().toUpperCase().trim().replaceAll(" ", "");
                        try {
                            if (scelta.equals("STOP")) {
                                return 0;
                            } else if (Integer.parseInt(scelta) > 0 && Integer.parseInt(scelta) <= counter.size()) {
                                return counter.get(Integer.parseInt(scelta) - 1);
                            } else {
                                System.out.println(ANSI_RED + "Inserimento errato! Inserire un numero compreso tra 1 e " + counter.size() + ANSI_RESET);
                            }
                        } catch (NumberFormatException e) {
                            System.err.println("Input non valido. Assicurati di inserire un numero intero.");
                            System.out.println("Inserisci un numero compreso tra 1 e " + counter.size());
                        }
                    }
                }
            }
        } else {
            System.out.println(ANSI_RED + "Impossibile cercare! Nessun risultato trovato!" + ANSI_RESET);
        }
        return 0;
    }
}