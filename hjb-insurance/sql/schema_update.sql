
-- ============================================================
-- HJB Insurance - Schema Update
-- 新增 employee 表和 customer_account 表
-- 执行前请备份数据库！
-- ============================================================

USE nice;

-- ------------------------------------------------------------
-- 1. 修改主键为 AUTO_INCREMENT（如果原表不是自增主键则需要执行）
-- ------------------------------------------------------------
SET FOREIGN_KEY_CHECKS = 0;
ALTER TABLE hjb_customer   MODIFY CUST_ID INT AUTO_INCREMENT;
ALTER TABLE hjb_autopolicy MODIFY AP_ID   INT AUTO_INCREMENT;
ALTER TABLE hjb_homepolicy MODIFY HP_ID   INT AUTO_INCREMENT;
ALTER TABLE hjb_invoice    MODIFY I_ID    INT AUTO_INCREMENT;
ALTER TABLE hjb_payment    MODIFY P_ID    INT AUTO_INCREMENT;
SET FOREIGN_KEY_CHECKS = 1;

-- ------------------------------------------------------------
-- 2. 创建员工表
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS employee (
    emp_id     INT          AUTO_INCREMENT PRIMARY KEY,
    username   VARCHAR(50)  UNIQUE NOT NULL,
    password   VARCHAR(255) NOT NULL,               -- BCrypt 加密存储
    email      VARCHAR(100) UNIQUE NOT NULL,
    fname      VARCHAR(50)  NOT NULL,
    lname      VARCHAR(50)  NOT NULL,
    role       VARCHAR(20)  NOT NULL DEFAULT 'EMPLOYEE', -- EMPLOYEE 或 ADMIN
    created_at DATETIME     DEFAULT CURRENT_TIMESTAMP
);

-- ------------------------------------------------------------
-- 3. 创建客户账号表（与 hjb_customer 通过外键关联）
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS customer_account (
    account_id  INT          AUTO_INCREMENT PRIMARY KEY,
    customer_id INT          NOT NULL,
    username    VARCHAR(50)  UNIQUE NOT NULL,
    password    VARCHAR(255) NOT NULL,              -- BCrypt 加密存储
    email       VARCHAR(100) UNIQUE NOT NULL,
    created_at  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES hjb_customer(CUST_ID)
);

-- ------------------------------------------------------------
-- 注意：默认 admin 账号由程序启动时自动创建（DataInitializer）
-- 默认账号: admin / Admin@123
-- ------------------------------------------------------------
