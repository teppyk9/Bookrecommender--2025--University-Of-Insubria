package bookrecommender.server;

import bookrecommender.common.*;

import java.io.Serial;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementazione dell'interfaccia remota {@link SearchInterface}, che consente ai client
 * di effettuare ricerche sui libri memorizzati nel database.
 * Questa classe viene esposta via RMI dal server e consente operazioni come:
 * ricerca per titolo, autore, anno, recupero dettagli di un libro e verifica
 * della presenza di valutazioni o consigli.
 * Ogni metodo è accompagnato da logging server-side per il monitoraggio delle richieste.
 */
public class SearchInterfaceImpl extends UnicastRemoteObject implements SearchInterface {
    /**UID per la serializzazione RMI.*/
    @Serial
    private static final long serialVersionUID = 1L;

    /**Logger utilizzato per registrare le operazioni eseguite dal client. */
    private final Logger logger;

    /**
     * Costruttore dell'implementazione remota dell'interfaccia di ricerca.
     * @throws RemoteException se si verifica un errore nell'esportazione dell'oggetto remoto.
     */
    protected SearchInterfaceImpl() throws RemoteException {
        super();
        this.logger = Logger.getLogger(LogRegInterfaceImpl.class.getName());
    }

    /**
     * Restituisce un oggetto {@link Libro} dato il suo ID univoco.
     * @param id identificatore del libro.
     * @return l'oggetto {@link Libro} corrispondente all'ID.
     * @throws RemoteException se si verifica un errore di comunicazione RMI.
     */
    @Override
    public Libro getLibro(int id) throws RemoteException {
        try {
            logger.info("Searching book with ID: " + id + " From client " + getClientHost());
        }catch (ServerNotActiveException ignored){}
        return ServerUtil.getInstance().getLibro(id);
    }

    /**
     * Cerca libri che corrispondono al titolo specificato.
     * @param title titolo del libro da cercare.
     * @return lista di libri con titolo corrispondente.
     * @throws RemoteException se si verifica un errore di comunicazione RMI.
     */
    @Override
    public List<Libro> searchByName(String title) throws RemoteException {
        try {
            logger.info("Searching for books with title: " + title + " From client " + getClientHost());
        }catch (ServerNotActiveException ignored){}
        List<Libro> risultati = new ArrayList<>();
        String query = "SELECT * FROM LIBRI WHERE LOWER(TITOLO) LIKE LOWER(?)";
        return libroTypeSearch(title, risultati, query);
    }

    /**
     * Cerca libri scritti da un determinato autore.
     * @param author nome dell'autore.
     * @return lista di libri dell'autore specificato.
     * @throws RemoteException se si verifica un errore di comunicazione RMI.
     */
    @Override
    public List<Libro> searchByAuthor(String author) throws RemoteException {
        try {
            logger.info("Searching for books by author: " + author + " From client " + getClientHost());
        }catch (ServerNotActiveException ignored){}
        List<Libro> risultati = new ArrayList<>();
        String query = "SELECT * FROM LIBRI WHERE LOWER(AUTORE) LIKE LOWER(?)";
        return libroTypeSearch(author, risultati, query);
    }

