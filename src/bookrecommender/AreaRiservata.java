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
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import static src.bookrecommender.BookRecommender.*;


/**
 * La classe {@code AreaRiservata} che gestisce l'interazione dell'utente con l'area riservata del sistema.
 * Fornisce funzionalità per autenticare gli utenti, visualizzare librerie, creare librerie e cercare libri.
 */
public class AreaRiservata {
    /**
     * Metodo principale che avvia l'interazione con l'area riservata.
     * Permette l'accesso autenticato e fornisce opzioni per navigare nel sistema.
     */
    public static void start() {
        Scanner in = new Scanner(System.in);
        String temp = "";
        System.out.println(ANSI_YELLOW + "BENVENUTO NELL'AREA RISERVATA!" + ANSI_RESET);
        while (!temp.equals("STOP")) {
            if (temp.equals("S"))
                System.out.println("Se vuoi procedere con un nuovo accesso digita (" + ANSI_GREEN + "S" + ANSI_RESET + ") altrimenti digita (" + ANSI_RED + "STOP" + ANSI_RESET + ")");
            else
                System.out.println("Se vuoi procedere con l'accesso digita (" + ANSI_GREEN + "S" + ANSI_RESET + ") altrimenti digita (" + ANSI_RED + "STOP" + ANSI_RESET + ")");
            temp = in.nextLine().toUpperCase().trim().replaceAll(" ", "");
            switch (temp) {
                case "S":
                    System.out.print("Inserisci lo username: ");
                    String temp1 = in.nextLine();
                    System.out.print("Inserisci la password: ");
                    String temp2 = in.nextLine();
                    if (VerificaCredenziali(temp1, temp2)) {
                        System.out.println(ANSI_PURPLE + "Utente Confermato! Benvenuto" + ANSI_RESET);
                        home(temp1);
                    } else
                        System.out.println(ANSI_RED + "Username o password errate!" + ANSI_RESET);
                    break;
                case "STOP":
                    break;
                default:
                    System.out.println(ANSI_RED + "Scelta non trovata!" + ANSI_RESET);
            }
        }
    }
    /**
     * Verifica le credenziali dell'utente confrontandole con quelle archiviate.
     *
     * @param username Nome utente inserito dall'utente.
     * @param password Password inserita dall'utente.
     * @return {@code true} se le credenziali sono valide, {@code false} altrimenti.
     */
    public static boolean VerificaCredenziali(String username, String password) {
        try (CSVReader reader = new CSVReaderBuilder(new FileReader(UtentiRegistratiData)).build()) {
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                if (nextLine[4].equals(username) && nextLine[5].equals(password)) {
                    return true;
                }
            }
        } catch (IOException | CsvValidationException ignored) {
        }
        return false;
    }
    /**
     * Mostra il menu principale dell'utente autenticato e permette di navigare tra le opzioni.
     *
     * @param username Nome utente dell'utente autenticato.
     */
    public static void home(String username) {
        Scanner in = new Scanner(System.in);
        String temp = "";
        while (!temp.equals("STOP")) {
            System.out.println("Cosa vuoi fare? Digita:\n(1) Per visualizzare le tue librerie\n(2) Per creare una nuova libreria\n(3) Per cercare un libro\n(" + ANSI_RED + "STOP" + ANSI_RESET + ") Se vuoi disconnetterti");
            temp = in.nextLine().toUpperCase().trim().replaceAll(" ", "");
            switch (temp) {
                case "1":
                    visualizzalibreria(username);
                    break;
                case "2":
                    registraLibreria(username);
                    break;
                case "3":
                    cercaLibro.cerca(0);
                    break;
                case "STOP":
                    break;
                default:
                    System.out.println(ANSI_RED + "Inserimento errato!" + ANSI_RESET);
            }
        }
    }
    /**
     * Permette la creazione di una nuova libreria associata all'utente autenticato.
     *
     * @param username Nome utente dell'utente autenticato.
     */
    public static void registraLibreria(String username) {
        Scanner in = new Scanner(System.in);
        String titolo = "";
        while (!titolo.equals("STOP")) {
            System.out.print("Inserisci il nome della libreria che vuoi creare altrimenti digita (" + ANSI_RED + "STOP" + ANSI_RESET + "): ");
            titolo = in.nextLine().toUpperCase().trim().replaceAll(" ", "");
            if (!titolo.equals("STOP")) {
                if (esiste(titolo, username, LibrerieData)) {
                    System.out.println(ANSI_RED + "La libreria <" + titolo + "> esiste già, prova ancora" + ANSI_RESET);
                } else {
                    try (CSVWriter scrittore = new CSVWriter(new FileWriter(LibrerieData, true))) {
                        String[] libreria = crealibreria(titolo, username);
                        scrittore.writeNext(libreria);
                    } catch (IOException ignored) {
                    }
                    System.out.println(ANSI_BLUE + "Libreria aggiunta correttamente!" + ANSI_RESET);
                    titolo = "STOP";
                }
            }
        }
    }
    /**
     * Crea una nuova libreria associata a un utente specifico e consente di aggiungere libri alla libreria.
     * <p>
     * Questo metodo consente all'utente di creare una libreria fornendo un titolo e associandola al proprio account.
     * Durante il processo, l'utente deve aggiungere almeno un libro e può continuare ad aggiungerne altri fino a quando non decide di terminare.
     * I libri aggiunti sono identificati tramite un sistema di ricerca.
     * </p>
     *
     * @param titolo   il titolo della libreria che l'utente desidera creare
     * @param username il nome utente a cui la libreria sarà associata
     * @return un {@code ArrayList<String>} contenente i dati della libreria:
     *         <p>- La prima posizione contiene il nome utente.
     *         <p>- La seconda posizione contiene il titolo della libreria.
     *         <p>- Le posizioni successive contengono gli ID dei libri aggiunti, ordinati in ordine crescente.
     */
    public static String[] crealibreria(String titolo, String username){
        Scanner in = new Scanner(System.in);
        ArrayList<String> lista = new ArrayList<>();
        String scelta = "S";
        String temp;
        lista.add(username);
        lista.add(titolo);
        System.out.println(ANSI_YELLOW + "BENVENUTO NELLA FUNZIONE DI CREAZIONE LIBRERIE!" + ANSI_RESET);
        temp = String.valueOf(cercaLibro.cerca(1));
        while(temp.equals("0")){
            System.out.println(ANSI_RED + "La ricerca non ha prodotto nessuna selezione, è necessario inserire almeno 1 libro" + ANSI_RESET);
            temp = String.valueOf(cercaLibro.cerca(1));
        }
        lista.add(temp);
        while(!scelta.equals("N")) {
            System.out.println("Vuoi aggiungere un altro libro? (" + ANSI_GREEN + "S" + ANSI_RESET + "/" + ANSI_RED + "N" + ANSI_RESET + ")");
            scelta = in.nextLine().toUpperCase().trim().replaceAll(" ", "");
            switch (scelta) {
                case "S":
                    temp = String.valueOf(cercaLibro.cerca(1));
                    while(temp.equals("0")){
                        System.out.println(ANSI_RED + "La ricerca non ha prodotto nessuna selezione, riprova" + ANSI_RESET);
                        temp = String.valueOf(cercaLibro.cerca(1));
                    }
                    if(!lista.contains(temp))
                        lista.add(temp);
                    else{
                        System.out.println(ANSI_RED + "Hai gia inserito questo libro!" + ANSI_RESET);
                    }
                    break;
                    case "N":
                        break;
                        default:
                            System.out.println(ANSI_RED + "Scelta errata! " + ANSI_RESET + "(" + ANSI_GREEN + "S" + ANSI_RESET + "/" + ANSI_RED + "N" + ANSI_RESET + ")");
            }

        }
        String[] output = new String[lista.size()];
        for(int i=0; i<lista.size(); i++)
            output[i] = lista.get(i);
        if (output.length > 3) {
            ArrayList<Integer> numeri = new ArrayList<>();
            for (int i = 2; i < output.length; i++) {
                numeri.add(Integer.parseInt(output[i]));
            }
            Collections.sort(numeri);
            for (int i = 2; i < output.length; i++) {
                output[i] = String.valueOf(numeri.get(i - 2));
            }
        }
        return output;
    }
    /**
     * Visualizza le librerie associate a un utente specifico e consente di interagire con esse.
     * <p>
     * Questo metodo permette all'utente di visualizzare le librerie associate al suo account,
     * scegliere una libreria da esplorare, e interagire con i libri presenti nella libreria
     * selezionata. Se l'utente non ha librerie, viene offerta la possibilità di crearne una.
     * </p>
     * @param username il nome utente dell'utente le cui librerie devono essere visualizzate
     */
    public static void visualizzalibreria(String username) {
        Scanner in = new Scanner(System.in);
        ArrayList<Integer> poslib = new ArrayList<>(); //Salva gli indici della posizione delle librerie associate all'utente all'interno del file Librerie.dati
        ArrayList<String> libri = new ArrayList<>(); //Salva gli indici dei libri presenti nella libreria associata
        String scelta = "";
        boolean flag = true;
        try (CSVReader reader = new CSVReaderBuilder(new FileReader(LibrerieData)).build()) {
            String[] nextLine;
            for(int i = 0; (nextLine = reader.readNext()) != null; i++) {
                if(nextLine[0].equals(username)){
                    poslib.add(i);
                    System.out.println("(" + poslib.size() + ")" + " " + nextLine[1]);
                }
            }
        } catch (IOException | CsvValidationException ignored) {}

        if(poslib.isEmpty()){
            System.out.println(ANSI_RED + "Non hai nessuna libreria! Vuoi crearne una? " + ANSI_RESET + "(" + ANSI_GREEN + "S" + ANSI_RESET + "/" + ANSI_RED + "N" + ANSI_RESET + ")");
            scelta = in.nextLine().toUpperCase().trim().replaceAll(" ", "");
            while(flag){
                switch (scelta) {
                    case "S":
                        registraLibreria(username);
                        flag = false;
                        break;
                    case "N":
                        flag = false;
                        break;
                    default:
                        System.out.println(ANSI_RED + "Inserimento errato! Vuoi crearne una? " + ANSI_RESET + "(" + ANSI_GREEN + "S" + ANSI_RESET + "/" + ANSI_RED + "N" + ANSI_RESET + ")");
                        scelta = in.nextLine().toUpperCase().trim().replaceAll(" ", "");
                        break;
                }
            }
        }else{
            while(!scelta.equals("STOP")){
                System.out.println("Inserisci l'indice della libreria che vuoi visualizzare altrimenti digita (" + ANSI_RED + "STOP" + ANSI_RESET + ")");
                scelta = in.nextLine().toUpperCase().trim().replaceAll(" ", "");
                try {
                    if (!scelta.equals("STOP") && Integer.parseInt(scelta) > 0 && Integer.parseInt(scelta) <= poslib.size()) {
                        try (CSVReader reader = new CSVReaderBuilder(new FileReader(LibrerieData)).build()) {
                            String[] nextLine;
                            for (int i = 0; (nextLine = reader.readNext()) != null; i++) { //Per ogni libro presente in libreria lo ricerca all'interno di Libri.dati
                                if (i == poslib.get(Integer.parseInt(scelta) - 1)) {
                                    System.out.println("La libreria " + ANSI_PURPLE + nextLine[1] + ANSI_RESET + " è composta da:");
                                    for (int j = 2; j < nextLine.length; ) {
                                        try (CSVReader reader2 = new CSVReaderBuilder(new FileReader(LibriData)).build()) {
                                            String[] nextLine2;
                                            for (int k = 0; (nextLine2 = reader2.readNext()) != null && j != nextLine.length; k++) {
                                                if (k == Integer.parseInt(nextLine[j])) {
                                                    System.out.println("(" + (j - 1) + ")" + StampaInfoFull(nextLine2));
                                                    libri.add(nextLine[j]);
                                                    j++;
                                                }
                                            }
                                        } catch (IOException | CsvValidationException ignored) {
                                        }
                                    }
                                }
                            }
                        } catch (IOException | CsvValidationException ignored) {
                        }
                        scelta = "";
                        while (!scelta.equals("N")) {
                            System.out.println("Vuoi aggiungere una valutazione ad uno di questi libri? (" + ANSI_GREEN + "S" + ANSI_RESET + "/" + ANSI_RED + "N" + ANSI_RESET + ")");
                            scelta = in.nextLine().toUpperCase().trim().replaceAll(" ", "");
                            if (scelta.equals("S"))
                                inserisciValutazioneLibro.ins(libri, username);
                            else if (!scelta.equals("N"))
                                System.out.println(ANSI_RED + "Inserimento errato! Inserire " + ANSI_RESET + "(" + ANSI_GREEN + "S" + ANSI_RESET + "/" + ANSI_RED + "N" + ANSI_RESET + ")");
                        }
                        scelta = "";
                        while (!scelta.equals("N")) {
                            System.out.println("Vuoi aggiungere un suggerimento di lettura a uno di questi libri? (" + ANSI_GREEN + "S" + ANSI_RESET + "/" + ANSI_RED + "N" + ANSI_RESET + ")");
                            scelta = in.nextLine().toUpperCase().trim().replaceAll(" ", "");
                            if (scelta.equals("S"))
                                inserisciSuggerimentoLibro.ins(libri, username);
                            else if (!scelta.equals("N"))
                                System.out.println(ANSI_RED + "Inserimento errato! Inserire " + ANSI_RESET + "(" + ANSI_GREEN + "S" + ANSI_RESET + "/" + ANSI_RED + "N" + ANSI_RESET + ")");
                        }
                        scelta = "STOP";
                    } else if (!scelta.equals("STOP")) {
                        System.out.println(ANSI_RED + "Inserimento errato!" + ANSI_RESET);
                        System.out.println("Inserisci un numero compreso tra 1 e " + poslib.size() + ", altrimenti digita (" + ANSI_RED + "STOP" + ANSI_RESET + ")");
                    }
                }catch (NumberFormatException e){
                    System.err.println("Input non valido. Assicurati di inserire un numero intero.");
                    System.out.println("Inserisci un numero compreso tra 1 e " + poslib.size() + ", altrimenti digita (" + ANSI_RED + "STOP" + ANSI_RESET + ")");
                }
            }
        }
    }
}