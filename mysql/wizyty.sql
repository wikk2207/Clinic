USE `Projekt_sql`;
DROP TABLE IF EXISTS wizyty;
CREATE TABLE wizyty (w_id int PRIMARY KEY AUTO_INCREMENT, id_pacjenta int , id_lekarza int, data_wizyty date,
FOREIGN KEY (id_pacjenta) REFERENCES pacjenci(p_id), FOREIGN KEY (id_lekarza) REFERENCES pracownicy(staff_id) );