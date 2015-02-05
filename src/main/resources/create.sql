CREATE TABLE Movie (mid INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                    idTMDB INTEGER,
                    idIMDB INTEGER,
                    path varchar(255),
                    favorite BOOLEAN DEFAULT false,
                    title varchar(100),
                    runTime INTEGER,
                    year INTEGER,
                    posterPath varchar(255),
                    language varchar(50),
                    budget varchar(20),
                    revenue varchar(20),
                    plot varchar(4048),
                    tagLine varchar(512),
                    similarFilms varchar(512),
                    genre varchar(100),
                    ignore BOOLEAN DEFAULT false);

CREATE TABLE Person(pid INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                    name varchar(100),
                    tmdbid INTEGER,
                    biography varchar(8096),
                    photoPath varchar(255));

CREATE TABLE Rating(mid INTEGER REFERENCES Movie(mid),
                    rating DOUBLE,
                    source varchar(40));

CREATE TABLE Subtitle(sid INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                    mid INTEGER REFERENCES Movie(mid),
                    language varchar(50),
                    commonWords varchar(512),
                    languagePath varchar(255),
                    commonWordsCount INTEGER,
                    comment varchar(128),
                    numberOfWords INTEGER  );

CREATE TABLE movie_has_person(mid INTEGER REFERENCES Movie(mid),
                    pid INTEGER REFERENCES Person(pid),
                    role varchar(64));
