package bookrecommender.server;

import bookrecommender.common.LibInterface;
import bookrecommender.common.Libro;
import bookrecommender.common.Token;
import bookrecommender.common.Valutazione;

import java.io.Serial;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Implementazione remota dell'interfaccia {@link LibInterface} per la gestione
 * delle librerie personali degli utenti. Fornisce funzionalità RMI per la creazione,
 * modifica, eliminazione, valutazione e consultazione delle librerie e dei libri.
 * Tutte le operazioni sono registrate tramite {@link Logger} e delegate al {@link DBManager}.
 * In caso di chiamata RMI non valida (es. Client sconosciuto), i metodi restituiscono valori di fallback.
 */
public class LibInterfaceImpl extends UnicastRemoteObject implements LibInterface {
    @Serial
    private static final long serialVersionUID = 1L;
    private final Logger logger;

    /**
     * Costruttore della classe {@code LibInterfaceImpl}
     * @throws RemoteException Se si verifica un errore nella configurazione dell'oggetto remoto.
     */
    protected LibInterfaceImpl() throws RemoteException {
        super();
        this.logger = Logger.getLogger(LibInterfaceImpl.class.getName());
    }

    /**
     * Crea una nuova libreria per l'utente specificato.
     * @param token Token di autenticazione dell'utente.
     * @param nome Nome della libreria da creare.
     * @param libri Lista dei libri iniziali da inserire nella libreria.
     * @return {@code true} se la libreria è stata creata con successo, {@code false} altrimenti.
     * @throws RemoteException In caso di errore di comunicazione remota.
     */
    @Override
    public boolean createLib(Token token, String nome, List<Libro> libri) throws RemoteException {
        try {
            logger.info("Creazione libreria: " + nome + " da parte di " + token.userId() + "con IP: " + getClientHost());
        }catch(ServerNotActiveException e){
            return false;
        }
        if (ServerUtil.getInstance().isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.token() + " utente di id " + token.userId() + " IP:" + token.ipClient());
            return false;
        }
        try {
            String checkQuery = "SELECT 1 FROM LIBRERIE WHERE ID_UTENTE = ? AND TITOLO_LIBRERIA = ?";
            Connection conn = ServerUtil.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(checkQuery);
            stmt.setInt(1, token.userId());
            stmt.setString(2, nome);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                logger.log(Level.WARNING, "Libreria con nome " + nome + " già esistente per l'utente con ID: " + token.userId());
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        String insertLibQuery = "INSERT INTO LIBRERIE (ID_UTENTE, TITOLO_LIBRERIA) VALUES (?, ?)";
        String insertLibroQuery = "INSERT INTO LIBRERIA_LIBRO (IDLIBRERIA, IDLIBRO) VALUES (?, ?)";

        try (Connection conn = ServerUtil.getInstance().getConnection();
             PreparedStatement insertLibStmt = conn.prepareStatement(insertLibQuery, Statement.RETURN_GENERATED_KEYS)) {
            insertLibStmt.setInt(1, token.userId());
            insertLibStmt.setString(2, nome);

            int rows = insertLibStmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet generatedKeys = insertLibStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int idLibreria = generatedKeys.getInt(1);
                        try (PreparedStatement insertLibroStmt = conn.prepareStatement(insertLibroQuery)) {
                            for (Libro libro : libri) {
                                insertLibroStmt.setInt(1, idLibreria);
                                insertLibroStmt.setInt(2, libro.getId());
                                insertLibroStmt.addBatch();
                            }
                            insertLibroStmt.executeBatch();
                        } catch (SQLException e) {
                            logger.log(Level.SEVERE, "Errore nell'inserimento dei libri nella libreria", e);
                            return false;
                        }
                        return true;
                    }
                }
            } else {
                logger.log(Level.WARNING, "Nessuna riga inserita nella libreria per l'utente con ID: " + token.userId());
                return false;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nella creazione della libreria", e);
        }
        return false;
    }

    /**
     * Elimina una libreria dell'utente.
     * @param token Token di autenticazione dell'utente.
     * @param nome Nome della libreria da eliminare.
     * @return {@code true} se la libreria è stata eliminata con successo, {@code false} altrimenti.
     * @throws RemoteException In caso di errore di comunicazione remota.
     */
    @Override
    public boolean deleteLib(Token token, String nome) throws RemoteException {
        try {
            logger.info("Eliminazione libreria: " + nome + " da parte di " + token.userId() + " con IP: " + getClientHost());
        } catch (ServerNotActiveException e) {
            return false;
        }
        if (ServerUtil.getInstance().isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.token() + " utente di id " + token.userId() + " IP:" + token.ipClient());
            return false;
        }
        String deleteQuery = "DELETE FROM LIBRERIE WHERE ID_UTENTE = ? AND TITOLO_LIBRERIA = ?";
        try (Connection conn = ServerUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {
            stmt.setInt(1, token.userId());
            stmt.setString(2, nome);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nell'eliminazione della libreria", e);
            return false;
        }
    }

    /**
     * Aggiorna il contenuto di una libreria dell'utente, aggiungendo o rimuovendo libri.
     * Vengono effettuati controlli per evitare rimozioni di libri valutati o consigliati.
     * @param token Token di autenticazione dell'utente.
     * @param nome Nome della libreria da aggiornare.
     * @param libriUp Lista aggiornata dei libri desiderati nella libreria.
     * @return Lista di interi: {@code [1]} se aggiornamento riuscito, {@code [0, idLibro, codiceErrore, ...]} altrimenti.
     *         I codici errore indicano: 0 → valutazione presente, 1 → libro consigliato, 2 → libro usato in un consiglio.
     * @throws RemoteException In caso di errore di comunicazione remota.
     */
    @Override
    public List<Integer> updateLib(Token token, String nome, List<Libro> libriUp) throws RemoteException {
        try {
            logger.info("Aggiornamento libreria: " + nome + " da parte di " + token.userId() + " con IP: " + getClientHost());
        } catch (ServerNotActiveException e) {
            return List.of(0);
        }
        if (ServerUtil.getInstance().isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.token() + " utente di id " + token.userId() + " IP:" + token.ipClient());
            return List.of(0);
        }
        int idLibreria;
        try (Connection conn = ServerUtil.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT id FROM librerie WHERE id_utente = ? AND titolo_libreria = ?")) {
            ps.setInt(1, token.userId());
            ps.setString(2, nome);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    idLibreria = rs.getInt("id");
                } else {
                    return List.of(0);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nel recupero dell'id libreria per utente id " + token.userId(), e);
            return List.of(0);
        }
        Set<Integer> correnti = new HashSet<>();
        try (Connection conn = ServerUtil.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT idlibro FROM libreria_libro WHERE idlibreria = ?")) {
            ps.setInt(1, idLibreria);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    correnti.add(rs.getInt("idlibro"));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nel caricamento dei libri correnti per libreria id " + idLibreria, e);
            return List.of(0);
        }
        Set<Integer> aggiornati = libriUp.stream().map(Libro::getId).collect(Collectors.toSet());
        Set<Integer> daEliminare = new HashSet<>(correnti);
        daEliminare.removeAll(aggiornati);
        Set<Integer> daAggiungere = new HashSet<>(aggiornati);
        daAggiungere.removeAll(correnti);
        List<Integer> errors = new ArrayList<>();
        try (Connection conn = ServerUtil.getInstance().getConnection();
             PreparedStatement valStmt = conn.prepareStatement("SELECT 1 FROM valutazioni WHERE id_utente = ? AND idlibro = ? LIMIT 1");
             PreparedStatement useStmt = conn.prepareStatement("SELECT 1 FROM consigli WHERE id_utente = ? AND idlibro = ? LIMIT 1");
             PreparedStatement recStmt = conn.prepareStatement("SELECT 1 FROM consigli WHERE id_utente = ? AND (lib_1 = ? OR lib_2 = ? OR lib_3 = ?) LIMIT 1")) {
            for (int id : daEliminare) {
                valStmt.setInt(1, token.userId());
                valStmt.setInt(2, id);
                if (valStmt.executeQuery().next()) {
                    errors.add(id);
                    errors.add(0);
                    continue;
                }
                useStmt.setInt(1, token.userId());
                useStmt.setInt(2, id);
                if (useStmt.executeQuery().next()) {
                    errors.add(id);
                    errors.add(1);
                    continue;
                }
                recStmt.setInt(1, token.userId());
                recStmt.setInt(2, id);
                recStmt.setInt(3, id);
                recStmt.setInt(4, id);
                if (recStmt.executeQuery().next()) {
                    errors.add(id);
                    errors.add(2);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nei controlli pre-eliminazione per libreria id " + idLibreria, e);
            return List.of(0);
        }
        if (!errors.isEmpty()) {
            List<Integer> result = new ArrayList<>();
            result.add(0);
            result.addAll(errors);
            return result;
        }
        String delQ = "DELETE FROM libreria_libro WHERE idlibreria = ? AND idlibro = ?";
        String insQ = "INSERT INTO libreria_libro (idlibreria, idlibro) VALUES (?, ?)";
        try (Connection conn = ServerUtil.getInstance().getConnection();
             PreparedStatement delStmt = conn.prepareStatement(delQ);
             PreparedStatement insStmt = conn.prepareStatement(insQ)) {
            for (int id : daEliminare) {
                delStmt.setInt(1, idLibreria);
                delStmt.setInt(2, id);
                delStmt.addBatch();
            }
            for (int id : daAggiungere) {
                insStmt.setInt(1, idLibreria);
                insStmt.setInt(2, id);
                insStmt.addBatch();
            }
            delStmt.executeBatch();
            insStmt.executeBatch();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nell'aggiornamento della libreria id " + idLibreria, e);
            return List.of(0);
        }
        return List.of(1);
    }

    /**
     * Recupera i libri presenti in una specifica libreria dell'utente.
     * @param token Token di autenticazione dell'utente.
     * @param nome Nome della libreria.
     * @return Lista dei libri contenuti nella libreria, oppure {@code null} in caso di errore.
     * @throws RemoteException In caso di errore di comunicazione remota.
     */
    @Override
    public List<Libro> getLib(Token token, String nome) throws RemoteException {
        try {
            logger.info("Recupero libreria: " + nome + " da parte di " + token.userId() + " con IP: " + getClientHost());
        } catch (ServerNotActiveException e) {
            return null;
        }
        if (ServerUtil.getInstance().isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.token() + " utente di id " + token.userId() + " IP:" + token.ipClient());
            return null;
        }
        List<Libro> libri = new ArrayList<>();
        String query = "SELECT L.* FROM LIBRERIE R JOIN LIBRERIA_LIBRO LL ON R.ID = LL.IDLIBRERIA JOIN LIBRI L ON LL.IDLIBRO = L.ID WHERE R.ID_UTENTE = ? AND R.TITOLO_LIBRERIA = ?";

        try (Connection conn = ServerUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, token.userId());
            stmt.setString(2, nome);
            ServerUtil.getInstance().resultStmt(libri, stmt);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nel recupero della libreria", e);
        }
        return libri;
    }

    /**
     * Recupera tutti i nomi delle librerie associate all'utente.
     * @param token Token di autenticazione dell'utente.
     * @return Lista dei nomi delle librerie, oppure {@code null} in caso di errore.
     * @throws RemoteException In caso di errore di comunicazione remota.
     */
    @Override
    public List<String> getLibs(Token token) throws RemoteException {
        try {
            logger.info("Recupero librerie da parte di " + token.userId() + " con IP: " + getClientHost());
        } catch (ServerNotActiveException e) {
            return null;
        }
        if (ServerUtil.getInstance().isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.token() + " utente di id " + token.userId() + " IP:" + token.ipClient());
            return null;
        }
        List<String> librerie = new ArrayList<>();
        String query = "SELECT TITOLO_LIBRERIA FROM LIBRERIE WHERE ID_UTENTE = ?";

        try (Connection conn = ServerUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, token.userId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    librerie.add(rs.getString("TITOLO_LIBRERIA"));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nel recupero delle librerie", e);
        }
        return librerie;
    }

    /**
     * Aggiunge una valutazione a un libro per conto dell'utente.
     * @param token Token di autenticazione dell'utente.
     * @param valutazione Oggetto {@link Valutazione} contenente punteggi e commenti.
     * @return {@code true} se la valutazione è stata inserita correttamente, {@code false} altrimenti.
     * @throws RemoteException In caso di errore di comunicazione remota.
     */
    @Override
    public boolean addValutazione(Token token, Valutazione valutazione) throws RemoteException {
        try{
            logger.info("Aggiunta valutazione da parte di " + token.userId() + " con IP: " + getClientHost());
        } catch (ServerNotActiveException e) {
            return false;
        }
        if (ServerUtil.getInstance().isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.token() + " utente di id " + token.userId() + " IP:" + token.ipClient());
            return false;
        }
        if(!ServerUtil.getInstance().userHasLibro(token, valutazione.getLibro())) {
            logger.log(Level.WARNING, "L'utente con ID: " + token.userId() + " non ha il libro con ID: " + valutazione.getIdLibro() + " nelle sue librerie.");
            return false;
        }
        List<String> commenti = valutazione.getCommenti();
        List<Float> valutazioni = valutazione.getValutazioni();
        String insertQuery = """
                INSERT INTO valutazioni (idlibro, id_utente, v_stile, c_stile, v_contenuto, c_contenuto,
                                         v_gradevolezza, c_gradevolezza, v_originalita, c_originalita,
                                         v_edizione, c_edizione, c_finale)
                VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)""";
        try (Connection conn = ServerUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
            stmt.setInt(1, valutazione.getIdLibro());
            stmt.setInt(2, token.userId());
            stmt.setFloat(3, valutazioni.get(0));
            stmt.setString(4, commenti.get(0));
            stmt.setFloat(5, valutazioni.get(1));
            stmt.setString(6, commenti.get(1));
            stmt.setFloat(7, valutazioni.get(2));
            stmt.setString(8, commenti.get(2));
            stmt.setFloat(9, valutazioni.get(3));
            stmt.setString(10, commenti.get(3));
            stmt.setFloat(11, valutazioni.get(4));
            stmt.setString(12, commenti.get(4));
            stmt.setString(13, commenti.get(5));

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                logger.log(Level.INFO, "Valutazione aggiunta con successo per il libro con ID: " + valutazione.getIdLibro() + " da parte dell'utente con ID: " + token.userId());
                return true;
            } else {
                logger.log(Level.WARNING, "Nessuna riga inserita nella tabella delle valutazioni per il libro con ID: " + valutazione.getIdLibro() + " da parte dell'utente con ID: " + token.userId());
                return false;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nell'aggiunta della valutazione per il libro con ID: " + valutazione.getIdLibro(), e);
            return false;
        }
    }

    /**
     * Aggiunge un consiglio associato a un libro dell'utente, suggerendo fino a 3 altri libri.
     * @param token Token di autenticazione dell'utente.
     * @param libri Lista dei libri: il primo è il libro target, gli altri sono suggerimenti.
     * @return {@code true} se il consiglio è stato salvato correttamente, {@code false} altrimenti.
     * @throws RemoteException In caso di errore di comunicazione remota.
     */
    @Override
    public boolean addConsiglio(Token token, List<Libro> libri) throws RemoteException {
        try{
            logger.info("Aggiunta consigli da parte di " + token.userId() + " con IP: " + getClientHost() + " a libro: " + libri.get(0).getId());
        } catch (ServerNotActiveException e) {
            return false;
        }
        if (ServerUtil.getInstance().isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.token() + " utente di id " + token.userId() + " IP:" + token.ipClient());
            return false;
        }
        if (libri.size() < 2) {
            logger.log(Level.WARNING, "Numero di libri per il consiglio inferiore a 2");
            return false;
        }
        if (libri.size() > 4) {
            logger.log(Level.WARNING, "Numero di libri per il consiglio superiore a 4, verranno considerati solo i primi 4");
            libri = libri.subList(0, 4);
        }
        if (libri.size() < 4) {
            for (int i = libri.size(); i < 4; i++) {
                libri.add(null);
            }
        }
        String insertQuery = """
                INSERT INTO consigli (idlibro, id_utente, lib_1, lib_2, lib_3)
                VALUES (?, ?, ?, ?, ?)""";
        try (Connection conn = ServerUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
            stmt.setInt(1, libri.get(0).getId());
            stmt.setInt(2, token.userId());
            stmt.setInt(3, libri.get(1).getId());
            if(libri.get(2) == null)
                stmt.setNull(4, Types.INTEGER);
            else
                stmt.setInt(4, libri.get(2).getId());
            if(libri.get(3) == null)
                stmt.setNull(5, Types.INTEGER);
            else
                stmt.setInt(5, libri.get(3).getId());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                logger.log(Level.INFO, "Consiglio aggiunto con successo per il libro con ID: " + libri.get(0).getId() + " da parte dell'utente con ID: " + token.userId());
                return true;
            } else {
                logger.log(Level.WARNING, "Nessuna riga inserita nella tabella dei consigli per il libro con ID: " + libri.get(0).getId() + " da parte dell'utente con ID: " + token.userId());
                return false;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nell'aggiunta del consiglio per il libro con ID: " + libri.get(0).getId(), e);
            return false;
        }
    }

    /**
     * Modifica il nome di una libreria dell'utente.
     * @param token Token di autenticazione dell'utente.
     * @param oldName Nome attuale della libreria.
     * @param newName Nuovo nome desiderato.
     * @return {@code true} se il nome è stato modificato correttamente, {@code false} altrimenti.
     * @throws RemoteException In caso di errore di comunicazione remota.
     */
    @Override
    public boolean modifyLibName(Token token, String oldName, String newName) throws RemoteException {
        try{
            logger.info("Modifica del nome della libreria " + oldName + " con il nome: " + newName + " da parte di " + token.userId() + " con IP: " + getClientHost());
        } catch (ServerNotActiveException e) {
            return false;
        }
        if (ServerUtil.getInstance().isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.token() + " utente di id " + token.userId() + " IP:" + token.ipClient());
            return false;
        }

        String sql = """
                     UPDATE librerie
                     SET titolo_libreria = ?
                     WHERE id_utente = ?
                     AND titolo_libreria = ?;""";

        try (Connection conn = ServerUtil.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newName);
            ps.setInt(2, token.userId());
            ps.setString(3, oldName);

            int righe = ps.executeUpdate();
            if (righe == 1) {
                logger.log(Level.INFO, "Rinominata libreria \"" + oldName + "\" in \"" + newName + "\" per utente id " + token.userId());
                return true;
            } else {
                logger.log(Level.WARNING, "Nessuna libreria aggiornata: non esiste una libreria \"" + oldName + "\" per utente id " + token.userId());
                return false;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore durante la modifica del nome della libreria \"" + oldName + "\" per utente id " + token.userId(), e);
            return false;
        }
    }

    /**
     * Verifica se un determinato libro è presente in almeno una libreria dell'utente.
     * @param token Token di autenticazione dell'utente.
     * @param libro Libro da cercare.
     * @return {@code true} se il libro è presente in una delle librerie dell'utente, {@code false} altrimenti.
     * @throws RemoteException In caso di errore di comunicazione remota.
     */
    @Override
    public boolean isLibPresent(Token token, Libro libro) throws RemoteException {
        //TODO: Gestire ServerNotActiveException e capire se avere una stampa di log
        return ServerUtil.getInstance().userHasLibro(token, libro);
    }

    /**
     * Restituisce la data di creazione di una libreria.
     * @param token Token di autenticazione dell'utente.
     * @param nome Nome della libreria.
     * @return {@link LocalDate} corrispondente alla data di creazione della libreria,
     *         oppure {@code null} se non trovata o in caso di errore.
     * @throws RemoteException In caso di errore di comunicazione remota.
     */
    @Override
    public LocalDate getCreationDate(Token token, String nome) throws RemoteException {
        //TODO: Gestire ServerNotActiveException e capire se avere una stampa di log
        if (ServerUtil.getInstance().isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.token() + " utente di id " + token.userId() + " IP:" + token.ipClient());
            return null;
        }
        final String SQL =
                "SELECT libreria_time " +
                        "  FROM librerie " +
                        " WHERE id_utente = ? " +
                        "   AND titolo_libreria = ?";

        int userId = token.userId();

        try (Connection conn = ServerUtil.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL)) {

            ps.setInt (1, userId);
            ps.setString(2, nome);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Timestamp ts = rs.getTimestamp("libreria_time");
                    return ts.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            logger.warning("Libreria non trovata per utente id " + userId + " con nome: " + nome + " - " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean existVal(Token token, Libro libro) throws RemoteException {
        //TODO: Gestire ServerNotActiveException e capire se avere una stampa di log
        if (ServerUtil.getInstance().isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.token() + " utente di id " + token.userId() + " IP:" + token.ipClient());
            return false;
        }
        String sql = "SELECT 1 FROM valutazioni WHERE id_utente = ? AND idlibro = ?";
        try (Connection conn = ServerUtil.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, token.userId());
            ps.setInt(2, libro.getId());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore in existVal", e);
            return false;
        }
    }

    @Override
    public boolean existCon(Token token, Libro libro) throws RemoteException {
        //TODO: Gestire ServerNotActiveException e capire se avere una stampa di log
        if (ServerUtil.getInstance().isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.token() + " utente di id " + token.userId() + " IP:" + token.ipClient());
            return false;
        }
        String sql = "SELECT 1 FROM consigli WHERE id_utente = ? AND idlibro = ?";
        try (Connection conn = ServerUtil.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, token.userId());
            ps.setInt(2, libro.getId());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore in existCon", e);
            return false;
        }
    }

    @Override
    public boolean updateVal(Token token, Valutazione valutazione) throws RemoteException {
        try{
            logger.info("Modifica valutazione per libro " + valutazione.getIdLibro() + " da parte di " + token.userId() + " con IP: " + getClientHost());
        } catch (ServerNotActiveException e) {
            return false;
        }
        if (ServerUtil.getInstance().isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.token() + " utente di id " + token.userId() + " IP:" + token.ipClient());
            return false;
        }
        String sql = """
            UPDATE valutazioni
               SET c_stile = ?,  v_stile = ?,
                   c_contenuto = ?, v_contenuto = ?,
                   c_gradevolezza = ?, v_gradevolezza = ?,
                   c_originalita = ?,  v_originalita = ?,
                   c_edizione = ?,  v_edizione = ?,
                   c_finale = ?,
                   valutazione_time = CURRENT_TIMESTAMP
             WHERE id_utente = ? AND idlibro = ?
            """;
        List<Float> ratings = valutazione.getValutazioni();
        List<String> comments = valutazione.getCommenti();
        try (
                Connection conn = ServerUtil.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1,  comments.get(0));
            ps.setInt(   2,  Math.round(ratings.get(0)));
            ps.setString(3,  comments.get(1));
            ps.setInt(   4,  Math.round(ratings.get(1)));
            ps.setString(5,  comments.get(2));
            ps.setInt(   6,  Math.round(ratings.get(2)));
            ps.setString(7,  comments.get(3));
            ps.setInt(   8,  Math.round(ratings.get(3)));
            ps.setString(9,  comments.get(4));
            ps.setInt(  10,  Math.round(ratings.get(4)));
            ps.setString(11, comments.size() > 5 ? comments.get(5) : "");
            ps.setInt(  12, token.userId());
            ps.setInt(  13, valutazione.getIdLibro());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore in updateVal", e);
            return false;
        }
    }

    @Override
    public boolean updateCon(Token token, List<Libro> libri) throws RemoteException {
        try{
            logger.info("Modifica consigli per libro " + libri.get(0).getId() + " da parte di " + token.userId() + " con IP: " + getClientHost());
        } catch (ServerNotActiveException e) {
            return false;
        }
        if (ServerUtil.getInstance().isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.token() + " utente di id " + token.userId() + " IP:" + token.ipClient());
            return false;
        }
        if (libri.size() < 2 || libri.size() > 4) {
            return false;
        }
        String sql = """
            UPDATE consigli
               SET lib_1 = ?, lib_2 = ?, lib_3 = ?, consiglio_time = CURRENT_TIMESTAMP
             WHERE id_utente = ? AND idlibro = ?
            """;
        try (Connection conn = ServerUtil.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            int refId = libri.get(0).getId();
            ps.setObject(1, libri.size() > 1 ? libri.get(1).getId() : null, Types.INTEGER);
            ps.setObject(2, libri.size() > 2 ? libri.get(2).getId() : null, Types.INTEGER);
            ps.setObject(3, libri.size() > 3 ? libri.get(3).getId() : null, Types.INTEGER);
            ps.setInt(4, token.userId());
            ps.setInt(5, refId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore in updateCon", e);
            return false;
        }
    }

    @Override
    public boolean deleteVal(Token token, Libro libro) throws RemoteException {
        try{
            logger.info("Eliminazione valutazione del libro " + libro.getId() + " da parte di " + token.userId() + " con IP: " + getClientHost());
        } catch (ServerNotActiveException e) {
            return false;
        }
        if (ServerUtil.getInstance().isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.token() + " utente di id " + token.userId() + " IP:" + token.ipClient());
            return false;
        }
        String sql = "DELETE FROM valutazioni WHERE id_utente = ? AND idlibro = ?";
        try (Connection conn = ServerUtil.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, token.userId());
            ps.setInt(2, libro.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore in deleteVal", e);
            return false;
        }
    }

    @Override
    public boolean deleteCon(Token token, Libro libro) throws RemoteException {
        try{
            logger.info("Eliminazione consigli del libro " + libro.getId() + " da parte di " + token.userId() + " con IP: " + getClientHost());
        } catch (ServerNotActiveException e) {
            return false;
        }
        if (ServerUtil.getInstance().isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.token() + " utente di id " + token.userId() + " IP:" + token.ipClient());
            return false;
        }
        String sql = "DELETE FROM consigli WHERE id_utente = ? AND idlibro = ?";
        try (Connection conn = ServerUtil.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, token.userId());
            ps.setInt(2, libro.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore in deleteCon", e);
            return false;
        }
    }

    @Override
    public LocalDate getValDate(Token token, Libro libro) throws RemoteException {
        try{
            logger.info("Recupero data di modifica per la valutazione del libro " + libro.getId()  + " da parte di " + token.userId() + " con IP: " + getClientHost());
        } catch (ServerNotActiveException e) {
            return null;
        }
        if (ServerUtil.getInstance().isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.token() + " utente di id " + token.userId() + " IP:" + token.ipClient());
            return null;
        }
        String sql = "SELECT valutazione_time FROM valutazioni WHERE id_utente = ? AND idlibro = ?";
        try (Connection conn = ServerUtil.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, token.userId());
            ps.setInt(2, libro.getId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Timestamp ts = rs.getTimestamp("valutazione_time");
                    return ts.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore in getValDate", e);
            return null;
        }
    }

    @Override
    public LocalDate getConDate(Token token, Libro libro) throws RemoteException {
        try{
            logger.info("Recupero data di modifica per il consiglio del libro " + libro.getId()  + " da parte di " + token.userId() + " con IP: " + getClientHost());
        } catch (ServerNotActiveException e) {
            return null;
        }
        if (ServerUtil.getInstance().isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.token() + " utente di id " + token.userId() + " IP:" + token.ipClient());
            return null;
        }
        String sql = "SELECT consiglio_time FROM consigli WHERE id_utente = ? AND idlibro = ?";
        try (Connection conn = ServerUtil.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, token.userId());
            ps.setInt(2, libro.getId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Timestamp ts = rs.getTimestamp("consiglio_time");
                    return ts.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore in getConsDate", e);
            return null;
        }
    }

    @Override
    public List<Libro> getConsigli(Token token, Libro libro) throws RemoteException {
        try{
            logger.info("Recupero consiglio del libro " + libro.getId()  + " da parte di " + token.userId() + " con IP: " + getClientHost());
        } catch (ServerNotActiveException e) {
            return null;
        }
        if (ServerUtil.getInstance().isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.token() + " utente di id " + token.userId() + " IP:" + token.ipClient());
            return null;
        }
        String sql = "SELECT lib_1, lib_2, lib_3 FROM consigli WHERE id_utente = ? AND idlibro = ?";
        try (Connection conn = ServerUtil.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, token.userId());
            ps.setInt(2, libro.getId());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                List<Libro> result = new ArrayList<>();
                result.add(ServerUtil.getInstance().getLibro(libro.getId()));
                for (int i = 0; i < 3; i++) {
                    int consigliatoId = rs.getInt("lib_" + (i+1));
                    if (!rs.wasNull()) {
                        result.add(ServerUtil.getInstance().getLibro(consigliatoId));
                    }
                }
                return result;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore in getConsiglio", e);
            return null;
        }
    }

    @Override
    public Valutazione getValutazione(Token token, Libro libro) throws RemoteException {
        try{
            logger.info("Recupero valutazione del libro " + libro.getId()  + " da parte di " + token.userId() + " con IP: " + getClientHost());
        } catch (ServerNotActiveException e) {
            return null;
        }
        if (ServerUtil.getInstance().isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.token() + " utente di id " + token.userId() + " IP:" + token.ipClient());
            return null;
        }
        String valSql = """
        SELECT c_stile, v_stile,
               c_contenuto, v_contenuto,
               c_gradevolezza, v_gradevolezza,
               c_originalita, v_originalita,
               c_edizione, v_edizione,
               c_finale, v_finale
          FROM valutazioni
         WHERE id_utente = ? AND idlibro = ?
        """;
        try (Connection conn = ServerUtil.getInstance().getConnection();
             PreparedStatement psVal  = conn.prepareStatement(valSql)) {
            psVal.setInt(1, token.userId());
            psVal.setInt(2, libro.getId());
            try (ResultSet rsV = psVal.executeQuery()) {
                if (!rsV.next()) {
                    return null;
                }
                List<Float> ratings = ServerUtil.getInstance().getVotiVal(rsV);
                List<String> comments = ServerUtil.getInstance().getComVal(rsV);
                return new Valutazione(String.valueOf(token.userId()), ratings, comments, libro);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore in getValutazione", e);
            return null;
        }
    }
}
