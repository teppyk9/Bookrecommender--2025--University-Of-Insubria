INSERT INTO LIBRI (TITOLO, AUTORE, DESCRIZIONE, CATEGORIA, EDITORE, PREZZO, ANNOPUBBLICAZIONE, MESEPUBBLICAZIONE) VALUES
('Il Signore degli Anelli', 'J.R.R. Tolkien', 'Un classico del fantasy', 'Fantasy', 'Bompiani', 24.5, 1954, 7),
('1984', 'George Orwell', 'Distopia totalitaria', 'Fantascienza', 'Mondadori', 15.9, 1949, 6),
('Il Nome della Rosa', 'Umberto Eco', 'Thriller storico ambientato in un monastero', 'Giallo', 'Bompiani', 19.9, 1980, 3),
('Harry Potter e la Pietra Filosofale', 'J.K. Rowling', 'Il primo anno di Harry a Hogwarts', 'Fantasy', 'Salani', 17.0, 1997, 6),
('Siddharta', 'Hermann Hesse', 'Romanzo filosofico', 'Narrativa', 'Adelphi', 13.5, 1922, 9),
('Il piccolo principe', 'Antoine de Saint-Exupéry', 'Favola poetica', 'Narrativa', 'Bompiani', 10.0, 1943, 4),
('Il Gattopardo', 'Giuseppe Tomasi di Lampedusa', 'Romanzo storico siciliano', 'Narrativa', 'Feltrinelli', 18.0, 1958, 11),
('La coscienza di Zeno', 'Italo Svevo', 'Romanzo psicologico', 'Narrativa', 'Einaudi', 16.5, 1923, 1),
('Il barone rampante', 'Italo Calvino', 'Romanzo fantastico e avventuroso', 'Narrativa', 'Mondadori', 14.0, 1957, 10),
('Cento anni di solitudine', 'Gabriel García Márquez', 'Romanzo epico e magico', 'Narrativa', 'Mondadori', 22.0, 1967, 5);

INSERT INTO UTENTI (USERNAME, NOME, COGNOME, CODICE_FISCALE, EMAIL, PASSWORD) VALUES
('mario123', 'Mario', 'Rossi', 'RSSMRA80A01H501U', 'mario@example.com', 'password123'),
('luca_b', 'Luca', 'Bianchi', 'BCHLCU85B15F205Z', 'luca@example.com', 'pass456'),
('sara_s', 'Sara', 'Sartori', 'SRTSRA90C30L219X', 'sara@example.com', 'securePass'),
('anna89', 'Anna', 'Verdi', 'VRDANN89M45F205H', 'anna@example.com', 'abc12345');

INSERT INTO VALUTAZIONI (IDLIBRO, USERNAME, C_STILE, V_STILE, C_CONTENUTO, V_CONTENUTO, C_GRADEVOLEZZA, V_GRADEVOLEZZA, C_ORIGINALITA, V_ORIGINALITA, C_EDIZIONE, V_EDIZIONE, C_FINALE) VALUES
(1, 'mario123', 'Scrittura epica', 5, 'Trama coinvolgente', 5, 'Piacevolissimo', 5, 'Molto originale', 5, 'Ottima edizione', 5, 'Capolavoro'),
(2, 'luca_b', 'Stile diretto', 4, 'Messaggio forte', 5, 'Molto interessante', 4, 'Inquietante e originale', 5, 'Buona edizione', 4, 'Distopico'),
(3, 'sara_s', 'Lento ma raffinato', 4, 'Contenuti profondi', 4, 'Gradevole', 3, 'Molto originale', 5, 'Ben curato', 4, 'Ottimo testo'),
(4, 'anna89', 'Stile per ragazzi', 4, 'Trama leggera', 4, 'Molto gradevole', 4, 'Originale per l’epoca', 4, 'Buona stampa', 4, 'Magico'),
(5, 'mario123', 'Stile semplice', 3, 'Temi profondi', 4, 'Molto personale', 4, 'Classico', 3, 'Edizione essenziale', 3, 'Filosofico');

INSERT INTO CONSIGLI (IDLIBRO, USERNAME, LIB_1, LIB_2, LIB_3) VALUES
(1, 'mario123', 4, 2, 5),
(2, 'luca_b', 3, 1, NULL),
(3, 'sara_s', 5, NULL, NULL);

INSERT INTO LIBRERIE (USERNAME, TITOLO_LIBRERIA) VALUES
('mario123', 'Fantasy preferiti'),
('sara_s', 'Classici da leggere'),
('anna89', 'La mia libreria');

INSERT INTO LIBRERIA_LIBRO (IDLIBRERIA, IDLIBRO) VALUES
(1, 1), (1, 4),
(2, 2), (2, 5),
(3, 3), (3, 1);