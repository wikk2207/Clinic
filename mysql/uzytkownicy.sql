USE `projekt_sql`;
DROP TABLE IF EXISTS Uzytkownicy;
CREATE TABLE Uzytkownicy (u_id int PRIMARY KEY, login VARCHAR(50), haslo VARCHAR(15));

DROP PROCEDURE IF EXISTS fill_uzytkownicy;
DELIMITER $$
CREATE PROCEDURE fill_uzytkownicy() 
BEGIN
	DECLARE done INT DEFAULT FALSE;
    DECLARE u_id INT;
	DECLARE u_imie VARCHAR(45);
    DECLARE u_login VARCHAR(50);
    DECLARE u_haslo VARCHAR(15); 
    
    DECLARE cur_pracownicy CURSOR FOR SELECT staff_id, imie FROM Pracownicy;
    DECLARE cur_pacjenci CURSOR FOR SELECT p_id, imie FROM Pacjenci;
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    OPEN cur_pracownicy;

    adding_loop1: LOOP
		FETCH cur_pracownicy INTO u_id, u_imie;
        IF done THEN
			LEAVE adding_loop1;
		END IF;
        SET u_login = CONCAT(u_imie,u_id);
        SET u_haslo = CONCAT(CHAR(ROUND(65 + (RAND()*25))),
							CHAR(ROUND(65 + (RAND()*25))),
                            CHAR(ROUND(65 + (RAND()*25))),
                            CHAR(ROUND(65 + (RAND()*25))),
                            CHAR(ROUND(65 + (RAND()*25))),
                            FLOOR(RAND()*10), FLOOR(RAND()*10));
        
        INSERT INTO Uzytkownicy VALUES (u_id, u_login, u_haslo);
	END LOOP;
    
    CLOSE cur_pracownicy;
    SET done = FALSE;
    OPEN cur_pacjenci;
    
    adding_loop2: LOOP
		FETCH cur_pacjenci INTO u_id, u_imie;
        IF done THEN
			LEAVE adding_loop2;
		END IF;
        SET u_login = CONCAT(u_imie,u_id);
        SET u_haslo = CONCAT(CHAR(ROUND(65 + (RAND()*25))), CHAR(ROUND(65 + (RAND()*25))), CHAR(ROUND(65 + (RAND()*25))), CHAR(ROUND(65 + (RAND()*25))), CHAR(ROUND(65 + (RAND()*25))), FLOOR(RAND()*10), FLOOR(RAND()*10));
        
        INSERT INTO Uzytkownicy VALUES (u_id, u_login, u_haslo);
	END LOOP;
    
    CLOSE cur_pacjenci;
    
END $$
DELIMITER ;

CALL fill_uzytkownicy();
DROP PROCEDURE fill_uzytkownicy;