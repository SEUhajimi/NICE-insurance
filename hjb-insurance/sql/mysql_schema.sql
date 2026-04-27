-- HJB Insurance MySQL DDL
-- Updated to match normalized schema used by the application
-- Requires MySQL 8.0+ (CHECK constraint enforcement)
--
-- Schema design:
--   hjb_policy (supertype) + hjb_autopolicy / hjb_homepolicy (subtypes)
--   Separate invoice/payment tables per policy type (no nullable FKs)
--   Driver has direct FK to Vehicle (1:M), no junction table
--   VIN is VARCHAR(17) with length constraint

SET FOREIGN_KEY_CHECKS = 0;

-- ============================================================
-- DROP TABLES (child -> parent order)
-- ============================================================
DROP TABLE IF EXISTS hjb_home_payment;
DROP TABLE IF EXISTS hjb_auto_payment;
DROP TABLE IF EXISTS hjb_home_invoice;
DROP TABLE IF EXISTS hjb_auto_invoice;
DROP TABLE IF EXISTS hjb_home;
DROP TABLE IF EXISTS hjb_driver;
DROP TABLE IF EXISTS hjb_vehicle;
DROP TABLE IF EXISTS hjb_homepolicy;
DROP TABLE IF EXISTS hjb_autopolicy;
DROP TABLE IF EXISTS hjb_policy;
DROP TABLE IF EXISTS hjb_customer;
DROP TABLE IF EXISTS customer_account;
DROP TABLE IF EXISTS employee;
DROP TABLE IF EXISTS hjb_plan;

-- ============================================================
-- TABLES (parent -> child order)
-- ============================================================

CREATE TABLE IF NOT EXISTS hjb_customer
(
    CUST_ID        INT          NOT NULL AUTO_INCREMENT,
    FNAME          VARCHAR(15)  NOT NULL,
    LNAME          VARCHAR(15)  NOT NULL,
    Gender         CHAR(1)      DEFAULT NULL,
    Marital_Status CHAR(1)      DEFAULT NULL,
    Cust_Type      CHAR(1)      NOT NULL,
    Addr_Street    VARCHAR(15)  NOT NULL,
    Addr_City      VARCHAR(15)  NOT NULL,
    Addr_State     VARCHAR(15)  NOT NULL,
    Zipcode        VARCHAR(10)  NOT NULL,
    CONSTRAINT HJB_CUSTOMER_PK   PRIMARY KEY (CUST_ID),
    CONSTRAINT CHK_CUST_GENDER   CHECK (Gender IN ('F', 'M')),
    CONSTRAINT CHK_CUST_MARITAL  CHECK (Marital_Status IN ('M', 'S', 'W')),
    CONSTRAINT CHK_CUST_TYPE     CHECK (Cust_Type IN ('A', 'H', 'B'))
) ENGINE = InnoDB AUTO_INCREMENT = 1001;

-- Supertype table: shared fields for both auto and home policies
CREATE TABLE IF NOT EXISTS hjb_policy
(
    POLICY_ID            INT            NOT NULL AUTO_INCREMENT,
    SDATE                DATE           NOT NULL,
    EDATE                DATE           NOT NULL,
    Amount               DECIMAL(10,2)  NOT NULL,
    Status               CHAR(1)        NOT NULL,
    Policy_Type          CHAR(1)        NOT NULL COMMENT 'A = Auto, H = Home',
    HJB_CUSTOMER_CUST_ID INT            NOT NULL,
    CONSTRAINT HJB_POLICY_PK             PRIMARY KEY (POLICY_ID),
    CONSTRAINT CHK_POLICY_STATUS         CHECK (Status IN ('C', 'E')),
    CONSTRAINT CHK_POLICY_TYPE           CHECK (Policy_Type IN ('A', 'H')),
    CONSTRAINT CHK_POLICY_AMOUNT         CHECK (Amount >= 0.01),
    CONSTRAINT CHK_POLICY_SEDATE         CHECK (EDATE > SDATE),
    CONSTRAINT HJB_POLICY_CUSTOMER_FK    FOREIGN KEY (HJB_CUSTOMER_CUST_ID)
        REFERENCES hjb_customer (CUST_ID)
) ENGINE = InnoDB;

