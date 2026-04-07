-- ============================================================
-- HJB Insurance - Schema Update v2
-- customer_account 添加个人信息字段；customer_id 改为可 NULL
-- 执行前请备份数据库！
-- ============================================================

USE nice;

-- 1. customer_id 改为可 NULL（注册时尚无 hjb_customer 记录）
ALTER TABLE customer_account
  MODIFY customer_id INT NULL;

-- 2. 添加个人信息字段（注册时填写，下单前暂存于此）
ALTER TABLE customer_account
  ADD COLUMN fname         VARCHAR(50)  NOT NULL DEFAULT '' AFTER email,
  ADD COLUMN lname         VARCHAR(50)  NOT NULL DEFAULT '' AFTER fname,
  ADD COLUMN gender        CHAR(1)      NOT NULL DEFAULT 'M' AFTER lname,
  ADD COLUMN marital_status CHAR(1)     NOT NULL DEFAULT 'S' AFTER gender,
  ADD COLUMN addr_street   VARCHAR(100) AFTER marital_status,
  ADD COLUMN addr_city     VARCHAR(50)  AFTER addr_street,
  ADD COLUMN addr_state    CHAR(2)      AFTER addr_city,
  ADD COLUMN zipcode       VARCHAR(10)  AFTER addr_state;
