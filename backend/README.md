# Smart Ledger 后端

智能个人记账系统后端服务

## 技术栈

- **Spring Boot 3.2.0** - 核心框架
- **MyBatis Plus 3.5.5** - ORM框架
- **MySQL 8.0** - 数据库
- **JWT** - 身份认证
- **Spring Security** - 安全框架

## 项目结构

```
src/main/java/com/smartledger/
├── SmartLedgerApplication.java  # 启动类
├── common/                       # 通用类
│   ├── Result.java              # 统一响应
│   └── PageResult.java          # 分页响应
├── config/                       # 配置类
│   ├── SecurityConfig.java      # 安全配置
│   ├── CorsConfig.java          # 跨域配置
│   ├── MybatisPlusConfig.java   # MyBatis Plus配置
│   └── GlobalExceptionHandler.java # 全局异常处理
├── controller/                   # 控制器
│   ├── AuthController.java      # 认证接口
│   ├── UserController.java      # 用户接口
│   ├── BillController.java      # 账单接口
│   ├── DiaryController.java     # 日记接口
│   └── SavingPlanController.java # 存钱计划接口
├── dto/                          # 数据传输对象
├── entity/                       # 实体类
├── mapper/                       # Mapper接口
├── service/                      # 服务层
│   └── impl/                    # 服务实现
└── utils/                        # 工具类
    └── JwtUtil.java             # JWT工具
```

## 快速开始

### 1. 创建数据库

```sql
CREATE DATABASE smart_ledger DEFAULT CHARACTER SET utf8mb4;
```

或执行 `src/main/resources/schema.sql`

### 2. 修改配置

编辑 `src/main/resources/application.yml`，修改数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/smart_ledger
    username: your_username
    password: your_password
```

### 3. 运行项目

```bash
mvn spring-boot:run
```

服务将在 http://localhost:8080 启动

## API 接口

### 认证接口

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | /api/auth/login | 用户登录 |
| POST | /api/auth/register | 用户注册 |

### 用户接口

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /api/user/info | 获取用户信息 |
| PUT | /api/user/info | 更新用户信息 |
| PUT | /api/user/password | 修改密码 |

### 账单接口

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /api/bills | 获取账单列表 |
| POST | /api/bills | 添加账单 |
| PUT | /api/bills/{id} | 更新账单 |
| DELETE | /api/bills/{id} | 删除账单 |
| GET | /api/bills/recent | 获取最近账单 |
| GET | /api/bills/statistics/month | 月度统计 |
| GET | /api/bills/statistics/category | 分类统计 |

### 日记接口

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /api/diaries | 获取日记列表 |
| POST | /api/diaries | 添加日记 |
| PUT | /api/diaries/{id} | 更新日记 |
| DELETE | /api/diaries/{id} | 删除日记 |

### 存钱计划接口

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /api/savings | 获取计划列表 |
| POST | /api/savings | 创建计划 |
| PUT | /api/savings/{id} | 更新计划 |
| DELETE | /api/savings/{id} | 删除计划 |
| POST | /api/savings/{id}/deposit | 存入金额 |
