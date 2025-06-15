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

INSERT INTO librerie (id, id_utente, titolo_libreria) VALUES
(1, 1, 'Libreria_1'),
(2, 2, 'Libreria_2'),
(3, 3, 'Libreria_3'),
(6, 1, 'Libreria_6');

INSERT INTO libreria_libro (idlibreria, idlibro) VALUES
(1, 47095),
(1, 87310),
(1, 6213),
(1, 66965),
(1, 5685),
(2, 47789),
(2, 44708),
(2, 51170),
(2, 61087),
(2, 64620),
(3, 22175),
(3, 86462),
(3, 52664),
(3, 42756),
(3, 12742),
(4, 48802),
(4, 86913),
(4, 13309),
(4, 43341),
(4, 37707),
(5, 78857),
(5, 5344),
(5, 31025),
(5, 37016),
(5, 7618),
(6, 93755),
(6, 13334),
(6, 65869),
(6, 30107),
(6, 1952);

INSERT INTO valutazioni (
    idlibro, id_utente, c_stile, v_stile, c_contenuto, v_contenuto,
    c_gradevolezza, v_gradevolezza, c_originalita, v_originalita,
    c_edizione, v_edizione, c_finale
) VALUES
      (13309, 4, 'Stile valido', 3, 'Contenuto interessante', 1, 'Gradevole', 2, 'Originale', 1, 'Buona edizione', 2,'Bel libro'),
      (31025, 5, 'Stile valido', 4, 'Contenuto interessante', 3, 'Gradevole', 4, 'Originale', 5, 'Buona edizione', 1,'Bel libro'),
      (37707, 4, 'Stile valido', 5, 'Contenuto interessante', 4, 'Gradevole', 5, 'Originale', 2, 'Buona edizione', 2,'Bel libro'),
      (1952, 1, 'Stile valido', 2, 'Contenuto interessante', 5, 'Gradevole', 2, 'Originale', 4, 'Buona edizione', 3,'Bel libro'),
      (87310, 1, 'Stile valido', 2, 'Contenuto interessante', 4, 'Gradevole', 2, 'Originale', 4, 'Buona edizione', 2,'Bel libro'),
      (42756, 3, 'Stile valido', 4, 'Contenuto interessante', 1, 'Gradevole', 3, 'Originale', 3, 'Buona edizione', 4,'Bel libro'),
      (12742, 3, 'Stile valido', 4, 'Contenuto interessante', 4, 'Gradevole', 5, 'Originale', 4, 'Buona edizione', 1,'Bel libro'),
      (61087, 2, 'Stile valido', 3, 'Contenuto interessante', 3, 'Gradevole', 4, 'Originale', 4, 'Buona edizione', 1,'Bel libro'),
      (52664, 3, 'Stile valido', 5, 'Contenuto interessante', 5, 'Gradevole', 4, 'Originale', 5, 'Buona edizione', 4,'Bel libro'),
      (86913, 4, 'Stile valido', 1, 'Contenuto interessante', 4, 'Gradevole', 1, 'Originale', 4, 'Buona edizione', 3,'Bel libro');

INSERT INTO consigli (idlibro, id_utente, lib_1, lib_2, lib_3) VALUES
(37707, 4, 86913, 43341, 48802),
(52664, 3, 12742, 22175, 86462),
(65869, 1, 87310, 6213, 30107),
(44708, 2, 47789, 61087, 64620),
(51170, 2, 61087, 64620, 44708),
(12742, 3, 86462, 22175, 52664),
(86913, 4, 48802, 43341, 37707),
(86913, 4, 43341, 37707, 13309),
(51170, 2, 64620, 47789, 44708),
(64620, 2, 47789, 51170, 44708);

INSERT INTO libreria_libro (idlibreria, idlibro) VALUES (5,42756);

INSERT INTO valutazioni (
    idlibro, id_utente, c_stile, v_stile, c_contenuto, v_contenuto,
    c_gradevolezza, v_gradevolezza, c_originalita, v_originalita,
    c_edizione, v_edizione, c_finale
) VALUES
      (42756, 5, 'Stile valido', 4, 'Contenuto interessante', 2, 'Gradevole', 3, 'Originale', 3, 'Buona edizione', 4,'Bel libro');


INSERT INTO libreria_libro (idlibreria, idlibro) VALUES (2,42756);

INSERT INTO valutazioni (
    idlibro, id_utente, c_stile, v_stile, c_contenuto, v_contenuto,
    c_gradevolezza, v_gradevolezza, c_originalita, v_originalita,
    c_edizione, v_edizione, c_finale
) VALUES
    (42756, 2, 'Stile valido', 2, 'Contenuto interessante', 4, 'Gradevole', 4, 'Originale', 4, 'Buona edizione', 2,'Bel libro');
