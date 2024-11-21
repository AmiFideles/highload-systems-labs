-- Таблица пользователи (users)
CREATE TABLE users
(
    id       BIGSERIAL PRIMARY KEY,
    email    VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255)        NOT NULL,
    name     VARCHAR(255)        NOT NULL,
    role     VARCHAR(50)         NOT NULL
);

INSERT INTO users (email, password, name, role)
VALUES ('admin@admin', '$2a$10$kDEwumd0A3EBbMm3wRyUSOBq1uFgR/nHoIXD/i3m1.NtQ4LemZ5gO', 'admin', 'ADMIN')
