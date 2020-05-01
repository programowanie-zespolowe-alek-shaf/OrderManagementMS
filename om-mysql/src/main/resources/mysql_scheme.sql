CREATE SCHEMA IF NOT EXISTS customer;

create user 'customer'@'%' identified by 'customer';
grant all on customer.* to 'customer'@'%';

USE customer;

CREATE TABLE user_order
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    shopping_card_id BIGINT                            NOT NULL,
    ship_date        DATE                              NOT NULL,
    status           VARCHAR(255)                      NOT NULL
);