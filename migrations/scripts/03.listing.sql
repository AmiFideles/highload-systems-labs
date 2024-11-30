CREATE TABLE listing (
                         id BIGSERIAL PRIMARY KEY,
                         title VARCHAR(255) NOT NULL,
                         description TEXT,
                         price DECIMAL(10, 2) NOT NULL,
                         creator_id BIGINT,
                         status VARCHAR(50) NOT NULL,
                         used BOOLEAN NOT NULL ,
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (creator_id) REFERENCES users(id)
);

CREATE INDEX ON listing(price);
CREATE INDEX ON listing(status);
CREATE INDEX ON listing(used);

CREATE TABLE category_listing (
                                  listing_id BIGINT NOT NULL,
                                  category_id BIGINT NOT NULL,
                                  PRIMARY KEY (listing_id, category_id),
                                  FOREIGN KEY (listing_id) REFERENCES listing(id),
                                  FOREIGN KEY (category_id) REFERENCES category(id)
);

CREATE INDEX ON category_listing(listing_id);
CREATE INDEX ON category_listing(category_id);
