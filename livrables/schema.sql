CREATE TABLE IF NOT EXISTS AttractionType (
                                              id INT PRIMARY KEY AUTO_INCREMENT,
                                              nom VARCHAR(100) NOT NULL UNIQUE
    );

CREATE TABLE IF NOT EXISTS Attraction (
                                          id INT PRIMARY KEY AUTO_INCREMENT,
                                          nom VARCHAR(100) NOT NULL,
    type_id INT NOT NULL,
    taille_min INT NOT NULL,
    taille_min_adulte INT NOT NULL,
    FOREIGN KEY (type_id) REFERENCES AttractionType(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS HoraireOuverture (
                                                attraction_id INT NOT NULL,
                                                jour_semaine INT NOT NULL,
                                                heure_ouverture TIME NOT NULL,
                                                heure_fermeture TIME NOT NULL,
                                                PRIMARY KEY (attraction_id, jour_semaine, heure_ouverture, heure_fermeture),
    FOREIGN KEY (attraction_id) REFERENCES Attraction(id) ON DELETE CASCADE
    );

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
                                         titre VARCHAR(100) NOT NULL,
                                         lieu_id INT NOT NULL,
                                         FOREIGN KEY (lieu_id) REFERENCES Lieu(id)
    );

CREATE TABLE IF NOT EXISTS Programmation (
                                             id INT PRIMARY KEY AUTO_INCREMENT,
                                             spectacle_id INT NOT NULL,
                                             jour_semaine INT NOT NULL,
                                             heure_debut TIME NOT NULL,
                                             heure_fin TIME NOT NULL,
                                             FOREIGN KEY (spectacle_id) REFERENCES Spectacle(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS SpectaclePersonnage (
                                                   spectacle_id INT NOT NULL,
                                                   personnage_id INT NOT NULL,
                                                   PRIMARY KEY (spectacle_id, personnage_id),
    FOREIGN KEY (spectacle_id) REFERENCES Spectacle(id) ON DELETE CASCADE,
    FOREIGN KEY (personnage_id) REFERENCES Personnage(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS RencontrePersonnage (
                                                   id INT PRIMARY KEY AUTO_INCREMENT,
                                                   personnage_id INT NOT NULL,
                                                   lieu_id INT NOT NULL,
                                                   jour_semaine INT NOT NULL,
                                                   heure_debut TIME NOT NULL,
                                                   heure_fin TIME NOT NULL,
    FOREIGN KEY (personnage_id) REFERENCES Personnage(id) ON DELETE CASCADE,
    FOREIGN KEY (lieu_id) REFERENCES Lieu(id) ON DELETE CASCADE
);

INSERT INTO AttractionType (nom) VALUES
('Familiale'),
('Cinéma 4D'),
('Rollercoaster');