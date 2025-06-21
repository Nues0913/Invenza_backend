-- 建立專案專用資料庫（如果還沒有的話）
CREATE DATABASE IF NOT EXISTS invenza_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 建立專用使用者
CREATE USER 'invenza_user'@'%' IDENTIFIED BY 'invenza_pass';

-- 授予此使用者對資料庫的所有權限
GRANT ALL PRIVILEGES ON invenza_db.* TO 'invenza_user'@'%';

-- 刷新權限設定
FLUSH PRIVILEGES;

-- 確認使用者已建立
SELECT user, host FROM mysql.user WHERE user = 'invenza_user';

-- 確認權限已設定
SHOW GRANTS FOR 'invenza_user'@'%';
