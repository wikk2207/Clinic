USE `Projekt_sql`;
DROP TABLE IF EXISTS Wyniki_zawartosc;
DROP TABLE IF EXISTS Badania;
CREATE TABLE Badania (b_id int PRIMARY KEY AUTO_INCREMENT, nazwa varchar(45), min_wartosc float, max_wartosc float, jednostka varchar(10), uwagi varchar(100) DEFAULT NULL);

DROP TABLE IF EXISTS Jednostki;
CREATE TABLE Jednostki (id int primary key auto_increment, nazwa varchar(10));
INSERT INTO Jednostki (nazwa) VALUES ("mg/dl"), ("g/dl"),("U/l"), ("pg"),("um");
DROP PROCEDURE IF EXISTS fill_badania;

DELIMITER $$
CREATE PROCEDURE fill_badania() 
BEGIN 
	SET @i = 0;
    WHILE @i < 1000 DO

    SET @jednostka_id = FLOOR((RAND()*5)+1);
    
    SET @nazwa =  CONCAT(CHAR(ROUND(65 + (RAND()*25))), CHAR(ROUND(65 + (RAND()*25))), CHAR(ROUND(65 + (RAND()*25))), CHAR(ROUND(65 + (RAND()*25))), CHAR(ROUND(65 + (RAND()*25))));
    SET @min_wart = RAND()*200;
    SET @max_wart = RAND()*300;
    SET @jednostka = (SELECT nazwa FROM Jednostki WHERE id = @jednostka_id);
    
    IF(@min_wart<@max_wart) THEN
		INSERT INTO Badania (nazwa,min_wartosc, max_wartosc, jednostka) VALUES (@nazwa, @min_wart, @max_wart, @jednostka);
    ELSE 
		INSERT INTO Badania (nazwa,min_wartosc, max_wartosc, jednostka) VALUES (@nazwa, @max_wart, @min_wart, @jednostka);
	END IF;
    SET @i = @i + 1;
	END WHILE;
END $$
DELIMITER ;

CALL fill_badania();
DROP PROCEDURE fill_badania;
DROP TABLE Jednostki;
