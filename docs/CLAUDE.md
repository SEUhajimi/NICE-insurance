# HJB Insurance — Claude 上下文文件

> 每次新会话自动读取，不需要重新解释背景。

---

## 项目背景

- **课程**：NYU CS-GY 6083 Principles of Database Systems，Part II
- **性质**：期末项目，需 Demo（80分）+ 提交报告 PDF（20分）
- **团队**：hhl（Git user）
- **提交方式**：NYU Brightspace，PDF 格式

---

## 技术栈

| 层 | 技术 |
|----|------|
| 后端框架 | Spring Boot 3.2.5，Java 17，Maven 多模块 |
| ORM | MyBatis 3.0.3（注解映射，无 XML） |
| 安全 | Spring Security + JWT (JJWT 0.12.3)，BCrypt 加密 |
| API 文档 | SpringDoc OpenAPI 2.3.0（Swagger UI） |
| 数据库 | MySQL 8.0+，数据库名 `nice3` |
| 前端 | Vue 3.5 + Vite + Element Plus + Vue Router + Axios |
| 部署 | 前端 Vercel，后端 Docker 容器化 |

---

## 项目模块

```
hjb-insurance/
├── hjb-pojo/      Entity + DTO + Enum
├── hjb-common/    Result<T> 通用响应
└── hjb-server/    核心应用（controller/service/mapper/config/filter/exception/scheduler）
```

---

## 编码规范

- **不写注释**，用有意义的命名代替
- 所有 HTTP 响应用 `Result<T>` 包装（code 1=成功，0=失败）
- 异常统一抛自定义类（`ValidationException` / `NotFoundException` / `UnauthorizedException`），由 `GlobalExceptionHandler` 处理
- MyBatis 注解 SQL，禁止字符串拼接 SQL（防 SQL 注入）
- 数据库字段下划线，Java 字段驼峰，MyBatis 自动映射（`map-underscore-to-camel-case: true`）

---

## 已实现的功能

- 客户注册 / 登录 / 找回密码
- 员工登录，启动时自动创建 admin 账户
- 客户门户：购保、查保单、查账单、在线支付
- 管理端：客户、保单、车辆、驾驶员、房产、账单、支付的完整 CRUD
- 保险套餐管理（Plan）
- JWT 无状态认证，URL 级别权限控制
- 每日凌晨 1 点自动更新过期保单状态（Scheduler）
- 事务隔离：购保 `@Transactional`，支付 `REPEATABLE_READ` 防并发重复支付
- 全局异常处理，敏感错误不暴露
- CORS 配置（localhost:* + Vercel 生产域名）
- Swagger UI（/swagger-ui.html）

---

## 课程要求对照（用于 Demo 和报告）

### 基础要求（必须满足）
- [x] 用户注册、登录
- [x] 客户和员工不同权限
- [x] CRUD 操作
- [x] RESTful API
- [x] 密码加密（BCrypt）
- [x] SQL 注入防护（MyBatis Prepared Statements）
- [x] 事务并发控制 + 死锁预防（REPEATABLE_READ）
- [x] XSS 防护（前端需确认）

### 报告 PDF 需包含（20分）
- [ ] 封面：课程名、成员姓名+学号、提交日期
- [ ] 目录
- [ ] Executive Summary（业务背景、方案、ER/关系模型）
- [ ] 技术栈说明
- [ ] DDL 文件内容（`sql/mysql_schema.sql`）
- [ ] 各表记录数统计
- [ ] Web 界面截图
- [ ] 安全功能说明
- [ ] Lessons Learned
- [ ] **6 条业务分析 SQL**（见下方）

### 6 条 SQL 查询（每条含 SQL + 结果 + 业务目的）
- [ ] Q1：3 表以上 JOIN
- [ ] Q2：Multi-row subquery
- [ ] Q3：Correlated subquery
- [ ] Q4：SET operator（UNION / INTERSECT / MINUS）
- [ ] Q5：Inline view 或 WITH clause
- [ ] Q6：TOP-N / BOTTOM-N

### 加分项（最高 +6%）
- [ ] 数据库索引（说明建了哪些索引、为什么、性能对比）
- [ ] 数据可视化（图表）
- [ ] 密码重置安全校验 / 存储过程 / 历史表

---

## 关键文件位置

| 内容 | 路径 |
|------|------|
| 应用配置 | `hjb-server/src/main/resources/application.yml` |
| 安全配置 | `hjb-server/.../config/SecurityConfig.java` |
| JWT 过滤器 | `hjb-server/.../filter/JwtAuthFilter.java` |
| 门户 Service | `hjb-server/.../service/impl/CustomerPortalServiceImpl.java` |
| 数据库 DDL | `sql/mysql_schema.sql` |
| 数据库 DML | `sql/hjb_mysql_dml.sql` |
| 开发者报告 | `PROJECT_REPORT.md` |
| 协作指南 | `CLAUDE_WORKFLOW.md` |

---

## 当前未完成 / 已知问题

- `CustomerPortalServiceImpl.java` 有一处未提交的修改：`setCustType` 从 `null` 改为根据类型赋值（`A`/`H`），需要确认并提交
- 密码重置无邮件验证（当前仅校验用户名+邮箱匹配）
- 删除保单后 `custType` 不会自动更新
- 缺分页功能

---

## 环境变量

```
PORT=8080, DB_HOST, DB_USER, DB_PASS, JWT_SECRET, ADMIN_PASSWORD
```
