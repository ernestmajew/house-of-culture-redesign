-- liquibase formatted sql
-- changeset ppuchala:populate_with_sample_data

-- password for users: password

-- contact info
INSERT INTO institution_contact_info (id, address_first_line, address_second_line, email, phone_number, facebook_url, instagram_url, latitude, longitude, institution_name)
VALUES (null, 'ul. Długa 34', '51-354 Wrocław', 'dom@kultury.pl', '+48 123 123 123', 'https://facebook.com/', 'https://instagram.com/', 51.10953108988421, 17.059643536679005, 'Młodzieżowy Dom Kultury');

-- admin
INSERT INTO user (id, password, role, status, email, phone_number, first_name, last_name, birthdate, gets_newsletter, user_type, parent_id)
VALUES (1, '$2a$10$VmXhTj3P1jtu/Qb4uRbV1.FX1E84H/Y3PziQq5qZdeBtb7Ys4H/Ti', 'ADMIN', 'ACTIVE', 'admin@example.com', '+48 123 456 789', 'Jan', 'Kowalski', '1990-07-01', 0, 'CLIENT', NULL);

-- employee
INSERT INTO user (id, password, role, status, email, phone_number, first_name, last_name, birthdate, gets_newsletter, user_type, parent_id)
VALUES (2, '$2a$10$VmXhTj3P1jtu/Qb4uRbV1.FX1E84H/Y3PziQq5qZdeBtb7Ys4H/Ti', 'EMPLOYEE', 'ACTIVE', 'employee@example.com', '+48 987 654 321', 'Anna', 'Nowak', '1995-03-15', 0, 'CLIENT', NULL);

-- instructors
INSERT INTO user (id, password, role, status, email, phone_number, first_name, last_name, birthdate, gets_newsletter, user_type, parent_id)
VALUES (3, '$2a$10$VmXhTj3P1jtu/Qb4uRbV1.FX1E84H/Y3PziQq5qZdeBtb7Ys4H/Ti', 'INSTRUCTOR', 'ACTIVE', 'instructor1@example.com', '+48 111 222 333', 'Andrzej', 'Kowalczyk', '1987-05-20', 0, 'CLIENT', NULL);

INSERT INTO user (id, password, role, status, email, phone_number, first_name, last_name, birthdate, gets_newsletter, user_type, parent_id)
VALUES (4, '$2a$10$VmXhTj3P1jtu/Qb4uRbV1.FX1E84H/Y3PziQq5qZdeBtb7Ys4H/Ti', 'INSTRUCTOR', 'ACTIVE', 'instructor2@example.com', '+48 444 555 666', 'Magdalena', 'Wojcik', '1989-08-10', 0, 'CLIENT', NULL);

-- clients
INSERT INTO user (id, password, role, status, email, phone_number, first_name, last_name, birthdate, gets_newsletter, user_type, parent_id)
VALUES (5, '$2a$10$VmXhTj3P1jtu/Qb4uRbV1.FX1E84H/Y3PziQq5qZdeBtb7Ys4H/Ti', 'CLIENT', 'ACTIVE', 'client1@example.com', '+48 555 666 777', 'Maria', 'Nowak', '1993-04-12', 0, 'CLIENT', NULL);

INSERT INTO user (id, password, role, status, email, phone_number, first_name, last_name, birthdate, gets_newsletter, user_type, parent_id)
VALUES (6, '$2a$10$VmXhTj3P1jtu/Qb4uRbV1.FX1E84H/Y3PziQq5qZdeBtb7Ys4H/Ti', 'CLIENT', 'ACTIVE', 'client2@example.com', '+48 777 888 999', 'Piotr', 'Kowalski', '1994-09-20', 0, 'CLIENT', NULL);

INSERT INTO user (id, password, role, status, email, phone_number, first_name, last_name, birthdate, gets_newsletter, user_type, parent_id)
VALUES (7, '$2a$10$VmXhTj3P1jtu/Qb4uRbV1.FX1E84H/Y3PziQq5qZdeBtb7Ys4H/Ti', 'CLIENT', 'ACTIVE', 'client3@example.com', '+48 999 000 111', 'Ewa', 'Dabrowski', '1990-07-01', 0, 'CLIENT', NULL);

-- children for the first client (parent_id: 5)
INSERT INTO user (id, password, role, status, email, phone_number, first_name, last_name, birthdate, gets_newsletter, user_type, parent_id)
VALUES (8, '$2a$10$VmXhTj3P1jtu/Qb4uRbV1.FX1E84H/Y3PziQq5qZdeBtb7Ys4H/Ti', 'CHILD', 'ACTIVE', 'child1@example.com', NULL, 'Anna', 'Kowalczyk', '2008-02-15', 0, 'CHILD', 5);

INSERT INTO user (id, password, role, status, email, phone_number, first_name, last_name, birthdate, gets_newsletter, user_type, parent_id)
VALUES (9, '$2a$10$VmXhTj3P1jtu/Qb4uRbV1.FX1E84H/Y3PziQq5qZdeBtb7Ys4H/Ti', 'CHILD', 'ACTIVE', 'child2@example.com', NULL, 'Jakub', 'Kowalczyk', '2010-07-21', 0, 'CHILD', 5);

INSERT INTO user (id, password, role, status, email, phone_number, first_name, last_name, birthdate, gets_newsletter, user_type, parent_id)
VALUES (10, '$2a$10$VmXhTj3P1jtu/Qb4uRbV1.FX1E84H/Y3PziQq5qZdeBtb7Ys4H/Ti', 'CHILD', 'ACTIVE', 'child3@example.com', NULL, 'Natalia', 'Kowalczyk', '2015-11-10', 0, 'CHILD', 5);

-- child for the second client (parent_id: 6)
INSERT INTO user (id, password, role, status, email, phone_number, first_name, last_name, birthdate, gets_newsletter, user_type, parent_id)
VALUES (11, '$2a$10$VmXhTj3P1jtu/Qb4uRbV1.FX1E84H/Y3PziQq5qZdeBtb7Ys4H/Ti', 'CHILD', 'ACTIVE', 'child4@example.com', NULL, 'Marta', 'Kowalski', '2012-04-25', 0, 'CHILD', 6);

