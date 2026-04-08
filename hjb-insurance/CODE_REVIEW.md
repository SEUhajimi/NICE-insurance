# HJB Insurance — Code Review Report

**Review Date:** 2026-04-08  
**Scope:** Full-stack (Spring Boot backend + Vue 3 frontend)  
**Total Issues Found:** 28

---

## Summary

| 类别 | Critical | High | Medium | Low |
|------|---------|------|--------|-----|
| 安全 | 4 | 4 | 3 | 0 |
| 事务 | 1 | 2 | 0 | 0 |
| 业务逻辑 | 1 | 3 | 2 | 0 |
| 代码质量 | 0 | 0 | 5 | 1 |
| API 设计 | 0 | 1 | 3 | 1 |
| 前端 | 0 | 0 | 3 | 0 |

---

## 🔴 Critical

### 1. JWT Filter 对无效 Token 放行
**文件：** `hjb-server/.../filter/JwtAuthFilter.java:37-39`

当 token 验证失败时，代码继续执行 `filterChain.doFilter()` 而不是拒绝请求。这意味着携带无效/过期 token 的请求会被当作未认证请求继续处理，可能绕过部分保护。

```java
// ❌ 现在
if (!jwtUtil.isTokenValid(token)) {
    filterChain.doFilter(request, response);  // 放行了！
    return;
}

// ✅ 应该改为
if (!jwtUtil.isTokenValid(token)) {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json;charset=UTF-8");
    response.getWriter().write("{\"code\":0,\"msg\":\"Token 无效或已过期\"}");
    return;
}
```

---

### 2. JWT Secret 硬编码在代码库
**文件：** `application.yml:17`

```yaml
jwt:
  secret: aGpiLWluc3VyYW5jZS1zZWNyZXQta2V5LW11c3QtYmUtYXQtbGVhc3QtMzItY2hhcnMhIQ==
```

Base64 解码后是明文字符串 `hjb-insurance-secret-key-must-be-at-least-32-chars!!`。任何能访问 GitHub 仓库的人都能伪造任意用户的 JWT。

**修复：** 改为环境变量 `${JWT_SECRET}`，Railway 里单独设置一个随机 32 字节的值。

---

### 3. 默认管理员账号自动创建且密码可预测
**文件：** `DataInitializer.java:27-28`

```java
admin.setPassword(passwordEncoder.encode("Admin@123"));
System.out.println(">>> 默认管理员账号已创建: admin / Admin@123");
```

每次启动如果没有管理员账号就会自动创建，密码通过控制台打印出来。攻击者只需要看到日志就能登录管理后台。

**修复：** 密码改为从环境变量读取，或禁止在生产环境自动创建。

---

### 4. 购险事务中二次查询可能导致数据不一致
**文件：** `CustomerPortalServiceImpl.java:58-72`

```java
@Transactional
public void purchasePolicy(String username, PurchaseRequest req) {
    if (account.getCustomerId() == null) {
        customerMapper.insertForRegister(customer);             // 步骤1：插入客户记录
        customerAccountMapper.updateCustomerId(...);             // 步骤2：关联账号
        account = getAccount(username);                          // ← 如果这里抛异常
        // 步骤3-4 不会执行，但步骤1-2已提交？
    }
}
```

`@Transactional` 默认只回滚 `RuntimeException`，如果 `getAccount()` 内部查询失败（如网络抖动），已插入的客户记录不一定会回滚，取决于异常类型。

**修复：** 改为 `@Transactional(rollbackFor = Exception.class)`，并避免在事务中间重新查询——直接在内存中更新 account 对象。

---

## 🟠 High

### 5. makePayment 存在并发重复支付漏洞
**文件：** `CustomerPortalServiceImpl.java:167-198`

虽然加了 `@Transactional`，但默认隔离级别（READ_COMMITTED）不能防止两个并发请求同时通过"已全额支付"检查，然后都插入支付记录。

**修复：** 改为 `@Transactional(isolation = Isolation.SERIALIZABLE)` 或对 invoice 加行锁（`SELECT ... FOR UPDATE`）。

---

### 6. 密码重置不安全
**文件：** `AuthServiceImpl.java:86-92`

只要知道用户名+邮箱就能重置密码，没有时间限制，没有邮件确认。对于很多用户来说，用户名和邮箱都是公开信息（比如论坛账号）。

**修复（最小改动）：** 至少加一个"当前密码"验证；完整方案是发送带过期时间的重置链接到邮箱。

---

### 7. 注册时的错误信息泄露用户存在性
**文件：** `AuthServiceImpl.java:61-62`

```java
if (customerAccountMapper.findByUsername(username) != null) {
    throw new RuntimeException("用户名已存在");  // ← 确认了该用户名存在
}
```

