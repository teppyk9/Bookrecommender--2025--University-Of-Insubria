/*
Maffioli Gianmarco 757587 VA
Rolla Francesca 757922 VA
Borsani Nicolò 757584 VA
Isopo Silvia 757368 VA
Mariani Amati Federico 756811 VA
 */
package src.bookrecommender;

import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import static src.bookrecommender.BookRecommender.*;
/**
 * La classe {@code inserisciValutazioneLibro} consente agli utenti di valutare libri presenti nella loro libreria personale.
 * <p>
 * Gli utenti possono assegnare valutazioni su diversi aspetti di un libro, includere note facoltative
 * e salvare le valutazioni in un file CSV. La classe garantisce che ogni utente possa valutare un libro solo una volta.
 * </p>
 */

public class inserisciValutazioneLibro {
    /**
     * Permette agli utenti di valutare un libro specifico dalla loro libreria personale.
     * Funzionalità principali:
     * <p>- Consente all'utente di selezionare un libro tramite il suo indice nella lista.
     * <p>- Verifica se l'utente ha già valutato il libro selezionato.
     * <p>- Raccoglie valutazioni su vari aspetti del libro e calcola un voto finale.
     * <p>- Salva le valutazioni e le note in un file CSV specificato da `ValutazioniLibriData`.
     * @param Libri    una lista di libri disponibili nella libreria personale dell'utente
     * @param username il nome utente dell'utente che sta effettuando l'operazione
     */

    public static void ins (ArrayList<String> Libri, String username){
        Scanner in = new Scanner(System.in);
        String scelta = "";
        while(!scelta.equals("STOP")){
            System.out.println("Inserisci l'indice del libro che vuoi valutare altrimenti digita " + ANSI_RED + "STOP" + ANSI_RESET + ": ");
            scelta = in.nextLine().trim().toUpperCase().replaceAll(" ", "");
            if(!scelta.equals("STOP")) {
                try {
                    if (Integer.parseInt(scelta) > 0 && Integer.parseInt(scelta) <= Libri.size()) {
                        if (!esiste(Libri.get(Integer.parseInt(scelta) - 1), username, ValutazioniLibriData)) {
                            String[] stile = input("Stile");
                            String[] contenuto = input("Contenuto");
                            String[] gradevolezza = input("Gradevolezza");
                            String[] originalita = input("Originalita");
                            String[] edizione = input("Edizione");
                            String[] votofinale = new String[]{String.format("%.1f", (Integer.parseInt(stile[0]) + Integer.parseInt(contenuto[0]) + Integer.parseInt(gradevolezza[0]) + Integer.parseInt(originalita[0]) + Integer.parseInt(edizione[0])) / 5.0), input2()};
                            votofinale[0] = votofinale[0].replace(',','.');
                            try (CSVWriter scrittore = new CSVWriter(new FileWriter(ValutazioniLibriData, true))) {
                                String[] valutazione = new String[]{Libri.get(Integer.parseInt(scelta) - 1), username, stile[0], stile[1], contenuto[0], contenuto[1], gradevolezza[0], gradevolezza[1], originalita[0], originalita[1], edizione[0], edizione[1], votofinale[0], votofinale[1]};
                                scrittore.writeNext(valutazione);
                            } catch (IOException ignored) {
                            }
                            System.out.println(ANSI_BLUE + "Valutazione inserita con successo!" + ANSI_RESET);
                            scelta = "STOP";
                        } else {
                            System.out.println(ANSI_RED + "Hai già effettuato una valutazione per questo libro!" + ANSI_RESET);
                        }
                    } else {
                        System.out.println(ANSI_RED + "Inserimento errato! Inserire un valore compreso tra 1 e " + Libri.size() + ANSI_RESET);
                    }
                }catch (NumberFormatException e){
                    System.err.println("Input non valido. Assicurati di inserire un numero intero.");
                    System.out.println("Inserisci un numero compreso tra 1 e " + Libri.size());
                }
            }
        }
    }
    /**
     * Raccoglie una valutazione numerica e una nota facoltativa su un aspetto specifico di un libro.
     *
     * @param cat il nome dell'aspetto del libro da valutare (es. "Stile", "Contenuto", ecc.)
     * @return un array di due stringhe:
     *         - La prima stringa contiene la valutazione numerica (da 1 a 5).
     *         - La seconda stringa contiene una nota facoltativa (può essere vuota) di massimo 256 caratteri.
     */

