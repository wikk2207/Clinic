DROP TRIGGER IF EXISTS dodawanie_uzytkownika;
DROP TRIGGER IF EXISTS usuwanie_uzytkownika;

DELIMITER $$
CREATE TRIGGER dodawanie_uzytkownika AFTER INSERT ON pracownicy
FOR EACH ROW
BEGIN
	SET @login = CONCAT(NEW.imie, NEW.staff_id);
	SET @haslo = CONCAT(CHAR(ROUND(65 + (RAND()*25))),
						CHAR(ROUND(65 + (RAND()*25))),
						CHAR(ROUND(65 + (RAND()*25))),
						CHAR(ROUND(65 + (RAND()*25))),
						CHAR(ROUND(65 + (RAND()*25))),
						FLOOR(RAND()*10), FLOOR(RAND()*10));
	INSERT INTO uzytkownicy (u_id, login, haslo) VALUES (NEW.staff_id, @login, @haslo);
END $$

CREATE TRIGGER usuwanie_uzytkownika AFTER DELETE ON pracownicy
FOR EACH ROW
BEGIN
	DELETE FROM uzytkownicy WHERE u_id = OLD.staff_id;
END $$



DELIMITER ;



