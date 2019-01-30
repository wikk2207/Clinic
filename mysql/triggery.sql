USE projekt_sql;
DROP TRIGGER IF EXISTS usun_zawartosc_wynikow;
DELIMITER //
CREATE TRIGGER usun_zawartosc_wynikow AFTER DELETE ON wyniki_badan FOR EACH ROW 
BEGIN
	DELETE FROM wyniki_zawartosc WHERE id_wynikow = OLD.wb_id;
END //
DELIMITER ;


DROP TRIGGER IF EXISTS usun_badanie;
DELIMITER //
CREATE TRIGGER usun_badanie AFTER DELETE ON badania FOR EACH ROW
BEGIN
	DELETE FROM wyniki_zawartosc WHERE id_badania = OLD.b_id;
END //
DELIMITER ;


DROP TRIGGER IF EXISTS sprawdz_poprawnosc_badania;
DELIMITER //
CREATE TRIGGER sprawdz_poprawnosc_badania BEFORE INSERT ON badania FOR EACH ROW
BEGIN
	DECLARE min_old FLOAT;
    IF NEW.min_wartosc > NEW.max_wartosc THEN
    	SET min_old = NEW.min_wartosc;
        SET NEW.min_wartosc = NEW.max_wartosc, NEW.max_wartosc = min_old;
    END IF;
END //
DELIMITER ;


DROP TRIGGER IF EXISTS dodaj_poprzednia_wartosc;
DELIMITER //
CREATE TRIGGER dodaj_poprzednia_wartosc BEFORE INSERT ON wyniki_zawartosc FOR EACH ROW
BEGIN
SET @idbadania = NEW.id_badania;
SET @idwynikowNEW = NEW.id_wynikow;
SET @idpacjenta = (SELECT DISTINCT wb.id_pacjenta FROM wyniki_badan wb INNER JOIN wyniki_zawartosc wz ON wb.wb_id = wz.id_wynikow WHERE wb.wb_id = @idwynikowNEW);
SET @datawystawiena = (SELECT MAX(wb.data_wystawienia) FROM wyniki_badan wb INNER JOIN wyniki_zawartosc wz ON wb.wb_id = wz.id_wynikow WHERE wb.id_pacjenta = @idpacjenta AND wz.id_badania = @idbadania);
SET @poprzedniawartosc = (SELECT wz.wartosc FROM wyniki_zawartosc wz INNER JOIN wyniki_badan wb ON wb.wb_id = wz.id_wynikow WHERE wb.data_wystawienia = @datawystawienia AND wz.id_badania = @idbadania);

	SET NEW.poprzednia_wartosc = @poprzedniawartosc;
END //
DELIMITER ;

