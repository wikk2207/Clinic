USE `Projekt_sql`;
DROP table if exists wizyty;
drop table if exists wyniki_zawartosc;
drop table if exists wyniki_badan;
DROP TABLE IF EXISTS Pracownicy;
DROP PROCEDURE IF EXISTS fill_staff;
CREATE TABLE Pracownicy (staff_id int PRIMARY KEY AUTO_INCREMENT, imie varchar(45), nazwisko varchar(45), typ ENUM("admin", "lekarz", "sekretarka"), specjalizacja varchar(45) DEFAULT NULL);


DROP TABLE IF EXISTS Przyklady_pracownikow;
CREATE table Przyklady_pracownikow(id int NOT NULL auto_increment, imie varchar (45), nazwisko varchar(45),
specjalizacja varchar(45), primary key (id));
INSERT INTO Przyklady_pracownikow (imie, nazwisko,specjalizacja)
VALUES	("Agnieszka","Mazur", "endokrynolog"),
        ("Magdalena", "Wieczorek", "pediatra"),
        ("Hanna","Dudek","dermatolog"),
		("Anna","Baran","lekarz rodzinny"),
		("Jan"," Kowalski", "pediatra"),
        ("Marek","Nowak", "ginekolog"),
        ("Pawel", "Kaczmarek", "dermatolog"),
        ("Krzysztof", "Adamczyk", "okulista"),
        ("Adam","Walczak","alergolog"),
        ("Wojciech","Sikora","lekarz rodzinny");

DELIMITER //
CREATE PROCEDURE fill_staff()
BEGIN
	SET @id =3;
	INSERT INTO Pracownicy(imie, nazwisko, typ, specjalizacja) VALUES ("Wiktoria", "S", "admin", NULL);
    INSERT INTO Pracownicy(imie, nazwisko, typ, specjalizacja) VALUES ("Filip", "R", "admin", NULL); 
    WHILE @id <= 30 DO
    SET @imie_id = FLOOR(RAND()*(10))+1;
    SET @nazwisko_id = FLOOR(RAND()*(10))+1;
    SET @specjalizacja_id = FLOOR(RAND()*(10))+1;
    
    SET @imie = (SELECT imie FROM Przyklady_pracownikow WHERE id=@imie_id);
    SET @nazwisko = (SELECT nazwisko FROM Przyklady_pracownikow WHERE id=@nazwisko_id);
    SET @specjalizacja = (SELECT specjalizacja FROM Przyklady_pracownikow WHERE id=@specjalizacja_id);
    
    INSERT INTO Pracownicy (imie, nazwisko, typ, specjalizacja) VALUES (@imie, @nazwisko, "lekarz", @specjalizacja);
    
    SET @id = @id + 1;
    END WHILE;
    
    WHILE  @id <= 40 DO
	SET @imie_id = FLOOR(RAND()*(4))+1;
    SET @nazwisko_id = FLOOR(RAND()*(10))+1;
    
    SET @imie = (SELECT imie FROM Przyklady_pracownikow WHERE id=@imie_id);
    SET @nazwisko = (SELECT nazwisko FROM Przyklady_pracownikow WHERE id=@nazwisko_id);
    
    INSERT INTO Pracownicy(imie, nazwisko, typ) VALUES (@imie, @nazwisko, "sekretarka");
    
	SET @id = @id + 1;
    END WHILE;
END //
DELIMITER ;

CALL fill_staff();
DROP PROCEDURE fill_staff;
DROP TABLE Przyklady_pracownikow;