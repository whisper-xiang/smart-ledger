-- 创建数据库
CREATE DATABASE IF NOT EXISTS smart_ledger DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE smart_ledger;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `email` VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `currency` VARCHAR(10) DEFAULT 'CNY' COMMENT '默认货币',
    `monthly_budget` DECIMAL(12,2) DEFAULT 5000.00 COMMENT '每月预算',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '是否删除 0-否 1-是'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 账单表
CREATE TABLE IF NOT EXISTS `bill` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '账单ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `type` VARCHAR(20) NOT NULL COMMENT '类型 income-收入 expense-支出',
    `category` VARCHAR(50) NOT NULL COMMENT '分类',
    `amount` DECIMAL(12,2) NOT NULL COMMENT '金额',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '描述',
    `bill_date` DATE NOT NULL COMMENT '账单日期',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '是否删除 0-否 1-是',
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_bill_date` (`bill_date`),
    INDEX `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='账单表';

-- 记账日记表
CREATE TABLE IF NOT EXISTS `diary` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日记ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `title` VARCHAR(100) NOT NULL COMMENT '标题',
    `content` TEXT NOT NULL COMMENT '内容',
    `mood` VARCHAR(20) DEFAULT NULL COMMENT '心情',
    `diary_date` DATE NOT NULL COMMENT '日记日期',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '是否删除 0-否 1-是',
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_diary_date` (`diary_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='记账日记表';

-- 存钱计划表
CREATE TABLE IF NOT EXISTS `saving_plan` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '计划ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `name` VARCHAR(100) NOT NULL COMMENT '计划名称',
    `goal_amount` DECIMAL(12,2) NOT NULL COMMENT '目标金额',
    `saved_amount` DECIMAL(12,2) DEFAULT 0.00 COMMENT '已存金额',
    `start_date` DATE NOT NULL COMMENT '开始日期',
    `end_date` DATE NOT NULL COMMENT '结束日期',
    `color` VARCHAR(20) DEFAULT '#409eff' COMMENT '主题颜色',
    `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '是否删除 0-否 1-是',
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='存钱计划表';

-- 插入测试用户 (密码: 123456)
-- 注意: 建议通过注册接口创建用户，或使用以下正确的BCrypt哈希
INSERT INTO `user` (`username`, `password`, `email`, `currency`, `monthly_budget`) VALUES
('admin', '$2a$10$EqKcp1WFKVQISheBxkVJceXr.8fzVw3y1Ls7FJzX6RQhYZpOHCYYG', 'admin@example.com', 'CNY', 10000.00)
ON DUPLICATE KEY UPDATE `username`=`username`;