    public static String [] input(String cat){
        Scanner in = new Scanner(System.in);
        String [] output = new String[2];
        String scelta = "";
        String nota;
        int valutazione;
        boolean flag = true;
        while(flag){
            System.out.println("Inserisci la valutazione (da 1 a 5) per " + ANSI_BLUE + cat + ANSI_RESET + ": ");
            valutazione = in.nextInt();
            in.nextLine();
            if(valutazione >= 1 && valutazione <= 5){
                output[0] = Integer.toString(valutazione);
                flag = false;
            }else System.out.println(ANSI_RED + "Inserimento errato! La valutazione deve essere da 1 a 5!" + ANSI_RESET);
        }
        flag = true;
        while(!scelta.equals("N")){
            System.out.println("Vuoi inserire una nota (MAX 256 CHARS) riguardo la tua valutazione? (" + ANSI_GREEN + "S" + ANSI_RESET + "/" + ANSI_RED + "N" + ANSI_RESET + ")");
            scelta = in.nextLine().trim().toUpperCase().replaceAll(" ", "");
            if(scelta.equals("S")) {
                while (flag){
                    System.out.println("Inserisci il tuo commento/nota: (MAX 256 CHARS)");
                    nota = in.nextLine();
                    if (nota.length() <= 256 && nota.length() > 1) {
                        output[1] = nota;
                        flag = false;
                    } else
                        System.out.println(ANSI_RED + "Inserimento errato! Inserire almeno 1 carattere e al massimo 256!" + ANSI_RESET);
                }
                scelta = "N";
            }else if(scelta.equals("N")){
                output[1] = "";
            }else{
                System.out.println(ANSI_RED + "Inserimento errato! Inserire " + ANSI_RESET + "(" + ANSI_GREEN + "S" + ANSI_RESET + "/" + ANSI_RED + "N" + ANSI_RESET + ")");
            }
        }
        return output;
    }
    /**
     * Raccoglie una nota facoltativa finale riguardo alla valutazione complessiva del libro.
     *
     * @return una {@code String} contenente la nota finale (può essere vuota se l'utente sceglie di non inserire una nota).
     */
    public static String input2(){
        Scanner in = new Scanner(System.in);
        String output = "";
        String scelta = "";
        boolean flag = true;
        while(!scelta.equals("N")){
            System.out.println("Vuoi inserire una nota finale (MAX 256 CHARS) riguardo la tua valutazione? (" + ANSI_GREEN + "S" + ANSI_RESET + "/" + ANSI_RED + "N" + ANSI_RESET + ")");
            scelta = in.nextLine().trim().toUpperCase().replaceAll(" ", "");
            if(scelta.equals("S")) {
                while (flag){
                    System.out.println("Inserisci il tuo commento/nota: (MAX 256 CHARS)");
                    output = in.nextLine();
                    if (output.length() <= 256 && output.length() > 1) {
                        flag = false;
                        scelta = "N";
                    }
                    else
                        System.out.println(ANSI_RED + "Inserimento errato! Inserire almeno 1 carattere e al massimo 256!" + ANSI_RESET);
                }
            }else if(!scelta.equals("N"))
                System.out.println(ANSI_RED + "Inserimento errato! Inserire " + ANSI_RESET + "(" + ANSI_GREEN + "S" + ANSI_RESET + "/" + ANSI_RED + "N" + ANSI_RESET + ")");
        }
        return output;
    }
}
