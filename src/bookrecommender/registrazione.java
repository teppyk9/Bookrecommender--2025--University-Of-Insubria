/*
Maffioli Gianmarco 757587 VA
Rolla Francesca 757922 VA
Borsani Nicolò 757584 VA
Isopo Silvia 757368 VA
Mariani Amati Federico 756811 VA
 */
package src.bookrecommender;

import java.io.*;
import java.lang.*;
import java.util.Scanner;
import java.util.regex.Pattern;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;

import static src.bookrecommender.BookRecommender.*;
/**
 * La classe {@code registrazione} gestisce il processo di registrazione di nuovi utenti.
 * Permette di raccogliere informazioni personali come nome, cognome, codice fiscale, email, nome utente e password,
 * e di salvarle in un file CSV per tenere traccia degli utenti registrati.
 * Gli utenti possono registrarsi selezionando l'opzione appropriata dal menu di registrazione, oppure interrompere il processo digitando "STOP".
 */

public class registrazione {
    /**
     * Gestisce il menu di registrazione per gli utenti, permettendo loro di inserire i propri dati.
     * Se l'utente sceglie di continuare con la registrazione, vengono richiesti nome, cognome, codice fiscale,
     * email, nome utente e password. I dati vengono quindi salvati in un file CSV.
     * Se l'utente sceglie "STOP", il processo di registrazione termina.
     */

    public static void reg() {
        Scanner scanner = new Scanner(System.in);
        String scelta = "";
        while(!scelta.equals("STOP")){
            System.out.println(ANSI_YELLOW + "BENVENUTO NEL MENU' DI REGISTRAZIONE!" + ANSI_RESET);
            System.out.println("Se desideri continuare con una nuova registrazione digita (" + ANSI_GREEN + "S" + ANSI_RESET + ") altrimenti digita (" + ANSI_RED + "STOP" + ANSI_RESET + ")");
            scelta = scanner.nextLine().toUpperCase().trim().replaceAll(" ", "");
            switch(scelta){
                case "S":
                    // Richiesta dei dati all'utente
                    String nome = chiediInput(scanner, "Nome");
                    String cognome = chiediInput(scanner, "Cognome");
                    String cf = chiediCodiceFiscale(scanner);
                    String mail = chiediEmail(scanner);
                    String userName = chiediInput(scanner, "UserName");
                    String password = chiediInput(scanner, "Password");
                        // Creazione della stringa da salvare nel file CS
                    try (CSVWriter scrittore = new CSVWriter(new FileWriter(UtentiRegistratiData, true))) {
                        String[] DatiUtente = new String[]{nome, cognome, cf, mail, userName, password};
                        scrittore.writeNext(DatiUtente);
                    } catch (IOException ignored) {}
                    System.out.println(ANSI_PURPLE + "Utente registrato con successo!" + ANSI_RESET);
                    break;
                    case "STOP":
                        break;
                        default:
                            System.out.println(ANSI_RED + "Scelta non trovata!" + ANSI_RESET);

            }

        }

    }
    /**
     * Verifica se un nome utente è già presente nel file CSV degli utenti registrati.
     * Se il nome utente è già in uso, restituisce {@code false}, altrimenti {@code true}.
     *
     * @param userName Il nome utente da verificare.
     * @return {@code true} se il nome utente è disponibile, {@code false} se è già stato registrato.
     */

    public static boolean UtenteDisponibile(String userName) {
        try (CSVReader reader = new CSVReaderBuilder(new FileReader(UtentiRegistratiData)).build()) {
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null)
                if (nextLine[4].equals(userName))
                    return false;
        } catch(IOException ignored) {} catch (CsvValidationException e) {
            System.err.println("CSV validation error: " + e.getMessage());
        }
        return true;
    }
    /**
     * Richiede all'utente di inserire un valore per un determinato campo (ad esempio, nome, cognome, username, ecc.).
     * Se l'utente inserisce un valore troppo corto o un nome utente già esistente, viene mostrato un messaggio di errore.
     *
     * @param scanner L'oggetto {@link Scanner} utilizzato per leggere l'input dell'utente.
     * @param messaggio Il messaggio da visualizzare all'utente per indicare quale valore inserire.
     * @return Il valore {@code String} inserito dall'utente, se valido.
     */

    public static String chiediInput(Scanner scanner, String messaggio) {
        String temp;
        while(true) {
            System.out.print(messaggio + ": ");
            temp = scanner.nextLine();
            if (temp.length() < 2) {
                System.out.println(ANSI_RED + "Inserimento errato! " + messaggio + " deve essere almeno di 2 caratteri!" + ANSI_RESET);
            } else if (messaggio.equals("UserName")) {
                if (UtenteDisponibile(temp)) {
                    return temp;
                } else {
                    System.out.println(ANSI_RED + "Nome utente non disponibile!" + ANSI_RESET);
                }
            } else {
                return temp;
            }
        }
    }
    /**
     * Richiede all'utente di inserire un codice fiscale valido. Il codice fiscale deve essere lungo 16 caratteri e
     * rispettare un formato specifico (6 lettere, 2 numeri, 1 lettera, 2 numeri, 1 lettera, 3 numeri, 1 lettera).
     * Se il codice fiscale non è valido, viene mostrato un messaggio di errore e viene chiesto di riprovare.
     *
     * @param scanner L'oggetto {@link Scanner} utilizzato per leggere l'input dell'utente.
     * @return Il codice fiscale {@code String} inserito dall'utente, se valido.
     */

    public static String chiediCodiceFiscale(Scanner scanner) {
        String cf;
        while (true) {
            System.out.print("Codice Fiscale: ");
            cf = scanner.nextLine().trim().toUpperCase();
            if (cf.length() != 16) {
                System.out.println(ANSI_RED + "Errore: Codice fiscale sbagliato, deve essere lungo 16 caratteri." + ANSI_RESET);
                continue;
            }
            String regex = "^[A-Z]{6}[0-9]{2}[A-Z][0-9]{2}[A-Z][0-9]{3}[A-Z]$";
            if (!cf.matches(regex)) {
                System.out.println(ANSI_RED + "Errore: Codice fiscale sbagliato, deve avere un formato valido." + ANSI_RESET);
                continue;
            }
            break;
        }
        return cf;
    }
    /**
     * Richiede all'utente di inserire un indirizzo email valido. L'email deve rispettare un formato standard
     * (ad esempio, "utente@dominio.com"). Se l'email non è valida, viene mostrato un messaggio di errore
     * e viene chiesto di riprovare.
     *
     * @param scanner L'oggetto {@link Scanner} utilizzato per leggere l'input dell'utente.
     * @return L'indirizzo email {@code String} inserito dall'utente, se valido.
     */

    public static String chiediEmail(Scanner scanner) {
        String email;
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"; //caratteri che può avere un email
        Pattern pattern = Pattern.compile(emailRegex);  //Questo metodo prende l'espressione regolare definita in emailRegex e la compila in un oggetto Pattern.

        while (true) {
            System.out.print("Email: ");
            email = scanner.nextLine().trim().replaceAll(" ", "");
            if (pattern.matcher(email).matches()) { /*crea un oggetto Matcher che confronta l
                    'email fornita con il pattern definito, .matches() restituisce true se l' email corrisponde
                    al pattern*/
                break;
            } else {
                System.out.println(ANSI_RED + "Errore: l'email inserita non è valida" + ANSI_RESET);
            }
        }
        return email;
    }
}