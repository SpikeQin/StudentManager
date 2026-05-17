-- ============================================================
-- MySQL Database Schema for Student Management System
-- Host: localhost    Database: student_manager
-- ============================================================

CREATE DATABASE IF NOT EXISTS `student_manager` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `student_manager`;

-- ============================================================
-- student (学生信息)
-- ============================================================
DROP TABLE IF EXISTS `score`;
DROP TABLE IF EXISTS `student`;
CREATE TABLE `student` (
  `id` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL DEFAULT '',
  `name` varchar(50) NOT NULL,
  `sex` varchar(50) NOT NULL,
  `school_date` varchar(50) NOT NULL,
  `major` varchar(50) NOT NULL,
  `email` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_student_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- teacher (教师信息)
-- ============================================================
DROP TABLE IF EXISTS `teacher`;
CREATE TABLE `teacher` (
  `id` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `name` varchar(50) DEFAULT '',
  `sex` varchar(50) DEFAULT '',
  `email` varchar(50) NOT NULL,
  `role` varchar(20) DEFAULT 'teacher' COMMENT 'teacher 普通教师 / admin 管理员',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- score (成绩信息)
-- ============================================================
CREATE TABLE `score` (
  `id` varchar(50) NOT NULL,
  `data_structure` varchar(50) DEFAULT '',
  `operating_system` varchar(50) DEFAULT '',
  `computer_network` varchar(50) DEFAULT '',
  `computer_organization` varchar(50) DEFAULT '',
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_score_student` FOREIGN KEY (`id`) REFERENCES `student` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- course (课程)
-- ============================================================
DROP TABLE IF EXISTS `course`;
CREATE TABLE `course` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- operation_log (操作日志)
-- ============================================================
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `user_id` VARCHAR(50) NOT NULL,
  `user_type` VARCHAR(20) NOT NULL COMMENT 'teacher / student / admin',
  `action` VARCHAR(100) NOT NULL COMMENT '操作动作',
  `detail` TEXT COMMENT '操作详情',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- announcement (公告)
-- ============================================================
DROP TABLE IF EXISTS `announcement`;
CREATE TABLE `announcement` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `title` VARCHAR(200) NOT NULL,
  `content` TEXT NOT NULL,
  `publisher` VARCHAR(50) NOT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 初始化数据
-- ============================================================
INSERT IGNORE INTO `teacher` (`id`, `password`, `name`, `sex`, `email`, `role`) VALUES
('ztw', 'ztw', '管理员', '男', 'admin@example.com', 'admin'),
('tea001', 'tea001', '教师A', '女', 'teaa@example.com', 'teacher'),
('tea002', 'tea002', '教师B', '男', 'teab@example.com', 'teacher');

INSERT IGNORE INTO `course` (`id`, `name`) VALUES
(1, '数据结构'),
(2, '操作系统'),
(3, '计算机网络'),
(4, '计算机组成原理');

INSERT IGNORE INTO `student` (`id`, `password`, `name`, `sex`, `school_date`, `major`, `email`) VALUES
('202412211214', '202412211214', '学生A', '男', '2024-9', '软件工程', NULL),
('202412211219', '202412211219', '学生B', '男', '2024-9', '软件工程', NULL),
('202412211220', '202412211220', '学生C', '男', '2024-9', '软件工程', NULL),
('202412211221', '202412211221', '学生D', '男', '2024-9', '软件工程', NULL),
('202412211222', '202412211222', '学生E', '男', '2024-9', '软件工程', NULL),
('202412211223', '202412211223', '学生F', '男', '2024-9', '软件工程', NULL),
('202412211225', '202412211225', '学生G', '男', '2024-9', '软件工程', NULL);

INSERT IGNORE INTO `score` (`id`, `data_structure`, `operating_system`, `computer_network`, `computer_organization`) VALUES
('202412211214', '85', '78', '92', '88'),
('202412211219', '72', '89', '85', '76'),
('202412211220', '91', '68', '79', '84'),
('202412211221', '64', '93', '77', '81'),
('202412211222', '88', '72', '95', '79'),
('202412211223', '79', '85', '68', '92'),
('202412211225', '93', '81', '74', '87');

INSERT IGNORE INTO `announcement` (`title`, `content`, `publisher`) VALUES
('欢迎使用教务管理系统', '各位老师和同学，欢迎使用教务管理系统！请首次登录后及时修改密码。', '管理员');
