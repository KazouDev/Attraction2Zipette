CREATE TABLE IF NOT EXISTS AttractionType (
                                              id INT PRIMARY KEY AUTO_INCREMENT,
                                              nom VARCHAR(100) NOT NULL UNIQUE
    );

-- Table des attractions
CREATE TABLE IF NOT EXISTS Attraction (
                                          id INT PRIMARY KEY AUTO_INCREMENT,
                                          nom VARCHAR(100) NOT NULL,
    type_id INT NOT NULL,
    taille_min INT NOT NULL,
    taille_min_adulte INT NOT NULL,
    FOREIGN KEY (type_id) REFERENCES AttractionType(id) ON DELETE CASCADE
    );

-- Table des horaires d'ouverture
CREATE TABLE IF NOT EXISTS HoraireOuverture (
                                                attraction_id INT NOT NULL,
                                                jour_semaine INT NOT NULL,
                                                heure_ouverture TIME NOT NULL,
                                                heure_fermeture TIME NOT NULL,
                                                PRIMARY KEY (attraction_id, jour_semaine, heure_ouverture, heure_fermeture),
    FOREIGN KEY (attraction_id) REFERENCES Attraction(id) ON DELETE CASCADE
    );

-- Table des lieux
CREATE TABLE IF NOT EXISTS Lieu (
                                    id INT PRIMARY KEY AUTO_INCREMENT,
                                    nom VARCHAR(100) NOT NULL UNIQUE
    );

-- Table des personnages
CREATE TABLE IF NOT EXISTS Personnage (
                                          id INT PRIMARY KEY AUTO_INCREMENT,
                                          nom VARCHAR(100) NOT NULL UNIQUE
    );

CREATE TABLE IF NOT EXISTS Spectacle (
                                         id INT PRIMARY KEY AUTO_INCREMENT,
                                         titre VARCHAR(100) NOT NULL
    );

CREATE TABLE IF NOT EXISTS Programmation (
                                             id INT PRIMARY KEY AUTO_INCREMENT,
                                             spectacle_id INT NOT NULL,
                                             jour_semaine INT NOT NULL,
                                             heure_debut TIME NOT NULL,
                                             heure_fin TIME NOT NULL,
                                             FOREIGN KEY (spectacle_id) REFERENCES Spectacle(id) ON DELETE CASCADE
    );

-- Table d'association Spectacle-Personnage (plusieurs personnages par spectacle)
CREATE TABLE IF NOT EXISTS SpectaclePersonnage (
                                                   spectacle_id INT NOT NULL,
                                                   personnage_id INT NOT NULL,
                                                   PRIMARY KEY (spectacle_id, personnage_id),
    FOREIGN KEY (spectacle_id) REFERENCES Spectacle(id) ON DELETE CASCADE,
    FOREIGN KEY (personnage_id) REFERENCES Personnage(id) ON DELETE CASCADE
    );

-- Table des rencontres avec personnages
CREATE TABLE IF NOT EXISTS RencontrePersonnage (
                                                   id INT PRIMARY KEY AUTO_INCREMENT,
                                                   personnage_id INT NOT NULL,
                                                   lieu_id INT NOT NULL,
                                                   jour_semaine INT NOT NULL,
                                                   heure_debut TIME NOT NULL,
                                                   heure_fin TIME NOT NULL,
                                                   duree INT NOT NULL COMMENT 'Durée en minutes',
                                                   FOREIGN KEY (personnage_id) REFERENCES Personnage(id) ON DELETE CASCADE,
    FOREIGN KEY (lieu_id) REFERENCES Lieu(id) ON DELETE CASCADE
    );

-- Types d'attractions
INSERT INTO AttractionType (nom) VALUES
                                     ('Montagnes Russes'),
                                     ('Carrousel'),
                                     ('Maison Hantée');

-- Attractions
INSERT INTO Attraction (nom, type_id, taille_min, taille_min_adulte) VALUES
                                                                         ('Grand 8', 1, 140, 0),         -- Montagnes Russes
                                                                         ('Chevaux Magiques', 2, 100, 0), -- Carrousel
                                                                         ('Manoir Hanté', 3, 120, 0);     -- Maison Hantée

-- Horaires d'ouverture du Grand 8 (id = 1)
INSERT INTO HoraireOuverture (attraction_id, jour_semaine, heure_ouverture, heure_fermeture) VALUES
                                                                                                 (1, 1, '10:00:00', '18:00:00'), -- Lundi
                                                                                                 (1, 2, '10:00:00', '18:00:00'), -- Mardi
                                                                                                 (1, 6, '10:00:00', '20:00:00'); -- Samedi

-- Horaires d'ouverture du Carrousel (id = 2)
INSERT INTO HoraireOuverture (attraction_id, jour_semaine, heure_ouverture, heure_fermeture) VALUES
                                                                                                 (2, 1, '09:30:00', '19:00:00'),
                                                                                                 (2, 2, '09:30:00', '19:00:00'),
                                                                                                 (2, 7, '09:30:00', '20:00:00'); -- Dimanche

-- Horaires d'ouverture du Manoir Hanté (id = 3)
INSERT INTO HoraireOuverture (attraction_id, jour_semaine, heure_ouverture, heure_fermeture) VALUES
                                                                                                 (3, 5, '11:00:00', '23:00:00'), -- Vendredi
                                                                                                 (3, 6, '11:00:00', '23:00:00'), -- Samedi
                                                                                                 (3, 7, '11:00:00', '23:00:00'); -- Dimanche
