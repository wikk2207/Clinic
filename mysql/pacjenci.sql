USE `Projekt_sql`;

DROP TABLE IF EXISTS pacjenci;
DROP PROCEDURE IF EXISTS fill_p;
CREATE TABLE pacjenci (p_id int PRIMARY KEY AUTO_INCREMENT, imie varchar(45), nazwisko varchar(45), pesel char(11), data_urodzenia date);
ALTER TABLE pacjenci AUTO_INCREMENT = 1000;

DELIMITER //
CREATE PROCEDURE fill_p()
BEGIN
	DECLARE id INT DEFAULT 1;
    DECLARE data_p DATE;
    WHILE id <= 2000 DO
		SET data_p = DATE_FORMAT(FROM_UNIXTIME(RAND()*(UNIX_TIMESTAMP('1940-01-01')-UNIX_TIMESTAMP('2019-01-01')) + UNIX_TIMESTAMP('2019-01-01')), '%Y-%m-%d');
		INSERT INTO pacjenci(imie, nazwisko, pesel, data_urodzenia) VALUES(
        CONCAT(CHAR(ROUND(65 + (RAND()*25))), CHAR(ROUND(65 + (RAND()*25))), CHAR(ROUND(65 + (RAND()*25))), CHAR(ROUND(65 + (RAND()*25))), CHAR(ROUND(65 + (RAND()*25)))),
        CONCAT(CHAR(ROUND(65 + (RAND()*25))), CHAR(ROUND(65 + (RAND()*25))), CHAR(ROUND(65 + (RAND()*25))), CHAR(ROUND(65 + (RAND()*25))), CHAR(ROUND(65 + (RAND()*25)))),
        CONCAT(RIGHT(YEAR(data_p),2), RIGHT(CONCAT('0', MONTH(data_p)), 2), RIGHT(CONCAT('0', DAY(data_p)), 2), ROUND(RAND() * (99999-10000) + 10000)),
		data_p);
        SET id = id + 1;
    END WHILE;
END //
DELIMITER ;

CALL fill_p();
DROP PROCEDURE fill_p;