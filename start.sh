#!/bin/bash

# Smart Ledger 项目一键启动脚本
# 使用方法: chmod +x start.sh && ./start.sh

set -e

echo "=========================================="
echo "   Smart Ledger 智能记账系统 - 启动脚本"
echo "=========================================="

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

# 获取脚本所在目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# 清理函数
cleanup() {
    echo -e "\n${YELLOW}正在停止服务...${NC}"
    if [ ! -z "$BACKEND_PID" ]; then
        kill $BACKEND_PID 2>/dev/null || true
    fi
    if [ ! -z "$FRONTEND_PID" ]; then
        kill $FRONTEND_PID 2>/dev/null || true
    fi
    echo -e "${GREEN}服务已停止${NC}"
    exit 0
}

# 捕获 Ctrl+C
trap cleanup SIGINT SIGTERM

# 启动后端
echo -e "\n${GREEN}>>> 启动后端服务...${NC}"
cd backend
mvn spring-boot:run -q &
BACKEND_PID=$!
cd ..

# 等待后端启动
echo "等待后端启动..."
sleep 10

# 检查后端是否启动成功
if ! kill -0 $BACKEND_PID 2>/dev/null; then
    echo -e "${RED}后端启动失败，请检查日志${NC}"
    exit 1
fi
echo -e "${GREEN}后端已启动 (PID: $BACKEND_PID)${NC}"

# 启动前端
echo -e "\n${GREEN}>>> 启动前端服务...${NC}"
cd frontend
if command -v pnpm &> /dev/null; then
    pnpm dev &
elif command -v yarn &> /dev/null; then
    yarn dev &
else
    npm run dev &
fi
FRONTEND_PID=$!
cd ..

# 等待前端启动
sleep 5

echo ""
echo "=========================================="
echo -e "${GREEN}服务启动完成!${NC}"
echo "=========================================="
echo ""
echo "访问地址:"
echo "  前端: http://localhost:5173"
echo "  后端: http://localhost:8080"
echo ""
echo "测试账号: admin / 123456"
echo ""
echo "按 Ctrl+C 停止所有服务"
echo "=========================================="

# 等待子进程
wait
