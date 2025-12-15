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

 Date: 15/12/2025 16:53:43
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
  `borrow_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '借阅时间',
  `expected_return_time` datetime NOT NULL COMMENT '预计归还时间（=借阅时间+可借天数）',
  `actual_return_time` datetime NULL DEFAULT NULL COMMENT '实际归还时间（null-未归还）',
  `renew_count` tinyint NULL DEFAULT 0 COMMENT '续借次数',
  `renew_days` int NULL DEFAULT 0 COMMENT '累计续借天数',
  `borrow_status` tinyint NOT NULL COMMENT '借阅状态（0-借阅中，1-已归还，2-已超时，3-归还待确认）',
  `operation_type` tinyint NOT NULL COMMENT '操作类型：1-借阅，2-续借，3-归还',
  `return_apply_time` datetime NULL DEFAULT NULL COMMENT '读者申请归还时间',
  `return_confirm_status` tinyint NULL DEFAULT 0 COMMENT '归还确认状态（0-待确认，1-已确认，仅管理员操作）',
  `confirm_admin_id` bigint NULL DEFAULT NULL COMMENT '确认管理员id（关联sys_user表，return_confirm_status=1时必填）',
  `confirm_time` datetime NULL DEFAULT NULL COMMENT '确认时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`borrow_id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  INDEX `book_id`(`book_id` ASC) USING BTREE,
  INDEX `confirm_admin_id`(`confirm_admin_id` ASC) USING BTREE,
  CONSTRAINT `book_borrow_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `book_borrow_ibfk_2` FOREIGN KEY (`book_id`) REFERENCES `book_info` (`book_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `book_borrow_ibfk_3` FOREIGN KEY (`confirm_admin_id`) REFERENCES `sys_user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of book_borrow
-- ----------------------------
INSERT INTO `book_borrow` VALUES (1, 2, 1, '2025-12-08 11:19:41', '2025-12-23 11:19:41', '2025-12-10 10:31:17', 0, 0, 1, 3, '2025-12-09 23:27:05', 1, 1, '2025-12-10 10:31:17', '2025-12-08 11:19:41', '2025-12-09 23:27:05');
INSERT INTO `book_borrow` VALUES (2, 2, 2, '2025-12-08 11:19:49', '2025-12-23 11:19:49', '2025-12-10 10:35:31', 0, 0, 1, 3, '2025-12-09 23:36:13', 1, 1, '2025-12-10 10:35:31', '2025-12-08 11:19:49', '2025-12-09 23:36:13');
INSERT INTO `book_borrow` VALUES (3, 3, 2, '2025-12-08 11:20:53', '2026-01-07 11:20:53', NULL, 0, 0, 0, 1, NULL, 0, NULL, NULL, '2025-12-08 11:20:53', '2025-12-08 11:20:53');
INSERT INTO `book_borrow` VALUES (4, 2, 3, '2025-12-09 23:54:13', '2025-12-24 23:54:13', NULL, 0, 0, 3, 3, '2025-12-09 23:55:03', 0, NULL, NULL, '2025-12-09 23:54:13', '2025-12-09 23:54:13');
INSERT INTO `book_borrow` VALUES (5, 2, 4, '2025-12-09 23:54:20', '2026-01-07 23:54:20', NULL, 1, 14, 0, 2, NULL, 0, NULL, NULL, '2025-12-09 23:54:20', '2025-12-09 23:54:20');
INSERT INTO `book_borrow` VALUES (6, 3, 3, '2025-12-10 08:24:53', '2026-01-09 08:24:53', NULL, 0, 0, 0, 1, NULL, 0, NULL, NULL, '2025-12-10 08:24:53', '2025-12-10 08:24:53');
INSERT INTO `book_borrow` VALUES (7, 3, 4, '2025-12-10 08:24:57', '2026-01-09 08:24:57', NULL, 0, 0, 0, 1, NULL, 0, NULL, NULL, '2025-12-10 08:24:57', '2025-12-10 08:24:57');
INSERT INTO `book_borrow` VALUES (8, 4, 4, '2025-12-10 08:46:09', '2026-03-10 08:46:09', NULL, 1, 30, 0, 2, NULL, 0, NULL, NULL, '2025-12-10 08:46:09', '2025-12-10 08:46:09');
INSERT INTO `book_borrow` VALUES (9, 4, 3, '2025-12-10 08:46:21', '2026-03-10 08:46:21', NULL, 1, 30, 0, 2, NULL, 0, NULL, NULL, '2025-12-10 08:46:21', '2025-12-10 08:46:21');
INSERT INTO `book_borrow` VALUES (10, 4, 1, '2025-12-11 10:04:09', '2026-02-09 10:04:09', NULL, 0, 0, 0, 1, NULL, 0, NULL, NULL, '2025-12-11 10:04:09', '2025-12-11 10:04:09');

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
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`category_id`) USING BTREE,
  UNIQUE INDEX `category_code`(`category_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of book_category
