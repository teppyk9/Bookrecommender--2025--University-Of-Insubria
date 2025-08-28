[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

<img src="client/src/main/resources/icons/SigilloAteneoTestoColori.svg" style="float: right; width: 250px;" alt="Insubria Logo">

# 📚 BookRecommender

**Progetto universitario per l'esame di Laboratorio Interdisciplinare B – Università degli Studi dell’Insubria (2025)**

Sistema client–server scritto in **Java 17** che permette di gestire un database di libri, valutazioni e librerie personali, con interfaccia grafica JavaFX e backend RMI su PostgreSQL.

---

## 👥 Autori

- **Gianmarco Maffioli 757587 VA** – Project Manager
- **Francesca Rolla 757922 VA** – UI/UX Designer
- **Gabriele Fabbian 755699 VA** – Technical Documentation & Complexity Analyst

---

## 📦 Dipendenze principali

Il progetto utilizza **Maven** per la gestione delle dipendenze.  
Le versioni esatte sono specificate in `pom.xml`.

| Libreria | Versione |
|----------|----------|
| `org.openjfx:javafx-*` | 17.0.15  |
| `org.postgresql:postgresql` | 42.7.5   |
| `com.zaxxer:HikariCP` | 5.1.0    |
| `org.slf4j:slf4j-api` | 2.0.17   |


📌 Se usi **Maven**, le librerie verranno scaricate automaticamente (`mvn clean install`).  
In alternativa puoi scaricarle da [Maven Central](https://search.maven.org/).

---

## ⚙️ Setup

### 1. Database PostgreSQL
1. Scarica, se non la possiedi, un’istanza **PostgreSQL** dal sito ufficiale https://www.postgresql.org/.
2. Configura le credenziali di accesso.
3. Avvia **setupDB.bat** per creare il database e le tabelle necessarie.
### 2. Configurazione progetto
- Java **17**
- IDE consigliato: **IntelliJ IDEA Ultimate** (già configurato con `pom.xml`)
- Modulo principale:
   - Server: `bookrecommender.server.Main_Server`
   - Client: `bookrecommender.client.Main_Client`

---

## ▶️ Avvio

### Avvio da IDE
- **Server** → esegui `Main_Server.java`
- **Client** → esegui `Main_Client.java`

### Avvio da distribuzione
Se hai creato i runtime con `jpackage`, puoi avviare:
```bash
dist/BookRecommenderSrv/BookRecommenderSrv.exe   # Server (Windows)
dist/BookRecommenderCli/BookRecommenderCli.exe   # Client (Windows)
```

In alternativa, avvio manuale con `.bat`:
```bat
runtime\bin\java -jar app\client-1.0.0.jar
runtime\bin\java -jar app\server-1.0.0.jar
```

---

## 🧪 Build & Packaging

Per generare runtime e installer multipiattaforma:

- **Build Maven**
```bash
mvn clean package
```

- **Runtime custom (jlink)**
```bash
jlink --module-path %JAVA_HOME%\jmods --add-modules javafx.controls,javafx.fxml --output runtime
```

- **Installer (jpackage)**
```bash
jpackage --name BookRecommenderCli --type app-image --app-version 1.0.0  --runtime-image runtime --input target/ --main-jar client-1.0.0.jar  --icon icons/icon.ico
```

📌 Sono disponibili script `.bat` di esecuzione rapida del client/server.

---

## 📌 Note finali

- Assicurati che il **server** sia attivo prima di avviare il **client**.
- Tutti i file `.fxml` hanno controller associati in `bookrecommender.client`.
- Connessioni DB gestite tramite **HikariCP** + driver PostgreSQL.
- Documentazione tecnica aggiuntiva in `/doc`.

---

## 📄 Licenza

Questo progetto è distribuito con licenza [MIT](LICENSE).
