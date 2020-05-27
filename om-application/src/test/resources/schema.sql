DROP ALL OBJECTS;

DROP SCHEMA IF EXISTS orders;
CREATE SCHEMA IF NOT EXISTS orders;

SET SCHEMA orders;

CREATE TABLE user_order
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    shopping_card_id BIGINT                            NOT NULL,
    ship_date        DATE                              NOT NULL,
    status           VARCHAR(255)                      NOT NULL,
    address          VARCHAR(255)                      NOT NULL
);