CREATE TABLE category (
                          id BIGSERIAL PRIMARY KEY,
                          name VARCHAR(255) NOT NULL
);

CREATE INDEX ON category(name);
