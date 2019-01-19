USE `Projekt_sql`;
DROP TABLE IF EXISTS wyniki_zawartosc;
CREATE TABLE wyniki_zawartosc (id_wynikow int, id_badania int , wartosc float, poprzednia_wartosc float, uwagi varchar(100),
FOREIGN KEY (id_wynikow) REFERENCES wyniki_badan(wb_id), FOREIGN KEY (id_badania) REFERENCES badania(b_id) );