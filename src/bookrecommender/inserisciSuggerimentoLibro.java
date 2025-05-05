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
import java.util.Comparator;
import java.util.Scanner;

import static src.bookrecommender.BookRecommender.*;
/**
 * La classe {@code inserisciSuggerimentoLibro} consente agli utenti di inserire suggerimenti di libri associati a specifici libri presenti in una libreria personale.
 * <p>
 * La classe utilizza file CSV per gestire i dati di librerie e suggerimenti, e fornisce un'interfaccia interattiva per aggiungere suggerimenti.
 * Gli utenti possono aggiungere fino a tre suggerimenti per ogni libro, evitando duplicazioni e verificando la validità dei dati.
 * </p>
 */
public class inserisciSuggerimentoLibro{
    /**
     * Permette di associare suggerimenti di libri a un libro specifico selezionato da una libreria personale.
     <p>
     * Funzionalità principali:
     * <p>- Consente di selezionare un libro dalla libreria e associare fino a tre suggerimenti.
     * <p>- Verifica che i suggerimenti non includano il libro stesso o duplicati.
     * <p>- Salva i suggerimenti in un file CSV specificato da `ConsigliLibriData`.
     * @param Libri    una lista di libri disponibili nella libreria personale dell'utente
     * @param username il nome utente dell'utente che sta effettuando l'operazione
     */
    public static void ins(ArrayList<String> Libri, String username){
        Scanner in = new Scanner(System.in);
        ArrayList<String> output = new ArrayList<>();
        String scelta = "";
        String indice;
        String temp;
        boolean presente;
        while(!scelta.equals("STOP")){
            System.out.println("Inserisci l'indice del libro a cui vuoi associare dei consigli altrimenti digita " + ANSI_RED + "STOP" + ANSI_RESET + ": ");
            scelta = in.nextLine().trim().toUpperCase().replaceAll(" ", "");
            try {
                if (!scelta.equals("STOP") && Integer.parseInt(scelta) > 0 && Integer.parseInt(scelta) <= Libri.size()) {
                    if (!esiste(Libri.get(Integer.parseInt(scelta) - 1), username, ConsigliLibriData)) {
                        indice = Libri.get(Integer.parseInt(scelta) - 1);
                        output.add(suggerimento(username, 0));
                        while(indice.equals(output.get(0))){
                            System.out.println(ANSI_RED + "Non puoi inserire il libro stesso come suggerimento, riprova" + ANSI_RESET);
                            output.set(0, suggerimento(username, 0));
                        }
                        while (output.size() < 3 && !scelta.equals("N")) {
                            System.out.println("Vuoi aggiungere un altro libro? (" + ANSI_GREEN + "S" + ANSI_RESET + "/" + ANSI_RED + "N" + ANSI_RESET + ")");
                            scelta = in.nextLine().toUpperCase().trim().replaceAll(" ", "");
                            switch (scelta) {
                                case "S":
                                    temp = suggerimento(username, 1);
                                    presente = false;
                                    if (temp.equals("STOP")) {
                                        scelta = "N";
                                        break;
                                    }
                                    if(indice.equals(temp)){
                                        System.out.println(ANSI_RED + "Non puoi inserire il libro stesso come suggerimento, riprova" + ANSI_RESET);
                                        break;
                                    }
                                    for (String s : output) {
                                        if (s.equals(temp)) {
                                            presente = true;
                                            break;
                                        }
                                    }
                                    if (!presente) {
                                        output.add(temp);
                                    } else {
                                        System.out.println(ANSI_RED + "Hai già inserito questo libro nei suggerimenti, riprova" + ANSI_RESET);
                                    }
                                    break;
                                case "N":
                                    break;
                                default:
                                    System.out.println(ANSI_RED + "Inserimento errato! Inserire " + ANSI_RESET + "(" + ANSI_GREEN + "S" + ANSI_RESET + "/" + ANSI_RED + "N" + ANSI_RESET + ")");
                                    break;
                            }
                        }
                        output.sort(Comparator.comparingInt(Integer::parseInt));
                        String[] out = new String[output.size() + 2];
                        out[0] = indice;
                        out[1] = username;
                        for(int i = 2; i < out.length; i++){
                            out[i] = output.get(i - 2);
                        }
                        try (CSVWriter scrittore = new CSVWriter(new FileWriter(ConsigliLibriData, true))) {
                            scrittore.writeNext(out);
                        } catch (IOException ignored) {
                        }
                        scelta = "STOP";
                        System.out.println(ANSI_BLUE + "Inserimento concluso!" + ANSI_RESET);
                    } else {
                        System.out.println(ANSI_RED + "Hai già associato dei suggerimenti per questo libro! Riprova" + ANSI_RESET);
                    }
                } else if (!scelta.equals("STOP")) {
                    System.out.println(ANSI_RED + "Inserimento errato! Inserire un valore compreso tra 1 e " + Libri.size() + ANSI_RESET);
                }
            }catch (NumberFormatException e){
                System.err.println("Input non valido. Assicurati di inserire un numero intero.");
                System.out.println("Inserisci un numero compreso tra 1 e " + Libri.size());
            }
        }
    }

