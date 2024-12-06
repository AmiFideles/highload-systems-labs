INSERT INTO users (email, password, name, role)
VALUES ('moderator@email.com', '$2a$10$kDEwumd0A3EBbMm3wRyUSOBq1uFgR/nHoIXD/i3m1.NtQ4LemZ5gO', 'moderator', 'MODERATOR');

INSERT INTO users (email, password, name, role)
VALUES ('buyer@email.com', '$2a$10$kDEwumd0A3EBbMm3wRyUSOBq1uFgR/nHoIXD/i3m1.NtQ4LemZ5gO', 'buyer', 'BUYER');

INSERT INTO users (email, password, name, role)
VALUES ('seller@email.com', '$2a$10$kDEwumd0A3EBbMm3wRyUSOBq1uFgR/nHoIXD/i3m1.NtQ4LemZ5gO', 'seller', 'SELLER');

INSERT INTO users (email, password, name, role)
VALUES ('buyer2@email.com', '$2a$10$kDEwumd0A3EBbMm3wRyUSOBq1uFgR/nHoIXD/i3m1.NtQ4LemZ5gO', 'buyer2', 'BUYER');

INSERT INTO users (email, password, name, role)
VALUES ('seller2@email.com', '$2a$10$kDEwumd0A3EBbMm3wRyUSOBq1uFgR/nHoIXD/i3m1.NtQ4LemZ5gO', 'seller2', 'SELLER');

INSERT INTO users (email, password, name, role)
VALUES ('buyer3@email.com', '$2a$10$kDEwumd0A3EBbMm3wRyUSOBq1uFgR/nHoIXD/i3m1.NtQ4LemZ5gO', 'buyer3', 'BUYER');

INSERT INTO users (email, password, name, role)
VALUES ('seller3@email.com', '$2a$10$kDEwumd0A3EBbMm3wRyUSOBq1uFgR/nHoIXD/i3m1.NtQ4LemZ5gO', 'seller3', 'SELLER');

INSERT INTO users (email, password, name, role)
VALUES ('buyer4@email.com', '$2a$10$kDEwumd0A3EBbMm3wRyUSOBq1uFgR/nHoIXD/i3m1.NtQ4LemZ5gO', 'buyer4', 'BUYER');

INSERT INTO users (email, password, name, role)
VALUES ('seller4@email.com', '$2a$10$kDEwumd0A3EBbMm3wRyUSOBq1uFgR/nHoIXD/i3m1.NtQ4LemZ5gO', 'seller4', 'SELLER');

INSERT INTO seller_review (author_id, seller_id, rating, comment)
VALUES (7, 8, 3, '123');

INSERT INTO seller_review (author_id, seller_id, rating, comment)
VALUES (9, 10, 3, '123');

INSERT INTO listing (title, description, price, creator_id, status, used, created_at, updated_at)
VALUES ('title', 'description', 10, 4, 'OPEN', false, now(), now());

INSERT INTO listing (title, description, price, creator_id, status, used, created_at, updated_at)
VALUES ('title', 'description', 10, 6, 'OPEN', false, now(), now());

INSERT INTO listing (title, description, price, creator_id, status, used, created_at, updated_at)
VALUES ('title', 'description', 10, 8, 'OPEN', false, now(), now());

INSERT INTO deal (buyer_id, listing_id, total_price, status)
VALUES (3, 1, 10, 'COMPLETED');

INSERT INTO deal (buyer_id, listing_id, total_price, status)
VALUES (5, 2, 10, 'COMPLETED');

INSERT INTO deal (buyer_id, listing_id, total_price, status)
VALUES (7, 3, 10, 'COMPLETED');

INSERT INTO deal_review (deal_id, rating, comment, created_at)
VALUES (2, 3, 'comment', now());
