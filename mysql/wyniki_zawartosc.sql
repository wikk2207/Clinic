USE `projekt_sql`;
DROP TABLE IF EXISTS Wyniki_zawartosc;
CREATE TABLE Wyniki_zawartosc (id_wynikow int, id_badania int , wartosc float, poprzednia_wartosc float DEFAULT NULL, uwagi varchar(100),
FOREIGN KEY (id_wynikow) REFERENCES Wyniki_badan(wb_id), FOREIGN KEY (id_badania) REFERENCES Badania(b_id));

DROP PROCEDURE IF EXISTS fill_wz;
DELIMITER $$
CREATE PROCEDURE fill_wz() 
BEGIN
	SET @i = 1;
    WHILE @i < 10001 DO
		SET @ilosc_badan = FLOOR((RAND()*10)+10);
        SET @j = 0;
        WHILE @j < @ilosc_badan DO
			SET @id_badania = FLOOR((RAND()*1000)+1);
            SET @max_wart = (SELECT max_wartosc FROM Badania WHERE b_id = @id_badania);
            SET @gorny_zakres = @max_wart + 0.15*@max_wart;
			SET @dolny_zakres = @min_wart - 0.15*@min_wart;
            SET @min_wart = (SELECT min_wartosc FROM Badania WHERE b_id = @id_badania);
            SET @wart = RAND()*(@gorny_zakres - @dolny_zakres) + @dolny_zakres;
            
            INSERT INTO Wyniki_zawartosc (id_wynikow, id_badania, wartosc) VALUES (@i, @id_badania, @wart);
            SET @j = @j + 1;
        END WHILE;
		SET @i = @i + 1;
	END WHILE;
END $$
DELIMITER ;

CALL fill_wz();
DROP PROCEDURE fill_wz;