-- ----------------------------
INSERT INTO `book_category` VALUES (1, 'A', '马克思主义、列宁主义、毛泽东思想、邓小平理论', 0, 1, '2025-12-07 20:48:45', '2025-12-07 20:48:45');
INSERT INTO `book_category` VALUES (2, 'B', '哲学、宗教', 0, 2, '2025-12-07 20:48:45', '2025-12-07 20:48:45');
INSERT INTO `book_category` VALUES (3, 'C', '社会科学总论', 0, 3, '2025-12-07 20:48:45', '2025-12-07 20:48:45');
INSERT INTO `book_category` VALUES (4, 'D', '政治、法律', 0, 4, '2025-12-07 20:48:45', '2025-12-07 20:48:45');
INSERT INTO `book_category` VALUES (5, 'E', '军事', 0, 5, '2025-12-07 20:48:45', '2025-12-07 20:48:45');
INSERT INTO `book_category` VALUES (6, 'F', '经济', 0, 6, '2025-12-07 20:48:45', '2025-12-07 20:48:45');
INSERT INTO `book_category` VALUES (7, 'G', '文化、科学、教育、体育', 0, 7, '2025-12-07 20:48:45', '2025-12-07 20:48:45');
INSERT INTO `book_category` VALUES (8, 'H', '语言、文字', 0, 8, '2025-12-07 20:48:45', '2025-12-07 20:48:45');
INSERT INTO `book_category` VALUES (9, 'I', '文学', 0, 9, '2025-12-07 20:48:45', '2025-12-07 20:48:45');
INSERT INTO `book_category` VALUES (10, 'J', '艺术', 0, 10, '2025-12-07 20:48:45', '2025-12-07 20:48:45');
INSERT INTO `book_category` VALUES (11, 'K', '历史、地理', 0, 11, '2025-12-07 20:48:45', '2025-12-07 20:48:45');
INSERT INTO `book_category` VALUES (12, 'N', '自然科学总论', 0, 12, '2025-12-07 20:48:45', '2025-12-07 20:48:45');
INSERT INTO `book_category` VALUES (13, 'O', '数理科学和化学', 0, 13, '2025-12-07 20:48:45', '2025-12-07 20:48:45');
INSERT INTO `book_category` VALUES (14, 'P', '天文学、地球科学', 0, 14, '2025-12-07 20:48:45', '2025-12-07 20:48:45');
INSERT INTO `book_category` VALUES (15, 'Q', '生物科学', 0, 15, '2025-12-07 20:48:45', '2025-12-07 20:48:45');
INSERT INTO `book_category` VALUES (16, 'R', '医药、卫生', 0, 16, '2025-12-07 20:48:45', '2025-12-07 20:48:45');
INSERT INTO `book_category` VALUES (17, 'S', '农业科学', 0, 17, '2025-12-07 20:48:45', '2025-12-07 20:48:45');
INSERT INTO `book_category` VALUES (18, 'T', '工业技术', 0, 18, '2025-12-07 20:48:45', '2025-12-07 20:48:45');
INSERT INTO `book_category` VALUES (19, 'U', '交通运输', 0, 19, '2025-12-07 20:48:45', '2025-12-07 20:48:45');
INSERT INTO `book_category` VALUES (20, 'V', '航空、航天', 0, 20, '2025-12-07 20:48:45', '2025-12-07 20:48:45');
INSERT INTO `book_category` VALUES (21, 'X', '环境科学、安全科学', 0, 21, '2025-12-07 20:48:45', '2025-12-07 20:48:45');
INSERT INTO `book_category` VALUES (22, 'Z', '综合性图书', 0, 22, '2025-12-07 20:48:45', '2025-12-07 20:48:45');

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
  `book_status` tinyint NOT NULL COMMENT '书籍状态（0-草稿未发布，1-非草稿未发布，2-待上架，3-可借阅，4-已借光）',
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
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除（0=未删除，1=已删除）',
  PRIMARY KEY (`book_id`) USING BTREE,
  UNIQUE INDEX `isbn`(`isbn` ASC) USING BTREE,
  INDEX `category_id`(`category_id` ASC) USING BTREE,
  CONSTRAINT `book_info_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `book_category` (`category_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `book_info_chk_1` CHECK (`total_count` between 1 and 999),
  CONSTRAINT `book_info_chk_2` CHECK (`publish_count` between 1 and 999)
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of book_info
-- ----------------------------
INSERT INTO `book_info` VALUES (1, '容智杰', 'https://loremflickr.com/2995/1681?lock=4424350047259353', '缑明', 'ut dolore pariatur fugiat', 2, 3, 34, 32, '2025-12-08 11:19:07', '九则每其千少天。制根马论集。报示青感强确花细。', 'magna labore et consectetur nostrud', '978-0-251-97250-9', 'deserunt Ut ullamco ipsum', 8, 'est', 'ullamco ex aliquip ea laboris', 'exercitation', '2026-07-02', 860.75, 2, '2025-12-08 11:09:45', '2025-12-11 10:04:09', 0);
INSERT INTO `book_info` VALUES (2, 'test251208-1', 'https://loremflickr.com/2995/1681?lock=4424350047259353', '张三', 'ut dolore pariatur fugiat', 10, 4, 2, 0, '2025-12-08 11:19:11', '这是一段简介', 'magna labore et consectetur nostrud', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 2, '2025-12-08 11:12:03', '2025-12-08 11:20:53', 0);
INSERT INTO `book_info` VALUES (3, '计算机网络', NULL, '王五', NULL, 13, 3, 34, 31, '2025-12-09 23:50:29', '计算机网络简介。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 3, '2025-12-09 23:48:55', '2025-12-10 08:46:21', 0);
INSERT INTO `book_info` VALUES (4, '操作系统', NULL, '李四', NULL, 13, 3, 34, 31, '2025-12-09 23:50:33', '操作系统简介。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 3, '2025-12-09 23:49:36', '2025-12-10 08:46:09', 0);

-- ----------------------------
-- Table structure for book_operation_log
-- ----------------------------
DROP TABLE IF EXISTS `book_operation_log`;
CREATE TABLE `book_operation_log`  (
  `log_id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志id',
  `user_id` bigint NOT NULL COMMENT '用户id（关联sys_user表）',
  `book_id` bigint NOT NULL COMMENT '书籍id（关联book_info表）',
  `operation_type` tinyint NOT NULL COMMENT '操作类型：1-预约，2-取消预约，3-借阅，4-续借，5-归还',
  `operation_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `operation_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`log_id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  INDEX `book_id`(`book_id` ASC) USING BTREE,
  CONSTRAINT `book_operation_log_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `book_operation_log_ibfk_2` FOREIGN KEY (`book_id`) REFERENCES `book_info` (`book_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of book_operation_log
-- ----------------------------
INSERT INTO `book_operation_log` VALUES (1, 2, 1, 3, '2025-12-08 11:19:41', '借阅图书《容智杰》，借阅天数：15天', '2025-12-08 11:19:41');
INSERT INTO `book_operation_log` VALUES (2, 2, 2, 3, '2025-12-08 11:19:49', '借阅图书《test251208-1》，借阅天数：15天', '2025-12-08 11:19:49');
INSERT INTO `book_operation_log` VALUES (3, 3, 2, 3, '2025-12-08 11:20:53', '借阅图书《test251208-1》，借阅天数：30天', '2025-12-08 11:20:53');
INSERT INTO `book_operation_log` VALUES (4, 2, 1, 5, '2025-12-09 23:27:05', '用户申请归还书籍', '2025-12-09 23:27:05');
INSERT INTO `book_operation_log` VALUES (5, 2, 2, 5, '2025-12-09 23:36:13', '用户申请归还书籍，借阅记录ID：2', '2025-12-09 23:36:13');
INSERT INTO `book_operation_log` VALUES (6, 2, 3, 3, '2025-12-09 23:54:13', '借阅图书《计算机网络》，借阅天数：15天', '2025-12-09 23:54:13');
INSERT INTO `book_operation_log` VALUES (7, 2, 4, 3, '2025-12-09 23:54:20', '借阅图书《操作系统》，借阅天数：15天', '2025-12-09 23:54:20');
INSERT INTO `book_operation_log` VALUES (8, 2, 3, 5, '2025-12-09 23:55:03', 'user申请归还书籍《计算机网络》', '2025-12-09 23:55:03');
INSERT INTO `book_operation_log` VALUES (9, 2, 4, 4, '2025-12-10 08:21:15', 'user续借书籍《操作系统》，续借14天，借阅记录ID：5', '2025-12-10 08:21:15');
INSERT INTO `book_operation_log` VALUES (10, 3, 3, 3, '2025-12-10 08:24:53', '借阅图书《计算机网络》，借阅天数：30天', '2025-12-10 08:24:53');
INSERT INTO `book_operation_log` VALUES (11, 3, 4, 3, '2025-12-10 08:24:57', '借阅图书《操作系统》，借阅天数：30天', '2025-12-10 08:24:57');
INSERT INTO `book_operation_log` VALUES (12, 4, 4, 3, '2025-12-10 08:46:09', '借阅图书《操作系统》，借阅天数：60天', '2025-12-10 08:46:09');
INSERT INTO `book_operation_log` VALUES (13, 4, 3, 3, '2025-12-10 08:46:21', '借阅图书《计算机网络》，借阅天数：60天', '2025-12-10 08:46:21');
INSERT INTO `book_operation_log` VALUES (14, 4, 4, 4, '2025-12-10 08:47:59', 'teacher续借书籍《操作系统》，续借30天', '2025-12-10 08:47:59');
INSERT INTO `book_operation_log` VALUES (15, 4, 3, 4, '2025-12-10 08:47:59', 'teacher续借书籍《计算机网络》，续借30天', '2025-12-10 08:47:59');
INSERT INTO `book_operation_log` VALUES (16, 1, 1, 5, '2025-12-10 10:31:17', '管理员(ID:1)确认归还用户【user】的书籍《容智杰》', '2025-12-10 10:31:17');
INSERT INTO `book_operation_log` VALUES (17, 1, 2, 5, '2025-12-10 10:35:31', '管理员(ID:1)确认归还用户【user】的书籍《test251208-1》', '2025-12-10 10:35:31');
INSERT INTO `book_operation_log` VALUES (18, 4, 1, 3, '2025-12-11 10:04:09', '借阅图书《容智杰》，借阅天数：60天', '2025-12-11 10:04:09');

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
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`preview_id`) USING BTREE,
  INDEX `book_id`(`book_id` ASC) USING BTREE,
  CONSTRAINT `book_preview_ibfk_1` FOREIGN KEY (`book_id`) REFERENCES `book_info` (`book_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of book_preview
-- ----------------------------

-- ----------------------------
-- Table structure for book_renew
-- ----------------------------
DROP TABLE IF EXISTS `book_renew`;
CREATE TABLE `book_renew`  (
  `renew_id` bigint NOT NULL AUTO_INCREMENT COMMENT '续借id',
  `borrow_id` bigint NOT NULL COMMENT '借阅id（关联book_borrow表）',
  `user_id` bigint NOT NULL COMMENT '用户id（关联sys_user表）',
  `renew_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '续借时间',
  `renew_days` int NOT NULL COMMENT '续借天数（根据角色，如学生20天、老师30天）',
  `before_return_time` datetime NOT NULL COMMENT '续借前预计归还时间',
  `after_return_time` datetime NOT NULL COMMENT '续借后预计归还时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`renew_id`) USING BTREE,
  INDEX `borrow_id`(`borrow_id` ASC) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `book_renew_ibfk_1` FOREIGN KEY (`borrow_id`) REFERENCES `book_borrow` (`borrow_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `book_renew_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of book_renew
-- ----------------------------
INSERT INTO `book_renew` VALUES (1, 5, 2, '2025-12-10 08:21:15', 14, '2025-12-24 23:54:20', '2026-01-07 23:54:20', '2025-12-10 08:21:15');
INSERT INTO `book_renew` VALUES (2, 8, 4, '2025-12-10 08:47:59', 30, '2026-02-08 08:46:09', '2026-03-10 08:46:09', '2025-12-10 08:47:59');
INSERT INTO `book_renew` VALUES (3, 9, 4, '2025-12-10 08:47:59', 30, '2026-02-08 08:46:21', '2026-03-10 08:46:21', '2025-12-10 08:47:59');

-- ----------------------------
-- Table structure for book_reservation
-- ----------------------------
DROP TABLE IF EXISTS `book_reservation`;
CREATE TABLE `book_reservation`  (
  `reservation_id` bigint NOT NULL AUTO_INCREMENT COMMENT '预约id',
  `user_id` bigint NOT NULL COMMENT '用户id（关联sys_user表）',
  `book_id` bigint NOT NULL COMMENT '书籍id（关联book_info表）',
  `reservation_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '预约时间',
  `reservation_status` tinyint NOT NULL COMMENT '预约状态（0-预约中，1-可借阅，2-已取消，3-已失效）',
  `invalid_time` datetime NULL DEFAULT NULL COMMENT '失效时间（如书籍上架后未借阅则失效）',
  `remind_status` tinyint NULL DEFAULT 0 COMMENT '提醒状态（0-未提醒，1-已提醒）',
  `remind_time` datetime NULL DEFAULT NULL COMMENT '提醒时间（书籍上架/有库存时）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`reservation_id`) USING BTREE,
  UNIQUE INDEX `uk_user_book`(`user_id` ASC, `book_id` ASC, `reservation_status` ASC) USING BTREE COMMENT '同一用户对同一书籍不可重复预约（预约中/可借阅状态）',
  INDEX `book_id`(`book_id` ASC) USING BTREE,
  CONSTRAINT `book_reservation_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `book_reservation_ibfk_2` FOREIGN KEY (`book_id`) REFERENCES `book_info` (`book_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of book_reservation
-- ----------------------------

-- ----------------------------
-- Table structure for file_info
-- ----------------------------
DROP TABLE IF EXISTS `file_info`;
CREATE TABLE `file_info`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '文件ID',
  `original_filename` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '原始文件名',
  `filename` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '存储的文件名',
  `file_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件存储路径',
  `size` bigint NOT NULL COMMENT '文件大小（字节）',
  `content_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件类型',
  `download_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '下载URL',
  `preview_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '预览URL',
  `upload_time` datetime NOT NULL COMMENT '上传时间',
  `upload_user_id` bigint NULL DEFAULT NULL COMMENT '上传用户ID',
  `upload_username` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '上传用户名',
  `md5` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件MD5值',
  `sha256` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件SHA256值',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件描述',
  `category` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件分类',
  `tags` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件标签',
  `is_public` tinyint(1) NULL DEFAULT 1 COMMENT '是否公开（0=私有，1=公开）',
  `download_count` int NULL DEFAULT 0 COMMENT '下载次数',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态（0=禁用，1=正常）',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除（0=未删除，1=已删除）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_filename`(`filename` ASC) USING BTREE,
  INDEX `idx_upload_user`(`upload_user_id` ASC) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE,
  INDEX `idx_upload_time`(`upload_time` ASC) USING BTREE,
  INDEX `idx_md5`(`md5` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of file_info
-- ----------------------------
INSERT INTO `file_info` VALUES (1, '封面.docx', '2bdabf95-dd78-4b2c-9fb3-dab64c454237.docx', 'C:\\Users\\WuHongyan/bbms_project/uploads/2bdabf95-dd78-4b2c-9fb3-dab64c454237.docx', 209587, 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', '/api/file/download/2bdabf95-dd78-4b2c-9fb3-dab64c454237.docx', '/api/file/preview/2bdabf95-dd78-4b2c-9fb3-dab64c454237.docx', '2025-12-15 16:24:53', NULL, NULL, NULL, NULL, NULL, '文档', NULL, 1, 0, 1, 0, '2025-12-15 16:24:53', '2025-12-15 16:24:53');
INSERT INTO `file_info` VALUES (2, '选题.png', 'e02d4ddd-c98f-4adf-9de2-bcb7ce26f22f.png', 'C:\\Users\\WuHongyan/bbms_project/uploads/e02d4ddd-c98f-4adf-9de2-bcb7ce26f22f.png', 152219, 'image/png', '/api/file/download/e02d4ddd-c98f-4adf-9de2-bcb7ce26f22f.png', '/api/file/preview/e02d4ddd-c98f-4adf-9de2-bcb7ce26f22f.png', '2025-12-15 16:35:17', NULL, NULL, NULL, NULL, NULL, '图片', NULL, 1, 0, 1, 0, '2025-12-15 16:35:17', '2025-12-15 16:35:17');
INSERT INTO `file_info` VALUES (3, '项目相关信息.txt', '项目相关信息.txt', 'C:\\Users\\WuHongyan/bbms_project/uploads/e8d51bd5-76cc-4140-ae5a-6327cdeb27c4.txt', 694, 'text/plain', '/api/file/download/e8d51bd5-76cc-4140-ae5a-6327cdeb27c4.txt', '/api/file/preview/e8d51bd5-76cc-4140-ae5a-6327cdeb27c4.txt', '2025-12-15 16:47:28', NULL, NULL, NULL, NULL, NULL, '文档', NULL, 1, 0, 1, 0, '2025-12-15 16:47:29', '2025-12-15 16:47:29');
INSERT INTO `file_info` VALUES (4, 'SpringMVC组小组分工.xlsx', '207941b6-1a14-4c22-82b2-bc8087671b75.xlsx', 'C:\\Users\\WuHongyan/bbms_project/uploads/SpringMVC组小组分工.xlsx', 6337, 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', '/api/file/download/SpringMVC组小组分工.xlsx', '/api/file/preview/SpringMVC组小组分工.xlsx', '2025-12-15 16:50:04', NULL, NULL, NULL, NULL, NULL, '文档', NULL, 1, 0, 1, 0, '2025-12-15 16:50:04', '2025-12-15 16:50:04');

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
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1, 0, '借阅中心', 'borrow:center', 'borrow', '/borrow', 'layout/index.vue', '0', 'Reading', NULL, 1, '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_menu` VALUES (2, 1, '新书推荐', 'borrow:newBooks', 'newBooks', '/borrow/newBooks', 'views/borrow/NewBooks/Index.vue', '1', 'Star', '借阅中心', 1, '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_menu` VALUES (3, 1, '图书借阅', 'borrow:bookBorrow', 'bookBorrow', '/borrow/bookBorrow', 'views/borrow/BookBorrow/Index.vue', '1', 'Notebook', '借阅中心', 2, '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_menu` VALUES (4, 1, '创建书籍', 'borrow:bookCreate', 'bookCreate', '/borrow/BookBorrow/BookCreate', 'views/borrow/BookBorrow/BookCreate.vue', '2', 'Plus', '借阅中心', 0, '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_menu` VALUES (5, 1, '编辑书籍', 'borrow:bookEdit', 'bookEdit', '/borrow/BookBorrow/BookEdit/:id', 'views/borrow/BookBorrow/BookCreate.vue', '2', 'Edit', '借阅中心', 0, '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_menu` VALUES (6, 1, '书籍详情', 'borrow:bookDetail', 'bookDetail', '/borrow/BookBorrow/BookDetail/:id', 'views/borrow/BookBorrow/BookDetail.vue', '2', 'Document', '借阅中心', 0, '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_menu` VALUES (7, 1, '当前借阅', 'borrow:currentBorrow', 'currentBorrow', '/borrow/currentBorrow', 'views/borrow/CurrentBorrow/index.vue', '1', 'Collection', '借阅中心', 3, '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_menu` VALUES (8, 1, '当前归还', 'borrow:currentReturn', 'currentReturn', '/borrow/currentReturn', 'views/borrow/CurrentReturn/index.vue', '1', 'Collection', '借阅中心', 4, '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_menu` VALUES (9, 1, '借阅记录', 'borrow:borrowRecord', 'borrowRecord', '/borrow/borrowRecord', 'views/borrow/BorrowRecord/index.vue', '1', 'Document', '借阅中心', 5, '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_menu` VALUES (10, 0, '管理中心', 'manage:center', 'manage', '/manage', 'layout/index.vue', '0', 'Setting', NULL, 2, '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_menu` VALUES (11, 10, '个人中心', 'manage:personalCenter', 'personalCenter', '/manage/personalCenter', 'views/manage/PersonalCenter/index.vue', '1', 'User', '管理中心', 1, '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_menu` VALUES (12, 10, '消息列表', 'manage:messageList', 'messageList', '/manage/messageList', 'views/manage/MessageList/index.vue', '1', 'ChatDotRound', '管理中心', 2, '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_menu` VALUES (13, 10, '用户列表', 'manage:userList', 'userList', '/manage/userList', 'views/manage/UserList/index.vue', '1', 'UserFilled', '管理中心', 3, '2025-12-07 20:48:44', '2025-12-07 20:48:44');

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
  `send_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`message_id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `sys_message_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_message
-- ----------------------------

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
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, 'READER_SOCIAL', '社会人员', 5, 15, 5, '社会人员借阅权限', '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_role` VALUES (2, 'READER_STUDENT', '学生', 20, 30, 20, '学生借阅权限', '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_role` VALUES (3, 'READER_TEACHER', '老师', 50, 60, 30, '老师借阅权限', '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_role` VALUES (4, 'ADMIN', '管理员', NULL, NULL, NULL, '系统管理权限', '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_role` VALUES (5, 'SYS_ADMIN', '系统管理员', NULL, NULL, NULL, '超级管理员权限', '2025-12-07 20:48:44', '2025-12-07 20:48:44');

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
) ENGINE = InnoDB AUTO_INCREMENT = 47 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES (1, 1, 1, 'admin', '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_role_menu` VALUES (2, 1, 2, 'admin', '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_role_menu` VALUES (3, 1, 3, 'admin', '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_role_menu` VALUES (4, 1, 7, 'admin', '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_role_menu` VALUES (5, 1, 9, 'admin', '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_role_menu` VALUES (6, 1, 10, 'admin', '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_role_menu` VALUES (7, 1, 11, 'admin', '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_role_menu` VALUES (8, 1, 12, 'admin', '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_role_menu` VALUES (9, 2, 1, 'admin', '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_role_menu` VALUES (10, 2, 2, 'admin', '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_role_menu` VALUES (11, 2, 3, 'admin', '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_role_menu` VALUES (12, 2, 7, 'admin', '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_role_menu` VALUES (13, 2, 9, 'admin', '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_role_menu` VALUES (14, 2, 10, 'admin', '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_role_menu` VALUES (15, 2, 11, 'admin', '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_role_menu` VALUES (16, 2, 12, 'admin', '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_role_menu` VALUES (17, 3, 1, 'admin', '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_role_menu` VALUES (18, 3, 2, 'admin', '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_role_menu` VALUES (19, 3, 3, 'admin', '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_role_menu` VALUES (20, 3, 7, 'admin', '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_role_menu` VALUES (21, 3, 9, 'admin', '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_role_menu` VALUES (22, 3, 10, 'admin', '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_role_menu` VALUES (23, 3, 11, 'admin', '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_role_menu` VALUES (24, 3, 12, 'admin', '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_role_menu` VALUES (25, 4, 1, 'admin', '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_role_menu` VALUES (26, 4, 2, 'admin', '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_role_menu` VALUES (27, 4, 3, 'admin', '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_role_menu` VALUES (28, 4, 4, 'admin', '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_role_menu` VALUES (29, 4, 5, 'admin', '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_role_menu` VALUES (30, 4, 6, 'admin', '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_role_menu` VALUES (31, 4, 8, 'admin', '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_role_menu` VALUES (32, 4, 9, 'admin', '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_role_menu` VALUES (33, 4, 10, 'admin', '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_role_menu` VALUES (34, 4, 12, 'admin', '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_role_menu` VALUES (35, 4, 13, 'admin', '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_role_menu` VALUES (36, 5, 1, 'sys_admin', '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_role_menu` VALUES (37, 5, 2, 'sys_admin', '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_role_menu` VALUES (38, 5, 3, 'sys_admin', '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_role_menu` VALUES (39, 5, 4, 'sys_admin', '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_role_menu` VALUES (40, 5, 5, 'sys_admin', '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_role_menu` VALUES (41, 5, 6, 'sys_admin', '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_role_menu` VALUES (42, 5, 8, 'sys_admin', '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_role_menu` VALUES (43, 5, 9, 'sys_admin', '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_role_menu` VALUES (44, 5, 10, 'sys_admin', '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_role_menu` VALUES (45, 5, 12, 'sys_admin', '2025-12-07 20:48:44', '2025-12-07 20:48:44');
INSERT INTO `sys_role_menu` VALUES (46, 5, 13, 'sys_admin', '2025-12-07 20:48:44', '2025-12-07 20:48:44');

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
  `account_status` tinyint NULL DEFAULT 1 COMMENT '账号状态（0-冻结，1-正常，2-停用，3-注销）',
  `freeze_time` datetime NULL DEFAULT NULL COMMENT '冻结开始时间',
  `unfreeze_time` datetime NULL DEFAULT NULL COMMENT '冻结结束时间',
  `login_error_count` tinyint NULL DEFAULT 0 COMMENT '登录错误次数（最大5次）',
  `current_borrow_count` int NULL DEFAULT 0 COMMENT '当前借阅数量',
  `current_reserve_count` int NULL DEFAULT 0 COMMENT '当前预约数量',
  `register_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `last_login_time` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `account`(`account` ASC) USING BTREE,
  UNIQUE INDEX `uid`(`uid` ASC) USING BTREE,
  INDEX `role_id`(`role_id` ASC) USING BTREE,
  CONSTRAINT `sys_user_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`role_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', 'admin', 'WOQNqTJfVImWV3s0NgAl3A==:fSk/tmec2p+Ik1c5PWTSdWonWdj4Xh4vhm/FNmJTITM=', 5, 'U17651117344301425', NULL, 100, 1, NULL, NULL, 0, 0, 0, '2025-12-07 20:48:54', '2025-12-15 10:19:10', '2025-12-07 20:48:54', '2025-12-15 10:19:10');
INSERT INTO `sys_user` VALUES (2, 'user', 'user', 'a8XMlJPQyofoOT8wiliOHw==:P3pJbkr3eNMs/tvaKQE7cv66yEILTxqZkCoRzu92kIo=', 4, 'U17651227299965145', NULL, 100, 1, NULL, NULL, 0, 0, 0, '2025-12-07 23:52:10', '2025-12-14 16:43:41', '2025-12-07 23:52:10', '2025-12-14 16:43:41');
INSERT INTO `sys_user` VALUES (3, 'student', 'student', 'fDjIcM8C5fbDQpfKWBP5bw==:WmvMLyno3xroHEkBUXotfHznWw8RxVkv3PAVbTHcH+M=', 2, 'U17651640388962146', NULL, 100, 1, NULL, NULL, 0, 0, 0, '2025-12-08 11:20:39', '2025-12-15 10:47:38', '2025-12-08 11:20:39', '2025-12-15 10:47:38');
INSERT INTO `sys_user` VALUES (4, 'teacher', 'teacher', 'IuFqBOGkYzfby186axY2uw==:n0gy0LsI5c93/Dj6MEY7XyEHjAG+AY+8CJwRtVWlxcw=', 3, 'U17653275581594518', NULL, 100, 1, NULL, NULL, 0, 1, 0, '2025-12-10 08:45:58', '2025-12-11 10:03:26', '2025-12-10 08:45:58', '2025-12-11 10:03:26');
INSERT INTO `sys_user` VALUES (5, 'social', 'social', 'N25CWubVHMI21HwhS4fpFQ==:xsTdo1sdekD9+U9cuTNd4tM/A4wRALBsNpLsWXlrISk=', 4, 'U17657646348166858', NULL, 100, 1, NULL, NULL, 0, 0, 0, '2025-12-15 10:10:35', NULL, '2025-12-15 10:10:35', '2025-12-15 10:10:35');
INSERT INTO `sys_user` VALUES (6, 'test1', 'test1', 'YpxDEaIoIb3fSuU5/2YpbQ==:wo6p5enxzL6zTFJ5NnVfhGMtfaMrHRpYsUb6iQ2MDgY=', 4, 'U17657651446871538', NULL, 100, 1, NULL, NULL, 0, 0, 0, '2025-12-15 10:19:05', NULL, '2025-12-15 10:19:05', '2025-12-15 10:19:05');

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `user_role_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户id（关联sys_user表）',
  `role_id` bigint NOT NULL COMMENT '角色id（关联sys_role表）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`user_role_id`) USING BTREE,
  UNIQUE INDEX `uk_user_role`(`user_id` ASC, `role_id` ASC) USING BTREE,
  INDEX `role_id`(`role_id` ASC) USING BTREE,
  CONSTRAINT `sys_user_role_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `sys_user_role_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`role_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1, 5, '2025-12-07 20:48:54');
INSERT INTO `sys_user_role` VALUES (3, 3, 2, '2025-12-08 11:20:39');
INSERT INTO `sys_user_role` VALUES (4, 4, 3, '2025-12-10 08:45:58');
INSERT INTO `sys_user_role` VALUES (6, 2, 4, '2025-12-15 00:05:35');
INSERT INTO `sys_user_role` VALUES (8, 5, 4, '2025-12-15 10:11:23');
INSERT INTO `sys_user_role` VALUES (10, 6, 4, '2025-12-15 10:19:23');

-- ----------------------------
-- Table structure for user_credit_history
-- ----------------------------
DROP TABLE IF EXISTS `user_credit_history`;
CREATE TABLE `user_credit_history`  (
  `history_id` bigint NOT NULL AUTO_INCREMENT COMMENT '历史id',
  `user_id` bigint NOT NULL COMMENT '用户id（关联sys_user表）',
  `change_value` int NOT NULL COMMENT '分数变化值（正数为增加，负数为减少）',
  `current_score` int NOT NULL COMMENT '变更后分数',
  `change_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '变更原因（如\"按时归还书籍\"、\"借阅超时\"等）',
  `change_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '变更时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`history_id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `user_credit_history_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_credit_history
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
