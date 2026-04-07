# HJB Insurance — Development Manual

A step-by-step guide for beginners to set up and run the full HJB Insurance project locally.

---

## Table of Contents

1. [Prerequisites](#1-prerequisites)
2. [Project Structure](#2-project-structure)
3. [Database Setup](#3-database-setup)
4. [Backend Setup (Spring Boot)](#4-backend-setup-spring-boot)
5. [Frontend Setup (Vue 3)](#5-frontend-setup-vue-3)
6. [Running the Application](#6-running-the-application)
7. [nginx Reverse Proxy (Optional)](#7-nginx-reverse-proxy-optional)
8. [API Overview](#8-api-overview)
9. [Common Issues & Fixes](#9-common-issues--fixes)

---

## 1. Prerequisites

Install the following tools before you begin.

### 1.1 Java Development Kit (JDK 17)

1. Download JDK 17 from [https://adoptium.net](https://adoptium.net) (choose "Temurin 17 LTS").
2. Run the installer.
3. Verify installation:
   ```bash
   java -version
   # Expected: openjdk version "17.x.x"
   ```
4. Set `JAVA_HOME` environment variable:
   - Windows: System Properties → Environment Variables → New system variable
     - Name: `JAVA_HOME`
     - Value: `C:\Program Files\Eclipse Adoptium\jdk-17.x.x-hotspot` (your install path)
   - Add `%JAVA_HOME%\bin` to the `PATH` variable.

### 1.2 Apache Maven 3.9+

1. Download from [https://maven.apache.org/download.cgi](https://maven.apache.org/download.cgi) (Binary zip archive).
2. Extract to a folder, e.g., `C:\tools\maven`.
3. Add `C:\tools\maven\bin` to the system `PATH`.
4. Verify:
   ```bash
   mvn -version
   # Expected: Apache Maven 3.x.x
   ```

### 1.3 Node.js 18+ (LTS)

1. Download from [https://nodejs.org](https://nodejs.org) (LTS version).
2. Run the installer.
3. Verify:
   ```bash
   node -v    # Expected: v18.x.x or higher
   npm -v     # Expected: 9.x.x or higher
   ```

### 1.4 MySQL 8.0

1. Download MySQL Installer from [https://dev.mysql.com/downloads/installer/](https://dev.mysql.com/downloads/installer/).
2. Choose "Developer Default" during setup.
3. Set root password (remember it — you'll need it).
4. Verify (open MySQL Command Line Client or Workbench):
   ```sql
   SELECT VERSION();
   -- Expected: 8.x.x
   ```

### 1.5 IDE (Recommended)

- **Backend**: IntelliJ IDEA Community Edition — [https://www.jetbrains.com/idea/download/](https://www.jetbrains.com/idea/download/)
- **Frontend**: VS Code — [https://code.visualstudio.com/](https://code.visualstudio.com/)
  - Install extension: **Volar** (Vue Language Features)

---

## 2. Project Structure

The repository lives in two sibling directories:

```
GitHub-Repos/NICE/
├── hjb-insurance/              ← Backend (Maven multi-module)
│   ├── hjb-pojo/               ← Entity & DTO classes
│   ├── hjb-common/             ← Shared utilities (JWT, Result, etc.)
│   ├── hjb-server/             ← Spring Boot application (controllers, services, mappers)
│   └── pom.xml                 ← Parent POM
│
└── hjb-frontend-vue/
    └── hjb-frontend-vue/       ← Vue 3 + Vite frontend
        ├── src/
        │   ├── api/            ← Axios API calls
        │   ├── views/          ← Page components
        │   └── router/         ← Vue Router config
        └── vite.config.js      ← Dev server & proxy config
```

### Backend Modules

| Module | Purpose |
|--------|---------|
| `hjb-pojo` | Plain Java entity classes (maps to DB tables), DTOs |
| `hjb-common` | Shared code: JWT utils, Result wrapper, constants |
| `hjb-server` | Main application: Spring Boot, controllers, services, MyBatis mappers |

---

## 3. Database Setup

### 3.1 Create the Schema

Open MySQL Workbench or the MySQL CLI and run:

```sql
CREATE DATABASE nice CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE nice;
```

### 3.2 Run the Main Schema Script

Execute `hjb-insurance/sql/schema.sql` (creates all tables and sample data):

```bash
mysql -u root -p nice < hjb-insurance/sql/schema.sql
```

### 3.3 Run the Migration Script (v2)

Execute `hjb-insurance/sql/schema_update_v2.sql` (adds personal info columns to `customer_account`, makes `customer_id` nullable):

```bash
mysql -u root -p nice < hjb-insurance/sql/schema_update_v2.sql
```

### 3.4 Key Tables

| Table | Description |
|-------|-------------|
| `employee` | Admin/staff accounts (login via `/api/admin/login`) |
| `customer_account` | Portal user accounts (login via `/api/auth/customer/login`) |
| `hjb_customer` | Customer records — created only after first insurance purchase |
| `hjb_autopolicy` | Auto insurance policies |
| `hjb_homepolicy` | Home insurance policies |
| `hjb_invoice` | Invoices linked to policies |
| `hjb_payment` | Payments linked to invoices |

> **Note:** A user can have a `customer_account` without having a `hjb_customer` record. The customer record is only created when they purchase their first policy.

### 3.5 Default Admin Account

The schema seeds one employee account:

| Field | Value |
|-------|-------|
| Username | `admin` |
| Password | `123456` (BCrypt-hashed in DB) |

---

## 4. Backend Setup (Spring Boot)

### 4.1 Configure Database Connection

Open `hjb-server/src/main/resources/application.yml` and update the datasource:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/nice?useSSL=false&serverTimezone=UTC
    username: root
    password: YOUR_MYSQL_PASSWORD   # ← Change this
    driver-class-name: com.mysql.cj.jdbc.Driver
```

### 4.2 JWT Secret

The JWT secret is configured in the same file. You can leave the default for development, but change it for production:

```yaml
hjb:
  jwt:
    secret-key: hjb-insurance-secret-key-change-me-in-production
    ttl: 7200000   # 2 hours in milliseconds
    token-name: token
```

### 4.3 Build the Project

From the `hjb-insurance/` directory:

```bash
mvn clean install -DskipTests
```

This compiles all three modules and packages `hjb-server` into a runnable JAR.

### 4.4 Run the Backend

```bash
cd hjb-server
mvn spring-boot:run
```

Or run the compiled JAR:

```bash
java -jar hjb-server/target/hjb-server-1.0-SNAPSHOT.jar
```

The backend starts on **port 8080** by default.

Verify it's running:
```bash
curl http://localhost:8080/api/admin/login -X POST \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'
# Expected: {"code":1,"msg":"success","data":"<jwt-token>"}
```

---

## 5. Frontend Setup (Vue 3)

### 5.1 Install Dependencies

```bash
cd hjb-frontend-vue/hjb-frontend-vue
npm install
```

### 5.2 Vite Dev Server Proxy

The frontend proxies `/api` requests to the backend. Check `vite.config.js`:

```js
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true
    }
  }
}
```

This means `fetch('/api/admin/login')` in the browser actually hits `http://localhost:8080/api/admin/login`.

### 5.3 Start the Dev Server

```bash
npm run dev
```

Vite will print something like:

```
  VITE v5.x.x  ready in 500 ms

  ➜  Local:   http://localhost:5173/
```

> **Note:** If port 5173 is already in use, Vite will auto-select the next available port (5174, 5175, etc.). The backend allows all `http://localhost:*` origins so this is fine.

---

## 6. Running the Application

With both servers running, open your browser:

| URL | Description |
|-----|-------------|
| `http://localhost:5173/` | Company landing page |
| `http://localhost:5173/admin` | Admin login |
| `http://localhost:5173/customer/auth` | Customer registration & login |
| `http://localhost:5173/customer/portal` | Customer portal (requires login) |

### Default Users

| Role | Username | Password | Login URL |
|------|----------|----------|-----------|
| Admin | `admin` | `123456` | `/admin` |
| Customer | *(register first)* | *(your choice)* | `/customer/auth` |

### Customer Registration Flow

1. Go to `/customer/auth` → **Register** tab.
2. Fill in: Username, Email, Password, First Name, Last Name, Gender, Marital Status, Address.
3. After registration, log in on the **Sign In** tab.
4. In the portal, go to **Get Insured** tab to purchase a plan.
5. After purchasing, the **Policies**, **Invoices**, and **Payment History** tabs will populate.

---

## 7. nginx Reverse Proxy (Optional)

For a production-like setup where the frontend is served on port 80 via nginx:

### 7.1 Build the Frontend

```bash
cd hjb-frontend-vue/hjb-frontend-vue
npm run build
# Output in: dist/
```

Copy the built files to the nginx html directory:

```bash
cp -r dist/* /path/to/nginx/html/
```

### 7.2 nginx Configuration

Edit `nginx.conf`:

```nginx
upstream webservers {
    server localhost:8080;
}

server {
    listen 80;
    server_name localhost;

    # Serve frontend static files
    location / {
        root   html;
        index  index.html index.htm;
        try_files $uri $uri/ /index.html;   # Required for Vue Router history mode
    }

    # Proxy API requests to Spring Boot
    location /api/ {
        proxy_pass http://webservers/api/;
    }
}
```

### 7.3 Start nginx

```bash
nginx            # Start
nginx -s reload  # Reload config after changes
nginx -s stop    # Stop
```

Access the app at `http://localhost/`.

---

## 8. API Overview

### Authentication

| Method | URL | Description | Auth Required |
|--------|-----|-------------|---------------|
| POST | `/api/admin/login` | Admin login | No |
| POST | `/api/auth/customer/register` | Customer register | No |
| POST | `/api/auth/customer/login` | Customer login | No |
| POST | `/api/auth/customer/reset-password` | Reset password | No |

### Admin Endpoints (require admin JWT in `token` header)

| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/admin/customers` | List all customers |
| POST | `/api/admin/customers` | Add customer |
| PUT | `/api/admin/customers` | Update customer |
| DELETE | `/api/admin/customers/{id}` | Delete customer |
| GET/POST/PUT/DELETE | `/api/admin/autopolicies` | Auto policy CRUD |
| GET/POST/PUT/DELETE | `/api/admin/homepolicies` | Home policy CRUD |
| GET/POST/PUT/DELETE | `/api/admin/invoices` | Invoice CRUD |
| GET/POST/PUT/DELETE | `/api/admin/payments` | Payment CRUD |

### Customer Portal Endpoints (require customer JWT in `token` header)

| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/portal/profile` | Get user profile |
| GET | `/api/portal/policies` | List my policies |
| GET | `/api/portal/invoices` | List my invoices |
| GET | `/api/portal/payments` | List my payments |
| POST | `/api/portal/purchase` | Purchase a plan |

---

## 9. Common Issues & Fixes

### ❌ Admin login returns 403 for OPTIONS request

**Cause:** Spring Security blocks CORS preflight requests before they reach the CORS filter.

**Fix:** In `SecurityConfig.java`, permit OPTIONS requests:
```java
.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
```

---

### ❌ `Invalid username or password` even with correct credentials

**Possible causes:**
1. The database wasn't seeded — run `schema.sql` again.
2. The `application.yml` password doesn't match your MySQL installation.
3. The BCrypt hash in the DB is for a different password.

**Check:** Connect to MySQL and verify:
```sql
SELECT username, password FROM employee WHERE username = 'admin';
```
The password column should start with `$2a$` (BCrypt prefix).

---

### ❌ Frontend can't connect to backend (Network Error)

**Check:**
1. Is the backend running? `curl http://localhost:8080/api/admin/login`
2. Is the Vite proxy configured in `vite.config.js`?
3. Are you hitting the right port? Check what Vite printed at startup.

---

### ❌ `400 Bad Request` when registering a customer

**Cause:** The `customer_account` table is missing the new columns added in v2.

**Fix:** Run the migration:
```bash
mysql -u root -p nice < hjb-insurance/sql/schema_update_v2.sql
```

---

### ❌ `mvn spring-boot:run` fails with compile errors

**Check:**
1. Is JDK 17 selected? Run `java -version`.
2. Did you run `mvn clean install` from the parent directory first?
3. Look at the error message — it usually points to the file and line number.

---

### ❌ Vue page shows blank / router doesn't work after nginx deploy

**Cause:** Vue Router uses HTML5 history mode — nginx must return `index.html` for unknown paths.

**Fix:** Add `try_files $uri $uri/ /index.html;` in the nginx `location /` block (shown in section 7.2).

---

### ❌ JWT token expired / 401 after some time

The token TTL is set to 2 hours in `application.yml`. Log out and log back in to get a fresh token. For development, increase the TTL:

```yaml
hjb:
  jwt:
    ttl: 86400000   # 24 hours
```

---

## Appendix: Technology Stack

| Layer | Technology | Version |
|-------|-----------|---------|
| Backend framework | Spring Boot | 3.2.5 |
| Security | Spring Security + JWT | — |
| ORM | MyBatis | 3.x |
| Database | MySQL | 8.0 |
| Build tool | Apache Maven | 3.9+ |
| Frontend framework | Vue | 3.x |
| Build tool | Vite | 5.x |
| UI component library | Element Plus | 2.x |
| HTTP client | Axios | 1.x |
| Web server (prod) | nginx | 1.20+ |
| Java version | JDK | 17 |
| Node.js version | Node.js | 18+ LTS |
