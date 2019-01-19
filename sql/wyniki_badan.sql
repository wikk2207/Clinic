USE `Projekt_sql`;
DROP TABLE IF EXISTS wyniki_badan;
CREATE TABLE wyniki_badan (wb_id int PRIMARY KEY AUTO_INCREMENT, data_wystawienia DATETIME, id_pacjenta int, id_lekarza int,
FOREIGN KEY (id_pacjenta) REFERENCES pacjenci(p_id), FOREIGN KEY (id_lekarza) REFERENCES pracownicy(staff_id) );