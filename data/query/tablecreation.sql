create table libri
(
    id                serial
        primary key,
    titolo            text not null,
    autore            text not null,
    descrizione       text,
    categoria         text,
    editore           text,
    prezzo            numeric(3, 1),
    annopubblicazione smallint,
    mesepubblicazione smallint
);

alter table libri
    owner to postgres;

create table utenti
(
    id             serial
        primary key,
    username       text not null
        unique,
    nome           text not null,
    cognome        text not null,
    codice_fiscale text not null
        unique,
    email          text not null
        unique,
    password       text not null
);

alter table utenti
    owner to postgres;

create table valutazioni
(
    idlibro        integer
        references libri,
    id_utente      integer
        references utenti,
    c_stile        text,
    v_stile        smallint not null
        constraint valutazioni_v_stile_check
            check ((v_stile >= 1) AND (v_stile <= 5))
        constraint valutazioni_v_stile_check1
            check ((v_stile >= 1) AND (v_stile <= 5))
        constraint valutazioni_v_stile_check2
            check ((v_stile >= 1) AND (v_stile <= 5))
        constraint valutazioni_v_stile_check3
            check ((v_stile >= 1) AND (v_stile <= 5))
        constraint valutazioni_v_stile_check4
            check ((v_stile >= 1) AND (v_stile <= 5)),
    c_contenuto    text,
    v_contenuto    smallint not null,
    c_gradevolezza text,
    v_gradevolezza smallint not null,
    c_originalita  text,
    v_originalita  smallint not null,
    c_edizione     text,
    v_edizione     smallint not null,
    c_finale       text,
    v_finale       numeric generated always as ((
        ((((v_stile + v_contenuto) + v_gradevolezza) + v_originalita) + v_edizione) / 5)) stored
);

alter table valutazioni
    owner to postgres;

create table consigli
(
    idlibro   integer not null
        references libri,
    id_utente integer not null
        references utenti,
    lib_1     integer
        references libri,
    lib_2     integer
        references libri,
    lib_3     integer
        references libri
);

alter table consigli
    owner to postgres;

create table librerie
(
    id              serial
        primary key,
    id_utente       integer
        references utenti,
    titolo_libreria text not null
);

alter table librerie
    owner to postgres;

create table libreria_libro
(
    idlibreria integer not null
        references librerie
            on delete cascade,
    idlibro    integer not null
        references libri,
    primary key (idlibreria, idlibro)
);

alter table libreria_libro
    owner to postgres;

create table sessioni_login
(
    id         serial
        primary key,
    idutente   integer
        references utenti
            on delete cascade,
    ip_client  text not null,
    token      text not null
        unique,
    login_time timestamp default CURRENT_TIMESTAMP
);

alter table sessioni_login
    owner to postgres;

