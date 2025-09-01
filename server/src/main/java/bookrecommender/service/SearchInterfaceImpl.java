package bookrecommender.service;

import bookrecommender.interfaces.SearchInterface;
import bookrecommender.model.Libro;
import bookrecommender.model.Libro_Details;
import bookrecommender.model.Token;
import bookrecommender.model.Valutazione;
import bookrecommender.util.ServerUtil;

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
 *
 * @author Maffioli Gianmarco, 757587, VA
 * @author Rolla Francesca, 757922, VA
 * @author Fabbian Gabriele, 755699, VA
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
    public SearchInterfaceImpl() throws RemoteException {
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
     * Cerca libri che corrispondono (parzialmente) al titolo specificato.
     * Il risultato viene limitato a {@code maxResults} se il totale supera tale soglia, con un
     * ordinamento basato su {@code STRPOS}.
     *
     * @param title titolo del libro da cercare.
     * @return lista di libri con titolo corrispondente.
     * @throws RemoteException se si verifica un errore di comunicazione RMI.
     */
    @Override
    public List<Libro> searchByName(String title, int maxResults) throws RemoteException {
        try {
            logger.info("Searching for books with title: " + title + " From client " + getClientHost());
        }catch (ServerNotActiveException ignored){}
        List<Libro> risultati = new ArrayList<>();
        String baseWhere = "FROM LIBRI WHERE LOWER(TITOLO) LIKE LOWER(?)";
        String countSql = "SELECT COUNT(*) " + baseWhere;
        String dataSql  = "SELECT * " + baseWhere;
        String orderedAndLimitedSql = "SELECT * " + baseWhere + " ORDER BY STRPOS(LOWER(?), LOWER(TITOLO))" + "LIMIT (?)";

        try (Connection conn = ServerUtil.getInstance().getConnection()) {
            int total;
            try ( PreparedStatement cstmt = conn.prepareStatement(countSql) ) {
                cstmt.setString(1, "%" + title + "%");
                try ( ResultSet crs = cstmt.executeQuery() ) {
                    if(crs.next())
                        total = crs.getInt(1);
                    else {
                        return risultati;
                    }
                }
            }

            String chosenSql = (total <= maxResults ? dataSql : orderedAndLimitedSql);
            try ( PreparedStatement stmt = conn.prepareStatement(chosenSql) ) {
                stmt.setString(1, "%" + title + "%");
                if (total > maxResults) {
                    stmt.setString(2, title);
                    stmt.setInt(3, maxResults);
                }
                ServerUtil.getInstance().resultStmt(risultati, stmt);
            }

        } catch (SQLException e) {
            logger.warning("Errore nella connessione al database: " + e.getMessage());
        }

        return risultati;
    }

    /**
     * Cerca libri scritti da un determinato autore (match parziale case-insensitive).
     * Se il numero di risultati supera {@code maxResults}, applica un limite e un
     * ordinamento euristico.
     *
     * @param author     nome (o parte del nome) dell'autore
     * @param maxResults massimo numero di risultati da restituire (se possibile)
     * @return lista di libri dell'autore specificato; può essere vuota ma non {@code null}
     * @throws RemoteException se si verifica un errore di comunicazione RMI
     */
    @Override
    public List<Libro> searchByAuthor(String author, int maxResults) throws RemoteException {
        try {
            logger.info("Searching for books by author: " + author + " From client " + getClientHost());
        }catch (ServerNotActiveException ignored){}
        List<Libro> risultati = new ArrayList<>();
        String baseWhere = "FROM LIBRI WHERE LOWER(AUTORE) LIKE LOWER(?)";
        String countSql = "SELECT COUNT(*) " + baseWhere;
        String dataSql = "SELECT * " + baseWhere;
        String orderedLimitedSql = "SELECT * " + baseWhere + " ORDER BY STRPOS(LOWER(AUTORE), LOWER(?))" + " LIMIT (?)";
        try (Connection conn = ServerUtil.getInstance().getConnection()) {
            int total;
            try (PreparedStatement cstmt = conn.prepareStatement(countSql)) {
                cstmt.setString(1, "%" + author + "%");
                try (ResultSet crs = cstmt.executeQuery()) {
                    if(crs.next())
                        total = crs.getInt(1);
                    else {
                        return risultati;
                    }
                }
            }
            String chosenSql = (total <= maxResults ? dataSql : orderedLimitedSql);
            try (PreparedStatement stmt = conn.prepareStatement(chosenSql)) {
                stmt.setString(1, "%" + author + "%");
                if (total > maxResults) {
                    stmt.setString(2, author);
                    stmt.setInt(3, maxResults);
                }
                ServerUtil.getInstance().resultStmt(risultati, stmt);
            }
        } catch (SQLException e) {
            logger.warning("Errore nella connessione al database: " + e.getMessage());
        }
        return risultati;
    }

    /**
     * Cerca libri scritti da un determinato autore in uno specifico anno (match parziale sull'anno).
     * Applica limite/ordinamento come negli altri metodi di ricerca, se necessario.
     *
     * @param author     autore (match parziale, case-insensitive)
     * @param year       anno di pubblicazione (match parziale su stringa)
     * @param maxResults massimo numero di risultati da restituire (se possibile)
     * @return lista di libri che soddisfano i criteri; può essere vuota ma non {@code null}
     * @throws RemoteException se si verifica un errore di comunicazione RMI
     */
    @Override
    public List<Libro> searchByAuthorAndYear(String author, int year, int maxResults) throws RemoteException {
        try {
            logger.info("Searching for books by author: " + author + " and year: " + year + " From client " + getClientHost());
        }catch (ServerNotActiveException ignored){}
        List<Libro> risultati = new ArrayList<>();
        String baseWhere = "FROM LIBRI WHERE LOWER(AUTORE) LIKE LOWER(?) " + "AND CAST(ANNOPUBBLICAZIONE AS TEXT) LIKE (?)";
        String countSql = "SELECT COUNT(*) " + baseWhere;
        String dataSql = "SELECT * " + baseWhere;
        String orderedLimitedSql = "SELECT * " + baseWhere + " ORDER BY STRPOS(LOWER(AUTORE), LOWER(?))" + " LIMIT (?)";
        try (Connection conn = ServerUtil.getInstance().getConnection()) {
            int total;
            try (PreparedStatement cstmt = conn.prepareStatement(countSql)) {
                cstmt.setString(1, "%" + author + "%");
                cstmt.setString(2, "%" + year + "%");
                try (ResultSet crs = cstmt.executeQuery()) {
                    if(crs.next())
                        total = crs.getInt(1);
                    else {
                        return risultati;
                    }
                }
            }
            String chosenSql = (total <= maxResults ? dataSql : orderedLimitedSql);
            try (PreparedStatement stmt = conn.prepareStatement(chosenSql)) {
                stmt.setString(1, "%" + author + "%");
                stmt.setString(2, "%" + year + "%");
                if (total > maxResults) {
                    stmt.setString(3, author);
                    stmt.setInt(4, maxResults);
                }
                ServerUtil.getInstance().resultStmt(risultati, stmt);
            }

        } catch (SQLException e) {
            logger.warning("Errore nella connessione al database: " + e.getMessage());
        }

        return risultati;
    }

    /**
     * Restituisce i dettagli completi (valutazioni e consigli) di un libro selezionato.
     *
     * @param libro oggetto {@link Libro} di cui si vogliono i dettagli
     * @return oggetto {@link Libro_Details} con le informazioni dettagliate; non {@code null}
     * @throws RemoteException se si verifica un errore di comunicazione RMI
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
     * Cerca libri per titolo limitando la ricerca ai libri nelle librerie dell'utente (token).
     *
     * @param token token utente autenticato
     * @param title titolo da cercare
     * @return lista di libri corrispondenti o {@code null} se token non valido/errore
     * @throws RemoteException se si verifica un errore di comunicazione RMI
     */
    public List<Libro> searchByName(Token token, String title) throws RemoteException {
        try{
            logger.info("Searching for books with title: " + title + " From client " + getClientHost());
        } catch (ServerNotActiveException ignored) {}
        if (ServerUtil.getInstance().isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.token() + " utente di id " + token.userId() + " IP:" + token.ipClient());
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
            stmt.setInt(1, token.userId());
            stmt.setString(2, title);
            ServerUtil.getInstance().resultStmt(risultati, stmt);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nella ricerca di un libro con titolo: " + title, e);
            return null;
        }
        return risultati;
    }

    /**
     * Cerca libri per autore limitando ai libri presenti nelle librerie dell'utente (token).
     *
     * @param token  token utente autenticato
     * @param author autore da cercare (match parziale, case-insensitive)
     * @return lista di libri corrispondenti o {@code null} se token non valido/errore
     * @throws RemoteException se si verifica un errore di comunicazione RMI
     */
    public List<Libro> searchByAuthor(Token token, String author) throws RemoteException {
        try{
            logger.info("Searching for books with author: " + author + " From client " + getClientHost());
        } catch (ServerNotActiveException ignored) {}
        if (ServerUtil.getInstance().isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.token() + " utente di id " + token.userId() + " IP:" + token.ipClient());
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
            stmt.setInt(1, token.userId());
            stmt.setString(2, "%" + author + "%");
            ServerUtil.getInstance().resultStmt(risultati, stmt);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nella ricerca di libri per autore: " + author, e);
            return null;
        }
        return risultati;
    }

    /**
     * Cerca libri per autore e anno limitando ai libri presenti nelle librerie dell'utente (token).
     *
     * @param token  token utente autenticato
     * @param author autore del libro (match parziale, case-insensitive)
     * @param year   anno di pubblicazione (match parziale su stringa)
     * @return lista di libri corrispondenti o {@code null} se token non valido/errore
     * @throws RemoteException se si verifica un errore di comunicazione RMI
     */
    public List<Libro> searchByAuthorAndYear(Token token, String author, int year) throws RemoteException {
        try {
            logger.info("Searching for books by author: " + author + " and year: " + year + " From client " + getClientHost());
        }catch (ServerNotActiveException ignored){}
        if (ServerUtil.getInstance().isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.token() + " utente di id " + token.userId() + " IP:" + token.ipClient());
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
            stmt.setInt(1, token.userId());
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
     * Restituisce la lista completa dei libri visibili/associati all’utente autenticato.
     *
     * @param token token dell’utente autenticato
     * @return lista dei libri; {@code null} se token non valido o in caso di errore
     * @throws RemoteException se si verifica un errore di comunicazione RMI
     */
    @Override
    public List<Libro> getAllBooks(Token token) throws RemoteException {
        try{
            logger.info("Searching for books from client " + getClientHost());
        } catch (ServerNotActiveException ignored) {}
        if (ServerUtil.getInstance().isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.token() + " utente di id " + token.userId() + " IP:" + token.ipClient());
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
            stmt.setInt(1, token.userId());
            ServerUtil.getInstance().resultStmt(risultati, stmt);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nel recupero di tutti i libri per utente id " + token.userId(), e);
            return null;
        }
        return risultati;
    }

    /**
     * Verifica se il libro specificato ha almeno una valutazione o un consiglio associato.
     *
     * @param libro oggetto {@link Libro} da analizzare
     * @return {@code true} se esiste almeno una valutazione o un consiglio; {@code false} altrimenti
     * @throws RemoteException se si verifica un errore di comunicazione RMI
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