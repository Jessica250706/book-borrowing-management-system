/*
 Navicat Premium Dump SQL

 Source Server         : 127.0.0.1
 Source Server Type    : MySQL
 Source Server Version : 80039 (8.0.39)
 Source Host           : localhost:3306
 Source Schema         : book

 Target Server Type    : MySQL
 Target Server Version : 80039 (8.0.39)
 File Encoding         : 65001

 Date: 11/11/2025 23:34:58
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for book_borrow
-- ----------------------------
DROP TABLE IF EXISTS `book_borrow`;
CREATE TABLE `book_borrow`  (
  `borrow_id` bigint NOT NULL AUTO_INCREMENT COMMENT '借阅id',
  `user_id` bigint NOT NULL COMMENT '用户id（关联sys_user表）',
  `book_id` bigint NOT NULL COMMENT '书籍id（关联book_info表）',
  `borrow_time` datetime NOT NULL COMMENT '借阅时间',
  `expected_return_time` datetime NOT NULL COMMENT '预计归还时间（=借阅时间+可借天数）',
  `actual_return_time` datetime NULL DEFAULT NULL COMMENT '实际归还时间（null-未归还）',
  `renew_count` tinyint NULL DEFAULT 0 COMMENT '续借次数（预留，当前需求未明确次数限制）',
  `renew_days` int NULL DEFAULT 0 COMMENT '累计续借天数',
  `borrow_status` tinyint NOT NULL COMMENT '借阅状态（0-借阅中，1-已归还，2-已超时）',
  `return_confirm_status` tinyint NULL DEFAULT 0 COMMENT '归还确认状态（0-待确认，1-已确认，仅管理员操作）',
  `confirm_admin_id` bigint NULL DEFAULT NULL COMMENT '确认管理员id（关联sys_user表，return_confirm_status=1时必填）',
  `confirm_time` datetime NULL DEFAULT NULL COMMENT '确认时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`borrow_id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  INDEX `book_id`(`book_id` ASC) USING BTREE,
  INDEX `confirm_admin_id`(`confirm_admin_id` ASC) USING BTREE,
  CONSTRAINT `book_borrow_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `book_borrow_ibfk_2` FOREIGN KEY (`book_id`) REFERENCES `book_info` (`book_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `book_borrow_ibfk_3` FOREIGN KEY (`confirm_admin_id`) REFERENCES `sys_user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for book_category
-- ----------------------------
DROP TABLE IF EXISTS `book_category`;
CREATE TABLE `book_category`  (
  `category_id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类id',
  `category_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类编码（如A、B、C）',
  `category_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称（如马克思主义、列宁主义、毛泽东思想、邓小平理论）',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父分类id（0-一级分类，预留多级分类扩展）',
  `order_num` int NULL DEFAULT 0 COMMENT '排序序号',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`category_id`) USING BTREE,
  UNIQUE INDEX `category_code`(`category_code` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for book_info
-- ----------------------------
DROP TABLE IF EXISTS `book_info`;
CREATE TABLE `book_info`  (
  `book_id` bigint NOT NULL AUTO_INCREMENT COMMENT '书籍id',
  `book_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '书籍名称（1-50字符）',
  `cover_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '书籍封面URL（1:1.42比例，格式jpg/png/svg/webp）',
  `author` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '作者（含国籍，1-30字符）',
  `translator` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '译者（1-30字符）',
  `category_id` bigint NOT NULL COMMENT '分类id（关联book_category表）',
  `book_status` tinyint NOT NULL COMMENT '书籍状态（0-未发布，1-待上架，2-可借阅，3-已借光）',
  `total_count` int NOT NULL COMMENT '书籍总数（1-999本）',
  `available_count` int NOT NULL DEFAULT 0 COMMENT '可借数量（=总数-已借数量）',
  `shelf_time` datetime NOT NULL COMMENT '上架时间（格式yyyy-MM-dd HH:mm:ss）',
  `intro` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '书籍简介（1-300字）',
  `publisher` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '出版社',
  `isbn` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'ISBN编号',
  `copyright_holder` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '版权持有方',
  `publish_count` int NULL DEFAULT NULL COMMENT '发行数量（1-999）',
  `publish_unit` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '发行单位',
  `publish_website` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '发行网站',
  `publish_batch` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '发行批次',
  `publish_date` date NULL DEFAULT NULL COMMENT '发行时间（格式yyyy-MM-dd）',
  `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '数字图书定价（保留两位小数）',
  `borrow_count` int NULL DEFAULT 0 COMMENT '累计借阅次数',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`book_id`) USING BTREE,
  UNIQUE INDEX `isbn`(`isbn` ASC) USING BTREE,
  INDEX `category_id`(`category_id` ASC) USING BTREE,
  CONSTRAINT `book_info_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `book_category` (`category_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `book_info_chk_1` CHECK (`total_count` between 1 and 999),
  CONSTRAINT `book_info_chk_2` CHECK (`publish_count` between 1 and 999)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for book_preview
-- ----------------------------
DROP TABLE IF EXISTS `book_preview`;
CREATE TABLE `book_preview`  (
  `preview_id` bigint NOT NULL AUTO_INCREMENT COMMENT '预览id',
  `book_id` bigint NOT NULL COMMENT '书籍id（关联book_info表）',
  `file_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '预览文件名称',
  `file_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件类型（如jpg、png、pdf）',
  `file_size` bigint NOT NULL COMMENT '文件大小（单位字节）',
  `file_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '预览文件URL',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`preview_id`) USING BTREE,
  INDEX `book_id`(`book_id` ASC) USING BTREE,
  CONSTRAINT `book_preview_ibfk_1` FOREIGN KEY (`book_id`) REFERENCES `book_info` (`book_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for book_renew
-- ----------------------------
DROP TABLE IF EXISTS `book_renew`;
CREATE TABLE `book_renew`  (
  `renew_id` bigint NOT NULL AUTO_INCREMENT COMMENT '续借id',
  `borrow_id` bigint NOT NULL COMMENT '借阅id（关联book_borrow表）',
  `user_id` bigint NOT NULL COMMENT '用户id（关联sys_user表）',
  `renew_time` datetime NOT NULL COMMENT '续借时间',
  `renew_days` int NOT NULL COMMENT '续借天数（根据角色，如学生20天、老师30天）',
  `before_return_time` datetime NOT NULL COMMENT '续借前预计归还时间',
  `after_return_time` datetime NOT NULL COMMENT '续借后预计归还时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`renew_id`) USING BTREE,
  INDEX `borrow_id`(`borrow_id` ASC) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `book_renew_ibfk_1` FOREIGN KEY (`borrow_id`) REFERENCES `book_borrow` (`borrow_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `book_renew_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for book_reservation
-- ----------------------------
DROP TABLE IF EXISTS `book_reservation`;
CREATE TABLE `book_reservation`  (
  `reservation_id` bigint NOT NULL AUTO_INCREMENT COMMENT '预约id',
  `user_id` bigint NOT NULL COMMENT '用户id（关联sys_user表）',
  `book_id` bigint NOT NULL COMMENT '书籍id（关联book_info表）',
  `reservation_time` datetime NOT NULL COMMENT '预约时间',
  `reservation_status` tinyint NOT NULL COMMENT '预约状态（0-待生效，1-已生效，2-已取消，3-已失效）',
  `invalid_time` datetime NULL DEFAULT NULL COMMENT '失效时间（如书籍上架后未借阅则失效）',
  `remind_status` tinyint NULL DEFAULT 0 COMMENT '提醒状态（0-未提醒，1-已提醒）',
  `remind_time` datetime NULL DEFAULT NULL COMMENT '提醒时间（书籍上架/有库存时）',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`reservation_id`) USING BTREE,
  UNIQUE INDEX `uk_user_book`(`user_id` ASC, `book_id` ASC, `reservation_status` ASC) USING BTREE COMMENT '同一用户对同一书籍不可重复预约（待生效/已生效状态）',
  INDEX `book_id`(`book_id` ASC) USING BTREE,
  CONSTRAINT `book_reservation_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `book_reservation_ibfk_2` FOREIGN KEY (`book_id`) REFERENCES `book_info` (`book_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `menu_id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单id',
  `parent_id` bigint NOT NULL DEFAULT 0 COMMENT '父级id（0表示一级菜单，对应需求中\"客户端-基本业务模块/系统基础功能模块\"）',
  `title` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '菜单名称（需与需求中模块名称一致，如\"登录\"\"新书推荐\"\"当前借阅\"）',
  `code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '权限字段（用于角色权限控制，如\"login:access\"\"book:borrow\"\"book:create\"）',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '路由名称（唯一标识路由，如\"Login\"\"NewBookRecommend\"\"CurrentBorrow\"）',
  `path` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '路由path（对应页面访问路径，如\"/login\"\"/borrow-center/new-book\"）',
  `url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '组件路径（对应前端组件文件路径，如\"views/login/Login.vue\"）',
  `type` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '类型(0-目录，如\"借阅中心\"\"管理中心\"；1-菜单，如\"新书推荐\"\"当前借阅\"；2-按钮，如\"创建书籍\"\"批量归还\")',
  `icon` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图标（可选，如\"el-icon-s-tools\"，用于目录/菜单显示）',
  `parent_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '上级菜单名称（冗余字段，便于前端显示，如\"借阅中心\"的子菜单\"新书推荐\"，parent_name为\"借阅中心\"）',
  `order_num` int NOT NULL DEFAULT 0 COMMENT '序号（控制菜单显示顺序，如需求中\"新书推荐\"在\"书籍详情\"之前）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（默认当前时间）',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间（修改时自动更新）',
  PRIMARY KEY (`menu_id`) USING BTREE,
  UNIQUE INDEX `code`(`code` ASC) USING BTREE,
  UNIQUE INDEX `name`(`name` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_message
-- ----------------------------
DROP TABLE IF EXISTS `sys_message`;
CREATE TABLE `sys_message`  (
  `message_id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息id',
  `user_id` bigint NOT NULL COMMENT '接收用户id（关联sys_user表）',
  `message_type` tinyint NOT NULL COMMENT '消息类型（1-预约提醒，2-归还提醒，3-上架提醒，4-账号冻结提醒）',
  `message_title` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '消息标题',
  `message_content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '消息内容',
  `read_status` tinyint NULL DEFAULT 0 COMMENT '阅读状态（0-未读，1-已读）',
  `read_time` datetime NULL DEFAULT NULL COMMENT '阅读时间',
  `send_time` datetime NOT NULL COMMENT '发送时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`message_id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `sys_message_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `role_id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色id',
  `role_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色编码（唯一标识角色，对应需求中角色：READER_SOCIAL-社会人员，READER_STUDENT-学生，READER_TEACHER-老师，ADMIN-管理员，SYS_ADMIN-系统管理员）',
  `role_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称（与需求一致：社会人员、学生、老师、管理员、系统管理员）',
  `max_borrow_num` int NULL DEFAULT NULL COMMENT '最大可借阅本数（对应需求1.4：社会人员5本，学生20本，老师50本，管理员/系统管理员无此限制则为null）',
  `max_borrow_days` int NULL DEFAULT NULL COMMENT '最大可借阅天数（对应需求1.4：社会人员15天，学生30天，老师60天，管理员/系统管理员无则为null）',
  `max_renew_days` int NULL DEFAULT NULL COMMENT '最大可续借天数（对应需求1.4：社会人员5天，学生20天，老师30天，管理员/系统管理员无则为null）',
  `remark` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注（补充角色权限说明，如\"系统管理员：比管理员多修改账号身份的权限\"）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`role_id`) USING BTREE,
  UNIQUE INDEX `role_code`(`role_code` ASC) USING BTREE,
  UNIQUE INDEX `role_name`(`role_name` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `role_menu_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `role_id` bigint NOT NULL COMMENT '角色id（关联sys_role表）',
  `menu_id` bigint NOT NULL COMMENT '菜单id（关联sys_menu表）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人（记录谁分配的权限，如\"admin\"\"sys_admin\"）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（权限分配时间）',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间（权限修改时间）',
  PRIMARY KEY (`role_menu_id`) USING BTREE,
  UNIQUE INDEX `uk_role_menu`(`role_id` ASC, `menu_id` ASC) USING BTREE COMMENT '唯一约束：同一角色不能重复关联同一菜单',
  INDEX `menu_id`(`menu_id` ASC) USING BTREE,
  CONSTRAINT `sys_role_menu_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`role_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `sys_role_menu_ibfk_2` FOREIGN KEY (`menu_id`) REFERENCES `sys_menu` (`menu_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户昵称',
  `account` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录账号（手机号/邮箱）',
  `password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '加密后的密码',
  `role_id` bigint NOT NULL COMMENT '角色id（关联sys_role表）',
  `uid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户唯一标识（用于展示）',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户头像URL',
  `credit_score` int NULL DEFAULT 100 COMMENT '信誉分（默认100分）',
  `account_status` tinyint NULL DEFAULT 1 COMMENT '账号状态（0-冻结，1-正常）',
  `freeze_time` datetime NULL DEFAULT NULL COMMENT '冻结开始时间',
  `unfreeze_time` datetime NULL DEFAULT NULL COMMENT '冻结结束时间',
  `login_error_count` tinyint NULL DEFAULT 0 COMMENT '登录错误次数（最大5次）',
  `register_time` datetime NOT NULL COMMENT '注册时间',
  `last_login_time` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `account`(`account` ASC) USING BTREE,
  UNIQUE INDEX `uid`(`uid` ASC) USING BTREE,
  INDEX `role_id`(`role_id` ASC) USING BTREE,
  CONSTRAINT `sys_user_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`role_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `user_role_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户id（关联sys_user表）',
  `role_id` bigint NOT NULL COMMENT '角色id（关联sys_role表）',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`user_role_id`) USING BTREE,
  UNIQUE INDEX `uk_user_role`(`user_id` ASC, `role_id` ASC) USING BTREE,
  INDEX `role_id`(`role_id` ASC) USING BTREE,
  CONSTRAINT `sys_user_role_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `sys_user_role_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`role_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