    private List<Libro> libroTypeSearch(String title, List<Libro> risultati, String query) {
        try(
                Connection conn = ServerUtil.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setString(1, "%" + title + "%");
            ServerUtil.getInstance().resultStmt(risultati, stmt);
            return risultati;
        } catch (SQLException e) {
            logger.warning("Errore nella connessione al database: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Cerca libri scritti da un determinato autore in un determinato anno.
     * @param author nome dell'autore.
     * @param year   anno di pubblicazione.
     * @return lista di libri che soddisfano i criteri.
     * @throws RemoteException se si verifica un errore di comunicazione RMI.
     */
    @Override
    public List<Libro> searchByAuthorAndYear(String author, int year) throws RemoteException {
        try {
            logger.info("Searching for books by author: " + author + " and year: " + year + " From client " + getClientHost());
        }catch (ServerNotActiveException ignored){}
        List<Libro> risultati = new ArrayList<>();
        String query = "SELECT * FROM LIBRI WHERE LOWER(AUTORE) LIKE LOWER(?) AND CAST(ANNOPUBBLICAZIONE AS TEXT) LIKE ?";

        try (Connection conn = ServerUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + author + "%");
            stmt.setString(2, "%" + year + "%");

            ServerUtil.getInstance().resultStmt(risultati, stmt);

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nella ricerca di un libro", e);
        }
        return risultati;
    }

    /**
     * Restituisce i dettagli completi di un libro selezionato.
     * @param libro oggetto {@link Libro} di cui si vogliono i dettagli.
     * @return oggetto {@link Libro_Details} con le informazioni dettagliate.
     * @throws RemoteException se si verifica un errore di comunicazione RMI.
     */
    @Override
    public Libro_Details getDetails(Libro libro) throws RemoteException {
        try{
            logger.info("Getting details for book with ID: " + libro.getId() + " From client " + getClientHost());
        } catch (ServerNotActiveException ignored) {}
        List<Valutazione> valutazioni = new ArrayList<>();
        Hashtable<String, List<Libro>> consigli = new Hashtable<>();

        String queryValutazioni = """
            SELECT u.username, v.v_stile, v.c_stile, v.v_contenuto, v.c_contenuto,
                   v.v_gradevolezza, v.c_gradevolezza, v.v_originalita, v.c_originalita,
                   v.v_edizione, v.c_edizione, v.v_finale, v.c_finale
            FROM valutazioni v
            JOIN utenti u ON v.id_utente = u.id
            WHERE v.idlibro = ?
        """;

        try (Connection conn = ServerUtil.getInstance().getConnection();
             PreparedStatement psValutazioni = conn.prepareStatement(queryValutazioni)) {
            psValutazioni.setInt(1, libro.getId());
            ResultSet rs = psValutazioni.executeQuery();
            while (rs.next()) {
                List<Float> valori = ServerUtil.getInstance().getVotiVal(rs);
                List<String> commenti = ServerUtil.getInstance().getComVal(rs);
                valutazioni.add(new Valutazione(rs.getString("username"), valori, commenti, libro));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nel recupero delle valutazioni per il libro con ID: " + libro.getId(), e);
        }

        String queryConsigli = """
            SELECT u.username, l.id, l.titolo, l.autore, l.descrizione, l.categoria,
                   l.editore, l.prezzo, l.annopubblicazione, l.mesepubblicazione
            FROM consigli c
            JOIN utenti u ON c.id_utente = u.id
            JOIN libri l ON l.id IN (c.lib_1, c.lib_2, c.lib_3)
            WHERE c.idlibro = ?
        """;

        try (Connection conn = ServerUtil.getInstance().getConnection();
             PreparedStatement psConsigli = conn.prepareStatement(queryConsigli)) {
            psConsigli.setInt(1, libro.getId());
            ResultSet rs = psConsigli.executeQuery();
            while (rs.next()) {
                String username = rs.getString("username");
                Libro libroC = new Libro(
                        rs.getInt("id"),
                        rs.getString("titolo"),
                        rs.getString("autore"),
                        rs.getString("descrizione"),
                        rs.getString("categoria"),
                        rs.getString("editore"),
                        rs.getFloat("prezzo"),
                        rs.getShort("annopubblicazione"),
                        rs.getShort("mesepubblicazione")
                );
                consigli.computeIfAbsent(username, k -> new ArrayList<>()).add(libroC);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nel recupero dei consigli per il libro con ID: " + libro.getId(), e);
        }

        return new Libro_Details(consigli, valutazioni);
    }

    /**
     * Cerca libri per titolo, tenendo conto del token utente per eventuali filtri o preferenze.
     * @param token token utente autenticato.
     * @param title titolo da cercare.
     * @return lista di libri corrispondenti.
     * @throws RemoteException se si verifica un errore di comunicazione RMI.
     */
    public List<Libro> searchByName(Token token, String title) throws RemoteException {
        try{
            logger.info("Searching for books with title: " + title + " From client " + getClientHost());
        } catch (ServerNotActiveException ignored) {}
        if (ServerUtil.getInstance().isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.getToken() + " utente di id " + token.getUserId() + " IP:" + token.getIpClient());
            return null;
        }
        List<Libro> risultati = new ArrayList<>();
        String query = """
                SELECT DISTINCT l.*
                FROM libri AS l
                JOIN libreria_libro AS ll
                ON ll.idlibro = l.id
                JOIN librerie AS lr
                ON lr.id = ll.idlibreria
                WHERE lr.id_utente = ?
                AND l.titolo ILIKE '%' || ? || '%';""";
        try (Connection conn = ServerUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, token.getUserId());
            stmt.setString(2, title);
            ServerUtil.getInstance().resultStmt(risultati, stmt);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nella ricerca di un libro con titolo: " + title, e);
            return null;
        }
        return risultati;
    }

    /**
     * Cerca libri per autore, tenendo conto del token utente.
     * @param token  token utente autenticato.
     * @param author autore da cercare.
     * @return lista di libri corrispondenti.
     * @throws RemoteException se si verifica un errore di comunicazione RMI.
     */
    public List<Libro> searchByAuthor(Token token, String author) throws RemoteException {
        try{
            logger.info("Searching for books with author: " + author + " From client " + getClientHost());
        } catch (ServerNotActiveException ignored) {}
        if (ServerUtil.getInstance().isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.getToken() + " utente di id " + token.getUserId() + " IP:" + token.getIpClient());
            return null;
        }
        List<Libro> risultati = new ArrayList<>();
        String query = """
                SELECT DISTINCT l.*
                FROM libri AS l
                JOIN libreria_libro AS ll
                ON ll.idlibro = l.id
                JOIN librerie AS lr
                ON lr.id = ll.idlibreria
                WHERE lr.id_utente = ?
                AND LOWER(l.autore) LIKE LOWER(?);""";
        try (Connection conn = ServerUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, token.getUserId());
            stmt.setString(2, "%" + author + "%");
            ServerUtil.getInstance().resultStmt(risultati, stmt);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nella ricerca di libri per autore: " + author, e);
            return null;
        }
        return risultati;
    }

