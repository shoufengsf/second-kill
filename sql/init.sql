INSERT INTO item(`name`, `code`, `stock`, `purchase_time`, `is_active`)
VALUES ('北京法源寺', 'book001', 100, NOW(), 1);
INSERT INTO item(`name`, `code`, `stock`, `purchase_time`, `is_active`)
VALUES ('虚拟的17岁', 'book002', 200, NOW(), 1);
INSERT INTO item(`name`, `code`, `stock`, `purchase_time`, `is_active`)
VALUES ('传统下的独白', 'book003', 300, NOW(), 1);

INSERT INTO item_kill(`item_id`, `total`, `start_time`, `end_time`, `is_active`)
VALUES (1, 10, '2019-09-21 17:00:00', '2019-09-21 17:10:00', 1);
INSERT INTO item_kill(`item_id`, `total`, `start_time`, `end_time`, `is_active`)
VALUES (2, 20, '2019-09-21 17:00:00', '2019-09-21 17:10:00', 1);
INSERT INTO item_kill(`item_id`, `total`, `start_time`, `end_time`, `is_active`)
VALUES (3, 30, '2019-09-21 17:00:00', '2019-09-21 17:10:00', 1);