    /**
     * Fornisce un'interfaccia interattiva per selezionare un libro da utilizzare come suggerimento.
     *
     * @param username il nome utente dell'utente che sta effettuando l'operazione
     * @param id       determina il comportamento del metodo:
     *                 <p>- Se {@code 0}, richiede l'inserimento obbligatorio di un suggerimento.
     *                 <p>- Se {@code 1}, consente di terminare l'operazione digitando "STOP".
     * @return l'ID {@code String} del libro selezionato come suggerimento oppure "STOP" se l'operazione è interrotta dall'utente.
     */

    public static String suggerimento(String username, int id){
        Scanner in = new Scanner(System.in);
        ArrayList<String> libri = new ArrayList<>();
        String scelta;

        try (CSVReader reader = new CSVReaderBuilder(new FileReader(LibrerieData)).build()) {
            String[] nextLine;
            while((nextLine = reader.readNext()) != null) {
                for(int i = 2; (i < nextLine.length) && nextLine[0].equals(username); i++){
                    if(!libri.contains(nextLine[i])){
                        libri.add(nextLine[i]);
                    }
                }
            }
        } catch (IOException | CsvValidationException ignored) {}
        libri.sort(Comparator.comparingInt(Integer::parseInt));

        try (CSVReader reader = new CSVReaderBuilder(new FileReader(LibriData)).build()) {
            String[] nextLine;
            for (int i = 0, k = 0; (nextLine = reader.readNext()) != null; i++) {
                if(libri.get(k).equals(String.valueOf(i))){
                    k++;
                    System.out.println("(" + k + ")" + StampaInfoFull(nextLine));
                    if(k == libri.size()){
                        break;
                    }
                }
            }
        } catch (IOException | CsvValidationException ignored) {}
        while(true) {
            if(id == 0) {
                try {
                    System.out.println("Inserisci l'indice del libro che vuoi aggiungere come suggerimento (Devi inserirne almeno uno!):");
                    scelta = in.nextLine().toUpperCase().trim().replaceAll(" ", "");
                    if (Integer.parseInt(scelta) > 0 && Integer.parseInt(scelta) <= libri.size()) {
                        return libri.get(Integer.parseInt(scelta) - 1);
                    } else {
                        System.out.println(ANSI_RED + "Input non valido!" + ANSI_RESET + "\nInserisci un numero compreso tra 1 e " + libri.size());
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Input non valido. Assicurati di inserire un numero intero.");
                    System.out.println("Inserisci un numero compreso tra 1 e " + libri.size());
                }
            }else{
                try {
                    System.out.println("Inserisci l'indice del libro che vuoi aggiungere come suggerimento altrimenti digita (" + ANSI_RED + "STOP" + ANSI_RESET + ")");
                    scelta = in.nextLine().toUpperCase().trim().replaceAll(" ", "");
                    if (scelta.equals("STOP")) {
                        return scelta;
                    } else if (Integer.parseInt(scelta) > 0 && Integer.parseInt(scelta) <= libri.size()){
                        return libri.get(Integer.parseInt(scelta) - 1);
                    }else{
                        System.out.println(ANSI_RED + "Input non valido!" + ANSI_RESET + "\nInserisci un numero compreso tra 1 e " + libri.size() + " altrimenti digita (" + ANSI_RED + "STOP" + ANSI_RESET + ")");
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Input non valido. Assicurati di inserire un numero intero.");
                    System.out.println("Inserisci un numero compreso tra 1 e " + libri.size());
                }
            }
        }
    }
}
