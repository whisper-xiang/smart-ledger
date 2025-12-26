@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

echo ==========================================
echo    Smart Ledger 智能记账系统 - 安装脚本
echo    Windows 版本
echo ==========================================

:: 检查 Node.js
echo.
echo [检查] Node.js...
where node >nul 2>nul
if %errorlevel% neq 0 (
    echo [错误] Node.js 未安装，请先安装 Node.js
    echo 下载地址: https://nodejs.org/
    pause
    exit /b 1
)
for /f "tokens=*" %%i in ('node -v') do echo Node 版本: %%i

:: 检查 Java
echo [检查] Java...
where java >nul 2>nul
if %errorlevel% neq 0 (
    echo [错误] Java 未安装，请先安装 JDK 17+
    echo 下载地址: https://adoptium.net/
    pause
    exit /b 1
)
java -version 2>&1 | findstr /i "version"

:: 检查 Maven
echo [检查] Maven...
where mvn >nul 2>nul
if %errorlevel% neq 0 (
    echo [错误] Maven 未安装，请先安装 Maven
    echo 下载地址: https://maven.apache.org/download.cgi
    pause
    exit /b 1
)
mvn -v | findstr /i "Apache Maven"

:: 检查 MySQL
echo [检查] MySQL...
where mysql >nul 2>nul
if %errorlevel% neq 0 (
    echo [警告] MySQL 命令行工具未找到，请确保 MySQL 已安装并添加到 PATH
    echo 下载地址: https://dev.mysql.com/downloads/mysql/
)

echo.
echo ==========================================
echo 请输入 MySQL 配置信息
echo ==========================================

set /p DB_USER="MySQL 用户名 (默认 root): "
if "%DB_USER%"=="" set DB_USER=root

set /p DB_PASS="MySQL 密码: "

:: 测试数据库连接
echo.
echo [执行] 测试数据库连接...
if "%DB_PASS%"=="" (
    mysql -u%DB_USER% -e "SELECT 1" >nul 2>nul
) else (
    mysql -u%DB_USER% -p%DB_PASS% -e "SELECT 1" >nul 2>nul
)
if %errorlevel% neq 0 (
    echo [错误] 无法连接到 MySQL，请检查用户名和密码
    pause
    exit /b 1
)
echo 数据库连接成功!

:: 初始化数据库
echo.
echo [执行] 初始化数据库...
if "%DB_PASS%"=="" (
    mysql -u%DB_USER% < backend\src\main\resources\schema.sql
) else (
    mysql -u%DB_USER% -p%DB_PASS% < backend\src\main\resources\schema.sql
)
if %errorlevel% neq 0 (
    echo [错误] 数据库初始化失败
    pause
    exit /b 1
)
echo 数据库初始化完成!

:: 更新后端配置
echo.
echo [执行] 更新后端配置...
if not "%DB_PASS%"=="" (
    powershell -Command "(Get-Content backend\src\main\resources\application.yml) -replace 'password: .*', 'password: %DB_PASS%' | Set-Content backend\src\main\resources\application.yml"
)

:: 安装前端依赖
echo.
echo [执行] 安装前端依赖...
cd frontend
where pnpm >nul 2>nul
if %errorlevel% equ 0 (
    pnpm install
) else (
    where yarn >nul 2>nul
    if %errorlevel% equ 0 (
        yarn install
    ) else (
        npm install
    )
)
cd ..

:: 下载后端依赖
echo.
echo [执行] 下载后端依赖...
cd backend
call mvn dependency:resolve -q
cd ..

echo.
echo ==========================================
echo 安装完成!
echo ==========================================
echo.
echo 启动方式:
echo   1. 启动后端: cd backend ^&^& mvn spring-boot:run
echo   2. 启动前端: cd frontend ^&^& npm run dev
echo.
echo 或者使用一键启动脚本:
echo   start.bat
echo.
echo 默认测试账号: admin / 123456
echo ==========================================
pause
