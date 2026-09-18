/*
 Navicat Premium Data Transfer

 Source Server         : 127.0.0.1
 Source Server Type    : MySQL
 Source Server Version : 90001
 Source Host           : localhost:3306
 Source Schema         : book

 Target Server Type    : MySQL
 Target Server Version : 90001
 File Encoding         : 65001

 Date: 23/12/2025 12:10:53
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
  INDEX `user_id`(`user_id`) USING BTREE,
  INDEX `book_id`(`book_id`) USING BTREE,
  INDEX `confirm_admin_id`(`confirm_admin_id`) USING BTREE,
  CONSTRAINT `book_borrow_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `book_borrow_ibfk_2` FOREIGN KEY (`book_id`) REFERENCES `book_info` (`book_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `book_borrow_ibfk_3` FOREIGN KEY (`confirm_admin_id`) REFERENCES `sys_user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of book_borrow
-- ----------------------------
INSERT INTO `book_borrow` VALUES (18, 7, 39, '2025-12-22 18:42:23', '2026-01-06 18:42:23', '2025-12-22 19:16:28', 0, 0, 1, 3, '2025-12-22 19:15:47', 1, 1, '2025-12-22 19:16:28', '2025-12-22 18:42:23', '2025-12-22 18:42:23');
INSERT INTO `book_borrow` VALUES (19, 7, 38, '2025-12-22 18:42:27', '2026-01-06 18:42:27', '2025-12-22 19:45:15', 0, 0, 1, 3, '2025-12-22 19:44:53', 1, 1, '2025-12-22 19:45:15', '2025-12-22 18:42:27', '2025-12-22 18:42:27');
INSERT INTO `book_borrow` VALUES (20, 7, 26, '2025-12-22 18:42:31', '2026-01-11 18:42:31', '2025-12-22 20:22:27', 1, 5, 1, 3, '2025-12-22 20:17:58', 1, 1, '2025-12-22 20:22:27', '2025-12-22 18:42:31', '2025-12-22 18:42:31');
INSERT INTO `book_borrow` VALUES (21, 9, 39, '2025-12-22 18:59:07', '2026-02-20 18:59:07', '2025-12-22 19:18:30', 0, 0, 1, 3, '2025-12-22 19:18:07', 1, 1, '2025-12-22 19:18:30', '2025-12-22 18:59:07', '2025-12-22 18:59:07');
INSERT INTO `book_borrow` VALUES (22, 9, 38, '2025-12-22 18:59:27', '2026-02-20 18:59:27', '2025-12-23 11:04:00', 0, 0, 1, 3, '2025-12-23 10:07:16', 1, 1, '2025-12-23 11:04:00', '2025-12-22 18:59:27', '2025-12-22 18:59:27');
INSERT INTO `book_borrow` VALUES (23, 9, 26, '2025-12-22 18:59:34', '2026-02-20 18:59:34', NULL, 0, 0, 0, 1, NULL, 0, NULL, NULL, '2025-12-22 18:59:34', '2025-12-22 18:59:34');
INSERT INTO `book_borrow` VALUES (24, 10, 24, '2025-12-22 19:45:48', '2026-01-21 19:45:48', NULL, 0, 0, 3, 3, '2025-12-23 12:06:30', 0, NULL, NULL, '2025-12-22 19:45:48', '2025-12-22 19:45:48');
INSERT INTO `book_borrow` VALUES (25, 7, 23, '2025-12-22 19:46:38', '2026-01-06 19:46:38', '2025-12-22 20:22:27', 0, 0, 1, 3, '2025-12-22 20:17:55', 1, 1, '2025-12-22 20:22:27', '2025-12-22 19:46:38', '2025-12-22 19:46:38');
INSERT INTO `book_borrow` VALUES (26, 7, 19, '2025-12-22 19:46:42', '2026-01-06 19:46:42', '2025-12-22 20:22:27', 0, 0, 1, 3, '2025-12-22 20:17:56', 1, 1, '2025-12-22 20:22:27', '2025-12-22 19:46:42', '2025-12-22 19:46:42');
INSERT INTO `book_borrow` VALUES (27, 7, 25, '2025-12-22 19:47:02', '2026-01-06 19:47:02', '2025-12-22 20:22:27', 0, 0, 1, 3, '2025-12-22 20:00:33', 1, 1, '2025-12-22 20:22:27', '2025-12-22 19:47:02', '2025-12-22 19:47:02');
INSERT INTO `book_borrow` VALUES (28, 7, 16, '2025-12-22 19:47:25', '2026-01-06 19:47:25', '2025-12-22 20:23:20', 0, 0, 1, 3, '2025-12-22 20:22:49', 1, 1, '2025-12-22 20:23:20', '2025-12-22 19:47:25', '2025-12-22 19:47:25');
INSERT INTO `book_borrow` VALUES (29, 7, 24, '2025-12-22 20:21:21', '2026-01-06 20:21:21', '2025-12-22 20:23:20', 0, 0, 1, 3, '2025-12-22 20:22:54', 1, 1, '2025-12-22 20:23:20', '2025-12-22 20:21:21', '2025-12-22 20:21:21');
INSERT INTO `book_borrow` VALUES (30, 7, 4, '2025-12-22 20:21:52', '2026-01-06 20:21:52', '2025-12-22 20:37:22', 0, 0, 1, 3, '2025-12-22 20:29:51', 1, 1, '2025-12-22 20:37:22', '2025-12-22 20:21:52', '2025-12-22 20:21:52');
INSERT INTO `book_borrow` VALUES (31, 7, 19, '2025-12-22 20:28:27', '2026-01-11 20:28:27', '2025-12-22 20:37:22', 1, 5, 1, 3, '2025-12-22 20:30:41', 1, 1, '2025-12-22 20:37:22', '2025-12-22 20:28:27', '2025-12-22 20:28:27');
INSERT INTO `book_borrow` VALUES (32, 7, 14, '2025-12-22 20:30:22', '2026-01-11 20:30:22', '2025-12-22 20:37:22', 1, 5, 1, 3, '2025-12-22 20:30:41', 1, 1, '2025-12-22 20:37:22', '2025-12-22 20:30:22', '2025-12-22 20:30:22');
INSERT INTO `book_borrow` VALUES (33, 7, 22, '2025-12-22 20:31:51', '2026-01-06 20:31:51', '2025-12-22 20:37:22', 0, 0, 1, 3, '2025-12-22 20:37:07', 1, 1, '2025-12-22 20:37:22', '2025-12-22 20:31:51', '2025-12-22 20:31:51');
INSERT INTO `book_borrow` VALUES (34, 7, 17, '2025-12-22 20:40:07', '2026-01-06 20:40:07', '2025-12-22 20:50:20', 0, 0, 1, 3, '2025-12-22 20:42:16', 1, 1, '2025-12-22 20:50:20', '2025-12-22 20:40:07', '2025-12-22 20:40:07');
INSERT INTO `book_borrow` VALUES (35, 7, 11, '2025-12-22 20:41:50', '2026-01-06 20:41:50', '2025-12-22 20:50:20', 0, 0, 1, 3, '2025-12-22 20:42:16', 1, 1, '2025-12-22 20:50:20', '2025-12-22 20:41:50', '2025-12-22 20:41:50');
INSERT INTO `book_borrow` VALUES (36, 7, 10, '2025-12-22 20:41:53', '2026-01-11 20:41:53', '2025-12-22 21:19:34', 1, 5, 1, 3, '2025-12-22 21:19:08', 1, 1, '2025-12-22 21:19:34', '2025-12-22 20:41:53', '2025-12-22 20:41:53');
INSERT INTO `book_borrow` VALUES (37, 7, 8, '2025-12-22 20:42:01', '2026-01-11 20:42:01', '2025-12-23 11:04:00', 1, 5, 1, 3, '2025-12-23 10:05:51', 1, 1, '2025-12-23 11:04:00', '2025-12-22 20:42:01', '2025-12-22 20:42:01');
INSERT INTO `book_borrow` VALUES (38, 10, 38, '2025-12-23 10:15:46', '2026-01-22 10:15:46', NULL, 0, 0, 0, 1, NULL, 0, NULL, NULL, '2025-12-23 10:15:46', '2025-12-23 10:15:46');
INSERT INTO `book_borrow` VALUES (39, 10, 39, '2025-12-23 10:15:51', '2026-01-22 10:15:51', NULL, 0, 0, 0, 1, NULL, 0, NULL, NULL, '2025-12-23 10:15:51', '2025-12-23 10:15:51');
INSERT INTO `book_borrow` VALUES (40, 10, 26, '2025-12-23 10:17:45', '2026-01-22 10:17:45', NULL, 0, 0, 0, 1, NULL, 0, NULL, NULL, '2025-12-23 10:17:45', '2025-12-23 10:17:45');
INSERT INTO `book_borrow` VALUES (41, 7, 39, '2025-12-23 10:22:10', '2026-01-07 10:22:10', '2025-12-23 10:30:19', 0, 0, 1, 3, '2025-12-23 10:23:37', 1, 1, '2025-12-23 10:30:19', '2025-12-23 10:22:10', '2025-12-23 10:22:10');
INSERT INTO `book_borrow` VALUES (42, 7, 22, '2025-12-23 10:23:03', '2026-01-12 10:23:03', '2025-12-23 11:04:00', 1, 5, 1, 3, '2025-12-23 10:24:13', 1, 1, '2025-12-23 11:04:00', '2025-12-23 10:23:03', '2025-12-23 10:23:03');
INSERT INTO `book_borrow` VALUES (43, 7, 23, '2025-12-23 10:23:06', '2026-01-12 10:23:06', '2025-12-23 11:55:28', 1, 5, 1, 3, '2025-12-23 11:54:32', 1, 1, '2025-12-23 11:55:28', '2025-12-23 10:23:06', '2025-12-23 10:23:06');
INSERT INTO `book_borrow` VALUES (44, 7, 19, '2025-12-23 10:23:10', '2026-01-07 10:23:10', '2025-12-23 11:04:00', 0, 0, 1, 3, '2025-12-23 10:24:13', 1, 1, '2025-12-23 11:04:00', '2025-12-23 10:23:10', '2025-12-23 10:23:10');
INSERT INTO `book_borrow` VALUES (45, 7, 25, '2025-12-23 10:36:09', '2026-01-12 10:36:09', '2025-12-23 11:03:47', 1, 5, 1, 3, '2025-12-23 10:47:41', 1, 1, '2025-12-23 11:03:47', '2025-12-23 10:36:09', '2025-12-23 10:36:09');
INSERT INTO `book_borrow` VALUES (46, 7, 16, '2025-12-23 10:37:12', '2026-01-12 10:37:12', '2025-12-23 11:04:00', 1, 5, 1, 3, '2025-12-23 10:37:37', 1, 1, '2025-12-23 11:04:00', '2025-12-23 10:37:12', '2025-12-23 10:37:12');
INSERT INTO `book_borrow` VALUES (47, 7, 15, '2025-12-23 10:37:15', '2026-01-12 10:37:15', '2025-12-23 11:04:00', 1, 5, 1, 3, '2025-12-23 10:37:37', 1, 1, '2025-12-23 11:04:00', '2025-12-23 10:37:15', '2025-12-23 10:37:15');
INSERT INTO `book_borrow` VALUES (48, 7, 14, '2025-12-23 10:37:18', '2026-01-12 10:37:18', '2025-12-23 11:03:54', 1, 5, 1, 3, '2025-12-23 10:47:37', 1, 1, '2025-12-23 11:03:54', '2025-12-23 10:37:18', '2025-12-23 10:37:18');
INSERT INTO `book_borrow` VALUES (49, 7, 4, '2025-12-23 10:46:31', '2026-01-12 10:46:31', '2025-12-23 11:03:54', 1, 5, 1, 3, '2025-12-23 10:47:37', 1, 1, '2025-12-23 11:03:54', '2025-12-23 10:46:31', '2025-12-23 10:46:31');
INSERT INTO `book_borrow` VALUES (50, 7, 11, '2025-12-23 10:46:34', '2026-01-12 10:46:34', '2025-12-23 11:53:29', 1, 5, 1, 3, '2025-12-23 11:09:09', 1, 1, '2025-12-23 11:53:29', '2025-12-23 10:46:34', '2025-12-23 10:46:34');
INSERT INTO `book_borrow` VALUES (51, 7, 19, '2025-12-23 11:08:02', '2026-01-12 11:08:02', '2025-12-23 11:53:29', 1, 5, 1, 3, '2025-12-23 11:09:09', 1, 1, '2025-12-23 11:53:29', '2025-12-23 11:08:02', '2025-12-23 11:08:02');
INSERT INTO `book_borrow` VALUES (52, 9, 38, '2025-12-23 11:57:08', '2026-02-21 11:57:08', '2025-12-23 11:58:04', 0, 0, 1, 3, '2025-12-23 11:57:53', 1, 1, '2025-12-23 11:58:04', '2025-12-23 11:57:08', '2025-12-23 11:57:08');
INSERT INTO `book_borrow` VALUES (53, 9, 40, '2025-12-23 11:59:59', '2026-02-21 11:59:59', NULL, 0, 0, 0, 1, NULL, 0, NULL, NULL, '2025-12-23 11:59:59', '2025-12-23 11:59:59');
INSERT INTO `book_borrow` VALUES (54, 10, 40, '2025-12-23 12:00:10', '2026-01-22 12:00:10', NULL, 0, 0, 0, 1, NULL, 0, NULL, NULL, '2025-12-23 12:00:10', '2025-12-23 12:00:10');
INSERT INTO `book_borrow` VALUES (55, 9, 7, '2025-12-23 12:00:59', '2026-02-21 12:00:59', NULL, 0, 0, 0, 1, NULL, 0, NULL, NULL, '2025-12-23 12:00:59', '2025-12-23 12:00:59');
INSERT INTO `book_borrow` VALUES (56, 10, 7, '2025-12-23 12:01:10', '2026-01-22 12:01:10', '2025-12-23 12:02:03', 0, 0, 1, 3, '2025-12-23 12:01:47', 1, 1, '2025-12-23 12:02:03', '2025-12-23 12:01:10', '2025-12-23 12:01:10');
INSERT INTO `book_borrow` VALUES (57, 10, 7, '2025-12-23 12:03:16', '2026-01-22 12:03:16', NULL, 0, 0, 0, 1, NULL, 0, NULL, NULL, '2025-12-23 12:03:16', '2025-12-23 12:03:16');
INSERT INTO `book_borrow` VALUES (58, 9, 20, '2025-12-23 12:06:57', '2026-02-21 12:06:57', NULL, 0, 0, 3, 3, '2025-12-23 12:07:35', 0, NULL, NULL, '2025-12-23 12:06:57', '2025-12-23 12:06:57');
INSERT INTO `book_borrow` VALUES (59, 9, 19, '2025-12-23 12:07:55', '2026-02-21 12:07:55', NULL, 0, 0, 3, 3, '2025-12-23 12:08:01', 0, NULL, NULL, '2025-12-23 12:07:55', '2025-12-23 12:07:55');

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
  UNIQUE INDEX `category_code`(`category_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

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
  `author` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '作者（含国籍，1-30字符）',
  `translator` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '译者（1-30字符）',
  `category_id` bigint NOT NULL COMMENT '分类id（关联book_category表）',
  `book_status` tinyint NOT NULL COMMENT '书籍状态（0-草稿未发布，1-非草稿未发布，2-待上架，3-可借阅，4-已借光）',
  `total_count` int NULL DEFAULT NULL COMMENT '书籍总数（1-999本）',
  `available_count` int NOT NULL DEFAULT 0 COMMENT '可借数量（=总数-已借数量）',
  `shelf_time` datetime NULL DEFAULT NULL COMMENT '上架时间（格式yyyy-MM-dd HH:mm:ss）',
  `intro` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '书籍简介（1-300字）',
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
  UNIQUE INDEX `isbn`(`isbn`) USING BTREE,
  INDEX `category_id`(`category_id`) USING BTREE,
  CONSTRAINT `book_info_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `book_category` (`category_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 37 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of book_info
-- ----------------------------
INSERT INTO `book_info` VALUES (1, '活着', 'http://localhost:8089/file/活着.png', '余华', '', 9, 3, 10, 10, '2025-12-10 19:19:07', '余华的代表作，一部深刻描绘二十世纪中国普通农民命运起伏的史诗性小说。故事以主人公福贵漫长而多舛的一生为主线，通过他历经内战、土改、大跃进、文化大革命等重大社会变革，不断失去亲人、财富乃至最后仅剩的希望的悲惨遭遇，展现了生命在极端苦难面前的韧性。余华用冷静近乎残酷的笔触，揭示了命运的不可预测与人生的荒诞，但最终又在福贵与一头老牛孤独相伴的平淡中，升华为对“活着”本身意义的巨大肯定。该书以其直抵人心的力量，超越了具体的历史叙事，引发了全球读者关于生存、苦难与坚韧的普遍共鸣。', '111', '978-0-251-97250-9', 'deserunt Ut ullamco ipsum', 80, 'est', 'ullamco ex aliquip ea laboris', 'exercitation', '2024-01-02', 45.00, 0, '2025-12-08 11:09:45', '2025-12-22 18:44:44', 0);
INSERT INTO `book_info` VALUES (3, '中国经济专题', 'http://localhost:8089/file/中国经济专题.png', '林毅夫', NULL, 6, 3, 10, 10, '2025-12-23 10:54:11', '本书源自林毅夫教授在北京大学的经典课程，系统阐述了他基于中国与发展中国家实践所提出的经济发展理论框架。书中深入分析了中国自近代以来，特别是改革开放后所创造的“经济奇迹”背后的逻辑。林毅夫强调了“比较优势发展战略”的重要性，解释了为何遵循本国要素禀赋结构来发展产业是可持续增长的关键，并讨论了政府与市场在经济发展中的适当角色。同时，他也直面中国经济当前面临的挑战与未来走向。该书语言深入浅出，将复杂的经济学原理与中国鲜活的改革实践紧密结合，是理解当代中国经济发展模式与道路自信的重要理论读本。', NULL, NULL, NULL, 54, NULL, NULL, NULL, NULL, 0.00, 0, '2025-12-09 23:48:55', '2025-12-23 10:54:11', 0);
INSERT INTO `book_info` VALUES (4, '万历十五年', 'http://localhost:8089/file/万历十五年.png', '黄仁宇', NULL, 11, 3, 10, 10, '2025-12-22 16:16:27', '本书以“大历史观”的独特视角，选取明朝万历十五年（公元1587年）这个看似平淡无奇的年份作为切入点，深入剖析了晚明帝国的运行机制与深层危机。通过重点描绘万历皇帝、首辅张居正与申时行、清官海瑞、名将戚继光、思想家李贽等关键人物的命运与选择，黄仁宇生动揭示了在僵化的文官制度与道德化治理模式下，个人的努力如何被庞大的官僚体系所吞噬，技术上的管理无力如何导致一个王朝无可挽回地走向衰落。该书文笔流畅，叙事生动，结论深刻，极大地改变了普通读者对历史研究的刻板印象，成为解读中国传统社会治理逻辑的经典之作。', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0.00, 2, '2025-12-09 23:49:36', '2025-12-23 11:03:54', 0);
INSERT INTO `book_info` VALUES (5, '共产党宣言', 'http://localhost:8089/file/共产党宣言.png', '卡尔·马克思，弗里德里希·恩格斯', NULL, 1, 3, 20, 20, '2025-10-01 14:19:07', '这是国际共产主义运动的第一个纲领性文献，标志着马克思主义的诞生。它阐述了阶级斗争理论和资本主义的内在矛盾，号召\"全世界无产者，联合起来！\"，对世界历史进程产生了深远影响。', '人民出版社', '978-7-01-000001-1', '中央编译出版社', 123, '第一版', 'http://www.peoplepress.com', '2025-01', '2023-01-01', 45.50, 0, '2025-12-01 11:09:45', '2025-12-22 18:17:46', 0);
INSERT INTO `book_info` VALUES (6, '中国哲学简史', 'http://localhost:8089/file/中国哲学简史.png', '冯友兰', NULL, 2, 1, 15, 15, NULL, '本书是冯友兰先生为西方读者了解中国哲学而用英文写就的经典著作。它系统、精炼地阐述了从先秦诸子到宋明理学的中国哲学发展史，脉络清晰，深入浅出，是了解中国思想文化的入门必读书。', '北京大学出版社', '978-7-01-000002-2', '北京大学出版社', 134, '第一版', 'http://www.pup.cn', '2025-01', '2020-11-01', 38.00, 0, '2025-12-02 11:09:45', '2025-12-22 18:17:50', 0);
INSERT INTO `book_info` VALUES (7, '乡土中国', 'http://localhost:8089/file/乡土中国.png', '费孝通', NULL, 3, 4, 2, 0, '2025-12-23 19:00:18', '这是社会学大师费孝通的代表作。书中提出了\"差序格局\"、\"礼治秩序\"、\"熟人社会\"等核心概念，深刻剖析了中国传统社会的结构和伦理观念，是理解中国社会与文化底蕴的一把钥匙。', '生活·读书·新知三联书店', '978-7-01-000003-3', '生活·读书·新知三联书店', 134, '第一版', 'http://www.sdxjpc.com', '2025-01', '2020-12-03', 32.80, 3, '2025-12-03 11:09:45', '2025-12-23 12:03:16', 0);
INSERT INTO `book_info` VALUES (8, '论法的精神', 'http://localhost:8089/file/论法的精神.png', '孟德斯鸠', '张雁深', 4, 3, 8, 8, '2025-12-05 13:19:07', '这部政治哲学巨著系统讨论了政体分类、三权分立以及法律与地理、气候、宗教、风俗等因素的关系。它奠定了近代西方政治与法律理论发展的基础，对现代国家的制度设计产生了巨大影响。', '商务印书馆', '978-7-01-000004-4', '商务印书馆', 34, '第一版', 'http://www.cp.com.cn', '2025-01', '2023-12-04', 55.00, 1, '2025-12-04 11:09:45', '2025-12-23 11:04:00', 0);
INSERT INTO `book_info` VALUES (9, '孙子兵法', 'http://localhost:8089/file/孙子兵法.png', '孙武', NULL, 5, 2, 12, 12, NULL, '被誉为\"兵学圣典\"，是世界上最早的军事著作之一。它不仅阐述了战争规律和战略战术，其思想如\"知己知彼，百战不殆\"、\"不战而屈人之兵\"等，已广泛应用于政治、经济、管理等各个领域。', '中华书局', '978-7-01-000005-5', '中华书局', 34, '第一版', 'http://www.zhbc.com.cn', '2025-01', '2023-12-05', 25.00, 0, '2025-12-05 11:09:45', '2025-12-22 18:33:48', 0);
INSERT INTO `book_info` VALUES (10, '国富论', 'http://localhost:8089/file/国富论.png', '亚当·斯密', '郭大力', 6, 3, 20, 20, '2025-11-01 13:19:07', '本书是现代经济学的奠基之作。书中首次系统阐述了市场经济的基本原理，强调了分工、自由市场和\"看不见的手\"的作用，对西方国家的经济政策产生了深远影响。', '商务印书馆', '978-7-01-000006-6', '商务印书馆', 134, '第一版', 'http://www.cp.com.cn', '2025-01', '2022-12-06', 68.00, 1, '2025-12-06 11:09:45', '2025-12-22 21:19:33', 0);
INSERT INTO `book_info` VALUES (11, '什么是教育', 'http://localhost:8089/file/什么是教育.png', '卡尔·雅斯贝尔斯', '邹进', 7, 3, 21, 21, '2025-10-23 15:58:40', '这是一本深刻的教育哲学著作。雅斯贝尔斯探讨了教育的本质、目的和形式，强调教育是\"人对人的主体间灵肉交流活动\"，旨在通过文化的传递，促进人的自由生成和潜能的充分发展。', '生活·读书·新知三联书店', '978-7-01-000007-7', '生活·读书·新知三联书店', 123, '第一版', 'http://www.sdxjpc.com', '2025-01', '2022-12-07', 36.50, 2, '2025-12-07 11:09:45', '2025-12-23 11:53:29', 0);
INSERT INTO `book_info` VALUES (12, '语法讲义', 'http://localhost:8089/file/语法讲义.png', '朱德熙', NULL, 8, 2, 16, 16, NULL, '这是中国现代语言学领域的经典教材。本书以结构主义语言学理论为基础，对现代汉语的语法体系进行了系统、精辟的分析和阐述，观点鲜明，例证丰富，影响深远。', '商务印书馆', '978-7-01-000008-8', '商务印书馆', 123, '第一版', 'http://www.cp.com.cn', '2025-01', '2021-12-08', 28.00, 0, '2025-12-08 11:09:45', '2025-12-23 11:44:49', 0);
INSERT INTO `book_info` VALUES (13, '红楼梦', 'http://localhost:8089/file/红楼梦.png', '曹雪芹', '高鹗(续)', 9, 0, 9, 9, NULL, '中国古典四大名著之首，是一部具有世界影响力的章回体长篇小说。它以贾、史、王、薛四大家族的兴衰为背景，以贾宝玉、林黛玉、薛宝钗的爱情婚姻悲剧为主线，描绘了广阔的社会生活画卷，被誉为\"中国封建社会的百科全书\"。', '人民文学出版社', '978-7-01-000009-9', '人民文学出版社', 23, '第一版', 'http://www.rw-cn.com', '2025-01', '2023-12-09', 75.00, 0, '2025-12-09 11:09:45', '2025-12-23 11:49:00', 0);
INSERT INTO `book_info` VALUES (14, '艺术的故事', 'http://localhost:8089/file/艺术的故事.png', 'E.H. 贡布里希', '范景中', 10, 3, 12, 12, '2025-12-11 10:37:54', '这是一部享誉全球的艺术史入门经典。贡布里希用生动的笔触，讲述了从史前洞穴壁画到现代实验艺术的漫长历史，强调艺术的演变是不断解决问题和传承创新的过程，语言平实，见解独到。', '广西美术出版社', '978-7-01-000010-5', '广西美术出版社', 122, '第一版', 'http://www.gxfinearts.com', '2025-01', '2024-12-10', 70.00, 2, '2025-12-10 11:09:45', '2025-12-23 11:03:54', 0);
INSERT INTO `book_info` VALUES (15, '全球通史：从史前史到21世纪', 'http://localhost:8089/file/全球通史.png', 'L.S. 斯塔夫里阿诺斯', '吴象婴', 11, 3, 14, 14, '2025-11-13 10:19:07', '本书突破了以西方为中心的传统史观，采用全新的全球视角，将世界看作一个不可分割的有机整体，从史前人类一直叙述到21世纪，内容涵盖全球，是理解人类历史整体发展的经典之作。', '北京大学出版社', '978-7-01-000011-6', '北京大学出版社', 12, '第一版', 'http://www.pup.cn', '2025-01', '2022-12-11', 88.00, 1, '2025-12-11 11:09:45', '2025-12-23 11:04:00', 0);
INSERT INTO `book_info` VALUES (16, '科学的旅程', 'http://localhost:8089/file/科学的旅程.png', '雷·斯潘根贝格', '郭奕玲', 12, 3, 15, 15, '2025-11-21 10:19:42', '这本书以\"批判性思维\"为核心，生动讲述了科学从诞生到现代的发展历程，不仅记录了成功的发现，也剖析了失败的歧途，揭示了科学作为一项人类探索活动的真实面貌。', '北京大学出版社', '978-7-01-000012-7', '北京大学出版社', 67, '第一版', 'http://www.pup.cn', '2025-01', '2014-12-12', 42.00, 2, '2025-12-12 11:09:45', '2025-12-23 11:04:00', 0);
INSERT INTO `book_info` VALUES (17, '几何原本', 'http://localhost:8089/file/几何原本.png', '欧几里得', '燕晓东', 13, 1, 7, 7, NULL, '这是一部划时代的数学巨著，它建立了几何学的公理体系，成为用公理化方法建立演绎数学体系的最早典范，被誉为\"数学家的圣经\"，影响深远。', '北京大学出版社', '978-7-01-000013-8', '北京大学出版社', 12, '第一版', 'http://www.pup.cn', '2025-01', '2021-12-13', 35.00, 1, '2025-12-13 11:09:45', '2025-12-23 11:52:05', 0);
INSERT INTO `book_info` VALUES (18, '时间简史', 'http://localhost:8089/file/时间简史.png', '斯蒂芬·霍金', '许明贤', 14, 2, 22, 22, NULL, '霍金的代表作之一，旨在向普通读者介绍宇宙学的前沿知识，如黑洞、大爆炸、时空本质等。它用通俗的语言探讨了\"宇宙从何而来，又将向何处去\"的终极问题。', '湖南科学技术出版社', '978-7-01-000014-9', '湖南科学技术出版社', 123, '第一版', 'http://www.hnstp.com', '2025-01', '2014-12-14', 39.80, 0, '2025-12-14 11:09:45', '2025-12-23 11:00:39', 0);
INSERT INTO `book_info` VALUES (19, '物种起源', 'http://localhost:8089/file/物种起源.png', '查尔斯·达尔文', '周建人', 15, 3, 14, 13, '2025-12-20 10:19:07', '这是进化论的奠基之作。达尔文在书中以大量证据系统阐述了\"自然选择\"为核心的进化理论，彻底改变了人类对自身和在自然界中位置的看法。', '商务印书馆', '978-7-01-000015-0', '商务印书馆', 32, '第一版', 'http://www.cp.com.cn', '2025-01', '2021-12-15', 48.00, 5, '2025-12-15 11:09:45', '2025-12-23 12:07:55', 0);
INSERT INTO `book_info` VALUES (20, '本草纲目', 'http://localhost:8089/file/本草纲目.png', '李时珍', NULL, 16, 3, 12, 11, '2025-12-23 10:59:39', '中国古代药学史上部头最大、内容最丰富的药学巨著。它收录了众多药物，并详细叙述了其产地、形态、气味、主治和用法，被誉为\"东方药物巨典\"。', '人民卫生出版社', '978-7-01-000016-1', '人民卫生出版社', 23, '第一版', 'http://www.pmph.com', '2025-01', '2015-12-16', 65.00, 1, '2025-12-16 11:09:45', '2025-12-23 12:06:57', 0);
INSERT INTO `book_info` VALUES (21, '杂交水稻学', 'http://localhost:8089/file/杂交水稻学.png', '袁隆平', NULL, 17, 2, 15, 15, NULL, '这是一部系统总结杂交水稻理论与技术成就的权威著作。袁隆平院士作为\"杂交水稻之父\"，带领团队在该领域取得了世界瞩目的突破。本书全面阐述了杂交水稻的育种原理、种子生产、栽培技术以及未来发展，不仅是中国近现代农业科学的里程碑式成果，也对全球粮食安全作出了不可磨灭的贡献。', '中国农业出版社', '978-7-01-000017-2', '中国农业出版社', 12, '第一版', 'http://www.ccap.com.cn', '2025-01', '2015-12-17', 75.00, 0, '2025-12-17 11:09:45', '2025-12-23 11:44:54', 0);
INSERT INTO `book_info` VALUES (22, 'C++ Primer', 'http://localhost:8089/file/C++ Primer.png', 'Stanley B. Lippman', '王刚', 18, 3, 12, 12, '2025-12-19 22:19:07', '这本书是C++编程领域公认的权威教程和参考手册，被誉为\"C++程序员的圣经\"。它全面、系统地讲解了C++国际标准的所有特性和用法，从基本的语法、数据类型到高级的面向对象编程、泛型编程和标准库。内容深入浅出，配有大量示例代码，是无数软件开发工程师、系统架构师和计算机专业学生的必读经典。本书所代表的软件工程技术，是现代一切工业技术（从操作系统、嵌入式设备到互联网服务）赖以发展的基石。', '电子工业出版社', '978-7-01-000018-3', '电子工业出版社', 123, '第一版', 'http://www.phei.com.cn', '2025-01', '2015-12-18', 40.00, 2, '2025-12-18 11:09:45', '2025-12-23 11:04:00', 0);
INSERT INTO `book_info` VALUES (23, '交通大辞典', 'http://localhost:8089/file/交通大辞典.png', '《交通大辞典》编委会', NULL, 19, 3, 13, 13, '2025-12-22 19:02:34', '这是一部大型综合性交通运输工具书，涵盖了公路、铁路、水路、航空、管道等各种运输方式的理论、技术、工程、管理、经济、法规等方面的知识，是了解交通运输领域的权威参考书。', '上海交通大学出版社', '978-7-01-000019-4', '上海交通大学出版社', 123, '第一版', 'http://www.jiaodapress.com.cn', '2025-01', '2015-12-19', 50.00, 2, '2025-12-19 11:09:45', '2025-12-23 11:55:27', 0);
INSERT INTO `book_info` VALUES (24, '航空航天概论', 'http://localhost:8089/file/航天航空概论.png', '贾玉红', NULL, 20, 3, 12, 11, '2025-12-22 19:02:07', '这是一本系统介绍航天领域基础知识的教材，内容涵盖飞行环境、飞行原理、推进系统、控制系统、航天器结构以及航天技术应用等，是了解航天科学与工程的入门读物。', '北京航空航天大学出版社', '978-7-01-000020-0', '北京航空航天大学出版社', 322, '第一版', 'http://www.buaapress.com.cn', '2025-01', '2015-12-20', 55.00, 2, '2025-12-20 11:09:45', '2025-12-22 20:23:19', 0);
INSERT INTO `book_info` VALUES (25, '寂静的春天', 'http://localhost:8089/file/寂静的春天.png', '蕾切尔·卡森', '吕瑞兰', 21, 3, 11, 11, '2025-12-21 13:19:07', '这部划时代的环保著作以生动的笔触揭示了滥用农药对生态环境和人类健康造成的巨大破坏。它开启了现代环境运动，促使公众开始关注环境问题，并推动了相关立法。', '上海译文出版社', '978-7-01-000021-1', '上海译文出版社', NULL, '第一版', 'http://www.yiwen.com.cn', '2025-01', '2015-12-21', 36.00, 2, '2025-12-21 11:09:45', '2025-12-23 11:03:46', 0);
INSERT INTO `book_info` VALUES (26, '辞海（第七版）', 'http://localhost:8089/file/辞海.png', '夏征农，陈至立', NULL, 22, 4, 2, 0, '2025-12-22 18:40:34', '《辞海》是中国最具权威性的大型综合性辞典，被誉为\"历史和时代的档案馆、镜子和丰碑\"。第七版为最新版本，收录词条近13万条，涵盖了语词、人物、图书、历史事件、科学发现、地理风貌等人类知识的各个方面。其内容准确、释义精当，兼具科学性和通俗性，是文化传承、知识查证和学术研究不可或缺的权威工具书。', '上海辞书出版社', '978-7-01-000022-2', '上海辞书出版社', NULL, '第一版', 'http://www.cishu.com.cn', '2025-01', '2015-12-30', 60.00, 3, '2025-12-22 11:09:45', '2025-12-23 10:17:45', 0);
INSERT INTO `book_info` VALUES (37, '齐民要术', 'http://localhost:8089/file/齐民要术.png', '[北魏] 贾思勰', NULL, 17, 1, 10, 10, NULL, '成书于北魏时期，是中国现存最早、最完整、最系统的综合性农业科学著作，被誉为“中国古代农业百科全书”。全书共十卷，九十二篇，详细记述了当时黄河中下游地区的农业生产技术、食品加工与贮藏、园艺栽培、畜牧养殖、林木种植乃至家庭手工业等方方面面。贾思勰强调“顺天时，量地利”，总结了耕、种、管、收、藏的一整套精耕细作经验，并收录了大量民谚和实地考察所得。该书不仅保存了六世纪以前中国宝贵的农业遗产，其蕴含的因地制宜、注重实践、循环利用的生态农业思想，至今仍闪烁着智慧的光芒。', '', NULL, '', NULL, '', '', '', NULL, NULL, 0, '2025-12-22 17:59:04', '2025-12-23 11:44:56', 0);
INSERT INTO `book_info` VALUES (38, '围城', 'http://localhost:8089/file/围城.png', '钱钟书', NULL, 9, 3, 2, 1, '2025-12-22 18:40:34', '中国现代文学史上一部风格独特的讽刺小说，被誉为“新《儒林外史》”。故事以留学归国的方鸿渐为中心，描绘了抗战初期一群知识分子的生活与情感状态。书名“围城”象征着人生的困境：“城外的人想冲进去，城里的人想逃出来”，这一隐喻贯穿于婚姻、事业乃至整个生活。钱钟书先生以高超的幽默、尖锐的洞察和博喻纷至的语言，对人性弱点、文化冲突和时代弊病进行了淋漓尽致地刻画与讽刺，其精妙比喻与机锋妙语令读者拍案叫绝，回味无穷。', '人民文学出版社', '9787020090500', '', NULL, '人民文学出版社', '', '', NULL, 0.00, 4, '2025-12-22 18:07:09', '2025-12-23 11:58:56', 0);
INSERT INTO `book_info` VALUES (39, '平凡的世界', 'http://localhost:8089/file/平凡的世界.png', '路遥', NULL, 9, 3, 2, 1, '2025-12-23 10:40:34', '一部全景式表现中国当代城乡社会生活的百万字长卷。小说以陕北黄土高原双水村孙少安、孙少平两兄弟的命运为主线，刻画了从1970年代中期到1980年代改革开放初期，大时代历史进程中普通人的奋斗、挫折、痛苦与追求。孙少安立足于乡土，在变革中艰难创业；孙少平则渴望走向更广阔的世界，在煤矿中寻找人生的价值。全书充满了对劳动、爱情、苦难的深刻理解与讴歌，洋溢着昂扬不屈的生命力量与温暖深厚的人间真情，激励了无数读者在平凡的世界中努力追求不平凡的人生。', '', NULL, '', NULL, '', '', '', NULL, 0.00, 4, '2025-12-22 18:09:45', '2025-12-23 10:30:56', 0);
INSERT INTO `book_info` VALUES (40, '乌合之众', 'http://localhost:8089/file/乌合之众.png', '[法] 古斯塔夫·勒庞', NULL, 3, 4, 2, 0, '2025-12-23 11:47:22', '社会心理学领域的奠基性经典，首次出版于1895年。勒庞在书中极具前瞻性地研究了群体心理与行为的特征。他指出，个人一旦融入群体，其个性便会被淹没，群体的思想将占据统治地位，表现出情绪化、无异议、低智商等显著特征。他分析了群体如何易于被形象、词语和套话所煽动，领袖如何通过断言、重复和传染来施加影响，以及群体在政治、宗教、犯罪等领域所扮演的角色。尽管书中观点不乏时代局限与争议，但其对群体非理性、盲目性与破坏力的深刻洞察，为理解法国大革命、群众运动乃至现代网络舆论等社会现象提供了独特的分析视角，影响力历久不衰。', '', NULL, '', 100, '', '', '', NULL, 0.00, 2, '2025-12-22 19:29:08', '2025-12-23 12:00:10', 0);
INSERT INTO `book_info` VALUES (41, '社会契约论', '', '[法] 让-雅克·卢梭', '', 4, 0, 2, 2, NULL, '西方政治哲学史上里程碑式的著作，首次系统阐述了“主权在民”的现代民主思想，为后来的法国大革命和美国独立战争提供了理论旗帜。卢梭开篇提出“人生而自由，却无往不在枷锁之中”的著名论断，进而探讨如何在社会状态下实现人的真正自由。他构想了一种理想的社会契约：每个结合者将其自身的一切权利全部转让给整个共同体，从而形成“公意”；个人服从公意，也就是在服从自己，从而获得道德自由。书中对“公意”与“众意”的区分、对人民主权的绝对性论述以及对政府作为执行机构角色的定位，深刻批判了君主专制与特权制度，对现代民主政治与宪政理论产生了不可估量的影响。', '', NULL, '', NULL, '', '', '', NULL, 0.00, 0, '2025-12-22 19:32:19', '2025-12-23 11:52:05', 0);
INSERT INTO `book_info` VALUES (42, '娱乐至死', 'http://localhost:8089/file/娱乐至死.png', '[美] 尼尔·波兹曼', '', 7, 3, 10, 10, '2025-12-23 11:47:22', '媒介文化批评的里程碑式著作。波兹曼承继麦克卢汉“媒介即讯息”的观点，深入剖析了以电视为代表的视觉媒介如何从根本上重塑了公共话语的逻辑和文化生态。他认为，当严肃的公共讨论——无论是政治、宗教、新闻还是教育——都必须通过娱乐化的方式呈现才能吸引观众时，其结果便是深度的消解、理性的衰退和文化的“幼稚化”。本书并非简单反对娱乐，而是警示人们：当一切文化内容都臣服于娱乐的表演逻辑，我们将可能成为一个“娱乐至死”的物种，失去对复杂现实进行严肃思考、理性辩论和有效行动的能力。在社交媒体时代，其警示意义愈发凸显。', '', NULL, '', NULL, '', '', '', NULL, 0.00, 0, '2025-12-23 11:01:38', '2025-12-23 11:47:22', 0);

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
  INDEX `user_id`(`user_id`) USING BTREE,
  INDEX `book_id`(`book_id`) USING BTREE,
  CONSTRAINT `book_operation_log_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `book_operation_log_ibfk_2` FOREIGN KEY (`book_id`) REFERENCES `book_info` (`book_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 42 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of book_operation_log
-- ----------------------------
INSERT INTO `book_operation_log` VALUES (42, 7, 39, 3, '2025-12-22 18:42:23', '借阅图书《平凡的世界》，借阅天数：15天', '2025-12-22 18:42:23');
INSERT INTO `book_operation_log` VALUES (43, 7, 38, 3, '2025-12-22 18:42:27', '借阅图书《围城》，借阅天数：15天', '2025-12-22 18:42:27');
INSERT INTO `book_operation_log` VALUES (44, 7, 26, 3, '2025-12-22 18:42:31', '借阅图书《辞海（第七版）》，借阅天数：15天', '2025-12-22 18:42:31');
INSERT INTO `book_operation_log` VALUES (45, 7, 24, 1, '2025-12-22 18:42:38', '预约图书《航空航天概论》', '2025-12-22 18:42:38');
INSERT INTO `book_operation_log` VALUES (46, 9, 39, 3, '2025-12-22 18:59:07', '借阅图书《平凡的世界》，借阅天数：60天', '2025-12-22 18:59:07');
INSERT INTO `book_operation_log` VALUES (47, 9, 38, 3, '2025-12-22 18:59:27', '借阅图书《围城》，借阅天数：60天', '2025-12-22 18:59:27');
INSERT INTO `book_operation_log` VALUES (48, 9, 26, 3, '2025-12-22 18:59:34', '借阅图书《辞海（第七版）》，借阅天数：60天', '2025-12-22 18:59:34');
INSERT INTO `book_operation_log` VALUES (49, 9, 24, 1, '2025-12-22 18:59:39', '预约图书《航空航天概论》', '2025-12-22 18:59:39');
INSERT INTO `book_operation_log` VALUES (50, 9, 23, 1, '2025-12-22 19:00:52', '预约图书《交通大辞典》', '2025-12-22 19:00:52');
INSERT INTO `book_operation_log` VALUES (51, 7, 24, 2, '2025-12-22 19:14:54', '取消预约图书《航空航天概论》', '2025-12-22 19:14:54');
INSERT INTO `book_operation_log` VALUES (52, 7, 39, 5, '2025-12-22 19:15:47', 'lin申请归还书籍《平凡的世界》', '2025-12-22 19:15:47');
INSERT INTO `book_operation_log` VALUES (53, 1, 39, 5, '2025-12-22 19:16:28', '管理员(ID:1)确认归还用户【lin】的书籍《平凡的世界》', '2025-12-22 19:16:28');
INSERT INTO `book_operation_log` VALUES (54, 9, 39, 5, '2025-12-22 19:18:07', 'xiao申请归还书籍《平凡的世界》', '2025-12-22 19:18:07');
INSERT INTO `book_operation_log` VALUES (55, 1, 39, 5, '2025-12-22 19:18:30', '管理员(ID:1)确认归还用户【xiao】的书籍《平凡的世界》', '2025-12-22 19:18:30');
INSERT INTO `book_operation_log` VALUES (56, 10, 38, 1, '2025-12-22 19:44:40', '预约图书《围城》', '2025-12-22 19:44:40');
INSERT INTO `book_operation_log` VALUES (57, 7, 38, 5, '2025-12-22 19:44:53', 'lin申请归还书籍《围城》', '2025-12-22 19:44:53');
INSERT INTO `book_operation_log` VALUES (58, 1, 38, 5, '2025-12-22 19:45:15', '管理员(ID:1)确认归还用户【lin】的书籍《围城》', '2025-12-22 19:45:15');
INSERT INTO `book_operation_log` VALUES (59, 10, 24, 3, '2025-12-22 19:45:48', '借阅图书《航空航天概论》，借阅天数：30天', '2025-12-22 19:45:48');
INSERT INTO `book_operation_log` VALUES (60, 7, 26, 4, '2025-12-22 19:46:18', 'lin续借书籍《辞海（第七版）》，续借5天', '2025-12-22 19:46:18');
INSERT INTO `book_operation_log` VALUES (61, 7, 23, 3, '2025-12-22 19:46:38', '借阅图书《交通大辞典》，借阅天数：15天', '2025-12-22 19:46:38');
INSERT INTO `book_operation_log` VALUES (62, 7, 19, 3, '2025-12-22 19:46:42', '借阅图书《物种起源》，借阅天数：15天', '2025-12-22 19:46:42');
INSERT INTO `book_operation_log` VALUES (63, 7, 3, 1, '2025-12-22 19:46:50', '预约图书《中国经济专题》', '2025-12-22 19:46:50');
INSERT INTO `book_operation_log` VALUES (64, 7, 25, 3, '2025-12-22 19:47:02', '借阅图书《寂静的春天》，借阅天数：15天', '2025-12-22 19:47:02');
INSERT INTO `book_operation_log` VALUES (65, 7, 16, 3, '2025-12-22 19:47:25', '借阅图书《科学的旅程》，借阅天数：15天', '2025-12-22 19:47:25');
INSERT INTO `book_operation_log` VALUES (66, 7, 7, 1, '2025-12-22 19:54:21', '预约图书《乡土中国》', '2025-12-22 19:54:21');
INSERT INTO `book_operation_log` VALUES (67, 7, 9, 1, '2025-12-22 19:54:25', '预约图书《孙子兵法》', '2025-12-22 19:54:25');
INSERT INTO `book_operation_log` VALUES (68, 7, 9, 2, '2025-12-22 19:54:34', '取消预约图书《孙子兵法》', '2025-12-22 19:54:34');
INSERT INTO `book_operation_log` VALUES (69, 7, 25, 5, '2025-12-22 20:00:33', 'lin申请归还书籍《寂静的春天》', '2025-12-22 20:00:33');
INSERT INTO `book_operation_log` VALUES (70, 7, 39, 1, '2025-12-22 20:13:46', '预约图书《平凡的世界》', '2025-12-22 20:13:46');
INSERT INTO `book_operation_log` VALUES (71, 7, 3, 2, '2025-12-22 20:17:46', '取消预约图书《中国经济专题》', '2025-12-22 20:17:46');
INSERT INTO `book_operation_log` VALUES (72, 7, 7, 2, '2025-12-22 20:17:48', '取消预约图书《乡土中国》', '2025-12-22 20:17:48');
INSERT INTO `book_operation_log` VALUES (73, 7, 39, 2, '2025-12-22 20:17:50', '取消预约图书《平凡的世界》', '2025-12-22 20:17:50');
INSERT INTO `book_operation_log` VALUES (74, 7, 23, 5, '2025-12-22 20:17:55', 'lin申请归还书籍《交通大辞典》', '2025-12-22 20:17:55');
INSERT INTO `book_operation_log` VALUES (75, 7, 19, 5, '2025-12-22 20:17:56', 'lin申请归还书籍《物种起源》', '2025-12-22 20:17:56');
INSERT INTO `book_operation_log` VALUES (76, 7, 26, 5, '2025-12-22 20:17:58', 'lin申请归还书籍《辞海（第七版）》', '2025-12-22 20:17:58');
INSERT INTO `book_operation_log` VALUES (77, 7, 39, 1, '2025-12-22 20:20:59', '预约图书《平凡的世界》', '2025-12-22 20:20:59');
INSERT INTO `book_operation_log` VALUES (78, 7, 24, 3, '2025-12-22 20:21:21', '借阅图书《航空航天概论》，借阅天数：15天', '2025-12-22 20:21:21');
INSERT INTO `book_operation_log` VALUES (79, 7, 4, 3, '2025-12-22 20:21:52', '借阅图书《万历十五年》，借阅天数：15天', '2025-12-22 20:21:52');
INSERT INTO `book_operation_log` VALUES (80, 1, 26, 5, '2025-12-22 20:22:27', '管理员(ID:1)确认归还用户【lin】的书籍《辞海（第七版）》', '2025-12-22 20:22:27');
INSERT INTO `book_operation_log` VALUES (81, 1, 23, 5, '2025-12-22 20:22:27', '管理员(ID:1)确认归还用户【lin】的书籍《交通大辞典》', '2025-12-22 20:22:27');
INSERT INTO `book_operation_log` VALUES (82, 1, 19, 5, '2025-12-22 20:22:27', '管理员(ID:1)确认归还用户【lin】的书籍《物种起源》', '2025-12-22 20:22:27');
INSERT INTO `book_operation_log` VALUES (83, 1, 25, 5, '2025-12-22 20:22:27', '管理员(ID:1)确认归还用户【lin】的书籍《寂静的春天》', '2025-12-22 20:22:27');
INSERT INTO `book_operation_log` VALUES (84, 7, 16, 5, '2025-12-22 20:22:49', 'lin申请归还书籍《科学的旅程》', '2025-12-22 20:22:49');
INSERT INTO `book_operation_log` VALUES (85, 7, 24, 5, '2025-12-22 20:22:54', 'lin申请归还书籍《航空航天概论》', '2025-12-22 20:22:54');
INSERT INTO `book_operation_log` VALUES (86, 1, 16, 5, '2025-12-22 20:23:20', '管理员(ID:1)确认归还用户【lin】的书籍《科学的旅程》', '2025-12-22 20:23:20');
INSERT INTO `book_operation_log` VALUES (87, 1, 24, 5, '2025-12-22 20:23:20', '管理员(ID:1)确认归还用户【lin】的书籍《航空航天概论》', '2025-12-22 20:23:20');
INSERT INTO `book_operation_log` VALUES (88, 7, 38, 1, '2025-12-22 20:28:09', '预约图书《围城》', '2025-12-22 20:28:09');
INSERT INTO `book_operation_log` VALUES (89, 7, 19, 3, '2025-12-22 20:28:27', '借阅图书《物种起源》，借阅天数：15天', '2025-12-22 20:28:27');
INSERT INTO `book_operation_log` VALUES (90, 7, 4, 5, '2025-12-22 20:29:51', 'lin申请归还书籍《万历十五年》', '2025-12-22 20:29:51');
INSERT INTO `book_operation_log` VALUES (91, 7, 19, 4, '2025-12-22 20:30:00', 'lin续借书籍《物种起源》，续借5天', '2025-12-22 20:30:00');
INSERT INTO `book_operation_log` VALUES (92, 7, 14, 3, '2025-12-22 20:30:22', '借阅图书《艺术的故事》，借阅天数：15天', '2025-12-22 20:30:22');
INSERT INTO `book_operation_log` VALUES (93, 7, 14, 4, '2025-12-22 20:30:34', 'lin续借书籍《艺术的故事》，续借5天', '2025-12-22 20:30:34');
INSERT INTO `book_operation_log` VALUES (94, 7, 19, 5, '2025-12-22 20:30:41', 'lin申请归还书籍《物种起源》', '2025-12-22 20:30:41');
INSERT INTO `book_operation_log` VALUES (95, 7, 14, 5, '2025-12-22 20:30:41', 'lin申请归还书籍《艺术的故事》', '2025-12-22 20:30:41');
INSERT INTO `book_operation_log` VALUES (96, 7, 22, 3, '2025-12-22 20:31:51', '借阅图书《C++ Primer》，借阅天数：15天', '2025-12-22 20:31:51');
INSERT INTO `book_operation_log` VALUES (97, 7, 38, 2, '2025-12-22 20:33:02', '取消预约图书《围城》', '2025-12-22 20:33:02');
INSERT INTO `book_operation_log` VALUES (98, 7, 22, 5, '2025-12-22 20:37:07', 'lin申请归还书籍《C++ Primer》', '2025-12-22 20:37:07');
INSERT INTO `book_operation_log` VALUES (99, 1, 4, 5, '2025-12-22 20:37:22', '管理员(ID:1)确认归还用户【lin】的书籍《万历十五年》', '2025-12-22 20:37:22');
INSERT INTO `book_operation_log` VALUES (100, 1, 19, 5, '2025-12-22 20:37:22', '管理员(ID:1)确认归还用户【lin】的书籍《物种起源》', '2025-12-22 20:37:22');
INSERT INTO `book_operation_log` VALUES (101, 1, 14, 5, '2025-12-22 20:37:22', '管理员(ID:1)确认归还用户【lin】的书籍《艺术的故事》', '2025-12-22 20:37:22');
INSERT INTO `book_operation_log` VALUES (102, 1, 22, 5, '2025-12-22 20:37:22', '管理员(ID:1)确认归还用户【lin】的书籍《C++ Primer》', '2025-12-22 20:37:22');
INSERT INTO `book_operation_log` VALUES (103, 7, 26, 1, '2025-12-22 20:39:53', '预约图书《辞海（第七版）》', '2025-12-22 20:39:53');
INSERT INTO `book_operation_log` VALUES (104, 7, 17, 3, '2025-12-22 20:40:07', '借阅图书《几何原本》，借阅天数：15天', '2025-12-22 20:40:07');
INSERT INTO `book_operation_log` VALUES (105, 7, 9, 1, '2025-12-22 20:40:47', '预约图书《孙子兵法》', '2025-12-22 20:40:47');
INSERT INTO `book_operation_log` VALUES (106, 7, 11, 3, '2025-12-22 20:41:50', '借阅图书《什么是教育》，借阅天数：15天', '2025-12-22 20:41:50');
INSERT INTO `book_operation_log` VALUES (107, 7, 10, 3, '2025-12-22 20:41:53', '借阅图书《国富论》，借阅天数：15天', '2025-12-22 20:41:53');
INSERT INTO `book_operation_log` VALUES (108, 7, 7, 1, '2025-12-22 20:41:58', '预约图书《乡土中国》', '2025-12-22 20:41:58');
INSERT INTO `book_operation_log` VALUES (109, 7, 8, 3, '2025-12-22 20:42:01', '借阅图书《论法的精神》，借阅天数：15天', '2025-12-22 20:42:01');
INSERT INTO `book_operation_log` VALUES (110, 7, 17, 5, '2025-12-22 20:42:16', 'lin申请归还书籍《几何原本》', '2025-12-22 20:42:16');
INSERT INTO `book_operation_log` VALUES (111, 7, 11, 5, '2025-12-22 20:42:16', 'lin申请归还书籍《什么是教育》', '2025-12-22 20:42:16');
INSERT INTO `book_operation_log` VALUES (112, 7, 10, 4, '2025-12-22 20:42:27', 'lin续借书籍《国富论》，续借5天', '2025-12-22 20:42:27');
INSERT INTO `book_operation_log` VALUES (113, 7, 8, 4, '2025-12-22 20:42:46', 'lin续借书籍《论法的精神》，续借5天', '2025-12-22 20:42:46');
INSERT INTO `book_operation_log` VALUES (114, 7, 26, 2, '2025-12-22 20:44:18', '取消预约图书《辞海（第七版）》', '2025-12-22 20:44:18');
INSERT INTO `book_operation_log` VALUES (115, 7, 38, 1, '2025-12-22 20:48:34', '预约图书《围城》', '2025-12-22 20:48:34');
INSERT INTO `book_operation_log` VALUES (116, 7, 26, 1, '2025-12-22 20:48:49', '预约图书《辞海（第七版）》', '2025-12-22 20:48:49');
INSERT INTO `book_operation_log` VALUES (117, 7, 3, 1, '2025-12-22 20:49:10', '预约图书《中国经济专题》', '2025-12-22 20:49:10');
INSERT INTO `book_operation_log` VALUES (118, 1, 17, 5, '2025-12-22 20:50:20', '管理员(ID:1)确认归还用户【lin】的书籍《几何原本》', '2025-12-22 20:50:20');
INSERT INTO `book_operation_log` VALUES (119, 1, 11, 5, '2025-12-22 20:50:20', '管理员(ID:1)确认归还用户【lin】的书籍《什么是教育》', '2025-12-22 20:50:20');
INSERT INTO `book_operation_log` VALUES (120, 7, 10, 5, '2025-12-22 21:19:08', 'lin申请归还书籍《国富论》', '2025-12-22 21:19:08');
INSERT INTO `book_operation_log` VALUES (121, 1, 10, 5, '2025-12-22 21:19:34', '管理员(ID:1)确认归还用户【lin】的书籍《国富论》', '2025-12-22 21:19:34');
INSERT INTO `book_operation_log` VALUES (122, 7, 39, 2, '2025-12-23 10:05:34', '取消预约图书《平凡的世界》', '2025-12-23 10:05:34');
INSERT INTO `book_operation_log` VALUES (123, 7, 9, 2, '2025-12-23 10:05:37', '取消预约图书《孙子兵法》', '2025-12-23 10:05:37');
INSERT INTO `book_operation_log` VALUES (124, 7, 7, 2, '2025-12-23 10:05:39', '取消预约图书《乡土中国》', '2025-12-23 10:05:39');
INSERT INTO `book_operation_log` VALUES (125, 7, 38, 2, '2025-12-23 10:05:41', '取消预约图书《围城》', '2025-12-23 10:05:41');
INSERT INTO `book_operation_log` VALUES (126, 7, 26, 2, '2025-12-23 10:05:43', '取消预约图书《辞海（第七版）》', '2025-12-23 10:05:43');
INSERT INTO `book_operation_log` VALUES (127, 7, 3, 2, '2025-12-23 10:05:45', '取消预约图书《中国经济专题》', '2025-12-23 10:05:45');
INSERT INTO `book_operation_log` VALUES (128, 7, 8, 5, '2025-12-23 10:05:51', 'lin申请归还书籍《论法的精神》', '2025-12-23 10:05:51');
INSERT INTO `book_operation_log` VALUES (129, 7, 39, 1, '2025-12-23 10:05:57', '预约图书《平凡的世界》', '2025-12-23 10:05:57');
INSERT INTO `book_operation_log` VALUES (130, 7, 39, 2, '2025-12-23 10:06:13', '取消预约图书《平凡的世界》', '2025-12-23 10:06:13');
INSERT INTO `book_operation_log` VALUES (131, 9, 38, 5, '2025-12-23 10:07:16', 'xiao申请归还书籍《围城》', '2025-12-23 10:07:16');
INSERT INTO `book_operation_log` VALUES (132, 10, 38, 3, '2025-12-23 10:15:46', '借阅图书《围城》，借阅天数：30天', '2025-12-23 10:15:46');
INSERT INTO `book_operation_log` VALUES (133, 10, 39, 3, '2025-12-23 10:15:51', '借阅图书《平凡的世界》，借阅天数：30天', '2025-12-23 10:15:51');
INSERT INTO `book_operation_log` VALUES (134, 10, 26, 3, '2025-12-23 10:17:45', '借阅图书《辞海（第七版）》，借阅天数：30天', '2025-12-23 10:17:45');
INSERT INTO `book_operation_log` VALUES (135, 7, 26, 1, '2025-12-23 10:21:48', '预约图书《辞海（第七版）》', '2025-12-23 10:21:48');
INSERT INTO `book_operation_log` VALUES (136, 7, 39, 3, '2025-12-23 10:22:10', '借阅图书《平凡的世界》，借阅天数：15天', '2025-12-23 10:22:10');
INSERT INTO `book_operation_log` VALUES (137, 7, 22, 3, '2025-12-23 10:23:03', '借阅图书《C++ Primer》，借阅天数：15天', '2025-12-23 10:23:03');
INSERT INTO `book_operation_log` VALUES (138, 7, 23, 3, '2025-12-23 10:23:06', '借阅图书《交通大辞典》，借阅天数：15天', '2025-12-23 10:23:06');
INSERT INTO `book_operation_log` VALUES (139, 7, 19, 3, '2025-12-23 10:23:10', '借阅图书《物种起源》，借阅天数：15天', '2025-12-23 10:23:10');
INSERT INTO `book_operation_log` VALUES (140, 7, 39, 5, '2025-12-23 10:23:37', 'lin申请归还书籍《平凡的世界》', '2025-12-23 10:23:37');
INSERT INTO `book_operation_log` VALUES (141, 7, 22, 4, '2025-12-23 10:23:55', 'lin续借书籍《C++ Primer》，续借5天', '2025-12-23 10:23:55');
INSERT INTO `book_operation_log` VALUES (142, 7, 23, 4, '2025-12-23 10:23:55', 'lin续借书籍《交通大辞典》，续借5天', '2025-12-23 10:23:55');
INSERT INTO `book_operation_log` VALUES (143, 7, 22, 5, '2025-12-23 10:24:13', 'lin申请归还书籍《C++ Primer》', '2025-12-23 10:24:13');
INSERT INTO `book_operation_log` VALUES (144, 7, 19, 5, '2025-12-23 10:24:13', 'lin申请归还书籍《物种起源》', '2025-12-23 10:24:13');
INSERT INTO `book_operation_log` VALUES (145, 7, 26, 2, '2025-12-23 10:25:41', '取消预约图书《辞海（第七版）》', '2025-12-23 10:25:41');
INSERT INTO `book_operation_log` VALUES (146, 1, 39, 5, '2025-12-23 10:30:19', '管理员(ID:1)确认归还用户【lin】的书籍《平凡的世界》', '2025-12-23 10:30:19');
INSERT INTO `book_operation_log` VALUES (147, 7, 40, 1, '2025-12-23 10:35:58', '预约图书《乌合之众》', '2025-12-23 10:35:58');
INSERT INTO `book_operation_log` VALUES (148, 7, 25, 3, '2025-12-23 10:36:09', '借阅图书《寂静的春天》，借阅天数：15天', '2025-12-23 10:36:09');
INSERT INTO `book_operation_log` VALUES (149, 7, 25, 4, '2025-12-23 10:36:56', 'lin续借书籍《寂静的春天》，续借5天', '2025-12-23 10:36:56');
INSERT INTO `book_operation_log` VALUES (150, 7, 16, 3, '2025-12-23 10:37:12', '借阅图书《科学的旅程》，借阅天数：15天', '2025-12-23 10:37:12');
INSERT INTO `book_operation_log` VALUES (151, 7, 15, 3, '2025-12-23 10:37:15', '借阅图书《全球通史：从史前史到21世纪》，借阅天数：15天', '2025-12-23 10:37:15');
INSERT INTO `book_operation_log` VALUES (152, 7, 14, 3, '2025-12-23 10:37:18', '借阅图书《艺术的故事》，借阅天数：15天', '2025-12-23 10:37:18');
INSERT INTO `book_operation_log` VALUES (153, 7, 16, 4, '2025-12-23 10:37:27', 'lin续借书籍《科学的旅程》，续借5天', '2025-12-23 10:37:27');
INSERT INTO `book_operation_log` VALUES (154, 7, 15, 4, '2025-12-23 10:37:27', 'lin续借书籍《全球通史：从史前史到21世纪》，续借5天', '2025-12-23 10:37:27');
INSERT INTO `book_operation_log` VALUES (155, 7, 16, 5, '2025-12-23 10:37:37', 'lin申请归还书籍《科学的旅程》', '2025-12-23 10:37:37');
INSERT INTO `book_operation_log` VALUES (156, 7, 15, 5, '2025-12-23 10:37:37', 'lin申请归还书籍《全球通史：从史前史到21世纪》', '2025-12-23 10:37:37');
INSERT INTO `book_operation_log` VALUES (157, 7, 40, 2, '2025-12-23 10:38:58', '取消预约图书《乌合之众》', '2025-12-23 10:38:58');
INSERT INTO `book_operation_log` VALUES (158, 7, 38, 1, '2025-12-23 10:46:12', '预约图书《围城》', '2025-12-23 10:46:12');
INSERT INTO `book_operation_log` VALUES (159, 7, 4, 3, '2025-12-23 10:46:31', '借阅图书《万历十五年》，借阅天数：15天', '2025-12-23 10:46:31');
INSERT INTO `book_operation_log` VALUES (160, 7, 11, 3, '2025-12-23 10:46:34', '借阅图书《什么是教育》，借阅天数：15天', '2025-12-23 10:46:34');
INSERT INTO `book_operation_log` VALUES (161, 7, 14, 4, '2025-12-23 10:47:21', 'lin续借书籍《艺术的故事》，续借5天', '2025-12-23 10:47:21');
INSERT INTO `book_operation_log` VALUES (162, 7, 4, 4, '2025-12-23 10:47:21', 'lin续借书籍《万历十五年》，续借5天', '2025-12-23 10:47:21');
INSERT INTO `book_operation_log` VALUES (163, 7, 14, 5, '2025-12-23 10:47:37', 'lin申请归还书籍《艺术的故事》', '2025-12-23 10:47:37');
INSERT INTO `book_operation_log` VALUES (164, 7, 4, 5, '2025-12-23 10:47:37', 'lin申请归还书籍《万历十五年》', '2025-12-23 10:47:37');
INSERT INTO `book_operation_log` VALUES (165, 7, 25, 5, '2025-12-23 10:47:41', 'lin申请归还书籍《寂静的春天》', '2025-12-23 10:47:41');
INSERT INTO `book_operation_log` VALUES (166, 7, 38, 2, '2025-12-23 10:48:56', '取消预约图书《围城》', '2025-12-23 10:48:56');
INSERT INTO `book_operation_log` VALUES (167, 1, 25, 5, '2025-12-23 11:03:47', '管理员(ID:1)确认归还用户【lin】的书籍《寂静的春天》', '2025-12-23 11:03:47');
INSERT INTO `book_operation_log` VALUES (168, 1, 14, 5, '2025-12-23 11:03:54', '管理员(ID:1)确认归还用户【lin】的书籍《艺术的故事》', '2025-12-23 11:03:54');
INSERT INTO `book_operation_log` VALUES (169, 1, 4, 5, '2025-12-23 11:03:54', '管理员(ID:1)确认归还用户【lin】的书籍《万历十五年》', '2025-12-23 11:03:54');
INSERT INTO `book_operation_log` VALUES (170, 1, 38, 5, '2025-12-23 11:04:00', '管理员(ID:1)确认归还用户【xiao】的书籍《围城》', '2025-12-23 11:04:00');
INSERT INTO `book_operation_log` VALUES (171, 1, 8, 5, '2025-12-23 11:04:00', '管理员(ID:1)确认归还用户【lin】的书籍《论法的精神》', '2025-12-23 11:04:00');
INSERT INTO `book_operation_log` VALUES (172, 1, 22, 5, '2025-12-23 11:04:00', '管理员(ID:1)确认归还用户【lin】的书籍《C++ Primer》', '2025-12-23 11:04:00');
INSERT INTO `book_operation_log` VALUES (173, 1, 19, 5, '2025-12-23 11:04:00', '管理员(ID:1)确认归还用户【lin】的书籍《物种起源》', '2025-12-23 11:04:00');
INSERT INTO `book_operation_log` VALUES (174, 1, 16, 5, '2025-12-23 11:04:00', '管理员(ID:1)确认归还用户【lin】的书籍《科学的旅程》', '2025-12-23 11:04:00');
INSERT INTO `book_operation_log` VALUES (175, 1, 15, 5, '2025-12-23 11:04:00', '管理员(ID:1)确认归还用户【lin】的书籍《全球通史：从史前史到21世纪》', '2025-12-23 11:04:00');
INSERT INTO `book_operation_log` VALUES (176, 7, 18, 1, '2025-12-23 11:07:48', '预约图书《时间简史》', '2025-12-23 11:07:48');
INSERT INTO `book_operation_log` VALUES (177, 7, 19, 3, '2025-12-23 11:08:02', '借阅图书《物种起源》，借阅天数：15天', '2025-12-23 11:08:02');
INSERT INTO `book_operation_log` VALUES (178, 7, 19, 4, '2025-12-23 11:08:55', 'lin续借书籍《物种起源》，续借5天', '2025-12-23 11:08:55');
INSERT INTO `book_operation_log` VALUES (179, 7, 11, 4, '2025-12-23 11:08:55', 'lin续借书籍《什么是教育》，续借5天', '2025-12-23 11:08:55');
INSERT INTO `book_operation_log` VALUES (180, 7, 11, 5, '2025-12-23 11:09:09', 'lin申请归还书籍《什么是教育》', '2025-12-23 11:09:09');
INSERT INTO `book_operation_log` VALUES (181, 7, 19, 5, '2025-12-23 11:09:09', 'lin申请归还书籍《物种起源》', '2025-12-23 11:09:09');
INSERT INTO `book_operation_log` VALUES (182, 7, 18, 2, '2025-12-23 11:10:25', '取消预约图书《时间简史》', '2025-12-23 11:10:25');
INSERT INTO `book_operation_log` VALUES (183, 1, 11, 5, '2025-12-23 11:53:29', '管理员(ID:1)确认归还用户【lin】的书籍《什么是教育》', '2025-12-23 11:53:29');
INSERT INTO `book_operation_log` VALUES (184, 1, 19, 5, '2025-12-23 11:53:29', '管理员(ID:1)确认归还用户【lin】的书籍《物种起源》', '2025-12-23 11:53:29');
INSERT INTO `book_operation_log` VALUES (185, 7, 23, 5, '2025-12-23 11:54:32', 'lin申请归还书籍《交通大辞典》', '2025-12-23 11:54:32');
INSERT INTO `book_operation_log` VALUES (186, 7, 38, 1, '2025-12-23 11:54:57', '预约图书《围城》', '2025-12-23 11:54:57');
INSERT INTO `book_operation_log` VALUES (187, 1, 23, 5, '2025-12-23 11:55:28', '管理员(ID:1)确认归还用户【lin】的书籍《交通大辞典》', '2025-12-23 11:55:28');
INSERT INTO `book_operation_log` VALUES (188, 9, 38, 3, '2025-12-23 11:57:08', '借阅图书《围城》，借阅天数：60天', '2025-12-23 11:57:08');
INSERT INTO `book_operation_log` VALUES (189, 7, 38, 2, '2025-12-23 11:57:31', '取消预约图书《围城》', '2025-12-23 11:57:31');
INSERT INTO `book_operation_log` VALUES (190, 7, 38, 1, '2025-12-23 11:57:39', '预约图书《围城》', '2025-12-23 11:57:39');
INSERT INTO `book_operation_log` VALUES (191, 9, 38, 5, '2025-12-23 11:57:53', 'xiao申请归还书籍《围城》', '2025-12-23 11:57:53');
INSERT INTO `book_operation_log` VALUES (192, 1, 38, 5, '2025-12-23 11:58:04', '管理员(ID:1)确认归还用户【xiao】的书籍《围城》', '2025-12-23 11:58:04');
INSERT INTO `book_operation_log` VALUES (193, 9, 40, 3, '2025-12-23 11:59:59', '借阅图书《乌合之众》，借阅天数：60天', '2025-12-23 11:59:59');
INSERT INTO `book_operation_log` VALUES (194, 10, 40, 3, '2025-12-23 12:00:10', '借阅图书《乌合之众》，借阅天数：30天', '2025-12-23 12:00:10');
INSERT INTO `book_operation_log` VALUES (195, 9, 7, 3, '2025-12-23 12:00:59', '借阅图书《乡土中国》，借阅天数：60天', '2025-12-23 12:00:59');
INSERT INTO `book_operation_log` VALUES (196, 10, 7, 3, '2025-12-23 12:01:10', '借阅图书《乡土中国》，借阅天数：30天', '2025-12-23 12:01:10');
INSERT INTO `book_operation_log` VALUES (197, 10, 7, 5, '2025-12-23 12:01:47', 'wan申请归还书籍《乡土中国》', '2025-12-23 12:01:47');
INSERT INTO `book_operation_log` VALUES (198, 1, 7, 5, '2025-12-23 12:02:03', '管理员(ID:1)确认归还用户【wan】的书籍《乡土中国》', '2025-12-23 12:02:03');
INSERT INTO `book_operation_log` VALUES (199, 10, 7, 3, '2025-12-23 12:03:16', '借阅图书《乡土中国》，借阅天数：30天', '2025-12-23 12:03:16');
INSERT INTO `book_operation_log` VALUES (200, 10, 24, 5, '2025-12-23 12:06:30', 'wan申请归还书籍《航空航天概论》', '2025-12-23 12:06:30');
INSERT INTO `book_operation_log` VALUES (201, 9, 20, 3, '2025-12-23 12:06:57', '借阅图书《本草纲目》，借阅天数：60天', '2025-12-23 12:06:57');
INSERT INTO `book_operation_log` VALUES (202, 9, 20, 5, '2025-12-23 12:07:35', 'xiao申请归还书籍《本草纲目》', '2025-12-23 12:07:35');
INSERT INTO `book_operation_log` VALUES (203, 9, 19, 3, '2025-12-23 12:07:55', '借阅图书《物种起源》，借阅天数：60天', '2025-12-23 12:07:55');
INSERT INTO `book_operation_log` VALUES (204, 9, 19, 5, '2025-12-23 12:08:01', 'xiao申请归还书籍《物种起源》', '2025-12-23 12:08:01');

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
  INDEX `book_id`(`book_id`) USING BTREE,
  CONSTRAINT `book_preview_ibfk_1` FOREIGN KEY (`book_id`) REFERENCES `book_info` (`book_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

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
  INDEX `borrow_id`(`borrow_id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  CONSTRAINT `book_renew_ibfk_1` FOREIGN KEY (`borrow_id`) REFERENCES `book_borrow` (`borrow_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `book_renew_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of book_renew
-- ----------------------------
INSERT INTO `book_renew` VALUES (6, 20, 7, '2025-12-22 19:46:18', 5, '2026-01-06 18:42:31', '2026-01-11 18:42:31', '2025-12-22 19:46:18');
INSERT INTO `book_renew` VALUES (7, 31, 7, '2025-12-22 20:30:00', 5, '2026-01-06 20:28:27', '2026-01-11 20:28:27', '2025-12-22 20:30:00');
INSERT INTO `book_renew` VALUES (8, 32, 7, '2025-12-22 20:30:34', 5, '2026-01-06 20:30:22', '2026-01-11 20:30:22', '2025-12-22 20:30:34');
INSERT INTO `book_renew` VALUES (9, 36, 7, '2025-12-22 20:42:27', 5, '2026-01-06 20:41:53', '2026-01-11 20:41:53', '2025-12-22 20:42:27');
INSERT INTO `book_renew` VALUES (10, 37, 7, '2025-12-22 20:42:46', 5, '2026-01-06 20:42:01', '2026-01-11 20:42:01', '2025-12-22 20:42:46');
INSERT INTO `book_renew` VALUES (11, 42, 7, '2025-12-23 10:23:55', 5, '2026-01-07 10:23:03', '2026-01-12 10:23:03', '2025-12-23 10:23:55');
INSERT INTO `book_renew` VALUES (12, 43, 7, '2025-12-23 10:23:55', 5, '2026-01-07 10:23:06', '2026-01-12 10:23:06', '2025-12-23 10:23:55');
INSERT INTO `book_renew` VALUES (13, 45, 7, '2025-12-23 10:36:56', 5, '2026-01-07 10:36:09', '2026-01-12 10:36:09', '2025-12-23 10:36:56');
INSERT INTO `book_renew` VALUES (14, 46, 7, '2025-12-23 10:37:27', 5, '2026-01-07 10:37:12', '2026-01-12 10:37:12', '2025-12-23 10:37:27');
INSERT INTO `book_renew` VALUES (15, 47, 7, '2025-12-23 10:37:27', 5, '2026-01-07 10:37:15', '2026-01-12 10:37:15', '2025-12-23 10:37:27');
INSERT INTO `book_renew` VALUES (16, 48, 7, '2025-12-23 10:47:21', 5, '2026-01-07 10:37:18', '2026-01-12 10:37:18', '2025-12-23 10:47:21');
INSERT INTO `book_renew` VALUES (17, 49, 7, '2025-12-23 10:47:21', 5, '2026-01-07 10:46:31', '2026-01-12 10:46:31', '2025-12-23 10:47:21');
INSERT INTO `book_renew` VALUES (18, 51, 7, '2025-12-23 11:08:55', 5, '2026-01-07 11:08:02', '2026-01-12 11:08:02', '2025-12-23 11:08:55');
INSERT INTO `book_renew` VALUES (19, 50, 7, '2025-12-23 11:08:55', 5, '2026-01-07 10:46:34', '2026-01-12 10:46:34', '2025-12-23 11:08:55');

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
  `reservation_reason` tinyint NULL DEFAULT NULL COMMENT '预约原因，2-待上架，4-已借光',
  PRIMARY KEY (`reservation_id`) USING BTREE,
  INDEX `book_id`(`book_id`) USING BTREE,
  INDEX `idx_user_book_status`(`user_id`, `book_id`, `reservation_status`) USING BTREE,
  CONSTRAINT `book_reservation_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `book_reservation_ibfk_2` FOREIGN KEY (`book_id`) REFERENCES `book_info` (`book_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2002670205537570819 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of book_reservation
-- ----------------------------
INSERT INTO `book_reservation` VALUES (2003053558212202497, 7, 24, '2025-12-22 18:42:38', 2, '2025-12-29 18:42:38', 1, '2025-12-22 19:02:07', '2025-12-22 18:42:38', '2025-12-22 19:14:54', 2);
INSERT INTO `book_reservation` VALUES (2003057842345611266, 9, 24, '2025-12-22 18:59:39', 0, '2025-12-29 18:59:39', 1, '2025-12-22 19:02:07', '2025-12-22 18:59:39', '2025-12-22 19:02:07', 2);
INSERT INTO `book_reservation` VALUES (2003058147720302593, 9, 23, '2025-12-22 19:00:52', 0, '2025-12-29 19:00:52', 1, '2025-12-22 19:02:34', '2025-12-22 19:00:52', '2025-12-22 19:02:34', 2);
INSERT INTO `book_reservation` VALUES (2003069168006598658, 10, 38, '2025-12-22 19:44:40', 0, '2025-12-29 19:44:40', 1, '2025-12-22 19:45:15', '2025-12-22 19:44:40', '2025-12-22 19:45:15', 4);
INSERT INTO `book_reservation` VALUES (2003069714998366210, 7, 3, '2025-12-22 19:46:50', 2, '2025-12-29 19:46:50', 0, NULL, '2025-12-22 19:46:50', '2025-12-23 10:05:45', 2);
INSERT INTO `book_reservation` VALUES (2003071605912244225, 7, 7, '2025-12-22 19:54:21', 2, '2025-12-29 19:54:21', 0, NULL, '2025-12-22 19:54:21', '2025-12-23 10:05:39', 2);
INSERT INTO `book_reservation` VALUES (2003071623759007746, 7, 9, '2025-12-22 19:54:25', 2, '2025-12-29 19:54:25', 0, NULL, '2025-12-22 19:54:25', '2025-12-23 10:05:37', 2);
INSERT INTO `book_reservation` VALUES (2003076493199151106, 7, 39, '2025-12-22 20:13:46', 2, '2025-12-29 20:13:46', 0, NULL, '2025-12-22 20:13:46', '2025-12-23 10:06:13', 4);
INSERT INTO `book_reservation` VALUES (2003078310779817985, 7, 39, '2025-12-22 20:20:59', 2, '2025-12-29 20:20:59', 0, NULL, '2025-12-22 20:20:59', '2025-12-23 10:06:13', 4);
INSERT INTO `book_reservation` VALUES (2003080111281278977, 7, 38, '2025-12-22 20:28:09', 2, '2025-12-29 20:28:09', 0, NULL, '2025-12-22 20:28:09', '2025-12-23 11:57:31', 4);
INSERT INTO `book_reservation` VALUES (2003083067267354625, 7, 26, '2025-12-22 20:39:53', 2, '2025-12-29 20:39:53', 0, NULL, '2025-12-22 20:39:53', '2025-12-23 10:25:41', 4);
INSERT INTO `book_reservation` VALUES (2003083292522450946, 7, 9, '2025-12-22 20:40:47', 2, '2025-12-29 20:40:47', 0, NULL, '2025-12-22 20:40:47', '2025-12-23 10:05:37', 2);
INSERT INTO `book_reservation` VALUES (2003083589726638082, 7, 7, '2025-12-22 20:41:58', 2, '2025-12-29 20:41:58', 0, NULL, '2025-12-22 20:41:58', '2025-12-23 10:05:39', 2);
INSERT INTO `book_reservation` VALUES (2003085252592013314, 7, 38, '2025-12-22 20:48:34', 2, '2025-12-29 20:48:34', 0, NULL, '2025-12-22 20:48:34', '2025-12-23 11:57:31', 4);
INSERT INTO `book_reservation` VALUES (2003085313623330818, 7, 26, '2025-12-22 20:48:49', 2, '2025-12-29 20:48:49', 0, NULL, '2025-12-22 20:48:49', '2025-12-23 10:25:41', 4);
INSERT INTO `book_reservation` VALUES (2003085400432840706, 7, 3, '2025-12-22 20:49:10', 2, '2025-12-29 20:49:10', 0, NULL, '2025-12-22 20:49:10', '2025-12-23 10:05:45', 2);
INSERT INTO `book_reservation` VALUES (2003285918891610113, 7, 39, '2025-12-23 10:05:57', 2, '2025-12-30 10:05:57', 0, NULL, '2025-12-23 10:05:57', '2025-12-23 10:06:13', 4);
INSERT INTO `book_reservation` VALUES (2003289906605166593, 7, 26, '2025-12-23 10:21:48', 2, '2025-12-30 10:21:48', 0, NULL, '2025-12-23 10:21:48', '2025-12-23 10:25:41', 4);
INSERT INTO `book_reservation` VALUES (2003293473491619841, 7, 40, '2025-12-23 10:35:58', 2, '2025-12-30 10:35:58', 0, NULL, '2025-12-23 10:35:58', '2025-12-23 10:38:58', 2);
INSERT INTO `book_reservation` VALUES (2003296046307053570, 7, 38, '2025-12-23 10:46:12', 2, '2025-12-30 10:46:12', 0, NULL, '2025-12-23 10:46:12', '2025-12-23 11:57:31', 4);
INSERT INTO `book_reservation` VALUES (2003301483874062337, 7, 18, '2025-12-23 11:07:48', 2, '2025-12-30 11:07:48', 0, NULL, '2025-12-23 11:07:48', '2025-12-23 11:10:25', 2);
INSERT INTO `book_reservation` VALUES (2003313351283945473, 7, 38, '2025-12-23 11:54:57', 2, '2025-12-30 11:54:57', 0, NULL, '2025-12-23 11:54:57', '2025-12-23 11:57:31', 4);
INSERT INTO `book_reservation` VALUES (2003314028886339585, 7, 38, '2025-12-23 11:57:39', 0, '2025-12-30 11:57:39', 1, '2025-12-23 11:58:04', '2025-12-23 11:57:39', '2025-12-23 11:58:04', 4);

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
  UNIQUE INDEX `uk_filename`(`filename`) USING BTREE,
  INDEX `idx_upload_user`(`upload_user_id`) USING BTREE,
  INDEX `idx_category`(`category`) USING BTREE,
  INDEX `idx_upload_time`(`upload_time`) USING BTREE,
  INDEX `idx_md5`(`md5`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 46 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of file_info
-- ----------------------------
INSERT INTO `file_info` VALUES (18, '共产党宣言.png', 'ec667b1b-3037-4c0b-9fa0-20c521a3f96b.png', 'C:\\Users\\DELL/bbms_project/uploads/共产党宣言.png', 220953, 'image/png', '/api/file/download/共产党宣言.png', '/file/共产党宣言.png', '2025-12-22 14:30:19', NULL, NULL, NULL, NULL, NULL, '图片', NULL, 1, 0, 1, 0, '2025-12-22 14:30:19', '2025-12-22 14:30:19');
INSERT INTO `file_info` VALUES (19, '辞海.png', '1832c799-893d-44c7-bb70-970346f0920d.png', 'C:\\Users\\DELL/bbms_project/uploads/辞海.png', 610702, 'image/png', '/api/file/download/辞海.png', '/file/辞海.png', '2025-12-22 14:37:52', NULL, NULL, NULL, NULL, NULL, '图片', NULL, 1, 0, 1, 0, '2025-12-22 14:37:52', '2025-12-22 14:37:52');
INSERT INTO `file_info` VALUES (20, '寂静的春天.png', 'ca314127-37c8-4897-9b3f-99bae3db6112.png', 'C:\\Users\\DELL/bbms_project/uploads/寂静的春天.png', 481501, 'image/png', '/api/file/download/寂静的春天.png', '/file/寂静的春天.png', '2025-12-22 14:38:43', NULL, NULL, NULL, NULL, NULL, '图片', NULL, 1, 0, 1, 0, '2025-12-22 14:38:43', '2025-12-22 14:38:43');
INSERT INTO `file_info` VALUES (21, '航天航空概论.png', 'cc6065ef-14a2-4f83-8921-99bfcf754900.png', 'C:\\Users\\DELL/bbms_project/uploads/航天航空概论.png', 584943, 'image/png', '/api/file/download/航天航空概论.png', '/file/航天航空概论.png', '2025-12-22 14:40:08', NULL, NULL, NULL, NULL, NULL, '图片', NULL, 1, 0, 1, 0, '2025-12-22 14:40:08', '2025-12-22 14:40:08');
INSERT INTO `file_info` VALUES (22, '交通大辞典.png', '3f14325d-063d-405e-b6b1-8c543a058617.png', 'C:\\Users\\DELL/bbms_project/uploads/交通大辞典.png', 330990, 'image/png', '/api/file/download/交通大辞典.png', '/file/交通大辞典.png', '2025-12-22 14:43:31', NULL, NULL, NULL, NULL, NULL, '图片', NULL, 1, 0, 1, 0, '2025-12-22 14:43:31', '2025-12-22 14:43:31');
INSERT INTO `file_info` VALUES (23, 'C++ Primer.png', '1e9ce681-d714-42aa-aab6-bf2315c61de8.png', 'C:\\Users\\DELL/bbms_project/uploads/C++ Primer.png', 426484, 'image/png', '/api/file/download/C++ Primer.png', '/file/C++ Primer.png', '2025-12-22 14:54:02', NULL, NULL, NULL, NULL, NULL, '图片', NULL, 1, 0, 1, 0, '2025-12-22 14:54:02', '2025-12-22 14:54:02');
INSERT INTO `file_info` VALUES (24, '杂交水稻学.png', '6847192b-93da-4b8c-b665-fdb233528542.png', 'C:\\Users\\DELL/bbms_project/uploads/杂交水稻学.png', 642567, 'image/png', '/api/file/download/杂交水稻学.png', '/file/杂交水稻学.png', '2025-12-22 14:54:52', NULL, NULL, NULL, NULL, NULL, '图片', NULL, 1, 0, 1, 0, '2025-12-22 14:54:52', '2025-12-22 14:54:52');
INSERT INTO `file_info` VALUES (25, '本草纲目.png', 'eecfbdbf-1399-4437-9a61-f0e7f76c338a.png', 'C:\\Users\\DELL/bbms_project/uploads/本草纲目.png', 412951, 'image/png', '/api/file/download/本草纲目.png', '/file/本草纲目.png', '2025-12-22 14:55:04', NULL, NULL, NULL, NULL, NULL, '图片', NULL, 1, 0, 1, 0, '2025-12-22 14:55:04', '2025-12-22 14:55:04');
INSERT INTO `file_info` VALUES (26, '物种起源.png', 'e7b5c65f-cdf3-479c-b88c-cd8eacb4c41a.png', 'C:\\Users\\DELL/bbms_project/uploads/物种起源.png', 278641, 'image/png', '/api/file/download/物种起源.png', '/file/物种起源.png', '2025-12-22 14:55:19', NULL, NULL, NULL, NULL, NULL, '图片', NULL, 1, 0, 1, 0, '2025-12-22 14:55:19', '2025-12-22 14:55:19');
INSERT INTO `file_info` VALUES (27, '时间简史.png', '8684e6c8-4583-449f-a0ce-178dd2b9cd49.png', 'C:\\Users\\DELL/bbms_project/uploads/时间简史.png', 731138, 'image/png', '/api/file/download/时间简史.png', '/file/时间简史.png', '2025-12-22 14:56:57', NULL, NULL, NULL, NULL, NULL, '图片', NULL, 1, 0, 1, 0, '2025-12-22 14:56:57', '2025-12-22 14:56:57');
INSERT INTO `file_info` VALUES (28, '几何原本.png', '6222c013-44fe-4f9b-a405-2ab0dab86e63.png', 'C:\\Users\\DELL/bbms_project/uploads/几何原本.png', 831116, 'image/png', '/api/file/download/几何原本.png', '/file/几何原本.png', '2025-12-22 14:59:33', NULL, NULL, NULL, NULL, NULL, '图片', NULL, 1, 0, 1, 0, '2025-12-22 14:59:33', '2025-12-22 14:59:33');
INSERT INTO `file_info` VALUES (29, '科学的旅程.png', '94882365-6f3b-4460-964d-5e48eb64653f.png', 'C:\\Users\\DELL/bbms_project/uploads/科学的旅程.png', 1039998, 'image/png', '/api/file/download/科学的旅程.png', '/file/科学的旅程.png', '2025-12-22 15:02:45', NULL, NULL, NULL, NULL, NULL, '图片', NULL, 1, 0, 1, 0, '2025-12-22 15:02:45', '2025-12-22 15:02:45');
INSERT INTO `file_info` VALUES (30, '什么是教育.png', '74db0279-3be6-4f26-957f-76cabfcaea86.png', 'C:\\Users\\DELL/bbms_project/uploads/什么是教育.png', 886003, 'image/png', '/api/file/download/什么是教育.png', '/file/什么是教育.png', '2025-12-22 15:39:52', NULL, NULL, NULL, NULL, NULL, '图片', NULL, 1, 0, 1, 0, '2025-12-22 15:39:52', '2025-12-22 15:39:52');
INSERT INTO `file_info` VALUES (31, '艺术的故事.png', '5fa25098-4d8a-45c9-aed3-12221193f569.png', 'C:\\Users\\DELL/bbms_project/uploads/艺术的故事.png', 272142, 'image/png', '/api/file/download/艺术的故事.png', '/file/艺术的故事.png', '2025-12-22 15:40:11', NULL, NULL, NULL, NULL, NULL, '图片', NULL, 1, 0, 1, 0, '2025-12-22 15:40:11', '2025-12-22 15:40:11');
INSERT INTO `file_info` VALUES (32, '语法讲义.png', 'c0014a40-74ba-4545-9546-40f5262e47b3.png', 'C:\\Users\\DELL/bbms_project/uploads/语法讲义.png', 288467, 'image/png', '/api/file/download/语法讲义.png', '/file/语法讲义.png', '2025-12-22 15:40:24', NULL, NULL, NULL, NULL, NULL, '图片', NULL, 1, 0, 1, 0, '2025-12-22 15:40:24', '2025-12-22 15:40:24');
INSERT INTO `file_info` VALUES (33, '全球通史.png', '1f87e33e-4bc3-4d10-ab1c-4b8ce12eb8fa.png', 'C:\\Users\\DELL/bbms_project/uploads/全球通史.png', 1098426, 'image/png', '/api/file/download/全球通史.png', '/file/全球通史.png', '2025-12-22 15:40:42', NULL, NULL, NULL, NULL, NULL, '图片', NULL, 1, 0, 1, 0, '2025-12-22 15:40:42', '2025-12-22 15:40:42');
INSERT INTO `file_info` VALUES (34, '国富论.png', 'f59603ac-c379-4d01-86fc-61a14528a185.png', 'C:\\Users\\DELL/bbms_project/uploads/国富论.png', 611363, 'image/png', '/api/file/download/国富论.png', '/file/国富论.png', '2025-12-22 15:43:49', NULL, NULL, NULL, NULL, NULL, '图片', NULL, 1, 0, 1, 0, '2025-12-22 15:43:49', '2025-12-22 15:43:49');
INSERT INTO `file_info` VALUES (35, '孙子兵法.png', '85d253b1-ab4c-4bd0-91e7-57c0e65e8375.png', 'C:\\Users\\DELL/bbms_project/uploads/孙子兵法.png', 137843, 'image/png', '/api/file/download/孙子兵法.png', '/file/孙子兵法.png', '2025-12-22 15:44:09', NULL, NULL, NULL, NULL, NULL, '图片', NULL, 1, 0, 1, 0, '2025-12-22 15:44:09', '2025-12-22 15:44:09');
INSERT INTO `file_info` VALUES (36, '论法的精神.png', 'a8655b00-7c19-483b-b562-0855f164b356.png', 'C:\\Users\\DELL/bbms_project/uploads/论法的精神.png', 130374, 'image/png', '/api/file/download/论法的精神.png', '/file/论法的精神.png', '2025-12-22 15:44:23', NULL, NULL, NULL, NULL, NULL, '图片', NULL, 1, 0, 1, 0, '2025-12-22 15:44:23', '2025-12-22 15:44:23');
INSERT INTO `file_info` VALUES (37, '乡土中国.png', '5bbf2276-328d-4650-8b2e-0ac38c10146a.png', 'C:\\Users\\DELL/bbms_project/uploads/乡土中国.png', 1061657, 'image/png', '/api/file/download/乡土中国.png', '/file/乡土中国.png', '2025-12-22 15:45:04', NULL, NULL, NULL, NULL, NULL, '图片', NULL, 1, 0, 1, 0, '2025-12-22 15:45:04', '2025-12-22 15:45:04');
INSERT INTO `file_info` VALUES (38, '红楼梦.png', '345a03fc-28c3-49c7-ab86-7e040650292c.png', 'C:\\Users\\DELL/bbms_project/uploads/红楼梦.png', 181376, 'image/png', '/api/file/download/红楼梦.png', '/file/红楼梦.png', '2025-12-22 15:50:29', NULL, NULL, NULL, NULL, NULL, '图片', NULL, 1, 0, 1, 0, '2025-12-22 15:50:29', '2025-12-22 15:50:29');
INSERT INTO `file_info` VALUES (39, '中国哲学简史.png', '4cf3c321-6c4d-4085-917e-eacb14acafce.png', 'C:\\Users\\DELL/bbms_project/uploads/中国哲学简史.png', 1396240, 'image/png', '/api/file/download/中国哲学简史.png', '/file/中国哲学简史.png', '2025-12-22 15:50:56', NULL, NULL, NULL, NULL, NULL, '图片', NULL, 1, 0, 1, 0, '2025-12-22 15:50:56', '2025-12-22 15:50:56');
INSERT INTO `file_info` VALUES (40, '活着.png', '5e0a80a9-e7de-42ab-a051-9556d76e7216.png', 'C:\\Users\\DELL/bbms_project/uploads/活着.png', 281620, 'image/png', '/api/file/download/活着.png', '/file/活着.png', '2025-12-22 16:01:27', NULL, NULL, NULL, NULL, NULL, '图片', NULL, 1, 0, 1, 0, '2025-12-22 16:01:27', '2025-12-22 16:01:27');
INSERT INTO `file_info` VALUES (41, '万历十五年.png', 'e5d0feb4-6d50-4e90-a241-4fbc2b956b97.png', 'C:\\Users\\DELL/bbms_project/uploads/万历十五年.png', 91970, 'image/png', '/api/file/download/万历十五年.png', '/file/万历十五年.png', '2025-12-22 16:15:52', NULL, NULL, NULL, NULL, NULL, '图片', NULL, 1, 0, 1, 0, '2025-12-22 16:15:52', '2025-12-22 16:15:52');
INSERT INTO `file_info` VALUES (42, '中国经济专题.png', '776bf403-c577-4502-b8bc-5c8bbf75539c.png', 'C:\\Users\\DELL/bbms_project/uploads/中国经济专题.png', 394727, 'image/png', '/api/file/download/中国经济专题.png', '/file/中国经济专题.png', '2025-12-22 17:56:27', NULL, NULL, NULL, NULL, NULL, '图片', NULL, 1, 0, 1, 0, '2025-12-22 17:56:27', '2025-12-22 17:56:27');
INSERT INTO `file_info` VALUES (43, '齐民要术.png', '087ea893-39ed-4f78-8205-97ed0b5afeb8.png', 'C:\\Users\\DELL/bbms_project/uploads/齐民要术.png', 131811, 'image/png', '/api/file/download/齐民要术.png', '/file/齐民要术.png', '2025-12-22 17:58:58', NULL, NULL, NULL, NULL, NULL, '图片', NULL, 1, 0, 1, 0, '2025-12-22 17:58:58', '2025-12-22 17:58:58');
INSERT INTO `file_info` VALUES (44, '围城.png', 'c8373b8f-9522-4545-b161-2ffa73042305.png', 'C:\\Users\\DELL/bbms_project/uploads/围城.png', 303364, 'image/png', '/api/file/download/围城.png', '/file/围城.png', '2025-12-22 18:05:50', NULL, NULL, NULL, NULL, NULL, '图片', NULL, 1, 0, 1, 0, '2025-12-22 18:05:50', '2025-12-22 18:05:50');
INSERT INTO `file_info` VALUES (45, '平凡的世界.png', 'bb95198c-5074-45dd-8faf-ffab9425e4c4.png', 'C:\\Users\\DELL/bbms_project/uploads/平凡的世界.png', 146587, 'image/png', '/api/file/download/平凡的世界.png', '/file/平凡的世界.png', '2025-12-22 18:09:43', NULL, NULL, NULL, NULL, NULL, '图片', NULL, 1, 0, 1, 0, '2025-12-22 18:09:43', '2025-12-22 18:09:43');
INSERT INTO `file_info` VALUES (46, '乌合之众.png', '55f9e9a6-8b7a-4296-8dea-896e7684629f.png', 'C:\\Users\\DELL/bbms_project/uploads/乌合之众.png', 86929, 'image/png', '/api/file/download/乌合之众.png', '/file/乌合之众.png', '2025-12-22 19:29:06', NULL, NULL, NULL, NULL, NULL, '图片', NULL, 1, 0, 1, 0, '2025-12-22 19:29:06', '2025-12-22 19:29:06');
INSERT INTO `file_info` VALUES (47, '娱乐至死.png', '47882ff6-9bab-4235-877d-c1b7747afa44.png', 'C:\\Users\\DELL/bbms_project/uploads/娱乐至死.png', 430881, 'image/png', '/api/file/download/娱乐至死.png', '/file/娱乐至死.png', '2025-12-23 11:02:03', NULL, NULL, NULL, NULL, NULL, '图片', NULL, 1, 0, 1, 0, '2025-12-23 11:02:04', '2025-12-23 11:02:04');

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
  UNIQUE INDEX `code`(`code`) USING BTREE,
  UNIQUE INDEX `name`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

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
  `book_id` bigint NULL DEFAULT NULL COMMENT '关联的图书ID',
  PRIMARY KEY (`message_id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  CONSTRAINT `sys_message_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2003016771389587459 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_message
-- ----------------------------
INSERT INTO `sys_message` VALUES (2003053037392891905, 0, 3, '书籍上架', '《平凡的世界》在2025-12-22 18:40:34成功上架。', 1, '2025-12-22 18:40:52', '2025-12-22 18:40:34', '2025-12-22 18:40:34', '2025-12-22 18:40:52', 39);
INSERT INTO `sys_message` VALUES (2003053037480972290, 0, 3, '书籍上架', '《围城》在2025-12-22 18:40:34成功上架。', 1, '2025-12-22 18:40:52', '2025-12-22 18:40:34', '2025-12-22 18:40:34', '2025-12-22 18:40:52', 38);
INSERT INTO `sys_message` VALUES (2003053037522915330, 0, 3, '书籍上架', '《辞海（第七版）》在2025-12-22 18:40:34成功上架。', 1, '2025-12-22 18:40:52', '2025-12-22 18:40:34', '2025-12-22 18:40:34', '2025-12-22 18:40:52', 26);
INSERT INTO `sys_message` VALUES (2003058462465069058, 7, 1, '预约上架', '您预约的《航空航天概论》已上架。', 1, '2025-12-23 10:26:15', '2025-12-22 19:02:07', '2025-12-22 19:02:07', '2025-12-23 10:26:15', 24);
INSERT INTO `sys_message` VALUES (2003058462494429185, 9, 1, '预约上架', '您预约的《航空航天概论》已上架。', 0, NULL, '2025-12-22 19:02:07', '2025-12-22 19:02:07', '2025-12-22 19:02:07', 24);
INSERT INTO `sys_message` VALUES (2003058462494429186, 0, 3, '书籍上架', '《航空航天概论》在2025-12-22 19:02:07成功上架。', 1, '2025-12-22 19:24:57', '2025-12-22 19:02:07', '2025-12-22 19:02:07', '2025-12-22 19:24:57', 24);
INSERT INTO `sys_message` VALUES (2003058574092275713, 9, 1, '预约上架', '您预约的《交通大辞典》已上架。', 0, NULL, '2025-12-22 19:02:34', '2025-12-22 19:02:34', '2025-12-22 19:02:34', 23);
INSERT INTO `sys_message` VALUES (2003058574130024449, 0, 3, '书籍上架', '《交通大辞典》在2025-12-22 19:02:34成功上架。', 1, '2025-12-22 19:24:57', '2025-12-22 19:02:34', '2025-12-22 19:02:34', '2025-12-22 19:24:57', 23);
INSERT INTO `sys_message` VALUES (2003061900192124929, 0, 4, '待确认还书', '用户【lin】申请归还书籍《平凡的世界》，请及时确认。', 1, '2025-12-22 19:24:57', '2025-12-22 19:15:47', '2025-12-22 19:15:47', '2025-12-22 19:24:57', 39);
INSERT INTO `sys_message` VALUES (2003062488120299522, 0, 4, '待确认还书', '用户【xiao】申请归还书籍《平凡的世界》，请及时确认。', 1, '2025-12-22 19:24:57', '2025-12-22 19:18:07', '2025-12-22 19:18:07', '2025-12-22 19:24:57', 39);
INSERT INTO `sys_message` VALUES (2003069223665012738, 0, 4, '待确认还书', '用户【lin】申请归还书籍《围城》，请及时确认。', 1, '2025-12-22 21:19:39', '2025-12-22 19:44:53', '2025-12-22 19:44:53', '2025-12-22 21:19:39', 38);
INSERT INTO `sys_message` VALUES (2003069317923606530, 10, 1, '预约归还', '您预约的《围城》已归还，请及时借阅。', 0, NULL, '2025-12-22 19:45:15', '2025-12-22 19:45:15', '2025-12-22 19:45:15', 38);
INSERT INTO `sys_message` VALUES (2003073166088474626, 0, 4, '待确认还书', '用户【lin】申请归还书籍《寂静的春天》，请及时确认。', 1, '2025-12-22 21:19:39', '2025-12-22 20:00:33', '2025-12-22 20:00:33', '2025-12-22 21:19:39', 25);
INSERT INTO `sys_message` VALUES (2003077536712626178, 0, 4, '待确认还书', '用户【lin】申请归还书籍《交通大辞典》，请及时确认。', 1, '2025-12-22 21:19:39', '2025-12-22 20:17:55', '2025-12-22 20:17:55', '2025-12-22 21:19:39', 23);
INSERT INTO `sys_message` VALUES (2003077543633227777, 0, 4, '待确认还书', '用户【lin】申请归还书籍《物种起源》，请及时确认。', 1, '2025-12-22 21:19:39', '2025-12-22 20:17:56', '2025-12-22 20:17:56', '2025-12-22 21:19:39', 19);
INSERT INTO `sys_message` VALUES (2003077550499303425, 0, 4, '待确认还书', '用户【lin】申请归还书籍《辞海（第七版）》，请及时确认。', 1, '2025-12-22 21:19:39', '2025-12-22 20:17:58', '2025-12-22 20:17:58', '2025-12-22 21:19:39', 26);
INSERT INTO `sys_message` VALUES (2003078770999824386, 0, 4, '待确认还书', '用户【lin】申请归还书籍《科学的旅程》，请及时确认。', 1, '2025-12-22 21:19:39', '2025-12-22 20:22:49', '2025-12-22 20:22:49', '2025-12-22 21:19:39', 16);
INSERT INTO `sys_message` VALUES (2003078791128289282, 0, 4, '待确认还书', '用户【lin】申请归还书籍《航空航天概论》，请及时确认。', 1, '2025-12-22 21:19:39', '2025-12-22 20:22:54', '2025-12-22 20:22:54', '2025-12-22 21:19:39', 24);
INSERT INTO `sys_message` VALUES (2003080541230993409, 0, 4, '待确认还书', '用户【lin】申请归还书籍《万历十五年》，请及时确认。', 1, '2025-12-22 21:19:39', '2025-12-22 20:29:51', '2025-12-22 20:29:51', '2025-12-22 21:19:39', 4);
INSERT INTO `sys_message` VALUES (2003080748815486978, 0, 4, '待确认还书', '用户【lin】申请归还书籍《物种起源》，请及时确认。', 1, '2025-12-22 21:19:39', '2025-12-22 20:30:41', '2025-12-22 20:30:41', '2025-12-22 21:19:39', 19);
INSERT INTO `sys_message` VALUES (2003080748815486979, 0, 4, '待确认还书', '用户【lin】申请归还书籍《艺术的故事》，请及时确认。', 1, '2025-12-22 21:19:39', '2025-12-22 20:30:41', '2025-12-22 20:30:41', '2025-12-22 21:19:39', 14);
INSERT INTO `sys_message` VALUES (2003082367602921474, 0, 4, '待确认还书', '用户【lin】申请归还书籍《C++ Primer》，请及时确认。', 1, '2025-12-22 21:19:39', '2025-12-22 20:37:07', '2025-12-22 20:37:07', '2025-12-22 21:19:39', 22);
INSERT INTO `sys_message` VALUES (2003083663764492289, 0, 4, '待确认还书', '用户【lin】申请归还书籍《几何原本》，请及时确认。', 1, '2025-12-22 21:19:39', '2025-12-22 20:42:16', '2025-12-22 20:42:16', '2025-12-22 21:19:39', 17);
INSERT INTO `sys_message` VALUES (2003083663764492290, 0, 4, '待确认还书', '用户【lin】申请归还书籍《什么是教育》，请及时确认。', 1, '2025-12-22 21:19:39', '2025-12-22 20:42:16', '2025-12-22 20:42:16', '2025-12-22 21:19:39', 11);
INSERT INTO `sys_message` VALUES (2003092942756225026, 0, 4, '待确认还书', '用户【lin】申请归还书籍《国富论》，请及时确认。', 1, '2025-12-22 21:19:39', '2025-12-22 21:19:08', '2025-12-22 21:19:08', '2025-12-22 21:19:39', 10);
INSERT INTO `sys_message` VALUES (2003285895348981761, 0, 4, '待确认还书', '用户【lin】申请归还书籍《论法的精神》，请及时确认。', 1, '2025-12-23 11:05:01', '2025-12-23 10:05:51', '2025-12-23 10:05:51', '2025-12-23 11:05:01', 8);
INSERT INTO `sys_message` VALUES (2003286251059515394, 0, 4, '待确认还书', '用户【xiao】申请归还书籍《围城》，请及时确认。', 1, '2025-12-23 11:05:01', '2025-12-23 10:07:16', '2025-12-23 10:07:16', '2025-12-23 11:05:01', 38);
INSERT INTO `sys_message` VALUES (2003290363008356354, 0, 4, '待确认还书', '用户【lin】申请归还书籍《平凡的世界》，请及时确认。', 1, '2025-12-23 11:05:01', '2025-12-23 10:23:37', '2025-12-23 10:23:37', '2025-12-23 11:05:01', 39);
INSERT INTO `sys_message` VALUES (2003290516259835906, 0, 4, '待确认还书', '用户【lin】申请归还书籍《C++ Primer》，请及时确认。', 1, '2025-12-23 11:05:01', '2025-12-23 10:24:13', '2025-12-23 10:24:13', '2025-12-23 11:05:01', 22);
INSERT INTO `sys_message` VALUES (2003290516259835907, 0, 4, '待确认还书', '用户【lin】申请归还书籍《物种起源》，请及时确认。', 1, '2025-12-23 11:05:01', '2025-12-23 10:24:13', '2025-12-23 10:24:13', '2025-12-23 11:05:01', 19);
INSERT INTO `sys_message` VALUES (2003293887653974018, 0, 4, '待确认还书', '用户【lin】申请归还书籍《科学的旅程》，请及时确认。', 1, '2025-12-23 11:05:01', '2025-12-23 10:37:37', '2025-12-23 10:37:37', '2025-12-23 11:05:01', 16);
INSERT INTO `sys_message` VALUES (2003293887700111362, 0, 4, '待确认还书', '用户【lin】申请归还书籍《全球通史：从史前史到21世纪》，请及时确认。', 1, '2025-12-23 11:05:01', '2025-12-23 10:37:37', '2025-12-23 10:37:37', '2025-12-23 11:05:01', 15);
INSERT INTO `sys_message` VALUES (2003296402969694210, 0, 4, '待确认还书', '用户【lin】申请归还书籍《艺术的故事》，请及时确认。', 1, '2025-12-23 11:05:01', '2025-12-23 10:47:37', '2025-12-23 10:47:37', '2025-12-23 11:05:01', 14);
INSERT INTO `sys_message` VALUES (2003296403011637250, 0, 4, '待确认还书', '用户【lin】申请归还书籍《万历十五年》，请及时确认。', 1, '2025-12-23 11:05:01', '2025-12-23 10:47:37', '2025-12-23 10:47:37', '2025-12-23 11:05:01', 4);
INSERT INTO `sys_message` VALUES (2003296419948236802, 0, 4, '待确认还书', '用户【lin】申请归还书籍《寂静的春天》，请及时确认。', 1, '2025-12-23 11:05:01', '2025-12-23 10:47:41', '2025-12-23 10:47:41', '2025-12-23 11:05:01', 25);
INSERT INTO `sys_message` VALUES (2003297746786947073, 0, 3, '书籍上架', '《社会契约论》在2025-12-23 10:52:57成功上架。', 1, '2025-12-23 11:05:01', '2025-12-23 10:52:57', '2025-12-23 10:52:57', '2025-12-23 11:05:01', 41);
INSERT INTO `sys_message` VALUES (2003298059015131137, 0, 3, '书籍上架', '《中国经济专题》在2025-12-23 10:54:11成功上架。', 1, '2025-12-23 11:05:01', '2025-12-23 10:54:11', '2025-12-23 10:54:11', '2025-12-23 11:05:01', 3);
INSERT INTO `sys_message` VALUES (2003299266316177410, 0, 3, '书籍上架', '《乌合之众》在2025-12-23 10:58:59成功上架。', 1, '2025-12-23 11:05:01', '2025-12-23 10:58:59', '2025-12-23 10:58:59', '2025-12-23 11:05:01', 40);
INSERT INTO `sys_message` VALUES (2003299434377744385, 0, 3, '书籍上架', '《本草纲目》在2025-12-23 10:59:39成功上架。', 1, '2025-12-23 11:05:01', '2025-12-23 10:59:39', '2025-12-23 10:59:39', '2025-12-23 11:05:01', 20);
INSERT INTO `sys_message` VALUES (2003299596575674370, 0, 3, '书籍上架', '《乡土中国》在2025-12-23 11:00:18成功上架。', 1, '2025-12-23 11:05:01', '2025-12-23 11:00:18', '2025-12-23 11:00:18', '2025-12-23 11:05:01', 7);
INSERT INTO `sys_message` VALUES (2003301824527044609, 0, 4, '待确认还书', '用户【lin】申请归还书籍《什么是教育》，请及时确认。', 0, NULL, '2025-12-23 11:09:09', '2025-12-23 11:09:09', '2025-12-23 11:09:09', 11);
INSERT INTO `sys_message` VALUES (2003301824527044610, 0, 4, '待确认还书', '用户【lin】申请归还书籍《物种起源》，请及时确认。', 0, NULL, '2025-12-23 11:09:09', '2025-12-23 11:09:09', '2025-12-23 11:09:09', 19);
INSERT INTO `sys_message` VALUES (2003311442825293825, 0, 3, '书籍上架', '《乌合之众》在2025-12-23 11:47:22成功上架。', 0, NULL, '2025-12-23 11:47:22', '2025-12-23 11:47:22', '2025-12-23 11:47:22', 40);
INSERT INTO `sys_message` VALUES (2003311442959511554, 0, 3, '书籍上架', '《娱乐至死》在2025-12-23 11:47:22成功上架。', 0, NULL, '2025-12-23 11:47:22', '2025-12-23 11:47:22', '2025-12-23 11:47:22', 42);
INSERT INTO `sys_message` VALUES (2003313245360992257, 0, 4, '待确认还书', '用户【lin】申请归还书籍《交通大辞典》，请及时确认。', 0, NULL, '2025-12-23 11:54:32', '2025-12-23 11:54:32', '2025-12-23 11:54:32', 23);
INSERT INTO `sys_message` VALUES (2003314086566408194, 0, 4, '待确认还书', '用户【xiao】申请归还书籍《围城》，请及时确认。', 0, NULL, '2025-12-23 11:57:53', '2025-12-23 11:57:53', '2025-12-23 11:57:53', 38);
INSERT INTO `sys_message` VALUES (2003314133760716801, 7, 1, '预约归还', '您预约的《围城》已归还，请及时借阅。', 0, NULL, '2025-12-23 11:58:04', '2025-12-23 11:58:04', '2025-12-23 11:58:04', 38);
INSERT INTO `sys_message` VALUES (2003315070994722818, 0, 4, '待确认还书', '用户【wan】申请归还书籍《乡土中国》，请及时确认。', 0, NULL, '2025-12-23 12:01:47', '2025-12-23 12:01:47', '2025-12-23 12:01:47', 7);
INSERT INTO `sys_message` VALUES (2003316255722024962, 0, 4, '待确认还书', '用户【wan】申请归还书籍《航空航天概论》，请及时确认。', 0, NULL, '2025-12-23 12:06:30', '2025-12-23 12:06:30', '2025-12-23 12:06:30', 24);
INSERT INTO `sys_message` VALUES (2003316530671235073, 0, 4, '待确认还书', '用户【xiao】申请归还书籍《本草纲目》，请及时确认。', 0, NULL, '2025-12-23 12:07:35', '2025-12-23 12:07:35', '2025-12-23 12:07:35', 20);
INSERT INTO `sys_message` VALUES (2003316639119159297, 0, 4, '待确认还书', '用户【xiao】申请归还书籍《物种起源》，请及时确认。', 0, NULL, '2025-12-23 12:08:01', '2025-12-23 12:08:01', '2025-12-23 12:08:01', 19);

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
  UNIQUE INDEX `role_code`(`role_code`) USING BTREE,
  UNIQUE INDEX `role_name`(`role_name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

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
  UNIQUE INDEX `uk_role_menu`(`role_id`, `menu_id`) USING BTREE COMMENT '唯一约束：同一角色不能重复关联同一菜单',
  INDEX `menu_id`(`menu_id`) USING BTREE,
  CONSTRAINT `sys_role_menu_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`role_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `sys_role_menu_ibfk_2` FOREIGN KEY (`menu_id`) REFERENCES `sys_menu` (`menu_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 47 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

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
  UNIQUE INDEX `account`(`account`) USING BTREE,
  UNIQUE INDEX `uid`(`uid`) USING BTREE,
  INDEX `role_id`(`role_id`) USING BTREE,
  CONSTRAINT `sys_user_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`role_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (0, 'adp', 'admin-pool', '', 4, 'admin-pool', NULL, 100, 1, NULL, NULL, 0, 0, 0, '2025-12-21 17:35:11', '2025-12-21 17:35:11', '2025-12-21 17:35:11', '2025-12-21 21:12:18');
INSERT INTO `sys_user` VALUES (1, 'admin', 'admin', 'WOQNqTJfVImWV3s0NgAl3A==:fSk/tmec2p+Ik1c5PWTSdWonWdj4Xh4vhm/FNmJTITM=', 5, 'U17651117344301425', NULL, 100, 1, NULL, NULL, 0, 0, 0, '2025-12-07 20:48:54', '2025-12-23 12:08:11', '2025-12-07 20:48:54', '2025-12-23 12:08:11');
INSERT INTO `sys_user` VALUES (2, 'user', 'user', 'a8XMlJPQyofoOT8wiliOHw==:P3pJbkr3eNMs/tvaKQE7cv66yEILTxqZkCoRzu92kIo=', 4, 'U17651227299965145', NULL, 100, 1, NULL, NULL, 0, 0, 0, '2025-12-07 23:52:10', '2025-12-23 10:26:47', '2025-12-07 23:52:10', '2025-12-23 10:26:47');
INSERT INTO `sys_user` VALUES (3, 'student', 'student', 'fDjIcM8C5fbDQpfKWBP5bw==:WmvMLyno3xroHEkBUXotfHznWw8RxVkv3PAVbTHcH+M=', 2, 'U17651640388962146', NULL, 100, 1, NULL, NULL, 0, 0, 0, '2025-12-08 11:20:39', '2025-12-19 11:00:48', '2025-12-08 11:20:39', '2025-12-19 15:04:49');
INSERT INTO `sys_user` VALUES (4, 'teacher', 'teacher', 'IuFqBOGkYzfby186axY2uw==:n0gy0LsI5c93/Dj6MEY7XyEHjAG+AY+8CJwRtVWlxcw=', 3, 'U17653275581594518', NULL, 100, 1, NULL, NULL, 0, 0, 0, '2025-12-10 08:45:58', '2025-12-19 11:01:01', '2025-12-10 08:45:58', '2025-12-19 15:04:45');
INSERT INTO `sys_user` VALUES (5, 'social', 'social', 'N25CWubVHMI21HwhS4fpFQ==:xsTdo1sdekD9+U9cuTNd4tM/A4wRALBsNpLsWXlrISk=', 1, 'U17657646348166858', NULL, 100, 1, NULL, NULL, 0, 0, 0, '2025-12-15 10:10:35', '2025-12-19 12:28:01', '2025-12-15 10:10:35', '2025-12-19 12:28:01');
INSERT INTO `sys_user` VALUES (6, 'test1', 'test1', 'YpxDEaIoIb3fSuU5/2YpbQ==:wo6p5enxzL6zTFJ5NnVfhGMtfaMrHRpYsUb6iQ2MDgY=', 4, 'U17657651446871538', NULL, 100, 1, NULL, NULL, 0, 0, 0, '2025-12-15 10:19:05', NULL, '2025-12-15 10:19:05', '2025-12-15 10:19:05');
INSERT INTO `sys_user` VALUES (7, 'lin', 'lin', 'vEePfnDasLP5HAVMFcZYrA==:QiaeRaO7MkBVDl4kuZKMCD1MVxP2AS7NpFgMMAPGCIc=', 1, 'U17661134482701828', NULL, 100, 1, NULL, NULL, 0, 0, 0, '2025-06-01 11:04:08', '2025-12-23 11:58:14', '2025-12-19 11:04:08', '2025-12-23 11:58:14');
INSERT INTO `sys_user` VALUES (8, 'wuu', 'wuu', '/aPYBndt8QZ6DMWOIgExww==:7qd0GurH1wkpuJqsyloXpAFZp6gAhZAtJxqj6+p4yX8=', 4, 'U17661134593803369', NULL, 100, 1, NULL, NULL, 0, 0, 0, '2025-12-19 11:04:19', '2025-12-23 11:56:29', '2025-12-19 11:04:19', '2025-12-23 11:56:29');
INSERT INTO `sys_user` VALUES (9, 'xiao', 'xiao', 'WQhn3BzkxTxAFCFrwSAIbQ==:RBdN9V50mUUvsJHY0/U9XublG6W+OWzLwht7Y5jgX+k=', 3, 'U17661134694973529', NULL, 100, 1, NULL, NULL, 0, 2, 0, '2025-06-19 11:04:29', '2025-12-23 12:06:49', '2025-12-19 11:04:29', '2025-12-23 12:06:49');
INSERT INTO `sys_user` VALUES (10, 'wan', 'wan', 'k3gIYgkPP1eGozm8m8WDRQ==:G3ORvkQWoS1p4CpccQfEqUcMdRljinH+uQ8Y4wtY74k=', 2, 'U17661134801528890', NULL, 100, 1, NULL, NULL, 0, 0, 0, '2025-12-19 11:04:40', '2025-12-23 12:06:26', '2025-12-19 11:04:40', '2025-12-23 12:06:26');
INSERT INTO `sys_user` VALUES (11, 'han', 'han', 'y6Hy/RxYDl/tjk9RmWBghQ==:NKwJKTyyB+itC7LdlNiS56pqqgsxIW1QvztefPqWZgQ=', 4, 'U17661134898034418', NULL, 100, 1, NULL, NULL, 0, 1, 0, '2025-12-19 11:04:50', '2025-12-19 14:47:53', '2025-12-19 11:04:50', '2025-12-19 15:06:24');
INSERT INTO `sys_user` VALUES (12, 'social2', 'social2', 'ECiEtwUzSa9LbuxsCXvGEw==:ps2nPgYcLObzZAZ5eYGIvV56qcKbmxxOwkpkzl+eUiQ=', 1, 'U17661135143508717', NULL, 100, 1, NULL, NULL, 0, 0, 0, '2025-12-19 11:05:14', NULL, '2025-12-19 11:05:14', '2025-12-19 15:06:25');
INSERT INTO `sys_user` VALUES (25, 'abc', 'abc', '5deyzSb+o1LreNK+6U+DyA==:MPZCXqfHyUxkUUXKKOdJhXUTqJjq4oC4J/hD3kq/7mE=', 4, 'U17664586172892546', NULL, 100, 1, NULL, NULL, 0, 0, 0, '2025-12-23 10:56:57', '2025-12-23 10:57:04', '2025-12-23 10:56:57', '2025-12-23 10:57:04');

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
  UNIQUE INDEX `uk_user_role`(`user_id`, `role_id`) USING BTREE,
  INDEX `role_id`(`role_id`) USING BTREE,
  CONSTRAINT `sys_user_role_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `sys_user_role_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`role_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1, 5, '2025-12-07 20:48:54');
INSERT INTO `sys_user_role` VALUES (3, 3, 2, '2025-12-08 11:20:39');
INSERT INTO `sys_user_role` VALUES (4, 4, 3, '2025-12-10 08:45:58');
INSERT INTO `sys_user_role` VALUES (6, 2, 4, '2025-12-15 00:05:35');
INSERT INTO `sys_user_role` VALUES (8, 5, 1, '2025-12-15 10:11:23');
INSERT INTO `sys_user_role` VALUES (10, 6, 4, '2025-12-15 10:19:23');
INSERT INTO `sys_user_role` VALUES (11, 7, 1, '2025-12-19 11:04:08');
INSERT INTO `sys_user_role` VALUES (12, 8, 4, '2025-12-19 11:04:19');
INSERT INTO `sys_user_role` VALUES (13, 9, 3, '2025-12-19 11:04:30');
INSERT INTO `sys_user_role` VALUES (14, 10, 2, '2025-12-19 11:04:40');
INSERT INTO `sys_user_role` VALUES (16, 12, 1, '2025-12-19 11:05:14');
INSERT INTO `sys_user_role` VALUES (18, 11, 4, '2025-12-19 15:05:44');
INSERT INTO `sys_user_role` VALUES (31, 25, 4, '2025-12-23 11:06:19');

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
  INDEX `user_id`(`user_id`) USING BTREE,
  CONSTRAINT `user_credit_history_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_credit_history
-- ----------------------------

-- ----------------------------
-- Procedure structure for insert_books
-- ----------------------------
DROP PROCEDURE IF EXISTS `insert_books`;
delimiter ;;
CREATE PROCEDURE `insert_books`()
BEGIN
  DECLARE i INT DEFAULT 1;
  -- 用变量+随机数替代数组，直接随机选1-22的分类ID
  DECLARE author_prefix VARCHAR(20);
  DECLARE publisher_prefix VARCHAR(20);
  DECLARE publish_year INT;
  
  WHILE i <= 100 DO
    -- 随机选分类、作者、出版社等
    SET author_prefix = CONCAT('作者', FLOOR(RAND()*50 + 1));
    SET publisher_prefix = CONCAT('出版社', FLOOR(RAND()*20 + 1));
    SET publish_year = FLOOR(RAND()*20 + 2000); -- 2000-2020年出版
    
    INSERT INTO book_info (
      book_name, cover_url, author, translator, category_id,
      book_status, total_count, available_count, shelf_time,
      intro, publisher, isbn, copyright_holder, publish_count,
      publish_unit, publish_website, publish_batch, publish_date,
      price, borrow_count, create_time, update_time
    ) VALUES (
      CONCAT('图书名称', i), -- 书名
      CONCAT('https://cover.com/', i, '.jpg'), -- 封面URL（示例）
      author_prefix, -- 作者
      IF(RAND() > 0.5, CONCAT('译者', FLOOR(RAND()*30 + 1)), NULL), -- 随机有无译者
      FLOOR(RAND()*22 + 1), -- 直接随机选1-22的分类ID（对应book_category）
      1, -- 可借
      FLOOR(RAND()*5 + 1), -- 总数量1-5本
      FLOOR(RAND()*5 + 1), -- 可借数量（同总数量）
      DATE_SUB(NOW(), INTERVAL FLOOR(RAND()*365) DAY), -- 上架时间（近1年）
      CONCAT('这是《图书名称', i, '》的简介，内容丰富，适合阅读。'), -- 简介
      publisher_prefix, -- 出版社
      CONCAT('978-', FLOOR(RAND()*1000000000000 + 100000000000)), -- 随机ISBN
      publisher_prefix, -- 版权方（同出版社）
      FLOOR(RAND()*3 + 1), -- 印刷次数1-3
      CONCAT('印刷单位', FLOOR(RAND()*10 + 1)), -- 印刷单位
      CONCAT('https://publish.com/', publisher_prefix, '.com'), -- 出版社官网
      CONCAT('2025-', FLOOR(RAND()*12 + 1), '-', FLOOR(RAND()*28 + 1)), -- 印刷批次
      STR_TO_DATE(CONCAT(publish_year, '-', FLOOR(RAND()*12 + 1), '-', FLOOR(RAND()*28 + 1)), '%Y-%m-%d'), -- 出版日期
      ROUND(RAND()*100 + 20, 2), -- 价格20-120元
      0, -- 初始借阅次数0
      NOW(), -- 创建时间
      NOW() -- 更新时间
    );
    SET i = i + 1;
  END WHILE;
END
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