-- categories
INSERT INTO category VALUES (6,'Sztuka'),(7,'Malarstwo'),(8,'Taniec'),(9,'Warsztaty'),(10,'Muzyka'),(11,'Koncert'),(12,'Fotografia'),(13,'Dla dzieci'),(14,'Aktualizacja'),(15,'Książka'),(16,'Integracja'),(17,'Wystawa'),(18,'Sport'),(19,'Rękodzieło'),(20,'Rośliny'),(21,'Gotowanie');

-- posts
INSERT INTO `post` VALUES (2,'Ważna Aktualizacja','2023-03-16 14:30:00','148955491630870_122124677204083277','https://www.instagram.com/p/C0eLEl8t304/',2,'<p style=\"text-align:center\">Ważna aktualizacja dotycząca naszych nadchodzących programów.</p><p><span style=\"color:rgb(55, 65, 81);\">Mamy przyjemność ogłosić, że wprowadzamy istotne zmiany, które zaczynają obowiązywać od 01.01.2024.</span></p><p><span style=\"color:rgb(55, 65, 81);\">Czym są te zmiany?</span></p><ul><li><p><span style=\"color:rgb(55, 65, 81);\">Nowa funkcja, która umożliwi Wam komentowanie zajęć.</span></p></li><li><p><span style=\"color:rgb(55, 65, 81);\">Poprawki interfejsu, aby korzystanie było jeszcze bardziej intuicyjne.</span></p></li><li><p><span style=\"color:rgb(55, 65, 81);\">Aktualizacja sytemu aby działał jeszcze szybciej.</span></p></li></ul><p style=\"text-align:start\"><span style=\"color:rgb(55, 65, 81);\">Dziękujemy za Wasze zrozumienie i cierpliwość podczas implementacji tych zmian. Jesteśmy pewni, że nowości sprawią, że Wasze doświadczenia będą jeszcze lepsze!</span></p><p style=\"text-align:start\"><span style=\"color:rgb(55, 65, 81);\">Do zobaczenia po aktualizacji!</span></p><p style=\"text-align:start\"><span style=\"color:rgb(55, 65, 81);\">#Aktualizacja #Nowości #Usprawnienia #DomKultury</span></p>'),(3,'Zabawny Warsztat','2023-03-17 11:15:00','148955491630870_122124676430083277','https://www.instagram.com/p/C0eKtgkN95_/',3,'<p style=\"text-align:center\">Dołącz do naszego zabawnego warsztatu w ten weekend!</p><p><span style=\"color:rgb(55, 65, 81);\">W czasie tych warsztatów przenieście się w świat rozrywki i beztroskiej zabawy. Niezależnie od wieku czy doświadczenia, te warsztaty są zaprojektowane dla każdego, kto pragnie rozśmieszyć się i czerpać radość z chwil!</span></p><p style=\"text-align:start\"><span style=\"color:rgb(55, 65, 81);\">Co Was czeka?</span></p><ul><li><p><span style=\"color:rgb(55, 65, 81);\">Kreatywne ćwiczenia, które rozbawią Was do łez</span></p></li><li><p><span style=\"color:rgb(55, 65, 81);\">Gry i zabawy, które pobudzą kreatywność</span></p></li><li><p><span style=\"color:rgb(55, 65, 81);\">Humorystyczne techniki poprawiające nastrój</span></p></li><li><p><span style=\"color:rgb(55, 65, 81);\">Spotkanie z innymi miłośnikami dobrej zabawy</span></p></li></ul><p style=\"text-align:start\"><span style=\"color:rgb(55, 65, 81);\">Przygotujcie się na wyjątkowe doświadczenie, które pozwoli Wam uwolnić się od codziennych trosk i po prostu bawić się wspaniale!</span></p><p style=\"text-align:start\"><span style=\"color:rgb(55, 65, 81);\">Zapraszamy wszystkich, którzy chcą spędzić czas w radosnej atmosferze! Do zobaczenia na Zabawnym Warsztacie!</span></p><p style=\"text-align:start\"><span style=\"color:rgb(55, 65, 81);\">#ZabawnyWarsztat #RadośćISmiech #BawSięŚwietnie #DomKultury</span></p>'),(5,'Wystawa Sztuki','2023-03-19 13:20:00','148955491630870_122124678272083277','https://www.instagram.com/p/C0eLny4NfRF/',4,'<p style=\"text-align:center\">Odkryj świat sztuki na naszej wystawie.</p><p></p><p style=\"text-align:start\"><span style=\"color:rgb(55, 65, 81);\">Czekamy na Wasze przybycie, aby wspólnie cieszyć się różnorodnością artystyczną.</span></p><p style=\"text-align:start\"><span style=\"color:rgb(55, 65, 81);\">Co Was czeka?</span></p><ul><li><p><span style=\"color:rgb(55, 65, 81);\">Prezentacja wyjątkowych dzieł sztuki</span></p></li><li><p><span style=\"color:rgb(55, 65, 81);\">Możliwość rozmowy z artystami</span></p></li><li><p><span style=\"color:rgb(55, 65, 81);\">Inspirująca atmosfera i estetyczne doznania</span></p></li></ul><p style=\"text-align:start\"><span style=\"color:rgb(55, 65, 81);\">Nie ważne, czy jesteście doświadczonymi miłośnikami sztuki czy dopiero zaczynacie swoją przygodę z artystycznym światem - ta wystawa jest dla Was!</span></p><p style=\"text-align:start\"><span style=\"color:rgb(55, 65, 81);\">Zapraszamy do wspólnego delektowania się pięknem i różnorodnością sztuki. Do zobaczenia na Wystawie Sztuki!</span></p><p style=\"text-align:start\"><span style=\"color:rgb(55, 65, 81);\">#WystawaSztuki #ArtystyczneDoznania #SztukaWspółczesna #DomKultury</span></p>'),(9,'Spotkanie Klubu Książki','2023-03-24 15:20:00','148955491630870_122124677834083277','https://www.instagram.com/p/C0eLYxutJZ0/',3,'<p style=\"text-align:center\">Zapraszamy na nasze kolejne spotkanie klubu książki!</p><p></p><p style=\"text-align:start\"><span style=\"color:rgb(55, 65, 81);\">Czekamy z niecierpliwością na nasze kolejne spotkanie, które będzie pełne pasjonujących dyskusji i literackich odkryć.</span></p><p style=\"text-align:start\"><span style=\"color:rgb(55, 65, 81);\">Co Was czeka?</span></p><ul><li><p><span style=\"color:rgb(55, 65, 81);\">Omówienie aktualnej lektury, wymiana spostrzeżeń i dyskusja</span></p></li><li><p><span style=\"color:rgb(55, 65, 81);\">Planowanie przyszłych czytań i wybór kolejnych tytułów</span></p></li><li><p><span style=\"color:rgb(55, 65, 81);\">Integracja i dzielenie się ulubionymi cytatami</span></p></li><li><p><span style=\"color:rgb(55, 65, 81);\">Przyjemna atmosfera i spotkanie z ludźmi, którzy kochają książki tak samo jak Ty!</span></p></li></ul><p style=\"text-align:start\"><span style=\"color:rgb(55, 65, 81);\">Nie ważne, czy jesteś zapalonym czytelnikiem, czy dopiero zaczynasz swoją przygodę z literaturą - dołącz do nas!</span></p><p style=\"text-align:start\"><span style=\"color:rgb(55, 65, 81);\">Przyjdź, podziel się swoimi myślami, poszerz horyzonty czytelnicze i spędź miły wieczór w towarzystwie pasjonatów książek. Do zobaczenia na Spotkaniu Klubu Książki!</span></p><p style=\"text-align:start\"><strong><span style=\"color:#d93f0b;\">#KlubKsiążki #SpotkanieLiterackie #CzytamyRazem #DomKultury</span></strong></p>'),(10,'Warsztaty Fotografii Krajobrazowej','2023-03-25 14:00:00','148955491630870_122124674966083277','https://www.instagram.com/p/C0eKYl4tP7-/',1,'<p style=\"text-align:center\"><span style=\"color:rgb(55, 65, 81);\">Zapraszamy na inspirujące Warsztaty Fotografii Krajobrazowej!</span></p><p></p><p style=\"text-align:start\"><span style=\"color:rgb(55, 65, 81);\">Podczas tych warsztatów odkryjecie tajniki sztuki uwieczniania piękna krajobrazu. Niezależnie od tego, czy jesteście doświadczonymi fotografami czy dopiero zaczynacie swoją przygodę z fotografią, te warsztaty są dla Was idealne!</span></p><p style=\"text-align:start\"><span style=\"color:rgb(55, 65, 81);\">Co Was czeka?</span></p><ul><li><p><span style=\"color:rgb(55, 65, 81);\">Praktyczne wskazówki od doświadczonych fotografów krajobrazowych</span></p></li><li><p><span style=\"color:rgb(55, 65, 81);\">Możliwość eksperymentowania z różnymi technikami fotograficznymi</span></p></li><li><p><span style=\"color:rgb(55, 65, 81);\">Wyjątkowe lokalizacje do fotografowania</span></p></li><li><p><span style=\"color:rgb(55, 65, 81);\">Wymiana doświadczeń i inspirujące rozmowy z pasjonatami fotografii</span></p></li></ul><p style=\"text-align:start\"><span style=\"color:rgb(55, 65, 81);\">Przygotujcie swoje aparaty i dołączcie do naszej społeczności miłośników fotografii krajobrazowej!</span></p><p style=\"text-align:start\"><span style=\"color:rgb(55, 65, 81);\">Zapraszamy wszystkich entuzjastów piękna otaczającego nas świata! Do zobaczenia na Warsztatach Fotografii Krajobrazowej!</span></p><p style=\"text-align:start\"><span style=\"color:rgb(55, 65, 81);\">#WarsztatyFotografii #KrajobrazowaSztuka #FotograficznaPrzygoda #DomKultury</span></p>'),(11,'Koncert Muzyki Klasycznej','2023-03-26 20:30:00','148955491630870_122124673196083277','https://www.instagram.com/p/C0eKDkWtk6_/',2,'<p style=\"text-align:center\"><span style=\"color:rgb(55, 65, 81);\">Zapraszamy serdecznie na wyjątkowy Koncert Muzyki Klasycznej!</span></p><p style=\"text-align:start\"></p><p style=\"text-align:start\"><span style=\"color:rgb(55, 65, 81);\">Przygotujcie się na niezapomnianą podróż dźwiękową w świecie klasyki!</span></p><p style=\"text-align:start\"><span style=\"color:rgb(55, 65, 81);\">Co Was czeka?</span></p><ul><li><p><span style=\"color:rgb(55, 65, 81);\">Wyjątkowe wykonania utworów klasyki, prezentowane przez uzdolnionych muzyków</span></p></li><li><p><span style=\"color:rgb(55, 65, 81);\">Subtelność i piękno kompozycji mistrzów muzyki klasycznej</span></p></li><li><p><span style=\"color:rgb(55, 65, 81);\">Możliwość delektowania się unikalnym brzmieniem instrumentów</span></p></li><li><p><span style=\"color:rgb(55, 65, 81);\">Atmosfera, która przeniesie Was w magiczny świat dźwięków</span></p></li></ul><p style=\"text-align:start\"><span style=\"color:rgb(55, 65, 81);\">Nie ważne, czy jesteście wielbicielami muzyki klasycznej czy odkrywacie jej piękno po raz pierwszy - koncert ten jest dedykowany wszystkim, którzy pragną doświadczyć wyjątkowej harmonii.</span></p><p style=\"text-align:start\"><span style=\"color:rgb(55, 65, 81);\">Zapraszamy do wspólnego uczestnictwa w tym wyjątkowym wydarzeniu! Do zobaczenia na Koncercie Muzyki Klasycznej!</span></p><p style=\"text-align:start\"><span style=\"color:rgb(55, 65, 81);\">#KoncertMuzykiKlasycznej #DźwiękiMistrzów #MuzykaBezCzasu #DomKultury</span></p>'),(12,'Kurs Tańca Nowoczesnego','2023-03-27 17:00:00','148955491630870_122124672752083277','https://www.instagram.com/p/C0eJuc9N1IP/',4,'<p style=\"text-align:center\"><span style=\"color:rgb(55, 65, 81);\">Zapraszamy serdecznie na wyjątkowe wydarzenie - Kurs Tańca Nowoczesnego!</span></p><p></p><p style=\"text-align:start\"><span style=\"color:rgb(55, 65, 81);\">To nie tylko lekcja tańca, ale również podróż w świat nowoczesnej choreografii, pełen emocji i energetycznych ruchów.</span></p><p style=\"text-align:start\"><span style=\"color:rgb(55, 65, 81);\">Co Was czeka?</span></p><ul><li><p><span style=\"color:rgb(55, 65, 81);\">Zajęcia prowadzone przez doświadczonych instruktorów tańca</span></p></li><li><p><span style=\"color:rgb(55, 65, 81);\">Ekspresywne ruchy i wyrażanie emocji poprzez taniec</span></p></li><li><p><span style=\"color:rgb(55, 65, 81);\">Możliwość poznania innych pasjonatów tańca</span></p></li><li><p><span style=\"color:rgb(55, 65, 81);\">Atmosfera pełna pozytywnej energii i inspiracji</span></p></li></ul><p style=\"text-align:start\"><span style=\"color:rgb(55, 65, 81);\">Nie ważne, czy jesteście doświadczonymi tancerzami, czy dopiero zaczynacie swoją przygodę z tańcem - ten kurs jest dla Was!</span></p><p style=\"text-align:start\"><span style=\"color:rgb(55, 65, 81);\">Zbierzcie swoich przyjaciół, rodzinę i wszystkich miłośników tańca! To wyjątkowa okazja, aby razem odkrywać piękno nowoczesnej choreografii.</span></p><p style=\"text-align:start\"><span style=\"color:rgb(55, 65, 81);\">Nie przegapcie tego wyjątkowego wydarzenia! Do zobaczenia na Kursie Tańca Nowoczesnego!</span></p><p style=\"text-align:start\"><span style=\"color:rgb(55, 65, 81);\">#KursTańcaNowoczesnego #RuchIWyrażenie #TanecznaPrzygoda #DomKultury</span></p>'),(13,'Wernisaż Młodych Artystów','2023-03-28 18:45:00','148955491630870_122124672296083277','https://www.instagram.com/p/C0eJYZJNMpn/',5,'<p style=\"text-align:center\"><span style=\"color:rgb(55, 65, 81);\"> Witajcie w magicznym świecie sztuki!</span></p><p style=\"text-align:start\"><span style=\"color:rgb(55, 65, 81);\">Cieszymy się ogromnie, że możemy Was zaprosić na niezwykłe wydarzenie - Wernisaż Młodych Artystów! </span></p><p><span style=\"color:rgb(55, 65, 81);\">To będzie niepowtarzalna okazja, aby zanurzyć się w świat kreatywności i odkryć talenty, które kształtują przyszłość sztuki. Młodzi artyści przeniosą Was w fascynującą podróż pełną wyrazistych kolorów, emocji i inspiracji.</span></p><p style=\"text-align:start\"><span style=\"color:rgb(55, 65, 81);\">Co Was czeka?</span></p><ul><li><p><span style=\"color:rgb(55, 65, 81);\"> Wspaniałe obrazy, pełne indywidualności i ekspresji</span></p></li><li><p><span style=\"color:rgb(55, 65, 81);\"> Performance artystyczne, które poruszą Wasze serca</span></p></li><li><p><span style=\"color:rgb(55, 65, 81);\"> Spotkanie z twórcami i możliwość rozmowy o ich inspiracjach</span></p></li><li><p><span style=\"color:rgb(55, 65, 81);\"> Muzyczne akcenty, które podkreślą magię chwili</span></p></li></ul><p style=\"text-align:start\"><span style=\"color:rgb(55, 65, 81);\">Przygotujcie się na wieczór pełen artystycznych doznań, które pozostaną w Waszej pamięci na długo. </span></p><p style=\"text-align:start\"><span style=\"color:rgb(55, 65, 81);\"> Zbierzcie swoich przyjaciół, rodzinę i wszystkich miłośników sztuki! To wyjątkowa okazja, aby wspólnie cieszyć się pięknem twórczości młodych talentów.</span></p><p style=\"text-align:start\"><span style=\"color:rgb(55, 65, 81);\">Nie przegapcie tego wyjątkowego wydarzenia! Do zobaczenia na Wernisażu Młodych Artystów! </span></p><p style=\"text-align:start\"><span style=\"color:rgb(55, 65, 81);\">#WernisażMłodychArtystów #SztukaMłodychTalentów #MagiaSztuki #DomKultury</span></p>');