-- Subtype: Auto policy (shares POLICY_ID as AP_ID)
CREATE TABLE IF NOT EXISTS hjb_autopolicy
(
    AP_ID INT NOT NULL,
    CONSTRAINT HJB_AUTOPOLICY_PK      PRIMARY KEY (AP_ID),
    CONSTRAINT HJB_AUTOPOLICY_POLICY_FK FOREIGN KEY (AP_ID)
        REFERENCES hjb_policy (POLICY_ID)
) ENGINE = InnoDB;

-- Subtype: Home policy (shares POLICY_ID as HP_ID)
CREATE TABLE IF NOT EXISTS hjb_homepolicy
(
    HP_ID INT NOT NULL,
    CONSTRAINT HJB_HOMEPOLICY_PK       PRIMARY KEY (HP_ID),
    CONSTRAINT HJB_HOMEPOLICY_POLICY_FK FOREIGN KEY (HP_ID)
        REFERENCES hjb_policy (POLICY_ID)
) ENGINE = InnoDB;

-- Vehicle: linked to auto policy; VIN is VARCHAR(17) with length check
CREATE TABLE IF NOT EXISTS hjb_vehicle
(
    VIN                  VARCHAR(17)  NOT NULL,
    MMY                  VARCHAR(50)  NOT NULL  COMMENT 'Make-Model-Year',
    Status               CHAR(1)      NOT NULL  COMMENT 'L=leased, F=financed, O=owned',
    HJB_AUTOPOLICY_AP_ID INT          NOT NULL,
    CONSTRAINT HJB_VEHICLE_PK            PRIMARY KEY (VIN),
    CONSTRAINT CHK_VEHICLE_STATUS        CHECK (Status IN ('L', 'F', 'O')),
    CONSTRAINT CHK_VIN_LENGTH            CHECK (CHAR_LENGTH(VIN) = 17),
    CONSTRAINT HJB_VEHICLE_AUTOPOLICY_FK FOREIGN KEY (HJB_AUTOPOLICY_AP_ID)
        REFERENCES hjb_autopolicy (AP_ID)
) ENGINE = InnoDB;

-- Driver: 1:M with vehicle (each driver belongs to one vehicle)
CREATE TABLE IF NOT EXISTS hjb_driver
(
    Driver_License  VARCHAR(20)  NOT NULL,
    FNAME           VARCHAR(20)  NOT NULL,
    LNAME           VARCHAR(20)  NOT NULL,
    Birthday        DATE         NOT NULL,
    HJB_VEHICLE_VIN VARCHAR(17)  NOT NULL,
    CONSTRAINT HJB_DRIVER_PK         PRIMARY KEY (Driver_License),
    CONSTRAINT HJB_DRIVER_VEHICLE_FK FOREIGN KEY (HJB_VEHICLE_VIN)
        REFERENCES hjb_vehicle (VIN)
) ENGINE = InnoDB;

-- Home: linked to home policy
CREATE TABLE IF NOT EXISTS hjb_home
(
    Home_ID              INT            NOT NULL AUTO_INCREMENT,
    PDate                DATE           NOT NULL,
    PValue               DECIMAL(10,2)  NOT NULL,
    Area                 INT            NOT NULL,
    Home_Type            CHAR(1)        NOT NULL  COMMENT 'S=Single, M=Multi, C=Condo, T=Townhouse',
    AFN                  TINYINT(1)     NOT NULL  COMMENT 'Alarm/Fire/Natural disaster: 0 or 1',
    HSS                  TINYINT(1)     NOT NULL  COMMENT 'Home security system: 0 or 1',
    SP                   CHAR(1)        DEFAULT NULL COMMENT 'Swimming pool: U/O/I/M',
    Basement             TINYINT(1)     NOT NULL  COMMENT '0 or 1',
    HJB_HOMEPOLICY_HP_ID INT            NOT NULL,
    CONSTRAINT HJB_HOME_PK            PRIMARY KEY (Home_ID),
    CONSTRAINT CHK_HOME_TYPE          CHECK (Home_Type IN ('S', 'M', 'C', 'T')),
    CONSTRAINT CHK_HOME_AFN           CHECK (AFN IN (0, 1)),
    CONSTRAINT CHK_HOME_HSS           CHECK (HSS IN (0, 1)),
    CONSTRAINT CHK_HOME_SP            CHECK (SP IN ('U', 'O', 'I', 'M')),
    CONSTRAINT CHK_HOME_BASEMENT      CHECK (Basement IN (0, 1)),
    CONSTRAINT HJB_HOME_HOMEPOLICY_FK FOREIGN KEY (HJB_HOMEPOLICY_HP_ID)
        REFERENCES hjb_homepolicy (HP_ID)
) ENGINE = InnoDB;

