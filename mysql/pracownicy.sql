USE `Projekt_sql`;
DROP TABLE IF EXISTS pracownicy;
DROP PROCEDURE IF EXISTS fill_staff;
CREATE TABLE pracownicy (staff_id int PRIMARY KEY AUTO_INCREMENT, imie varchar(45), nazwisko varchar(45), typ ENUM("admin", "lekarz", "sekretarka"), specjalizacja varchar(45));

DELIMITER //
CREATE PROCEDURE fill_staff()
BEGIN
	DECLARE id INT DEFAULT 3;
	INSERT INTO pracownicy(imie, nazwisko, typ, specjalizacja) VALUES ("Wiktoria", "S", "admin", "brak");
    INSERT INTO pracownicy(imie, nazwisko, typ, specjalizacja) VALUES ("Filip", "R", "admin", "brak"); 
    WHILE id <= 30 DO
		INSERT INTO pracownicy(imie, nazwisko, typ, specjalizacja) VALUES(
        CONCAT(CHAR(ROUND(65 + (RAND()*25))), CHAR(ROUND(65 + (RAND()*25))), CHAR(ROUND(65 + (RAND()*25))), CHAR(ROUND(65 + (RAND()*25))), CHAR(ROUND(65 + (RAND()*25)))),
        CONCAT(CHAR(ROUND(65 + (RAND()*25))), CHAR(ROUND(65 + (RAND()*25))), CHAR(ROUND(65 + (RAND()*25))), CHAR(ROUND(65 + (RAND()*25))), CHAR(ROUND(65 + (RAND()*25)))),
        "lekarz",
        CONCAT(CHAR(ROUND(65 + (RAND()*25))), CHAR(ROUND(65 + (RAND()*25))), CHAR(ROUND(65 + (RAND()*25))), CHAR(ROUND(65 + (RAND()*25))), CHAR(ROUND(65 + (RAND()*25)))));
        SET id = id + 1;
    END WHILE;
    
    WHILE id <= 40 DO
		INSERT INTO pracownicy(imie, nazwisko, typ, specjalizacja) VALUES(
        CONCAT(CHAR(ROUND(65 + (RAND()*25))), CHAR(ROUND(65 + (RAND()*25))), CHAR(ROUND(65 + (RAND()*25))), CHAR(ROUND(65 + (RAND()*25))), CHAR(ROUND(65 + (RAND()*25)))),
        CONCAT(CHAR(ROUND(65 + (RAND()*25))), CHAR(ROUND(65 + (RAND()*25))), CHAR(ROUND(65 + (RAND()*25))), CHAR(ROUND(65 + (RAND()*25))), CHAR(ROUND(65 + (RAND()*25)))),
        "sekretarka",
        "brak");
        SET id = id + 1;
    END WHILE;
END //
DELIMITER ;

CALL fill_staff();
DROP PROCEDURE fill_staff;