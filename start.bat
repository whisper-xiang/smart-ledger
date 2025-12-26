@echo off
chcp 65001 >nul
setlocal

echo ==========================================
echo    Smart Ledger 智能记账系统 - 启动脚本
echo    Windows 版本
echo ==========================================

:: 启动后端
echo.
echo [启动] 后端服务...
start "Smart Ledger Backend" cmd /c "cd backend && mvn spring-boot:run"

:: 等待后端启动
echo 等待后端启动 (10秒)...
timeout /t 10 /nobreak >nul

:: 启动前端
echo.
echo [启动] 前端服务...
start "Smart Ledger Frontend" cmd /c "cd frontend && npm run dev"

echo.
echo ==========================================
echo 服务启动中...
echo ==========================================
echo.
echo 访问地址:
echo   前端: http://localhost:5173
echo   后端: http://localhost:8080
echo.
echo 测试账号: admin / 123456
echo.
echo 关闭窗口可停止对应服务
echo ==========================================
pause
