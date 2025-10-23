CREATE TABLE canciones
(
    id_cancion INT PRIMARY KEY,
    titulo     VARCHAR(255),
    artista    VARCHAR(255),
    duracion   DECIMAL(2, 2)
);

INSERT INTO canciones
VALUES (1, "Master of Puppets", "Metallica", 8.5);
INSERT INTO canciones
VALUES (2, "So payaso", "Extremoduro", 5);
INSERT INTO canciones
VALUES (3, "Hey Joe", "Jimi Hendrix", 4.2);
INSERT INTO canciones
VALUES (4, "Enter Sandman", "Metallica", 6.35);

SELECT *
FROM canciones;

DROP TABLE canciones;

CREATE TABLE artistas
(
    id_artista INT PRIMARY KEY,
    nombre     VARCHAR(255)
);

CREATE TABLE canciones
(
    id_cancion INT PRIMARY KEY,
    titulo     VARCHAR(255),
    id_artista INT,
    duracion   DECIMAL(2, 2),
    FOREIGN KEY (id_artista) REFERENCES artistas (id_artista)
);

INSERT INTO artistas
VALUES (1, "Metallica");
INSERT INTO artistas
VALUES (2, "Extremoduro");
INSERT INTO artistas
VALUES (3, "Jimi Hendrix");

INSERT INTO canciones
VALUES (1, "Master of Puppets", 1, 8.5);
INSERT INTO canciones
VALUES (2, "So payaso", 2, 5);
INSERT INTO canciones
VALUES (3, "Hey Joe", 3, 4.2);
INSERT INTO canciones
VALUES (4, "Enter Sandman", 1, 6.35);

SELECT c.titulo, a.nombre
FROM canciones c
         INNER JOIN artistas a ON a.id_artista = c.id_artista
WHERE a.nombre = "Metallica";
