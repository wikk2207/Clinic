USE `Projekt_sql`;
DROP TABLE IF EXISTS badania;
CREATE TABLE badania (b_id int PRIMARY KEY AUTO_INCREMENT, nazwa varchar(45), min_wartosc float, max_wartosc float, jednostka varchar(10), uwagi varchar(100));