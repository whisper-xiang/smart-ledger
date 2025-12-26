#!/bin/bash

# Smart Ledger 项目一键安装启动脚本
# 使用方法: chmod +x setup.sh && ./setup.sh

set -e

echo "=========================================="
echo "   Smart Ledger 智能记账系统 - 安装脚本"
echo "=========================================="

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 检查命令是否存在
check_command() {
    if ! command -v $1 &> /dev/null; then
        echo -e "${RED}错误: $1 未安装，请先安装 $1${NC}"
        exit 1
    fi
}

# 打印步骤
print_step() {
    echo -e "\n${GREEN}>>> $1${NC}"
}

# 打印警告
print_warning() {
    echo -e "${YELLOW}警告: $1${NC}"
}

# 检查必要的依赖
print_step "检查系统依赖..."
check_command node
check_command npm
check_command java
check_command mvn
check_command mysql

echo "Node 版本: $(node -v)"
echo "NPM 版本: $(npm -v)"
echo "Java 版本: $(java -version 2>&1 | head -n 1)"
echo "Maven 版本: $(mvn -v | head -n 1)"

# 获取脚本所在目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# 数据库配置
DB_HOST="localhost"
DB_PORT="3306"
DB_NAME="smart_ledger"
DB_USER="root"
DB_PASS=""

# 读取数据库密码
print_step "配置数据库..."
read -sp "请输入 MySQL root 密码 (直接回车表示无密码): " DB_PASS
echo ""

# 测试数据库连接
print_step "测试数据库连接..."
if [ -z "$DB_PASS" ]; then
    MYSQL_CMD="mysql -u$DB_USER"
else
    MYSQL_CMD="mysql -u$DB_USER -p$DB_PASS"
fi

if ! $MYSQL_CMD -e "SELECT 1" &> /dev/null; then
    echo -e "${RED}错误: 无法连接到 MySQL，请检查密码是否正确${NC}"
    exit 1
fi
echo "数据库连接成功!"

# 初始化数据库
print_step "初始化数据库..."
$MYSQL_CMD < backend/src/main/resources/schema.sql
echo "数据库初始化完成!"

# 更新后端数据库配置
print_step "更新后端配置..."
if [ -n "$DB_PASS" ]; then
    sed -i.bak "s/password: .*/password: $DB_PASS/" backend/src/main/resources/application.yml
    rm -f backend/src/main/resources/application.yml.bak
fi

# 安装前端依赖
print_step "安装前端依赖..."
cd frontend
if command -v pnpm &> /dev/null; then
    pnpm install
elif command -v yarn &> /dev/null; then
    yarn install
else
    npm install
fi
cd ..

# 安装后端依赖
print_step "下载后端依赖..."
cd backend
mvn dependency:resolve -q
cd ..

echo ""
echo "=========================================="
echo -e "${GREEN}安装完成!${NC}"
echo "=========================================="
echo ""
echo "启动方式:"
echo "  1. 启动后端: cd backend && mvn spring-boot:run"
echo "  2. 启动前端: cd frontend && npm run dev"
echo ""
echo "或者使用一键启动脚本:"
echo "  ./start.sh"
echo ""
echo "默认测试账号: admin / 123456"
echo "=========================================="
