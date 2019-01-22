USE `projekt_sql`;
DROP TABLE IF EXISTS Wyniki_badan;
CREATE TABLE Wyniki_badan (wb_id int PRIMARY KEY AUTO_INCREMENT, data_wystawienia DATETIME, id_pacjenta int, id_lekarza int,
FOREIGN KEY (id_pacjenta) REFERENCES Pacjenci(p_id), FOREIGN KEY (id_lekarza) REFERENCES Pracownicy(staff_id) );


DROP PROCEDURE IF EXISTS fill_wb;
DELIMITER $$
CREATE PROCEDURE fill_wb() 
BEGIN
	SET @i = 1; #id pacjenta
    
    WHILE @i < 2001 DO #dla kazdego pacjenta
		SET @rand_days = FLOOR((RAND()*1000)+200);
		SET @data_wystawienia = DATE_SUB(NOW(), INTERVAL @rand_days DAY);
        SET @j = 0;
        WHILE @j < 5 DO #dodaj po 5 wynikow 
			SET @id_lekarza = FLOOR((RAND()*27)+3);
			INSERT INTO Wyniki_badan (data_wystawienia, id_pacjenta, id_lekarza) VALUES
				(@data_wystawienia, @i, @id_lekarza);
			SET @data_wystawienia = DATE_ADD(@data_wystawienia, INTERVAL 30 DAY); #wyniki co 30 dni
			SET @j = @j + 1;
        END WHILE;
		SET @i = @i + 1;
	END WHILE;
END $$
DELIMITER ;

CALL fill_wb();
DROP PROCEDURE fill_wb;