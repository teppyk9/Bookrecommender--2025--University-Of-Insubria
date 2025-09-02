[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

<img src="client/src/main/resources/icons/SigilloAteneoTestoColori.svg" style="float: right; width: 250px;" alt="Insubria Logo">

# 📚 BookRecommender

**Progetto universitario per l'esame di Laboratorio Interdisciplinare B – Università degli Studi dell’Insubria (2025)**

Sistema client–server scritto in **Java 17** che permette di gestire un database di libri, valutazioni e librerie personali, con interfaccia grafica JavaFX e backend RMI su PostgreSQL.

Manuale Tecnico, Manuale Utente, Diagrammi UML e JavaDoc disponibili in `/doc`.

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
3. Dopo aver compilato il progetto (`mvn clean package`), esegui DBCreator. Sono disponibili script per l'esecuzione per Windows, Linux e MacOS. In alternativa trovi i file .jar in `/bin`.
4. Se si desidera testare con un database vuoto utilizzare la modalità `basic`, in caso contrario utilizzare la modalità `full` per popolare il database con dei dati/utente di test.
5. Le credenziali dell'utente test sono:
   - Username: `test`
   - Password: `testtest`
### 2. Configurazione progetto
- Java **17**
- IDE consigliato: **IntelliJ IDEA Ultimate** (già configurato con `pom.xml`)
- Moduli:
   - Server: `bookrecommender.server.Main_Server`
   - Client: `bookrecommender.client.Main_Client`
   - DBCreator: `bookrecommender.DBCreator`
---

## ▶️ Avvio

### Avvio da IDE
- **Server** → esegui `Main_Server.java`
- **Client** → esegui `Main_Client.java`
- **DBCreator** → esegui `DBCreator.java`

In alternativa, avvio manuale tramite gli script forniti:
- **Windows**: `Windows-serverBR.cmd`, `Windows-clientBR.cmd`, `Windows-DBCreator.cmd`
- **Linux**: `Linux-serverBR.sh`, `Linux-clientBR.sh`, `Linux-DBCreator.sh`
- **MacOS-Intel**: `MacOS-Intel-serverBR.sh`, `MacOS-Intel-clientBR.sh`, `MacOS-Intel-DBCreator.sh`
- **MacOS-aarch64**: `MacOS-aarch64-serverBR.sh`, `MacOS-aarch64-clientBR.sh`, `MacOS-aarch64-DBCreator.sh`

---

## 🧪 Build & Packaging

Per generare runtime e installer multipiattaforma:

- **Build Maven**
```bash
   mvn clean package
```
---

## 📌 Note finali

- Assicurati che il **server** sia attivo prima di avviare il **client**.