DROP TRIGGER IF EXISTS dodaj_poprzednia_wartosc_i_sprawdz_poprawnosc;

DELIMITER $$
CREATE TRIGGER dodaj_poprzednia_wartosc_i_sprawdz_poprawnosc BEFORE INSERT ON Wyniki_zawartosc
FOR EACH ROW
BEGIN
		IF((SELECT COUNT(id_badania) FROM Wyniki_zawartosc WHERE id_wynikow = NEW.id_wynikow AND id_badania = NEW.id_badania GROUP BY id_badania)>1) THEN
		SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Wynik tego badania zostal juz wprowadzony';
	END IF;
		

	SET @id_pacjenta = (SELECT id_pacjenta FROM Wyniki_badan WHERE wb_id = NEW.id_wynikow);
    SET @data_wyst = (SELECT data_wystawienia FROM Wyniki_badan WHERE wb_id = NEW.id_wynikow);
	
                                    
                                    
	SET @poprzednia_wart =	(SELECT wartosc 
							FROM 	(SELECT wartosc, data_wystawienia 
									FROM Wyniki_zawartosc Z JOIN Wyniki_badan B ON Z.id_wynikow=B.wb_id 
									WHERE id_pacjenta = @id_pacjenta AND id_badania = NEW.id_badania AND data_wystawienia <> @data_wyst) A
							WHERE data_wystawienia  = (SELECT MAX(data_wystawienia ) FROM (SELECT wartosc, data_wystawienia 
																					FROM Wyniki_zawartosc Z JOIN Wyniki_badan B ON Z.id_wynikow=B.wb_id 
																					WHERE id_pacjenta = @id_pacjenta AND id_badania = NEW.id_badania AND data_wystawienia <> @data_wyst) A));
	
	
    SET NEW.poprzednia_wartosc = @poprzednia_wart;

END $$
