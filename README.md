# Smart Ledger 智能个人记账系统

一个基于 Vue 3 + Spring Boot 的个人记账管理系统，支持账单管理、记账日记、存钱计划等功能。

## 技术栈

### 前端
- **Vue 3** + TypeScript
- **Vite** - 构建工具
- **Element Plus** - UI 组件库
- **Vue Router** - 路由管理
- **ECharts** - 数据可视化
- **Axios** - HTTP 请求

### 后端
- **Spring Boot 3.2** - 核心框架
- **MyBatis Plus** - ORM 框架
- **MySQL 8.0** - 数据库
- **JWT** - 身份认证
- **Spring Security** - 安全框架

## 系统要求

- Node.js >= 18
- Java >= 17
- Maven >= 3.6
- MySQL >= 8.0

## 快速开始

### 方式一：一键安装（推荐）

```bash
# 1. 克隆项目
git clone <repository-url>
cd smart-ledger

# 2. 添加执行权限
chmod +x setup.sh start.sh

# 3. 运行安装脚本（首次使用）
./setup.sh

# 4. 启动项目
./start.sh
```

### 方式二：手动安装

#### 1. 初始化数据库

```bash
# 登录 MySQL
mysql -u root -p

# 执行初始化脚本
source backend/src/main/resources/schema.sql
```

#### 2. 配置后端

编辑 `backend/src/main/resources/application.yml`，修改数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/smart_ledger
    username: root
    password: your_password
```

#### 3. 安装依赖

```bash
# 前端依赖
cd frontend
npm install  # 或 pnpm install / yarn install

# 后端依赖
cd ../backend
mvn dependency:resolve
```

#### 4. 启动服务

```bash
# 启动后端（终端1）
cd backend
mvn spring-boot:run

# 启动前端（终端2）
cd frontend
npm run dev
```

## 访问地址

- 前端：http://localhost:5173
- 后端 API：http://localhost:8080

## 测试账号

- 用户名：`admin`
- 密码：`123456`

## 功能模块

### 1. 用户管理
- 用户注册/登录
- 个人信息管理
- 密码修改

### 2. 账单管理
- 收入/支出记录
- 分类管理
- 搜索筛选
- 分页展示

### 3. 记账日记
- 每日财务心得
- 心情标签
- 按月筛选

### 4. 存钱计划
- 创建存钱目标
- 进度追踪
- 存入记录
- 每日建议存款

### 5. 统计分析
- 收支趋势图
- 分类占比
- 月度统计

## 项目结构

```
smart-ledger/
├── frontend/                 # 前端项目
│   ├── src/
│   │   ├── api/             # API 请求
│   │   ├── layout/          # 布局组件
│   │   ├── router/          # 路由配置
│   │   ├── views/           # 页面组件
│   │   └── main.ts          # 入口文件
│   └── package.json
│
├── backend/                  # 后端项目
│   ├── src/main/java/com/smartledger/
│   │   ├── config/          # 配置类
│   │   ├── controller/      # 控制器
│   │   ├── service/         # 服务层
│   │   ├── mapper/          # 数据访问层
│   │   ├── entity/          # 实体类
│   │   ├── dto/             # 数据传输对象
│   │   └── common/          # 通用类
│   └── pom.xml
│
├── setup.sh                  # 安装脚本
├── start.sh                  # 启动脚本
└── README.md
```

## API 文档

### 认证接口
| 方法 | 路径 | 描述 |
|------|------|------|
| POST | /api/auth/login | 用户登录 |
| POST | /api/auth/register | 用户注册 |

### 账单接口
| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /api/bills | 获取账单列表 |
| POST | /api/bills | 添加账单 |
| PUT | /api/bills/{id} | 更新账单 |
| DELETE | /api/bills/{id} | 删除账单 |

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

## 常见问题

### 1. 数据库连接失败
- 检查 MySQL 服务是否启动
- 检查数据库用户名密码是否正确
- 确认数据库 `smart_ledger` 是否已创建

### 2. 前端启动失败
- 确保 Node.js 版本 >= 18
- 删除 `node_modules` 后重新安装依赖

### 3. 后端启动失败
- 确保 Java 版本 >= 17
- 检查 Maven 是否正确配置

## License

MIT
