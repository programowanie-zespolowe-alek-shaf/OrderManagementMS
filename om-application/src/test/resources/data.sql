-- noinspection SqlWithoutWhere
DELETE
FROM user_order;

INSERT INTO user_order
VALUES (1, 1, '2020-05-12', 'PLACED', 1);
INSERT INTO user_order
VALUES (2, 2, '2020-05-14', 'APPROVED', 1);
INSERT INTO user_order
VALUES (3, 3, '2020-06-15', 'DELIVERED', 1);
