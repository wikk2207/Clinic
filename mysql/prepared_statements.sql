#SET @terminy = CONCAT("SELECT termin_wizyty FROM Wizyty WHERE termin_wizyty BETWEEN '?-?-? 00:00:00' AND '?-?-? 23:59:59';");

#PREPARE zajete_terminy FROM @terminy; #jako argument: dzien, miesiac, rok, dzien, miesiac, rok

SET @wyniki = CONCAT("SELECT wb_id, data_wystawienia, imie, nazwisko, specjalizacja FROM Wyniki_badan A JOIN Pracownicy B ON A.id_lekarza=B.staff_id WHERE A.id_pacjenta=? ;");

PREPARE wyniki_pacjenta FROM @wyniki; #jako argument id pacjenta

#SET @szczegoly_wynikow = CONCAT("SELECT nazwa, wartosc, poprzednia_wartosc, jednostka, A.uwagi FROM Wyniki_zawartosc A JOIN Badania B ON A.id_badania=B.id WHERE id_wynikow=?;");

#PREPARE pokaz_szczegolowe_wyniki FROM @szczegoly_wynikow; #jako argument id wynikow

#SET @moje_wizyty = CONCAT("SELECT id_wizyty,termin_wizyty,imię, nazwisko,specjalizacja FROM Wizyty W JOIN Pracownicy P ON W.id_lekarza=P.id WHERE W.id_pacjenta=?;");

#PREPARE pokaz_moje_wizyty FROM @moje_wizyty;

