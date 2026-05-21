-- =====================================================
-- 校园失物招领管理系统 数据库初始化脚本
-- 数据库: campus_lost_found
-- 字符集: utf8mb4
-- =====================================================

CREATE DATABASE IF NOT EXISTS campus_lost_found DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE campus_lost_found;

-- -----------------------------------------------------
-- 用户表: 存储系统用户信息
-- 关联关系: 一对多 -> 物品表、认领表、留言表
-- -----------------------------------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码（BCrypt加密）',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `role` TINYINT NOT NULL DEFAULT 0 COMMENT '角色: 0普通用户, 1管理员',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0禁用, 1正常',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- -----------------------------------------------------
-- 分类表: 物品分类信息
-- 关联关系: 一对多 -> 物品表
-- -----------------------------------------------------
DROP TABLE IF EXISTS `t_category`;
CREATE TABLE `t_category` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `icon` VARCHAR(255) DEFAULT NULL COMMENT '分类图标',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序序号',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0禁用, 1正常',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类表';

-- -----------------------------------------------------
-- 物品表: 失物招领信息
-- 关联关系: 多对一 -> 用户表、分类表; 一对多 -> 认领表、留言表
-- -----------------------------------------------------
DROP TABLE IF EXISTS `t_item`;
CREATE TABLE `t_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '物品ID',
    `user_id` BIGINT NOT NULL COMMENT '发布用户ID',
    `category_id` BIGINT DEFAULT NULL COMMENT '分类ID（AI自动分类）',
    `title` VARCHAR(100) NOT NULL COMMENT '物品名称',
    `description` TEXT NOT NULL COMMENT '物品描述',
    `images` VARCHAR(1000) DEFAULT NULL COMMENT '图片URL列表（逗号分隔）',
    `location` VARCHAR(200) NOT NULL COMMENT '拾获/丢失地点',
    `contact_info` VARCHAR(200) DEFAULT NULL COMMENT '联系方式',
    `item_type` TINYINT NOT NULL DEFAULT 0 COMMENT '类型: 0失物(捡到), 1寻物(丢失)',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0待审核, 1已通过, 2已拒绝, 3已认领',
    `ai_category` VARCHAR(100) DEFAULT NULL COMMENT 'AI分类结果',
    `ai_review_result` VARCHAR(500) DEFAULT NULL COMMENT 'AI审核结果',
    `ai_review_pass` TINYINT DEFAULT NULL COMMENT 'AI审核是否通过: 0不通过, 1通过',
    `found_time` DATETIME DEFAULT NULL COMMENT '拾获/丢失时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_status` (`status`),
    KEY `idx_item_type` (`item_type`),
    KEY `idx_create_time` (`create_time`),
    CONSTRAINT `fk_item_user` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_item_category` FOREIGN KEY (`category_id`) REFERENCES `t_category` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物品表';

-- -----------------------------------------------------
-- 认领表: 失物认领申请记录
-- 关联关系: 多对一 -> 物品表、用户表
-- -----------------------------------------------------
DROP TABLE IF EXISTS `t_claim`;
CREATE TABLE `t_claim` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '认领ID',
    `item_id` BIGINT NOT NULL COMMENT '物品ID',
    `user_id` BIGINT NOT NULL COMMENT '认领用户ID',
    `reason` VARCHAR(500) NOT NULL COMMENT '认领理由/证明',
    `contact_info` VARCHAR(200) NOT NULL COMMENT '联系方式',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0待审核, 1已通过, 2已拒绝',
    `review_remark` VARCHAR(500) DEFAULT NULL COMMENT '审核备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_item_id` (`item_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    CONSTRAINT `fk_claim_item` FOREIGN KEY (`item_id`) REFERENCES `t_item` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_claim_user` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='认领表';

-- -----------------------------------------------------
-- 留言表: 物品留言互动
-- 关联关系: 多对一 -> 物品表、用户表
-- -----------------------------------------------------
DROP TABLE IF EXISTS `t_message`;
CREATE TABLE `t_message` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '留言ID',
    `item_id` BIGINT NOT NULL COMMENT '物品ID',
    `user_id` BIGINT NOT NULL COMMENT '留言用户ID',
    `content` VARCHAR(500) NOT NULL COMMENT '留言内容',
    `parent_id` BIGINT DEFAULT NULL COMMENT '父留言ID（回复功能）',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_item_id` (`item_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_parent_id` (`parent_id`),
    CONSTRAINT `fk_message_item` FOREIGN KEY (`item_id`) REFERENCES `t_item` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_message_user` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='留言表';

-- -----------------------------------------------------
-- 公告表: 系统公告信息
-- 关联关系: 多对一 -> 用户表
-- -----------------------------------------------------
DROP TABLE IF EXISTS `t_announcement`;
CREATE TABLE `t_announcement` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '公告ID',
    `title` VARCHAR(200) NOT NULL COMMENT '公告标题',
    `content` TEXT NOT NULL COMMENT '公告内容',
    `user_id` BIGINT NOT NULL COMMENT '发布管理员ID',
    `is_top` TINYINT NOT NULL DEFAULT 0 COMMENT '是否置顶: 0否, 1是',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0草稿, 1已发布',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    CONSTRAINT `fk_announcement_user` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告表';

-- =====================================================
-- 初始化数据
-- =====================================================

-- 管理员账户（密码: admin123）
INSERT INTO `t_user` (`username`, `password`, `nickname`, `role`, `status`) VALUES
('admin', 'admin123', '系统管理员', 1, 1);

-- 测试用户（密码: 123456）
INSERT INTO `t_user` (`username`, `password`, `nickname`, `phone`, `role`, `status`) VALUES
('zhangsan', '123456', '张三', '13800138001', 0, 1),
('lisi', '123456', '李四', '13800138002', 0, 1);

-- 物品分类
INSERT INTO `t_category` (`name`, `icon`, `sort_order`) VALUES
('证件卡类', 'id-card', 1),
('钥匙钱包', 'key', 2),
('电子产品', 'laptop', 3),
('书籍文具', 'book', 4),
('衣物配饰', 'shirt', 5),
('水杯雨伞', 'cup', 6),
('玩具', 'gift', 7),
('其他物品', 'box', 8);

-- 测试物品数据
INSERT INTO `t_item` (`user_id`, `category_id`, `title`, `description`, `location`, `contact_info`, `item_type`, `status`, `ai_category`, `ai_review_pass`, `ai_review_result`, `found_time`) VALUES
(2, 1, '校园卡', '在教学楼3栋201教室捡到一张校园卡，姓名模糊不清', '教学楼3栋201教室', '13800138001', 0, 1, '证件卡类', 1, '内容安全，通过审核', '2026-04-28 14:30:00'),
(2, 2, '黑色钱包', '在食堂二楼捡到一个黑色皮质钱包，内有少量现金', '第二食堂二楼', '13800138001', 0, 1, '钥匙钱包', 1, '内容安全，通过审核', '2026-04-29 12:00:00'),
(3, 3, 'AirPods耳机', '丢失一副白色AirPods Pro耳机，带有个性化贴纸', '图书馆三楼自习区', '13800138002', 1, 1, '电子产品', 1, '内容安全，通过审核', '2026-04-30 09:00:00');

-- 测试公告
INSERT INTO `t_announcement` (`title`, `content`, `user_id`, `is_top`, `status`) VALUES
('欢迎使用校园失物招领系统', '本系统旨在帮助师生快速发布和查找失物信息，请如实填写物品信息，共同维护校园诚信环境。', 1, 1, 1),
('关于物品认领流程说明', '认领物品时请提供详细的证明信息，管理员将在1-3个工作日内完成审核。', 1, 0, 1);
