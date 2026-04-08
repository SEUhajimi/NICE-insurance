# Changelog

## [1.1.0] - 2026-04-08

### 安全修复

#### 🔴 Critical

- **JWT Filter 修复**：`JwtAuthFilter.java` — 无效/过期 token 原先会被放行继续执行请求链，现改为立即返回 HTTP 401 并终止请求，彻底杜绝 token 绕过漏洞

- **JWT Secret 环境变量化**：`application.yml` — 密钥不再硬编码，改为 `${JWT_SECRET:默认值}` 形式，生产环境通过 Railway Variables 注入随机密钥

- **默认管理员密码环境变量化**：`DataInitializer.java` — 密码改为从 `${admin.default-password:Admin@123}` 读取，日志不再打印明文密码，改为使用 SLF4J `log.warn()` 输出安全提示

#### 🟠 High

- **注册错误信息模糊化**：`AuthServiceImpl.java` — 用户名/邮箱已存在时，错误信息从 `"用户名已存在"` 改为 `"该用户名不可用"`，防止攻击者通过注册接口枚举有效账号

- **DTO 输入校验**：`CustomerRegisterRequest.java`, `ResetPasswordRequest.java` — 新增 `@NotBlank`, `@Email`, `@Size`, `@Pattern` 校验注解；`AuthController` 对应方法加 `@Valid`，校验失败时自动返回 400 及具体错误信息

- **ValidationException 依赖**：`hjb-pojo/pom.xml` 新增 `jakarta.validation-api`，`hjb-server/pom.xml` 新增 `spring-boot-starter-validation`

---

### 事务管理修复

- **purchasePolicy 事务加强**：`CustomerPortalServiceImpl.java` — `@Transactional` 改为 `@Transactional(rollbackFor = Exception.class)`，确保任何异常（包括 checked exception）都触发回滚；消除中间二次数据库查询，改为在内存中更新 account 对象，避免事务中途状态不一致

- **makePayment 事务隔离级别提升**：`CustomerPortalServiceImpl.java` — 改为 `@Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)`，防止并发支付时的幻读导致重复扣款

- **CRUD Service 补齐事务**：`AutoPolicyServiceImpl`, `CustomerServiceImpl`, `DriverServiceImpl`, `HomeServiceImpl`, `HomePolicyServiceImpl`, `InvoiceServiceImpl`, `PaymentServiceImpl`, `VehicleServiceImpl` — 所有 `add()`, `update()`, `deleteById()` 方法加 `@Transactional` 注解

---

### 代码质量改进

- **自定义异常类**（新增文件）：
  - `exception/UnauthorizedException.java` → HTTP 403
  - `exception/NotFoundException.java` → HTTP 404
  - `exception/ValidationException.java` → HTTP 400
  
- **GlobalExceptionHandler 更新**：按异常类型返回正确 HTTP 状态码；新增 `MethodArgumentNotValidException` 处理（格式化 @Valid 的校验错误信息）；未知异常不再将内部错误信息暴露给前端

- **枚举类替代魔法字符串**（新增文件）：
  - `enums/PolicyStatus.java`：`CURRENT("C")`, `TERMINATED("T")`
  - `enums/CustomerType.java`：`AUTO("A")`, `HOME("H")`, `BOTH("B")`，含 `calculate(hasAuto, hasHome)` 静态方法
  - `CustomerPortalServiceImpl` 中所有硬编码的 `"C"`, `"A"`, `"H"`, `"B"` 改为使用枚举

- **SLF4J 替代 System.out**：
  - `DataInitializer.java` — 改用 `Logger log = LoggerFactory.getLogger(...)`
  - `PolicyScheduler.java` — 改用 `log.info()`

- **N+1 查询修复**：`invoiceBelongsToUser()` 原先加载用户所有保单做 Java 层过滤，现在改为精准 SQL 查询（`WHERE ap_id = ? AND hjb_customer_cust_id = ?`）；对应新增 Mapper 方法：
  - `AutoPolicyMapper.findByIdAndCustomerId()`
  - `HomePolicyMapper.findByIdAndCustomerId()`

---

### 前端改进

- **全局错误处理**：`src/api/request.js`
  - 业务层错误（`code: 0`）自动弹出 `ElMessage.error(msg)`，无需每个页面单独处理
  - 401 未授权：清除 token 并跳转登录页，弹出 `ElMessage.warning()`
  - 403 禁止访问：弹出 `ElMessage.error('无权限执行该操作')`
  - 网络错误：弹出具体错误信息而非静默失败

---

### 未修复项（标记待处理）

| 问题 | 原因 | 计划 |
|------|------|------|
| 密码重置无邮件验证 | 需要邮件服务（SMTP/SendGrid）接入 | v1.2.0 |
| 分页 | 需要修改 Mapper 和前端表格 | v1.2.0 |
| LocalStorage 存储 Token | 需要后端 Cookie 机制配合 | v1.2.0 |
| 方法级别 @PreAuthorize | 低优先级，SecurityConfig URL 规则已覆盖 | v1.3.0 |
| custType 删除保单后不更新 | 管理员删保单场景罕见 | v1.3.0 |
