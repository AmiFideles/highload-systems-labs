CREATE TABLE saved_listing (
                               user_id BIGSERIAL,
                               listing_id BIGINT,
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               PRIMARY KEY (user_id, listing_id),
                               FOREIGN KEY (user_id) REFERENCES users(id),
                               FOREIGN KEY (listing_id) REFERENCES listing(id)
);

CREATE INDEX ON saved_listing(user_id);
CREATE INDEX ON saved_listing(listing_id);
