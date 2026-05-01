# HJB Insurance — 项目报告

> **课程**：NYU CS-GY 6083-B Principles of Database Systems  
> **项目**：Part II — Web Application  
> **最后更新**：2026-04-27

---

## 目录

1. [Executive Summary](#1-executive-summary)
2. [技术栈](#2-技术栈)
3. [系统架构](#3-系统架构)
4. [数据模型](#4-数据模型)
5. [API 接口](#5-api-接口)
6. [核心业务流程](#6-核心业务流程)
7. [安全功能](#7-安全功能)
8. [数据库设计（DDL 说明）](#8-数据库设计ddl-说明)
9. [各表记录数](#9-各表记录数)
10. [业务分析 SQL](#10-业务分析-sql)
11. [配置与部署](#11-配置与部署)
12. [文件速查表](#12-文件速查表)
13. [已知问题与改进空间](#13-已知问题与改进空间)
14. [加分项](#14-加分项)
    - 14.1 数据库索引优化
    - 14.2 数据可视化 Dashboard
    - 14.3 密码重置 OTP 安全校验
    

---

## 1. Executive Summary

### 业务背景

HJB Insurance 模拟一家中小型保险公司的核心业务系统，提供**汽车保险（Auto）**和**房屋保险（Home）**两类产品。业务痛点在于传统纸质流程效率低、客户无法自助管理保单，员工需要手动处理大量数据录入和账单追踪工作。

### 解决方案

构建一套前后端分离的 Web 应用，将保险公司的日常业务完整数字化：

- **客户侧**：注册账号、在线购保、查看保单和账单、在线支付，全程自助
- **员工侧**：统一管理客户档案、保单、资产（车辆/房产）、账单和支付记录
- **系统侧**：自动到期保单状态更新、事务并发控制、安全认证

### 数据模型设计思路

采用**表继承**模式处理两类保险产品的共性与差异：`hjb_autopolicy` / `hjb_homepolicy` 各自管理专属数据，账单和支付也拆分为 `auto_invoice/payment` 和 `home_invoice/payment`，避免大量 NULL 字段，保持数据完整性。客户账号（`customer_account`）与客户档案（`hjb_customer`）分离，注册时创建账号，首次购保时才创建客户档案，真实还原业务流程。

---

## 2. 技术栈

### 后端

| 组件 | 版本 | 用途 |
|------|------|------|
| Java | 17 | 运行环境 |
| Spring Boot | 3.2.5 | 应用框架 |
| Spring Security | (随 Boot) | 认证与授权 |
| MyBatis | 3.0.3 | ORM，注解 SQL |
| JJWT | 0.12.3 | JWT 令牌 |
| Lombok | 1.18.30 | 代码生成 |
| SpringDoc OpenAPI | 2.3.0 | Swagger 文档 |
| MySQL Connector | (随 Boot) | 数据库驱动 |

### 前端

| 组件 | 版本 | 用途 |
|------|------|------|
| Vue | 3.5+ | 前端框架 |
| Vite | 8.0+ | 构建工具 |
| Element Plus | 2.13+ | UI 组件库 |
| Axios | 1.14+ | HTTP 客户端 |
| Vue Router | 5.0+ | 前端路由 |

### 数据库

- **MySQL 8.0+**，数据库名 `nice3`

### 部署

- 前端：Vercel (`https://nice-insurance.vercel.app`)
- 后端：Docker 多阶段构建，容器化部署

---

## 3. 系统架构

```
┌─────────────────────────────────────┐
│  前端 (Vue 3 + Vercel)               │
│  客户门户 / 员工管理台 / 公开页面     │
└────────────────┬────────────────────┘
                 │ HTTPS / REST API
                 │ Authorization: Bearer <JWT>
┌────────────────▼────────────────────┐
│  后端 (Spring Boot + Docker)         │
│  ┌──────────┐ ┌──────────────────┐  │
│  │Controller│ │  Spring Security │  │
│  └────┬─────┘ │  JwtAuthFilter   │  │
│       │       └──────────────────┘  │
│  ┌────▼─────┐                       │
│  │ Service  │  @Transactional       │
│  └────┬─────┘                       │
│  ┌────▼─────┐                       │
│  │  Mapper  │  MyBatis Prepared SQL │
│  └────┬─────┘                       │
└───────┼─────────────────────────────┘
        │
┌───────▼─────────────────────────────┐
│  MySQL 8.0+  (数据库 nice3)          │
└─────────────────────────────────────┘
```

### Maven 多模块结构

```
hjb-insurance/
├── hjb-pojo/      Entity + DTO + Enum
├── hjb-common/    Result<T> 通用响应包装
└── hjb-server/    核心应用
    ├── config/        SecurityConfig, WebConfig, SwaggerConfig, DataInitializer
    ├── controller/    REST 控制器（含 admin/）
    ├── service/       业务逻辑接口 + 实现
    ├── mapper/        MyBatis 数据库操作
    ├── filter/        JwtAuthFilter
    ├── exception/     自定义异常 + GlobalExceptionHandler
    ├── utils/         JwtUtil
    └── scheduler/     PolicyScheduler（定时任务）
```

---

## 4. 数据模型

### ER 关系概览

```
customer_account ──(1:1, 首次购保)──► hjb_customer
                                           │
                               ┌───────────┴───────────┐
                               ▼                       ▼
                        hjb_autopolicy          hjb_homepolicy
                               │                       │
                    ┌──────────┤              ┌────────┴────────┐
                    ▼          ▼              ▼                 ▼
              hjb_vehicle  auto_invoice    hjb_home        home_invoice
                    │          │                                │
                    ▼          ▼                                ▼
              hjb_driver  auto_payment                   home_payment

employee        (独立，不关联客户体系)
hjb_plan        (保险套餐，独立)
```

### 核心表说明

#### 账户类

| 表 | 主键 | 关键字段 | 说明 |
|----|------|---------|------|
| `customer_account` | account_id | username(UNIQUE), password(BCrypt), email(UNIQUE), fname, lname, customer_id(FK,可NULL) | 客户注册账号 |
| `employee` | emp_id | username(UNIQUE), password(BCrypt), email(UNIQUE), role(EMPLOYEE/ADMIN) | 员工账号 |
| `hjb_customer` | cust_id (1001+) | fname, lname, gender(F/M), marital_status(M/S/W), cust_type(A/H/B), addr_* | 首次购保时创建 |

#### 保单类（超类型 + 子类型）

| 表 | 主键 | 关键字段 | 说明 |
|----|------|---------|------|
| `hjb_policy` | policy_id | sdate, edate, amount, status(C/E), policy_type(A/H), hjb_customer_cust_id | 保单超类型，存储所有保单共有字段 |
| `hjb_autopolicy` | ap_id (= policy_id) | — | 车险子类型，仅含 PK（共享 hjb_policy 数据） |
| `hjb_homepolicy` | hp_id (= policy_id) | — | 房险子类型，仅含 PK（共享 hjb_policy 数据） |

保单状态：`C` = Current（有效）/ `E` = Expired（已到期）

#### 资产类

| 表 | 主键 | 关键字段 | 说明 |
|----|------|---------|------|
| `hjb_vehicle` | vin (VARCHAR 17) | mmy, status(L/F/O), hjb_autopolicy_ap_id | 车辆，精确 17 位 VIN |
| `hjb_driver` | driver_license | fname, lname, birthday, hjb_vehicle_vin | 驾驶员，1辆车可有多个驾驶员 |
| `hjb_home` | home_id | pdate, pvalue, area, home_type(S/M/C/T), afn, hss, sp, basement | 房产 |

#### 账单与支付类

| 表 | 主键起始 | 关键字段 | 说明 |
|----|---------|---------|------|
| `hjb_auto_invoice` | 10001 | i_date, due, amount, hjb_autopolicy_ap_id | 车险账单 |
| `hjb_home_invoice` | 20001 | i_date, due, amount, hjb_homepolicy_hp_id | 房险账单（ID段错开避免冲突） |
| `hjb_auto_payment` | 1 | method, pay_amount, pay_date, invoice_id | 车险支付 |
| `hjb_home_payment` | 1 | method, pay_amount, pay_date, invoice_id | 房险支付 |

支付方式：`Credit Card` / `ACH` / `Check` / `Debit Card` / `Cash`

#### 其他

| 表 | 主键 | 关键字段 | 说明 |
|----|------|---------|------|
| `hjb_plan` | plan_id | plan_name, plan_type(AUTO/HOME), amount, features, is_active | 保险套餐 |

---

## 5. API 接口

### 通用响应格式

```json
{ "code": 1, "msg": "success", "data": { ... } }   // 成功
{ "code": 0, "msg": "错误描述", "data": null }       // 失败
```

### 认证接口（无需 Token）

```
POST /api/auth/employee/login
POST /api/auth/customer/login
POST /api/auth/customer/register
POST /api/auth/customer/send-otp      # 发送 6 位 OTP 到注册邮箱
POST /api/auth/customer/reset-password
```

### 客户门户（CUSTOMER Token）

```
GET  /api/portal/my-profile
POST /api/portal/purchase
GET  /api/portal/my-policies/auto
GET  /api/portal/my-policies/home
GET  /api/portal/my-invoices
GET  /api/portal/my-payments
POST /api/portal/payments
```

### 管理端（EMPLOYEE Token）

```
/api/customers              客户 CRUD
/api/auto-policies          车险保单 CRUD
/api/home-policies          房险保单 CRUD
/api/vehicles               车辆 CRUD
/api/drivers                驾驶员 CRUD
/api/homes                  房产 CRUD
/api/invoices               账单 CRUD
/api/payments               支付 CRUD
/api/vehicle-drivers        车辆-驾驶员关联查询
/api/admin/employees        员工 CRUD
/api/admin/plans            套餐 CRUD
GET /api/plans              查看活跃套餐（公开）
```

### 文档

```
Swagger UI:  /swagger-ui.html
API Docs:    /v3/api-docs
```

---

## 6. 核心业务流程

### 客户购保流程

```
注册 → 登录（获取 JWT）→ 浏览套餐 → 购保（POST /api/portal/purchase）
  └─ 自动创建 hjb_customer（首次）
  └─ 创建保单（autopolicy / homepolicy）
  └─ 生成账单（auto_invoice / home_invoice）
  └─ 更新 custType：A / H / B
```

### 支付流程（并发安全）

```
POST /api/portal/payments  [@Transactional REPEATABLE_READ]
  1. 验证账单归属本人（防越权）
  2. 已付总额 + 本次金额 <= 账单总额（防超付）
  3. 创建 payment 记录
```

### 密码重置流程（OTP）

```
POST /api/auth/customer/send-otp  {username, email}
  1. 验证用户名+邮箱匹配（防枚举攻击）
  2. 生成 6 位随机 OTP，存入内存（TTL 10 分钟）
  3. 通过 Gmail SMTP 发送 OTP 到注册邮箱

POST /api/auth/customer/reset-password  {username, email, otp, newPassword}
  1. 再次验证用户名+邮箱匹配
  2. 校验 OTP 正确且未过期
  3. BCrypt 加密新密码后更新，OTP 立即作废（一次性）
```

### 保单自动到期

```
每日 01:00  [cron: "0 0 1 * * *"]
  UPDATE hjb_policy SET Status='E' WHERE EDATE < TODAY AND Status='C'
```

### 启动初始化

```
启动时若无 "admin" 账号 → 自动创建（密码来自 ADMIN_PASSWORD 环境变量）
```

---

## 7. 安全功能

### SQL 注入防护

MyBatis 所有 SQL 使用 `#{}` 参数绑定（Prepared Statement），禁止字符串拼接。

```java
// 安全：MyBatis 自动使用 PreparedStatement
@Select("SELECT * FROM hjb_customer WHERE cust_id = #{custId}")
Customer findById(Integer custId);
```

### 密码安全

- BCrypt 加密存储，不存明文
- 登录时 `passwordEncoder.matches()` 比对

### 身份认证与授权

- JWT 无状态认证，Token 有效期 24 小时
- Spring Security URL 级别权限控制（CUSTOMER vs EMPLOYEE）
- JwtAuthFilter 每次请求校验 Token，Token 无效返回 401

### 事务并发控制

- 购保操作：`@Transactional(rollbackFor = Exception.class)`
- 支付操作：`@Transactional(isolation = Isolation.REPEATABLE_READ)` 防止并发重复支付

### 敏感信息保护

- GlobalExceptionHandler 对 500 错误不暴露内部信息
- JWT Secret 通过环境变量注入，不硬编码在代码中

### XSS / CSRF

- CSRF 禁用（前后端分离，Token 认证不依赖 Cookie）
- 前端使用 Element Plus 组件，输入通过框架自动处理

TODO: 详解一下 

### CORS

```
允许来源：http://localhost:*  /  https://nice-insurance.vercel.app
允许凭证：true
```

---

## 8. 数据库设计（DDL 说明）

DDL 完整内容见 `sql/mysql_schema.sql`。

### 设计要点

- 所有密码字段 `VARCHAR(255)` 存储 BCrypt hash
- `hjb_customer.CUST_ID` 从 1001 开始（`AUTO_INCREMENT = 1001`），区分于系统 ID
- `hjb_auto_invoice` 从 10001，`hjb_home_invoice` 从 20001，两类账单 ID 不重叠
- VIN 精确 17 字符：`VARCHAR(17)` + `CHECK(CHAR_LENGTH(vin) = 17)`
- 金额字段：`DECIMAL(10,2)` + `CHECK(amount >= 0.01)`
- 日期约束：`CHECK(EDATE > SDATE)`，`CHECK(DUE > I_DATE)`
- 所有外键均设置 `ON DELETE CASCADE` 或 `ON DELETE RESTRICT`（视业务）

---

## 9. 各表记录数

| 表名 | 记录数 | 说明 |
|------|--------|------|
| `hjb_customer` | 50 | 样本客户（CUST_ID 1001–1050） |
| `hjb_policy` | 80 | 超类型：车险 40 + 房险 40 |
| `hjb_autopolicy` | 40 | 车险子类型（AP_ID 2001–2040） |
| `hjb_homepolicy` | 40 | 房险子类型（HP_ID 3001–3040） |
| `hjb_vehicle` | 40 | 被保车辆（VIN 17 位） |
| `hjb_driver` | 40 | 驾驶员记录 |
| `hjb_home` | 40 | 被保房产 |
| `hjb_auto_invoice` | 40 | 车险账单 |
| `hjb_home_invoice` | 40 | 房险账单 |
| `hjb_auto_payment` | 40 | 车险支付记录 |
| `hjb_home_payment` | 40 | 房险支付记录 |
| `hjb_plan` | 6 | 保险套餐（Auto 3 档 + Home 3 档） |
| `employee` | 1 | 启动时自动创建 admin 账户 |
| `customer_account` | — | 用户注册后写入，Demo 时动态增加 |

> 实时查询：`SELECT 'hjb_customer' AS tbl, COUNT(*) AS cnt FROM hjb_customer UNION ALL ...`（完整 SQL 见 `sql/hjb_mysql_dml.sql` 头部注释）

---

## 10. 业务分析 SQL

> 以下 6 条 SQL 对应课程 Project Report 要求的 Q1–Q6。

---

### Q1 — 3 表以上 JOIN

**业务目的**：查看所有有效车险保单的详细信息，包含客户姓名、车辆 VIN 和驾驶员姓名。

```sql
SELECT
    c.CUST_ID                           AS customer_id,
    CONCAT(c.FNAME, ' ', c.LNAME)       AS customer_name,
    ap.AP_ID                            AS policy_id,
    ap.SDATE                            AS start_date,
    ap.EDATE                            AS end_date,
    ap.AMOUNT                           AS premium,
    v.VIN                               AS vin,
    v.MMY                               AS vehicle,
    CONCAT(d.FNAME, ' ', d.LNAME)       AS driver_name
FROM hjb_customer         c
JOIN hjb_autopolicy       ap ON ap.HJB_CUSTOMER_CUST_ID = c.CUST_ID
JOIN hjb_vehicle          v  ON v.HJB_AUTOPOLICY_AP_ID  = ap.AP_ID
JOIN hjb_driver           d  ON d.HJB_VEHICLE_VIN       = v.VIN
WHERE ap.STATUS = 'C'
ORDER BY c.CUST_ID, ap.AP_ID;
```

---

### Q2 — Multi-row Subquery

**业务目的**：找出保费金额高于所有房险保单平均保费的车险保单客户。

```sql
SELECT
    c.CUST_ID                        AS customer_id,
    CONCAT(c.FNAME, ' ', c.LNAME)    AS customer_name,
    ap.AP_ID                         AS auto_policy_id,
    ap.AMOUNT                        AS premium
FROM hjb_customer   c
JOIN hjb_autopolicy ap ON ap.HJB_CUSTOMER_CUST_ID = c.CUST_ID
WHERE ap.AMOUNT > ALL (
    SELECT hp.AMOUNT
    FROM hjb_homepolicy hp
    WHERE hp.STATUS = 'C'
)
ORDER BY ap.AMOUNT DESC;
```

---

### Q3 — Correlated Subquery

**业务目的**：找出每位客户账单中存在未付清（已付金额 < 账单金额）的车险账单。

```sql
SELECT
    c.CUST_ID                        AS customer_id,
    CONCAT(c.FNAME, ' ', c.LNAME)    AS customer_name,
    ai.I_ID                          AS invoice_id,
    ai.AMOUNT                        AS invoice_amount,
    ai.DUE                           AS due_date,
    COALESCE(
        (SELECT SUM(p.PAY_AMOUNT)
         FROM hjb_auto_payment p
         WHERE p.HJB_AUTO_INVOICE_I_ID = ai.I_ID), 0
    )                                AS paid_amount
FROM hjb_customer    c
JOIN hjb_autopolicy  ap ON ap.HJB_CUSTOMER_CUST_ID = c.CUST_ID
JOIN hjb_auto_invoice ai ON ai.HJB_AUTOPOLICY_AP_ID = ap.AP_ID
WHERE ai.AMOUNT > (
    SELECT COALESCE(SUM(p2.PAY_AMOUNT), 0)
    FROM hjb_auto_payment p2
    WHERE p2.HJB_AUTO_INVOICE_I_ID = ai.I_ID
)
ORDER BY ai.DUE;
```

---

### Q4 — SET Operator（UNION）

**业务目的**：列出所有持有保单的客户（无论车险还是房险），并标注保险类型。

```sql
SELECT
    c.CUST_ID                        AS customer_id,
    CONCAT(c.FNAME, ' ', c.LNAME)    AS customer_name,
    'Auto Insurance'                 AS policy_type,
    ap.AP_ID                         AS policy_id,
    ap.AMOUNT                        AS premium
FROM hjb_customer   c
JOIN hjb_autopolicy ap ON ap.HJB_CUSTOMER_CUST_ID = c.CUST_ID

UNION ALL

SELECT
    c.CUST_ID,
    CONCAT(c.FNAME, ' ', c.LNAME),
    'Home Insurance',
    hp.HP_ID,
    hp.AMOUNT
FROM hjb_customer    c
JOIN hjb_homepolicy  hp ON hp.HJB_CUSTOMER_CUST_ID = c.CUST_ID

ORDER BY customer_id, policy_type;
```

---

### Q5 — WITH Clause（CTE）

**业务目的**：统计每位客户的总保费支出（车险+房险），找出总保费高于平均水平的客户。

```sql
WITH customer_premium AS (
    SELECT
        c.CUST_ID,
        CONCAT(c.FNAME, ' ', c.LNAME)                       AS customer_name,
        COALESCE(SUM(ap.AMOUNT), 0)                         AS auto_premium,
        COALESCE(SUM(hp.AMOUNT), 0)                         AS home_premium,
        COALESCE(SUM(ap.AMOUNT), 0) + COALESCE(SUM(hp.AMOUNT), 0) AS total_premium
    FROM hjb_customer c
    LEFT JOIN hjb_autopolicy ap ON ap.HJB_CUSTOMER_CUST_ID = c.CUST_ID
    LEFT JOIN hjb_homepolicy hp ON hp.HJB_CUSTOMER_CUST_ID = c.CUST_ID
    GROUP BY c.CUST_ID, c.FNAME, c.LNAME
),
avg_premium AS (
    SELECT AVG(total_premium) AS avg_total FROM customer_premium
)
SELECT
    cp.customer_name,
    cp.auto_premium,
    cp.home_premium,
    cp.total_premium,
    ROUND(ap.avg_total, 2)  AS avg_premium
FROM customer_premium cp, avg_premium ap
WHERE cp.total_premium > ap.avg_total
ORDER BY cp.total_premium DESC;
```

---

### Q6 — TOP-N Query

**业务目的**：找出支付金额最高的前 5 名客户（车险和房险支付合计）。

```sql
SELECT
    c.CUST_ID                        AS customer_id,
    CONCAT(c.FNAME, ' ', c.LNAME)    AS customer_name,
    c.CUST_TYPE                      AS insurance_type,
    COALESCE(SUM(apy.PAY_AMOUNT), 0) AS auto_paid,
    COALESCE(SUM(hpy.PAY_AMOUNT), 0) AS home_paid,
    COALESCE(SUM(apy.PAY_AMOUNT), 0)
        + COALESCE(SUM(hpy.PAY_AMOUNT), 0) AS total_paid
FROM hjb_customer c
LEFT JOIN hjb_autopolicy  ap  ON ap.HJB_CUSTOMER_CUST_ID  = c.CUST_ID
LEFT JOIN hjb_auto_invoice ai ON ai.HJB_AUTOPOLICY_AP_ID  = ap.AP_ID
LEFT JOIN hjb_auto_payment apy ON apy.HJB_AUTO_INVOICE_I_ID = ai.I_ID
LEFT JOIN hjb_homepolicy  hp  ON hp.HJB_CUSTOMER_CUST_ID  = c.CUST_ID
LEFT JOIN hjb_home_invoice hi ON hi.HJB_HOMEPOLICY_HP_ID  = hp.HP_ID
LEFT JOIN hjb_home_payment hpy ON hpy.HJB_HOME_INVOICE_I_ID = hi.I_ID
GROUP BY c.CUST_ID, c.FNAME, c.LNAME, c.CUST_TYPE
ORDER BY total_paid DESC
LIMIT 5;
```

---

## 11. 配置与部署

### 环境变量

| 变量 | 默认值 | 说明 |
|------|--------|------|
| `PORT` | `8080` | 服务端口 |
| `DB_HOST` | `localhost` | 数据库主机 |
| `DB_USER` | `root` | 数据库用户名 |
| `DB_PASS` | `213212297` | 数据库密码 |
| `JWT_SECRET` | (内置值) | JWT 签名密钥 |
| `ADMIN_PASSWORD` | — | 初始管理员密码 |

### 本地启动

```bash
# 初始化数据库
mysql -u root -p < sql/mysql_schema.sql
mysql -u root -p nice3 < sql/hjb_mysql_dml.sql

# 构建运行
mvn clean package -DskipTests
java -jar hjb-server/target/hjb-server-1.0-SNAPSHOT.jar
# 访问 http://localhost:8080/swagger-ui.html
```

### Dockerfile

```dockerfile
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY ../hjb-insurance .
RUN cd hjb-insurance && mvn clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/hjb-insurance/hjb-server/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

## 12. 文件速查表

| 内容 | 路径 |
|------|------|
| Entity 类 | `hjb-pojo/src/main/java/com/hjb/nice/entity/` |
| DTO 类 | `hjb-pojo/src/main/java/com/hjb/nice/dto/` |
| Result 包装类 | `hjb-common/src/main/java/com/hjb/nice/result/Result.java` |
| 控制器 | `hjb-server/src/main/java/com/hjb/nice/server/controller/` |
| Service 实现 | `hjb-server/src/main/java/com/hjb/nice/server/service/impl/` |
| Mapper | `hjb-server/src/main/java/com/hjb/nice/server/mapper/` |
| 安全配置 | `hjb-server/src/main/java/com/hjb/nice/server/config/SecurityConfig.java` |
| JWT 过滤器 | `hjb-server/src/main/java/com/hjb/nice/server/filter/JwtAuthFilter.java` |
| 定时任务 | `hjb-server/src/main/java/com/hjb/nice/server/scheduler/PolicyScheduler.java` |
| 应用配置 | `hjb-server/src/main/resources/application.yml` |
| 数据库 DDL | `sql/mysql_schema.sql` |
| 数据库 DML | `sql/hjb_mysql_dml.sql` |
| Claude 上下文 | `CLAUDE.md` |
| 协作指南 | `CLAUDE_WORKFLOW.md` |

---

## 13. 已知问题与改进空间

| 问题 | 优先级 | 状态 |
|------|--------|------|
| 密码重置 OTP 邮件验证 | High | ✅ 已完成 |
| 删除保单后 `custType` 不自动更新 | Medium | 待处理 |
| 缺分页功能 | Medium | 待实现 |

---

## 14. 加分项

### 14.1 数据库索引优化

#### 背景与问题

MySQL InnoDB **不会自动为外键列建立索引**。项目中所有外键列在建表时仅有 `FOREIGN KEY` 约束，没有对应的 B-Tree 索引，导致所有基于 FK 的 `WHERE` 查询都退化为全表扫描（`EXPLAIN type: ALL`）。

这一问题在支付场景下尤为突出：`makePayment()` 在 `REPEATABLE_READ` 隔离级别下执行，若 `hjb_auto_payment(HJB_AUTO_INVOICE_I_ID)` 无索引，计算已付总额的 SUM 查询需要持有全表的行锁，直接加剧并发支付的锁竞争，与事务隔离设计的目标相悖。

#### 高频查询路径分析

客户每次加载门户页面，`CustomerPortalServiceImpl` 都会执行以下嵌套查询链：

```
getProfile / getAutoPolicies / getInvoices / getPayments
  │
  ├─ hjb_policy         WHERE HJB_CUSTOMER_CUST_ID = ?   ← 全表扫描（无索引）
  │    │
  │    ├─ hjb_auto_invoice   WHERE HJB_AUTOPOLICY_AP_ID = ?  ← 全表扫描
  │    │    └─ hjb_auto_payment  WHERE HJB_AUTO_INVOICE_I_ID = ?  ← 全表扫描
  │    │
  │    └─ hjb_home_invoice   WHERE HJB_HOMEPOLICY_HP_ID = ?  ← 全表扫描
  │         └─ hjb_home_payment  WHERE HJB_HOME_INVOICE_I_ID = ?  ← 全表扫描
  │
  └─ hjb_vehicle   WHERE HJB_AUTOPOLICY_AP_ID = ?  ← 全表扫描
       └─ hjb_driver    WHERE HJB_VEHICLE_VIN = ?  ← 全表扫描
```

#### 建立的索引（共 8 个）

完整 SQL 见 `sql/hjb_mysql_ddl.sql` 末尾 CREATE INDEX 部分。

| 索引名 | 表 | 列 | 覆盖的查询 |
|--------|----|----|-----------|
| `idx_policy_customer` | `hjb_policy` | `HJB_CUSTOMER_CUST_ID` | 按客户查保单（每次门户加载） |
| `idx_auto_invoice_policy` | `hjb_auto_invoice` | `HJB_AUTOPOLICY_AP_ID` | 按保单查账单 |
| `idx_auto_payment_invoice` | `hjb_auto_payment` | `HJB_AUTO_INVOICE_I_ID` | 按账单查支付（含 REPEATABLE_READ 事务内） |
| `idx_home_invoice_policy` | `hjb_home_invoice` | `HJB_HOMEPOLICY_HP_ID` | 房险账单查询 |
| `idx_home_payment_invoice` | `hjb_home_payment` | `HJB_HOME_INVOICE_I_ID` | 房险支付查询（含事务内） |
| `idx_vehicle_policy` | `hjb_vehicle` | `HJB_AUTOPOLICY_AP_ID` | 按保单查车辆 |
| `idx_driver_vehicle` | `hjb_driver` | `HJB_VEHICLE_VIN` | 按 VIN 查驾驶员 |
| `idx_home_policy` | `hjb_home` | `HJB_HOMEPOLICY_HP_ID` | 按保单查房产 |

#### EXPLAIN 对比（以核心查询为例）

**加索引前**

```sql
EXPLAIN SELECT ap.AP_ID, p.SDATE, p.EDATE, p.Amount, p.Status, p.HJB_CUSTOMER_CUST_ID
        FROM hjb_autopolicy ap
        JOIN hjb_policy p ON ap.AP_ID = p.POLICY_ID
        WHERE p.HJB_CUSTOMER_CUST_ID = 1001;
```

| table | type | key | rows | Extra |
|-------|------|-----|------|-------|
| p | **ALL** | NULL | *(全表行数)* | Using where |
| ap | eq_ref | PRIMARY | 1 | — |

**加索引后**

```sql
-- 运行 sql/hjb_mysql_ddl.sql 末尾 CREATE INDEX 后重新执行相同 EXPLAIN
```

| table | type | key | rows | Extra |
|-------|------|-----|------|-------|
| p | **ref** | idx_policy_customer | **1~2** | Using index condition |
| ap | eq_ref | PRIMARY | 1 | — |

`type` 从 `ALL`（全表扫描）变为 `ref`（索引查找），`rows` 从全表行数降至实际匹配行数。在数据量增大时效果更显著。

#### 与并发控制的关联

`makePayment()` 中计算已付金额的查询：

```java
// CustomerPortalServiceImpl.java 第 204 行
BigDecimal paid = autoPaymentMapper.findByInvoiceId(req.getInvoiceId()).stream()
        .map(AutoPayment::getPayAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
```

该查询在 `REPEATABLE_READ` 事务内执行。无索引时全表扫描持锁范围大、持锁时间长；加索引后精确定位到目标行，锁粒度从表级降为行级，并发支付的吞吐量显著提升。

---

### 14.2 数据可视化 Dashboard

在员工管理台新增 **Dashboard** 页面（`/dashboard`），通过 ECharts 图表库将保险业务数据可视化。

#### 后端统计接口

新增 `GET /api/admin/stats`（需 EMPLOYEE Token），一次返回所有图表所需数据：

```json
{
  "policyByType":    [{"label": "A", "value": 15}, {"label": "H", "value": 10}],
  "policyByStatus":  [{"label": "C", "value": 18}, {"label": "E", "value": 7}],
  "customerByType":  [{"label": "A", "value": 8}, ...],
  "paymentByMethod": [{"label": "Credit Card", "value": 12}, ...],
  "monthlyRevenue":  [{"month": "2025-11", "revenue": 5600.00}, ...]
}
```

月度收入使用 UNION ALL 将两类账单合并后按月聚合：

```sql
SELECT DATE_FORMAT(I_Date, '%Y-%m') AS month, ROUND(SUM(Amount), 2) AS revenue
FROM (
  SELECT I_Date, Amount FROM hjb_auto_invoice
  UNION ALL
  SELECT I_Date, Amount FROM hjb_home_invoice
) t
WHERE I_Date >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH)
GROUP BY month ORDER BY month
```

#### 前端图表（Apache ECharts）

| 图表 | 类型 | 数据来源 | 业务含义 |
|------|------|---------|---------|
| Policy Type Distribution | 环形图 (Donut) | `policyByType` | Auto vs Home 保单占比 |
| Policy Status Overview | 环形图 (Donut) | `policyByStatus` | 有效 vs 已到期保单占比 |
| Payment Method Distribution | 饼图 (Pie) | `paymentByMethod` | 客户偏好的支付方式 |
| Monthly Revenue (6mo) | 柱状图 (Bar) | `monthlyRevenue` | 近 6 个月保费收入趋势 |

页面顶部另有 4 张**汇总数字卡片**（Total Policies / Active Policies / Insured Customers / Total Revenue），数据均由同一接口计算得出。

**实现文件：**
- 后端：`hjb-server/.../mapper/StatsMapper.java`、`hjb-server/.../controller/admin/StatsController.java`
- 前端：`src/views/DashboardView.vue`（ECharts，响应式 resize）

---

### 14.3 密码重置 OTP 安全校验

#### 背景

基础需求仅要求用户名+邮箱匹配即可重置密码，存在任何知道对方账号的人都能修改密码的风险。

#### 实现方案

采用**基于邮件的一次性验证码（OTP）**双因素校验流程：

**核心组件：**
- `OtpStore`：线程安全的内存存储（`ConcurrentHashMap`），每条记录携带 6 位随机码 + 到期时间戳（10 分钟 TTL）。`@Scheduled` 每 5 分钟清理过期记录
- `EmailService`：通过 Gmail SMTP（STARTTLS，端口 587）发送包含验证码的邮件
- `AuthController`：两个新端点 `send-otp` / `reset-password`

**安全设计要点：**

| 要点 | 实现 |
|------|------|
| 防枚举攻击 | `send-otp` 先验证用户名+邮箱匹配，不匹配则不发送也不暴露账号是否存在 |
| 一次性 OTP | 验证成功后立即从 `OtpStore` 删除，无法复用 |
| 短 TTL | OTP 10 分钟后自动失效 |
| 密码加密 | 新密码经 BCrypt 加密后存储，不存明文 |
| 传输安全 | SMTP STARTTLS 加密传输，App Password 认证 |

**密码重置流程：**

```
Step 1 — POST /api/auth/customer/send-otp
  ① 校验 username + email 匹配 customer_account
  ② SecureRandom 生成 6 位 OTP，存入 OtpStore（email → {code, expiry}）
  ③ JavaMailSender 发送 OTP 到用户注册邮箱

Step 2 — POST /api/auth/customer/reset-password
  ① 再次校验 username + email 匹配
  ② OtpStore.verify(email, otp) — 检查存在且未过期
  ③ 验证通过后 OTP 从 store 中删除（一次性）
  ④ BCrypt 加密新密码后写入 customer_account
```

**实现文件：**
- `hjb-server/.../service/OtpStore.java`
- `hjb-server/.../service/EmailService.java`
- `hjb-server/.../controller/auth/AuthController.java`（`send-otp` / `reset-password`）
- `hjb-server/.../service/impl/AuthServiceImpl.java`（`sendResetOtp` / `resetPassword`）
- 前端：`src/views/CustomerAuthView.vue`（两步式 UI：输入账号→收验证码→重置密码）

