-- 測試資料 (data-test.sql)

-- 清空表
DELETE FROM member;
DELETE FROM commodity;
DELETE FROM procurement;
DELETE FROM orders;

-- 插入會員測試資料
INSERT INTO member (id, account, name, password, email, phone, role) VALUES ('F00001', 'invenza_user1', 'Admin', '$2a$10$wA9I1vWm4uark5G0VEV9Ke8wEpEFxE5GE25TXVVEqXTYwkl3j24Ey', 'user1@email.com', '0912345678', 'F');
INSERT INTO member (id, account, name, password, email, phone, role) VALUES ('400001', 'invenza_user2', '蔡英文', '$2b$12$6nBsacdVMIuTNURhPYS8buwgGIk2QZw0i9QcmeACO2aZvbOj1Djom', 'user2@email.com', '0914345678', '4');
INSERT INTO member (id, account, name, password, email, phone, role) VALUES ('200001', 'invenza_user3', '柯文哲', '$2b$12$vBqlQAD3Fb4Himv.frJVH.eFjrBlTMpXgGsgUSgzLBJ6xwOFL267S', 'user3@email.com', '0916345678', '2');
INSERT INTO member (id, account, name, password, email, phone, role) VALUES ('100001', 'invenza_user4', 'Remilia Scarlet', '$2b$12$sUATzoIXpLnMUzDyyFmFnuPWnhTCgMDvGGUDqWWG1McSbj15Y3wW6', 'user4@email.com', '0918345678', '1');
INSERT INTO member (id, account, name, password, email, phone, role) VALUES ('100002', 'invenza_user5', 'Beilleheila Langdass', '$2b$12$RuDS6.r6DJ1V5Cj5cSfnSOFbCmOdZtFxNiu4a0PQN4rNhTU09W3Re', 'user5@email.com', '0910345678', '1');

-- 插入商品測試資料
INSERT INTO commodity (id, name, type, stock_quantity, expected_import_quantity, expected_export_quantity) VALUES (1, 'apple', 'Z232', 30.0, 10.0, 20.0);
INSERT INTO commodity (id, name, type, stock_quantity, expected_import_quantity, expected_export_quantity) VALUES (2, 'banana', 'Y213', 12.0, 5.0, 7.0);
INSERT INTO commodity (id, name, type, stock_quantity, expected_import_quantity, expected_export_quantity) VALUES (3, 'cake', 'E545', 60.0, 10.0, 22.0);
-- 插入採購測試資料
INSERT INTO procurement (id, commodity_name, commodity_type, unit_price, quantity, supplier_name, supplier_id, supplier_email, supplier_phone, order_date, deadline_date, employee_name, employee_id, employee_email, employee_phone) VALUES (1, 'apple', 'Z232', 30.0, 10, 'WEEE', 'W213', 'supplier1@email.com', '0911111111', '2025-06-01 09:00', '2025-06-02 17:30', 'Admin', 'F00001', 'user1@email.com', '0912345678');
INSERT INTO procurement (id, commodity_name, commodity_type, unit_price, quantity, supplier_name, supplier_id, supplier_email, supplier_phone, order_date, deadline_date, employee_name, employee_id, employee_email, employee_phone) VALUES (3, 'Mississippi Queen', 'M414', 45.0, 17, 'Rigby', 'R147', 'Rigby147@email.com', '0917111111', '2025-06-08 10:15', '2025-06-22 14:00', 'Admin', 'F00001', 'user1@email.com', '0912345678');
INSERT INTO procurement (id, commodity_name, commodity_type, unit_price, quantity, supplier_name, supplier_id, supplier_email, supplier_phone, order_date, deadline_date, employee_name, employee_id, employee_email, employee_phone) VALUES (10, 'Krabby Patty', 'M414', 45.0, 17, 'Spongebob', 'S606', 'Spongebob@email.com', '0917111111', '2025-06-09 08:30', '2025-06-22 16:45', '蔡英文', '400001', 'user2@email.com', '0914345678');


-- 插入訂單測試資料
INSERT INTO orders (id, commodity_name, commodity_type, unit_price, quantity, dealer_name, dealer_id, dealer_email, dealer_phone, order_date, deadline_date) VALUES (1, 'apple', 'Z232', 30.0, 10, 'Andy', 'C487', 'dealer1@email.com', '0922222222', '2025-06-01 11:00', '2025-06-02 15:30');
INSERT INTO orders (id, commodity_name, commodity_type, unit_price, quantity, dealer_name, dealer_id, dealer_email, dealer_phone, order_date, deadline_date) VALUES (7, 'Ulti-Meatum', 'Y213', 12.0, 5, 'Betty', 'C488', 'dealer2@email.com', '0922222223', '2025-06-04 13:45', '2025-06-22 18:00');
