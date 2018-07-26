/*
 Navicat Premium Data Transfer

 Source Server         : java_test
 Source Server Type    : MySQL
 Source Server Version : 50722
 Source Host           : localhost:3306
 Source Schema         : test_mybatis

 Target Server Type    : MySQL
 Target Server Version : 50722
 File Encoding         : 65001

 Date: 26/07/2018 15:14:27
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for auth_token
-- ----------------------------
DROP TABLE IF EXISTS `auth_token`;
CREATE TABLE `auth_token` (
  `id` varchar(255) NOT NULL,
  `updateAt` bigint(20) NOT NULL,
  `createAt` bigint(20) NOT NULL,
  `userId` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of auth_token
-- ----------------------------

-- ----------------------------
-- Table structure for permission
-- ----------------------------
DROP TABLE IF EXISTS `permission`;
CREATE TABLE `permission` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `label` varchar(255) NOT NULL,
  `createAt` bigint(20) DEFAULT NULL,
  `updateAt` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of permission
-- ----------------------------
BEGIN;
INSERT INTO `permission` VALUES (1, 'user:add', '添加用户', 1, 1);
INSERT INTO `permission` VALUES (2, 'user:delete', '删除用户', 1, 1);
INSERT INTO `permission` VALUES (3, 'user:find', '查询用户', 1, 1);
INSERT INTO `permission` VALUES (4, 'user:list', '用户列表', 1, 1);
COMMIT;

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `label` varchar(255) NOT NULL,
  `createAt` bigint(20) unsigned DEFAULT NULL,
  `updateAt` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of role
-- ----------------------------
BEGIN;
INSERT INTO `role` VALUES (1, 'admin', '管理员', 1, 1);
COMMIT;

-- ----------------------------
-- Table structure for role_permission
-- ----------------------------
DROP TABLE IF EXISTS `role_permission`;
CREATE TABLE `role_permission` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `roleId` int(10) unsigned NOT NULL,
  `permissionId` int(10) unsigned NOT NULL,
  `createAt` bigint(20) unsigned DEFAULT NULL,
  `updateAt` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_role_permission_role` (`roleId`),
  KEY `fk_role_permission_permission` (`permissionId`),
  CONSTRAINT `fk_role_permission_permission` FOREIGN KEY (`permissionId`) REFERENCES `permission` (`id`),
  CONSTRAINT `fk_role_permission_role` FOREIGN KEY (`roleId`) REFERENCES `role` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of role_permission
-- ----------------------------
BEGIN;
INSERT INTO `role_permission` VALUES (1, 1, 1, 1, 1);
INSERT INTO `role_permission` VALUES (2, 1, 2, 1, 1);
INSERT INTO `role_permission` VALUES (3, 1, 3, 1, 1);
COMMIT;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(100) NOT NULL,
  `email` varchar(255) NOT NULL,
  `phoneNumber` varchar(255) DEFAULT NULL,
  `nickname` varchar(100) DEFAULT NULL,
  `age` int(10) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `salt` varchar(255) NOT NULL,
  `createAt` bigint(20) unsigned DEFAULT NULL,
  `updateAt` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
BEGIN;
INSERT INTO `user` VALUES (2, 'xiaoxi', '494273025@qq.com', NULL, NULL, NULL, '5a4dfccbae7e90db47516019079b6a12e3c7bf92', 'f102ab30-df76-44a5-b143-2098aa7ea736', 1532581293000, 1532581293000);
INSERT INTO `user` VALUES (3, 'lrq', 'lrq@qq.com', NULL, NULL, NULL, '0031645aa0b4994ffc95c50102cb7f989d6ac945', '4c47d31b-cff8-433d-9a64-aefb8e208265', 1532588904000, 1532588904000);
INSERT INTO `user` VALUES (5, 'lrq1', 'lrq1@qq.com', NULL, NULL, NULL, '5b4570d6471903e5c6019508cec5a9b04feb5cff', '0a563274-5974-49af-8914-ccd8024a6c55', 1532589179000, 1532589179000);
COMMIT;

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `userId` int(10) unsigned NOT NULL,
  `roleId` int(10) unsigned NOT NULL,
  `createAt` bigint(20) unsigned DEFAULT NULL,
  `updateAt` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_user_role_user` (`userId`),
  KEY `fk_user_role_role` (`roleId`),
  CONSTRAINT `fk_user_role_role` FOREIGN KEY (`roleId`) REFERENCES `role` (`id`),
  CONSTRAINT `fk_user_role_user` FOREIGN KEY (`userId`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_role
-- ----------------------------
BEGIN;
INSERT INTO `user_role` VALUES (1, 2, 1, 1, 1);
COMMIT;

