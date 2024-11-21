CREATE TABLE deal_review
(
    deal_id    BIGINT PRIMARY KEY,
    rating     INT CHECK (rating BETWEEN 1 AND 5),
    comment    TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (deal_id) REFERENCES deal (id)
);

CREATE TABLE seller_review
(
    author_id  BIGINT, -- покупатель, который оставляет отзыв
    seller_id  BIGINT, -- продавец, на которого оставлен отзыв
    rating     INT CHECK (rating BETWEEN 1 AND 5),
    comment    TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (author_id, seller_id),
    FOREIGN KEY (author_id) REFERENCES users (id),
    FOREIGN KEY (seller_id) REFERENCES users (id)
);

CREATE INDEX ON seller_review(author_id);
CREATE INDEX ON seller_review(seller_id);
