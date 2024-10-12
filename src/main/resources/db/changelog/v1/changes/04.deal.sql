CREATE TABLE deal (
                      id BIGSERIAL PRIMARY KEY,
                      buyer_id BIGINT,
                      listing_id BIGINT,
                      total_price DECIMAL(10, 2) NOT NULL,
                      status VARCHAR(50) CHECK(status IN ('PENDING', 'COMPLETED', 'CANCELED')) NOT NULL,
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      FOREIGN KEY (buyer_id) REFERENCES users(id),
                      FOREIGN KEY (listing_id) REFERENCES listing(id)
);

CREATE INDEX ON deal(buyer_id);
CREATE INDEX ON deal(status);
CREATE INDEX ON deal(listing_id);