-- Auto invoices (separate table; no nullable FKs)
-- AUTO_INCREMENT starts at 10001 to stay well above home invoices (20001+) preventing ID collisions in UNION queries
CREATE TABLE IF NOT EXISTS hjb_auto_invoice
(
    I_ID                 INT            NOT NULL AUTO_INCREMENT,
    I_Date               DATE           NOT NULL,
    Due                  DATE           NOT NULL,
    Amount               DECIMAL(10,2)  NOT NULL,
    HJB_AUTOPOLICY_AP_ID INT            NOT NULL,
    CONSTRAINT HJB_AUTO_INVOICE_PK          PRIMARY KEY (I_ID),
    CONSTRAINT CHK_AUTO_INV_AMOUNT          CHECK (Amount >= 0.01),
    CONSTRAINT CHK_AUTO_INV_DATES           CHECK (Due > I_Date),
    CONSTRAINT HJB_AUTO_INVOICE_AUTOPOL_FK  FOREIGN KEY (HJB_AUTOPOLICY_AP_ID)
        REFERENCES hjb_autopolicy (AP_ID)
) ENGINE = InnoDB;

-- Home invoices (separate table; no nullable FKs)
CREATE TABLE IF NOT EXISTS hjb_home_invoice
(
    I_ID                 INT            NOT NULL AUTO_INCREMENT,
    I_Date               DATE           NOT NULL,
    Due                  DATE           NOT NULL,
    Amount               DECIMAL(10,2)  NOT NULL,
    HJB_HOMEPOLICY_HP_ID INT            NOT NULL,
    CONSTRAINT HJB_HOME_INVOICE_PK          PRIMARY KEY (I_ID),
    CONSTRAINT CHK_HOME_INV_AMOUNT          CHECK (Amount >= 0.01),
    CONSTRAINT CHK_HOME_INV_DATES           CHECK (Due > I_Date),
    CONSTRAINT HJB_HOME_INVOICE_HOMEPOL_FK  FOREIGN KEY (HJB_HOMEPOLICY_HP_ID)
        REFERENCES hjb_homepolicy (HP_ID)
) ENGINE = InnoDB;

-- Auto payments
CREATE TABLE IF NOT EXISTS hjb_auto_payment
(
    P_ID                  INT            NOT NULL AUTO_INCREMENT,
    Method                VARCHAR(20)    NOT NULL,
    HJB_AUTO_INVOICE_I_ID INT            NOT NULL,
    Pay_Amount            DECIMAL(10,2)  NOT NULL,
    Pay_Date              DATE           NOT NULL,
    CONSTRAINT HJB_AUTO_PAYMENT_PK          PRIMARY KEY (P_ID),
    CONSTRAINT CHK_AUTO_PAY_AMOUNT          CHECK (Pay_Amount >= 0.01),
    CONSTRAINT CHK_AUTO_PAY_METHOD          CHECK (Method IN ('Credit Card', 'ACH', 'Check', 'Debit Card', 'Cash')),
    CONSTRAINT HJB_AUTO_PAYMENT_INVOICE_FK  FOREIGN KEY (HJB_AUTO_INVOICE_I_ID)
        REFERENCES hjb_auto_invoice (I_ID)
) ENGINE = InnoDB;

