CREATE TABLE county (
	county_id INT,
    county VARCHAR(20),
    PRIMARY KEY (county_id)
);

CREATE TABLE gender(
	gender_id INT,
    gender VARCHAR(20),
    PRIMARY KEY (gender_id)
);

CREATE TABLE person(
	person_id int,
    first_name VARCHAR(25),
    last_name VARCHAR(25),
    age INT,
    gender INT,
    residence INT,
    PRIMARY KEY(person_id),
    FOREIGN KEY(gender) REFERENCES gender(gender_id),
    FOREIGN KEY(residence) REFERENCES county(county_id)
);

CREATE TABLE connections (
	person_id INT NOT NULL UNIQUE,
    father_id INT,
    mother_id INT,
    FOREIGN KEY(person_id) REFERENCES person(person_id),
    FOREIGN KEY(father_id) REFERENCES person(person_id),
    FOREIGN KEY(mother_id) REFERENCES person(person_id)
);
