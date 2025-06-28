<img src="src/resources/bookrecommender/client/icons/SigilloAteneoTestoColori.svg" style="float: right; width: 300px;" alt="Icona Insubria">

# BookRecommender

**Progetto universitario per l'esame di Laboratorio Interdisciplinare B – Università degli Studi dell’Insubria (2025)**

## Autori del progetto

- **Gianmarco Maffioli 757587 VA** – Project Manager
- **Francesca Rolla 757922 VA** – UI/UX Designer
- **Gabriele Fabbian 755699 VA** – Technical Documentation & Complexity Analyst

---

## 📦 Dipendenze e Librerie

Il progetto utilizza **Java 17** e richiede l’installazione delle seguenti librerie esterne:

### Librerie Maven (versioni esatte)

| Libreria | Versione |
|----------|----------|
| `org.openjfx:javafx-base` | 17.0.8 |
| `org.openjfx:javafx-base:win` | 17.0.8 |
| `org.openjfx:javafx-controls` | 17.0.8 |
| `org.openjfx:javafx-controls:win` | 17.0.8 |
| `org.openjfx:javafx-fxml` | 17.0.8 |
| `org.openjfx:javafx-fxml:win` | 17.0.8 |
| `org.openjfx:javafx-graphics` | 17.0.8 |
| `org.openjfx:javafx-graphics:win` | 17.0.8 |
| `org.postgresql:postgresql` | 42.7.3 |
| `org.apache.commons:commons-lang3` | 3.17.0 |
| `com.opencsv:opencsv` | 5.9 |
#### 🔗 Dove reperire le librerie

Puoi scaricare le librerie da:

- [Maven Central Repository](https://search.maven.org/)
- Oppure usare un sistema di build come **Maven** o **Gradle**

In alternativa, se il progetto è già configurato con Maven o Gradle, sarà sufficiente eseguire una build (`mvn install` o `gradle build`) per scaricare automaticamente tutte le dipendenze.

---

## 🗃️ Struttura del progetto

```
BookRecommender/
├── data/
│   ├── [*.dati.csv] — Dati di libri, utenti, valutazioni, librerie, consigli
│   └── query/
│       ├── tablecreation.sql — Script di creazione tabelle
│       ├── datainsert.sql — Popolamento tabelle
│       ├── login_tableCreation.sql — Tabelle per login
│       └── newRules.sql — Regole o vincoli addizionali
│
├── src/
│   ├── java/
│   │   ├── databaseUpdater/
│   │   │   └── DBUp.java — Programma per importare i dati da Libri.dati.csv
│   │   │
│   │   ├── bookrecommender/
│   │   │   ├── client/
│   │   │   │   ├── Main_Client.java — Punto di avvio client
│   │   │   │   ├── CliUtil.java — Singleton con costanti e metodi utili
│   │   │   │   ├── [Controller].java — Controller per ogni FXML
│   │   │   │
│   │   │   ├── common/
│   │   │   │   ├── Libro.java — Classe serializzabile del libro
│   │   │   │   ├── Token.java / RegToken.java — Token di sessione
│   │   │   │   └── [Interfacce RMI] — Interfacce per comunicazione client-server
│   │   │   │
│   │   │   └── server/
│   │   │       ├── Main_Server.java — Avvio del server
│   │   │       ├── DBManager.java — Connessione al database
│   │   │       └── [Impl].java — Implementazioni delle interfacce `common`
│   │
│   └── resources/
│       └── bookrecommender/
│           ├── client/
│           │   └── [*.fxml] — Interfaccia grafica del client (Login, Home, Registrazione, ecc.)
│           ├── icons/
│           │   └── [*.png/*.jpg] — Icone per alert, frecce, immagini decorative
│           └── stylesheets/
│               └── [*.css] — Fogli di stile associati ai vari FXML
│
└── doc/ Documentazione
```

---

## ⚙️ Setup e Installazione

### 1. Preparazione del Database PostgreSQL

1. Assicurati di avere un'istanza di **PostgreSQL** attiva.
2. Crea un database vuoto (es. `bookrecommenderdb`).
3. Vai nella cartella `data`.
4. Esegui il file SQL `tablecreation.sql` con un client PostgreSQL come `psql`, **pgAdmin**, o un tool Java.
5. Esegui in ordine: 
   - `libri.sql`
   - `utenti.sql`
   - `librerie.sql`
   - `libreria_libro.sql`
   - `consigli.sql`
   - `valutazioni.sql`
6. Assicurati di inserire all'avvio del server le credenziali corrette.

---

## ▶️ Esecuzione del programma

### Server

- Avvia il file `Main_Server.java` nella cartella `bookrecommender.server`.

### Interfaccia Cliente

- Avvia il file `Main_Client.java` nella cartella `bookrecommender.client`.
- Questo caricherà l’interfaccia grafica (FXML), gli stili (CSS) e le icone.

> ⚠️ Assicurati che il server sia attivo prima di avviare il client.

---

## 📚 Dettagli tecnici

- `CliUtil.java`: contiene metodi comuni e variabili di configurazione globali (singleton).
- I controller associati agli FXML si trovano nel package `bookrecommender.client`.
- Le interfacce di comunicazione e le classi serializzabili si trovano in `bookrecommender.common`.
- Le relative implementazioni lato server si trovano in `bookrecommender.server`.

---

## 🧪 Requisiti tecnici

- Java 17
- PostgreSQL ≥ 12
- IDE consigliato: IntelliJ IDEA o Eclipse
- Librerie elencate nella sezione **Dipendenze**

---

## 📌 Note finali

- Assicurati che i percorsi relativi siano rispettati.
- Le connessioni al database sono gestite via JDBC tramite il driver PostgreSQL.
- Tutti gli FXML devono avere il rispettivo controller associato dichiarato correttamente nel tag `fx:controller`.

---
