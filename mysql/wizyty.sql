USE `Projekt_sql`;
DROP TABLE IF EXISTS Wizyty;
CREATE TABLE Wizyty (w_id int PRIMARY KEY AUTO_INCREMENT, id_pacjenta int , id_lekarza int, termin_wizyty date,
FOREIGN KEY (id_pacjenta) REFERENCES Pacjenci(p_id), FOREIGN KEY (id_lekarza) REFERENCES Pracownicy(staff_id) );