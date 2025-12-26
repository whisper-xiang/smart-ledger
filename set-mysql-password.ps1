param (
    [string]$MySQLPassword = "123456"
)

Write-Host "==== MySQL 初始化 & 设置 root 密码 ====" -ForegroundColor Green

# MySQL 服务名（一般是 MySQL 或 MySQL80）
$serviceName = "MySQL80"

# 启动 MySQL 服务
Write-Host "启动 MySQL 服务..." -ForegroundColor Cyan
Start-Service -Name $serviceName -ErrorAction SilentlyContinue

# 等待服务启动
Start-Sleep -Seconds 5

# 初始化（无密码）
Write-Host "初始化 MySQL（无密码模式）..." -ForegroundColor Cyan
mysqld --initialize-insecure --console

# 重启服务
Restart-Service -Name $serviceName
Start-Sleep -Seconds 5

# 设置 root 密码
Write-Host "设置 root 密码..." -ForegroundColor Cyan
mysql -u root -e "ALTER USER 'root'@'localhost' IDENTIFIED BY '$MySQLPassword'; FLUSH PRIVILEGES;"

# 验证
Write-Host "验证 MySQL 登录..." -ForegroundColor Yellow
mysql -u root -p$MySQLPassword -e "SELECT VERSION();"

Write-Host "🎉 MySQL root 密码设置完成！" -ForegroundColor Green