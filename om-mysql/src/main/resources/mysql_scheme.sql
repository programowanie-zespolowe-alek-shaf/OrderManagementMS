DROP SCHEMA IF EXISTS customer;
CREATE SCHEMA IF NOT EXISTS customer;

create user 'shopcard'@'%' identified by 'shopcard';
grant all on customer.* to 'shopcard'@'%';

USE customer;

CREATE TABLE shopping_card
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    userId     BIGINT                            NOT NULL,
    createDate DATE                              NOT NULL
);

CREATE TABLE shopping_card_item
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    shoppingCardId BIGINT                            NOT NULL,
    bookId         BIGINT                            NOT NULL,
    quantity       BIGINT                            NOT NULL,
    createDate     DATE                              NOT NULL
#   FOREIGN KEY (shoppingCardId) REFERENCES shoppingCards(id)
);

CREATE TABLE user_order
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    shopping_card_id BIGINT                            NOT NULL,
    ship_date        DATE                              NOT NULL,
    status           VARCHAR(255)                      NOT NULL,
    complete         TINYINT(1)                        NOT NULL DEFAULT '1'
);