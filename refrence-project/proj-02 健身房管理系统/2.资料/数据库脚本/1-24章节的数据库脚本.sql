/*
SQLyog Ultimate v10.00 Beta1
MySQL - 5.7.28 : Database - gymnasium
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`gymnasium` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `gymnasium`;

/*Table structure for table `course` */

DROP TABLE IF EXISTS `course`;

CREATE TABLE `course` (
  `course_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '课程id',
  `course_name` varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '课程名称',
  `image` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '封面图',
  `teacher_name` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '授课教师',
  `course_hour` int(11) DEFAULT NULL COMMENT '课时',
  `course_details` text COLLATE utf8mb4_bin COMMENT '课程详情',
  `course_price` decimal(18,2) DEFAULT NULL COMMENT '课程价格',
  PRIMARY KEY (`course_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC;

/*Data for the table `course` */

insert  into `course`(`course_id`,`course_name`,`image`,`teacher_name`,`course_hour`,`course_details`,`course_price`) values (1,'跆拳道','http://localhost:8089/images/cdb847d1-0503-4b24-b99d-9f37ae65a4e5.jpg','李四',50,'<p>非常实用!333</p>','500.00'),(2,'篮球专训11','http://localhost:8089/images/7c01a1f5-a1c6-4bd1-8508-e859062c7041.jpg','李四',60,'<p>非常精彩的技术讲解，演示，实际操练11;</p><p><img src=\"http://localhost:8089/images/e2cab6c1-b6d8-4368-8bd5-52bafa69b384.jpg\" alt=\"\" data-href=\"\" style=\"\"/></p>','10001.00'),(3,'99','http://localhost:8089/images/0890a18a-e906-49f2-b1f5-2a59d0bfd635.jpg','王五',99,'<p><img src=\"http://localhost:8089/images/f62b0eb2-cb2e-4c17-b094-ff00b67af453.jpg\" alt=\"\" data-href=\"\" style=\"\"/></p><p>非常好！</p>','99.00'),(4,'足球专训','http://localhost:8089/images/d0674c5b-2ef8-466d-bb0c-ee51c568b6a8.jpg','李四',60,'<p>实际操练，非常好!</p><p style=\"text-align: center;\"><img src=\"http://localhost:8089/images/a50ffd0a-0d1c-42c3-bf8b-9e8872fa062d.webp\" alt=\"\" data-href=\"\" style=\"\"></p>','666.00'),(5,'测试','http://localhost:8089/images/1ef1fe21-be21-48c2-aecb-c1cd2094b7e3.jpg','李四',100,'<p>测试</p>','2000.00');

/*Table structure for table `goods` */

DROP TABLE IF EXISTS `goods`;

CREATE TABLE `goods` (
  `goods_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '商品id',
  `name` varchar(200) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '名称',
  `price` decimal(18,2) DEFAULT NULL COMMENT '价格',
  `store` int(11) DEFAULT NULL COMMENT '库存',
  `image` varchar(220) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '图片',
  `details` text COLLATE utf8mb4_bin COMMENT '描述',
  `unit` varchar(36) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '单位',
  `specs` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '规格',
  PRIMARY KEY (`goods_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC;

/*Data for the table `goods` */

insert  into `goods`(`goods_id`,`name`,`price`,`store`,`image`,`details`,`unit`,`specs`) values (1,'冰红茶','6.00',100,'http://localhost:8089/images/6f889207-6017-4bc3-8a2c-81a95e203c08.jpg','<p>呵呵</p><p><img src=\"http://localhost:8089/images/777054cb-62a3-4bed-84d5-6211d905ece6.jpg\" alt=\"\" data-href=\"\" style=\"\"/></p>','瓶','500ml/瓶'),(2,'测试666','1166.00',1166,'http://localhost:8089/images/014293e5-da41-417e-877c-1c21af364300.jpg','<p>412434</p><p><img src=\"http://localhost:8089/images/3818e664-9ccb-46b0-b09a-63dc0c25659c.webp\" alt=\"\" data-href=\"\" style=\"\"/></p>','1166','1166'),(8,'22','22.00',2,'http://localhost:8089/images/27662d82-9647-4753-a929-4434e6dd0fcd.jpg','<p>44444</p>','22','22'),(9,'33','33.00',33,'http://localhost:8089/images/27cd0d92-efaa-4919-a0cc-9a8544418d91.jpg','<p>33333</p>','33','33');

/*Table structure for table `lost` */

DROP TABLE IF EXISTS `lost`;

CREATE TABLE `lost` (
  `lost_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `lost_name` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '名称',
  `found_time` varchar(36) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '捡到时间',
  `found_addres` varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '捡到地址',
  `found_person` varchar(36) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '捡到人',
  `found_phone` varchar(15) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '捡到人电话',
  `status` varchar(2) COLLATE utf8mb4_bin DEFAULT NULL COMMENT ' 认领状态 0：未认领 1：已认领',
  `lost_person` varchar(36) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '认领人',
  PRIMARY KEY (`lost_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC;

/*Data for the table `lost` */

insert  into `lost`(`lost_id`,`lost_name`,`found_time`,`found_addres`,`found_person`,`found_phone`,`status`,`lost_person`) values (1,'钱包','2022-06-28','大厅','李四','18787171906','1','张明'),(2,'手机','2022-06-02','厕所','张三','187871719036','1','赵柳'),(3,'测试','2022-06-28','11','11','11','1','李记'),(5,'777','2022-06-08','77','77','77','1','77');

/*Table structure for table `material` */

DROP TABLE IF EXISTS `material`;

CREATE TABLE `material` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '器材id',
  `name` varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '器材名称',
  `num_total` int(11) DEFAULT NULL COMMENT '数量',
  `details` text COLLATE utf8mb4_bin COMMENT '简介',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC;

/*Data for the table `material` */

insert  into `material`(`id`,`name`,`num_total`,`details`) values (1,'跑步机',10,'跑步机'),(2,'篮球',20,'篮球'),(3,'测试',12,'测试');

/*Table structure for table `member` */

DROP TABLE IF EXISTS `member`;

CREATE TABLE `member` (
  `member_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '会员id',
  `name` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '姓名',
  `sex` varchar(2) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '性别',
  `phone` varchar(15) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '电话',
  `age` int(11) DEFAULT NULL COMMENT '年龄',
  `birthday` varchar(36) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '生日',
  `height` int(11) DEFAULT NULL COMMENT '身高',
  `weight` int(11) DEFAULT NULL COMMENT '体重',
  `waist` int(11) DEFAULT NULL COMMENT '腰围',
  `join_time` varchar(36) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '加入时间',
  `end_time` varchar(36) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '到期时间',
  `username` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '会员卡号',
  `password` varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '密码',
  `status` varchar(2) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '状态 1：启用  0：停用',
  `money` decimal(18,2) DEFAULT '0.00' COMMENT '充值金额',
  `card_type` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '会员类型',
  `card_day` int(11) DEFAULT NULL COMMENT '天数',
  `price` decimal(18,2) DEFAULT '0.00' COMMENT '会员价格',
  PRIMARY KEY (`member_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC;

/*Data for the table `member` */

insert  into `member`(`member_id`,`name`,`sex`,`phone`,`age`,`birthday`,`height`,`weight`,`waist`,`join_time`,`end_time`,`username`,`password`,`status`,`money`,`card_type`,`card_day`,`price`) values (1,'张三','0','18787171906',100,'2022-06-15',180,200,100,'2022-06-16','2022-06-23','2022001','1234','1','700.00','7天卡',7,'500.00'),(2,'李四','1','18787171906',NULL,'',NULL,NULL,NULL,'2022-06-07','2022-06-24','2022002','1234','1','300.00','7天卡',7,'500.00'),(3,'王五','0','187871523300',NULL,'',NULL,NULL,NULL,'2022-06-05','','2022003','1234','1','0.00',NULL,NULL,'0.00');

/*Table structure for table `member_apply` */

DROP TABLE IF EXISTS `member_apply`;

CREATE TABLE `member_apply` (
  `apply_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `member_id` int(11) DEFAULT NULL COMMENT '会员id',
  `card_type` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '会员类型',
  `card_day` int(11) DEFAULT NULL COMMENT '天数',
  `price` decimal(18,2) DEFAULT '0.00' COMMENT '价格',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_user` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`apply_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC;

/*Data for the table `member_apply` */

insert  into `member_apply`(`apply_id`,`member_id`,`card_type`,`card_day`,`price`,`create_time`,`create_user`) values (1,1,'7天卡',7,'500.00','2022-06-16 16:11:49',NULL),(2,2,'1天体验卡',1,'1.00','2022-06-16 16:12:13',NULL),(3,2,'7天卡',7,'500.00','2022-06-16 16:12:28',NULL);

/*Table structure for table `member_card` */

DROP TABLE IF EXISTS `member_card`;

CREATE TABLE `member_card` (
  `card_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '标题',
  `card_type` varchar(5) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '卡类型  1:天卡  2：周卡 3：月卡 4：年卡',
  `price` decimal(18,2) DEFAULT NULL COMMENT '价格',
  `card_day` int(11) DEFAULT NULL COMMENT '天数',
  `status` varchar(2) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '状态 1：启用 0：停用',
  PRIMARY KEY (`card_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC;

/*Data for the table `member_card` */

insert  into `member_card`(`card_id`,`title`,`card_type`,`price`,`card_day`,`status`) values (1,'1天体验卡','1','1.00',1,'1'),(2,'7天卡','2','500.00',7,'1'),(3,'1月卡','3','999.00',30,'1');

/*Table structure for table `member_recharge` */

DROP TABLE IF EXISTS `member_recharge`;

CREATE TABLE `member_recharge` (
  `recharge_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '充值id',
  `member_id` int(11) DEFAULT NULL COMMENT '会员id',
  `money` decimal(18,2) DEFAULT '0.00' COMMENT '充值金额',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_user` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`recharge_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC;

/*Data for the table `member_recharge` */

insert  into `member_recharge`(`recharge_id`,`member_id`,`money`,`create_time`,`create_user`) values (3,1,'300.00','2022-06-16 16:32:11',NULL),(4,1,'100.00','2022-06-16 16:32:54',NULL),(5,1,'300.00','2022-06-16 16:33:03',NULL),(6,2,'200.00','2022-06-16 16:33:40',NULL),(7,2,'100.00','2022-06-16 16:33:49',NULL);

/*Table structure for table `member_role` */

DROP TABLE IF EXISTS `member_role`;

CREATE TABLE `member_role` (
  `member_role_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '会员角色id',
  `member_id` int(11) DEFAULT NULL COMMENT '会员id',
  `role_id` int(11) DEFAULT NULL COMMENT '角色id',
  PRIMARY KEY (`member_role_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC;

/*Data for the table `member_role` */

insert  into `member_role`(`member_role_id`,`member_id`,`role_id`) values (12,2,5),(14,3,5),(16,1,5);

/*Table structure for table `suggest` */

DROP TABLE IF EXISTS `suggest`;

CREATE TABLE `suggest` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(200) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '标题',
  `content` text COLLATE utf8mb4_bin COMMENT '反馈内容',
  `date_time` datetime DEFAULT NULL COMMENT '反馈时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC;

/*Data for the table `suggest` */

insert  into `suggest`(`id`,`title`,`content`,`date_time`) values (1,'器材太少','建议增加器材','2022-06-29 19:07:32'),(4,'22','22','2022-06-29 19:51:14'),(5,'33','33','2022-06-29 19:53:04');

/*Table structure for table `sys_menu` */

DROP TABLE IF EXISTS `sys_menu`;

CREATE TABLE `sys_menu` (
  `menu_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '菜单id',
  `parent_id` int(11) NOT NULL COMMENT '父级id',
  `title` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '菜单名称',
  `code` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '权限字段',
  `name` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '路由名称',
  `path` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '路由path',
  `url` varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '组件路径',
  `type` varchar(2) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '类型(0 目录 1菜单，2按钮)',
  `icon` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '图标',
  `parent_name` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '上级菜单名称',
  `order_num` int(11) DEFAULT NULL COMMENT '序号',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC;

/*Data for the table `sys_menu` */

insert  into `sys_menu`(`menu_id`,`parent_id`,`title`,`code`,`name`,`path`,`url`,`type`,`icon`,`parent_name`,`order_num`,`create_time`,`update_time`) values (1,0,'系统管理','sys:manage','system','/system','','0','Setting','顶级菜单',1,'2022-06-13 11:20:17',NULL),(2,1,'员工管理','sys:user','userList','/userList','/system/user/UserList','1','UserFilled','系统管理',2,'2022-06-13 11:24:39','2022-06-13 14:21:37'),(3,1,'角色管理','sys:role','roleList','/roleList','/system/role/RoleList','1','Wallet','系统管理',3,'2022-06-13 11:26:25',NULL),(4,0,'会员管理','sys:memberRoot','memberRoot','/memberRoot','','0','Setting','顶级菜单',2,'2022-06-13 13:30:37',NULL),(5,4,'会员卡类型','sys:cardType','cardType','/cardType','/member/type/CardType','1','UserFilled','会员管理',4,'2022-06-13 13:31:42',NULL),(6,4,'会员管理','sys:memberList','memberList','/memberList','/member/list/MemberList','1','Wallet','会员管理',5,'2022-06-13 13:32:38','2022-06-13 14:21:51'),(7,4,'我的充值','sys:myFee','myFee','/myFee','/member/fee/MyFee','1','Menu','会员管理',6,'2022-06-13 13:33:24','2022-07-02 18:40:53'),(8,2,'新增','sys:user:add','','','','2','','员工管理',1,'2022-06-13 13:33:56',NULL),(9,2,'编辑','sys:user:edit','','','','2','','员工管理',2,'2022-06-13 13:34:16',NULL),(10,2,'删除','sys:user:delete','','','','2','','员工管理',3,'2022-06-13 13:34:34','2022-06-13 14:21:06'),(11,3,'新增','sys:role:add','','','','2','','角色管理',1,'2022-06-13 13:35:01',NULL),(12,3,'编辑','sys:role:edit','','','','2','','角色管理',2,'2022-06-13 13:35:17',NULL),(13,3,'删除','sys:role:delete','','','','2','','角色管理',3,'2022-06-13 13:35:33',NULL),(14,5,'新增','sys:memberRoot:add','','','','2','','会员卡类型',1,'2022-06-13 13:35:59',NULL),(15,5,'编辑','sys:memberRoot:edit','','','','2','','会员卡类型',2,'2022-06-13 13:36:21',NULL),(16,5,'删除','sys:memberRoot:delete','','','','2','','会员卡类型',3,'2022-06-13 13:36:35',NULL),(17,1,'菜单管理','sys:menu','menuList','/menuList','/system/menu/MenuList','1','Menu','系统管理',7,'2022-07-02 18:19:56',NULL),(18,17,'新增','sys:menu:add','','','','2','','菜单管理',8,'2022-07-02 18:20:23',NULL),(19,17,'编辑','sys:menu:edit','','','','2','','菜单管理',9,'2022-07-02 18:20:41',NULL),(20,17,'删除','sys:menu:delete','','','','2','','菜单管理',11,'2022-07-02 18:20:58',NULL),(21,6,'新增','sys:memberList:add','','','','2','','会员管理',12,'2022-07-02 18:22:06',NULL),(22,6,'编辑','sys:memberList:edit','','','','2','','会员管理',13,'2022-07-02 18:22:32',NULL),(23,6,'删除','sys:memberList:delete','','','','2','','会员管理',14,'2022-07-02 18:22:49',NULL),(24,6,'充值','sys:memberList:rechart','','','','2','','会员管理',15,'2022-07-02 18:23:41',NULL),(25,6,'办卡','sys:memberList:setCard','','','','2','','会员管理',16,'2022-07-02 18:24:18',NULL),(26,0,'课程管理','sys:courseRoot','courseRoot','/courseRoot','','0','ScaleToOriginal','顶级菜单',18,'2022-07-02 18:25:24',NULL),(27,26,'课程列表','sys:courseList','courseList','/courseList','/course/CourseList','1','UserFilled','课程管理',19,'2022-07-02 18:26:24',NULL),(28,27,'新增','sys:courseList:add','','','','2','','课程列表',20,'2022-07-02 18:26:45',NULL),(29,27,'编辑','sys:courseList:edit','','','','2','','课程列表',21,'2022-07-02 18:27:02',NULL),(30,27,'删除','sys:courseList:delete','','','','2','','课程列表',22,'2022-07-02 18:27:19',NULL),(31,26,'我的课程','sys:mycourse','mycourse','/mycourse','/mycourse/mycourse','1','Wallet','课程管理',23,'2022-07-02 18:28:21',NULL),(32,0,'器材管理','sys:materialList','materialList','/materialList','/material/MaterialList','1','UserFilled','顶级菜单',24,'2022-07-02 18:29:36',NULL),(33,32,'新增','sys:materialList:add','','','','2','','器材管理',25,'2022-07-02 18:29:57',NULL),(34,32,'编辑','sys:materialList:edit','','','','2','','器材管理',26,'2022-07-02 18:30:15',NULL),(35,32,'删除','sys:materialList:delete','','','','2','','器材管理',27,'2022-07-02 18:30:31',NULL),(36,0,'商品管理','sys:goodsRoot','goodsRoot','/goodsRoot','','0','Document','顶级菜单',30,'2022-07-02 18:31:25',NULL),(37,36,'商品列表','sys:goodsList','goodsList','/goodsList','/goods/GoodsList','1','UserFilled','商品管理',31,'2022-07-02 18:32:18',NULL),(38,37,'新增','sys:goodsList:add','','','','2','','商品列表',32,'2022-07-02 18:32:44',NULL),(39,37,'编辑','sys:goodsList:edit','','','','2','','商品列表',33,'2022-07-02 18:33:01',NULL),(40,37,'删除','sys:goodsList:delete','','','','2','','商品列表',34,'2022-07-02 18:33:19',NULL),(41,36,'订单管理','sys:orderList','orderList','/orderList','/order/OrderList','1','UserFilled','商品管理',35,'2022-07-02 18:34:12',NULL),(42,51,'失物列表','sys:lostList','lostList','/lostList','/lost/LostList','1','UserFilled','失物招领1',36,'2022-07-02 18:35:06','2022-07-02 20:12:07'),(43,42,'新增','sys:lostList:add','','','','2','','失物招领',37,'2022-07-02 18:35:28',NULL),(44,42,'编辑','sys:lostList:edit','','','','2','','失物招领',38,'2022-07-02 18:35:47',NULL),(45,42,'删除','sys:lostList:delete','','','','2','','失物招领',39,'2022-07-02 18:36:03',NULL),(46,52,'反馈列表','sys:suggestList','suggestList','/suggestList','/suggest/SuggestList','1','UserFilled','反馈管理',40,'2022-07-02 18:37:12','2022-07-02 20:13:51'),(47,46,'新增','sys:suggestList:add','','','','2','','反馈管理',42,'2022-07-02 18:37:39',NULL),(48,46,'编辑','sys:suggestList:edit','','','','2','','反馈管理',43,'2022-07-02 18:37:55',NULL),(49,46,'删除','sys:suggestList:delete','','','','2','','反馈管理',44,'2022-07-02 18:38:11',NULL),(50,42,'查看','sys:lostList:look','','','','2','','失物招领',45,'2022-07-02 20:00:09',NULL),(51,0,'失物招领','sys:lostRoot','lostRoot','/lostRoot','','0','Document','顶级菜单',36,'2022-07-02 20:10:57','2022-07-02 20:12:14'),(52,0,'反馈管理','sys:suggestRoot','suggestRoot','/suggestRoot','/suggest/SuggestList','0','Document','顶级菜单',40,'2022-07-02 20:13:40',NULL);

/*Table structure for table `sys_role` */

DROP TABLE IF EXISTS `sys_role`;

CREATE TABLE `sys_role` (
  `role_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '角色id',
  `role_name` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '角色名称',
  `remark` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`role_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC;

/*Data for the table `sys_role` */

insert  into `sys_role`(`role_id`,`role_name`,`remark`,`create_time`,`update_time`) values (2,'系统管理员','系统管理员','2022-06-05 19:28:40','2022-06-11 22:12:40'),(3,'员工','员工','2022-06-05 19:47:41','2022-06-11 22:12:56'),(4,'教练','教练','2022-06-05 19:50:52','2022-06-11 22:13:08'),(5,'会员','会员','2022-06-16 10:30:36',NULL);

/*Table structure for table `sys_role_menu` */

DROP TABLE IF EXISTS `sys_role_menu`;

CREATE TABLE `sys_role_menu` (
  `role_menu_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `role_id` int(11) DEFAULT NULL COMMENT '角色id',
  `menu_id` int(11) DEFAULT NULL COMMENT '菜单id',
  PRIMARY KEY (`role_menu_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC;

/*Data for the table `sys_role_menu` */

insert  into `sys_role_menu`(`role_menu_id`,`role_id`,`menu_id`) values (7,2,1),(8,2,2),(9,2,8),(10,2,9),(11,2,10),(12,2,3),(13,2,11),(14,2,12),(15,2,13),(18,3,7),(19,3,4),(28,4,27),(29,4,28),(30,4,29),(31,4,30),(32,4,32),(33,4,33),(34,4,34),(35,4,35),(36,4,42),(37,4,43),(38,4,44),(39,4,45),(40,4,50),(41,4,26),(42,5,31),(43,5,50),(44,5,52),(45,5,46),(46,5,47),(47,5,48),(48,5,49),(49,5,26),(50,5,51),(51,5,42);

/*Table structure for table `sys_user` */

DROP TABLE IF EXISTS `sys_user`;

CREATE TABLE `sys_user` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '员工id',
  `username` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '账户(员工编号)',
  `password` varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '密码',
  `phone` varchar(15) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '电话',
  `email` varchar(36) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '邮箱',
  `sex` varchar(2) COLLATE utf8mb4_bin DEFAULT NULL COMMENT ' 性别  0：男 1：女',
  `salary` decimal(18,2) DEFAULT NULL COMMENT '工资',
  `user_type` varchar(2) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '类型（1：员工 2：教练）',
  `status` varchar(2) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '状态 0：停用 1：启用',
  `is_admin` varchar(2) COLLATE utf8mb4_bin DEFAULT NULL COMMENT ' 是否是管理员 （1：是 0 ：否）',
  `is_account_non_expired` tinyint(4) DEFAULT NULL COMMENT '帐户是否过期(1 未过期，0已过期)',
  `is_account_non_locked` tinyint(4) DEFAULT NULL COMMENT '帐户是否被锁定(1 未锁定，0已锁定)',
  `is_credentials_non_expired` tinyint(4) DEFAULT NULL COMMENT '密码是否过期(1 未过期，0已过期)',
  `is_enabled` tinyint(4) DEFAULT NULL COMMENT '帐户是否可用(1 可用，0 删除用户)',
  `nick_name` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '姓名',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC;

/*Data for the table `sys_user` */

insert  into `sys_user`(`user_id`,`username`,`password`,`phone`,`email`,`sex`,`salary`,`user_type`,`status`,`is_admin`,`is_account_non_expired`,`is_account_non_locked`,`is_credentials_non_expired`,`is_enabled`,`nick_name`,`create_time`,`update_time`) values (1,'zs001','202cb962ac59075b964b07152d234b70','18687116223','2383404558@qq.com','0','5000.00','1','1','0',1,1,1,1,'张三','2022-06-11 23:11:00','2022-06-12 13:29:00'),(2,'ls001','202cb962ac59075b964b07152d234b70','18787871623','2383404558@qq.com','1','6000.00','2','1','0',1,1,1,1,'李四','2022-06-12 11:50:16',NULL),(3,'admin','e10adc3949ba59abbe56e057f20f883e','18687116223','2383404558@qq.com','1',NULL,NULL,'1','1',1,1,1,1,'admin',NULL,NULL),(4,'eric','29988429c481f219b8c5ba8c071440e1','18888888888','eric1234@qq.com','0','2000.00','1','1','0',1,1,1,1,'eric','2024-05-24 23:00:26',NULL);

/*Table structure for table `sys_user_role` */

DROP TABLE IF EXISTS `sys_user_role`;

CREATE TABLE `sys_user_role` (
  `user_role_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int(11) DEFAULT NULL COMMENT '员工id',
  `role_id` int(11) DEFAULT NULL COMMENT '角色id',
  PRIMARY KEY (`user_role_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC;

/*Data for the table `sys_user_role` */

insert  into `sys_user_role`(`user_role_id`,`user_id`,`role_id`) values (2,2,4),(8,1,3);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
