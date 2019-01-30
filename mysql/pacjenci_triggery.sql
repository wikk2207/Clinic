DROP TRIGGER IF EXISTS sprawdz_pesel;
DROP TRIGGER IF EXISTS usun_dane_pacjenta; 

DELIMITER $$
CREATE TRIGGER sprawdz_pesel BEFORE INSERT ON pacjenci
FOR EACH ROW
BEGIN
	SET @date_string =date_format(NEW.data_urodzenia, "%y%m%d");
    
    IF SUBSTRING(NEW.pesel, 7,5) NOT regexp '^-?[0-9]+$' THEN
         SIGNAL SQLSTATE '45000'
		SET MESSAGE_TEXT = 'Nieprawidłowy format numeru pesel';
	END IF;
    
    IF SUBSTRING(@date_string, 5, 2) <> SUBSTRING(NEW.pesel, 5,2) THEN
		SIGNAL SQLSTATE '45000'
		SET MESSAGE_TEXT = 'Nieprawidłowy pesel1';
	END IF; 
	IF SUBSTRING(@date_string, 3, 2) <> SUBSTRING(NEW.pesel, 3,2) THEN
		SIGNAL SQLSTATE '45000'
		SET MESSAGE_TEXT = 'Nieprawidłowy pesel2';
	END IF;
   IF SUBSTRING(@date_string, 1, 2) <> SUBSTRING(NEW.pesel, 1,2)  THEN
		SIGNAL SQLSTATE '45000'
		SET MESSAGE_TEXT = 'Nieprawidłowy pesel3';
	END IF;
END $$

CREATE TRIGGER usun_dane_pacjenta AFTER DELETE ON pacjenci
FOR EACH ROW
BEGIN
DELETE FROM wyniki_zawartosc WHERE id_wynikow IN (SELECT wb_id FROM wyniki_badan WHERE id_pacjenta =OLD.p_id);
DELETE FROM wyniki_badan WHERE id_pacjenta=OLD.p_id;
DELETE FROM uzytkownicy WHERE u_id=OLD.p_id;
END $$

DELIMITER ;
