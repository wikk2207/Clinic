USE `projekt_sql`;

DROP TABLE IF EXISTS Pacjenci;
DROP PROCEDURE IF EXISTS fill_p;
CREATE TABLE Pacjenci (p_id int PRIMARY KEY AUTO_INCREMENT, imie varchar(45), nazwisko varchar(45), pesel char(11), data_urodzenia date);

DROP TABLE IF EXISTS Przyklady_pacjentow;
CREATE table Przyklady_pacjentow (id int NOT NULL auto_increment, imie varchar (45), nazwisko varchar(45), primary key (id));
INSERT INTO Przyklady_pacjentow (imie, nazwisko)
VALUES	("Agnieszka","Mazur"),
        ("Magdalena", "Wieczorek"),
        ("Hanna","Dudek"),
		("Anna","Baran"),
        ("Barbara","Pawlak"),
        ("Danuta","Krawczyk"),
        ("Karolina","Kowalczyk"),
        ("Weronika","Michalak"),
        ("Paulina","Krawiec"),
		("Jan"," Kowalski"),
        ("Marek","Nowak"),
        ("Pawel", "Kaczmarek"),
        ("Krzysztof", "Adamczyk"),
        ("Adam","Walczak"),
        ("Andrzej", "Matera"),
        ("Bartosz", "Pietrzak"),
        ("Szymon", "Szewczyk"),
        ("Wojciech","Sikora");

DELIMITER //
CREATE PROCEDURE fill_p()
BEGIN
	SET @id =1;
    WHILE @id <= 2000 DO
    
	SET @imie_id = FLOOR(RAND()*(18))+1;
    SET @nazwisko_id = FLOOR(RAND()*(18))+1;
    
    SET @imie = (SELECT imie FROM Przyklady_pacjentow WHERE id=@imie_id);
    SET @nazwisko = (SELECT nazwisko FROM Przyklady_pacjentow WHERE id=@nazwisko_id);
        
	SET @data_p = DATE_FORMAT(FROM_UNIXTIME(RAND()*(UNIX_TIMESTAMP('1940-01-01')-UNIX_TIMESTAMP('2019-01-01')) + UNIX_TIMESTAMP('2019-01-01')), '%Y-%m-%d');
	
    INSERT INTO Pacjenci(imie, nazwisko, pesel, data_urodzenia) VALUES(
        @imie, @nazwisko,
        CONCAT(RIGHT(YEAR(@data_p),2), RIGHT(CONCAT('0', MONTH(@data_p)), 2), RIGHT(CONCAT('0', DAY(@data_p)), 2), ROUND(RAND() * (99999-10000) + 10000)),
		@data_p);
        SET @id = @id + 1;
    END WHILE;
END //
DELIMITER ;

CALL fill_p();
DROP PROCEDURE fill_p;
DROP TABLE Przyklady_pacjentow;
