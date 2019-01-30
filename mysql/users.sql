CREATE USER 'admin'@'localhost' IDENTIFIED BY 'a3d6m9i2n';
GRANT SELECT, INSERT, UPDATE, DELETE ON projekt_sql.* TO 'admin'@'localhost';

CREATE USER 'lekarz'@'localhost' IDENTIFIED BY'l2e6k0a4r8z';
GRANT SELECT, INSERT, UPDATE ON projekt_sql.pacjenci TO 'lekarz'@'localhost';
GRANT SELECT, INSERT, UPDATE ON projekt_sql.wyniki_badan TO 'lekarz'@'localhost';
GRANT SELECT, INSERT, UPDATE ON projekt_sql.wyniki_zawartosc TO 'lekarz'@'localhost';
GRANT SELECT ON projekt_sql.wizyty TO 'lekarz'@'localhost';
GRANT SELECT ON projekt_sql.badania TO 'lekarz'@'localhost';
GRANT SELECT ON projekt_sql.godziny TO 'lekarz'@'localhost';
GRANT SELECT ON projekt_sql.pracownicy TO 'lekarz'@'localhost';

CREATE USER 'pacjent'@'localhost' IDENTIFIED BY 'p4a8c2j6e0n1t';
GRANT SELECT, INSERT, DELETE ON projekt_sql.wizyty TO 'pacjent'@'localhost';
GRANT SELECT ON projekt_sql.pracownicy TO 'pacjent'@'localhost';
GRANT SELECT ON projekt_sql.wyniki_badan TO 'pacjent'@'localhost';
GRANT SELECT ON projekt_sql.wyniki_zawartosc TO 'pacjent'@'localhost';
GRANT SELECT ON projekt_sql.badania TO 'pacjent'@'localhost';
GRANT SELECT ON projekt_sql.godziny TO 'pacjent'@'localhost';

CREATE USER 'sekretarka'@'localhost' IDENTIFIED BY 's5e3k1r2e4t';
GRANT SELECT, INSERT, UPDATE, DELETE ON projekt_sql.wizyty TO 'sekretarka'@'localhost';
GRANT SELECT, INSERT, UPDATE ON projekt_sql.pacjenci TO 'sekretarka'@'localhost';
GRANT SELECT ON projekt_sql.pracownicy TO 'sekretarka'@'localhost';
GRANT SELECT ON projekt_sql.wyniki_badan TO 'sekretarka'@'localhost';
GRANT SELECT ON projekt_sql.wyniki_zawartosc TO 'sekretarka'@'localhost';
GRANT SELECT ON projekt_sql.badania TO 'sekretarka'@'localhost';
GRANT SELECT ON projekt_sql.godziny TO 'sekretarka'@'localhost';

FLUSH PRIVILEGES;
