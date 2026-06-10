-- Удаление старых таблиц (если есть)
DROP TABLE IF EXISTS cities CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Таблица пользователей
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    login VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(56) NOT NULL
);

-- Таблица городов
CREATE TABLE cities (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    coord_x BIGINT NOT NULL CHECK (coord_x > 0),
    coord_y DOUBLE PRECISION NOT NULL CHECK (coord_y <= 793),
    creation_date DATE NOT NULL,
    area DOUBLE PRECISION NOT NULL CHECK (area > 0),
    population INTEGER NOT NULL CHECK (population > 0),
    meters_above_sea_level INTEGER,
    car_code INTEGER NOT NULL CHECK (car_code > 0 AND car_code <= 1000),
    climate VARCHAR(20) NOT NULL,
    standard_of_living VARCHAR(20) NOT NULL,
    governor_height REAL,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE
);

-- Индексы для производительности
CREATE INDEX idx_cities_user_id ON cities(user_id);
CREATE INDEX idx_cities_car_code ON cities(car_code);