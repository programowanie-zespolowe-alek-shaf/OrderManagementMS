-- noinspection SqlWithoutWhere
DELETE
FROM user_order;

INSERT INTO user_order
VALUES (1, 1, '2020-05-12', 'PLACED', 'London Street, Dunedin Central, Dunedin 9016');
INSERT INTO user_order
VALUES (2, 2, '2020-05-14', 'APPROVED', 'London Street, Dunedin Central, Dunedin 9016');
INSERT INTO user_order
VALUES (3, 3, '2020-06-15', 'DELIVERED', 'London Street, Dunedin Central, Dunedin 9016');
