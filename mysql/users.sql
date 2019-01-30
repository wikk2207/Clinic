CREATE USER 'admin'@'localhost' IDENTIFIED BY 'a3d6m9i2n';
GRANT SELECT, INSERT, UPDATE, DELETE ON projekt_sql.* TO 'admin'@'localhost';

CREATE USER 'lekarz'@'localhost' IDENTIFIED BY'l2e6k0a4r8z';
GRANT SELECT, INSERT, UPDATE ON projekt_sql.Pacjenci TO 'lekarz'@'localhost';
GRANT SELECT, INSERT, UPDATE ON projekt_sql.Wyniki_badan TO 'lekarz'@'localhost';
GRANT SELECT, INSERT, UPDATE ON projekt_sql.Wyniki_zawartosc TO 'lekarz'@'localhost';
GRANT SELECT ON projekt_sql.Pracownicy TO 'lekarz'@'localhost';
GRANT SELECT ON projekt_sql.Wizyty TO 'lekarz'@'localhost';
GRANT SELECT ON projekt_sql.Badania TO 'lekarz'@'localhost';
GRANT SELECT ON projekt_sql.Godziny TO 'lekarz'@'localhost';

CREATE USER 'pacjent'@'localhost' IDENTIFIED BY 'p4a8c2j6e0n1t';
GRANT SELECT, INSERT, DELETE ON projekt_sql.Wizyty TO 'pacjent'@'localhost';
GRANT SELECT ON projekt_sql.Pracownicy TO 'pacjent'@'localhost';
GRANT SELECT ON projekt_sql.Wyniki_badan TO 'pacjent'@'localhost';
GRANT SELECT ON projekt_sql.Wyniki_zawartosc TO 'pacjent'@'localhost';
GRANT SELECT ON projekt_sql.Badania TO 'pacjent'@'localhost';
GRANT SELECT ON projekt_sql.Godziny TO 'pacjent'@'localhost';

CREATE USER 'sekretarka'@'localhost' IDENTIFIED BY 's5e3k1r2e4t';
GRANT SELECT, INSERT, UPDATE, DELETE ON projekt_sql.Wizyty TO 'sekretarka'@'localhost';
GRANT SELECT, INSERT, UPDATE ON projekt_sql.Pacjenci TO 'sekretarka'@'localhost';
GRANT SELECT ON projekt_sql.Pracownicy TO 'sekretarka'@'localhost';
GRANT SELECT ON projekt_sql.Wyniki_badan TO 'sekretarka'@'localhost';
GRANT SELECT ON projekt_sql.Wyniki_zawartosc TO 'sekretarka'@'localhost';
GRANT SELECT ON projekt_sql.Badania TO 'sekretarka'@'localhost';
GRANT SELECT ON projekt_sql.Godziny TO 'sekretarka'@'localhost';

FLUSH PRIVILEGES;