-- Home payments
CREATE TABLE IF NOT EXISTS hjb_home_payment
(
    P_ID                   INT            NOT NULL AUTO_INCREMENT,
    Method                 VARCHAR(20)    NOT NULL,
    HJB_HOME_INVOICE_I_ID  INT            NOT NULL,
    Pay_Amount             DECIMAL(10,2)  NOT NULL,
    Pay_Date               DATE           NOT NULL,
    CONSTRAINT HJB_HOME_PAYMENT_PK          PRIMARY KEY (P_ID),
    CONSTRAINT CHK_HOME_PAY_AMOUNT          CHECK (Pay_Amount >= 0.01),
    CONSTRAINT CHK_HOME_PAY_METHOD          CHECK (Method IN ('Credit Card', 'ACH', 'Check', 'Debit Card', 'Cash')),
    CONSTRAINT HJB_HOME_PAYMENT_INVOICE_FK  FOREIGN KEY (HJB_HOME_INVOICE_I_ID)
        REFERENCES hjb_home_invoice (I_ID)
) ENGINE = InnoDB;

-- Employee accounts (staff portal)
CREATE TABLE IF NOT EXISTS employee
(
    emp_id     INT          NOT NULL AUTO_INCREMENT,
    username   VARCHAR(50)  NOT NULL,
    password   VARCHAR(255) NOT NULL,
    email      VARCHAR(100) NOT NULL,
    fname      VARCHAR(50)  NOT NULL,
    lname      VARCHAR(50)  NOT NULL,
    role       VARCHAR(20)  NOT NULL DEFAULT 'EMPLOYEE' COMMENT 'EMPLOYEE or ADMIN',
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT EMPLOYEE_PK           PRIMARY KEY (emp_id),
    CONSTRAINT EMPLOYEE_USERNAME_UQ  UNIQUE (username),
    CONSTRAINT EMPLOYEE_EMAIL_UQ     UNIQUE (email),
    CONSTRAINT CHK_EMPLOYEE_ROLE     CHECK (role IN ('EMPLOYEE', 'ADMIN'))
) ENGINE = InnoDB;

-- Customer accounts (web portal login; customer_id linked after first purchase)
CREATE TABLE IF NOT EXISTS customer_account
(
    account_id     INT          NOT NULL AUTO_INCREMENT,
    customer_id    INT          DEFAULT NULL COMMENT 'FK set after first policy purchase',
    username       VARCHAR(50)  NOT NULL,
    password       VARCHAR(255) NOT NULL,
    email          VARCHAR(100) NOT NULL,
    fname          VARCHAR(50)  NOT NULL,
    lname          VARCHAR(50)  NOT NULL,
    gender         CHAR(1)      DEFAULT NULL,
    marital_status CHAR(1)      DEFAULT NULL,
    addr_street    VARCHAR(50)  NOT NULL,
    addr_city      VARCHAR(50)  NOT NULL,
    addr_state     VARCHAR(50)  NOT NULL,
    zipcode        VARCHAR(10)  NOT NULL,
    created_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT CUSTOMER_ACCOUNT_PK          PRIMARY KEY (account_id),
    CONSTRAINT CUSTOMER_ACCOUNT_USERNAME_UQ UNIQUE (username),
    CONSTRAINT CUSTOMER_ACCOUNT_EMAIL_UQ    UNIQUE (email),
    CONSTRAINT CUSTOMER_ACCOUNT_CUST_FK     FOREIGN KEY (customer_id)
        REFERENCES hjb_customer (CUST_ID)
) ENGINE = InnoDB;

-- Insurance plans shown on the landing page
CREATE TABLE IF NOT EXISTS hjb_plan
(
    plan_id    INT            NOT NULL AUTO_INCREMENT,
    plan_name  VARCHAR(100)   NOT NULL,
    plan_type  VARCHAR(10)    NOT NULL COMMENT 'AUTO or HOME',
    amount     DECIMAL(10,2)  NOT NULL,
    features   TEXT           DEFAULT NULL COMMENT 'Comma-separated feature list',
    is_active  TINYINT(1)     NOT NULL DEFAULT 1,
    CONSTRAINT HJB_PLAN_PK        PRIMARY KEY (plan_id),
    CONSTRAINT CHK_PLAN_TYPE      CHECK (plan_type IN ('AUTO', 'HOME')),
    CONSTRAINT CHK_PLAN_AMOUNT    CHECK (amount >= 0.01)
) ENGINE = InnoDB;

SET FOREIGN_KEY_CHECKS = 1;