-- insert post-categories
INSERT INTO `post_category` VALUES (6,13),(7,13),(8,12),(9,12),(10,11),(11,11),(9,10),(12,10),(9,3),(13,3),(14,2),(15,9),(16,9),(6,5),(7,5),(17,5);

-- events
INSERT INTO `event` VALUES
                        (1, 'Kurs Rysunku dla Dzieci', '<p>Zajęcia dla najmłodszych artystów! Odkryjcie razem radość tworzenia i rozwijajcie wyobraźnię podczas naszych lekcji rysunku.</p>', 6, 12, 15, 3, NULL, 'ACTIVE', 'MULTIPLE'),
                        (2, 'Spotkanie Literackie - Nowości Książkowe', '<p style=\"text-align:center\"><span style=\"color:rgb(55, 65, 81);\">Zapraszamy serdecznie na Spotkanie Literackie poświęcone Nowościom Książkowym, które organizujemy w naszym Domu Kultury!</span></p><p style=\"text-align:start\"><span style=\"color:rgb(55, 65, 81);\">Podczas tego wydarzenia będziemy miały okazję razem odkrywać najnowsze tytuły literackie, dzielić się spostrzeżeniami i cieszyć się atmosferą pełną literackiej pasji.</span></p><p><span style=\"color:rgb(55, 65, 81);\">Co czeka na uczestników?</span></p><ul><li><p><span style=\"color:rgb(55, 65, 81);\">Prezentacja najnowszych publikacji różnych gatunków literackich</span></p></li><li><p><span style=\"color:rgb(55, 65, 81);\">Dyskusja i wymiana opinii na temat nowości książkowych</span></p></li><li><p><span style=\"color:rgb(55, 65, 81);\">Możliwość zapisywania się do czytelniczych wyzwań i klubów książki</span></p></li><li><p><span style=\"color:rgb(55, 65, 81);\">Integracja z innymi miłośnikami literatury</span></p></li></ul>', NULL, NULL, NULL, 4, NULL, 'ACTIVE', 'MULTIPLE'),
                        (3, 'Kurs Gotowania dla Początkujących', '<p>Odkryj tajniki kuchni podczas naszych kursów gotowania. Idealne dla osób początkujących!</p>', 18, NULL, 15, 3, 25.99, 'ACTIVE', 'MULTIPLE'),
                        (4, 'Sesja Jogii w Parku', '<p>Praktykuj jogę na świeżym powietrzu. Sesja jogii w parku dla wszystkich poziomów zaawansowania.</p>', 14, NULL, 30, 1, NULL, 'ACTIVE', 'MULTIPLE'),
                        (5, 'Warsztaty Rękodzielnicze - Własnoręczne Biżuterie', '<p>Zrób swoją własną biżuterię podczas naszych warsztatów rękodzielniczych. Kreatywność i zabawa gwarantowane!</p>', 12, NULL, 25, 3, 15.75, 'ACTIVE', 'MULTIPLE'),
                        (6, 'Poradnik Ogrodniczy - Zdobądź Zielony Kciuk', '<p>Przygotuj się do sezonu ogrodniczego podczas naszego poradnika ogrodniczego. Zdobądź zielony kciuk i ciesz się pięknem roślin!</p>', NULL, NULL, 40, 4, NULL, 'ACTIVE', 'MULTIPLE'),
                        (7, 'Rozwijaj Umiejętności Kulinarne', '<p>Zapraszamy na cykl warsztatów kulinarnych, gdzie rozwijasz swoje umiejętności w gotowaniu. Gotuj razem z naszym doświadczonym szefem kuchni!</p>', NULL, NULL, 25, 3, 20.5, 'ACTIVE', 'MULTIPLE'),
                        (8, 'Sztuka Fotografii - Warsztaty Praktyczne', '<p>Przeżyj niezapomniane chwile podczas warsztatów fotograficznych. Nauka praktycznych umiejętności w obszarze sztuki fotografii.</p>', 16, NULL, 20, 4, 35.75, 'ACTIVE', 'MULTIPLE'),
                        (9, 'Zumba Fitness Party', '<p style=\"text-align:center\"><span style=\"color:rgb(55, 65, 81);\">Zapraszamy na energetyczne Zajęcia - Zumba Fitness Party, które organizujemy w naszym Domu Kultury!</span></p><p style=\"text-align:start\"><span style=\"color:rgb(55, 65, 81);\">Podczas tych zajęć, każdy uczestnik będzie miał okazję połączyć przyjemność tańca z intensywnym treningiem fitness. To idealna okazja do zabawy, poprawy kondycji fizycznej i rozładowania stresu przy rytmicznych dźwiękach muzyki.</span></p><p><span style=\"color:rgb(55, 65, 81);\">Nie ma znaczenia, czy dopiero zaczynasz swoją przygodę z tańcem czy jesteś doświadczonym tancerzem – Zumba Fitness Party jest dla każdego!</span></p><p style=\"text-align:start\"><span style=\"color:rgb(55, 65, 81);\">Zapraszamy do dołączenia do naszej społeczności, gdzie zabawa łączy się z dbaniem o kondycję fizyczną. Do zobaczenia na Zajęciach Zumba Fitness Party!</span></p>', 15, NULL, 50, 1, 15, 'ACTIVE', 'MULTIPLE'),
                        (10, 'Malowanie Na Talerzach - Sztuka Dekoracyjna', '<p>Odkryj sztukę malowania na talerzach. Warsztaty sztuki dekoracyjnej dla miłośników kreatywności i designu.</p>', 10, NULL, 30, 4, 28.99, 'ACTIVE', 'MULTIPLE');