    /**
     * Cerca libri per autore e anno, con token utente.
     * @param token  token utente autenticato.
     * @param author autore del libro.
     * @param year   anno di pubblicazione.
     * @return lista di libri corrispondenti.
     * @throws RemoteException se si verifica un errore di comunicazione RMI.
     */
    public List<Libro> searchByAuthorAndYear(Token token, String author, int year) throws RemoteException {
        try {
            logger.info("Searching for books by author: " + author + " and year: " + year + " From client " + getClientHost());
        }catch (ServerNotActiveException ignored){}
        if (ServerUtil.getInstance().isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.getToken() + " utente di id " + token.getUserId() + " IP:" + token.getIpClient());
            return null;
        }
        List<Libro> risultati = new ArrayList<>();
        String query = """
                SELECT DISTINCT l.*
                FROM libri AS l
                JOIN libreria_libro AS ll
                ON ll.idlibro = l.id
                JOIN librerie AS lr
                ON lr.id = ll.idlibreria
                WHERE lr.id_utente = ?
                AND LOWER(l.autore) LIKE LOWER(?)
                AND CAST(l.annopubblicazione AS TEXT) LIKE ?;""";
        try (Connection conn = ServerUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, token.getUserId());
            stmt.setString(2, "%" + author + "%");
            stmt.setString(3, "%" + year + "%");
            ServerUtil.getInstance().resultStmt(risultati, stmt);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nella ricerca di libri per autore e anno: autore=" + author + " anno=" + year, e);
            return null;
        }
        return risultati;
    }

    /**
     * Restituisce la lista completa dei libri visibili all’utente.
     * @param token token dell’utente autenticato.
     * @return lista di libri associati o visibili all’utente.
     * @throws RemoteException se si verifica un errore di comunicazione RMI.
     */
    @Override
    public List<Libro> getAllBooks(Token token) throws RemoteException {
        try{
            logger.info("Searching for books from client " + getClientHost());
        } catch (ServerNotActiveException ignored) {}
        if (ServerUtil.getInstance().isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.getToken() + " utente di id " + token.getUserId() + " IP:" + token.getIpClient());
            return null;
        }

        List<Libro> risultati = new ArrayList<>();
        String query = """
        SELECT DISTINCT l.*
          FROM libri AS l
          JOIN libreria_libro AS ll
            ON ll.idlibro = l.id
          JOIN librerie AS lr
            ON lr.id = ll.idlibreria
         WHERE lr.id_utente = ?;
        """;

        try (Connection conn = ServerUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, token.getUserId());
            ServerUtil.getInstance().resultStmt(risultati, stmt);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nel recupero di tutti i libri per utente id " + token.getUserId(), e);
            return null;
        }
        return risultati;
    }

    /**
     * Verifica se il libro specificato ha almeno una valutazione o un consiglio associato.
     * @param libro oggetto {@link Libro} da analizzare.
     * @return {@code true} se il libro ha almeno una valutazione o consiglio, {@code false} altrimenti.
     * @throws RemoteException se si verifica un errore di comunicazione RMI.
     */
    @Override
    public boolean hasValRec(Libro libro) throws RemoteException {
        //TODO: Gestire ServerNotActiveException e capire se avere una stampa di log
        String sql = """
                SELECT
                ( EXISTS (SELECT 1 FROM valutazioni WHERE idlibro = ?)
                OR EXISTS (SELECT 1 FROM consigli WHERE idlibro = ?)
                ) AS presente
                """;

        try (Connection conn = ServerUtil.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, libro.getId());
            ps.setInt(2, libro.getId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("presente");
                }
            }
        }catch(SQLException e){
            logger.log(Level.SEVERE, "Errore nel controllo se il libro con ID: " + libro.getId() + " ha valutazioni, consigli o associazioni", e);
        }
        return false;
    }
}