# Windows 环境安装清单

## 一、环境要求

### 1. Node.js (v18+)
- **下载地址**: https://nodejs.org/
- **推荐版本**: Node.js 18 LTS 或 20 LTS
- **安装说明**: 下载 `.msi` 安装包，双击安装，勾选 "Add to PATH"
- **验证安装**: 
  ```cmd
  node -v
  npm -v
  ```

### 2. Java JDK (v17+)
- **下载地址**: https://adoptium.net/ (推荐) 或 https://www.oracle.com/java/technologies/downloads/
- **推荐版本**: JDK 17 或 JDK 21
- **安装说明**: 
  1. 下载 `.msi` 安装包并安装
  2. 设置环境变量 `JAVA_HOME` 指向 JDK 安装目录
  3. 将 `%JAVA_HOME%\bin` 添加到 `PATH`
- **验证安装**:
  ```cmd
  java -version
  ```

### 3. Apache Maven (v3.6+)
- **下载地址**: https://maven.apache.org/download.cgi
- **推荐版本**: Maven 3.9.x
- **安装说明**:
  1. 下载 `apache-maven-x.x.x-bin.zip`
  2. 解压到 `C:\Program Files\Apache\maven`
  3. 设置环境变量 `MAVEN_HOME` 指向 Maven 目录
  4. 将 `%MAVEN_HOME%\bin` 添加到 `PATH`
- **验证安装**:
  ```cmd
  mvn -v
  ```

### 4. MySQL (v8.0+)
- **下载地址**: https://dev.mysql.com/downloads/mysql/
- **推荐版本**: MySQL 8.0.x
- **安装说明**:
  1. 下载 MySQL Installer
  2. 选择 "Developer Default" 或 "Server only"
  3. 设置 root 密码
  4. 将 `C:\Program Files\MySQL\MySQL Server 8.0\bin` 添加到 `PATH`
- **验证安装**:
  ```cmd
  mysql --version
  ```

### 5. Git (可选，用于克隆项目)
- **下载地址**: https://git-scm.com/download/win
- **安装说明**: 下载安装包，使用默认选项安装

---

## 二、环境变量配置示例

### 打开环境变量设置
1. 右键 "此电脑" → "属性"
2. 点击 "高级系统设置"
3. 点击 "环境变量"

### 系统变量配置
| 变量名 | 变量值示例 |
|--------|-----------|
| JAVA_HOME | C:\Program Files\Java\jdk-17 |
| MAVEN_HOME | C:\Program Files\Apache\maven |

### PATH 变量添加
```
%JAVA_HOME%\bin
%MAVEN_HOME%\bin
C:\Program Files\MySQL\MySQL Server 8.0\bin
C:\Program Files\nodejs
```

---

## 三、快速安装步骤

### 方式一：使用安装脚本（推荐）

1. **打开命令提示符** (以管理员身份运行)

2. **进入项目目录**
   ```cmd
   cd C:\path\to\smart-ledger
   ```

3. **运行安装脚本**
   ```cmd
   setup.bat
   ```

4. **按提示输入 MySQL 用户名和密码**

5. **等待安装完成**

### 方式二：手动安装

1. **初始化数据库**
   ```cmd
   mysql -u root -p < backend\src\main\resources\schema.sql
   ```

2. **修改数据库配置**
   
   编辑 `backend\src\main\resources\application.yml`：
   ```yaml
   spring:
     datasource:
       password: 你的MySQL密码
   ```

3. **安装前端依赖**
   ```cmd
   cd frontend
   npm install
   cd ..
   ```

4. **下载后端依赖**
   ```cmd
   cd backend
   mvn dependency:resolve
   cd ..
   ```

---

## 四、启动项目

### 方式一：使用启动脚本
```cmd
start.bat
```

### 方式二：手动启动

**终端 1 - 启动后端：**
```cmd
cd backend
mvn spring-boot:run
```

**终端 2 - 启动前端：**
```cmd
cd frontend
npm run dev
```

---

## 五、访问地址

| 服务 | 地址 |
|------|------|
| 前端 | http://localhost:5173 |
| 后端 API | http://localhost:8080 |

**测试账号**: admin / 123456

---

## 六、常见问题

### 1. 'node' 不是内部或外部命令
- **原因**: Node.js 未安装或未添加到 PATH
- **解决**: 重新安装 Node.js，确保勾选 "Add to PATH"

### 2. 'java' 不是内部或外部命令
- **原因**: JDK 未安装或 JAVA_HOME 未配置
- **解决**: 
  1. 确认 JDK 已安装
  2. 设置 JAVA_HOME 环境变量
  3. 将 `%JAVA_HOME%\bin` 添加到 PATH

### 3. 'mvn' 不是内部或外部命令
- **原因**: Maven 未安装或未添加到 PATH
- **解决**: 下载 Maven 并配置 MAVEN_HOME 和 PATH

### 4. MySQL 连接失败
- **原因**: MySQL 服务未启动或密码错误
- **解决**:
  1. 打开 "服务" (services.msc)
  2. 找到 "MySQL80" 服务并启动
  3. 确认密码正确

### 5. 端口被占用
- **原因**: 8080 或 5173 端口已被其他程序使用
- **解决**:
  ```cmd
  # 查看端口占用
  netstat -ano | findstr :8080
  
  # 结束占用进程
  taskkill /PID <进程ID> /F
  ```

### 6. Maven 下载依赖慢
- **解决**: 配置阿里云镜像
  
  编辑 `%USERPROFILE%\.m2\settings.xml`：
  ```xml
  <mirrors>
    <mirror>
      <id>aliyun</id>
      <mirrorOf>central</mirrorOf>
      <url>https://maven.aliyun.com/repository/central</url>
    </mirror>
  </mirrors>
  ```

### 7. npm 安装依赖慢
- **解决**: 使用淘宝镜像
  ```cmd
  npm config set registry https://registry.npmmirror.com
  ```

---

## 七、推荐开发工具

| 工具 | 用途 | 下载地址 |
|------|------|----------|
| VS Code | 前端开发 | https://code.visualstudio.com/ |
| IntelliJ IDEA | 后端开发 | https://www.jetbrains.com/idea/ |
| Navicat / DBeaver | 数据库管理 | https://dbeaver.io/ |
| Postman | API 测试 | https://www.postman.com/ |