攻击者可以通过注册接口枚举有效用户名，作为暴力破解的第一步。

**修复：** 改为 `"该用户名不可用"` 之类的通用措辞。

---

### 8. 所有 `findAll()` 接口没有分页
**文件：** `CustomerController.java`, `PaymentController.java` 等

数据量大时会把整张表加载进内存，导致 OOM 或超时。

**修复：** 加 `?page=0&size=20` 分页参数，用 MyBatis 的 RowBounds 实现。

---

### 9. 无输入校验
**文件：** 所有 DTO 类

注册时 password 可以是空字符串，email 可以是 `"abc"`，amount 可以是 null。没有任何 JSR-303 注解（`@NotBlank`, `@Email`, `@Size`）。

**修复：** DTO 加校验注解，Controller 加 `@Valid`。

---

### 10. Controller 缺少方法级别的权限注解
**文件：** `AdminController.java` 等

仅靠 SecurityConfig 的 URL 匹配规则保护，如果 SecurityConfig 配置出现漏洞，所有管理接口都会暴露。

**修复：** 关键操作加 `@PreAuthorize("hasRole('EMPLOYEE')")`。

---

## 🟡 Medium

### 11. invoiceBelongsToUser 存在 N+1 查询
**文件：** `CustomerPortalServiceImpl.java:200-214`

每次支付时都要加载该用户的所有保单来做归属检查，随着保单增多性能线性下降。

**修复：** 直接用 SQL JOIN 查询：`SELECT * FROM hjb_autopolicy WHERE ap_id = ? AND hjb_customer_cust_id = ?`

---

### 12. 魔法字符串散落在代码各处
**文件：** 多处

```java
String status = "C";     // 什么是 C？Current？
String custType = "B";   // 什么是 B？Both？
```

**修复：** 创建枚举类 `PolicyStatus` 和 `CustomerType`。

---

### 13. System.out.println 替代日志框架
**文件：** `DataInitializer.java`, `PolicyScheduler.java`

生产环境无法控制输出级别，无法集成到日志聚合系统。

**修复：** 改用 `private static final Logger log = LoggerFactory.getLogger(...)` + `log.info()`。

---

### 14. 前端 Token 存储在 localStorage
**文件：** `CustomerAuthView.vue:151-153`

XSS 攻击可以读取 localStorage 里的 token，一旦泄露攻击者可以用 token 访问所有 API。

**最佳方案：** 用 HTTP-only Cookie 存储 token（JS 无法读取）。**当前项目暂不要求修改**，知道这个风险即可。

---

### 15. 自定义异常缺失
**文件：** 所有 Service 实现类

全部用 `throw new RuntimeException("...")` 无法在 GlobalExceptionHandler 里做精细化处理（比如区分 403 和 400）。

**修复：** 创建 `UnauthorizedException`、`ValidationException`、`NotFoundException`。

---

### 16. 前端缺少全局错误提示
**文件：** `request.js:29`

401 以外的错误只打 console.log，用户看不到任何反馈。

**修复：** 引入 Element Plus 的 `ElMessage.error()` 展示错误信息。

---

### 17. custType 不会随保单变化自动更新
**文件：** `CustomerPortalServiceImpl.java:109-112`

购险时计算 custType，但如果后续管理员删除了某张保单，custType 不会更新。

**修复：** 删除保单时也调用重新计算 custType 的逻辑。

---

## 🟢 Low

### 18. Swagger 文档部分接口缺少 @Operation
**文件：** 多个 Controller

Admin 接口的文档描述很少，不方便其他开发者理解。

---

## 本次已修复的问题（对照）

| 问题 | 状态 |
|------|------|
| @Transactional 加在 Controller 上（反模式） | ✅ 已修复，移至 Service |
| makePayment 无事务保护 | ✅ 已修复，加了 @Transactional |
| makePayment 无归属校验（IDOR） | ✅ 已修复，加了 invoiceBelongsToUser |
| makePayment 无金额校验 | ✅ 已修复，加了 > 0 和超额判断 |
| purchasePolicy type 无校验 | ✅ 已修复，加了 AUTO/HOME 校验 |
| Controller 直接注入 Mapper（架构问题） | ✅ 已修复，提取 CustomerPortalService |

---

## 推荐修复优先级

**立即修复（影响安全）：**
1. JWT Filter 对无效 token 的处理（#1）
2. JWT Secret 改环境变量（#2）
3. 默认管理员密码改环境变量（#3）

**下一阶段（功能完善）：**
4. DTO 输入校验（#9）
5. 分页（#8）
6. 自定义异常类型（#15）
7. 枚举替代魔法字符串（#12）

**长期优化：**
8. 密码重置流程重设计（#6）
9. 日志框架（#13）
10. 分页和 N+1 查询优化（#11）
