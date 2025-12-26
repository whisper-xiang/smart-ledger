Write-Host "==== 开始安装开发环境 ====" -ForegroundColor Green

# -----------------------------
# 1. Node.js v18+
# -----------------------------
Write-Host "`n[1/4] 安装 Node.js (v18 LTS)..." -ForegroundColor Cyan
winget install --id OpenJS.NodeJS.LTS -e

# -----------------------------
# 2. Java JDK v17+
# -----------------------------
Write-Host "`n[2/4] 安装 Java JDK 17 (Temurin)..." -ForegroundColor Cyan
winget install --id EclipseAdoptium.Temurin.17.JDK -e

# -----------------------------
# 3. Apache Maven v3.6+
# -----------------------------
Write-Host "`n[3/4] 安装 Apache Maven..." -ForegroundColor Cyan
winget install --id Apache.Maven -e

# -----------------------------
# 4. MySQL v8.0+
# -----------------------------
Write-Host "`n[4/4] 安装 MySQL Server 8.0..." -ForegroundColor Cyan
winget install --id Oracle.MySQL -e

# -----------------------------
# 刷新环境变量
# -----------------------------
Write-Host "`n刷新环境变量..." -ForegroundColor Yellow
$env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" +
            [System.Environment]::GetEnvironmentVariable("Path","User")

# -----------------------------
# 验证安装
# -----------------------------
Write-Host "`n==== 安装结果验证 ====" -ForegroundColor Green

Write-Host "`nNode.js:"
node -v

Write-Host "`nJava:"
java -version

Write-Host "`nMaven:"
mvn -v

Write-Host "`nMySQL:"
mysql --version

Write-Host "`n🎉 所有环境安装完成！如有问题，建议重启电脑。" -ForegroundColor Green