USE `projekt_sql`;
DROP TABLE IF EXISTS Wizyty;
CREATE TABLE Wizyty (w_id int PRIMARY KEY AUTO_INCREMENT, id_pacjenta int , id_lekarza int, data_wizyty date, godzina_wizyty int,
FOREIGN KEY (id_pacjenta) REFERENCES Pacjenci(p_id), FOREIGN KEY (id_lekarza) REFERENCES Pracownicy(staff_id), FOREIGN KEY (godzina_wizyty) REFERENCES Godziny(g_id));

DROP PROCEDURE IF EXISTS fill_wizyty;
DELIMITER $$
CREATE PROCEDURE fill_wizyty() 
BEGIN
	SET @i = 0;
   
    SET @data_wizyty = DATE_SUB(CURDATE(), INTERVAL 20 DAY);
    
    WHILE @i < 60 DO
		SET @j = 0;
		WHILE @j < 20 DO
			SET @id_pacjenta = FLOOR((RAND()*2000)+1);
            SET @id_lekarza = FLOOR((RAND()*27)+3);
            SET @godzina_wizyty = FLOOR((RAND()*24)+1);
        
			INSERT INTO Wizyty (id_pacjenta, id_lekarza, data_wizyty, godzina_wizyty)
				VALUES (@id_pacjenta, @id_lekarza, @data_wizyty, @godzina_wizyty);
        
			SET @j = @j +1;
        END WHILE;
        SET @data_wizyty = DATE_ADD(@data_wizyty, INTERVAL 1 DAY);
		SET @i = @i + 1;
    END WHILE;

END $$
DELIMITER ;

CALL fill_wizyty();
DROP PROCEDURE fill_wizyty;