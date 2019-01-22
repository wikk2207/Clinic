USE `projekt_sql`;
DROP TABLE IF EXISTS Godziny;
CREATE TABLE Godziny (g_id int PRIMARY KEY AUTO_INCREMENT, poczatek time , koniec time);

DROP PROCEDURE IF EXISTS fill_godziny;
DELIMITER $$
CREATE PROCEDURE fill_godziny()
BEGIN
	SET @i = 0;
    SET @poczatek = '08:00:00';
    SET @koniec = '08:15:00';
    WHILE @i < 24 DO
		INSERT INTO Godziny (poczatek, koniec) VALUES (@poczatek, @koniec);
        SET @poczatek = @koniec;
        SET @koniec = ADDTIME(@koniec, "00:15:00");
		SET @i = @i + 1;
	END WHILE;
END $$
DELIMITER ;

CALL fill_godziny();
DROP PROCEDURE fill_godziny;