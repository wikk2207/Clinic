-- SHOW TABLES;

-- #SELECT * FROM Pacjenci;

-- SELECT * FROM Wyniki_badan WHERE id_pacjenta =500;

SELECT * FROM Pracownicy;

-- SELECT * FROM Badania;

-- SELECT * FROM Wyniki_badan;

-- #SET @id = 365;
-- #EXECUTE wyniki_pacjenta USING @id;
-- INSERT INTO Wyniki_zawartosc (id_wynikow, id_badania, wartosc) VALUES (2,144,45);
-- SELECT * FROM Wyniki_zawartosc;
-- SELECT * FROM Wyniki_zawartosc WHERE poprzednia_wartosc IS NOT NULL;

#INSERT INTO Wyniki_zawartosc (id_wynikow, id_badania, wartosc) VALUES (2,2,23);

#SELECT * FROM Wyniki_zawartosc;
#ELECT id_badania, COUNT(id_badania) FROM Wyniki_zawartosc WHERE id_wynikow = 790 OR id_wynikow = 1694 OR id_wynikow = 3413 OR id_wynikow = 3154 GROUP BY id_badania;
#SELECT DISTINCT id_badania FROM Wyniki_zawartosc WHERE id_wynikow = 790 OR id_wynikow = 1694 OR id_wynikow = 3413 OR id_wynikow = 3154;

SELECT * FROM Godziny;
SELECT * FROM Wizyty;
