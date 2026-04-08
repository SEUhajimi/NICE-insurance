# HJB Insurance 教学项目开发文档

> 本文档面向计算机专业新生，以 HJB Insurance 保险管理系统为载体，完整讲解一个前后端分离 Web 项目从需求分析、系统设计到编码实现的全流程。读完本文档并动手实践，你将掌握：Spring Boot 后端开发、MyBatis 数据库操作、Spring Security + JWT 认证、Vue 3 前端开发、以及前后端联调的完整技能。

---

## 目录

1. [项目概述](#1-项目概述)
2. [需求分析](#2-需求分析)
3. [技术选型](#3-技术选型)
4. [系统设计](#4-系统设计)
5. [开发环境搭建](#5-开发环境搭建)
6. [数据库设计与实现](#6-数据库设计与实现)
7. [后端开发](#7-后端开发)
8. [前端开发](#8-前端开发)
9. [前后端联调](#9-前后端联调)
10. [功能演示与测试](#10-功能演示与测试)
11. [常见问题与解决方案](#11-常见问题与解决方案)
12. [附录](#12-附录)

---

## 1. 项目概述

### 1.1 项目背景

HJB Insurance 是一个模拟真实保险公司的业务管理系统。保险公司的核心业务包括：管理客户信息、销售保险产品（车险、房险）、生成账单、处理支付。

本项目将这些业务搬到 Web 系统上，分为两个角色：
- **管理员（员工）**：通过后台管理客户、保单、账单、支付等所有数据
- **客户**：通过官网注册账号、浏览产品、购买保险、在个人中心查看自己的保单和账单

### 1.2 系统功能概览

```
HJB Insurance
├── 官网 (LandingPage)
│   ├── 产品展示（车险/房险套餐）
│   ├── 公司介绍
│   └── 跳转注册/登录
│
├── 客户门户 (Customer Portal)
│   ├── 注册 / 登录 / 找回密码
│   ├── 个人资料
│   ├── 购买保险（选套餐）
│   ├── 我的保单
│   ├── 我的账单（在线支付）
│   └── 支付记录
│
└── 管理后台 (Admin Dashboard)
    ├── 客户管理
    ├── 车险保单管理
    ├── 房险保单管理
    ├── 车辆管理
    ├── 驾驶员管理
    ├── 房产管理
    ├── 账单管理
    ├── 支付管理
    ├── 员工管理
    └── 保险套餐管理
```

---

## 2. 需求分析

### 2.1 角色与权限

| 角色 | 如何登录 | 能做什么 |
|------|----------|----------|
| 游客 | 无需登录 | 浏览官网、查看套餐价格 |
| 客户 | `/customer-login` 页面 | 注册/登录、购险、查看自己的保单和账单 |
| 员工/管理员 | `/login` 页面 | 管理所有业务数据 |

### 2.2 核心业务流程

#### 客户购险流程
```
注册账号 → 登录 → 选择套餐（车险/房险）→ 系统自动：
  1. 创建 hjb_customer 记录（首次购险才创建）
  2. 创建 hjb_autopolicy 或 hjb_homepolicy 记录
  3. 生成 hjb_invoice 账单（30天内付款）
  4. 更新客户类型（A车险/H房险/B两者）
→ 客户在账单页面支付 → 生成 hjb_payment 记录
```

#### 保单到期流程
```
系统每天凌晨1点自动检查 → 将到期保单状态由 C(Current) 改为 T(Terminated)
```

### 2.3 数据约束

- 账单到期日 ≥ 账单日期（数据库约束）
- 支付金额 ≤ 账单未付余额
- 用户名、邮箱全局唯一
- 密码存储必须加密（BCrypt）
- 客户可以没有 hjb_customer 记录（注册但未购险）

---

## 3. 技术选型

### 3.1 技术栈一览

| 层次 | 技术 | 版本 | 说明 |
|------|------|------|------|
| 后端框架 | Spring Boot | 3.2.5 | Java Web 应用框架 |
| ORM框架 | MyBatis | 3.0.3 | 数据库操作框架 |
| 安全框架 | Spring Security | (随Spring Boot) | 认证与授权 |
| 认证方式 | JWT (JJWT) | 0.12.3 | 无状态身份令牌 |
| 数据库 | MySQL | 8.0 | 关系型数据库 |
| 代码简化 | Lombok | 1.18.30 | 自动生成getter/setter等 |
| API文档 | SpringDoc OpenAPI | 2.3.0 | 自动生成Swagger文档 |
| 前端框架 | Vue | 3.5 | 渐进式JavaScript框架 |
| 构建工具 | Vite | 8.0 | 前端构建工具 |
| UI组件库 | Element Plus | 2.13 | Vue 3 UI组件库 |
| HTTP客户端 | Axios | 1.14 | 前端发起HTTP请求 |
| 前端路由 | Vue Router | 5.0 | 前端页面路由 |

### 3.2 为什么选这些技术？

- **Spring Boot**：Java 后端最主流框架，配置简单，生态丰富，企业用得最多
- **MyBatis**：SQL 透明可控，适合初学者理解数据库操作（比 JPA 更直观）
- **JWT**：前后端分离项目的标准认证方案，不需要服务器存储 Session
- **Vue 3**：上手快，中文文档好，国内前端生产环境主流选择
- **Element Plus**：开箱即用的表单、表格、对话框组件，适合后台管理系统

---

## 4. 系统设计

### 4.1 项目架构

```
前端（Vue 3 + Vite）         后端（Spring Boot）
       │                            │
       │  HTTP请求（JSON）           │
       │ ─────────────────────────→ │
       │                            ├── Controller（接收请求）
       │                            ├── Service（业务逻辑）
       │                            ├── Mapper（数据库操作）
       │ ←───────────────────────── │
       │  HTTP响应（Result<T>）      │
                                    │
                              MySQL数据库
```

**后端项目结构（Maven 多模块）**：
```
hjb-insurance/                    ← 父项目
├── hjb-pojo/                     ← 实体类模块（Entity + DTO）
├── hjb-common/                   ← 公共模块（Result 响应包装类）
└── hjb-server/                   ← 主服务模块（Controller/Service/Mapper）
```

> **为什么分模块？** 将实体类单独放在 hjb-pojo 模块，将来如果需要写客户端 SDK，可以直接引用这个模块，不需要把整个服务都打包进去。

### 4.2 数据库 ER 关系图

```
employee                     customer_account
(员工/管理员账号)                (客户账号)
                                    │ customer_id (可为空)
                                    ↓
                             hjb_customer ←──────────────────────┐
                             (客户记录)                           │
                               │                                 │
                ───────────────┴────────────────                 │
                │                              │                 │
                ↓                              ↓                 │
         hjb_autopolicy               hjb_homepolicy            │
         (车险保单)                    (房险保单)                 │
              │                              │                   │
              │                              ↓                   │
              │                         hjb_home                 │
              ↓                         (房产信息)               │
         hjb_vehicle ←── hjb_vehicle_driver ──→ hjb_driver      │
         (车辆)            (多对多关联)           (驾驶员)         │
              │                              │                   │
              └──────────────┬───────────────┘                   │
                             ↓                                   │
                        hjb_invoice                              │
                        (账单)                                   │
                             │                                   │
                             ↓                                   │
                        hjb_payment                              │
                        (支付记录)                               │
                                                                 │
                         hjb_plan ───────────────────────────────┘
                         (保险套餐，供购险选择)
```

### 4.3 数据库表结构

#### employee（员工/管理员）
| 字段 | 类型 | 说明 |
|------|------|------|
| emp_id | INT PK AUTO | 主键 |
| username | VARCHAR(50) UNIQUE | 登录用户名 |
| password | VARCHAR(255) | BCrypt 加密密码 |
| email | VARCHAR(100) UNIQUE | 邮箱 |
| fname | VARCHAR(50) | 名 |
| lname | VARCHAR(50) | 姓 |
| role | VARCHAR(20) | 角色：EMPLOYEE 或 ADMIN |
| created_at | DATETIME | 创建时间 |

#### customer_account（客户账号）
| 字段 | 类型 | 说明 |
|------|------|------|
| account_id | INT PK AUTO | 主键 |
| customer_id | INT FK NULL | 关联 hjb_customer，注册时为 NULL |
| username | VARCHAR(50) UNIQUE | 登录用户名 |
| password | VARCHAR(255) | BCrypt 加密密码 |
| email | VARCHAR(100) UNIQUE | 邮箱 |
| fname/lname | VARCHAR(50) | 姓名 |
| gender | CHAR(1) | M/F |
| marital_status | CHAR(1) | S单身/M已婚/W丧偶 |
| addr_street/city/state | VARCHAR | 地址 |
| zipcode | VARCHAR(10) | 邮编 |

#### hjb_customer（客户记录，购险后创建）
| 字段 | 类型 | 说明 |
|------|------|------|
| cust_id | INT PK AUTO | 主键 |
| fname/lname | VARCHAR(50) | 姓名 |
| gender | CHAR(1) | 性别 |
| marital_status | CHAR(1) | 婚姻状况 |
| cust_type | CHAR(1) | A=车险/H=房险/B=两者 |
| addr_* | VARCHAR | 地址信息 |

#### hjb_autopolicy（车险保单）
| 字段 | 类型 | 说明 |
|------|------|------|
| ap_id | INT PK AUTO | 主键 |
| sdate | DATE | 开始日期 |
| edate | DATE | 结束日期 |
| amount | DECIMAL(10,2) | 年费 |
| status | CHAR(1) | C=有效/T=已终止 |
| hjb_customer_cust_id | INT FK | 关联客户 |

#### hjb_homepolicy（房险保单）
结构与车险保单相同，主键为 hp_id。

#### hjb_invoice（账单）
| 字段 | 类型 | 说明 |
|------|------|------|
| i_id | INT PK AUTO | 主键 |
| i_date | DATE | 开单日期 |
| due | DATE | 到期日（≥ i_date） |
| amount | DECIMAL(10,2) | 账单金额 |
| hjb_autopolicy_ap_id | INT FK NULL | 关联车险保单 |
| hjb_homepolicy_hp_id | INT FK NULL | 关联房险保单 |

#### hjb_payment（支付记录）
| 字段 | 类型 | 说明 |
|------|------|------|
| p_id | INT PK AUTO | 主键 |
| method | VARCHAR(20) | Credit/Debit/Check/PayPal |
| hjb_invoice_i_id | INT FK | 关联账单 |
| pay_amount | DECIMAL(10,2) | 支付金额 |
| pay_date | DATE | 支付日期 |

#### hjb_plan（保险套餐）
| 字段 | 类型 | 说明 |
|------|------|------|
| plan_id | INT PK AUTO | 主键 |
| plan_name | VARCHAR(50) | 套餐名（Basic/Standard/Premium） |
| plan_type | VARCHAR(10) | AUTO 或 HOME |
| amount | DECIMAL(10,2) | 年费 |
| features | VARCHAR(500) | 特性（逗号分隔） |
| is_active | TINYINT(1) | 1=显示给用户，0=隐藏 |

### 4.4 API 接口设计

所有接口统一响应格式：
```json
{
  "code": 1,       // 1=成功, 0=失败
  "msg": "success",
  "data": { ... }  // 响应数据，失败时为null
}
```

**公开接口（无需登录）**

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/auth/employee/login | 员工登录 |
| POST | /api/auth/customer/login | 客户登录 |
| POST | /api/auth/customer/register | 客户注册 |
| POST | /api/auth/customer/reset-password | 重置密码 |
| GET  | /api/plans | 获取可用保险套餐 |

**客户接口（需 CUSTOMER 角色 JWT）**

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/portal/my-profile | 获取个人资料 |
| POST | /api/portal/purchase | 购买保险 |
| GET | /api/portal/my-policies/auto | 我的车险保单 |
| GET | /api/portal/my-policies/home | 我的房险保单 |
| GET | /api/portal/my-invoices | 我的账单 |
| POST | /api/portal/payments | 支付账单 |
| GET | /api/portal/my-payments | 支付记录 |

**管理员接口（需 EMPLOYEE 角色 JWT）**

| 路径前缀 | 说明 |
|---------|------|
| /api/customers | 客户 CRUD |
| /api/auto-policies | 车险保单 CRUD |
| /api/home-policies | 房险保单 CRUD |
| /api/invoices | 账单 CRUD |
| /api/payments | 支付 CRUD |
| /api/vehicles | 车辆 CRUD |
| /api/drivers | 驾驶员 CRUD |
| /api/homes | 房产 CRUD |
| /api/vehicle-drivers | 车辆-驾驶员关联 |
| /api/admin/employees | 员工管理 |
| /api/admin/plans | 保险套餐管理 |

---

## 5. 开发环境搭建

### 5.1 安装 JDK 17

1. 打开 [https://adoptium.net](https://adoptium.net)，下载 **Temurin 17 LTS**
2. 安装完成后，设置环境变量：
   - 新建系统变量 `JAVA_HOME` = `C:\Program Files\Eclipse Adoptium\jdk-17.x.x-hotspot`（你的实际安装路径）
   - 在 `Path` 变量中添加 `%JAVA_HOME%\bin`
3. 验证安装：
   ```bash
   java -version
   # 应输出: openjdk version "17.x.x"
   ```

### 5.2 安装 Maven 3.9+

1. 下载 [https://maven.apache.org/download.cgi](https://maven.apache.org/download.cgi) 中的 Binary zip
2. 解压到 `C:\tools\maven`
3. 将 `C:\tools\maven\bin` 添加到系统 `Path`
4. 验证：
   ```bash
   mvn -version
   # 应输出: Apache Maven 3.x.x
   ```

### 5.3 安装 Node.js 20 LTS

1. 下载 [https://nodejs.org](https://nodejs.org) LTS 版本
2. 安装，全程默认
3. 验证：
   ```bash
   node -v   # v20.x.x 或更高
   npm -v    # 10.x.x 或更高
   ```

### 5.4 安装 MySQL 8.0

1. 下载 MySQL Installer：[https://dev.mysql.com/downloads/installer/](https://dev.mysql.com/downloads/installer/)
2. 选择 Developer Default 安装
3. **记住你设置的 root 密码**
4. 验证（打开 MySQL Command Line）：
   ```sql
   SELECT VERSION(); -- 应输出 8.x.x
   ```

### 5.5 安装 IDE

**后端：IntelliJ IDEA Community Edition（免费）**
- 下载：[https://www.jetbrains.com/idea/download/](https://www.jetbrains.com/idea/download/)
- 安装时勾选：.java 文件关联、Add to PATH

**前端：VS Code**
- 下载：[https://code.visualstudio.com/](https://code.visualstudio.com/)
- 安装插件：
  - **Volar**（Vue Language Features）
  - **ESLint**

### 5.6 项目目录规划

在你的工作目录下创建如下结构（两个项目必须平级）：
```
workspace/
├── hjb-insurance/       ← 后端项目（本文档中创建）
└── hjb-frontend-vue/    ← 前端项目（本文档中创建）
```

---

## 6. 数据库设计与实现

### 6.1 创建数据库

打开 MySQL Workbench 或命令行，执行：
```sql
CREATE DATABASE nice CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE nice;
```

### 6.2 建表（执行顺序很重要！）

> 由于存在外键约束，必须先建被引用的表，再建引用表。

**第一批：无外键依赖的基础表**

```sql
-- 车险保单表（暂时先建空表，后续执行 schema_update.sql 会加 AUTO_INCREMENT）
CREATE TABLE hjb_customer (
    cust_id       INT PRIMARY KEY,
    fname         VARCHAR(50),
    lname         VARCHAR(50),
    gender        CHAR(1),
    marital_status CHAR(1),
    cust_type     CHAR(1),
    addr_street   VARCHAR(100),
    addr_city     VARCHAR(50),
    addr_state    VARCHAR(20),
    zipcode       VARCHAR(10)
);

CREATE TABLE hjb_autopolicy (
    ap_id                  INT PRIMARY KEY,
    sdate                  DATE,
    edate                  DATE,
    amount                 DECIMAL(10,2),
    status                 CHAR(1),
    hjb_customer_cust_id   INT,
    CONSTRAINT fk_ap_cust FOREIGN KEY (hjb_customer_cust_id) REFERENCES hjb_customer(cust_id)
);

CREATE TABLE hjb_homepolicy (
    hp_id                  INT PRIMARY KEY,
    sdate                  DATE,
    edate                  DATE,
    amount                 DECIMAL(10,2),
    status                 CHAR(1),
    hjb_customer_cust_id   INT,
    CONSTRAINT fk_hp_cust FOREIGN KEY (hjb_customer_cust_id) REFERENCES hjb_customer(cust_id)
);

CREATE TABLE hjb_vehicle (
    vin                    VARCHAR(17) PRIMARY KEY,
    mmy                    VARCHAR(100),
    status                 VARCHAR(20),
    hjb_autopolicy_ap_id   INT,
    CONSTRAINT fk_veh_ap FOREIGN KEY (hjb_autopolicy_ap_id) REFERENCES hjb_autopolicy(ap_id)
);

CREATE TABLE hjb_driver (
    driver_license VARCHAR(20) PRIMARY KEY,
    fname          VARCHAR(50),
    lname          VARCHAR(50),
    birthday       DATE
);

CREATE TABLE hjb_vehicle_driver (
    hjb_vehicle_vin              VARCHAR(17),
    hjb_driver_driver_license    VARCHAR(20),
    PRIMARY KEY (hjb_vehicle_vin, hjb_driver_driver_license),
    CONSTRAINT fk_vd_v FOREIGN KEY (hjb_vehicle_vin) REFERENCES hjb_vehicle(vin),
    CONSTRAINT fk_vd_d FOREIGN KEY (hjb_driver_driver_license) REFERENCES hjb_driver(driver_license)
);

CREATE TABLE hjb_home (
    home_id                INT PRIMARY KEY AUTO_INCREMENT,
    pdate                  DATE,
    pvalue                 DECIMAL(12,2),
    area                   INT,
    home_type              VARCHAR(20),
    afn                    INT,
    hss                    INT,
    sp                     VARCHAR(10),
    basement               INT,
    hjb_homepolicy_hp_id   INT,
    CONSTRAINT fk_home_hp FOREIGN KEY (hjb_homepolicy_hp_id) REFERENCES hjb_homepolicy(hp_id)
);

CREATE TABLE hjb_invoice (
    i_id                       INT PRIMARY KEY,
    i_date                     DATE,
    due                        DATE,
    amount                     DECIMAL(10,2),
    hjb_autopolicy_ap_id       INT NULL,
    hjb_homepolicy_hp_id       INT NULL,
    CONSTRAINT fk_inv_ap FOREIGN KEY (hjb_autopolicy_ap_id) REFERENCES hjb_autopolicy(ap_id),
    CONSTRAINT fk_inv_hp FOREIGN KEY (hjb_homepolicy_hp_id) REFERENCES hjb_homepolicy(hp_id),
    CONSTRAINT invoice_due_idate CHECK (due >= i_date)
);

CREATE TABLE hjb_payment (
    p_id              INT PRIMARY KEY,
    method            VARCHAR(20),
    hjb_invoice_i_id  INT,
    pay_amount        DECIMAL(10,2),
    pay_date          DATE,
    CONSTRAINT fk_pay_inv FOREIGN KEY (hjb_invoice_i_id) REFERENCES hjb_invoice(i_id)
);
```

**第二批：执行迁移脚本（按版本顺序）**

```sql
-- schema_update.sql：开启 AUTO_INCREMENT，创建 employee 和 customer_account 表
-- schema_update_v2.sql：customer_account 增加个人信息字段，customer_id 改为可空
-- schema_update_v3.sql：创建 hjb_plan 套餐表并插入初始数据
```

> 实际操作：在 MySQL Workbench 中依次打开并执行项目 `sql/` 目录下的三个文件。

**验证建表结果：**
```sql
SHOW TABLES;
-- 应看到 10 张表
```

---

## 7. 后端开发

### 7.1 创建 Maven 多模块项目

#### 步骤 1：创建父项目

打开 IntelliJ IDEA → New Project → Maven Archetype：
- GroupId：`com.hjb.nice`
- ArtifactId：`hjb-insurance`
- 清空 `src/` 目录（父项目不写代码）

**父 pom.xml 关键配置：**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <!-- 继承 Spring Boot 父 POM，自动管理所有 Spring 相关依赖版本 -->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.5</version>
    </parent>

    <groupId>com.hjb.nice</groupId>
    <artifactId>hjb-insurance</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>pom</packaging>   <!-- 父项目必须是 pom 类型 -->

    <!-- 声明子模块 -->
    <modules>
        <module>hjb-pojo</module>
        <module>hjb-common</module>
        <module>hjb-server</module>
    </modules>

    <properties>
        <java.version>17</java.version>
        <mybatis.version>3.0.3</mybatis.version>
    </properties>

    <!-- 依赖版本管理（子模块引用时不需要写版本号） -->
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.mybatis.spring.boot</groupId>
                <artifactId>mybatis-spring-boot-starter</artifactId>
                <version>${mybatis.version}</version>
            </dependency>
            <dependency>
                <groupId>com.hjb.nice</groupId>
                <artifactId>hjb-pojo</artifactId>
                <version>${project.version}</version>
            </dependency>
            <dependency>
                <groupId>com.hjb.nice</groupId>
                <artifactId>hjb-common</artifactId>
                <version>${project.version}</version>
            </dependency>
        </dependencies>
    </dependencyManagement>
</project>
```

#### 步骤 2：创建子模块

右键父项目 → New Module，创建三个子模块：`hjb-pojo`、`hjb-common`、`hjb-server`

---

### 7.2 hjb-common 模块：统一响应类

这个模块只有一个类，定义了所有接口返回的统一格式。

**目录结构：**
```
hjb-common/src/main/java/com/hjb/nice/result/Result.java
```

```java
package com.hjb.nice.result;

import lombok.Data;

/**
 * 统一 API 响应包装类
 * 所有接口都返回这个类型，前端统一处理
 *
 * 成功示例：{"code":1,"msg":"success","data":{...}}
 * 失败示例：{"code":0,"msg":"用户不存在","data":null}
 */
@Data
public class Result<T> {
    private Integer code;   // 1=成功, 0=失败
    private String msg;
    private T data;

    /** 有数据的成功响应 */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.code = 1;
        result.msg = "success";
        result.data = data;
        return result;
    }

    /** 无数据的成功响应（增删改操作常用） */
    public static <T> Result<T> success() {
        return success(null);
    }

    /** 失败响应 */
    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<>();
        result.code = 0;
        result.msg = msg;
        return result;
    }
}
```

---

### 7.3 hjb-pojo 模块：实体类

**pom.xml 依赖：**
```xml
<dependencies>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    <!-- 用于 @JsonProperty 注解，解决 Jackson 序列化驼峰问题 -->
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-annotations</artifactId>
    </dependency>
</dependencies>
```

> **Lombok 的作用**：在类上加 `@Data` 注解，Lombok 会在编译时自动生成 `getter`、`setter`、`toString`、`equals`、`hashCode` 等方法，大幅减少样板代码。

**重要知识点：Jackson 序列化陷阱**

当字段名前两个字母都是大写时（如 `iId`、`pId`），Java 默认生成的 getter 为 `getIId()`，Jackson 在序列化时会错误地将其变成 `IId`（首字母不降低）。解决方案是使用 `@JsonProperty` 注解强制指定 JSON 中的字段名：

```java
@JsonProperty("iId")   // 强制 JSON 输出为 "iId"，而不是 "IId"
private Integer iId;
```

**关键实体类示例：**

```java
// AutoPolicy.java
package com.hjb.nice.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class AutoPolicy {
    private Integer apId;           // 对应数据库 ap_id（MyBatis 下划线转驼峰）
    private LocalDate sdate;        // 开始日期
    private LocalDate edate;        // 结束日期
    private BigDecimal amount;      // 年保费
    private String status;          // C=有效, T=已终止
    private Integer hjbCustomerCustId; // 外键，关联客户
}
```

```java
// Invoice.java
package com.hjb.nice.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class Invoice {
    @JsonProperty("iId")    // 必须！否则前端收到的是 "IId"
    private Integer iId;

    @JsonProperty("iDate")  // 必须！否则前端收到的是 "IDate"
    private LocalDate iDate;

    private LocalDate due;
    private BigDecimal amount;
    private Integer hjbHomepolicyHpId;
    private Integer hjbAutopolicyApId;
}
```

---

### 7.4 hjb-server 模块：核心服务

**pom.xml 依赖（关键部分）：**
```xml
<dependencies>
    <!-- 引用本项目其他模块 -->
    <dependency>
        <groupId>com.hjb.nice</groupId>
        <artifactId>hjb-pojo</artifactId>
    </dependency>
    <dependency>
        <groupId>com.hjb.nice</groupId>
        <artifactId>hjb-common</artifactId>
    </dependency>

    <!-- Spring Boot Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- Spring Security -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>

    <!-- MyBatis -->
    <dependency>
        <groupId>org.mybatis.spring.boot</groupId>
        <artifactId>mybatis-spring-boot-starter</artifactId>
    </dependency>

    <!-- MySQL 驱动 -->
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- JWT -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.12.3</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>0.12.3</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>0.12.3</version>
        <scope>runtime</scope>
    </dependency>

    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

**application.yml：**
```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/nice?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8
    username: root
    password: 你的MySQL密码    # ← 修改这里
    driver-class-name: com.mysql.cj.jdbc.Driver

mybatis:
  configuration:
    map-underscore-to-camel-case: true  # 自动将数据库 cust_id → Java custId

# JWT 配置
jwt:
  secret: aGpiLWluc3VyYW5jZS1zZWNyZXQta2V5LW11c3QtYmUtYXQtbGVhc3QtMzItY2hhcnMhIQ==
  expiration: 86400000  # 24小时，单位毫秒
```

---

### 7.5 JWT 工具类

**什么是 JWT？**

JWT（JSON Web Token）是一种用于身份认证的令牌。结构如下：
```
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsInJvbGUiOiJFTVBMT1lFRSJ9.xxx
└─── Header ──────┘.└────────── Payload ────────────────────────┘.└Signature┘
```

- **Header**：算法类型（HS256）
- **Payload**：存放用户名、角色、过期时间等信息（不要存密码！）
- **Signature**：用服务器密钥签名，防止篡改

**流程：**
1. 用户登录成功 → 服务器生成 JWT 返回给前端
2. 前端存储 JWT（localStorage）
3. 前端每次请求在 Header 中携带 JWT：`Authorization: Bearer <token>`
4. 服务器验证 JWT 合法性 → 识别用户身份

```java
package com.hjb.nice.server.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * 生成 JWT token
     * @param username 用户名（存入 sub 字段）
     * @param role 角色（存入 role 字段）
     */
    public String generateToken(String username, String role) {
        return Jwts.builder()
                .subject(username)                          // 主体（用户名）
                .claim("role", role)                        // 自定义字段：角色
                .issuedAt(new Date())                       // 签发时间
                .expiration(new Date(System.currentTimeMillis() + expiration))  // 过期时间
                .signWith(getSignKey())                     // 签名
                .compact();
    }

    /** 从 token 中提取用户名 */
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    /** 从 token 中提取角色 */
    public String extractRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    /** 验证 token 是否有效（签名正确且未过期） */
    public boolean isTokenValid(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
```

---

### 7.6 JWT 过滤器

每个请求到达 Controller 之前，都会经过这个过滤器验证身份：

```java
package com.hjb.nice.server.filter;

import com.hjb.nice.server.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws jakarta.servlet.ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // 检查 Authorization 头是否包含 Bearer token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // 去掉 "Bearer " 前缀

            if (jwtUtil.isTokenValid(token)) {
                String username = jwtUtil.extractUsername(token);
                String role = jwtUtil.extractRole(token);

                // 将用户信息存入 Spring Security 上下文
                // Spring Security 要求角色以 "ROLE_" 开头
                UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                        username, null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + role))
                    );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
```

---

### 7.7 Spring Security 配置

这是整个后端安全体系的核心配置，控制哪些接口需要认证、哪些角色可以访问：

```java
package com.hjb.nice.server.config;

import com.hjb.nice.server.filter.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)          // 前后端分离不需要 CSRF
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 无状态（使用JWT）
            .authorizeHttpRequests(auth -> auth
                // ① 放行所有 CORS 预检请求（OPTIONS），否则浏览器跨域请求会 403
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // ② 公开接口：登录/注册
                .requestMatchers("/api/auth/**").permitAll()
                // ③ 公开接口：套餐列表（官网展示用）
                .requestMatchers(HttpMethod.GET, "/api/plans").permitAll()
                // ④ Swagger 文档（开发环境）
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                // ⑤ 客户门户：仅 CUSTOMER 角色
                .requestMatchers("/api/portal/**").hasRole("CUSTOMER")
                // ⑥ 管理端：仅 EMPLOYEE 角色
                .requestMatchers("/api/admin/**").hasRole("EMPLOYEE")
                .requestMatchers("/api/**").hasRole("EMPLOYEE")
                .anyRequest().authenticated()
            )
            // 将 JWT 过滤器注册到 Spring Security 过滤链中
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCrypt 是目前最安全的密码哈希算法之一
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // 允许所有 localhost 端口（前端开发时 Vite 会随机选端口）
        config.setAllowedOriginPatterns(List.of("http://localhost:*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
```

> **CORS 是什么？** 浏览器的安全策略不允许 `http://localhost:5173`（前端）直接请求 `http://localhost:8080`（后端），因为端口不同（跨域）。服务器需要在响应头中声明"允许来自 localhost 的请求"，这就是 CORS 配置的作用。

---

### 7.8 全局异常处理

集中处理所有 Controller 抛出的异常，返回统一格式：

```java
package com.hjb.nice.server.config;

import com.hjb.nice.result.Result;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice  // 应用于所有 @RestController
public class GlobalExceptionHandler {

    /** 业务异常：返回 400 Bad Request */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleRuntimeException(RuntimeException e) {
        return Result.error(e.getMessage());
    }

    /** 其他未知异常：返回 500 Internal Server Error */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleException(Exception e) {
        return Result.error("服务器内部错误：" + e.getMessage());
    }
}
```

---

### 7.9 Mapper 层（数据库操作）

MyBatis 的 Mapper 接口通过注解直接写 SQL，配合 `map-underscore-to-camel-case: true`，数据库字段 `cust_id` 自动映射到 Java 字段 `custId`。

**典型 Mapper 示例：**

```java
package com.hjb.nice.server.mapper;

import com.hjb.nice.entity.AutoPolicy;
import org.apache.ibatis.annotations.*;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface AutoPolicyMapper {

    @Select("SELECT * FROM hjb_autopolicy")
    List<AutoPolicy> findAll();

    @Select("SELECT * FROM hjb_autopolicy WHERE AP_ID = #{apId}")
    AutoPolicy findById(Integer apId);

    @Select("SELECT * FROM hjb_autopolicy WHERE HJB_CUSTOMER_CUST_ID = #{custId}")
    List<AutoPolicy> findByCustomerId(Integer custId);

    // INSERT 时不传 ID，让数据库自动生成，然后通过 @Options 回填到对象中
    @Insert("INSERT INTO hjb_autopolicy(SDATE, EDATE, Amount, Status, HJB_CUSTOMER_CUST_ID) " +
            "VALUES(#{sdate}, #{edate}, #{amount}, #{status}, #{hjbCustomerCustId})")
    @Options(useGeneratedKeys = true, keyProperty = "apId")  // 自动将生成的ID赋值给apId字段
    void insertAutoId(AutoPolicy autoPolicy);

    @Update("UPDATE hjb_autopolicy SET SDATE=#{sdate}, EDATE=#{edate}, Amount=#{amount}, " +
            "Status=#{status}, HJB_CUSTOMER_CUST_ID=#{hjbCustomerCustId} WHERE AP_ID=#{apId}")
    void update(AutoPolicy autoPolicy);

    @Delete("DELETE FROM hjb_autopolicy WHERE AP_ID = #{apId}")
    void deleteById(Integer apId);

    // 定时任务调用：批量更新过期保单状态
    @Update("UPDATE hjb_autopolicy SET Status='T' WHERE EDATE < #{today} AND Status='C'")
    int updateExpired(@Param("today") LocalDate today);
}
```

---

### 7.10 定时任务：保单自动到期

```java
package com.hjb.nice.server.config;

import com.hjb.nice.server.mapper.AutoPolicyMapper;
import com.hjb.nice.server.mapper.HomePolicyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 保单到期定时任务
 * 每天凌晨 1:00 自动检查并更新过期保单状态
 */
@Component
public class PolicyScheduler {

    @Autowired
    private AutoPolicyMapper autoPolicyMapper;

    @Autowired
    private HomePolicyMapper homePolicyMapper;

    // cron 表达式：秒 分 时 日 月 周
    // "0 0 1 * * *" = 每天凌晨 1:00:00 执行
    @Scheduled(cron = "0 0 1 * * *")
    public void expirePolicies() {
        LocalDate today = LocalDate.now();
        int autoCount = autoPolicyMapper.updateExpired(today);
        int homeCount = homePolicyMapper.updateExpired(today);
        System.out.println("保单到期更新：车险 " + autoCount + " 条，房险 " + homeCount + " 条");
    }
}
```

在启动类上开启定时任务：
```java
@SpringBootApplication
@EnableScheduling  // 必须加！
public class HjbApplication {
    public static void main(String[] args) {
        SpringApplication.run(HjbApplication.class, args);
    }
}
```

---

### 7.11 Controller 层

**认证 Controller（AuthController）：**

```java
package com.hjb.nice.server.controller.auth;

import com.hjb.nice.dto.LoginRequest;
import com.hjb.nice.dto.ResetPasswordRequest;
import com.hjb.nice.result.Result;
import com.hjb.nice.server.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /** 员工登录 */
    @PostMapping("/employee/login")
    public Result employeeLogin(@RequestBody LoginRequest request) {
        return Result.success(authService.employeeLogin(request));
    }

    /** 客户登录 */
    @PostMapping("/customer/login")
    public Result customerLogin(@RequestBody LoginRequest request) {
        return Result.success(authService.customerLogin(request));
    }

    /** 客户注册 */
    @PostMapping("/customer/register")
    public Result customerRegister(@RequestBody CustomerRegisterRequest request) {
        authService.customerRegister(request);
        return Result.success();
    }

    /** 重置密码（通过用户名+邮箱验证身份） */
    @PostMapping("/customer/reset-password")
    public Result resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return Result.success();
    }
}
```

**标准 CRUD Controller 示例（AutoPolicyController）：**

```java
package com.hjb.nice.server.controller;

import com.hjb.nice.entity.AutoPolicy;
import com.hjb.nice.result.Result;
import com.hjb.nice.server.mapper.AutoPolicyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auto-policies")
public class AutoPolicyController {

    @Autowired
    private AutoPolicyMapper autoPolicyMapper;

    @GetMapping
    public Result<List<AutoPolicy>> findAll() {
        return Result.success(autoPolicyMapper.findAll());
    }

    @GetMapping("/{id}")
    public Result<AutoPolicy> findById(@PathVariable Integer id) {
        return Result.success(autoPolicyMapper.findById(id));
    }

    @PostMapping
    public Result add(@RequestBody AutoPolicy autoPolicy) {
        autoPolicyMapper.insert(autoPolicy);
        return Result.success();
    }

    @PutMapping
    public Result update(@RequestBody AutoPolicy autoPolicy) {
        autoPolicyMapper.update(autoPolicy);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        autoPolicyMapper.deleteById(id);
        return Result.success();
    }
}
```

**客户门户 Controller（核心业务逻辑）：**

```java
package com.hjb.nice.server.controller.portal;

import com.hjb.nice.entity.*;
import com.hjb.nice.result.Result;
import com.hjb.nice.server.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/portal")
public class CustomerPortalController {

    @Autowired private CustomerAccountMapper accountMapper;
    @Autowired private CustomerMapper customerMapper;
    @Autowired private AutoPolicyMapper autoPolicyMapper;
    @Autowired private HomePolicyMapper homePolicyMapper;
    @Autowired private InvoiceMapper invoiceMapper;
    @Autowired private PaymentMapper paymentMapper;

    /** 从 Spring Security 上下文获取当前登录用户名 */
    private String currentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    /** 获取当前登录的账号对象 */
    private CustomerAccount currentAccount() {
        return accountMapper.findByUsername(currentUsername());
    }

    /** 购险核心逻辑 */
    @PostMapping("/purchase")
    public Result purchase(@RequestBody PurchaseRequest req) {
        CustomerAccount account = currentAccount();
        LocalDate today = LocalDate.now();

        // 如果还没有 customer 记录，在这里创建
        if (account.getCustomerId() == null) {
            Customer customer = new Customer();
            customer.setFname(account.getFname());
            customer.setLname(account.getLname());
            // ...复制其他字段
            customer.setCustType(req.getType().equals("AUTO") ? "A" : "H");
            customerMapper.insertForRegister(customer);
            accountMapper.updateCustomerId(account.getAccountId(), customer.getCustId());
            account.setCustomerId(customer.getCustId());
        }

        Integer custId = account.getCustomerId();

        // 创建保单（有效期1年）
        if ("AUTO".equals(req.getType())) {
            AutoPolicy policy = new AutoPolicy();
            policy.setSdate(today);
            policy.setEdate(today.plusYears(1));
            policy.setAmount(req.getAmount());
            policy.setStatus("C");
            policy.setHjbCustomerCustId(custId);
            autoPolicyMapper.insertAutoId(policy);

            // 生成账单
            Invoice invoice = new Invoice();
            invoice.setIDate(today);
            invoice.setDue(today.plusDays(30));
            invoice.setAmount(req.getAmount());
            invoice.setHjbAutopolicyApId(policy.getApId()); // 回填的自增ID
            invoiceMapper.insertAutoId(invoice);
        }
        // HOME 类型类似，省略...

        // 更新客户类型（A/H/B）
        updateCustType(custId);

        return Result.success();
    }

    /** 根据当前保单情况更新 cust_type */
    private void updateCustType(Integer custId) {
        boolean hasAuto = !autoPolicyMapper.findByCustomerId(custId).isEmpty();
        boolean hasHome = !homePolicyMapper.findByCustomerId(custId).isEmpty();
        String type = (hasAuto && hasHome) ? "B" : (hasAuto ? "A" : "H");
        customerMapper.updateCustType(custId, type);
    }

    // DTO：购险请求体
    record PurchaseRequest(String type, BigDecimal amount) {}
}
```

---

## 8. 前端开发

### 8.1 创建 Vue 3 项目

```bash
npm create vue@latest hjb-frontend-vue
cd hjb-frontend-vue

# 安装选项（推荐）：
# ✔ Add Vue Router? → Yes
# ✔ Add ESLint? → Yes
# 其余 No

npm install

# 额外安装依赖
npm install element-plus axios
```

配置 Element Plus 按需自动导入（可选，全量导入也可以）：

```js
// main.js
import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'

const app = createApp(App)
app.use(ElementPlus)
app.use(router)
app.mount('#app')
```

### 8.2 Axios 封装

**src/api/request.js** — 创建 Axios 实例，统一处理请求头和响应：

```js
import axios from 'axios'
import router from '../router'

const request = axios.create({
  baseURL: 'http://localhost:8080/api',  // 后端地址
  timeout: 5000
})

// 请求拦截器：每个请求自动带上 JWT token
request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers['Authorization'] = `Bearer ${token}`
  }
  return config
})

// 响应拦截器：统一处理响应
request.interceptors.response.use(
  response => response.data,  // 只返回 data 部分，不需要每次写 res.data
  error => {
    if (error.response?.status === 401) {
      // Token 过期或无效，自动跳转登录
      localStorage.removeItem('token')
      localStorage.removeItem('username')
      localStorage.removeItem('role')
      router.push('/login')
    }
    return Promise.reject(error)
  }
)

export default request
```

**src/api/index.js** — 按业务模块组织 API：

```js
import request from './request'

// 每个模块导出一个对象，包含该模块所有 API 方法
export const customerApi = {
  findAll:  ()     => request.get('/customers'),
  findById: (id)   => request.get(`/customers/${id}`),
  add:      (data) => request.post('/customers', data),
  update:   (data) => request.put('/customers', data),
  delete:   (id)   => request.delete(`/customers/${id}`)
}

export const autoPolicyApi = {
  findAll:          ()       => request.get('/auto-policies'),
  findByCustomerId: (custId) => request.get(`/auto-policies/customer/${custId}`),
  add:              (data)   => request.post('/auto-policies', data),
  update:           (data)   => request.put('/auto-policies', data),
  delete:           (id)     => request.delete(`/auto-policies/${id}`)
}

export const portalApi = {
  myProfile:      () => request.get('/portal/my-profile'),
  myAutoPolicies: () => request.get('/portal/my-policies/auto'),
  myHomePolicies: () => request.get('/portal/my-policies/home'),
  myInvoices:     () => request.get('/portal/my-invoices'),
  myPayments:     () => request.get('/portal/my-payments'),
  makePayment:    (data) => request.post('/portal/payments', data),
  purchasePolicy: (data) => request.post('/portal/purchase', data),
}

export const planApi = {
  findActive: () => request.get('/plans'),            // 公开接口
  findAll:    () => request.get('/admin/plans'),      // 管理员接口
  add:        (data) => request.post('/admin/plans', data),
  update:     (data) => request.put('/admin/plans', data),
  delete:     (id)   => request.delete(`/admin/plans/${id}`)
}

// ... 其他模块类似
```

### 8.3 Vue Router 配置

**src/router/index.js：**

```js
import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    // 公开页面（无需登录）
    {
      path: '/',
      component: () => import('../views/LandingPage.vue'),
      meta: { layout: 'public', public: true }
    },
    {
      path: '/customer-login',
      component: () => import('../views/CustomerAuthView.vue'),
      meta: { layout: 'public', public: true }
    },
    {
      path: '/login',
      component: () => import('../views/LoginView.vue'),
      meta: { layout: 'public', public: true }
    },

    // 客户门户（需要 CUSTOMER 角色）
    {
      path: '/portal',
      component: () => import('../views/CustomerPortalView.vue'),
      meta: { layout: 'portal', role: 'CUSTOMER' }
    },

    // 管理后台（需要 EMPLOYEE 角色）
    { path: '/customers',      component: () => import('../views/CustomerView.vue') },
    { path: '/auto-policies',  component: () => import('../views/AutoPolicyView.vue') },
    { path: '/home-policies',  component: () => import('../views/HomePolicyView.vue') },
    { path: '/invoices',       component: () => import('../views/InvoiceView.vue') },
    { path: '/payments',       component: () => import('../views/PaymentView.vue') },
    { path: '/vehicles',       component: () => import('../views/VehicleView.vue') },
    { path: '/drivers',        component: () => import('../views/DriverView.vue') },
    { path: '/homes',          component: () => import('../views/HomeView.vue') },
    { path: '/vehicle-drivers',component: () => import('../views/VehicleDriverView.vue') },
    { path: '/employees',      component: () => import('../views/EmployeeView.vue') },
    { path: '/plans',          component: () => import('../views/PlanView.vue') },
  ]
})

// 路由守卫：访问控制
router.beforeEach((to) => {
  const token = localStorage.getItem('token')
  const role = localStorage.getItem('role')

  // 公开页面，直接放行
  if (to.meta.public) return true

  // 未登录，跳转登录
  if (!token) return '/login'

  // 客户访问了管理后台，重定向到门户
  if (role === 'CUSTOMER' && to.meta.layout !== 'portal') return '/portal'

  // 员工访问了客户门户，重定向到管理后台
  if (role === 'EMPLOYEE' && to.meta.role === 'CUSTOMER') return '/customers'
})

export default router
```

### 8.4 App.vue 布局系统

整个应用有三种布局，通过路由的 `meta.layout` 字段切换：

```vue
<template>
  <!-- 公开布局：只渲染页面组件本身 -->
  <router-view v-if="layout === 'public'" />

  <!-- 客户门户布局：顶部导航 + 内容区 -->
  <div v-else-if="layout === 'portal'" class="portal-layout">
    <header class="portal-header">
      <div class="portal-logo">HJB Insurance</div>
      <div>
        <span>{{ username }}</span>
        <el-button size="small" @click="logout">Logout</el-button>
      </div>
    </header>
    <main class="portal-main">
      <router-view />
    </main>
  </div>

  <!-- 管理后台布局：左侧固定侧边栏 + 右侧内容区 -->
  <div v-else class="app-layout">
    <aside class="sidebar">
      <!-- Logo -->
      <div class="logo">HJB Insurance</div>

      <!-- 导航菜单 -->
      <nav class="nav-menu">
        <router-link
          v-for="item in menuItems"
          :key="item.path"
          :to="item.path"
          class="nav-item"
          active-class="nav-item--active"
        >
          <span>{{ item.icon }}</span>
          <span>{{ item.label }}</span>
        </router-link>
      </nav>

      <!-- 底部：用户名 + 登出 -->
      <div class="sidebar-footer">
        <span>{{ username }}</span>
        <button @click="logout">Logout</button>
      </div>
    </aside>

    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

// 根据路由 meta 决定使用哪种布局
const layout = computed(() => route.meta.layout || 'admin')

// 响应式读取 localStorage 中的用户名（路由切换时刷新）
const username = ref(localStorage.getItem('username') || '')
watch(route, () => {
  username.value = localStorage.getItem('username') || ''
})

const menuItems = [
  { path: '/customers',      label: 'Customers',       icon: '👥' },
  { path: '/auto-policies',  label: 'Auto Policies',   icon: '🚗' },
  { path: '/home-policies',  label: 'Home Policies',   icon: '🏠' },
  { path: '/vehicles',       label: 'Vehicles',        icon: '🚙' },
  { path: '/drivers',        label: 'Drivers',         icon: '🪪' },
  { path: '/homes',          label: 'Homes',           icon: '🏡' },
  { path: '/invoices',       label: 'Invoices',        icon: '📄' },
  { path: '/payments',       label: 'Payments',        icon: '💳' },
  { path: '/vehicle-drivers',label: 'Vehicle-Drivers', icon: '🔗' },
  { path: '/employees',      label: 'Employees',       icon: '👔' },
  { path: '/plans',          label: 'Insurance Plans', icon: '📋' },
]

function logout() {
  localStorage.removeItem('token')
  localStorage.removeItem('username')
  localStorage.removeItem('role')
  router.push('/')
}
</script>
```

### 8.5 登录页面

```vue
<!-- src/views/LoginView.vue（员工登录） -->
<template>
  <div class="login-page">
    <div class="login-card">
      <h2>Employee Login</h2>
      <el-form :model="form" label-position="top">
        <el-form-item label="Username">
          <el-input v-model="form.username" />
        </el-form-item>
        <el-form-item label="Password">
          <el-input v-model="form.password" type="password" show-password />
        </el-form-item>
        <div v-if="error" style="color:red;margin-bottom:8px">{{ error }}</div>
        <el-button type="primary" style="width:100%" :loading="loading" @click="handleLogin">
          Sign In
        </el-button>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import request from '../api/request.js'

const router = useRouter()
const form = ref({ username: '', password: '' })
const error = ref('')
const loading = ref(false)

async function handleLogin() {
  error.value = ''
  loading.value = true
  try {
    // 调用员工登录接口
    const res = await request.post('/auth/employee/login', form.value)
    if (res.code === 1) {
      // 存储 token 和用户信息到 localStorage
      localStorage.setItem('token', res.data.token)
      localStorage.setItem('username', res.data.username)
      localStorage.setItem('role', 'EMPLOYEE')
      router.push('/customers')  // 跳转到管理后台首页
    } else {
      error.value = res.msg || 'Login failed'
    }
  } catch {
    error.value = 'Invalid username or password'
  } finally {
    loading.value = false
  }
}
</script>
```

### 8.6 标准管理页面（CRUD 模板）

以下是所有管理页面都遵循的模板模式，以 CustomerView 为例：

```vue
<template>
  <div class="page">
    <!-- 页面头部：标题 + 新增按钮 -->
    <div class="page-header">
      <h1 class="page-title">Customers</h1>
      <el-button type="primary" @click="openAdd">+ Add Customer</el-button>
    </div>

    <!-- 搜索框 -->
    <el-input v-model="search" placeholder="Search..." clearable style="width:300px;margin-bottom:16px">
      <template #prefix><el-icon><Search /></el-icon></template>
    </el-input>

    <!-- 数据表格 -->
    <el-table :data="filteredData" stripe v-loading="loading">
      <el-table-column prop="custId" label="ID" width="80" />
      <el-table-column prop="fname" label="First Name" />
      <el-table-column prop="lname" label="Last Name" />
      <el-table-column prop="custType" label="Type" width="80">
        <template #default="{ row }">
          <el-tag size="small">{{ row.custType }}</el-tag>
        </template>
      </el-table-column>
      <!-- 操作列 -->
      <el-table-column label="Actions" width="160" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">Edit</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.custId)">Delete</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? 'Edit Customer' : 'Add Customer'" width="500">
      <el-form :model="form" label-width="120px">
        <el-form-item label="First Name">
          <el-input v-model="form.fname" />
        </el-form-item>
        <el-form-item label="Last Name">
          <el-input v-model="form.lname" />
        </el-form-item>
        <!-- 其他字段... -->
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="handleSubmit">
          {{ isEdit ? 'Update' : 'Add' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { customerApi } from '../api'

const tableData = ref([])        // 表格全量数据
const loading = ref(false)       // 加载状态
const dialogVisible = ref(false) // 对话框显示状态
const isEdit = ref(false)        // 是新增还是编辑
const search = ref('')           // 搜索关键词

// 前端过滤（实时搜索）
const filteredData = computed(() => {
  const q = search.value.toLowerCase()
  if (!q) return tableData.value
  return tableData.value.filter(r =>
    (r.fname + ' ' + r.lname).toLowerCase().includes(q)
  )
})

// 表单默认值
const defaultForm = { custId: null, fname: '', lname: '', gender: 'M', custType: 'A' }
const form = ref({ ...defaultForm })

// 加载数据
const loadData = async () => {
  loading.value = true
  const res = await customerApi.findAll()
  tableData.value = res.data
  loading.value = false
}

// 打开新增对话框
const openAdd = () => {
  isEdit.value = false
  form.value = { ...defaultForm }
  dialogVisible.value = true
}

// 打开编辑对话框（传入当前行数据）
const openEdit = (row) => {
  isEdit.value = true
  form.value = { ...row }  // 浅拷贝，防止直接修改表格数据
  dialogVisible.value = true
}

// 提交（新增或更新）
const handleSubmit = async () => {
  if (isEdit.value) {
    await customerApi.update(form.value)
    ElMessage.success('Updated successfully')
  } else {
    await customerApi.add(form.value)
    ElMessage.success('Added successfully')
  }
  dialogVisible.value = false
  loadData()  // 刷新表格
}

// 删除（二次确认）
const handleDelete = async (id) => {
  await ElMessageBox.confirm('Are you sure to delete this record?', 'Warning', { type: 'warning' })
  await customerApi.delete(id)
  ElMessage.success('Deleted')
  loadData()
}

onMounted(loadData)  // 组件挂载时自动加载数据
</script>
```

---

## 9. 前后端联调

### 9.1 启动步骤

**第一步：启动 MySQL**（确保服务正在运行）

**第二步：启动后端**
```bash
cd hjb-insurance
mvn spring-boot:run
# 或在 IntelliJ IDEA 中直接运行 HjbApplication.java
```
看到 `Started HjbApplication in x.xxx seconds` 表示成功。

**第三步：启动前端**
```bash
cd hjb-frontend-vue
npm run dev
# 浏览器打开 http://localhost:5173
```

### 9.2 联调验证清单

| 功能 | 验证方法 |
|------|----------|
| 员工登录 | 访问 `/login`，输入 admin/Admin@123 |
| 查看客户列表 | 登录后访问 `/customers`，应有数据 |
| 客户注册 | 访问 `/customer-login` 注册新账号 |
| 客户购险 | 登录客户账号 → Get Insured → 选套餐 |
| 账单支付 | 在 Invoices 标签页点 Pay Now |
| 套餐管理 | 管理员访问 `/plans`，增删改套餐 |

### 9.3 前后端数据流追踪

以"客户登录"为例，追踪完整数据流：

```
① 用户点击 Sign In 按钮
   ↓
② Vue 调用 request.post('/auth/customer/login', {username, password})
   ↓
③ Axios 发送 POST http://localhost:8080/api/auth/customer/login
   请求体：{"username":"tom","password":"123456"}
   ↓
④ JwtAuthFilter 检查：该接口在 permitAll 白名单，直接放行
   ↓
⑤ AuthController.customerLogin() 接收请求
   ↓
⑥ AuthServiceImpl.customerLogin()：
   - 查 customer_account 表
   - BCrypt.matches(rawPwd, storedHash) 验证密码
   - JwtUtil.generateToken("tom", "CUSTOMER") 生成 token
   ↓
⑦ 返回 Result：{"code":1,"msg":"success","data":{"token":"eyJ...","role":"CUSTOMER","username":"tom"}}
   ↓
⑧ 响应拦截器 → 返回 res.data → Vue 代码得到 {code:1, data:{token,role,username}}
   ↓
⑨ Vue 存入 localStorage：token, username="tom", role="CUSTOMER"
   ↓
⑩ router.push('/portal') 跳转到客户门户
```

---

## 10. 功能演示与测试

### 10.1 访问地址

| 页面 | 地址 |
|------|------|
| 公司官网 | http://localhost:5173/ |
| 客户注册/登录 | http://localhost:5173/customer-login |
| 客户门户 | http://localhost:5173/portal |
| 员工登录 | http://localhost:5173/login |
| 管理后台 | http://localhost:5173/customers |
| API 文档 | http://localhost:8080/swagger-ui/index.html |

### 10.2 默认测试账号

| 角色 | 用户名 | 密码 | 登录页面 |
|------|--------|------|----------|
| 管理员 | admin | Admin@123 | /login |
| 客户 | 自行注册 | 自定义 | /customer-login |

### 10.3 使用 Swagger 测试 API

访问 http://localhost:8080/swagger-ui/index.html，可以在浏览器中直接测试每个接口：

1. 先调用 `/api/auth/employee/login` 获取 token
2. 点击右上角 **Authorize** 按钮
3. 输入 `Bearer <你的token>`
4. 之后就可以测试需要认证的接口

---

## 11. 常见问题与解决方案

### Q1：后端启动报错 "Access denied for user 'root'@'localhost'"

**原因：** `application.yml` 中的 MySQL 密码不正确

**解决：** 打开 `hjb-server/src/main/resources/application.yml`，将 `password` 改为你安装 MySQL 时设置的 root 密码

---

### Q2：前端访问接口报 "Network Error" 或 CORS 错误

**原因可能有两个：**

1. 后端没有启动
   - 验证：浏览器访问 `http://localhost:8080/api/plans`，看是否有响应

2. 前端地址使用了 `127.0.0.1` 而不是 `localhost`
   - Spring Security 的 CORS 配置只允许 `http://localhost:*`
   - 解决：将浏览器地址栏中的 `127.0.0.1` 改成 `localhost`

---

### Q3：登录成功但进入页面后立即跳回登录页（401）

**原因：** JWT token 过期，或者 request.js 中没有正确附加 Authorization 请求头

**排查：**
1. 打开浏览器开发者工具（F12）→ Network 标签
2. 找到报 401 的请求
3. 检查 Request Headers 中是否有 `Authorization: Bearer eyJ...`
4. 如果没有，检查 `src/api/request.js` 中的请求拦截器

---

### Q4：表格数据显示不出来（字段为空或 undefined）

**原因：** 前端字段名和后端返回的 JSON 字段名不匹配

**常见场景：**
- 后端实体有 `@JsonProperty("iId")` → JSON 返回 `"iId"`
- 前端表格 `prop="iid"`（小写）→ 匹配不到

**解决：** 检查后端实际返回的 JSON（在 Network 标签中查看 Response），确保前端 `prop` 值与 JSON 键名完全一致（区分大小写）

---

### Q5：购险时报 400 错误 "column 'xxx' cannot be null"

**原因：** `schema_update_v2.sql` 未执行，`customer_account` 表缺少个人信息字段

**解决：** 在 MySQL 中执行 `sql/schema_update_v2.sql`

---

### Q6：保险套餐页面为空

**原因：** `schema_update_v3.sql` 未执行，`hjb_plan` 表不存在

**解决：** 在 MySQL 中执行 `sql/schema_update_v3.sql`，此脚本会建表并插入 6 条初始套餐数据

---

### Q7：`mvn spring-boot:run` 提示 Java 版本错误

**原因：** 系统默认 Java 不是 17

**解决：**
```bash
java -version   # 查看当前版本
# 如果不是 17，检查 JAVA_HOME 环境变量是否正确设置
```

---

### Q8：前端页面刷新后 404（部署到 nginx 时）

**原因：** Vue Router 使用 HTML5 History 模式，刷新时 nginx 找不到对应的 HTML 文件

**解决：** 在 nginx.conf 中加入：
```nginx
location / {
    try_files $uri $uri/ /index.html;  # 所有路径都返回 index.html
}
```

---

## 12. 附录

### 12.1 项目完整文件结构

```
hjb-insurance/
├── pom.xml
├── hjb-common/
│   └── src/main/java/com/hjb/nice/result/Result.java
├── hjb-pojo/
│   ├── pom.xml
│   └── src/main/java/com/hjb/nice/
│       ├── entity/
│       │   ├── AutoPolicy.java
│       │   ├── Customer.java
│       │   ├── CustomerAccount.java
│       │   ├── Driver.java
│       │   ├── Employee.java
│       │   ├── Home.java
│       │   ├── HomePolicy.java
│       │   ├── Invoice.java       ← 含 @JsonProperty
│       │   ├── Payment.java       ← 含 @JsonProperty
│       │   ├── Plan.java
│       │   ├── Vehicle.java
│       │   └── VehicleDriver.java
│       └── dto/
│           ├── LoginRequest.java
│           ├── LoginResponse.java
│           ├── CustomerRegisterRequest.java
│           └── ResetPasswordRequest.java
├── hjb-server/
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/hjb/nice/server/
│       │   ├── HjbApplication.java           ← @SpringBootApplication @EnableScheduling
│       │   ├── config/
│       │   │   ├── SecurityConfig.java        ← JWT + CORS + 权限配置
│       │   │   ├── GlobalExceptionHandler.java
│       │   │   └── PolicyScheduler.java       ← 定时到期任务
│       │   ├── filter/
│       │   │   └── JwtAuthFilter.java         ← JWT 验证过滤器
│       │   ├── utils/
│       │   │   └── JwtUtil.java               ← JWT 工具类
│       │   ├── mapper/                        ← MyBatis Mapper 接口（12个）
│       │   ├── service/
│       │   │   ├── AuthService.java
│       │   │   └── impl/AuthServiceImpl.java
│       │   └── controller/
│       │       ├── auth/AuthController.java   ← 登录/注册
│       │       ├── portal/CustomerPortalController.java ← 客户门户
│       │       ├── admin/AdminController.java ← 员工管理
│       │       ├── PlanController.java
│       │       ├── CustomerController.java
│       │       ├── AutoPolicyController.java
│       │       ├── HomePolicyController.java
│       │       ├── InvoiceController.java
│       │       ├── PaymentController.java
│       │       ├── VehicleController.java
│       │       ├── DriverController.java
│       │       ├── HomeController.java
│       │       └── VehicleDriverController.java
│       └── resources/
│           └── application.yml
└── sql/
    ├── schema_update.sql      ← 基础表结构
    ├── schema_update_v2.sql   ← customer_account 字段扩展
    └── schema_update_v3.sql   ← hjb_plan 套餐表

hjb-frontend-vue/
├── package.json
├── vite.config.js
└── src/
    ├── main.js                ← 应用入口，注册 ElementPlus
    ├── App.vue                ← 布局系统（public/portal/admin）
    ├── api/
    │   ├── request.js         ← Axios 实例 + 拦截器
    │   └── index.js           ← 各业务模块 API
    ├── router/
    │   └── index.js           ← 路由配置 + 守卫
    └── views/
        ├── LandingPage.vue         ← 公司官网
        ├── LoginView.vue           ← 员工登录
        ├── CustomerAuthView.vue    ← 客户登录/注册/找回密码
        ├── CustomerPortalView.vue  ← 客户门户（我的保单/账单/支付）
        ├── CustomerView.vue        ← 客户管理（CRUD）
        ├── AutoPolicyView.vue      ← 车险保单管理
        ├── HomePolicyView.vue      ← 房险保单管理
        ├── InvoiceView.vue         ← 账单管理
        ├── PaymentView.vue         ← 支付管理
        ├── VehicleView.vue         ← 车辆管理
        ├── DriverView.vue          ← 驾驶员管理
        ├── HomeView.vue            ← 房产管理
        ├── VehicleDriverView.vue   ← 车辆-驾驶员关联
        ├── EmployeeView.vue        ← 员工管理
        └── PlanView.vue            ← 保险套餐管理
```

### 12.2 Spring Boot 核心注解速查

| 注解 | 位置 | 作用 |
|------|------|------|
| `@SpringBootApplication` | 启动类 | 开启自动配置、组件扫描 |
| `@EnableScheduling` | 启动类 | 开启定时任务 |
| `@RestController` | Controller类 | 标记为REST控制器，方法返回值自动序列化为JSON |
| `@RequestMapping` | Controller类/方法 | 映射URL路径 |
| `@GetMapping` `@PostMapping` `@PutMapping` `@DeleteMapping` | 方法 | HTTP方法快捷注解 |
| `@PathVariable` | 参数 | 获取URL路径中的变量 |
| `@RequestBody` | 参数 | 将请求体JSON反序列化为Java对象 |
| `@Mapper` | Mapper接口 | 标记为MyBatis Mapper |
| `@Select` `@Insert` `@Update` `@Delete` | Mapper方法 | 写SQL语句 |
| `@Options` | Mapper方法 | 配置useGeneratedKeys回填自增ID |
| `@Param` | Mapper方法参数 | 多参数时指定参数名 |
| `@Service` | Service实现类 | 标记为Spring服务组件 |
| `@Component` | 普通类 | 标记为Spring组件 |
| `@Autowired` | 字段/构造器 | 依赖注入 |
| `@Configuration` | 配置类 | 标记为配置类 |
| `@Bean` | 配置类中的方法 | 将方法返回值注册为Spring Bean |
| `@Value` | 字段 | 从配置文件注入值 |
| `@Transactional` | Service方法 | 开启数据库事务 |
| `@Scheduled` | 方法 | 定时执行 |
| `@Data` | 类（Lombok） | 自动生成getter/setter/toString等 |
| `@RestControllerAdvice` | 类 | 全局异常处理器 |
| `@ExceptionHandler` | 方法 | 处理特定类型的异常 |

### 12.3 Vue 3 核心概念速查

| 概念/API | 说明 | 示例 |
|---------|------|------|
| `ref(value)` | 创建响应式基本类型，访问时用 `.value` | `const count = ref(0)` |
| `computed(fn)` | 派生响应式值，自动追踪依赖 | `const total = computed(() => list.value.length)` |
| `watch(source, cb)` | 监听响应式值变化 | `watch(route, () => reload())` |
| `onMounted(fn)` | 组件挂载后执行（相当于页面加载） | `onMounted(() => loadData())` |
| `v-model` | 双向绑定，表单输入常用 | `<el-input v-model="name" />` |
| `v-for` | 列表渲染 | `<div v-for="item in list" :key="item.id">` |
| `v-if/v-else` | 条件渲染 | `<div v-if="show">` |
| `v-loading` | Element Plus 加载遮罩 | `<el-table v-loading="loading">` |
| `@click` | 绑定点击事件 | `<button @click="handleClick">` |
| `:prop` | 绑定动态属性 | `<el-tag :type="active ? 'success' : 'info'">` |
| `$router.push` | 编程式导航 | `router.push('/portal')` |
| `useRoute()` | 获取当前路由信息 | `const route = useRoute()` |
| `useRouter()` | 获取路由器实例 | `const router = useRouter()` |

### 12.4 MyBatis 下划线转驼峰规则

配置了 `map-underscore-to-camel-case: true` 后，MyBatis 自动转换：

| 数据库字段 | Java 字段 |
|-----------|-----------|
| `cust_id` | `custId` |
| `ap_id` | `apId` |
| `i_date` | `iDate` |
| `hjb_customer_cust_id` | `hjbCustomerCustId` |
| `pay_amount` | `payAmount` |

**注意：** 当 Java 字段前两个字母都大写时（如 `iId`），Jackson 的序列化会出问题，需要加 `@JsonProperty("iId")` 注解强制指定 JSON 键名。

### 12.5 延伸学习路径

完成本项目后，推荐继续学习：

1. **数据库优化**：索引原理、慢查询分析、连接池配置（HikariCP）
2. **前端路由进阶**：动态路由、懒加载、导航守卫
3. **接口规范**：RESTful API 设计规范、分页查询实现
4. **项目部署**：Docker 容器化、nginx 配置、云服务器部署（阿里云/腾讯云）
5. **安全加固**：HTTPS、SQL 注入防护（MyBatis 参数化查询）、XSS 防护
6. **测试**：JUnit 单元测试、Spring Boot Test 集成测试

---

## 13. Service 层重构与事务管理

> 本章讲解为什么需要 Service 层，以及 `@Transactional` 在真实项目中解决了什么问题。

### 13.1 最初的问题：Controller 直接操作 Mapper

项目初期，`CustomerPortalController` 直接注入了六个 Mapper：

```java
// ❌ 反模式：Controller 直接操控数据库
@Autowired private CustomerAccountMapper customerAccountMapper;
@Autowired private CustomerMapper customerMapper;
@Autowired private AutoPolicyMapper autoPolicyMapper;
@Autowired private HomePolicyMapper homePolicyMapper;
@Autowired private InvoiceMapper invoiceMapper;
@Autowired private PaymentMapper paymentMapper;
```

这带来两个严重问题：

**问题 1：事务位置错误**

`purchasePolicy` 方法需要同时操作四张表（创建客户 → 创建保单 → 创建账单 → 更新客户类型），要么全部成功，要么全部回滚。代码里虽然加了 `@Transactional`，但加在了 Controller 方法上：

```java
// ❌ @Transactional 加在 Controller 上是反模式
@PostMapping("/purchase")
@Transactional   // ← 放错了地方
public Result<Void> purchasePolicy(...) { ... }
```

Spring 的 `@Transactional` 通过 **AOP 代理**实现：Spring 在调用你的方法前后插入事务开启/提交/回滚的逻辑。这个代理只对被 Spring 容器管理的 **Bean 的外部调用**生效。Controller 虽然也是 Bean，但事务最佳实践要求事务边界在 Service 层，原因：
- Controller 负责处理 HTTP，不应承担业务逻辑
- Service 方法可以被多个 Controller 复用，事务跟着走
- 更容易写单元测试（可以单独测 Service 而不需要启动 HTTP）

**问题 2：makePayment 完全没有事务**

支付方法先查询已付金额，再插入支付记录，这两步之间如果同时有两个请求进来：

```
请求A: 查询已付 = $0  →  (还没插入)
请求B: 查询已付 = $0  →  (还没插入)
请求A: 插入支付 $1200  ✓
请求B: 插入支付 $1200  ✓   ← 本应被拒绝！账单被多付了！
```

没有事务保护，这就是经典的**并发重复支付**漏洞。

**问题 3：安全漏洞**

原始 `makePayment` 只验证账单是否存在，没有验证账单是否属于当前用户：

```java
// ❌ 任何登录用户都能通过猜测 invoiceId 来操作别人的账单
Invoice invoice = invoiceMapper.findById(req.getInvoiceId());
if (invoice == null) throw new RuntimeException("不存在");
// 直接付款，没有检查这张账单是不是这个用户的！
```

---

### 13.2 重构方案：下沉到 Service 层

**架构变化：**

```
重构前：
Controller → Mapper（直接操数据库，事务混乱）

重构后：
Controller → Service（含 @Transactional） → Mapper
```

**新增文件：**
- `service/CustomerPortalService.java`（接口）
- `service/impl/CustomerPortalServiceImpl.java`（实现）

**Controller 瘦身后的样子：**

```java
@RestController
@RequestMapping("/api/portal")
public class CustomerPortalController {

    @Autowired
    private CustomerPortalService portalService;  // 只依赖 Service

    private String currentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @PostMapping("/purchase")
    public Result<Void> purchasePolicy(@RequestBody PurchaseRequest req) {
        portalService.purchasePolicy(currentUsername(), req);  // 一行调用
        return Result.success();
    }

    @PostMapping("/payments")
    public Result<Void> makePayment(@RequestBody PaymentRequest req) {
        portalService.makePayment(currentUsername(), req);
        return Result.success();
    }
}
```

Controller 现在只做两件事：拿到当前用户名、调用 Service。不再有任何业务逻辑。

---

### 13.3 @Transactional 的正确用法

**purchasePolicy：四步操作原子化**

```java
@Service
public class CustomerPortalServiceImpl implements CustomerPortalService {

    @Override
    @Transactional   // ← 正确：加在 Service 方法上
    public void purchasePolicy(String username, PurchaseRequest req) {
        // 步骤1：首次购险时创建 hjb_customer 记录
        // 步骤2：创建 AutoPolicy 或 HomePolicy
        // 步骤3：创建 Invoice
        // 步骤4：更新客户类型（A/H/B）
        // 任何一步抛出异常 → 整个方法的所有数据库操作全部回滚
    }
}
```

`@Transactional` 的工作原理：
1. 方法开始前：Spring 开启一个数据库事务（`BEGIN`）
2. 方法执行中：所有 Mapper 操作都在这个事务里
3. 方法正常结束：自动提交（`COMMIT`）
4. 方法抛出 RuntimeException：自动回滚（`ROLLBACK`）

**makePayment：加事务 + 三重校验**

```java
@Override
@Transactional
public void makePayment(String username, PaymentRequest req) {
    // 校验1：金额必须大于0
    if (req.getPayAmount().compareTo(BigDecimal.ZERO) <= 0) {
        throw new RuntimeException("支付金额必须大于 0");
    }

    Invoice invoice = invoiceMapper.findById(req.getInvoiceId());
    if (invoice == null) throw new RuntimeException("Invoice 不存在");

    // 校验2：账单必须属于当前用户（安全校验）
    if (!invoiceBelongsToUser(invoice, username)) {
        throw new RuntimeException("无权操作该账单");
    }

    // 校验3：不能超额支付
    BigDecimal paid = paymentMapper.findByInvoiceId(req.getInvoiceId()).stream()
            .map(Payment::getPayAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    if (paid.compareTo(invoice.getAmount()) >= 0) {
        throw new RuntimeException("该账单已全额支付");
    }
    BigDecimal remaining = invoice.getAmount().subtract(paid);
    if (req.getPayAmount().compareTo(remaining) > 0) {
        throw new RuntimeException("支付金额不能超过剩余应付金额 " + remaining);
    }

    // 通过所有校验后才插入支付记录
    Payment payment = new Payment();
    payment.setHjbInvoiceIId(req.getInvoiceId());
    payment.setMethod(req.getMethod());
    payment.setPayAmount(req.getPayAmount());
    payment.setPayDate(LocalDate.now());
    paymentMapper.insertAutoId(payment);
}
```

---

### 13.4 关键收获

| 概念 | 本项目中的体现 |
|------|--------------|
| 分层架构 | Controller → Service → Mapper，职责分离 |
| `@Transactional` | purchasePolicy 四步原子操作，makePayment 防并发 |
| 权限校验 | invoiceBelongsToUser 防止越权访问他人数据 |
| 输入校验 | 金额 > 0、类型必须为 AUTO/HOME、不能超额支付 |

---

## 14. 生产环境部署（Railway + Vercel）

> 本章讲解如何将项目免费部署到互联网，使用 Railway（后端+数据库）和 Vercel（前端）。

### 14.1 整体架构

```
用户浏览器
    │
    ├──→ Vercel（前端静态文件，全球 CDN）
    │       ↓ API 请求
    └──→ Railway（Spring Boot 后端）
                ↓ JDBC
            Railway MySQL（数据库）
```

### 14.2 后端部署到 Railway

**前提：** 代码已推送到 GitHub。

**步骤：**

1. 注册并登录 [railway.app](https://railway.app)
2. New Project → Deploy from GitHub repo → 选择后端仓库
3. 添加 MySQL 服务：New → Database → MySQL
4. 在后端服务的 **Variables** 标签里添加环境变量：

   | Key | Value 来源 |
   |-----|-----------|
   | `DB_HOST` | MySQL 服务 → Connect → `MYSQLHOST`（内网地址） |
   | `DB_USER` | MySQL 服务 → Connect → `MYSQLUSER` |
   | `DB_PASS` | MySQL 服务 → Connect → `MYSQLPASSWORD` |

5. Settings → Build → Builder 改为 **Dockerfile**
6. Settings → Deploy → Start Command 改为 `java -jar app.jar`
7. Settings → Networking → Generate Domain，得到后端域名

**为什么需要 Dockerfile？**

Railway 默认使用 Railpack 构建，它在 Maven 多模块项目上支持不完善。自己写 Dockerfile 可以精确控制构建过程：

```dockerfile
# 阶段1：构建（使用包含 Maven 的完整镜像）
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN cd hjb-insurance && mvn clean package -DskipTests

# 阶段2：运行（只用轻量 JRE，不需要 Maven）
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/hjb-insurance/hjb-server/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

> **多阶段构建的意义：** 构建阶段的 Maven、源码、class 文件都不会打进最终镜像，最终镜像只有 JRE + JAR，体积从 ~600MB 降到 ~200MB。

**导入数据库：**

Railway MySQL 初始是空的，需要用 MySQL Workbench 连接后导入 SQL 文件：
- Host/Port/User/Password：从 MySQL 服务 Connect 标签的公网 URL 读取
- 导入：Server → Data Import → Import from Self-Contained File → 选择 `nice.sql`

---

### 14.3 前端部署到 Vercel

**步骤：**

1. 注册并登录 [vercel.com](https://vercel.com)
2. New Project → 导入前端 GitHub 仓库
3. Framework Preset 选 **Vite**，Build Command `npm run build`，Output Directory `dist`
4. Deploy

**配置后端地址：**

前端的 API 请求地址在 `src/api/request.js`：

```js
const request = axios.create({
  baseURL: 'https://你的后端域名.up.railway.app/api',
  timeout: 5000
})
```

将 `你的后端域名` 替换为 Railway 生成的域名，commit + push 后 Vercel 自动重新部署。

**解决刷新 404 问题：**

Vue Router 使用 HTML5 History 模式，刷新时 Vercel 会找不到对应的文件。在前端根目录添加 `vercel.json`：

```json
{
  "rewrites": [
    { "source": "/(.*)", "destination": "/index.html" }
  ]
}
```

这告诉 Vercel：所有路径都返回 `index.html`，由 Vue Router 在浏览器端处理路由。

---

### 14.4 配置 CORS

后端需要允许来自 Vercel 域名的跨域请求。在 `SecurityConfig.java` 中：

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOriginPatterns(List.of(
        "http://localhost:*",                    // 本地开发
        "https://你的项目名.vercel.app"           // 生产环境
    ));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(List.of("*"));
    config.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
}
```

> **CORS 是什么？** 浏览器的安全策略不允许一个域名（`xxx.vercel.app`）的页面直接请求另一个域名（`xxx.railway.app`）的接口，除非后端明确允许。后端通过返回特定的响应头（`Access-Control-Allow-Origin`）来告诉浏览器"我允许你来请求"。

---

### 14.5 自动部署流程

配置完成后，每次 `git push`：

```
git push
   ↓
GitHub 仓库更新
   ↓
Vercel 检测到变化 → 自动构建前端 → 部署新版本
Railway 检测到变化 → 自动构建后端 → 部署新版本
```

整个流程约 2-5 分钟完成，无需手动操作。

---

*本文档基于 HJB Insurance v1.0 项目编写。如遇到文档与代码不一致的情况，以实际代码为准。*