-- event_category
INSERT INTO event_category VALUES (2,15),(10,6),(10,9),(10,19),(1,6),(1,13),(5,9),(5,19),(8,9),(8,12),(4,18),(6,20),(3,21),(7,21),(9,8),(9,18);

-- single events
-- event 1
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (1, 1, '2023-10-03 17:00:00', 0, '2023-10-03 18:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (2, 1, '2023-10-10 17:00:00', 0, '2023-10-10 18:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (3, 1, '2023-10-17 17:00:00', 0, '2023-10-17 18:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (4, 1, '2023-10-24 17:00:00', 0, '2023-10-24 18:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (5, 1, '2023-10-31 17:00:00', 0, '2023-10-31 18:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (6, 1, '2023-11-07 17:00:00', 0, '2023-11-07 18:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (7, 1, '2023-11-14 17:00:00', 0, '2023-11-14 18:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (8, 1, '2023-11-21 17:00:00', 0, '2023-11-21 18:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (9, 1, '2023-11-28 17:00:00', 0, '2023-11-28 18:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (10, 1, '2023-12-05 17:00:00', 0, '2023-12-05 18:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (11, 1, '2023-12-12 17:00:00', 0, '2023-12-12 18:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (12, 1, '2023-12-19 17:00:00', 0, '2023-12-19 18:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (13, 1, '2023-12-26 17:00:00', 0, '2023-12-26 18:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (14, 1, '2024-01-02 17:00:00', 0, '2024-01-02 18:00:00');

-- event 2
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (15, 2, '2023-10-15 18:30:00', 0, '2023-10-15 19:30:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (16, 2, '2023-10-29 18:30:00', 0, '2023-10-29 19:30:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (17, 2, '2023-11-13 18:30:00', 0, '2023-11-13 19:30:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (18, 2, '2023-11-27 18:30:00', 0, '2023-11-27 19:30:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (19, 2, '2023-12-10 18:30:00', 0, '2023-12-10 19:30:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (20, 2, '2023-12-24 18:30:00', 0, '2023-12-24 19:30:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (21, 2, '2023-01-08 18:30:00', 0, '2023-01-08 19:30:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (22, 2, '2023-01-22 18:30:00', 0, '2023-01-22 19:30:00');

-- Event 3, every week
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (23, 3, '2023-11-01 20:00:00', 0, '2023-11-01 21:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (24, 3, '2023-11-08 20:00:00', 0, '2023-11-08 21:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (25, 3, '2023-11-15 20:00:00', 0, '2023-11-15 21:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (26, 3, '2023-11-22 20:00:00', 0, '2023-11-22 21:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (27, 3, '2023-11-29 20:00:00', 0, '2023-11-29 21:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (28, 3, '2023-12-06 20:00:00', 0, '2023-12-06 21:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (29, 3, '2023-12-13 20:00:00', 0, '2023-12-13 21:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (30, 3, '2023-12-20 20:00:00', 0, '2023-12-20 21:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (31, 3, '2023-12-27 20:00:00', 0, '2023-12-27 21:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (32, 3, '2023-01-03 20:00:00', 0, '2023-01-03 21:00:00');

-- Event 4, every 3 weeks
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (33, 4, '2023-09-01 15:30:00', 0, '2023-09-01 16:30:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (34, 4, '2023-09-22 15:30:00', 0, '2023-09-22 16:30:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (35, 4, '2023-10-13 15:30:00', 0, '2023-10-13 16:30:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (36, 4, '2023-11-03 15:30:00', 0, '2023-11-03 16:30:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (37, 4, '2023-11-24 15:30:00', 0, '2023-11-24 16:30:00');

-- Event 5, every week, ending after December 20
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (38, 5, '2023-10-05 14:00:00', 0, '2023-10-05 15:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (39, 5, '2023-10-12 14:00:00', 0, '2023-10-12 15:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (40, 5, '2023-10-19 14:00:00', 0, '2023-10-19 15:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (41, 5, '2023-10-26 14:00:00', 0, '2023-10-26 15:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (42, 5, '2023-11-02 14:00:00', 0, '2023-11-02 15:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (43, 5, '2023-11-09 14:00:00', 0, '2023-11-09 15:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (44, 5, '2023-11-16 14:00:00', 0, '2023-11-16 15:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (45, 5, '2023-11-23 14:00:00', 0, '2023-11-23 15:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (46, 5, '2023-11-30 14:00:00', 0, '2023-11-30 15:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (47, 5, '2023-12-07 14:00:00', 0, '2023-12-07 15:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (48, 5, '2023-12-14 14:00:00', 0, '2023-12-14 15:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (49, 5, '2023-12-21 14:00:00', 0, '2023-12-21 15:00:00');

-- Event 6, every week, ending after December 20
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (50, 6, '2023-11-04 16:30:00', 0, '2023-11-04 17:30:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (51, 6, '2023-11-11 16:30:00', 0, '2023-11-11 17:30:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (52, 6, '2023-11-18 16:30:00', 0, '2023-11-18 17:30:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (53, 6, '2023-11-25 16:30:00', 0, '2023-11-25 17:30:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (54, 6, '2023-12-01 16:30:00', 0, '2023-12-01 17:30:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (55, 6, '2023-12-08 16:30:00', 0, '2023-12-08 17:30:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (56, 6, '2023-12-15 16:30:00', 0, '2023-12-15 17:30:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (57, 6, '2023-12-22 16:30:00', 0, '2023-12-22 17:30:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (58, 6, '2023-12-29 16:30:00', 0, '2023-12-29 17:30:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (59, 6, '2023-01-06 16:30:00', 0, '2023-01-06 17:30:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (60, 6, '2023-01-13 16:30:00', 0, '2023-01-13 17:30:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (61, 6, '2023-01-20 16:30:00', 0, '2023-01-20 17:30:00');

-- Event 7, every 3 weeks
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (62, 7, '2023-09-05 10:00:00', 0, '2023-09-05 12:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (63, 7, '2023-09-26 10:00:00', 0, '2023-09-26 12:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (64, 7, '2023-10-17 10:00:00', 0, '2023-10-17 12:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (65, 7, '2023-11-07 10:00:00', 0, '2023-11-07 12:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (66, 7, '2023-11-28 10:00:00', 0, '2023-11-28 12:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (67, 7, '2023-12-19 10:00:00', 0, '2023-12-19 12:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (68, 7, '2024-01-09 10:00:00', 0, '2024-01-09 12:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (69, 7, '2024-01-30 10:00:00', 0, '2024-01-30 12:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (70, 7, '2024-02-20 10:00:00', 0, '2024-02-20 12:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (71, 7, '2024-03-12 10:00:00', 0, '2024-03-12 12:00:00');

-- Event 8, every 2 weeks
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (72, 8, '2023-10-01 13:00:00', 0, '2023-10-01 14:30:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (73, 8, '2023-10-15 13:00:00', 0, '2023-10-15 14:30:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (74, 8, '2023-10-29 13:00:00', 0, '2023-10-29 14:30:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (75, 8, '2023-11-12 13:00:00', 0, '2023-11-12 14:30:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (76, 8, '2023-11-26 13:00:00', 0, '2023-11-26 14:30:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (77, 8, '2023-12-10 13:00:00', 0, '2023-12-10 14:30:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (78, 8, '2023-12-24 13:00:00', 0, '2023-12-24 14:30:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (79, 8, '2024-01-07 13:00:00', 0, '2024-01-07 14:30:00');

-- Event 9, every week from November 3
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (80, 9, '2023-11-03 16:00:00', 0, '2023-11-03 17:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (81, 9, '2023-11-10 16:00:00', 0, '2023-11-10 17:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (82, 9, '2023-11-17 16:00:00', 0, '2023-11-17 17:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (83, 9, '2023-11-24 16:00:00', 0, '2023-11-24 17:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (84, 9, '2023-12-01 16:00:00', 0, '2023-12-01 17:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (85, 9, '2023-12-08 16:00:00', 0, '2023-12-08 17:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (86, 9, '2023-12-15 16:00:00', 0, '2023-12-15 17:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (87, 9, '2023-12-22 16:00:00', 0, '2023-12-22 17:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (88, 9, '2023-12-29 16:00:00', 0, '2023-12-29 17:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (89, 9, '2024-01-05 16:00:00', 0, '2024-01-05 17:00:00');

-- Event 9, every two weeks from November 6
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (90, 9, '2023-11-06 18:00:00', 0, '2023-11-06 19:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (91, 9, '2023-11-20 18:00:00', 0, '2023-11-20 19:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (92, 9, '2023-12-04 18:00:00', 0, '2023-12-04 19:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (93, 9, '2023-12-18 18:00:00', 0, '2023-12-18 19:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (94, 9, '2024-01-01 18:00:00', 0, '2024-01-01 19:00:00');

-- Event 10, every week from November 10
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (110, 10, '2023-11-10 15:00:00', 0, '2023-11-10 16:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (111, 10, '2023-11-17 15:00:00', 0, '2023-11-17 16:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (112, 10, '2023-11-24 15:00:00', 0, '2023-11-24 16:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (113, 10, '2023-12-01 15:00:00', 0, '2023-12-01 16:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (114, 10, '2023-12-08 15:00:00', 0, '2023-12-08 16:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (115, 10, '2023-12-15 15:00:00', 0, '2023-12-15 16:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (116, 10, '2023-12-22 15:00:00', 0, '2023-12-22 16:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (117, 10, '2023-12-29 15:00:00', 0, '2023-12-29 16:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (118, 10, '2024-01-05 15:00:00', 0, '2024-01-05 16:00:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (119, 10, '2024-01-12 15:00:00', 0, '2024-01-12 16:00:00');

-- Event 10, every two weeks from November 17
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (120, 10, '2023-11-17 17:30:00', 0, '2023-11-17 18:30:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (121, 10, '2023-12-01 17:30:00', 0, '2023-12-01 18:30:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (122, 10, '2023-12-15 17:30:00', 0, '2023-12-15 18:30:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (123, 10, '2023-12-29 17:30:00', 0, '2023-12-29 18:30:00');
INSERT INTO single_event (id, event_id, starts, is_cancelled, ends) VALUES (124, 10, '2024-01-12 17:30:00', 0, '2024-01-12 18:30:00');

-- Payments
INSERT INTO payment (id, payu_id, status, amount, time, buyer_id) VALUES (0x2F7D0603742942A5B61AE99CB0AC9252, '2RHSC3KTDT231207GUEST000P01', 'COMPLETED', 250.18000000000004, '2023-12-07 12:44:21', 7);
INSERT INTO payment (id, payu_id, status, amount, time, buyer_id) VALUES (0x3785127C9AB740F38150D4BEB9339DA0, '91WG3KKSHP231207GUEST000P01', 'COMPLETED', 31.5, '2023-12-07 12:42:21', 6);
INSERT INTO payment (id, payu_id, status, amount, time, buyer_id) VALUES (0x3A82157BEC5A48CFB700197B604B79F5, 'MHJB8HX7M2231207GUEST000P01', 'COMPLETED', 189.25, '2023-12-07 12:36:54', 5);
INSERT INTO payment (id, payu_id, status, amount, time, buyer_id) VALUES (0x65035AAD23E249808B47DAB5671DF2CC, 'TC41DFJPSZ231207GUEST000P01', 'COMPLETED', 82, '2023-12-07 12:42:36', 6);
INSERT INTO payment (id, payu_id, status, amount, time, buyer_id) VALUES (0x8553850171794D46B96257A9E8440162, 'V4GRCBGKX2231207GUEST000P01', 'COMPLETED', 45, '2023-12-07 12:42:52', 6);
INSERT INTO payment (id, payu_id, status, amount, time, buyer_id) VALUES (0xCE7E23233CFD498FA90EC3693E8BB30F, 'BR2DW4JL6S231207GUEST000P01', 'COMPLETED', 20.5, '2023-12-07 12:38:19', 5);
INSERT INTO payment (id, payu_id, status, amount, time, buyer_id) VALUES (0xDF3B20BFB8E548978E4F647E6EF9304B, '442F9NPT5B231207GUEST000P01', 'COMPLETED', 71.5, '2023-12-07 12:46:32', 3);

-- Enrollments
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (1, 5, 85, '2023-12-07 11:35:10', 0x3A82157BEC5A48CFB700197B604B79F5);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (2, 5, 86, '2023-12-07 11:35:10', 0x3A82157BEC5A48CFB700197B604B79F5);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (3, 5, 93, '2023-12-07 11:35:10', 0x3A82157BEC5A48CFB700197B604B79F5);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (4, 5, 87, '2023-12-07 11:35:10', null);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (5, 5, 88, '2023-12-07 11:35:10', null);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (6, 5, 94, '2023-12-07 11:35:10', null);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (7, 5, 89, '2023-12-07 11:35:10', null);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (8, 8, 85, '2023-12-07 11:35:19', 0x3A82157BEC5A48CFB700197B604B79F5);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (9, 8, 86, '2023-12-07 11:35:19', 0x3A82157BEC5A48CFB700197B604B79F5);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (10, 8, 93, '2023-12-07 11:35:19', 0x3A82157BEC5A48CFB700197B604B79F5);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (11, 8, 87, '2023-12-07 11:35:19', null);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (12, 8, 88, '2023-12-07 11:35:19', null);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (13, 8, 94, '2023-12-07 11:35:19', null);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (14, 8, 89, '2023-12-07 11:35:19', null);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (15, 9, 67, '2023-12-07 11:35:46', 0x3A82157BEC5A48CFB700197B604B79F5);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (16, 9, 68, '2023-12-07 11:35:46', 0xCE7E23233CFD498FA90EC3693E8BB30F);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (17, 9, 69, '2023-12-07 11:35:46', null);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (18, 9, 70, '2023-12-07 11:35:46', null);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (19, 10, 11, '2023-12-07 11:35:54', null);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (20, 10, 12, '2023-12-07 11:35:54', null);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (21, 10, 13, '2023-12-07 11:35:54', null);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (22, 10, 14, '2023-12-07 11:35:54', null);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (23, 5, 19, '2023-12-07 11:36:08', null);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (24, 5, 20, '2023-12-07 11:36:08', null);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (25, 8, 47, '2023-12-07 11:36:26', 0x3A82157BEC5A48CFB700197B604B79F5);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (26, 8, 48, '2023-12-07 11:36:26', 0x3A82157BEC5A48CFB700197B604B79F5);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (27, 9, 47, '2023-12-07 11:36:30', 0x3A82157BEC5A48CFB700197B604B79F5);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (28, 9, 48, '2023-12-07 11:36:30', 0x3A82157BEC5A48CFB700197B604B79F5);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (29, 9, 49, '2023-12-07 11:36:30', 0x3A82157BEC5A48CFB700197B604B79F5);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (30, 6, 55, '2023-12-07 11:41:07', null);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (31, 6, 56, '2023-12-07 11:41:07', null);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (32, 6, 57, '2023-12-07 11:41:07', null);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (33, 6, 58, '2023-12-07 11:41:07', null);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (34, 11, 67, '2023-12-07 11:41:15', 0x65035AAD23E249808B47DAB5671DF2CC);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (35, 11, 68, '2023-12-07 11:41:15', 0x65035AAD23E249808B47DAB5671DF2CC);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (36, 11, 69, '2023-12-07 11:41:15', 0x65035AAD23E249808B47DAB5671DF2CC);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (37, 11, 70, '2023-12-07 11:41:15', 0x65035AAD23E249808B47DAB5671DF2CC);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (38, 11, 71, '2023-12-07 11:41:15', null);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (39, 6, 47, '2023-12-07 11:41:24', 0x3785127C9AB740F38150D4BEB9339DA0);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (40, 6, 48, '2023-12-07 11:41:24', 0x3785127C9AB740F38150D4BEB9339DA0);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (41, 6, 49, '2023-12-07 11:41:24', null);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (42, 6, 85, '2023-12-07 11:41:38', 0x8553850171794D46B96257A9E8440162);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (43, 6, 86, '2023-12-07 11:41:38', 0x8553850171794D46B96257A9E8440162);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (44, 6, 93, '2023-12-07 11:41:38', 0x8553850171794D46B96257A9E8440162);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (45, 11, 114, '2023-12-07 11:41:50', null);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (46, 11, 115, '2023-12-07 11:41:50', null);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (47, 11, 122, '2023-12-07 11:41:50', null);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (48, 11, 116, '2023-12-07 11:41:50', null);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (49, 11, 117, '2023-12-07 11:41:50', null);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (50, 11, 123, '2023-12-07 11:41:50', null);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (51, 11, 118, '2023-12-07 11:41:50', null);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (52, 11, 119, '2023-12-07 11:41:50', null);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (53, 11, 124, '2023-12-07 11:41:50', null);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (54, 11, 11, '2023-12-07 11:42:07', null);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (55, 11, 12, '2023-12-07 11:42:07', null);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (56, 11, 13, '2023-12-07 11:42:07', null);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (57, 11, 14, '2023-12-07 11:42:07', null);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (58, 7, 47, '2023-12-07 11:43:49', 0x2F7D0603742942A5B61AE99CB0AC9252);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (59, 7, 48, '2023-12-07 11:43:49', 0x2F7D0603742942A5B61AE99CB0AC9252);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (60, 7, 49, '2023-12-07 11:43:49', 0x2F7D0603742942A5B61AE99CB0AC9252);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (61, 7, 55, '2023-12-07 11:44:02', null);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (62, 7, 56, '2023-12-07 11:44:02', null);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (63, 7, 57, '2023-12-07 11:44:02', null);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (64, 7, 58, '2023-12-07 11:44:02', null);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (65, 7, 114, '2023-12-07 11:44:10', 0x2F7D0603742942A5B61AE99CB0AC9252);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (66, 7, 115, '2023-12-07 11:44:10', 0x2F7D0603742942A5B61AE99CB0AC9252);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (67, 7, 122, '2023-12-07 11:44:10', 0x2F7D0603742942A5B61AE99CB0AC9252);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (68, 7, 116, '2023-12-07 11:44:10', 0x2F7D0603742942A5B61AE99CB0AC9252);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (69, 7, 117, '2023-12-07 11:44:10', 0x2F7D0603742942A5B61AE99CB0AC9252);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (70, 7, 123, '2023-12-07 11:44:10', 0x2F7D0603742942A5B61AE99CB0AC9252);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (71, 7, 118, '2023-12-07 11:44:10', 0x2F7D0603742942A5B61AE99CB0AC9252);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (72, 7, 119, '2023-12-07 11:44:10', null);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (73, 7, 124, '2023-12-07 11:44:10', null);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (74, 3, 77, '2023-12-07 11:46:28', 0xDF3B20BFB8E548978E4F647E6EF9304B);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (75, 3, 78, '2023-12-07 11:46:28', 0xDF3B20BFB8E548978E4F647E6EF9304B);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (76, 3, 79, '2023-12-07 11:46:28', null);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (77, 2, 47, '2023-12-07 11:49:48', null);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (78, 2, 48, '2023-12-07 11:49:48', null);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (79, 2, 49, '2023-12-07 11:49:48', null);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (80, 1, 67, '2023-12-07 11:50:26', null);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (81, 1, 68, '2023-12-07 11:50:26', null);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (82, 1, 69, '2023-12-07 11:50:26', null);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (83, 1, 70, '2023-12-07 11:50:26', null);
INSERT INTO enrollment (id, client_id, event_id, enrollment_time, payment_id) VALUES (84, 1, 71, '2023-12-07 11:50:26', null);
